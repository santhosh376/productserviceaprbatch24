package com.example.productserviceaprbatch24.controllers;

import com.example.productserviceaprbatch24.models.Product;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/*
    CRUD (create,read,update,delete)
    Get - Getting a product
    Delete - Delete a product
    Post - Create a product
    Put - Replace the product
    Patch - Updating a product

    Get product - modify - put
 */
@RestController
@RequestMapping("/products")

public class ProductController {

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable("id") Long id) {
        return  new Product();
    }

    @GetMapping()
    public List<Product> getAllProducts() {
        return  new ArrayList<Product>();
    }

    @PostMapping()
    public Product createProduct(@RequestBody Product product){
        return new Product();
    }

    //replacing the product
    @PutMapping("/{id}")
    public Product replaceProduct(@PathVariable("id") Long id ,@RequestBody Product product) {
        return new Product();
    }


    @PatchMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id ,@RequestBody Product product) {
        return new Product();
    }

    @GetMapping("/{id}")
    public Product deleteProductById(@PathVariable("id") Long id) {
        return  new Product();
    }

}