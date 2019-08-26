/**
 *  	Packet definition
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

#ifndef __UNIFINGER_PACKET_H__
#define __UNIFINGER_PACKET_H__

#ifdef _WIN32
#include <windows.h>
#endif // _WIN32

//
// Constants
//
#define UF_PACKET_START_CODE 0x40
#define UF_NETWORK_PACKET_START_CODE 0x41
#define UF_PACKET_END_CODE 0x0a
#define UF_PACKET_LEN 13
#define UF_NETWORK_PACKET_LEN 15

#define UF_PACKET_COMMAND 0
#define UF_PACKET_TERMINAL_ID 1
#define UF_PACKET_PARAM 2
#define UF_PACKET_SIZE 3
#define UF_PACKET_FLAG 4
#define UF_PACKET_CHECKSUM 5

//
// Byte position of packet components
//
#define UF_PACKET_START_CODE_POS 0
#define UF_PACKET_COMMAND_POS 1
#define UF_PACKET_PARAM_POS 2
#define UF_PACKET_SIZE_POS 6
#define UF_PACKET_FLAG_POS 10
#define UF_PACKET_CHECKSUM_POS 11
#define UF_PACKET_END_CODE_POS 12

#define UF_NETWORK_PACKET_START_CODE_POS 0
#define UF_NETWORK_PACKET_TERMINALID_POS 1
#define UF_NETWORK_PACKET_COMMAND_POS 3
#define UF_NETWORK_PACKET_PARAM_POS 4
#define UF_NETWORK_PACKET_SIZE_POS 8
#define UF_NETWORK_PACKET_FLAG_POS 12
#define UF_NETWORK_PACKET_CHECKSUM_POS 13
#define UF_NETWORK_PACKET_END_CODE_POS 14

//
// Data packet
//
#define UF_DEFAULT_DATA_PACKET_SIZE (4 * 1024)

#ifdef __cplusplus
extern "C"
{
#endif

    void UF_MakePacket(BYTE command, UINT32 param, UINT32 size, BYTE flag, BYTE *packet);
    void UF_MakeNetworkPacket(BYTE command, USHORT terminalID, UINT32 param, UINT32 size, BYTE flag, BYTE *packet);

    void UF_MakeDataPacket(BYTE command, USHORT index, USHORT numOfPacket, UINT32 dataSize, BYTE *packet);
    void UF_MakeNetworkDataPacket(BYTE command, USHORT terminalID, USHORT index, USHORT numOfPacket, UINT32 dataSize, BYTE *packet);

    BYTE UF_CalculateChecksum(BYTE *packet, int size);
    UINT32 UF_CalculateDataChecksum(BYTE *packet, int size);

    UF_API UINT32 UF_GetPacketValue(int component, BYTE *packet);
    UF_API UINT32 UF_GetNetworkPacketValue(int component, BYTE *packet);

    UF_API int UF_ReadData(unsigned char *buf, int size, int timeout);
    UF_API int UF_WriteData(unsigned char *buf, int size, int timeout);

    UF_API int UF_ClearReadBuffer();
    UF_API int UF_ClearWriteBuffer();

#ifdef __cplusplus
}
#endif

#endif
