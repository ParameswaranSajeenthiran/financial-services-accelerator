package org.wso2.financial.services.accelerator.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.financial.services.accelerator.common.constant.OAuthConstant;
import org.wso2.financial.services.accelerator.common.exception.OAuth2ServiceException;
import org.wso2.financial.services.accelerator.common.services.OAuth2Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;

import static org.wso2.financial.services.accelerator.common.constant.AsgardeoConstant.ASGARDEO_URI;

/**
 * AsgardeoUtils.
 * This class is used to implement IS functionality of Asgardeo.
 */
public class AsgardeoUtils {

    private static AsgardeoUtils asgardeoUtils = null;
    private static Instant expiryTime; // Store expiration time
    /**
     * API management APP
     **/
    private static String accessToken = null;
    private static final String clientId = "D3nfKNP70fOCjF60djhC8XgYLGAa";
    private static final String clientSecret = "VcC0SDj6pbb8aj1YcaMMf9rw_KYix4SxUcCK2WaKufIa";
    private static final String SCOPE = "internal_org_application_mgt_update internal_org_application_mgt_view " +
            "internal_dcr_create internal_dcr_delete internal_dcr_view internal_dcr_update " +
            "internal_organization_create internal_organization_view internal_organization_update " +
            "internal_organization_delete internal_user_mgt_create internal_user_mgt_list" +
            " internal_user_mgt_view internal_user_mgt_delete internal_user_mgt_update " +
            "internal_user_association_delete" +
            " internal_user_association_view";


    private static final Log log = LogFactory.getLog(AsgardeoUtils.class);

    // Check if Token is Expired
    private static boolean isTokenExpired() {
        return expiryTime == null || Instant.now().isAfter(expiryTime);
    }

    private static void refreshAccessToken() throws OAuth2ServiceException, UnsupportedEncodingException {
        OAuth2Service oAuth2Service = OAuth2Service.getInstance();
        accessToken = oAuth2Service.getAccessTokenByClientCredentialsGrantType(
                ASGARDEO_URI + "/sajeenthiran2", clientId, clientSecret, SCOPE);
        expiryTime = Instant.now().plusSeconds(3600); // Token valid for 1 hour
    }

    /**
     * retrieve username by userId
     *
     * @param userId
     * @param tenant
     * @return String
     * @throws OAuth2ServiceException
     * @throws UnsupportedEncodingException
     */

    public static String getUserNameByUserId(String userId, String tenant) throws
            OAuth2ServiceException, UnsupportedEncodingException {
        String accessToken = getAccessToken();

        String url = ASGARDEO_URI + "/" + tenant + "/scim2/Users/" + userId;
        // get request to get the application details
        HttpGet httpGet = new HttpGet(url);


        httpGet.setHeader(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + accessToken);
        httpGet.setHeader(OAuthConstant.CONTENT_TYPE, OAuthConstant.APPLICATION_JSON);
        httpGet.setHeader(OAuthConstant.ACCEPT, OAuthConstant.APPLICATION_JSON);

        // send the request
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getCode() != HttpStatus.SC_OK) {
                throw new OAuth2ServiceException("Error occurred while retrieving the application details. " +
                        "Status code: " + response.getCode());
            }
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            return responseJson.get("userName").toString();
        } catch (IOException | ParseException | JSONException e) {
            throw new OAuth2ServiceException("Error occurred while retrieving the application details.", e);
        }

    }

    /**
     * This method is used to retrieve isFAPIconformantApp.
     *
     * @param clientId
     * @param tenant
     * @return boolean
     * @throws OAuth2ServiceException
     * @throws UnsupportedEncodingException
     */
    public static boolean isFAPIApplication(String clientId, String tenant) throws OAuth2ServiceException,
            UnsupportedEncodingException {
        JSONObject appDetails = getAppIdByClientId(clientId, tenant);
        String appId = appDetails.getString("id");
        JSONObject appDetailsByAppId = getAppDetailsByAppId(appId, tenant);
        return appDetailsByAppId.getBoolean("isFAPIApplication");
    }

    /**
     * get clientSecret by clientId
     *
     * @param clientId
     * @param tenant
     * @return String
     * @throws OAuth2ServiceException
     */
    public static String getClientSecretByClientId(String clientId, String tenant) throws UnsupportedEncodingException,
            OAuth2ServiceException {
        String accessToken = getAccessToken();

        String url = ASGARDEO_URI + "/" + tenant + "/api/identity/oauth2/dcr/v1.1/register/" + clientId;

        // get request to get the application details
        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + accessToken);
        httpGet.setHeader(OAuthConstant.CONTENT_TYPE, OAuthConstant.APPLICATION_JSON);
        httpGet.setHeader(OAuthConstant.ACCEPT, OAuthConstant.APPLICATION_JSON);

        // send the request
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getCode() != HttpStatus.SC_OK) {
                throw new OAuth2ServiceException("Error occurred while retrieving the application details." +
                        " Status code: " + response.getCode());
            }
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            return responseJson.get("client_secret").toString();
        } catch (IOException | ParseException | JSONException e) {
            throw new OAuth2ServiceException("Error occurred while retrieving the application details.", e);
        }

    }

    /**
     * This method is used to retrieve the application details by client id.
     */
    public static JSONObject getAppIdByClientId(String clientId, String tenant) throws OAuth2ServiceException,
            UnsupportedEncodingException {
        String accessToken = getAccessToken();

        String url = ASGARDEO_URI + "/" + tenant + "/o/api/server/v1/applications?filter=clientId+eq+" + clientId;
        // get request to get the application details
        HttpGet httpGet = new HttpGet(url);


        httpGet.setHeader(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + accessToken);
        httpGet.setHeader(OAuthConstant.CONTENT_TYPE, OAuthConstant.APPLICATION_JSON);
        httpGet.setHeader(OAuthConstant.ACCEPT, OAuthConstant.APPLICATION_JSON);

        // send the request
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getCode() != HttpStatus.SC_OK) {
                throw new OAuth2ServiceException("Error occurred while retrieving the application details. " +
                        "Status code: " + response.getCode());
            }
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            return (JSONObject) responseJson.getJSONArray("applications").getJSONObject(0);
        } catch (IOException | ParseException | JSONException e) {
            throw new OAuth2ServiceException("Error occurred while retrieving the application details.", e);
        }

    }

    /**
     * This method is used to retrieve the application details.
     */
    public static JSONObject getAppDetailsByAppId(String appId, String tenant) throws OAuth2ServiceException,
            UnsupportedEncodingException {
        String accessToken = getAccessToken();

        // get request to get the application details
        HttpGet tokenReq = new HttpGet(ASGARDEO_URI + "/" + tenant + "/o/api/server/v1/applications/"
                + appId + "/inbound-protocols/oidc");
        tokenReq.setHeader(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + accessToken);
        tokenReq.setHeader(OAuthConstant.CONTENT_TYPE, OAuthConstant.APPLICATION_JSON);
        tokenReq.setHeader(OAuthConstant.ACCEPT, OAuthConstant.APPLICATION_JSON);

        // send the request
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(tokenReq)) {
            if (response.getCode() != HttpStatus.SC_OK) {
                throw new OAuth2ServiceException("Error occurred while retrieving the application details. " +
                        "Status code: " + response.getCode());
            }
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            return responseJson;
        } catch (IOException | ParseException | JSONException e) {
            throw new OAuth2ServiceException("Error occurred while retrieving the application details.", e);
        }


    }

    /**
     * revoke tokens
     */
    public static void revokeTokens(List<String> tokens, OAuthRevocationRequestDTO oAuthRevocationRequestDTO,
                                    String tenant)
            throws UnsupportedEncodingException, OAuth2ServiceException {
        OAuth2Service oAuth2Service = OAuth2Service.getInstance();

        String clientSecret = getClientSecretByClientId(oAuthRevocationRequestDTO.getConsumerKey(), tenant);

        oAuthRevocationRequestDTO.setConsumerSecret(clientSecret);
        //async revoke tokens

        for (String token : tokens) {
            oAuthRevocationRequestDTO.setToken(token);
            try {
                oAuth2Service.revokeToken(ASGARDEO_URI + "/" + tenant, oAuthRevocationRequestDTO);
            } catch (OAuth2ServiceException e) {
                log.error("Error occurred while revoking the token", e);

            }
        }
    }

    // Get Access Token (Refresh if expired)
    public static String getAccessToken() throws OAuth2ServiceException, UnsupportedEncodingException {
        if (isTokenExpired()) {
            refreshAccessToken();
        }
        return accessToken;
    }


}
