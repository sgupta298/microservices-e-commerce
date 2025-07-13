package com.sanjay.ecommerce.dto;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
