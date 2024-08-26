package com.example.productserviceaprbatch24.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
@Getter
@Setter
public class Product {
    private long id;
    private String title;
    private String description;
    private double price;
    private String imageUrl;
    private String categoryName;
}