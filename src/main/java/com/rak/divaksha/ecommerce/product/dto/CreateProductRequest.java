package com.rak.divaksha.ecommerce.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CreateProductRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    private String shortDescription;

    private String description;

    @NotBlank
    private String sku;

    private String brand;

    private String netWeight;

    private String nutritionInfo;

    private String allergenInfo;

    private String storageInstructions;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotNull
    private Integer stock;

    private String thumbnailUrl;

    private Boolean active = true;

    private Boolean featured = false;

    private List<String> flavors;

    private Map<String, String> flavorImages;

    private List<String> highlights;

    private List<String> ingredients;

    private List<String> images;

}
