package com.vitorlima.udemy.controller.admin;

import com.vitorlima.udemy.InMemoryStore;
import com.vitorlima.udemy.domain.Product;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/admin/products")
public class AdminProductsController {

    private final InMemoryStore store;

    public AdminProductsController(InMemoryStore store) {this.store = store;}

    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Product addNewProduct(@Body Product product) {

        return store.addProduct(product);
    }
}
