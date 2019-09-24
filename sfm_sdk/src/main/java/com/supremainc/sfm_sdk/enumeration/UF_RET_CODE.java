/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.enumeration;

/**
 * Please refer to the SFM Protocol Manual for the details.
 */
public enum UF_RET_CODE {
    UF_RET_SUCCESS(0),

    // serial communication error
    UF_ERR_CANNOT_OPEN_SERIAL(-1),
    UF_ERR_CANNOT_SETUP_SERIAL(-2),
    UF_ERR_CANNOT_WRITE_SERIAL(-3),
    UF_ERR_WRITE_SERIAL_TIMEOUT(-4),
    UF_ERR_CANNOT_READ_SERIA(-5),
    UF_ERR_READ_SERIAL_TIMEOUT(-6),
    UF_ERR_CHECKSUM_ERROR(-7),
    UF_ERR_CANNOT_SET_TIMEOUT(-8),

    // generic command error code
    UF_ERR_SCAN_FAIL(-101),
    UF_ERR_NOT_FOUND(-102),
    UF_ERR_NOT_MATCH(-103),
    UF_ERR_TRY_AGAIN(-104),
    UF_ERR_TIME_OUT(-105),
    UF_ERR_MEM_FULL(-106),
    UF_ERR_EXIST_ID(-107),
    UF_ERR_FINGER_LIMIT(-108),
    UF_ERR_UNSUPPORTED(-109),
    UF_ERR_INVALID_ID(-110),
    UF_ERR_TIMEOUT_MATCH(-111),
    UF_ERR_BUSY(-112),
    UF_ERR_CANCELED(-113),
    UF_ERR_DATA_ERROR(-114),
    UF_ERR_EXIST_FINGER(-115),
    UF_ERR_DURESS_FINGER(-116),
    UF_ERR_CARD_ERROR(-117),
    UF_ERR_LOCKED(-118),
    UF_ERR_ACCESS_NOT_GRANTED(-119),
    UF_ERR_REJECTED_ID(-120),
    UF_ERR_EXCEED_ENTRANCE_LIMIT(-121),
    UF_ERR_FAKE_DETECTED(-122),

    UF_ERR_OUT_OF_MEMORY(-200),
    UF_ERR_INVALID_PARAMETER(-201),
    UF_ERR_FILE_IO(-202),
    UF_ERR_INVALID_FILE(-203),

    // socket error
    UF_ERR_CANNOT_START_SOCKET(-301),
    UF_ERR_CANNOT_OPEN_SOCKE(-302),
    UF_ERR_CANNOT_CONNECT_SOCKET(-303),
    UF_ERR_CANNOT_READ_SOCKET(-304),
    UF_ERR_READ_SOCKET_TIMEOUT(-305),
    UF_ERR_CANNOT_WRITE_SOCKET(-306),
    UF_ERR_WRITE_SOCKET_TIMEOUT(-307),

    UF_ERR_RECOVERY_MODE(-401),
    UF_ERR_NO_SERIAL_NUMBER(-402),

    UF_ERR_UNKNOWN(-9999),

    ;
    private int value;

    UF_RET_CODE(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_RET_CODE ToRetCode(int i) {
        UF_RET_CODE result = null;

        for(UF_RET_CODE iter : UF_RET_CODE.values()) {
            if(i == iter.getValue()) {
                result = iter;
            }
        }
        return result;
    }

}

