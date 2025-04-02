#!/bin/bash
# Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
#
# WSO2 LLC. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

MVNSTATE=1 #This variable is read by the test-grid to determine success or failure of the build. (0=Successful)
RUNNER_HOME=`pwd`


#=== FUNCTION ==================================================================
# NAME: get_prop
# DESCRIPTION: Retrieve specific property from deployment.properties.sample
# PARAMETER 1: property_value
#===============================================================================
function get_prop {
    local prop=$(grep -w "${1}" "${RUNNER_HOME}/deployment.properties" | cut -d'=' -f2)
    echo $prop
}

while getopts u:p:o:h flag
do
    case "${flag}" in
        u) USERNAME=${OPTARG};;
        p) PASSWORD=${OPTARG};;
        o) TEST_HOME=${OPTARG};;
        h) INPUT_DIR=${OPTARG};;
    esac
done

# ====== variables ======
# Username and Password for WSO2 Updates
# TEST_HOME : Folder to install IS server

echo "Username: $USERNAME"
echo "Password: $PASSWORD"
echo "TEST_HOME:  $TEST_HOME"



echo ' Building packs ======================='

#mvn -B install --file pom.xml
#
echo '======================= SetUp base Products ======================='

# Create the test home directory if it doesn't exist
if [ ! -d "$TEST_HOME" ]; then
    mkdir -p $TEST_HOME
fi
#wget "https://filebin.net/ezmc7r5vlk4al2t9/wso2is-7.0.0.zip" -O $TEST_HOME/wso2is-7.0.0.zip
unzip $TEST_HOME/wso2is-7.0.0.zip -d $TEST_HOME/

echo '======================= Installing WSO2 Updates ======================='
name=$(echo "$USERNAME" | cut -d'@' -f1)
WSO2_UPDATES_HOME=home/$name/.wso2updates
sudo mkdir -p /home/$name/.wso2-updates/docker && sudo chmod -R 777 /home/$name/.wso2-updates

$TEST_HOME/wso2is-7.0.0/bin/wso2update_linux --username $USERNAME --password $PASSWORD ||  ($TEST_HOME/wso2is-7.0.0/bin/wso2update_linux --username $USERNAME --password $PASSWORD )
#
echo '======================= Moving Packs to IS_HOME ======================='
unzip ../financial-services-accelerator/accelerators/fs-is/target/wso2-fsiam-accelerator-4.0.0-M3.zip -d $TEST_HOME/wso2is-7.0.0/
#wget https://github.com/ParameswaranSajeenthiran/files/raw/refs/heads/master/wso2-fsiam-accelerator-4.0.0-M3.zip -O wso2-fsiam-accelerator-4.0.0-M3.zip
#unzip wso2-fsiam-accelerator-4.0.0-M3.zip -d $TEST_HOME/wso2is-7.0.0/


echo '======================= configure.properties ======================='
# delete the existing configure.properties
rm -f $TEST_HOME/wso2is-7.0.0/wso2-fsiam-accelerator-4.0.0-M3/repository/conf/configure.properties
# copy the new configure.properties
cp $RUNNER_HOME/is-accelerator/configure.properties $TEST_HOME/wso2is-7.0.0/wso2-fsiam-accelerator-4.0.0-M3/repository/conf


echo '======================= Setup MYSQL ======================='
sudo apt-get update
sudo apt-get install -y mysql-server
sudo systemctl start mysql
mysql --version

echo '======================= Download and install Drivers ======================='
wget -q https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar
mv mysql-connector-j-9.2.0.jar $TEST_HOME/wso2is-7.0.0/repository/components/lib

echo '======================= Generate and Export Certificates ======================='
storepass=wso2carbon
declare -A servers
servers["wso2"]="$TEST_HOME/wso2is-7.0.0/repository/resources/security/wso2carbon.jks"

cert_dir="$TEST_HOME/certs"
mkdir -p $cert_dir

for alias in "${!servers[@]}"; do
  keystore="${servers[$alias]}"

  echo "removing old key pair if exists"
  # Remove old key pair if exists
  keytool -delete -alias wso2carbon -keystore $keystore -storepass wso2carbon

  echo "generating new key pair"

  # Generate new key pair
  keytool -genkey -alias wso2carbon -keystore $keystore -keysize 2048 -keyalg RSA -validity 9999 -dname   "CN=obiam, O=OB, L=WSO2, S=COL, C=LK, OU=OB" -ext san=ip:127.0.0.1,dns:localhost,dns:$alias -keypass  wso2carbon -storepass wso2carbon

  echo "exporting public certificate"
  # Export public certificate
  keytool -export -alias wso2carbon -keystore $keystore -file $cert_dir/$alias.pem -storepass wso2carbon
done

echo '======================= Import Certificates into the truststore ======================='

aliases=("wso2")

# Define the client truststore
truststores=(
  "$TEST_HOME/wso2is-7.0.0/repository/resources/security/client-truststore.jks"
)
# Import certificates into truststores
for alias in "${aliases[@]}"; do
  cert="$cert_dir/$alias.pem"
  for truststore in "${truststores[@]}"; do
    echo "Importing certificate for alias '$alias' into truststore: $truststore"
    keytool -import -alias $alias -file $cert_dir/$alias.pem -keystore $truststore -storepass wso2carbon -keypass  wso2carbon -noprompt
  done
done

echo '======================= Verify Exchanged Certificates ======================='

# Function to check if alias exists in the truststore
check_alias() {
  local truststore=$1
  local alias=$2
  echo "Checking alias '$alias' in truststore: $truststore"

  keytool -list -keystore $truststore -storepass "$storepass" -alias $alias
  if [ $? -eq 0 ]; then
    echo "[✔] Alias '$alias' found in truststore: $truststore"
  else
    echo "[✘] Alias '$alias' NOT found in truststore: $truststore"
    exit 1  # Fail the workflow if a certificate is missing
  fi
}

# Function to display certificate details
show_certificate_details() {
  local truststore=$1
  local alias=$2

  echo "-------------------------------"
  echo "Details for alias '$alias' in truststore: $truststore"
  keytool -list -v -keystore "$truststore" -storepass "$storepass" -alias "$alias" | grep -E "Alias|Valid from|Issuer|Subject"
  echo "-------------------------------"
}

# Verify imported certificates
for truststore in "${truststores[@]}"; do
  echo "Checking truststore: $truststore"



  for alias in "${aliases[@]}"; do
    check_alias "$truststore" "$alias"
    show_certificate_details "$truststore" "$alias"
  done
done

echo '======================= Import OB sandbox Root and Issuer Certificates ======================='

wget 'https://github.com/ParameswaranSajeenthiran/files/raw/refs/heads/master/OB_SandBox_PP_Root%20CA.cer' -O "${TEST_HOME}/OB_SandBox_PP_Root CA.cer"
keytool -import -alias root -file "${TEST_HOME}/OB_SandBox_PP_Root CA.cer" -keystore "${TEST_HOME}/wso2is-7.0.0/repository/resources/security/client-truststore.jks" -storepass wso2carbon -noprompt


wget 'https://github.com/ParameswaranSajeenthiran/files/raw/refs/heads/master/OB_SandBox_PP_Issuing%20CA.cer' -O "${TEST_HOME}/OB_SandBox_PP_Issuing CA.cer"
keytool -import -alias issuer -file "${TEST_HOME}/OB_SandBox_PP_Issuing CA.cer" -keystore "${TEST_HOME}/wso2is-7.0.0/repository/resources/security/client-truststore.jks" -storepass wso2carbon -noprompt



echo '======================= Run merge and Config scripts ======================='
cd $TEST_HOME/wso2is-7.0.0/wso2-fsiam-accelerator-4.0.0-M3/bin
bash merge.sh
bash configure.sh

echo '======================= Update deployment.toml ======================='

# delete the existing deployment.toml
rm -f $TEST_HOME/wso2is-7.0.0/repository/conf/deployment.toml
# copy the new deployment.toml
cp p $RUNNER_HOME/is-accelerator/deployment.toml $TEST_HOME/wso2is-7.0.0/repository/conf

cd $TEST_HOME/wso2is-7.0.0/bin
#nohup ./wso2server.sh > ${RUNNER_HOME}/wso2.log 2>&1 &
#cat ${RUNNER_HOME}/wso2.log
./wso2server.sh