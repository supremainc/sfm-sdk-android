/**
 *  	SFM SDK Main API
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

#ifndef __UNIFINGER_API__
#define __UNIFINGER_API__

#ifdef _WIN32
#include <windows.h>
#include <winsock.h>
#endif // _WIN32

#include <stdio.h>
#include <time.h>
#include "UF_Def.h"
#include "UF_Error.h"
#include "UF_Packet.h"
#include "UF_Command.h"
#include "UF_SysParameter.h"
#include "UF_Template.h"
#include "UF_Module.h"
#include "UF_Enroll.h"
#include "UF_Image.h"
#include "UF_3500IO.h"
#include "UF_3000IO.h"
#include "UF_UserMemory.h"
#include "UF_Wiegand.h"
#include "UF_SmartCard.h"
#include "UF_Log.h"
#include "UF_Delete.h"
#include "UF_AccessControl.h"
#include "UF_WSQ.h"
#include "UF_Serial.h"
#include "UF_Upgrade.h"
#include "UF_Socket.h"

#ifdef __cplusplus
extern "C"
{
#endif

    //
    // SDK version
    //
    UF_API void UF_GetSDKVersion(int *major, int *minor, int *revision);

    //
    // Initialize serial communication
    //
    UF_API UF_RET_CODE UF_InitCommPort(const char *commPort, int baudrate, BOOL asciiMode);
    UF_API UF_RET_CODE UF_CloseCommPort();
    UF_API UF_RET_CODE UF_InitSocket(const char *inetAddr, int port, BOOL asciiMode);
    UF_API UF_RET_CODE UF_CloseSocket();
    UF_API void UF_Reconnect();
    UF_API UF_RET_CODE UF_SetBaudrate(int baudrate);
    UF_API void UF_SetAsciiMode(BOOL asciiMode);

#ifdef __ANDROID__
    //
    // Callback functions for android 
    //
    
    // Open serial
    //UF_API void UF_SetOpenSerialCallback_Android(BOOL(*Callback)(void));
    // Cloase serial
    //UF_API void UF_SetCloseSerialCallback_Android(void(*Callback)(void));
    // Set baudrate
    UF_API void UF_SetSetupSerialCallback_Android(void(*Callback)(int));
    // Read serial
    UF_API void UF_SetReadSerialCallback_Android(int(*Callback)(BYTE*, int, int));
    // Write serial
    UF_API void UF_SetWriteSerialCallback_Android(int(*Callback)(BYTE*, int, int));
#endif

    //
    // Basic packet interface
    //
    UF_API UF_RET_CODE UF_SendPacket(BYTE command, UINT32 param, UINT32 size, BYTE flag, int timeout);
    UF_API UF_RET_CODE UF_SendNetworkPacket(BYTE command, USHORT terminalID, UINT32 param, UINT32 size, BYTE flag, int timeout);
    UF_API UF_RET_CODE UF_ReceivePacket(BYTE *packet, int timeout);
    UF_API UF_RET_CODE UF_ReceiveNetworkPacket(BYTE *packet, int timeout);
    UF_API UF_RET_CODE UF_SendRawData(BYTE *buf, UINT32 size, int timeout);
    UF_API UF_RET_CODE UF_ReceiveRawData(BYTE *buf, UINT32 size, int timeout, BOOL checkEndCode);
    UF_API UF_RET_CODE UF_SendDataPacket(BYTE command, BYTE *buf, UINT32 dataSize, UINT32 dataPacketSize);
    UF_API UF_RET_CODE UF_ReceiveDataPacket(BYTE command, BYTE *buf, UINT32 dataSize);
    UF_API void UF_SetSendPacketCallback(void (*Callback)(BYTE *));
    UF_API void UF_SetReceivePacketCallback(void (*Callback)(BYTE *));
    UF_API void UF_SetSendDataPacketCallback(void (*Callback)(int index, int numOfPacket));
    UF_API void UF_SetReceiveDataPacketCallback(void (*Callback)(int index, int numOfPacket));
    UF_API void UF_SetSendRawDataCallback(void (*Callback)(int writtenLen, int totalSize));
    UF_API void UF_SetReceiveRawDataCallback(void (*Callback)(int readLen, int totalSize));
    UF_API void UF_SetDefaultPacketSize(int defaultSize);
    UF_API int UF_GetDefaultPacketSize();

    //
    // Generic command interface
    //
    UF_API void UF_SetProtocol(UF_PROTOCOL protocol, UINT32 moduleID);
    UF_API UF_PROTOCOL UF_GetProtocol();
    UF_API UINT32 UF_GetModuleID();
    UF_API void UF_SetGenericCommandTimeout(int timeout);
    UF_API void UF_SetInputCommandTimeout(int timeout);
    UF_API int UF_GetGenericCommandTimeout();
    UF_API int UF_GetInputCommandTimeout();
    UF_API void UF_SetNetworkDelay(int delay);
    UF_API int UF_GetNetworkDelay();
    UF_API UF_RET_CODE UF_Command(BYTE command, UINT32 *param, UINT32 *size, BYTE *flag);
    UF_API UF_RET_CODE UF_CommandEx(BYTE command, UINT32 *param, UINT32 *size, BYTE *flag, BOOL (*msgCallback)(BYTE));
    UF_API UF_RET_CODE UF_CommandSendData(BYTE command, UINT32 *param, UINT32 *size, BYTE *flag, BYTE *data, UINT32 dataSize);
    UF_API UF_RET_CODE UF_CommandSendDataEx(BYTE command, UINT32 *param, UINT32 *size, BYTE *flag, BYTE *data, UINT32 dataSize, BOOL (*msgCallback)(BYTE), BOOL waitUserInput);
    UF_API UF_RET_CODE UF_Cancel(BOOL receivePacket);

    //
    // Module information
    //
    UF_API UF_RET_CODE UF_GetModuleInfo(UF_MODULE_TYPE *type, UF_MODULE_VERSION *version, UF_MODULE_SENSOR *sensorType);
    UF_API char *UF_GetModuleString(UF_MODULE_TYPE type, UF_MODULE_VERSION version, UF_MODULE_SENSOR sensorType);
    UF_API UF_RET_CODE UF_SearchModule(const char *port, int *baudrate, BOOL *asciiMode, UF_PROTOCOL *protocol, UINT32 *moduleID, void (*callback)(const char *comPort, int baudrate));
    UF_API UF_RET_CODE UF_SearchModuleBySocket(const char *inetAddr, int tcpPort, BOOL *asciiMode, UF_PROTOCOL *protocol, UINT32 *moduleID);
    UF_API UF_RET_CODE UF_SearchModuleID(UINT32 *moduleID);
    UF_API UF_RET_CODE UF_SearchModuleIDEx(unsigned short *foundModuleID, int numOfFoundID, unsigned short *moduleID, int *numOfID);
    UF_API UF_RET_CODE UF_CalibrateSensor();
    UF_API UF_RET_CODE UF_Reset();
    UF_API UF_RET_CODE UF_Lock();
    UF_API UF_RET_CODE UF_Unlock(const unsigned char *password);
    UF_API UF_RET_CODE UF_ChangePassword(const unsigned char *newPassword, const unsigned char *oldPassword);
    UF_API UF_RET_CODE UF_ReadChallengeCode(unsigned char *challengeCode);
    UF_API UF_RET_CODE UF_WriteChallengeCode(const unsigned char *challengeCode);
    UF_API UF_RET_CODE UF_PowerOff();

    //
    // System parameters
    //
    UF_API void UF_InitSysParameter();
    UF_API UF_RET_CODE UF_GetSysParameter(UF_SYS_PARAM parameter, UINT32 *value);
    UF_API UF_RET_CODE UF_SetSysParameter(UF_SYS_PARAM parameter, UINT32 value);
    UF_API UF_RET_CODE UF_GetMultiSysParameter(int parameterCount, UF_SYS_PARAM *parameters, UINT32 *values);
    UF_API UF_RET_CODE UF_SetMultiSysParameter(int parameterCount, UF_SYS_PARAM *parameters, UINT32 *values);
    UF_API UF_RET_CODE UF_Save();
    UF_API UF_RET_CODE UF_SaveConfiguration(const char *filename, const char *description, int numOfComponent, UFConfigComponentHeader *componentHeader, void **componentData);
    UF_API UF_RET_CODE UF_ReadConfigurationHeader(const char *filename, UFConfigFileHeader *header);
    UF_API UF_RET_CODE UF_LoadConfiguration(const char *filename, int numOfComponent, UF_CONFIG_TYPE *type);
    UF_API UF_RET_CODE UF_MakeParameterConfiguration(UFConfigComponentHeader *configHeader, BYTE *configData);

    //
    // Template management
    //
    UF_API UF_RET_CODE UF_GetNumOfTemplate(UINT32 *numOfTemplate);
    UF_API UF_RET_CODE UF_GetMaxNumOfTemplate(UINT32 *maxNumOfTemplate);
    UF_API UF_RET_CODE UF_GetAllUserInfo(UFUserInfo *userInfo, UINT32 *numOfUser, UINT32 *numOfTemplate);
    UF_API UF_RET_CODE UF_GetAllUserInfoEx(UFUserInfoEx *userInfo, UINT32 *numOfUser, UINT32 *numOfTemplate);
    UF_API void UF_SortUserInfo(UFUserInfo *userInfo, int numOfUser);
    UF_API void UF_SetUserInfoCallback(void (*callback)(int index, int numOfTemplate));
    UF_API UF_RET_CODE UF_SetAdminLevel(UINT32 userID, UF_ADMIN_LEVEL adminLevel);
    UF_API UF_RET_CODE UF_GetAdminLevel(UINT32 userID, UF_ADMIN_LEVEL *adminLevel);
    UF_API UF_RET_CODE UF_SetSecurityLevel(UINT32 userID, UF_USER_SECURITY_LEVEL securityLevel);
    UF_API UF_RET_CODE UF_GetSecurityLevel(UINT32 userID, UF_USER_SECURITY_LEVEL *securityLevel);
    UF_API UF_RET_CODE UF_ClearAllAdminLevel();
    UF_API UF_RET_CODE UF_SaveDB(const char *fileName);
    UF_API UF_RET_CODE UF_LoadDB(const char *fileName);
    UF_API UF_RET_CODE UF_CheckTemplate(UINT32 userID, UINT32 *numOfTemplate);
    UF_API UF_RET_CODE UF_ReadTemplate(UINT32 userID, UINT32 *numOfTemplate, BYTE *templateData);
    UF_API UF_RET_CODE UF_ReadOneTemplate(UINT32 userID, int subID, BYTE *templateData);
    UF_API void UF_SetScanCallback(void (*Callback)(BYTE));
    UF_API UF_RET_CODE UF_ScanTemplate(BYTE *templateData, UINT32 *templateSize, UINT32 *imageQuality);
    UF_API UF_RET_CODE UF_FixProvisionalTemplate();
    UF_API UF_RET_CODE UF_SetAuthType(UINT32 userID, UF_AUTH_TYPE authType);
    UF_API UF_RET_CODE UF_GetAuthType(UINT32 userID, UF_AUTH_TYPE *authType);
    UF_API UF_RET_CODE UF_GetUserIDByAuthType(UF_AUTH_TYPE authType, int *numOfID, UINT32 *userID);
    UF_API UF_RET_CODE UF_ResetAllAuthType();
    UF_API UF_RET_CODE UF_AddBlacklist(UINT32 userID, int *numOfBlacklistedID);
    UF_API UF_RET_CODE UF_DeleteBlacklist(UINT32 userID, int *numOfBlacklistedID);
    UF_API UF_RET_CODE UF_GetBlacklist(int *numOfBlacklistedID, UINT32 *userID);
    UF_API UF_RET_CODE UF_DeleteAllBlacklist();
    UF_API UF_RET_CODE UF_SetEntranceLimit(UINT32 userID, int entranceLimit);
    UF_API UF_RET_CODE UF_GetEntranceLimit(UINT32 userID, int *entranceLimit, int *entranceCount);
    UF_API UF_RET_CODE UF_ClearAllEntranceLimit();

    //
    // Image
    //
    UF_API UF_RET_CODE UF_SaveImage(const char *fileName, UFImage *image);
    UF_API UF_RET_CODE UF_LoadImage(const char *fileName, UFImage *image);
    UF_API UF_RET_CODE UF_ReadImage(UFImage *image);
    UF_API UF_RET_CODE UF_ScanImage(UFImage *image);
#ifdef _WIN32
    UF_API HBITMAP UF_ConvertToBitmap(UFImage *image);
#endif

    //
    // Identify
    //
    UF_API void UF_SetIdentifyCallback(void (*Callback)(BYTE));
    UF_API UF_RET_CODE UF_Identify(UINT32 *userID, BYTE *subID);
    UF_API UF_RET_CODE UF_IdentifyTemplate(UINT32 templateSize, BYTE *templateData, UINT32 *userID, BYTE *subID);
    UF_API UF_RET_CODE UF_IdentifyImage(UINT32 imageSize, BYTE *imageData, UINT32 *userID, BYTE *subID);

    //
    // Verify
    //
    UF_API void UF_SetVerifyCallback(void (*Callback)(BYTE));
    UF_API UF_RET_CODE UF_Verify(UINT32 userID, BYTE *subID);
    UF_API UF_RET_CODE UF_VerifyTemplate(UINT32 templateSize, BYTE *templateData, UINT32 userID, BYTE *subID);
    UF_API UF_RET_CODE UF_VerifyHostTemplate(UINT32 numOfTemplate, UINT32 templateSize, BYTE *templateData);
    UF_API UF_RET_CODE UF_VerifyImage(UINT32 imageSize, BYTE *imageData, UINT32 userID, BYTE *subID);

    //
    // Enroll
    //
    UF_API void UF_SetEnrollCallback(void (*Callback)(BYTE errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess));
    UF_API UF_RET_CODE UF_Enroll(UINT32 userID, UF_ENROLL_OPTION option, UINT32 *enrollID, UINT32 *imageQuality);
    //UF_API UF_RET_CODE UF_GetEnrollMode();
    UF_API UF_RET_CODE UF_EnrollContinue(UINT32 userID, UINT32 *enrollID, UINT32 *imageQuality);
    UF_API UF_RET_CODE UF_EnrollAfterVerification(UINT32 userID, UF_ENROLL_OPTION option, UINT32 *enrollID, UINT32 *imageQuality);
    UF_API UF_RET_CODE UF_EnrollTemplate(UINT32 userID, UF_ENROLL_OPTION option, UINT32 templateSize, BYTE *templateData, UINT32 *enrollID);
    UF_API UF_RET_CODE UF_EnrollMultipleTemplates(UINT32 userID, UF_ENROLL_OPTION option, int numOfTemplate, UINT32 templateSize, BYTE *templateData, UINT32 *enrollID);
    UF_API UF_RET_CODE UF_EnrollMultipleTemplatesEx(UINT32 userID, UF_ENROLL_OPTION option, int numOfTemplate, int numOfEnroll, UINT32 templateSize, BYTE *templateData, UINT32 *enrollID);
    UF_API UF_RET_CODE UF_EnrollImage(UINT32 userID, UF_ENROLL_OPTION option, UINT32 imageSize, BYTE *imageData, UINT32 *enrollID, UINT32 *imageQuality);

    //
    // Delete
    //
    UF_API void UF_SetDeleteCallback(void (*Callback)(BYTE));
    UF_API UF_RET_CODE UF_Delete(UINT32 userID);
    UF_API UF_RET_CODE UF_DeleteOneTemplate(UINT32 userID, int subID);
    UF_API UF_RET_CODE UF_DeleteMultipleTemplates(UINT32 startUserID, UINT32 lastUserID, int *deletedUserID);
    UF_API UF_RET_CODE UF_DeleteAll();
    UF_API UF_RET_CODE UF_DeleteAllAfterVerification();

    //
    // IO for SFM3500/SFM5500
    //
    UF_API void UF_InitIO();
    UF_API UF_RET_CODE UF_SetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC inputFunction, UINT32 minimumTime);
    UF_API UF_RET_CODE UF_GetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC *inputFunction, UINT32 *minimumTime);
    UF_API UF_RET_CODE UF_GetInputStatus(UF_INPUT_PORT port, BOOL remainStatus, UINT32 *status);
    UF_API UF_RET_CODE UF_GetOutputEventList(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT *events, int *numOfEvent);
    UF_API UF_RET_CODE UF_ClearAllOutputEvent(UF_OUTPUT_PORT port);
    UF_API UF_RET_CODE UF_ClearOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event);
    UF_API UF_RET_CODE UF_SetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal signal);
    UF_API UF_RET_CODE UF_GetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal *signal);
    UF_API UF_RET_CODE UF_SetOutputStatus(UF_OUTPUT_PORT port, BOOL status);
    UF_API UF_RET_CODE UF_SetLegacyWiegandConfig(BOOL enableInput, BOOL enableOutput, UINT32 fcBits, UINT32 fcCode);
    UF_API UF_RET_CODE UF_GetLegacyWiegandConfig(BOOL *enableInput, BOOL *enableOutput, UINT32 *fcBits, UINT32 *fcCode);
    UF_API UF_RET_CODE UF_MakeIOConfiguration(UFConfigComponentHeader *configHeader, BYTE *configData);

    //
    // IO for SFM3000/SFM5000/SFM6000
    //
    UF_API UF_RET_CODE UF_GetGPIOConfiguration(UF_GPIO_PORT port, UF_GPIO_MODE *mode, int *numOfData, UFGPIOData *data);
    UF_API UF_RET_CODE UF_SetInputGPIO(UF_GPIO_PORT port, UFGPIOInputData data);
    UF_API UF_RET_CODE UF_SetOutputGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData *data);
    UF_API UF_RET_CODE UF_SetBuzzerGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData *data);
    UF_API UF_RET_CODE UF_SetSharedGPIO(UF_GPIO_PORT port, UFGPIOInputData inputData, int numOfOutputData, UFGPIOOutputData *outputData);
    UF_API UF_RET_CODE UF_DisableGPIO(UF_GPIO_PORT port);
    UF_API UF_RET_CODE UF_ClearAllGPIO();
    UF_API UF_RET_CODE UF_SetDefaultGPIO();
    UF_API UF_RET_CODE UF_EnableWiegandInput(UFGPIOWiegandData data);
    UF_API UF_RET_CODE UF_EnableWiegandOutput(UFGPIOWiegandData data);
    UF_API UF_RET_CODE UF_DisableWiegandInput();
    UF_API UF_RET_CODE UF_DisableWiegandOutput();
    UF_API UF_RET_CODE UF_MakeGPIOConfiguration(UFConfigComponentHeader *configHeader, BYTE *configData);

    //
    // User memory
    //
    UF_API UF_RET_CODE UF_WriteUserMemory(BYTE *memory);
    UF_API UF_RET_CODE UF_ReadUserMemory(BYTE *memory);

    //
    // Log and time management
    //
    UF_API UF_RET_CODE UF_SetTime(time_t timeVal);
    UF_API UF_RET_CODE UF_GetTime(time_t *timeVal);
    UF_API UF_RET_CODE UF_GetNumOfLog(int *numOfLog, int *numOfTotalLog);
    UF_API UF_RET_CODE UF_ReadLog(int startIndex, int count, UFLogRecord *logRecord, int *readCount);
    UF_API UF_RET_CODE UF_ReadLatestLog(int count, UFLogRecord *logRecord, int *readCount);
    UF_API UF_RET_CODE UF_DeleteOldestLog(int count, int *deletedCount);
    UF_API UF_RET_CODE UF_DeleteAllLog();
    UF_API UF_RET_CODE UF_ClearLogCache();
    UF_API UF_RET_CODE UF_ReadLogCache(int dataPacketSize, int *numOfLog, UFLogRecord *logRecord);
    UF_API UF_RET_CODE UF_SetCustomLogField(UF_LOG_SOURCE source, unsigned customField);
    UF_API UF_RET_CODE UF_GetCustomLogField(UF_LOG_SOURCE source, unsigned *customField);

    //
    // Upgrade
    //
    UF_API UF_RET_CODE UF_Upgrade(const char *firmwareFilename, int dataPacketSize);
    UF_API UF_RET_CODE UF_UpgradeEx(const char *firmwareFilename, UF_UPGRADE_OPTION option, int dataPacketSize);
    UF_API UF_RET_CODE UF_DFU_Upgrade();

    //
    // FileSystem (in UF_Upgrade.c)
    //
    UF_API UF_RET_CODE UF_FormatUserDatabase();
    UF_API UF_RET_CODE UF_ResetSystemConfiguration();

    //
    // Extended wiegand
    //
    UF_API UF_RET_CODE UF_SetWiegandFormat(UFWiegandFormatHeader *header, UFWiegandFormatData *data, int pulseWidth, int pulseInterval);
    UF_API UF_RET_CODE UF_GetWiegandFormat(UFWiegandFormatHeader *header, UFWiegandFormatData *data, int *pulseWidth, int *pulseInterval);
    UF_API UF_RET_CODE UF_SetWiegandIO(UF_WIEGAND_INPUT_MODE inputMode, UF_WIEGAND_OUTPUT_MODE outputMode, int numOfChar);
    UF_API UF_RET_CODE UF_GetWiegandIO(UF_WIEGAND_INPUT_MODE *inputMode, UF_WIEGAND_OUTPUT_MODE *outputMode, int *numOfChar);
    UF_API UF_RET_CODE UF_SetWiegandOption(BOOL useFailID, UINT32 failID, BOOL inverseParityOnFail);
    UF_API UF_RET_CODE UF_GetWiegandOption(BOOL *useFailID, UINT32 *failID, BOOL *inverseParityOnFail);
    UF_API UF_RET_CODE UF_SetAltValue(int fieldIndex, UINT32 value);
    UF_API UF_RET_CODE UF_ClearAltValue(int fieldIndex);
    UF_API UF_RET_CODE UF_GetAltValue(int fieldIndex, UINT32 *value);
    UF_API UF_RET_CODE UF_MakeWiegandConfiguration(UFConfigComponentHeader *configHeader, BYTE *configData);

    //
    // Wiegand command card
    //
    UF_API UF_RET_CODE UF_AddWiegandCommandCard(UINT32 userID, UF_INPUT_FUNC function);
    UF_API UF_RET_CODE UF_GetWiegandCommandCardList(int *numOfCard, UFWiegandCommandCard *commandCard);
    UF_API UF_RET_CODE UF_ClearAllWiegandCommandCard();

    //
    // Smart Card
    //
    UF_API void UF_SetSmartCardCallback(void (*Callback)(BYTE));
    UF_API UF_RET_CODE UF_ReadSmartCard(UFCardHeader *header, BYTE *template1, BYTE *template2);
    UF_API UF_RET_CODE UF_ReadSmartCardWithAG(UFCardHeader *header, BYTE *template1, BYTE *template2, int *numOfAccessGroup, BYTE *accessGroup);
    UF_API UF_RET_CODE UF_WriteSmartCard(UINT32 userID, UF_CARD_SECURITY_LEVEL securityLevel, int numOfTemplate, int templateSize, BYTE *template1, BOOL duress1, BYTE *template2, BOOL duress2);
    UF_API UF_RET_CODE UF_WriteSmartCardWithAG(UINT32 userID, UF_CARD_SECURITY_LEVEL securityLevel, int numOfTemplate, int templateSize, BYTE *template1, BOOL duress1, BYTE *template2, BOOL duress2, int numOfAccessGroup, BYTE *accessGroup);
    UF_API UF_RET_CODE UF_WriteSmartCardWithEntranceLimit(UINT32 userID, UF_CARD_SECURITY_LEVEL securityLevel, int entranceLimit, int numOfTemplate, int templateSize, BYTE *template1, BOOL duress1, BYTE *template2, BOOL duress2, int numOfAccessGroup, BYTE *accessGroup);
    UF_API UF_RET_CODE UF_FormatSmartCard(BOOL templateOnly);
    UF_API UF_RET_CODE UF_SetSmartCardMode(UF_CARD_MODE mode);
    UF_API UF_RET_CODE UF_GetSmartCardMode(UF_CARD_MODE *mode);
    UF_API UF_RET_CODE UF_ChangePrimaryKey(BYTE *oldPrimaryKey, BYTE *newPrimaryKey);
    UF_API UF_RET_CODE UF_ChangeSecondaryKey(BYTE *primaryKey, BYTE *newSecondaryKey);
    UF_API UF_RET_CODE UF_SetKeyOption(BYTE *primaryKey, BOOL useSecondaryKey, BOOL autoUpdate);
    UF_API UF_RET_CODE UF_GetKeyOption(BOOL *useSecondaryKey, BOOL *autoUpdate);
    UF_API UF_RET_CODE UF_SetCardLayout(UFCardLayout *layout);
    UF_API UF_RET_CODE UF_GetCardLayout(UFCardLayout *layout);

    //
    // Access Control
    //
    UF_API UF_RET_CODE UF_AddTimeSchedule(UFTimeSchedule *schedule);
    UF_API UF_RET_CODE UF_GetTimeSchedule(int ID, UFTimeSchedule *schedule);
    UF_API UF_RET_CODE UF_DeleteTimeSchedule(int ID);
    UF_API UF_RET_CODE UF_DeleteAllTimeSchedule();
    UF_API UF_RET_CODE UF_AddHoliday(UFHoliday *holiday);
    UF_API UF_RET_CODE UF_GetHoliday(int ID, UFHoliday *holiday);
    UF_API UF_RET_CODE UF_DeleteHoliday(int ID);
    UF_API UF_RET_CODE UF_DeleteAllHoliday();
    UF_API UF_RET_CODE UF_AddAccessGroup(UFAccessGroup *group);
    UF_API UF_RET_CODE UF_GetAccessGroup(int ID, UFAccessGroup *group);
    UF_API UF_RET_CODE UF_DeleteAccessGroup(int ID);
    UF_API UF_RET_CODE UF_DeleteAllAccessGroup();
    UF_API UF_RET_CODE UF_SetUserAccessGroup(UINT32 userID, int numOfGroup, int *groupID);
    UF_API UF_RET_CODE UF_GetUserAccessGroup(UINT32 userID, int *numOfGroup, int *groupID);

    //
    // WSQ decoding
    //
    UF_API UF_RET_CODE UF_WSQ_Decode(unsigned char **odata, int *ow, int *oh, int *od, int *oppi, int *lossyflag, unsigned char *idata, const int ilen);
    UF_API UF_RET_CODE UF_ReadImageEx(UFImage *image, UF_IMAGE_TYPE type, int wsqBitRate);
    UF_API UF_RET_CODE UF_ScanImageEx(UFImage *image, UF_IMAGE_TYPE type, int wsqBitRate);

#ifdef __cplusplus
}
#endif

#endif
