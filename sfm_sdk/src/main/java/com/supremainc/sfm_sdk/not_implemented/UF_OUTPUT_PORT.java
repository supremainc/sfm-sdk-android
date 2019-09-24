/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_OUTPUT_PORT {
    UF_OUTPUT_PORT0(0x00),
    UF_OUTPUT_PORT1(0x01),
    UF_OUTPUT_PORT2(0x02),
    UF_OUTPUT_LED0(0x03),
    UF_OUTPUT_LED1(0x04),
    UF_OUTPUT_LED2(0x05),
    ;
    private int value;

    UF_OUTPUT_PORT(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
