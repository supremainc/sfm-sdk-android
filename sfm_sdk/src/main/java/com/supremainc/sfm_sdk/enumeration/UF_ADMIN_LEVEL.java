/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_ADMIN_LEVEL {
    UF_ADMIN_LEVEL_NONE(0),
    UF_ADMIN_LEVEL_ENROLL(1),
    UF_ADMIN_LEVEL_DELETE(2),
    UF_ADMIN_LEVEL_ALL(3);
    private int value;

    UF_ADMIN_LEVEL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_ADMIN_LEVEL ToAdminLevel(int i) {
        UF_ADMIN_LEVEL adminLevel = null;

        for (UF_ADMIN_LEVEL iter : UF_ADMIN_LEVEL.values()) {
            if (i == iter.getValue()) {
                adminLevel = iter;
            }
        }
        return adminLevel;
    }
}
