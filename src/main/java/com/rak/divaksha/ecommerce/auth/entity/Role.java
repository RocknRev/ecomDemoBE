package com.rak.divaksha.ecommerce.auth.entity;

import com.rak.divaksha.ecommerce.common.entity.BaseEntity;
import com.rak.divaksha.ecommerce.common.enums.RoleName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private RoleName name;

}