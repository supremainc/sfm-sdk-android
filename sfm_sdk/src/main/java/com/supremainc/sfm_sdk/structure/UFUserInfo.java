/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.structure;

import java.util.Arrays;

public class UFUserInfo {
    private int _userID = 0;
    private byte _numOfTemplate = 0;
    private byte _adminLevel = 0;
    private byte[] _reserved = new byte[]{0, 0};

    public int userID() {
        return this._userID;
    }

    public byte numOfTemplate() {
        return this._numOfTemplate;
    }

    public byte adminLevel() {
        return this._adminLevel;
    }

    public byte[] reserved() {
        return this._reserved;
    }

    public UFUserInfo(int userID, byte numOfTemplate, byte adminLevel, byte[] reserved) {
        this._userID = userID;
        this._numOfTemplate = numOfTemplate;
        this._adminLevel = adminLevel;
        this._reserved = Arrays.copyOf(reserved, this._reserved.length);
    }

    public UFUserInfo() {

    }
}
