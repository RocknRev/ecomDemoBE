package com.rak.divaksha.ecommerce.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    private Boolean active;

    private Integer sortOrder;

    private Long parentId;

}