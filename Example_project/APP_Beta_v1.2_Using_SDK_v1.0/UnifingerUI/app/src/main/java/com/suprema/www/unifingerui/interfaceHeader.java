package com.suprema.www.unifingerui;

/**
 * Created by jmlee on 2015-09-09.
 */
interface PACKET_INFO {
    int UF_PACKET_LEN   =   13,
    UF_NETWORK_PACKET_LEN			=		15,
    UF_DEFAULT_DATA_PACKET_SIZE = 4 * 1024;
}
interface CONNECTION_TYPE {
    int FTDI = 0,
        LEGACY_USB = 1;
}

