package com.sanjay.ecommerce.repository;

import com.sanjay.ecommerce.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine,Integer> {
}
