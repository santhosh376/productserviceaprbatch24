package com.example.productserviceaprbatch24.controllers;

import com.example.productserviceaprbatch24.dtos.CreateProductRequestDto;
import com.example.productserviceaprbatch24.dtos.CreateProductResponseDto;
import com.example.productserviceaprbatch24.models.Product;
import com.example.productserviceaprbatch24.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    /*
    @Value("{productService}")
    public String productServiceType;*/

    //Dependency Injection
    private ProductService productService;

    public ProductController(@Qualifier("fakeStoreProductService") ProductService productService) {
        this.productService = productService;;
    }


    //get the single product  by id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long id,@RequestBody Product product) {
        return new Product();
    }

    //get all the products
    @GetMapping()
    public List<Product> getAllProducts() {
        return new ArrayList<Product>();
    }

    //add the product
    @PostMapping()  /*Naman Bhalla*/
    public CreateProductResponseDto createProduct(@RequestBody CreateProductRequestDto createProductRequestDto) {
        Product product = productService.createProduct(
                createProductRequestDto.toProduct()
        );
       /* return createProductRequestDto.getPrice();*/
        return CreateProductResponseDto.fromProduct(
                product
        );
    }

    //replacing the product by id
    @PutMapping("/{id}")
    public Product replaceProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        return new Product();
    }

    //update the product by id
    @PatchMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        return new Product();
    }

    //delete product by id
    @DeleteMapping("/{id}")
    public Product deleteProductById(@PathVariable("id") Long id) {
        return new Product();
    }
}