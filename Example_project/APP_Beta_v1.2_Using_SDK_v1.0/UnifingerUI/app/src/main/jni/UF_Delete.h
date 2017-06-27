/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */


#ifndef __UNIFINGERDELETE_H__
#define __UNIFINGERDELETE_H__

// Options for DT command
#define UF_DELETE_ONLY_ONE		0x70
#define UF_DELETE_MULTIPLE_ID	0x71

#ifdef __cplusplus
extern "C" 
{
#endif


unsigned char UF_DeleteMsgCallback( unsigned char errCode );

#ifdef __cplusplus
}
#endif

#endif

