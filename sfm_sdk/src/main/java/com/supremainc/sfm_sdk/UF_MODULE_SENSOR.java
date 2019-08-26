package com.supremainc.sfm_sdk;

public enum UF_MODULE_SENSOR {
    UF_SENSOR_FL(0),
    UF_SENSOR_FC(1),
    UF_SENSOR_OP(2),
    UF_SENSOR_OC(3),
    UF_SENSOR_TC(4),
    UF_SENSOR_OC2(5),
    UF_SENSOR_TS(6),
    UF_SENSOR_OL(7),
    UF_SENSOR_OH(8),
    UF_SENSOR_UNKNOWN(-1),
    ;
    private int value;

    UF_MODULE_SENSOR(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
