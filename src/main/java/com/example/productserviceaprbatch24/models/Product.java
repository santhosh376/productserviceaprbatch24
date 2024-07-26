package com.example.productserviceaprbatch24.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
@Getter
@Setter
public class Product {
    private long id;
    private String title;
    private double price;
    private Category category;
    private String description;
}