/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_INPUT_PORT {
    UF_INPUT_PORT0(0x00),
    UF_INPUT_PORT1(0x01),
    UF_INPUT_PORT2(0x02),
    ;
    private int value;

    UF_INPUT_PORT(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

}
