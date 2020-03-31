/**
 *  	Basic Module Information
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

#ifndef __UNIFINGER_MODULE_H__
#define __UNIFINGER_MODULE_H__

typedef enum
{
	UF_MODULE_3000 = 0,
	UF_MODULE_3500 = 1,
	UF_BIOENTRY_SMART = 2,
	UF_BIOENTRY_PASS = 3,
	UF_SFOD_3100 = 4,
	UF_3000FC = 5,
	UF_MODULE_4000 = 6,
	UF_MODULE_5000 = 7,
	UF_MODULE_5500 = 8,
	UF_MODULE_6000 = 9,
	UF_MODULE_SLIM = 10,
	UF_MODULE_UNKNOWN = -1,
} UF_MODULE_TYPE;

typedef enum
{
	UF_VERSION_1_0 = 0,
	UF_VERSION_1_1 = 1,
	UF_VERSION_1_2 = 2,
	UF_VERSION_1_3 = 3,
	UF_VERSION_1_4 = 4,
	UF_VERSION_1_5 = 5,
	UF_VERSION_1_6 = 6,
	UF_VERSION_1_7 = 7,
	UF_VERSION_1_8 = 8,
	UF_VERSION_1_9 = 9,
	UF_VERSION_2_0 = 20,
	UF_VERSION_2_1 = 21,
	UF_VERSION_2_2 = 22,
	UF_VERSION_2_3 = 23,
	UF_VERSION_2_4 = 24,
	UF_VERSION_3_0 = 30,
	UF_VERSION_3_1 = 31,
	UF_VERSION_3_2 = 32,
	UF_VERSION_3_3 = 33,
	UF_VERSION_3_4 = 34,
	UF_VERSION_UNKNOWN = -1,
} UF_MODULE_VERSION;

typedef enum
{
	UF_SENSOR_FL = 0,
	UF_SENSOR_FC = 1,
	UF_SENSOR_OP = 2,
	UF_SENSOR_OC = 3,
	UF_SENSOR_TC = 4,
	UF_SENSOR_OC2 = 5,
	UF_SENSOR_TS = 6,
	UF_SENSOR_OL = 7,
	UF_SENSOR_OH = 8,
	UF_SENSOR_SLIM = 9,
	UF_SENSOR_UNKNOWN = -1,
} UF_MODULE_SENSOR;

#endif
