package com.multitenant.ticker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name = "roles")
public class Role {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    public Role() {
    }
    public Role(String name) {
        this.name = name;
    }
}
