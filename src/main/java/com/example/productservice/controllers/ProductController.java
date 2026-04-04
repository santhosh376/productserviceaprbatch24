package com.example.productservice.controllers;

import com.example.productservice.dtos.products.*;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.models.Product;
import com.example.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private final ProductService productService;
    private final RestTemplate restTemplate;

    public ProductController(@Qualifier("dbProductService") ProductService productService,RestTemplate restTemplate) {
        this.productService = productService;
        this.restTemplate = restTemplate;
    }

    //get the single product  by id
    @GetMapping("/{id}")
    public ResponseEntity<GetProductResponseDTO> getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {

        System.out.println("Yes API is hitting");

        try {
            if(id < 0 ){
                throw new ProductNotFoundException();
            }else if(id == 0){
                throw new RuntimeException("Something went wrong");
            }
//          id++;
            Product product = productService.getProductById(id);
            GetProductResponseDTO response =  new GetProductResponseDTO();
            GetProductDto productDto = GetProductDto.from(product);
            response.setProduct(productDto);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException(e);
        }
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
    public CreateProductResponseDto createProduct(@RequestHeader("Authorization")String token, @RequestBody CreateProductRequestDto createProductRequestDto) {
        boolean isAuthenticated = restTemplate.getForObject("http://userService/auth/validate?token="+token,Boolean.class);

        System.out.println("isAuthenticated = " + isAuthenticated);

        if(!isAuthenticated){
            throw new RuntimeException("STOP HERE");
        }

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
    public PatchProductResponseDto updateProduct(@PathVariable("id") Long productId,@RequestBody CreateProductDto productDto) throws ProductNotFoundException {
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