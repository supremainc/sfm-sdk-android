/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.not_implemented;

//#region UF_3500IO_H
public enum UF_INPUT_FUNC {
    UF_INPUT_NO_ACTION(0x00),
    UF_INPUT_ENROLL(0x10),
    UF_INPUT_IDENTIFY(0x30),
    UF_INPUT_DELETE(0x40),
    UF_INPUT_DELETE_ALL(0x49),
    UF_INPUT_ENROLL_BY_WIEGAND(0x11),
    UF_INPUT_VERIFY_BY_WIEGAND(0x21),
    UF_INPUT_DELETE_BY_WIEGAND(0x41),
    UF_INPUT_ENROLL_VERIFICATION(0x1a),
    UF_INPUT_ENROLL_BY_WIEGAND_VERIFICATION(0x1b),
    UF_INPUT_DELETE_VERIFICATION(0x4a),
    UF_INPUT_DELETE_BY_WIEGAND_VERIFICATION(0x4b),
    UF_INPUT_DELETE_ALL_VERIFICATION(0x4c),
    UF_INPUT_CANCEL(0x60),
    UF_INPUT_TAMPER_SWITCH_IN(0x66),
    UF_INPUT_RESET(0x69),
    ;
    private int value;

    UF_INPUT_FUNC(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

}
