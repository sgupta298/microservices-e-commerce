package com.sanjay.ecommerce.dto;

import com.sanjay.ecommerce.customer.Address;

public record CustomerResponse (
        String id,
        String firstName,
        String lastName,
        String email,
        Address address
){
}
