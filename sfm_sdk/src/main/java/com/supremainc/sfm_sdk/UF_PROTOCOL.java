package com.supremainc.sfm_sdk;

public enum UF_PROTOCOL {
    UF_SINGLE_PROTOCOL(0),
    UF_NETWORK_PROTOCOL(1),
    ;
    private int value;

    UF_PROTOCOL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
