package com.example.productserviceaprbatch24.services;

import com.example.productserviceaprbatch24.models.Product;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dbProductService")
public class ProductServiceDBImpl implements ProductService {

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }

    @Override
    public Product partialUpdateProduct(Long productId,Product product){
        return null;
    }
}