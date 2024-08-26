package com.example.productserviceaprbatch24.dtos;

import com.example.productserviceaprbatch24.models.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductResponseDto {
    private long id;
    private String title;
    private String description;
    private double price;
    private String imageUrl;

    public static CreateProductResponseDto fromProduct(Product product) {
        CreateProductResponseDto createProductResponseDto = new CreateProductResponseDto();
        createProductResponseDto.setId(product.getId());
        createProductResponseDto.setTitle(product.getTitle());
        createProductResponseDto.setDescription(product.getDescription());
        createProductResponseDto.setPrice(product.getPrice());
        createProductResponseDto.setImageUrl(product.getImageUrl());

        return createProductResponseDto;
    }
}
