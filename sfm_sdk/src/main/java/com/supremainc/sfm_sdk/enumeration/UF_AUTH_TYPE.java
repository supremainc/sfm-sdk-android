package com.supremainc.sfm_sdk.enumeration;

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

    public static UF_AUTH_TYPE ToAuthType(int i) {
        UF_AUTH_TYPE authType = null;

        for (UF_AUTH_TYPE iter : UF_AUTH_TYPE.values()) {
            if (i == iter.getValue()) {
                authType = iter;
            }
        }
        return authType;
    }
}
