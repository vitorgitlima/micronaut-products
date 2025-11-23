package com.vitorlima.udemy.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UpdatedProductRequest(

        String name,
        Product.Type type) {
}
