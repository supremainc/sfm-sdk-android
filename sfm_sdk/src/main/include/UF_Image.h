/**
 *  	Image handling API
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

#ifndef __UNIFINGER_IMAGE_H__
#define __UNIFINGER_IMAGE_H__

#define UF_IMAGE_HEADER_SIZE 7

#define UF_MAX_IMAGE_SIZE (640 * 480)

typedef enum
{
	UF_GRAY_IMAGE = 0x30,
	UF_BINARY_IMAGE = 0x31,
	UF_4BIT_GRAY_IMAGE = 0x32,
	/// added by hclee
	UF_WSQ_IMAGE = 0x33,
	UF_WSQ_HQ_IMAGE = 0x33,
	UF_WSQ_MQ_IMAGE = 0x34,
	UF_WSQ_LQ_IMAGE = 0x35,
} UF_IMAGE_TYPE;

typedef struct
{
	int width;
	int height;
	int compressed;
	int encrypted;
	int format;
	int imgLen;
	int templateLen;
	BYTE buffer[1];
} UFImage;

#endif
