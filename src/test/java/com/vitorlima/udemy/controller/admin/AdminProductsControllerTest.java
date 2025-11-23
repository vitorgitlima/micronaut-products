package com.vitorlima.udemy.controller.admin;

import com.vitorlima.udemy.InMemoryStore;
import com.vitorlima.udemy.domain.Product;
import com.vitorlima.udemy.domain.UpdatedProductRequest;
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

    @Test
    void aProductCanBeUpdatedUsingTheAdminPutendpoint(){
        var productToUpdate = new Product(999, "old-value", Product.Type.OTHER);

        store.getProducts().put(productToUpdate.id(), productToUpdate);
        assertEquals(productToUpdate, store.getProducts().get(productToUpdate.id()));

        var updatedRequest = new UpdatedProductRequest("new-value", Product.Type.TEA);

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/" + productToUpdate.id(), updatedRequest),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());

        var productFromStore = store.getProducts().get(productToUpdate.id());
        assertEquals(updatedRequest.name(), productFromStore.name());
        assertEquals(updatedRequest.type(), productFromStore.type());
    }

    @Test
    void aNonExistingProductWillBeAddedWhenUsingTheAdminPutEndpoint(){
        var productId = 999;

        store.getProducts().remove(productId);
        assertNull(store.getProducts().get(productId));

        var updatedRequest = new UpdatedProductRequest("new-value", Product.Type.TEA);

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/" + productId, updatedRequest),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        var productFromStore = store.getProducts().get(productId);
        assertEquals(updatedRequest.name(), productFromStore.name());
        assertEquals(updatedRequest.type(), productFromStore.type());

    }




}