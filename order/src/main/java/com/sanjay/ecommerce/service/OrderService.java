package com.sanjay.ecommerce.service;


import com.sanjay.ecommerce.clients.ProductClient;
import com.sanjay.ecommerce.dto.OrderLineRequest;
import com.sanjay.ecommerce.dto.PurchaseRequest;
import com.sanjay.ecommerce.exception.BusinessException;
import com.sanjay.ecommerce.clients.CustomerClient;
import com.sanjay.ecommerce.dto.OrderRequest;
import com.sanjay.ecommerce.mapper.OrderMapper;
import com.sanjay.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest orderRequest) {
        //check the customer --> OpenFeign
        var customer = customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer exists with the provided ID :: "+orderRequest.customerId()));
        //purchase the products --> product microservice (Rest template)
        productClient.purchaseProduct(orderRequest.products());
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
        //start payment process

        //send the order confirmation --> notification microservice (kafka)
    }
}
