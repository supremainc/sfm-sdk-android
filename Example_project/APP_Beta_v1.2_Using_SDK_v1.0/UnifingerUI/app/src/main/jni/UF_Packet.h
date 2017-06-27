/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGERPACKET_H__
#define __UNIFINGERPACKET_H__

//#include <windows.h>

//
// Constants
//
#define	UF_PACKET_START_CODE					0x40
#define	UF_NETWORK_PACKET_START_CODE			0x41
#define	UF_PACKET_END_CODE						0x0a
#define	UF_PACKET_LEN							13
#define	UF_NETWORK_PACKET_LEN					15

#define	UF_PACKET_COMMAND			0
#define	UF_PACKET_TERMINAL_ID		1
#define	UF_PACKET_PARAM				2
#define	UF_PACKET_SIZE				3
#define	UF_PACKET_FLAG				4
#define	UF_PACKET_CHECKSUM			5

//
// Byte position of packet components
//
#define	UF_PACKET_START_CODE_POS		0
#define	UF_PACKET_COMMAND_POS			1
#define	UF_PACKET_PARAM_POS				2
#define	UF_PACKET_SIZE_POS				6
#define	UF_PACKET_FLAG_POS				10
#define	UF_PACKET_CHECKSUM_POS			11
#define	UF_PACKET_END_CODE_POS			12

#define	UF_NETWORK_PACKET_START_CODE_POS		0
#define	UF_NETWORK_PACKET_TERMINALID_POS		1
#define	UF_NETWORK_PACKET_COMMAND_POS			3
#define	UF_NETWORK_PACKET_PARAM_POS				4
#define	UF_NETWORK_PACKET_SIZE_POS				8
#define	UF_NETWORK_PACKET_FLAG_POS				12
#define	UF_NETWORK_PACKET_CHECKSUM_POS			13
#define	UF_NETWORK_PACKET_END_CODE_POS			14

//
// Data packet
//
#define UF_DEFAULT_DATA_PACKET_SIZE		(4 * 1024)

#ifdef __cplusplus
extern "C"
{
#endif

void UF_MakePacket( unsigned char command, unsigned int param, unsigned int size, unsigned char flag, unsigned char* packet );
void UF_MakeNetworkPacket( unsigned char command, unsigned short terminalID, unsigned int param, unsigned int size, unsigned char flag, unsigned char* packet );

void UF_MakeDataPacket( unsigned char command, unsigned short index, unsigned short numOfPacket, unsigned int dataSize, unsigned char* packet );
void UF_MakeNetworkDataPacket( unsigned char command,  unsigned short terminalID, unsigned short index, unsigned short numOfPacket, unsigned int dataSize, unsigned char* packet );
	
unsigned char UF_CalculateChecksum( unsigned char* packet, int size );
unsigned int UF_CalculateDataChecksum( unsigned char* packet, int size );

unsigned int UF_GetPacketValue( int component, unsigned char* packet );
unsigned int UF_GetNetworkPacketValue( int component, unsigned char* packet );

int UF_ReadData( unsigned char* buf, int size, int timeout );
int UF_WriteData( unsigned char* buf, int size, int timeout );

int UF_ClearReadBuffer();
int UF_ClearWriteBuffer();

#ifdef __cplusplus
}
#endif

 #endif
