/*
 *  Copyright (c) 2017 Suprema Co., Ltd. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Co., Ltd. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGERAPI__
#define __UNIFINGERAPI__

#include <stdio.h>
#include "UF_Command.h"
#include "UF_SysParameter.h"
#include "UF_Template.h"
#include "UF_Module.h"
#include "UF_Enroll.h"
#include "UF_Image.h"
#include "UF_Packet.h"


typedef enum {
	UF_SINGLE_PROTOCOL		= 0,
	UF_NETWORK_PROTOCOL		= 1,
} UF_PROTOCOL;

typedef enum {
	UF_SERIAL_CHANNEL		= 0,
	UF_SOCKET_CHANNEL		= 1,
} UF_CHANNEL_TYPE;


#ifdef __cplusplus
extern "C" 
{
#endif


void UF_SetWriteCommandCallback( int (*Callback)( unsigned char * ) );
void UF_SetReadCommandCallback( int (*Callback)(int, unsigned char *) );
int UF_WriteSerial( unsigned char* buf, int size, int timeout );

//
// Basic packet interface
//
UF_RET_CODE UF_SendPacket( unsigned char command, unsigned int param, unsigned int size, unsigned char flag, int timeout );
UF_RET_CODE UF_SendNetworkPacket( unsigned char command, unsigned short terminalID, unsigned int param, unsigned int size, unsigned char flag, int timeout );
UF_RET_CODE UF_ReceivePacket( unsigned char* packet, int timeout );
UF_RET_CODE UF_ReceiveNetworkPacket( unsigned char* packet, int timeout );
UF_RET_CODE UF_SendRawData( unsigned char* buf, unsigned int size, int timeout );
UF_RET_CODE UF_ReceiveRawData( unsigned char* buf, unsigned int size, int timeout, unsigned char checkEndCode );
UF_RET_CODE UF_SendDataPacket( unsigned char command, unsigned char* buf, unsigned int dataSize, unsigned int dataPacketSize );
UF_RET_CODE UF_ReceiveDataPacket( unsigned char command, unsigned char* buf, unsigned int dataSize );
void UF_SetDefaultPacketSize( int defaultSize );
int UF_GetDefaultPacketSize();

//
// Generic command interface
//
void UF_SetProtocol( UF_PROTOCOL protocol, unsigned int moduleID );
UF_PROTOCOL UF_GetProtocol();
unsigned int UF_GetModuleID();
void UF_SetGenericCommandTimeout( int timeout );
void UF_SetInputCommandTimeout( int timeout );
int UF_GetGenericCommandTimeout();
int UF_GetInputCommandTimeout();
void UF_SetNetworkDelay( int delay );
int UF_GetNetworkDelay();
UF_RET_CODE UF_Command( unsigned char command, unsigned int* param, unsigned int* size, unsigned char * flag );
UF_RET_CODE UF_CommandEx( unsigned char command, unsigned int* param, unsigned int* size, unsigned char* flag, unsigned char (*msgCallback)(unsigned char) );
UF_RET_CODE UF_CommandSendData( unsigned char command, unsigned int* param, unsigned int* size, unsigned char* flag, unsigned char* data, unsigned int dataSize );
UF_RET_CODE UF_CommandSendDataEx( unsigned char command, unsigned int* param, unsigned int* size, unsigned char* flag, unsigned char* data, unsigned int dataSize, unsigned char (*msgCallback)(unsigned char), unsigned char waitUserInput );
UF_RET_CODE UF_Cancel( unsigned char receivePacket );

//
// Module information
//
UF_RET_CODE UF_GetModuleInfo( UF_MODULE_TYPE* type, UF_MODULE_VERSION* version, UF_MODULE_SENSOR* sensorType );
char* UF_GetModuleString( UF_MODULE_TYPE type, UF_MODULE_VERSION version, UF_MODULE_SENSOR sensorType );
UF_RET_CODE UF_SearchModule( const char* port, int* baudrate, unsigned char* asciiMode, UF_PROTOCOL* protocol, unsigned int* moduleID, void (*callback)( const char* comPort, int baudrate ) );
UF_RET_CODE UF_SearchModuleBySocket( const char* inetAddr, int tcpPort, unsigned char* asciiMode, UF_PROTOCOL* protocol, unsigned int* moduleID );
UF_RET_CODE UF_SearchModuleID( unsigned int* moduleID );
UF_RET_CODE UF_SearchModuleIDEx( unsigned short* foundModuleID, int numOfFoundID, unsigned short* moduleID, int* numOfID );
UF_RET_CODE UF_CalibrateSensor();
UF_RET_CODE UF_Reset();
UF_RET_CODE UF_Lock();
UF_RET_CODE UF_Unlock( const unsigned char* password );
UF_RET_CODE UF_ChangePassword( const unsigned char* newPassword, const unsigned char* oldPassword );
UF_RET_CODE UF_ReadChallengeCode( unsigned char* challengeCode );
UF_RET_CODE UF_WriteChallengeCode( const unsigned char* challengeCode );

//
// System parameters
//
void UF_InitSysParameter();
UF_RET_CODE UF_GetSysParameter( UF_SYS_PARAM parameter, unsigned int* value );
UF_RET_CODE UF_SetSysParameter( UF_SYS_PARAM parameter, unsigned int value );
UF_RET_CODE UF_GetMultiSysParameter( int parameterCount, UF_SYS_PARAM* parameters, unsigned int* values );
UF_RET_CODE UF_SetMultiSysParameter( int parameterCount, UF_SYS_PARAM* parameters, unsigned int* values );
UF_RET_CODE UF_Save();
UF_RET_CODE UF_SaveConfiguration( const char* filename, const char* description, int numOfComponent, UFConfigComponentHeader* componentHeader, void** componentData );
UF_RET_CODE UF_ReadConfigurationHeader( const char* filename, UFConfigFileHeader* header );
UF_RET_CODE UF_LoadConfiguration( const char* filename, int numOfComponent, UF_CONFIG_TYPE* type );
UF_RET_CODE UF_MakeParameterConfiguration( UFConfigComponentHeader* configHeader, unsigned char* configData );


//
// Template management
//
UF_RET_CODE UF_GetNumOfTemplate( unsigned int* numOfTemplate );
UF_RET_CODE UF_GetMaxNumOfTemplate( unsigned int* maxNumOfTemplate );
UF_RET_CODE UF_GetAllUserInfo( UFUserInfo* userInfo, unsigned int* numOfUser, unsigned int* numOfTemplate );
UF_RET_CODE UF_GetAllUserInfoEx( UFUserInfoEx* userInfo, unsigned int* numOfUser, unsigned int* numOfTemplate );
void UF_SortUserInfo( UFUserInfo* userInfo, int numOfUser );
void UF_SetUserInfoCallback( void (*callback)( int index, int numOfTemplate ) );
UF_RET_CODE UF_SetAdminLevel( unsigned int userID, UF_ADMIN_LEVEL adminLevel );
UF_RET_CODE UF_GetAdminLevel( unsigned int userID, UF_ADMIN_LEVEL* adminLevel );
UF_RET_CODE UF_SetSecurityLevel( unsigned int userID, UF_USER_SECURITY_LEVEL securityLevel );
UF_RET_CODE UF_GetSecurityLevel( unsigned int userID, UF_USER_SECURITY_LEVEL* securityLevel );
UF_RET_CODE UF_ClearAllAdminLevel();
UF_RET_CODE UF_SaveDB( const char* fileName );
UF_RET_CODE UF_LoadDB( const char* fileName );
UF_RET_CODE UF_CheckTemplate( unsigned int userID, unsigned int* numOfTemplate );
UF_RET_CODE UF_ReadTemplate( unsigned int userID, unsigned int* numOfTemplate, unsigned char* templateData );
UF_RET_CODE UF_ReadOneTemplate( unsigned int userID, int subID, unsigned char* templateData );
void UF_SetScanCallback( void (*Callback)( unsigned char ) );
UF_RET_CODE UF_ScanTemplate( unsigned char* templateData, unsigned int* templateSize, unsigned int* imageQuality );
UF_RET_CODE UF_FixProvisionalTemplate();
UF_RET_CODE UF_SetAuthType( unsigned int userID, UF_AUTH_TYPE authType );
UF_RET_CODE UF_GetAuthType( unsigned int userID, UF_AUTH_TYPE* authType );
UF_RET_CODE UF_GetUserIDByAuthType( UF_AUTH_TYPE authType, int* numOfID, unsigned int* userID );
UF_RET_CODE UF_ResetAllAuthType();
UF_RET_CODE UF_AddBlacklist( unsigned int userID, int* numOfBlacklistedID );
UF_RET_CODE UF_DeleteBlacklist( unsigned int userID, int* numOfBlacklistedID );
UF_RET_CODE UF_GetBlacklist( int* numOfBlacklistedID, unsigned int* userID );
UF_RET_CODE UF_DeleteAllBlacklist();
UF_RET_CODE UF_SetEntranceLimit( unsigned int userID, int entranceLimit );
UF_RET_CODE UF_GetEntranceLimit( unsigned int userID, int* entranceLimit, int* entranceCount );
UF_RET_CODE UF_ClearAllEntranceLimit();

//
// Image 
//

UF_RET_CODE UF_ReadImage( UFImage* image );
UF_RET_CODE UF_ScanImage( UFImage* image );


//
// Identify
//
void UF_SetIdentifyCallback( void (*Callback)( unsigned char ) );
UF_RET_CODE UF_Identify( unsigned int* userID, unsigned char* subID );
UF_RET_CODE UF_IdentifyTemplate( unsigned int templateSize, unsigned char* templateData, unsigned int* userID, unsigned char* subID );
UF_RET_CODE UF_IdentifyImage( unsigned int imageSize, unsigned char* imageData, unsigned int* userID, unsigned char* subID );

//
// Verify
//
void UF_SetVerifyCallback( void (*Callback)( unsigned char ) );
UF_RET_CODE UF_Verify( unsigned int userID, unsigned char* subID );
UF_RET_CODE UF_VerifyTemplate( unsigned int templateSize, unsigned char* templateData, unsigned int userID, unsigned char* subID );
UF_RET_CODE UF_VerifyHostTemplate( unsigned int numOfTemplate, unsigned int templateSize, unsigned char* templateData );
UF_RET_CODE UF_VerifyImage( unsigned int imageSize, unsigned char* imageData, unsigned int userID, unsigned char* subID );


//
// Enroll
//
void UF_SetEnrollCallback( void (*Callback)( unsigned char errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess ) );
UF_RET_CODE UF_Enroll( unsigned int userID, UF_ENROLL_OPTION option, unsigned int* enrollID, unsigned int* imageQuality );
UF_RET_CODE UF_GetEnrollMode();
UF_RET_CODE UF_EnrollContinue(  unsigned int userID, unsigned int* enrollID, unsigned int* imageQuality );
UF_RET_CODE UF_EnrollAfterVerification( unsigned int userID, UF_ENROLL_OPTION option, unsigned int* enrollID, unsigned int* imageQuality );
UF_RET_CODE UF_EnrollTemplate( unsigned int userID, UF_ENROLL_OPTION option, unsigned int templateSize, unsigned char* templateData, unsigned int* enrollID );
UF_RET_CODE UF_EnrollMultipleTemplates( unsigned int userID, UF_ENROLL_OPTION option, int numOfTemplate, unsigned int templateSize, unsigned char* templateData, unsigned int* enrollID );
UF_RET_CODE UF_EnrollMultipleTemplatesEx( unsigned int userID, UF_ENROLL_OPTION option, int numOfTemplate, int numOfEnroll,  unsigned int templateSize, unsigned char* templateData, unsigned int* enrollID );
UF_RET_CODE UF_EnrollImage( unsigned int userID, UF_ENROLL_OPTION option, unsigned int imageSize, unsigned char* imageData, unsigned int* enrollID, unsigned int* imageQuality );


//
// Delete
//
void UF_SetDeleteCallback( void (*Callback)( unsigned char ) );
UF_RET_CODE UF_Delete( unsigned int userID );
UF_RET_CODE UF_DeleteOneTemplate( unsigned int userID, int subID );
UF_RET_CODE UF_DeleteMultipleTemplates( unsigned int startUserID, unsigned int lastUserID, int* deletedUserID  );
UF_RET_CODE UF_DeleteAll();
UF_RET_CODE UF_DeleteAllAfterVerification();


//
// IO for SFM3500
//

void UF_InitIO();
UF_RET_CODE UF_SetInputFunction( UF_INPUT_PORT port, UF_INPUT_FUNC inputFunction, unsigned int minimumTime );
UF_RET_CODE UF_GetInputFunction( UF_INPUT_PORT port, UF_INPUT_FUNC* inputFunction, unsigned int* minimumTime );
UF_RET_CODE UF_GetInputStatus( UF_INPUT_PORT port, unsigned char remainStatus, unsigned int* status );
UF_RET_CODE UF_GetOutputEventList( UF_OUTPUT_PORT port, UF_OUTPUT_EVENT* events, int* numOfEvent );
UF_RET_CODE UF_ClearAllOutputEvent( UF_OUTPUT_PORT port );
UF_RET_CODE UF_ClearOutputEvent( UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event );
UF_RET_CODE UF_SetOutputEvent( UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal signal );
UF_RET_CODE UF_GetOutputEvent( UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal* signal );
UF_RET_CODE UF_SetOutputStatus( UF_OUTPUT_PORT port, unsigned char status );
UF_RET_CODE UF_SetLegacyWiegandConfig( unsigned char enableInput, unsigned char enableOutput, unsigned int fcBits, unsigned int fcCode );
UF_RET_CODE UF_GetLegacyWiegandConfig( unsigned char* enableInput, unsigned char* enableOutput, unsigned int* fcBits, unsigned int* fcCode );
UF_RET_CODE UF_MakeIOConfiguration( UFConfigComponentHeader* configHeader, unsigned char* configData );


//
// IO for SFM3000
//

UF_RET_CODE UF_GetGPIOConfiguration( UF_GPIO_PORT port, UF_GPIO_MODE* mode, int* numOfData, UFGPIOData* data );
UF_RET_CODE UF_SetInputGPIO( UF_GPIO_PORT port, UFGPIOInputData data );
UF_RET_CODE UF_SetOutputGPIO( UF_GPIO_PORT port, int numOfData, UFGPIOOutputData* data );
UF_RET_CODE UF_SetSharedGPIO( UF_GPIO_PORT port, UFGPIOInputData inputData, int numOfOutputData, UFGPIOOutputData* outputData );
UF_RET_CODE UF_DisableGPIO( UF_GPIO_PORT port );
UF_RET_CODE UF_ClearAllGPIO();
UF_RET_CODE UF_SetDefaultGPIO();
UF_RET_CODE UF_EnableWiegandInput( UFGPIOWiegandData data );
UF_RET_CODE UF_EnableWiegandOutput( UFGPIOWiegandData data );
UF_RET_CODE UF_DisableWiegandInput();
UF_RET_CODE UF_DisableWiegandOutput();
UF_RET_CODE UF_MakeGPIOConfiguration( UFConfigComponentHeader* configHeader, unsigned char* configData );



//
// User memory
//
UF_RET_CODE UF_WriteUserMemory( unsigned char* memory );
UF_RET_CODE UF_ReadUserMemory( unsigned char* memory );

//
// Upgrade
//
UF_RET_CODE UF_Upgrade( const char* firmwareFilename, int dataPacketSize );



#ifdef __cplusplus
}
#endif

#endif

