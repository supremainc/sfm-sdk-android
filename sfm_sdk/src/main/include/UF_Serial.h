/**
 *  	Wrapper API for serial communication
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

#ifndef __UNIFINGER_SERIAL_H__
#define __UNIFINGER_SERIAL_H__

#include "UF_Def.h"

#ifdef _WIN32
#include <windows.h>
#endif // _WIN32

typedef enum
{
	UF_SINGLE_PROTOCOL = 0,
	UF_NETWORK_PROTOCOL = 1,
} UF_PROTOCOL;

typedef enum
{
	UF_SERIAL_CHANNEL = 0,
	UF_SOCKET_CHANNEL = 1,
} UF_CHANNEL_TYPE;

#ifdef __cplusplus
extern "C"
{
#endif

	UF_API int UF_ReadSerial(unsigned char *buf, int size, int timeout);
	UF_API int UF_WriteSerial(unsigned char *buf, int size, int timeout);
	UF_API HANDLE UF_GetCommHandle();

	int UF_OpenSerial(const char *deviceName);
	int UF_CloseSerial();
	int UF_SetupSerial(int baudrate);
	int UF_ClearSerialReadBuffer();
	int UF_ClearSerialWriteBuffer();
	void UF_CancelReadSerial();
	void UF_CancelWriteSerial();
	int UF_GetBaudrate();

	void UF_SetSerialWriteCallback(void (*Callback)(int writtenLen, int totalSize));
	void UF_SetSerialReadCallback(void (*Callback)(int readLen, int totalSize));

#ifdef __cplusplus
}
#endif

#endif
