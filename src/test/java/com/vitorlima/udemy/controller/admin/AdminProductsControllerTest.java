package com.vitorlima.udemy.controller.admin;

import com.vitorlima.udemy.InMemoryStore;
import com.vitorlima.udemy.domain.Product;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class AdminProductsControllerTest {

    @Inject
    @Client("/admin/products")
    HttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void aNewProductCanBeAddedUsingTheAdminPostEndpoint() {

        var productToAdd = new Product(123, "my-test-product", Product.Type.OTHER);

        store.getProducts().remove(productToAdd.id());
        assertNull(store.getProducts().get(productToAdd.id()));

        var response = client.toBlocking().exchange(HttpRequest.POST("/", productToAdd),
                Product.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(productToAdd.id(), response.getBody().get().id());
        assertEquals(productToAdd.name(), response.getBody().get().name());
        assertEquals(productToAdd.type(), response.getBody().get().type());
    }

    @Test
    void addinNewProductTwiceResultsInConflit() {

        var productToAdd = new Product(123, "my-test-product", Product.Type.OTHER);

        store.getProducts().remove(productToAdd.id());
        assertNull(store.getProducts().get(productToAdd.id()));

        var response = client.toBlocking().exchange(HttpRequest.POST("/", productToAdd),
                Product.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());

        var expectedConflit = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/", productToAdd)));

        assertEquals(HttpStatus.CONFLICT, expectedConflit.getStatus());
    }


}