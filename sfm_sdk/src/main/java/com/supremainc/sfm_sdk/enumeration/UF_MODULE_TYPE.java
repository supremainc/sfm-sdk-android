/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_MODULE_TYPE {
    UF_MODULE_3000(0),
    UF_MODULE_3500(1),
    UF_BIOENTRY_SMART(2),
    UF_BIOENTRY_PASS(3),
    UF_SFOD_3100(4),
    UF_3000FC(5),
    UF_MODULE_4000(6),
    UF_MODULE_5000(7),
    UF_MODULE_5500(8),
    UF_MODULE_6000(9),
    UF_MODULE_SLIM(10),
    UF_MODULE_UNKNOWN(-1),
    ;
    private int value;

    UF_MODULE_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    private void setValue(int i) {
        this.value = i;
    }

    public static UF_MODULE_TYPE ToObjectType(int i) {
        UF_MODULE_TYPE result = null;

        for (UF_MODULE_TYPE iter : UF_MODULE_TYPE.values()) {
            if (i == iter.getValue()) {
                result = iter;
            }
        }
        return result;
    }


}
