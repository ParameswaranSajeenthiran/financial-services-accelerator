#!/bin/bash

#
# Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#

set -o xtrace

HOME=`pwd`
TEST_SCRIPT=test.sh
MVNSTATE=1 #This variable is read by the test-grid to determine success or failure of the build. (0=Successful)

# Test configurations
SOLUTION_VERSION="3.0.0"
APIM_VERSION="4.2.0"
API_VERSION="3.1.10"
VRP_API_VERSION="3.1.9"
DCR_API_VERSION="3.3.0"

function usage()
{
    echo "
    Usage bash test.sh --input-dir /workspace/data-bucket.....
    Following are the expected input parameters. all of these are optional
    --input-dir       | -i    : input directory for test.sh
    --output-dir      | -o    : output directory for test.sh
    "
}

#=== FUNCTION ==================================================================
# NAME: get_prop
# DESCRIPTION: Retrieve specific property from deployment.properties file
# PARAMETER 1: property_value
#===============================================================================
function get_prop {
    local prop=$(grep -w "${1}" "${INPUT_DIR}/deployment.properties" | cut -d'=' -f2)
    echo $prop
}

optspec=":hiom-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                input-dir)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    INPUT_DIR=$val
                    ;;
                output-dir)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    OUTPUT_DIR=$val
                    ;;
                mvn-opts)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    MAVEN_OPTS=$val
                    ;;
                *)
                    usage
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                    fi
                    ;;
            esac;;
        h)
            usage
            exit 2
            ;;
        o)
            OUTPUT_DIR=$val
            ;;
        m)
            MVN_OPTS=$val
            ;;
        i)
            INPUT_DIR=$val
            ;;
        *)
            usage
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

export DATA_BUCKET_LOCATION=${INPUT_DIR}

cat ${INPUT_DIR}/deployment.properties

cd ../../
PROJECT_HOME=`pwd`
APIS_HOME=${PROJECT_HOME}/toolkits/ob-apim/repository/resources/apis/openbanking.org.uk

echo "--- Go to open-banking-test-suite folder---"
cd open-banking-test-suite
SOURCE_HOME=`pwd`
TEST_FRAMEWORK_HOME=${SOURCE_HOME}/open-banking-test-framework
UK_TOOL_KIT_TESTS_HOME=${SOURCE_HOME}/toolkit-uk-test
UK_TOOL_KIT_TEST_CONFIG=${UK_TOOL_KIT_TESTS_HOME}/resources/test-config.xml
TEST_ARTIFACTS=${SOURCE_HOME}/test-artifacts

#--------------Set configs for tool kit test-config.xml-----------------#
cp ${UK_TOOL_KIT_TESTS_HOME}/resources/sample-test-config.xml ${UK_TOOL_KIT_TEST_CONFIG}

#-------------Solution Configurations----------------#
sed -i -e "s|APIM.Version|${APIM_VERSION}|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|Api.Version|${API_VERSION}|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|{TestSuiteDirectoryPath}|${UK_TOOL_KIT_TESTS_HOME}|g" $UK_TOOL_KIT_TEST_CONFIG

#--------------Server Configurations-----------------#
sed -i -e "s|{IS_HOST}|$(get_prop "IsHostname")|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|{AM_HOST}|$(get_prop "ApimHostname")|g" $UK_TOOL_KIT_TEST_CONFIG

#--------------Application Configurations-----------------#
sed -i -e "s|{TestArtifactDirectoryPath}|${TEST_ARTIFACTS}|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|AppConfig.Application.ClientID|Application.ClientID|g" $UK_TOOL_KIT_TEST_CONFIG

#--------------Application Configurations App 2-----------------#
sed -i -e "s|AppConfig2.Application.ClientID|Application.ClientID|g" $UK_TOOL_KIT_TEST_CONFIG

#--------------Browser Automation Configurations-----------------#
sed -i -e "s|BrowserAutomation.BrowserPreference|firefox|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|BrowserAutomation.HeadlessEnabled|true|g" $UK_TOOL_KIT_TEST_CONFIG

#--------------Database configurations-----------------------------------------#
sed -i -e "s|DataBaseConfiguration.DBType|mysql|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|DataBaseConfiguration.DBServerHost|localhost:3306|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|DataBaseConfiguration.DBUsername|dbuser|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|DataBaseConfiguration.DBPassword|dbuser1234|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|DataBaseConfiguration.DBDriverClass|com.mysql.jdbc.Driver|g" $UK_TOOL_KIT_TEST_CONFIG
sed -i -e "s|DataBaseConfiguration.OracleDBSID|driver|g" $UK_TOOL_KIT_TEST_CONFIG

#----------------Set hostnames for sequences -----------#
#__replace hostname before deploy
sed -i -e "s|localhost:9446|$(get_prop "IsHostname"):9446|g" ${APIS_HOME}/Accounts/${API_VERSION}/accounts-dynamic-endpoint-insequence-${API_VERSION}.xml
sed -i -e "s|localhost:9446|$(get_prop "IsHostname"):9446|g" ${APIS_HOME}/FundsConfirmation/${API_VERSION}/funds-confirmation-dynamic-endpoint-insequence-${API_VERSION}.xml
sed -i -e "s|localhost:9446|$(get_prop "IsHostname"):9446|g" ${APIS_HOME}/Payments/${API_VERSION}/payments-dynamic-endpoint-insequence-${API_VERSION}.xml
sed -i -e "s|localhost:9446|$(get_prop "IsHostname"):9446|g" ${APIS_HOME}/VRP/${VRP_API_VERSION}/vrp-dynamic-endpoint-insequence-${VRP_API_VERSION}.xml
sed -i -e "s|localhost:9446|$(get_prop "IsHostname"):9446|g" ${APIS_HOME}/DynamicClientRegistration/${DCR_API_VERSION}/dcr-dynamic-endpoint-insequence-${DCR_API_VERSION}.xml


#----------------Install geckodriver------------------------#
export DEBIAN_FRONTEND=noninteractive
wget https://github.com/mozilla/geckodriver/releases/download/v0.29.1/geckodriver-v0.29.1-linux64.tar.gz
tar xvzf geckodriver*
cp geckodriver ${TEST_ARTIFACTS}/selenium-libs/
chmod +x ${TEST_ARTIFACTS}/selenium-libs/geckodriver

#--------------Build the test framework-----------------#
mvn clean install -Dprofile=open-banking-test-suite -Dmaven.test.skip=true -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

#--------------API Publish and Subscribe Step-----------------#
cd ${UK_TOOL_KIT_TESTS_HOME}/uk.integration.test.common
mvn clean install -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
MVNSTATE=$?

#=== FUNCTION ==================================================================
# NAME: get_test_prop
# DESCRIPTION: Retrieve specific property from test.properties file
# PARAMETER 1: property_value
#===============================================================================
function get_test_prop {
    local prop=$(grep -w "${1}" "target/test.properties" | cut -d'=' -f2)
    echo $prop
}

#--------------run UK tests-----------------#
cd ${UK_TOOL_KIT_TESTS_HOME}/com.wso2.openbanking.toolkit.uk.test/integration.tests/accounts
mvn clean install -DgroupToRun=$API_VERSION -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f pom.xml
MVNSTATE=$((MVNSTATE+$?))
mkdir -p ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/accounts-$API_VERSION
find . -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/accounts-$API_VERSION \;

cd ${UK_TOOL_KIT_TESTS_HOME}/com.wso2.openbanking.toolkit.uk.test/integration.tests/cof
mvn clean install -DgroupToRun=$API_VERSION -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f pom.xml
MVNSTATE=$((MVNSTATE+$?))
mkdir -p ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/cof-$API_VERSION
find . -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/cof-$API_VERSION \;

cd ${UK_TOOL_KIT_TESTS_HOME}/com.wso2.openbanking.toolkit.uk.test/integration.tests/payments
mvn clean install -DgroupToRun=$API_VERSION -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f pom.xml
MVNSTATE=$((MVNSTATE+$?))
mkdir -p ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/payments-$API_VERSION
find . -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/payments-$API_VERSION \;

cd ${UK_TOOL_KIT_TESTS_HOME}/com.wso2.openbanking.toolkit.uk.test/integration.tests/vrp
mvn clean install -DgroupToRun=$VRP_API_VERSION -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f pom.xml
MVNSTATE=$((MVNSTATE+$?))
mkdir -p ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/vrp-$VRP_API_VERSION
find . -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/vrp-$VRP_API_VERSION \;

cd ${UK_TOOL_KIT_TESTS_HOME}/com.wso2.openbanking.toolkit.uk.test/integration.tests/dcr
mvn clean install -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f pom.xml
MVNSTATE=$((MVNSTATE+$?))
mkdir -p ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/dcr
find . -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios/com.wso2.openbanking.toolkit.uk.test/integration.tests/dcr \;