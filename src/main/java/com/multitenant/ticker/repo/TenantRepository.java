package com.multitenant.ticker.repo;

import com.multitenant.ticker.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    Optional<Tenant> findByTenantKey(String tenantKey);
    Boolean existsByTenantKey(String tenantKey);
}
