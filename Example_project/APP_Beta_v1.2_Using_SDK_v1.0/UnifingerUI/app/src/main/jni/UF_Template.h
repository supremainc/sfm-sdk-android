/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGERTEMPLATE_H__
#define __UNIFINGERTEMPLATE_H__

#define MAXIMUM_TEMPLATE_SIZE	384

// option for ST command
#define UF_ADD_CHECKSUM	0x70

typedef enum {
	UF_ADMIN_LEVEL_NONE			= 0,
	UF_ADMIN_LEVEL_ENROLL		= 1,
	UF_ADMIN_LEVEL_DELETE		= 2,
	UF_ADMIN_LEVEL_ALL			= 3
} UF_ADMIN_LEVEL;

typedef enum {
	UF_USER_SECURITY_DEFAULT			= 0,
	UF_USER_SECURITY_1_TO_1000			= 1,
	UF_USER_SECURITY_3_TO_10000			= 2,
	UF_USER_SECURITY_1_TO_10000			= 3,
	UF_USER_SECURITY_3_TO_100000		= 4,
	UF_USER_SECURITY_1_TO_100000		= 5,
	UF_USER_SECURITY_3_TO_1000000		= 6,
	UF_USER_SECURITY_1_TO_1000000		= 7,
	UF_USER_SECURITY_3_TO_10000000		= 8,
	UF_USER_SECURITY_1_TO_10000000		= 9,
	UF_USER_SECURITY_3_TO_100000000		= 10,
	UF_USER_SECURITY_1_TO_100000000		= 11,
} UF_USER_SECURITY_LEVEL;

typedef enum {
	UF_AUTH_FINGERPRINT = 0x00,
	UF_AUTH_BYPASS = 0x01,
	UF_AUTH_REJECT = 0x03,
} UF_AUTH_TYPE;

typedef struct {
	unsigned int 	userID;
	unsigned char	numOfTemplate;
	unsigned char	adminLevel;
	unsigned char	reserved[2]; // reserved[0] is used for user security level
} UFUserInfo;

typedef struct {
	unsigned int 	userID;
	unsigned int 	checksum[10];
	unsigned char	numOfTemplate;
	unsigned char	adminLevel;
	unsigned char	duress[10];
	unsigned char	securityLevel;
} UFUserInfoEx;


#define UF_VALID_TEMPLATE_DB	0x1f2f3f4f

typedef struct {
	unsigned int 	magicNo;
	unsigned int 	numOfUser;
	unsigned int 	numOfTemplate;
	unsigned int	templateSize;
	unsigned int	dataSize;
} UFTemplateDBHeader;

typedef struct {
	unsigned int	userID;
	unsigned int	userLevel; // securityLevel << 16 | adminLevel
	unsigned int 	numOfTemplate;
	unsigned int	checksum;
} UFUserItemHeader; 

#ifdef __cplusplus
extern "C" 
{
#endif

int UF_SortByUserID( const void* elem1, const void* elm2 );

unsigned char UF_ScanScaMsgCallback( unsigned char errCode );

#ifdef __cplusplus
}

#endif


#endif
