/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */
 
#ifndef __UNIFINGERSERIAL_H__
#define __UNIFINGERSERIAL_H__

//#include <windows.h>

#ifdef __cplusplus
extern "C" 
{
#endif

int UF_ReadSerial( unsigned char* buf, int size, int timeout );
int UF_WriteSerial( unsigned char* buf, int size, int timeout );
/*HANDLE*/int UF_GetCommHandle();

int UF_OpenSerial( const char* deviceName );
int UF_CloseSerial();
int UF_SetupSerial( int baudrate );
int UF_ClearSerialReadBuffer();
int UF_ClearSerialWriteBuffer();
void UF_CancelReadSerial();
void UF_CancelWriteSerial();
int UF_GetBaudrate();

void UF_SetSerialWriteCallback( void (*Callback)( int writtenLen, int totalSize ) );
void UF_SetSerialReadCallback( void (*Callback)( int readLen, int totalSize ) );


#ifdef __cplusplus
}
#endif

#endif


