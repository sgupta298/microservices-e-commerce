package com.sanjay.ecommerce.service;

import com.sanjay.ecommerce.dto.OrderLineRequest;
import com.sanjay.ecommerce.dto.OrderLineResponse;
import com.sanjay.ecommerce.mapper.OrderLineMapper;
import com.sanjay.ecommerce.repository.OrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper orderLineMapper;

    public Integer saveOrderLine(OrderLineRequest orderLineRequest) {
        var order = orderLineMapper.toOrderLine(orderLineRequest);
        return orderLineRepository.save(order).getId();
    }

    public List<OrderLineResponse> findAllByOrderId(Integer orderId) {
        return orderLineRepository.findAllByOrderId(orderId).stream().map(orderLineMapper::toOrderLineResponse).toList();
    }
}
