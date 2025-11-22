package com.vitorlima.udemy.domain;

public record Product(
        Integer id,
        String name,
        Type type
) {

    public enum Type {
        COFFEE, TEA, OTHER
    }
}
