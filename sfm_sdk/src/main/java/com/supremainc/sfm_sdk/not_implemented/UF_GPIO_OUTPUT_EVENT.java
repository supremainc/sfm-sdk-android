package com.supremainc.sfm_sdk.not_implemented;

public enum UF_GPIO_OUTPUT_EVENT {
    UF_GPIO_OUT_ENROLL_WAIT_FINGER(0x81),
    UF_GPIO_OUT_ENROLL_PROCESSING(0x82),
    UF_GPIO_OUT_ENROLL_SUCCESS(0x83),
    UF_GPIO_OUT_ENROLL_FAIL(0x84),
    UF_GPIO_OUT_MATCH_WAIT_FINGER(0x85),
    UF_GPIO_OUT_MATCH_PROCESSING(0x86),
    UF_GPIO_OUT_MATCH_SUCCESS(0x87),
    UF_GPIO_OUT_MATCH_FAIL(0x88),
    UF_GPIO_OUT_DELETE_WAIT(0x89),
    UF_GPIO_OUT_DELETE_PROCESSING(0x8a),
    UF_GPIO_OUT_DELETE_SUCCESS(0x8b),
    UF_GPIO_OUT_DELETE_FAIL(0x8c),
    UF_GPIO_OUT_BEEP(0x8d),
    UF_GPIO_OUT_VOLTAGE_WARNING(0x8e),
    UF_GPIO_OUT_POWER_UP(0x8f),

    UF_GPIO_OUT_UNKNOWN(-1),
    ;
    private int value;

    UF_GPIO_OUTPUT_EVENT(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
