package com.rak.divaksha.ecommerce.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductResponse {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String slug;

    private String shortDescription;

    private String description;

    private String sku;

    private String brand;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer stock;

    private String thumbnailUrl;

    private Boolean active;

    private Boolean featured;

    private List<String> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}