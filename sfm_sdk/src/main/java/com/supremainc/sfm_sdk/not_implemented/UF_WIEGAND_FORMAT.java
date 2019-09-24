/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_WIEGAND_FORMAT {
    UF_WIEGAND_26BIT(0x01),
    UF_WIEGAND_PASS_THRU(0x02),
    UF_WIEGAND_CUSTOM(0x03),
    ;
    private int value;

    UF_WIEGAND_FORMAT(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
