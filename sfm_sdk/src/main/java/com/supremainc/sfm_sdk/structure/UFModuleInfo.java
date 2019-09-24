package com.supremainc.sfm_sdk.structure;

import com.supremainc.sfm_sdk.enumeration.UF_MODULE_SENSOR;
import com.supremainc.sfm_sdk.enumeration.UF_MODULE_TYPE;
import com.supremainc.sfm_sdk.enumeration.UF_MODULE_VERSION;

public class UFModuleInfo {
    public UF_MODULE_TYPE _type;
    public UF_MODULE_VERSION _version;
    public UF_MODULE_SENSOR _sensor;

    public UF_MODULE_TYPE type() {
        return this._type;
    }

    public UF_MODULE_VERSION version() {
        return this._version;
    }

    public UF_MODULE_SENSOR sensorType() {
        return this._sensor;
    }

    public UFModuleInfo(UF_MODULE_TYPE t, UF_MODULE_VERSION v, UF_MODULE_SENSOR s) {
        this._type = t;
        this._version = v;
        this._sensor = s;
    }

    public UFModuleInfo() {

    }

    public void setInfo(UFModuleInfo info) {
        this._type = info._type;
        this._version = info._version;
        this._sensor = info._sensor;
    }
}
