/**
 *  	User & Template Management
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

#ifndef __UNIFINGER_TEMPLATE_H__
#define __UNIFINGER_TEMPLATE_H__

#define MAXIMUM_TEMPLATE_SIZE 384

// option for ST command
#define UF_ADD_CHECKSUM 0x70

typedef enum
{
	UF_ADMIN_LEVEL_NONE = 0,
	UF_ADMIN_LEVEL_ENROLL = 1,
	UF_ADMIN_LEVEL_DELETE = 2,
	UF_ADMIN_LEVEL_ALL = 3
} UF_ADMIN_LEVEL;

typedef enum
{
	UF_USER_SECURITY_DEFAULT = 0,
	UF_USER_SECURITY_1_TO_1000 = 1,
	UF_USER_SECURITY_3_TO_10000 = 2,
	UF_USER_SECURITY_1_TO_10000 = 3,
	UF_USER_SECURITY_3_TO_100000 = 4,
	UF_USER_SECURITY_1_TO_100000 = 5,
	UF_USER_SECURITY_3_TO_1000000 = 6,
	UF_USER_SECURITY_1_TO_1000000 = 7,
	UF_USER_SECURITY_3_TO_10000000 = 8,
	UF_USER_SECURITY_1_TO_10000000 = 9,
	UF_USER_SECURITY_3_TO_100000000 = 10,
	UF_USER_SECURITY_1_TO_100000000 = 11,
} UF_USER_SECURITY_LEVEL;

typedef enum
{
	UF_AUTH_FINGERPRINT = 0x00,
	UF_AUTH_BYPASS = 0x01,
	UF_AUTH_REJECT = 0x03,
} UF_AUTH_TYPE;

typedef struct
{
	UINT32 userID;
	BYTE numOfTemplate;
	BYTE adminLevel;
	BYTE reserved[2]; // reserved[0] is used for user security level
} UFUserInfo;

typedef struct
{
	UINT32 userID;
	UINT32 checksum[10];
	BYTE numOfTemplate;
	BYTE authMode : 4;
	BYTE adminLevel : 4;
	BYTE duress[10];
	BYTE securityLevel;
} UFUserInfoEx;

#define UF_VALID_TEMPLATE_DB 0x1f2f3f4f

typedef struct
{
	UINT32 magicNo;
	UINT32 numOfUser;
	UINT32 numOfTemplate;
	UINT32 templateSize;
	UINT32 dataSize;
} UFTemplateDBHeader;

typedef struct
{
	UINT32 userID;
	UINT32 userLevel; // entLimit << 24 | securityLevel << 16 | authMode << 8 | adminLevel
	UINT32 numOfTemplate;
	UINT32 checksum;
} UFUserItemHeader;

#ifdef __cplusplus
extern "C"
{
#endif

	UF_API int UF_SortByUserID(const void *elem1, const void *elem2);

#ifdef __cplusplus
}

#endif

#endif
