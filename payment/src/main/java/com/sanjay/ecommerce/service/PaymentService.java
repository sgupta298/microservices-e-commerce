package com.sanjay.ecommerce.service;

import com.sanjay.ecommerce.dto.PaymentNotificationRequest;
import com.sanjay.ecommerce.dto.PaymentRequest;
import com.sanjay.ecommerce.kafka.NotificationProducer;
import com.sanjay.ecommerce.mapper.PaymentMapper;
import com.sanjay.ecommerce.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(@Valid PaymentRequest paymentRequest) {
        var payment =  paymentRepository.save(paymentMapper.toMapper(paymentRequest));
        notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        paymentRequest.orderReference(),
                        paymentRequest.amount(),
                        paymentRequest.paymentMethod(),
                        paymentRequest.customer().firstName(),
                        paymentRequest.customer().lastName(),
                        paymentRequest.customer().email()
                )
        );
        return payment.getId();
    }
}
