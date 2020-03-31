/**
 *  	Command definition
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

#ifndef __UNIFINGER_COMMAND_H__
#define __UNIFINGER_COMMAND_H__

#ifdef _WIN32
#include <windows.h>
#endif // _WIN32

//
// Command definitions
//
typedef enum
{
	UF_COM_SW = 0x01,
	UF_COM_SF = 0x02,
	UF_COM_SR = 0x03,
	UF_COM_SS = 0x04,
	UF_COM_CS = 0x1a,
	UF_COM_ES = 0x05,
	UF_COM_EI = 0x06,
	UF_COM_ET = 0x07,
	UF_COM_VS = 0x08,
	UF_COM_VI = 0x09,
	UF_COM_VT = 0x10,
	UF_COM_VH = 0x22,
	UF_COM_IS = 0x11,
	UF_COM_II = 0x12,
	UF_COM_IT = 0x13,
	UF_COM_RI = 0x20,
	UF_COM_RT = 0x14,
	UF_COM_SI = 0x15,
	UF_COM_ST = 0x21,
	UF_COM_DT = 0x16,
	UF_COM_DA = 0x17,
	UF_COM_LT = 0x18,
	UF_COM_CT = 0x19,
	UF_COM_FP = 0x23,
	UF_COM_DP = 0x24,
	UF_COM_KW = 0x34,
	UF_COM_KS = 0x35,
	UF_COM_GR = 0x36,
	UF_COM_GW = 0x37,
	UF_COM_GC = 0x38,
	UF_COM_GD = 0x39,
	UF_COM_DS = 0x1e,
	UF_COM_EW = 0x1c,
	UF_COM_VW = 0x1d,
	UF_COM_DW = 0x1f,

	UF_COM_WW = 0x41,
	UF_COM_WR = 0x42,
	UF_COM_WG = 0x43,
	UF_COM_WS = 0x44,
	UF_COM_IW = 0x47,
	UF_COM_IR = 0x48,
	UF_COM_IG = 0x49,
	UF_COM_OW = 0x4a,
	UF_COM_OR = 0x4b,
	UF_COM_OL = 0x4c,
	UF_COM_OS = 0x4d,
	UF_COM_TW = 0x3a,
	UF_COM_TR = 0x3b,
	UF_COM_LN = 0x3c,
	UF_COM_LR = 0x3d,
	UF_COM_LD = 0x3e,

	UF_COM_LC = 0x3f,

	UF_COM_ML = 0x31,
	UF_COM_MW = 0x32,
	UF_COM_MR = 0x33,

	UF_COM_CA = 0x60,
	UF_COM_UG = 0x62,

	UF_COM_AW = 0x65,
	UF_COM_AR = 0x66,
	UF_COM_AC = 0x67,

	UF_COM_WM = 0x68,
	UF_COM_WL = 0x69,
	UF_COM_WC = 0x6a,

	UF_COM_WSL = 0x6b,
	UF_COM_RSL = 0x6c,

	UF_COM_ESA = 0x70,
	UF_COM_EWA = 0x71,
	UF_COM_DSA = 0x72,
	UF_COM_DWA = 0x73,
	UF_COM_DAA = 0x74,

	UF_COM_EIX = 0x80,
	UF_COM_IIX = 0x81,
	UF_COM_VIX = 0x82,
	UF_COM_SIX = 0x83,
	UF_COM_RIX = 0x84,

	UF_COM_CR = 0xa0,
	UF_COM_CW = 0xa1,
	UF_COM_CC = 0xa2,
	UF_COM_CG = 0xa8,
	UF_COM_CF = 0xae,
	UF_COM_UW = 0xa3,
	UF_COM_UR = 0xa4,
	UF_COM_UC = 0xa5,
	UF_COM_UL = 0xa6,
	UF_COM_VC = 0xa7,
	UF_COM_EC = 0xa9,

	UF_COM_CKW = 0xaa,
	UF_COM_CKR = 0xab,
	UF_COM_CLR = 0xac,
	UF_COM_CLW = 0xad,

	UF_COM_UM = 0xb0,
	UF_COM_LM = 0xb1,
	UF_COM_MP = 0xb2,
	UF_COM_EV = 0xb3,
	UF_COM_CCR = 0xb5,
	UF_COM_CCW = 0xb6,

	UF_COM_WWX = 0xc0,
	UF_COM_WRX = 0xc1,
	UF_COM_WGX = 0xc2,
	UF_COM_WSX = 0xc3,
	UF_COM_WFW = 0xc4,
	UF_COM_WFR = 0xc5,
	UF_COM_WPW = 0xc6,
	UF_COM_WPR = 0xc7,

	UF_COM_LTX = 0x86,
	UF_COM_ECX = 0xaf,
	UF_COM_ETX = 0x87,
	UF_COM_RTX = 0x89,

	UF_COM_ID = 0x85,

	UF_COM_RS = 0xd0,

	UF_COM_GE = 0xd1,

	UF_COM_OFF = 0xd2,

	UF_COM_WTS = 0xE0,
	UF_COM_RTS = 0xE1,
	UF_COM_CTS = 0xE2,
	UF_COM_WHS = 0xE3,
	UF_COM_RHS = 0xE4,
	UF_COM_CHS = 0xE5,
	UF_COM_WAG = 0xE6,
	UF_COM_RAG = 0xE7,
	UF_COM_CAG = 0xE8,
	UF_COM_WUG = 0xE9,
	UF_COM_RUG = 0xEA,

	UF_COM_CCL = 0xEB,
	UF_COM_RCL = 0xEC,

	UF_COM_CRX = 0xB7,

	UF_COM_WME = 0xF0,
	UF_COM_RME = 0xF1,
	UF_COM_CME = 0xF2,

	UF_COM_ABL = 0xF3,
	UF_COM_DBL = 0xF4,
	UF_COM_RBL = 0xF5,
	UF_COM_CBL = 0xF6,

	UF_COM_FF = 0xFF,
	UF_COM_FR = 0xFA,

	UF_COM_DFU = 0xDF,

} UF_COMMAND;

int UF_CalculateTimeout(int dataSize);

#ifdef __cplusplus
extern "C"
{
#endif

#ifdef __cplusplus
}
#endif

#endif
