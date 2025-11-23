package com.vitorlima.udemy;

import com.vitorlima.udemy.domain.Product;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Singleton
public class InMemoryStore {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryStore.class);

    private final Map<Integer, Product> products = new HashMap<>();
    private final Faker faker = new Faker();

    @PostConstruct
    public void initialise() {
        IntStream.range(0, 10).forEach(
                this::addProduct
        );
    }

    private void addProduct(int id) {

        var novoProduto = new Product(id, faker.coffee().blendName(), Product.Type.COFFEE);
        products.put(id, novoProduto);

        LOG.debug("Added Product: {}", novoProduto);
    }

    public Product addProduct(Product product) {

        products.put(product.id(), product);

        return products.get(product.id());
    }

    public Map<Integer, Product> getProducts() {
        return products;
    }

}
