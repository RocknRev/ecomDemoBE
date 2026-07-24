package com.rak.divaksha.ecommerce.order.dto;

import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus orderStatus;

}
