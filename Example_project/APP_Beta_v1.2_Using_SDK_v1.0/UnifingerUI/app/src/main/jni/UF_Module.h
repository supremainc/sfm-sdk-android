/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */
 
#ifndef __UNIFINGERMODULE_H__
#define __UNIFINGERMODULE_H__

typedef enum {
	UF_MODULE_3000			= 0,
	UF_MODULE_3500			= 1, 
	UF_BIOENTRY_SMART		= 2,
	UF_BIOENTRY_PASS		= 3,
	UF_SFOD_3100			= 4,
	UF_3000FC				= 5,

	UF_MODULE_UNKNOWN		= -1,
} UF_MODULE_TYPE;

typedef enum {
	UF_VERSION_1_0			= 0,
	UF_VERSION_1_1			= 1,
	UF_VERSION_1_2			= 2,
	UF_VERSION_1_3			= 3,
	UF_VERSION_1_4			= 4,	
	UF_VERSION_1_5			= 5,	
	UF_VERSION_1_6			= 6,
	UF_VERSION_1_7			= 7,

	UF_VERSION_UNKNOWN		= -1,
} UF_MODULE_VERSION;

typedef enum {
	UF_SENSOR_FL			= 0,
	UF_SENSOR_FC			= 1,
	UF_SENSOR_OP			= 2,
	UF_SENSOR_OC			= 3,
	UF_SENSOR_TC			= 4,
	UF_SENSOR_OC2			= 5,

	UF_SENSOR_UNKNOWN		= -1,
} UF_MODULE_SENSOR;

#endif
