/**
 *  	Access Control
 */

/*  
 *  Copyright (c) 2006 Suprema Inc. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Inc. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __ACCESSCONTROL_H__
#define __ACCESSCONTROL_H__

#define UF_TIMECODE_PER_DAY 5
#define UF_SCHEDULE_PER_GROUP 16
#define UF_GROUP_PER_USER 4
#define UF_MAX_HOLIDAY 16
#define UF_MAX_SCHEDULE 64
#define UF_MAX_ACCESS_GROUP 64

typedef struct
{
	unsigned short startTime;
	unsigned short endTime;
} UFTimeCodeElem;

typedef struct
{
	UFTimeCodeElem codeElement[UF_TIMECODE_PER_DAY];
} UFTimeCode;

typedef struct
{
	int scheduleID;			// -1 if not used
	UFTimeCode timeCode[7]; // 0 - Sunday, 1 - Monday, ...
	int holidayID;
} UFTimeSchedule;

typedef struct
{
	int holidayID; // -1 if not used
	int numOfHoliday;
	unsigned short holiday[32]; // (month << 8) | day
	UFTimeCode timeCode;
} UFHoliday;

typedef struct
{
	int groupID; // -1 if not used
	int numOfSchedule;
	int scheduleID[UF_SCHEDULE_PER_GROUP];
} UFAccessGroup;

#endif
