package com.deliveryservice.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeliveryFeeResponse {
    private BigDecimal fee;
}
