/**
 *  Extended Wiegand Interface
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

#ifndef __UNIFINGER_WIEGAND_H__
#define __UNIFINGER_WIEGAND_H__

#define MAX_WIEGAND_FIELD 12
#define MAX_WIEGAND_PARITY 8
#define MAX_WIEGAND_COMMAND_CARD 16

typedef enum
{
	UF_WIEGAND_26BIT = 0x01,
	UF_WIEGAND_PASS_THRU = 0x02,
	UF_WIEGAND_CUSTOM = 0x03,
} UF_WIEGAND_FORMAT;

typedef enum
{
	UF_WIEGAND_EVEN_PARITY = 0,
	UF_WIEGAND_ODD_PARITY = 1,
} UF_WIEGAND_PARITY_TYPE;

typedef struct
{
	int bitIndex;
	int bitLength;
} UFWiegandField;

typedef struct
{
	int bitIndex;
	UF_WIEGAND_PARITY_TYPE type;
	BYTE bitMask[8];
} UFWiegandParity;

typedef struct
{
	UF_WIEGAND_FORMAT format;
	int totalBits;
} UFWiegandFormatHeader;

typedef struct
{
	int numOfIDField;
	UFWiegandField field[MAX_WIEGAND_FIELD];
} UFWiegandPassThruData;

typedef struct
{
	int numOfField;
	UINT32 idFieldMask;
	UFWiegandField field[MAX_WIEGAND_FIELD];
	int numOfParity;
	UFWiegandParity parity[MAX_WIEGAND_PARITY];
} UFWiegandCustomData;

typedef union {
	UFWiegandPassThruData passThruData;
	UFWiegandCustomData customData;
} UFWiegandFormatData;

typedef struct
{
	unsigned short pulseWidth;
	unsigned short pulseInterval;
	UFWiegandFormatHeader header;
	UFWiegandFormatData data;
} UFConfigWiegand;

typedef enum
{
	UF_WIEGAND_OUTPUT_DISABLE = 0,
	UF_WIEGAND_OUTPUT_WIEGAND_ONLY = 1,
	UF_WIEGAND_OUTPUT_ALL = 2,
	UF_WIEGAND_OUTPUT_ABA_TRACK_II = 3,
} UF_WIEGAND_OUTPUT_MODE;

typedef enum
{
	UF_WIEGAND_INPUT_DISABLE = 0,
	UF_WIEGAND_INPUT_VERIFY = 0x22,
} UF_WIEGAND_INPUT_MODE;

typedef struct
{
	UINT32 userID;
	UF_INPUT_FUNC function;
} UFWiegandCommandCard;

#endif
