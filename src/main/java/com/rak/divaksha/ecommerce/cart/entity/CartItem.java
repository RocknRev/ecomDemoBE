package com.rak.divaksha.ecommerce.cart.entity;

import com.rak.divaksha.ecommerce.common.entity.BaseEntity;
import com.rak.divaksha.ecommerce.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id", "flavor"}))
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String flavor = "";

}
