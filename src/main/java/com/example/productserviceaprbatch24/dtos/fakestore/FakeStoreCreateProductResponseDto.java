package com.example.productserviceaprbatch24.dtos.fakestore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreCreateProductResponseDto {
    private Long id;
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;
}
