/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.financial.services.accelerator.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.financial.services.accelerator.common.constant.FinancialServicesConstants;
import org.wso2.financial.services.accelerator.common.exception.FinancialServicesException;
import org.wso2.financial.services.accelerator.common.exception.FinancialServicesRuntimeException;
import org.wso2.financial.services.accelerator.common.exception.OAuth2ServiceException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * Financial Services common utility class.
 */
public class FinancialServicesUtils {

    private static final Log log = LogFactory.getLog(FinancialServicesUtils.class);

    /**
     * Get Tenant Domain String for the client id.
     *
     * @param clientId the client id of the application
     * @return tenant domain of the client
     * @throws FinancialServicesException if an error occurs while retrieving the
     *                                    tenant domain
     */
    @Generated(message = "Ignoring because OAuth2Util cannot be mocked with no constructors")
    public static String getSpTenantDomain(String clientId) throws FinancialServicesException {

        try {
            return OAuth2Util.getTenantDomainOfOauthApp(clientId);
        } catch (InvalidOAuthClientException | IdentityOAuth2Exception e) {
            throw new FinancialServicesException("Error retrieving service provider tenant domain for client_id: "
                    + clientId, e);
        }
    }

    /**
     * Method to obtain the Object when the full class path is given.
     *
     * @param classpath full class path
     * @return new object instance
     */
    @Generated(message = "Ignoring since method contains no logics")
    public static Object getClassInstanceFromFQN(String classpath) {

        try {
            return Class.forName(classpath).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Class not found: " + classpath.replaceAll("[\r\n]", ""));
            throw new FinancialServicesRuntimeException("Cannot find the defined class", e);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException
                 | IllegalAccessException e) {
            // Throwing a runtime exception since we cannot proceed with invalid objects
            throw new FinancialServicesRuntimeException("Defined class" + classpath + "cannot be instantiated.", e);
        }
    }

    /**
     * Method to reduce string length.
     *
     * @param input     Input for dispute data
     * @param maxLength Max length for dispute data
     * @return String with reduced length
     */
    public static String reduceStringLength(String input, int maxLength) {
        if (StringUtils.isEmpty(input) || input.length() <= maxLength) {
            return input;
        } else {
            return input.substring(0, maxLength);
        }
    }

    /**
     * Check whether the client ID belongs to a regulatory app.
     *
     * @param clientId client ID
     * @return true if the client ID belongs to a regulatory app
     * @throws OAuth2ServiceException If an error occurs while using OAuth2 Rest API
     */
    @Generated(message = "Excluding from code coverage since it requires a service call")
    public static boolean isRegulatoryApp(String clientId) throws OAuth2ServiceException {
        try {
            return AsgardeoUtils.isFAPIApplication(clientId, FinancialServicesConstants.TENANT_DOMAIN);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to resolve username from user ID.
     *
     * @param userID   User ID
     * @return Username
     */
    @Generated(message = "Ignoring because OAuth2Util cannot be mocked with no constructors")
    public static String resolveUsernameFromUserId(String userID) {

        if (!startsWithUUID(userID)) {
            // If the user ID is not starting with a UUID that means request has sent the username,
            // return the same user ID as the username.
            return userID;
        }

        String username = null;
        try {
            if (userID.contains(FinancialServicesConstants.TENANT_DOMAIN)) {

                username = AsgardeoUtils.getUserNameByUserId(userID.split("@" +
                                FinancialServicesConstants.TENANT_DOMAIN)[0],
                        FinancialServicesConstants.TENANT_DOMAIN);

            } else {
                username =  AsgardeoUtils.getUserNameByUserId(FinancialServicesConstants.TENANT_DOMAIN, userID);
            }
        } catch (UnsupportedEncodingException e) {
            log.debug("Error occurred while resolving username from user ID", e);
        } catch (OAuth2ServiceException e) {
            log.debug("Error occurred while resolving username from user ID", e);
            return null;

        }
        return username;
    }

    /**
     * Method to check whether the input string starts with a UUID.
     * @param input Input string
     * @return  true if the input string starts with a UUID
     */
    public static boolean startsWithUUID(String input) {
        Pattern uuidPattern = Pattern.compile("^" + FinancialServicesConstants.UUID_REGEX + ".*$");
        return uuidPattern.matcher(input).matches();
    }
}
