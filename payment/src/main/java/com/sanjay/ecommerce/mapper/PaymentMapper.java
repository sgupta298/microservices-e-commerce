package com.sanjay.ecommerce.mapper;

import com.sanjay.ecommerce.dto.PaymentRequest;
import com.sanjay.ecommerce.entity.Payment;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {
    public Payment toMapper(@Valid PaymentRequest paymentRequest) {
        return Payment.builder()
                .id(paymentRequest.id())
                .amount(paymentRequest.amount())
                .orderId(paymentRequest.orderId())
                .paymentMethod(paymentRequest.paymentMethod())
                .build();
    }
}
