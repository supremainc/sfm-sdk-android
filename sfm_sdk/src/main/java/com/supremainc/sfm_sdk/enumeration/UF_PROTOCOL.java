/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_PROTOCOL {
    UF_SINGLE_PROTOCOL(0),
    UF_NETWORK_PROTOCOL(1),
    ;
    private int value;

    UF_PROTOCOL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_PROTOCOL ToProtocol(int i) {
        UF_PROTOCOL protocol = null;

        for (UF_PROTOCOL iter : UF_PROTOCOL.values()) {
            if (i == iter.getValue()) {
                protocol = iter;
            }
        }
        return protocol;
    }
}
