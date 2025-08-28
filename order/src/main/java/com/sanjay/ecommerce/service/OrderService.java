package com.sanjay.ecommerce.service;


import com.sanjay.ecommerce.clients.ProductClient;
import com.sanjay.ecommerce.dto.*;
import com.sanjay.ecommerce.exception.BusinessException;
import com.sanjay.ecommerce.clients.CustomerClient;
import com.sanjay.ecommerce.kafka.OrderProducer;
import com.sanjay.ecommerce.mapper.OrderMapper;
import com.sanjay.ecommerce.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(OrderRequest orderRequest) {
        //check the customer --> OpenFeign
        var customer = customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer exists with the provided ID :: "+orderRequest.customerId()));
        //purchase the products --> product microservice (Rest template)
        var purchasedProducts = this.productClient.purchaseProduct(orderRequest.products());
        //persist order
        var order = orderRepository.save(orderMapper.toOrder(orderRequest));
        //persist the order lines
        for(PurchaseRequest purchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // todo start payment process

        //send the order confirmation --> notification microservice (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::fromOrder).toList();
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
