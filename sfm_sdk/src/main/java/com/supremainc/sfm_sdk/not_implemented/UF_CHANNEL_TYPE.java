package com.supremainc.sfm_sdk.not_implemented;

public enum UF_CHANNEL_TYPE {
    UF_SERIAL_CHANNEL(0),
    UF_SOCKET_CHANNEL(1),
    ;
    private int value;

    UF_CHANNEL_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
