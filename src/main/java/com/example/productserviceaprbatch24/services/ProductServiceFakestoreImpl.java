package com.example.productserviceaprbatch24.services;

import com.example.productserviceaprbatch24.dtos.FakeStoreCreateProductDto;
import com.example.productserviceaprbatch24.dtos.FakeStoreProductResponseDto;
import com.example.productserviceaprbatch24.models.Category;
import com.example.productserviceaprbatch24.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Service("fakeStoreProductService")
@Primary
public class ProductServiceFakestoreImpl implements ProductService{

    private RestTemplate restTemplate;

    private ProductServiceFakestoreImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product createProduct(Product product) {
        FakeStoreCreateProductDto request = new FakeStoreCreateProductDto();
        request.setCategory(product.getCategoryName());
        request.setTitle(product.getTitle());
        request.setDescription(product.getDescription());
        request.setImage(product.getImageUrl());
        request.setPrice(product.getPrice());
        FakeStoreProductResponseDto response = restTemplate.postForObject(
                "https://fakestoreapi.com/products",
                 request,
                 FakeStoreProductResponseDto.class
        );
        Product product1 = new Product();
        product1.setId(response.getId());
        product1.setTitle(response.getTitle());
        product1.setDescription(response.getDescription());
        product1.setPrice(response.getPrice());
        product1.setImageUrl(response.getImage());
        product1.setCategoryName(response.getCategory());

        return product1;
    }
}
