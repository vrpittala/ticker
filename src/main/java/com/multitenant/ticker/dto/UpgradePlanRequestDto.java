package com.multitenant.ticker.dto;

import com.multitenant.ticker.enums.PlanType;
import lombok.Data;

@Data
public class UpgradePlanRequestDto {
    private PlanType planType;
    public UpgradePlanRequestDto(PlanType planType) {
        this.planType = planType;
    }
}
