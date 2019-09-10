/**
 *  Definitions of Smartcard related constants
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

#ifndef __UNIFINGER_SMARTCARD_H__
#define __UNIFINGER_SMARTCARD_H__

#define UF_SMARTCARD_KEY_SIZE 6

typedef struct
{
	UINT32 csn;
	UINT32 wiegandLower;
	UINT32 wiegandHigher;
	BYTE version;
	BYTE commandType; // entrance limit if it is not a command card
	BYTE securityLevel;
	BYTE numOfTemplate;
	BYTE template1Duress;
	BYTE template1Length[2];
	BYTE template2Duress;
	BYTE template2Length[2];
} UFCardHeader;

typedef struct
{
	unsigned short templateSize;
	BYTE headerBlock;
	BYTE template1StartBlock;
	BYTE template1BlockSize;
	BYTE template2StartBlock;
	BYTE template2BlockSize;
} UFCardLayout;

typedef enum
{
	UF_SECURITY_READER_DEFAULT = 0,
	UF_SECURITY_1_TO_10000 = 1,
	UF_SECURITY_3_TO_100000 = 2,
	UF_SECURITY_1_TO_100000 = 3,
	UF_SECURITY_3_TO_1000000 = 4,
	UF_SECURITY_1_TO_1000000 = 5,
	UF_SECURITY_3_TO_10000000 = 6,
	UF_SECURITY_1_TO_10000000 = 7,
	UF_SECURITY_3_TO_100000000 = 8,
	UF_SECURITY_1_TO_100000000 = 9,
	UF_SECURITY_BYPASS = 10,
	UF_SECURITY_1_TO_1000 = 11,
	UF_SECURITY_3_TO_10000 = 12
} UF_CARD_SECURITY_LEVEL;

typedef enum
{
	UF_CARD_DISABLE = 0x00,
	UF_CARD_VERIFY_ID = 0x01,
	UF_CARD_VERIFY_TEMPLATE = 0x02,
} UF_CARD_MODE;

typedef enum
{
	UF_CHANGE_OPTION_ONLY = 0x00,
	UF_CHANGE_PRIMARY_KEY = 0x01,
	UF_CHANGE_SECONDARY_KEY = 0x02
} UF_WRITE_KEY_OPTION;

#endif
