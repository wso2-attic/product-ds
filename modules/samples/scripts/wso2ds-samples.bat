@echo off

REM ---------------------------------------------------------------------------
REM        Copyright 2005-2009 WSO2, Inc. http://www.wso2.org
REM
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.

rem ---------------------------------------------------------------------------
rem Main Script for WSO2 DS samples
rem
rem Environment Variable Prequisites
rem
rem   CARBON_HOME     Home of CARBON installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem
rem NOTE: Borrowed generously from apache Tomcat startup script
rem ---------------------------------------------------------------------------

SET script=%~dp0

rem Defining Sample data and directories' paths
set gadgetDropLocation=%script%..\repository\deployment\server\jaggeryapps\portal\store\carbon.super\gadget
set dashboardDropLocation=%script%..\repository\deployment\server\jaggeryapps\portal\extensions\dashboards

set cn=%1
set UserInputValue=%2

if "%cn%"=="-sn" goto sample
if "%cn%"=="sn" goto sample
if "%cn%"=="" (
	echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
	echo "Example, to run sample 0: wso2ds-samples.bat -sn 0"
	goto exitWithoutClosing
)

:sample
if "%UserInputValue%"=="" goto invalidArgument
if %UserInputValue% GEQ 0 goto validArgument

:invalidArgument
echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
echo "Example, to run sample 0: wso2ds-samples.bat -sn 0"
goto exitWithoutClosing

:validArgument
set sampleFolder=%script%..\samples\s%UserInputValue%

xcopy "%sampleFolder%\gadget\*" "%gadgetDropLocation%" /e /i /h /Y
xcopy "%sampleFolder%\dashboards\*" "%dashboardDropLocation%" /Y

echo "Starting the dashboard server with sample dashboard"
wso2server.bat
:exitWithoutClosing