package com.example.productserviceaprbatch24.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreCreateProductDto {
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;
}
