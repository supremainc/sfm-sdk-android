/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

public enum UF_CHANNEL_TYPE {
    UF_SERIAL_CHANNEL(0),
    UF_SOCKET_CHANNEL(1),
    ;
    private int value;

    UF_CHANNEL_TYPE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
