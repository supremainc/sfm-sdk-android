/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_ENROLL_MODE {
    UF_ENROLL_ONE_TIME(0x30),
    UF_ENROLL_TWO_TIMES1(0x31),
    UF_ENROLL_TWO_TIMES2(0x32),
    UF_ENROLL_TWO_TEMPLATES1(0x41),
    UF_ENROLL_TWO_TEMPLATES2(0x42),
    ;
    private int value;

    UF_ENROLL_MODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_ENROLL_MODE ToEnrollMode(int i) {
        UF_ENROLL_MODE enrollMode = null;

        for (UF_ENROLL_MODE iter : UF_ENROLL_MODE.values()) {
            if (i == iter.getValue()) {
                enrollMode = iter;
            }
        }
        return enrollMode;
    }

}
