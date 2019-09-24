/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_ENROLL_OPTION {
    UF_ENROLL_NONE(0x00),
    UF_ENROLL_ADD_NEW(0x71),
    UF_ENROLL_AUTO_ID(0x79),
    UF_ENROLL_CONTINUE(0x74),
    UF_ENROLL_CHECK_ID(0x70),
    UF_ENROLL_CHECK_FINGER(0x84),
    UF_ENROLL_CHECK_FINGER_AUTO_ID(0x85),
    UF_ENROLL_DURESS(0x92),
    ;
    private int value;

    UF_ENROLL_OPTION(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
