package com.multitenant.ticker.entity;

import com.multitenant.ticker.enums.PlanType;
import com.multitenant.ticker.enums.TenantStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name= "tenants")
public class Tenant {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tenant_key", unique = true, nullable = false)
    private String tenantKey;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    @Enumerated(EnumType.STRING)
    private PlanType plan;

    private Instant created;
    private Instant latUpdated;
}
