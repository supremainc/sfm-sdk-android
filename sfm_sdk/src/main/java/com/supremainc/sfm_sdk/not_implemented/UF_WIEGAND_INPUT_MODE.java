/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_WIEGAND_INPUT_MODE {
    UF_WIEGAND_INPUT_DISABLE(0x00),
    UF_WIEGAND_INPUT_VERIFY(0x22),
    ;
    private int value;

    UF_WIEGAND_INPUT_MODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
