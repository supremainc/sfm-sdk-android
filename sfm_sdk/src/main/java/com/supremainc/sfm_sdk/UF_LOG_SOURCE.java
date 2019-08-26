package com.supremainc.sfm_sdk;

public enum UF_LOG_SOURCE {
    UF_LOG_SOURCE_SYSTEM(0x00),
    UF_LOG_SOURCE_HOST_PORT(0x01),
    UF_LOG_SOURCE_AUX_PORT(0x02),
    UF_LOG_SOURCE_WIEGAND_INPUT(0x03),
    UF_LOG_SOURCE_IN0(0x04),
    UF_LOG_SOURCE_IN1(0x05),
    UF_LOG_SOURCE_IN2(0x06),
    UF_LOG_SOURCE_FREESCAN(0x07),
    UF_LOG_SOURCE_SMARTCARD(0x08),
    UF_LOG_SOURCE_OTHER(0x0f),
    ;
    private int value;

    UF_LOG_SOURCE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

}
