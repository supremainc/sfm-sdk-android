/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_WIEGAND_OUTPUT_MODE {
    UF_WIEGAND_OUTPUT_DISABLE(0),
    UF_WIEGAND_OUTPUT_WIEGAND_ONLY(1),
    UF_WIEGAND_OUTPUT_ALL(2),
    UF_WIEGAND_OUTPUT_ABA_TRACK_II(3),
    ;
    private int value;

    UF_WIEGAND_OUTPUT_MODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
