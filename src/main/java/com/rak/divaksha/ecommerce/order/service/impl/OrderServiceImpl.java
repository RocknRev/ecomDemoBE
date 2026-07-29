package com.rak.divaksha.ecommerce.order.service.impl;

import com.rak.divaksha.ecommerce.address.entity.Address;
import com.rak.divaksha.ecommerce.address.repository.AddressRepository;
import com.rak.divaksha.ecommerce.auth.entity.User;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.cart.entity.Cart;
import com.rak.divaksha.ecommerce.cart.entity.CartItem;
import com.rak.divaksha.ecommerce.cart.repository.CartRepository;
import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import com.rak.divaksha.ecommerce.common.enums.PaymentMethod;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.order.dto.PlaceOrderRequest;
import com.rak.divaksha.ecommerce.order.dto.OrderResponse;
import com.rak.divaksha.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.rak.divaksha.ecommerce.order.entity.Order;
import com.rak.divaksha.ecommerce.order.entity.OrderItem;
import com.rak.divaksha.ecommerce.order.repository.OrderRepository;
import com.rak.divaksha.ecommerce.order.service.OrderService;
import com.rak.divaksha.ecommerce.payment.entity.Payment;
import com.rak.divaksha.ecommerce.payment.repository.PaymentRepository;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import com.rak.divaksha.ecommerce.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;

    private User currentUser() {

        CustomUserDetails principal =
                (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        User user = currentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Address address = addressRepository
                .findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address not found"));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setNotes(request.getNotes());
        order.setFullName(address.getFullName());
        order.setPhone(address.getPhone());
        order.setEmail(address.getEmail());
        order.setAddressLine1(address.getAddressLine1());
        order.setAddressLine2(address.getAddressLine2());
        order.setCity(address.getCity());
        order.setState(address.getState());
        order.setPostalCode(address.getPostalCode());
        order.setCountry(address.getCountry());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = productRepository.findById(
                            cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found"));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new BadRequestException(product.getName() + " is inactive");
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for " + product.getName());
            }

            BigDecimal price =
                    product.getDiscountPrice() != null
                            ? product.getDiscountPrice()
                            : product.getPrice();

            BigDecimal lineTotal =
                    price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setSku(product.getSku());
            orderItem.setFlavor(cartItem.getFlavor());
            orderItem.setPrice(price);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(lineTotal);

            order.getItems().add(orderItem);

            subtotal = subtotal.add(lineTotal);

//            product.setStock(product.getStock() - cartItem.getQuantity());
//
//            productRepository.save(product);
        }

        order.setSubtotal(subtotal);
        order.setShippingCharge(BigDecimal.ZERO);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal);
        orderRepository.save(order);

        Payment payment = getPayment(order);
        paymentRepository.save(payment);

        cart.getItems().clear();

        cartRepository.save(cart);

        return map(order);
    }

    private static @NonNull Payment getPayment(Order order) {
        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("INR");
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setGateway(
                switch (order.getPaymentMethod()) {
                    case COD -> PaymentGateway.COD;
                    case UPI, NET_BANKING, CARD -> PaymentGateway.RAZORPAY;
                });
        return payment;
    }

    private String generateOrderNumber() {

        return "DVK"
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        User user = currentUser();

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return map(order);

    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<OrderResponse> getMyOrders() {

        User user = currentUser();

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public void cancelOrder(Long orderId) {

        User user = currentUser();

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.SHIPPED
                || order.getOrderStatus() == OrderStatus.DELIVERED) {

            throw new BadRequestException(
                    "Order cannot be cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Order already cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() + item.getQuantity());

//            productRepository.save(product);
        }

        orderRepository.save(order);

    }

    private OrderResponse map(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .subtotal(order.getSubtotal())
                .shippingCharge(order.getShippingCharge())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .email(order.getEmail())
                .addressLine1(order.getAddressLine1())
                .addressLine2(order.getAddressLine2())
                .city(order.getCity())
                .state(order.getState())
                .postalCode(order.getPostalCode())
                .country(order.getCountry())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .items(
                        order.getItems()
                                .stream()
                                .map(item ->
                                        com.rak.divaksha.ecommerce.order.dto.OrderItemResponse.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProductName())
                                                .sku(item.getSku())
                                                .flavor(item.getFlavor())
                                                .price(item.getPrice())
                                                .quantity(item.getQuantity())
                                                .subtotal(item.getSubtotal())
                                                .build())
                                .toList()
                )
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {

        return orderRepository.findByOrderStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        OrderStatus current = order.getOrderStatus();
        OrderStatus next = request.getOrderStatus();

        validateStatusTransition(current, next);

        order.setOrderStatus(next);

        /*
         * COD orders become paid only after delivery.
         */
        if (order.getPaymentMethod() == PaymentMethod.COD
                && next == OrderStatus.DELIVERED) {

            order.setPaymentStatus(PaymentStatus.PAID);

            paymentRepository.findByOrderId(order.getId())
                    .ifPresent(payment -> {

                        payment.setPaymentStatus(PaymentStatus.PAID);
                        payment.setTransactionTime(LocalDateTime.now());

                        paymentRepository.save(payment);

                    });

        }

        orderRepository.save(order);

        return map(order);

    }

    private void validateStatusTransition(
            OrderStatus current,
            OrderStatus next) {

        if (current == next) {
            return;
        }

        switch (current) {

            case PENDING -> {
                if (next != OrderStatus.CONFIRMED
                        && next != OrderStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Invalid status transition");
                }
            }

            case CONFIRMED -> {
                if (next != OrderStatus.PROCESSING
                        && next != OrderStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Invalid status transition");
                }
            }

            case PROCESSING -> {
                if (next != OrderStatus.SHIPPED) {
                    throw new BadRequestException(
                            "Invalid status transition");
                }
            }

            case SHIPPED -> {
                if (next != OrderStatus.DELIVERED) {
                    throw new BadRequestException(
                            "Invalid status transition");
                }
            }

            case DELIVERED, CANCELLED ->
                    throw new BadRequestException(
                            "Order cannot be updated");

        }

    }

}
