/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */


#ifndef __UNIFINGE3000IO_H__
#define __UNIFINGE3000IO_H__


#define UF_NUM_OF_GPIO					8
#define UF_MAX_GPIO_OUTPUT_EVENT		24
#define UF_GPIO_DATA_SIZE				8


typedef enum {
 	UF_GPIO_0 = 0,
 	UF_GPIO_1 = 1,
 	UF_GPIO_2 = 2,
 	UF_GPIO_3 = 3,
 	UF_GPIO_4 = 4,
 	UF_GPIO_5 = 5,
 	UF_GPIO_6 = 6,
 	UF_GPIO_7 = 7,
} UF_GPIO_PORT;

typedef enum {
	UF_GPIO_DISABLE 			= 0,
	UF_GPIO_INPUT				= 1,
	UF_GPIO_OUTPUT				= 2,	
	UF_GPIO_SHARED_IO			= 3,
	UF_GPIO_WIEGAND_INPUT		= 4,
	UF_GPIO_WIEGAND_OUTPUT		= 5,

	UF_GPIO_NETWORK_MODE		= 6,

	UF_GPIO_MODE_UNKNOWN		= -1,
} UF_GPIO_MODE;

typedef enum {
	UF_GPIO_IN_ENROLL						= 0x01,
	UF_GPIO_IN_IDENTIFY						= 0x02,			
	UF_GPIO_IN_DELETE_ALL					= 0x03,
	UF_GPIO_IN_DELETE_ALL_BY_CONFIRM		= 0x04,
	UF_GPIO_IN_CANCEL						= 0x06,
	UF_GPIO_IN_ENROLL_VERIFICATION 			= 0x07,
	UF_GPIO_IN_DELETE_VERIFICATION			= 0x08,
	UF_GPIO_IN_DELETE_ALL_VERIFICATION		= 0x09,
	UF_GPIO_IN_RESET						= 0x0a,

	UF_GPIO_IN_UNKONWN						= -1,
} UF_GPIO_INPUT_FUNC;

typedef enum {
	UF_GPIO_IN_ACTIVE_HIGH		= 0x01,
	UF_GPIO_IN_ACTIVE_LOW		= 0x02,
	UF_GPIO_IN_RISING_EDGE		= 0x03,
	UF_GPIO_IN_FALLING_EDGE		= 0x04,
} UF_GPIO_INPUT_ACTIVATION;

typedef enum {
	UF_GPIO_OUT_ENROLL_WAIT_FINGER	= 0x81,
	UF_GPIO_OUT_ENROLL_PROCESSING		= 0x82,
	UF_GPIO_OUT_ENROLL_SUCCESS			= 0x83,
	UF_GPIO_OUT_ENROLL_FAIL				= 0x84,
	UF_GPIO_OUT_MATCH_WAIT_FINGER		= 0x85,
	UF_GPIO_OUT_MATCH_PROCESSING		= 0x86,
	UF_GPIO_OUT_MATCH_SUCCESS			= 0x87,
	UF_GPIO_OUT_MATCH_FAIL				= 0x88,
	UF_GPIO_OUT_DELETE_WAIT				= 0x89,
	UF_GPIO_OUT_DELETE_PROCESSING		= 0x8a,
	UF_GPIO_OUT_DELETE_SUCCESS			= 0x8b,
	UF_GPIO_OUT_DELETE_FAIL				= 0x8c,
	UF_GPIO_OUT_BEEP					= 0x8d,

	UF_GPIO_OUT_UNKNOWN					= -1,
} UF_GPIO_OUTPUT_EVENT;

typedef enum {
	UF_GPIO_OUT_ACTIVE_HIGH		= 0x82,
	UF_GPIO_OUT_ACTIVE_LOW		= 0x84,
	UF_GPIO_OUT_HIGH_BLINK		= 0x83,
	UF_GPIO_OUT_LOW_BLINK		= 0x85,
} UF_GPIO_OUTPUT_LEVEL;


typedef struct {
	unsigned short	inputFunction;
	unsigned short	activationLevel;
	unsigned short	timeout;
	unsigned short	reserved;
} UFGPIOInputData;


typedef struct {
	unsigned short 	event;
	unsigned short	level;
	unsigned short	interval;
	unsigned short	blinkingPeriod;
} UFGPIOOutputData;

typedef struct {
	unsigned short	totalBits;
	unsigned short  fcBits;
	unsigned short	idBits;
	unsigned short  fcCode;
} UFGPIOWiegandData;

typedef struct {
	unsigned short field0;
	unsigned short field1;
	unsigned short field2;
	unsigned short field3;
} UFGPIOData;

typedef struct {
	UF_GPIO_MODE mode[UF_NUM_OF_GPIO];
	int numOfData[UF_NUM_OF_GPIO];
	UFGPIOData gpioData[UF_NUM_OF_GPIO][UF_MAX_GPIO_OUTPUT_EVENT];
} UFConfigGPIO;

#endif

