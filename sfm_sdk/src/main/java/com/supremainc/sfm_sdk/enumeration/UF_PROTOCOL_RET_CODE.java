/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * This enumeration can be used in callback functions to know what the error or the flag means to
 * be returned.
 */
public enum UF_PROTOCOL_RET_CODE {
    UF_PROTO_RET_SUCCESS(0x61),
    UF_PROTO_RET_SCAN_SUCCESS(062),
    UF_PROTO_RET_SCAN_FAIL(0x63),
    UF_PROTO_RET_NOT_FOUND(0x69),
    UF_PROTO_RET_NOT_MATCH(0x6A),
    UF_PROTO_RET_TRY_AGAIN(0x6B),
    UF_PROTO_RET_TIME_OUT(0x6C),
    UF_PROTO_RET_MEM_FULL(0x6D),
    UF_PROTO_RET_EXIST_ID(0x6E),
    UF_PROTO_RET_FINGER_LIMIT(0x72),
    UF_PROTO_RET_CONTINUE(0x74),
    UF_PROTO_RET_UNSUPPORTED(0x75),
    UF_PROTO_RET_INVALID_ID(0x76),
    UF_PROTO_RET_TIMEOUT_MATCH(0x7A),
    UF_PROTO_RET_INVALID_FIRMWARE(0x7C),
    UF_PROTO_RET_BUSY(0x80),
    UF_PROTO_RET_CANCELED(0x81),
    UF_PROTO_RET_DATA_ERROR(0x82),
    UF_PROTO_RET_DATA_OK(0x83),
    UF_PROTO_RET_EXIST_FINGER(0x86),
    UF_PROTO_RET_DURESS_FINGER(0x91),
    UF_PROTO_RET_ACCESS_NOT_GRANTED(0x93),
    UF_PROTO_RET_CARD_ERROR(0xA0),
    UF_PROTO_RET_LOCKED(0xA1),
    UF_PROTO_RET_REJECTED_ID(0x90),
    UF_PROTO_RET_EXCEED_ENTRANCE_LIMIT(0x94),
    UF_PROTO_FAKE_DETECTED(0xB0),

    //ADDED FOR SFM60X0 SERIES
    UF_PROTO_RET_NO_SERIAL_NUMBER(0xA2),
    UF_PROTO_RET_RECOVERY_MODE(0xA3),

    ;
    private int value;

    UF_PROTOCOL_RET_CODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_PROTOCOL_RET_CODE ToProtoRetCode(int i) {
        UF_PROTOCOL_RET_CODE result = null;

        for (UF_PROTOCOL_RET_CODE iter : UF_PROTOCOL_RET_CODE.values()) {
            if (i == iter.getValue()) {
                result = iter;
            }
        }
        return result;
    }
}
