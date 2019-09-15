package com.supremainc.sfm_sdk.structure;

import java.util.Arrays;

public class UFUserInfoEx {
    private int _userID = 0;
    private int[] _checkSum = new int[10];
    private byte _numOfTemplate = 0;
    private byte _authMode = 0;
    private byte _adminLevel = 0;
    private byte[] _duress = new byte[10];
    private byte _securityLevel = 0;

    public int userID() {
        return this._userID;
    }

    public int[] checkSum() {
        return this._checkSum;
    }

    public byte numOfTemplate() {
        return this._numOfTemplate;
    }

    public byte authMode() {
        return this._authMode;
    }

    public byte adminLevel() {
        return this._adminLevel;
    }

    public byte[] duress() {
        return this.duress();
    }

    public byte securityLevel() {
        return this._securityLevel;
    }

    public UFUserInfoEx(int userID, int[] checksum, byte numOfTemplate, byte authMode, byte adminLevel, byte[] duress, byte securitylevel) {
        this._userID = userID;
        this._checkSum = Arrays.copyOf(checksum, checksum.length);
        this._numOfTemplate = numOfTemplate;
        this._adminLevel = adminLevel;
        this._authMode = authMode;
        this._duress = Arrays.copyOf(duress, duress.length);
        this._securityLevel = securitylevel;
    }

    public UFUserInfoEx() {

    }
}
