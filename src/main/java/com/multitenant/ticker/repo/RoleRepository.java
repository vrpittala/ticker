package com.multitenant.ticker.repo;

import com.multitenant.ticker.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
    Boolean existsByName(String name);
}
