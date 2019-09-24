package com.supremainc.sfm_sdk.not_implemented;

public enum UF_GPIO_INPUT_FUNC {
    UF_GPIO_IN_ENROLL(0x01),
    UF_GPIO_IN_IDENTIFY(0x02),
    UF_GPIO_IN_DELETE_ALL(0x03),
    UF_GPIO_IN_DELETE_ALL_BY_CONFIRM(0x04),
    UF_GPIO_IN_CANCEL(0x06),
    UF_GPIO_IN_ENROLL_VERIFICATION(0x07),
    UF_GPIO_IN_DELETE_VERIFICATION(0x08),
    UF_GPIO_IN_DELETE_ALL_VERIFICATION(0x09),
    UF_GPIO_IN_RESET(0x0a),
    UF_GPIO_IN_UNKONWN(-1),
    ;
    private int value;

    UF_GPIO_INPUT_FUNC(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
