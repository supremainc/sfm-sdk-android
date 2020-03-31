/**
 *  	Wrapper API for socket communication
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

#ifndef __UNIFINGER_SOCKET_H__
#define __UNIFINGER_SOCKET_H__

#ifdef _WIN32
#include <windows.h>
#endif

#include "UF_API.h"

#ifdef __cplusplus
extern "C"
{
#endif

    UF_API int UF_ReadSocket(unsigned char *buf, int size, int timeout);
    UF_API int UF_WriteSocket(unsigned char *buf, int size, int timeout);

    void UF_SetSocketWriteCallback(void (*Callback)(int writtenLen, int totalSize));
    void UF_SetSocketReadCallback(void (*Callback)(int readLen, int totalSize));

    int UF_ClearSocketReadBuffer();
    int UF_ClearSocketWriteBuffer();

#ifdef __cplusplus
}
#endif

#endif
