package com.example.productserviceaprbatch24.services;

import com.example.productserviceaprbatch24.models.Product;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class FakeProductService implements ProductService{

    private RestTemplate restTemplate;

    FakeProductService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getAllProductById(Long id) {
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        return null;
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public Product deleteProduct(Long id) {
        return null;
    }
}
