package com.supremainc.sfm_sdk;

public enum UF_GPIO_MODE {
    UF_GPIO_DISABLE(0),
    UF_GPIO_INPUT(1),
    UF_GPIO_OUTPUT(2),
    UF_GPIO_SHARED_IO(3),
    UF_GPIO_WIEGAND_INPUT(4),
    UF_GPIO_WIEGAND_OUTPUT(5),
    UF_GPIO_BUZZER(6),
    UF_GPIO_NETWORK_MODE(6),
    UF_GPIO_MODE_UNKNOWN(-1),
    ;

    private int value;

    UF_GPIO_MODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
