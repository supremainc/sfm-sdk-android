/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */
 
 #ifndef __UNIFINGERERROR_H__
 #define __UNIFINGERERROR_H__


typedef enum {
	UF_RET_SUCCESS					= 0,

	// serial communication error
	UF_ERR_CANNOT_OPEN_SERIAL		= -1,
	UF_ERR_CANNOT_SETUP_SERIAL		= -2,
	UF_ERR_CANNOT_WRITE_SERIAL		= -3,
	UF_ERR_WRITE_SERIAL_TIMEOUT	= -4,
	UF_ERR_CANNOT_READ_SERIAL		= -5,
	UF_ERR_READ_SERIAL_TIMEOUT		= -6,
	UF_ERR_CHECKSUM_ERROR			= -7,
	UF_ERR_CANNOT_SET_TIMEOUT		= -8,

	// generic command error code
	UF_ERR_SCAN_FAIL				= -101,
	UF_ERR_NOT_FOUND				= -102,
	UF_ERR_NOT_MATCH				= -103,
	UF_ERR_TRY_AGAIN				= -104,
	UF_ERR_TIME_OUT					= -105,
	UF_ERR_MEM_FULL					= -106,
	UF_ERR_EXIST_ID					= -107,
	UF_ERR_FINGER_LIMIT				= -108,
	UF_ERR_UNSUPPORTED				= -109,
	UF_ERR_INVALID_ID				= -110,
	UF_ERR_TIMEOUT_MATCH			= -111,
	UF_ERR_BUSY						= -112,
	UF_ERR_CANCELED					= -113,
	UF_ERR_DATA_ERROR				= -114,
	UF_ERR_EXIST_FINGER 			= -115,
	UF_ERR_DURESS_FINGER 			= -116,
	UF_ERR_CARD_ERROR				= -117,
	UF_ERR_LOCKED					= -118,
	UF_ERR_ACCESS_NOT_GRANTED		= -119,
	UF_ERR_REJECTED_ID				= -120,
	UF_ERR_EXCEED_ENTRANCE_LIMIT	= -121,

	UF_ERR_OUT_OF_MEMORY			= -200,
	UF_ERR_INVALID_PARAMETER		= -201,
	UF_ERR_FILE_IO					= -202,
	UF_ERR_INVALID_FILE				= -203,

	// socket error
	UF_ERR_CANNOT_START_SOCKET		= -301,
	UF_ERR_CANNOT_OPEN_SOCKET		= -302,
	UF_ERR_CANNOT_CONNECT_SOCKET	= -303,
	UF_ERR_CANNOT_READ_SOCKET		= -304,
	UF_ERR_READ_SOCKET_TIMEOUT		= -305,
	UF_ERR_CANNOT_WRITE_SOCKET		= -306,
	UF_ERR_WRITE_SOCKET_TIMEOUT		= -307,
	

	UF_ERR_UNKNOWN					= -9999,
 } UF_RET_CODE;


 typedef enum {
 	UF_PROTO_RET_SUCCESS			= 0x61,
	UF_PROTO_RET_SCAN_SUCCESS		= 0x62,
	UF_PROTO_RET_SCAN_FAIL			= 0x63,
	UF_PROTO_RET_NOT_FOUND			= 0x69,
	UF_PROTO_RET_NOT_MATCH			= 0x6a,
	UF_PROTO_RET_TRY_AGAIN			= 0x6b,
	UF_PROTO_RET_TIME_OUT			= 0x6c,
	UF_PROTO_RET_MEM_FULL			= 0x6d,
	UF_PROTO_RET_EXIST_ID			= 0x6e,
	UF_PROTO_RET_FINGER_LIMIT		= 0x72,
	UF_PROTO_RET_CONTINUE			= 0x74,
	UF_PROTO_RET_UNSUPPORTED		= 0x75,
	UF_PROTO_RET_INVALID_ID			= 0x76,
	UF_PROTO_RET_TIMEOUT_MATCH		= 0x7a,
	UF_PROTO_RET_BUSY				= 0x80,
	UF_PROTO_RET_CANCELED			= 0x81,
	UF_PROTO_RET_DATA_ERROR			= 0x82,
	UF_PROTO_RET_DATA_OK			= 0x83,
	UF_PROTO_RET_EXIST_FINGER 		= 0x86,
	UF_PROTO_RET_DURESS_FINGER 		= 0x91,
	UF_PROTO_RET_ACCESS_NOT_GRANTED = 0x93,
	UF_PROTO_RET_CARD_ERROR			= 0xa0,
	UF_PROTO_RET_LOCKED				= 0xa1,

	UF_PROTO_RET_REJECTED_ID		= 0x90,
	UF_PROTO_RET_EXCEED_ENTRANCE_LIMIT = 0x94,
} UF_PROTOCOL_RET_CODE;

#ifdef __cplusplus
extern "C" 
{
#endif
//__declspec( dllexport ) UF_RET_CODE UF_GetErrorCode( UF_PROTOCOL_RET_CODE retCode );
#ifdef __cplusplus
}
#endif

#endif

