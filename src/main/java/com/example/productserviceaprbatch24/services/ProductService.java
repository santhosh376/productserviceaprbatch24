package com.example.productserviceaprbatch24.services;

import com.example.productserviceaprbatch24.models.Product;

import java.util.List;

public interface ProductService {
    Product getAllProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, Product product);
    Product replaceProduct(Long id, Product product);
    Product createProduct(Product product);
    Product deleteProduct(Long id);


}
