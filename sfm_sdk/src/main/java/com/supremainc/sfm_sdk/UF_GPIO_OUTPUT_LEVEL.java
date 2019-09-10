package com.supremainc.sfm_sdk;

public enum UF_GPIO_OUTPUT_LEVEL {
    UF_GPIO_OUT_ACTIVE_HIGH(0x82),
    UF_GPIO_OUT_ACTIVE_LOW(0x84),
    UF_GPIO_OUT_HIGH_BLINK(0x83),
    UF_GPIO_OUT_LOW_BLINK(0x85),
    ;
    private int value;

    UF_GPIO_OUTPUT_LEVEL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
