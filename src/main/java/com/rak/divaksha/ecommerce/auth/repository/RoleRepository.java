package com.rak.divaksha.ecommerce.auth.repository;

import com.rak.divaksha.ecommerce.auth.entity.Role;
import com.rak.divaksha.ecommerce.common.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

}