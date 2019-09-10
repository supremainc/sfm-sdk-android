package com.supremainc.sfm_sdk;

public enum UF_AUTH_TYPE {
    UF_AUTH_FINGERPRINT(0x00),
    UF_AUTH_BYPASS(0x01),
    UF_AUTH_REJECT(0x03),
    ;
    private int value;

    UF_AUTH_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
