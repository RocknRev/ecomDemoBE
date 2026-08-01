package com.rak.divaksha.ecommerce.wishlist.entity;

import com.rak.divaksha.ecommerce.common.entity.BaseEntity;
import com.rak.divaksha.ecommerce.product.entity.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_wishlist_items", uniqueConstraints = @UniqueConstraint(columnNames = {"wishlist_id", "product_id"}))
public class WishlistItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
