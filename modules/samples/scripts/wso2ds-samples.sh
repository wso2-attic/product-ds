#!/bin/sh

#  Copyright 2016 The Apache Software Foundation
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

# ----------------------------------------------------------------------------
# Script for running the WSO2 DS samples
#
# Environment Variable Perquisites
#
#   CARBON_HOME   Home of WSO2 Carbon installation. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

# Get application Home path.

PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set pathEdited
pathEdited=`cd "$PRGDIR/.." ; pwd`

binPath=$pathEdited/bin

# Defining Sample data and directories' paths
gadgetDropLocation="$pathEdited/repository/deployment/server/jaggeryapps/portal/store/carbon.super/"
dashboardDropLocation="$pathEdited/repository/deployment/server/jaggeryapps/portal/extensions/"
jaggeryAppsDropLocation="$pathEdited/repository/deployment/server/"

# Process the user input
sample=""
validate=""
sampleFolder=""
for c in $*
do
    if [ "$c" = "-sn" ] || [ "$c" = "sn" ]; then
        sample="t"
        validate="t"
        continue
    elif [ "$sample" = "t" ]; then
        noDigits="$(echo $c | sed 's/[[:digit:]]//g')"
        if [ -z $noDigits ]; then
            sample=""
            sampleFolder="$pathEdited/samples/s$c"
        else
           echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
           echo "Example, to run sample 0: wso2ds-samples.sh -sn 0"
           exit
        fi
    else
        sampleFolder="$pathEdited/samples/s$c"
    fi
done

if [ -z $validate ]; then
  echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
  echo "Example, to run sample 0: wso2ds-samples.sh -sn 0"
  exit
fi

# Coping the sample files
cp -r "$sampleFolder/gadget" "$gadgetDropLocation"
cp -r "$sampleFolder/dashboards" "$dashboardDropLocation"
[ -d "$sampleFolder/jaggeryapps" ] && cp -r "$sampleFolder/jaggeryapps" "$jaggeryAppsDropLocation"

echo "Starting the dashboard server with sample dashboard"
sh "$binPath/wso2server.sh"

