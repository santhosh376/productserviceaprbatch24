package com.example.productserviceaprbatch24.services;

import com.example.productserviceaprbatch24.dtos.fakestore.FakeStoreCreateProductRequestDto;
import com.example.productserviceaprbatch24.dtos.fakestore.FakeStoreCreateProductResponseDto;
import com.example.productserviceaprbatch24.dtos.fakestore.FakeStoreGetProductResponseDto;
import com.example.productserviceaprbatch24.models.Product;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service("fakeStoreProductService")
@Primary
public class ProductServiceFakestoreImpl implements ProductService{

    private RestTemplate restTemplate;

    private ProductServiceFakestoreImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product createProduct(Product product) {
        FakeStoreCreateProductRequestDto request = new FakeStoreCreateProductRequestDto();
        request.setCategory(product.getCategoryName());
        request.setTitle(product.getTitle());
        request.setDescription(product.getDescription());
        request.setImage(product.getImageUrl());
        request.setPrice(product.getPrice());
        FakeStoreCreateProductResponseDto response = restTemplate.postForObject(
                "https://fakestoreapi.com/products",
                 request,
                 FakeStoreCreateProductResponseDto.class
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

    @Override
    public List<Product> getAllProducts() {
        FakeStoreGetProductResponseDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                    FakeStoreGetProductResponseDto[].class
        );

        List<FakeStoreGetProductResponseDto> responseDtoList = Stream.of(response).toList();

        List<Product> products = new ArrayList<>();
        for(FakeStoreGetProductResponseDto fakeStoreGetProductResponseDto : responseDtoList){
            products.add(fakeStoreGetProductResponseDto.toProduct());
        }
        return products;
    }

    @Override
    public Product partialUpdateProduct(Long productId,Product product){
           FakeStoreGetProductResponseDto fakeStoreGetProductResponseDto = restTemplate.patchForObject(
                "https://fakestoreapi.com/products" + productId,
                FakeStoreCreateProductRequestDto.fromProduct(product),
                FakeStoreGetProductResponseDto.class
           );
           return fakeStoreGetProductResponseDto.toProduct();
    }
}
