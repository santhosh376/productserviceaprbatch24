package com.example.productserviceaprbatch24.controllers;

import com.example.productserviceaprbatch24.dtos.products.*;
import com.example.productserviceaprbatch24.models.Product;
import com.example.productserviceaprbatch24.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public GetAllProductsResponseDto getAllProducts() {
        List<Product> products = productService.getAllProducts();
        GetAllProductsResponseDto response = new GetAllProductsResponseDto();

        List<GetProductDto> getProductResponseDtos = new ArrayList<>();
        for (Product product : products) {
            getProductResponseDtos.add(GetProductDto.from(product));
        }

        response.setProducts(getProductResponseDtos);

        return response;
    }


         //code for Dto GetProductResponseDto
//        List<Product> products = productService.getAllProducts();
//        List<GetProductResponseDTO> getProductResponseDTOs = new ArrayList<>();
//        for(Product product: products){
//            getProductResponseDTOs.add(GetProductResponseDTO.from(product));
//        }
//        return getProductResponseDTOs;

    //add the product
    @PostMapping()
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

    //update the product by id (partial update)
    @PatchMapping("/{id}")
    public PatchProductResponseDto updateProduct(@PathVariable("id") Long productId,@RequestBody CreateProductDto productDto) {
       Product product = productService.partialUpdateProduct(
               productId,
               productDto.toProduct()
       );

       PatchProductResponseDto response = new PatchProductResponseDto();
       response.setProduct(GetProductDto.from(product));
       return response;
    }

    //delete product by id
    @DeleteMapping("/{id}")
    public Product deleteProductById(@PathVariable("id") Long id) {
        return new Product();
    }
}