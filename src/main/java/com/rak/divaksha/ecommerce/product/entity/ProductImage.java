package com.rak.divaksha.ecommerce.product.entity;

import com.rak.divaksha.ecommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;

}