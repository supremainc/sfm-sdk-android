package com.supremainc.sfm_sdk.not_implemented;

public enum UF_GPIO_INPUT_ACTIVATION
{
    UF_GPIO_IN_ACTIVE_HIGH(0x01),
    UF_GPIO_IN_ACTIVE_LOW(0x02),
    UF_GPIO_IN_RISING_EDGE(0x03),
    UF_GPIO_IN_FALLING_EDGE(0x04),
    ;
    private int value;
    UF_GPIO_INPUT_ACTIVATION(int i)
    {
        this.value = i;
    }
    public int getValue()
    {
        return this.value;
    }
}
