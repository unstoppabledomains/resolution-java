package com.unstoppabledomains.resolution.naming.service.uns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UNSLocation {
    Layer1("Layer 1", "ETH"),
    Layer2("Layer 2", "MATIC");

    private final String name;
    private final String blockchain;
}
