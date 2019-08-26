package com.supremainc.sfm_sdk;

public enum UF_ADMIN_LEVEL {
    UF_ADMIN_LEVEL_NONE(0),
    UF_ADMIN_LEVEL_ENROLL(1),
    UF_ADMIN_LEVEL_DELETE(2),
    UF_ADMIN_LEVEL_ALL(3);
    private int value;

    UF_ADMIN_LEVEL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
