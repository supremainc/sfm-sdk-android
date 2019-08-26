/**
 *  	Log 
 */

/*  
 *  Copyright (c) 2001-2019 Suprema Inc. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Inc. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGER_LOG_H__
#define __UNIFINGER_LOG_H__

#define UF_LOG_RECORD_SIZE 16

#include "UF_3500IO.h"

typedef enum
{
	UF_LOG_SOURCE_SYSTEM = 0x00,
	UF_LOG_SOURCE_HOST_PORT = 0x01,
	UF_LOG_SOURCE_AUX_PORT = 0x02,
	UF_LOG_SOURCE_WIEGAND_INPUT = 0x03,
	UF_LOG_SOURCE_IN0 = 0x04,
	UF_LOG_SOURCE_IN1 = 0x05,
	UF_LOG_SOURCE_IN2 = 0x06,
	UF_LOG_SOURCE_FREESCAN = 0x07,
	UF_LOG_SOURCE_SMARTCARD = 0x08,
	UF_LOG_SOURCE_OTHER = 0x0f,
} UF_LOG_SOURCE;

typedef struct
{
	BYTE event;
	BYTE source;
	BYTE date[3];
	BYTE time[3];
	UINT32 userID;
	UINT32 reserved;
} UFLogRecord;

#endif
