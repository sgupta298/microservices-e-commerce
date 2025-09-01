package com.sanjay.ecommerce.dto;

public record Customer(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
