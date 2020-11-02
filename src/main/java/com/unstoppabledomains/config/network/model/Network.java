package com.unstoppabledomains.config.network.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Network {
    MAINNET(1, "mainnet"),
    ROPSTEN(3, "ropsten"),
    RINKEBY(4, "rinkeby"),
    GOERLI(5, "goerli"),
    KOVAN(42, "kovan");

    private final int code;
    private final String name;
}
