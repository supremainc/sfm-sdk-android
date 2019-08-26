/**
 *  	Type definition
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

#ifndef __UNIFINGER_DEF_H__
#define __UNIFINGER_DEF_H__

#ifdef UF_API_STATIC
#define UF_API
#else
#if defined _WIN32 || defined _WIN64 || defined __CYGWIN__
#ifdef UF_API_EXPORTS
#ifdef __GNUC__
#define UF_API __attribute__((dllexport))
#else
#define UF_API __declspec(dllexport)
#endif
#else
#ifdef __GNUC__
#define UF_API __attribute__((dllimport))
#else
#define UF_API __declspec(dllimport)
#endif
#endif
#else
#if __GNUC__ >= 4
#define UF_API __attribute__((visibility("default")))
#else
#define UF_API
#endif
#endif
#endif

#if defined(_WIN32)
#include <windows.h>
#else

//
// Type definition
//
#include <stdint.h>
#include <stdbool.h>

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

// DWORD, UINT32, and LONG must be used as unsigned 32-bit data for compatibility between 32-bit and 64-bit system.

#ifndef DWORD
typedef uint32_t DWORD;
#endif

#ifndef WORD
typedef uint16_t WORD;

#ifndef BYTE
typedef uint8_t BYTE;
#endif

#ifndef BOOL
#ifndef _Bool
typedef bool BOOL;
#elif
typedef _Bool BOOL;
#endif
#endif

#ifndef UINT32
typedef uint32_t UINT32;
#endif

#ifndef USHORT
typedef uint16_t USHORT;
#endif

#ifndef LONG
typedef uint32_t LONG;
#endif

#ifndef HANDLE
typedef void *HANDLE;

#endif
#endif
#endif
#endif
