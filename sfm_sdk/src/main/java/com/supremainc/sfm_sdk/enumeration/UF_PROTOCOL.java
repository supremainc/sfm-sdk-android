package com.supremainc.sfm_sdk.enumeration;

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

    public static UF_PROTOCOL ToProtocol(int i) {
        UF_PROTOCOL protocol = null;

        for (UF_PROTOCOL iter : UF_PROTOCOL.values()) {
            if (i == iter.getValue()) {
                protocol = iter;
            }
        }
        return protocol;
    }
}
