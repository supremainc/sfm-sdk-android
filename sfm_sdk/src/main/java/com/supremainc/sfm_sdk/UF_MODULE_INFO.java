package com.supremainc.sfm_sdk;

public class UF_MODULE_INFO {
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

    public UF_MODULE_INFO(UF_MODULE_TYPE t, UF_MODULE_VERSION v, UF_MODULE_SENSOR s) {
        this._type = t;
        this._version = v;
        this._sensor = s;
    }

    public UF_MODULE_INFO() {

    }

    public void setInfo(UF_MODULE_INFO info) {
        this._type = info._type;
        this._version = info._version;
        this._sensor = info._sensor;
    }
}
