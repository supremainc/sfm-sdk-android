package com.supremainc.sfm_sdk;

public enum UF_INPUT_PORT {
    UF_INPUT_PORT0(0x00),
    UF_INPUT_PORT1(0x01),
    UF_INPUT_PORT2(0x02),
    ;
    private int value;

    UF_INPUT_PORT(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

}
