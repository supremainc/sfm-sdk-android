/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_WIEGAND_PARITY_TYPE {
    UF_WIEGAND_EVEN_PARITY(0),
    UF_WIEGAND_ODD_PARITY(1),
    ;
    private int value;

    UF_WIEGAND_PARITY_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
