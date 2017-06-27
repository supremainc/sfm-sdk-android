/**
 *  	Image handling API
 *
 *  	@author sjlee@suprema.co.kr
 *  	@see    
 */


/*  
 *  Copyright (c) 2005 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */


#ifndef __UNIFINGEIMAGE_H__
#define __UNIFINGEIMAGE_H__

#define UF_IMAGE_HEADER_SIZE	7

#define UF_MAX_IMAGE_SIZE (640 * 480)

typedef enum {
	UF_GRAY_IMAGE			= 0x30,
	UF_BINARY_IMAGE			= 0x31,
	UF_4BIT_GRAY_IMAGE		= 0x32,
} UF_IMAGE_TYPE;

typedef struct {
	int	width;
	int	height;
	int	compressed;
	int	encrypted;
	int	format;
	int	imgLen;
	int	templateLen;
	unsigned char buffer[1];
} UFImage;

#endif
