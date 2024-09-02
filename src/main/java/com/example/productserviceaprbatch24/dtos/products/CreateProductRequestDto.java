package com.example.productserviceaprbatch24.dtos.products;

import com.example.productserviceaprbatch24.models.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequestDto {
       private String title;
       private String description;
       private double price;
       private String imageUrl;
       private String categoryName;

       public Product toProduct(){
              Product product= new Product();
              product.setTitle(this.title);
              product.setDescription(this.description);
              product.setPrice(this.price);
              product.setImageUrl(this.imageUrl);
              product.setCategoryName(this.categoryName);

              return product;
       }
}
