/**
 *  	System Parameters
 */

/*  
 *  Copyright (c) 2001-2020 Suprema Inc. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Inc. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGER_SYSPARAMETER_H__
#define __UNIFINGER_SYSPARAMETER_H__

#include "UF_API.h"
#include "UF_Error.h"
#include "UF_3500IO.h"
#include "UF_3000IO.h"
#include "UF_Wiegand.h"

#define UF_SYS_PARAMETER_NOT_FOUND 0xffffffff
#define UF_SYS_PARAMETER_NOT_READ 0x00

typedef struct
{
	int parameter;
	UINT32 value;
} SysParameter;

/* System Parameter */
extern SysParameter s_SysParameter[];

/* System Parameter List (String Name)*/
extern char s_SysParameterList[][20];

typedef enum
{
	UF_SYS_TIMEOUT = 0x62,
	UF_SYS_ENROLL_MODE = 0x65,
	UF_SYS_SECURITY_LEVEL = 0x66,
	UF_SYS_ENCRYPTION_MODE = 0x67,
	UF_SYS_SENSOR_TYPE = 0x68,
	UF_SYS_IMAGE_FORMAT = 0x6c,
	UF_SYS_MODULE_ID = 0x6d,
	UF_SYS_FIRMWARE_VERSION = 0x6e,
	UF_SYS_SERIAL_NUMBER = 0x6f,
	UF_SYS_BAUDRATE = 0x71,
	UF_SYS_AUX_BAUDRATE = 0x72,
	UF_SYS_ENROLLED_FINGER = 0x73,
	UF_SYS_AVAILABLE_FINGER = 0x74,
	UF_SYS_SEND_SCAN_SUCCESS = 0x75,
	UF_SYS_ASCII_PACKET = 0x76,
	UF_SYS_ROTATE_IMAGE = 0x77,
	UF_SYS_SENSITIVITY = 0x80,
	UF_SYS_IMAGE_QUALITY = 0x81,
	UF_SYS_AUTO_RESPONSE = 0x82,
	UF_SYS_NETWORK_MODE = 0x83,
	UF_SYS_FREE_SCAN = 0x84,
	UF_SYS_PROVISIONAL_ENROLL = 0x85,
	UF_SYS_PASS_WHEN_EMPTY = 0x86,
	UF_SYS_RESPONSE_DELAY = 0x87,
	UF_SYS_MATCHING_TIMEOUT = 0x88,
	UF_SYS_BUILD_NO = 0x89,
	UF_SYS_ENROLL_DISPLACEMENT = 0x8a,
	UF_SYS_TEMPLATE_SIZE = 0x64,
	UF_SYS_ROTATION = 0x78,
	UF_SYS_LIGHTING_CONDITION = 0x90,
	UF_SYS_FREESCAN_DELAY = 0x91,
	UF_SYS_CARD_ENROLL_MODE = 0x92,
	UF_SYS_FAST_MODE = 0x93,

	UF_SYS_WATCHDOG = 0x94,

	UF_SYS_TEMPLATE_TYPE = 0x96,

	UF_SYS_ENHANCED_PRIVACY = 0x97,
	UF_SYS_FAKE_DETECT = 0x98,
	//UF_SYS_MAX_TEMPLATE				= 0x99, , 140428
	UF_SYS_CHECK_LATENT = 0x9a,

	UF_SYS_VOLTAGE_WARNING = 0x9b,
	UF_SYS_POWEROFF_TIMEOUT = 0x9c,
	UF_SYS_EXPOSURE = 0x9d,
	UF_SYS_PROTOCOL_INTERFACE = 0X9e,
	UF_SYS_TOUCH_SENSOR_SENSITIVITY = 0xa1, // Added for supporting SFM Slim.
	UF_SYS_RESERVED = 0xff,
} UF_SYS_PARAM;

#define UF_VALID_CONFIG_FILE 0xf1f2f3f4

typedef enum
{
	UF_CONFIG_PARAMETERS = 0x01,
	UF_CONFIG_GPIO = 0x02,
	UF_CONFIG_IO = 0x04,
	UF_CONFIG_WIEGAND = 0x08,
	UF_CONFIG_USER_MEMORY = 0x10,
} UF_CONFIG_TYPE;

typedef struct
{
	UINT32 magicNo;
	UINT32 numOfComponent;
	char description[256];
} UFConfigFileHeader;

typedef struct
{
	UF_CONFIG_TYPE type;
	UINT32 dataSize;
	UINT32 checksum;
} UFConfigComponentHeader;

typedef struct
{
	UF_SYS_PARAM parameter;
	UINT32 value;
} UFConfigParameterItem;

typedef struct
{
	int numOfParameter;
	UFConfigParameterItem parameter[1];
} UFConfigParameter;

#ifdef __cplusplus
extern "C"
{
#endif

	UF_RET_CODE UF_ReadSysParameter(UF_SYS_PARAM parameter, UINT32 *value);
	UF_RET_CODE UF_WriteSysParameter(UF_SYS_PARAM parameter, UINT32 value);
	UF_RET_CODE UF_ReadMultiSysParameter(int parameterCount, UF_SYS_PARAM *parameters, UINT32 *values);
	UF_RET_CODE UF_WriteMultiSysParameter(int parameterCount, UF_SYS_PARAM *parameters, UINT32 *values);
	UF_RET_CODE UF_ApplyConfigComponent(UFConfigComponentHeader *header, void *data);
	//UF_RET_CODE UF_GetSysParameter( UF_SYS_PARAM parameter, UINT32* value );

#ifdef __cplusplus
}
#endif

#endif
