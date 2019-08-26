package com.supremainc.sfm_sdk;

public enum UF_WIEGAND_INPUT_MODE {
    UF_WIEGAND_INPUT_DISABLE(0x00),
    UF_WIEGAND_INPUT_VERIFY(0x22),
    ;
    private int value;

    UF_WIEGAND_INPUT_MODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
