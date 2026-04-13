package com.prf.prixstrategie.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AnsoffQuadrant {
    penetration("penetration"),
    market_dev("market-dev"),
    product_dev("product-dev"),
    diversification("diversification");

    private final String apiValue;

    AnsoffQuadrant(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static AnsoffQuadrant fromApiValue(String value) {
        if (value == null) throw new IllegalArgumentException("Quadrant value must not be null");
        for (AnsoffQuadrant q : values()) {
            if (q.apiValue.equals(value)) return q;
        }
        throw new IllegalArgumentException("Unknown Ansoff quadrant: " + value);
    }
}
