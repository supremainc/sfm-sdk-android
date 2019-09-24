/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_CONFIG_TYPE {
    UF_CONFIG_PARAMETERS(0x01),
    UF_CONFIG_GPIO(0x02),
    UF_CONFIG_IO(0x04),
    UF_CONFIG_WIEGAND(0x08),
    UF_CONFIG_USER_MEMORY(0x10),
    ;
    private int value;

    UF_CONFIG_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
