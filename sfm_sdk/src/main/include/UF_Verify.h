/**
 *  	Verification
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

#ifndef __UNIFINGER_VERIFY_H__
#define __UNIFINGER_VERIFY_H__

#include "UF_API.h"

#ifdef __cplusplus
extern "C"
{
#endif
    UF_RET_CODE UF_VerifyTemplate(UINT32 templateSize, BYTE *templateData, UINT32 userID, BYTE *subID);

#ifdef __cplusplus
}
#endif

#endif
