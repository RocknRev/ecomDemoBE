package com.rak.divaksha.ecommerce.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateProductRequest {

    private Long categoryId;

    private String name;

    private String shortDescription;

    private String description;

    private String sku;

    private String brand;

    private String netWeight;

    private String nutritionInfo;

    private String allergenInfo;

    private String storageInstructions;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer stock;

    private String thumbnailUrl;

    private Boolean active;

    private Boolean featured;

    private List<String> flavors;

    private List<String> highlights;

    private List<String> ingredients;

    private List<String> images;

}
