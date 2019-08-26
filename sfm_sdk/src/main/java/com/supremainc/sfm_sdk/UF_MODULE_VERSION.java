package com.supremainc.sfm_sdk;

public enum UF_MODULE_VERSION {
    UF_VERSION_1_0(0),
    UF_VERSION_1_1(1),
    UF_VERSION_1_2(2),
    UF_VERSION_1_3(3),
    UF_VERSION_1_4(4),
    UF_VERSION_1_5(5),
    UF_VERSION_1_6(6),
    UF_VERSION_1_7(7),
    UF_VERSION_1_8(8),
    UF_VERSION_1_9(9),
    UF_VERSION_2_0(20),
    UF_VERSION_2_1(21),
    UF_VERSION_3_0(30),
    UF_VERSION_UNKNOWN(-1),
    ;
    private int value;

    UF_MODULE_VERSION(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
