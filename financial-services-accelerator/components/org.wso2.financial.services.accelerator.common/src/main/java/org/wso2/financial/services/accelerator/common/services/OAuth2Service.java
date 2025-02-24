package org.wso2.financial.services.accelerator.common.services;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationResponseDTO;
import org.wso2.financial.services.accelerator.common.constant.OAuthConstant;
import org.wso2.financial.services.accelerator.common.exception.OAuth2ServiceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * OAuthService.
 * This specifies service methods to use in oauth2 flow.
 */
public class OAuth2Service {

    private static OAuth2Service oauthService;

    private OAuth2Service() {
    }

    public static synchronized OAuth2Service getInstance() {
        if (oauthService == null) {
            oauthService = new OAuth2Service();
        }
        return oauthService;
    }

    /**
     * send token request
     * params: iamBaseUrl, clientKey, clientSecret, params
     *
     * @return JSONObject
     */
    public JSONObject sendTokenRequest(String iamBaseUrl, String clientKey, String clientSecret,
                                       List<BasicNameValuePair>
                                               params) throws OAuth2ServiceException {

        HttpPost tokenReq = new HttpPost(iamBaseUrl + OAuthConstant.PATH_TOKEN);

        // generating basic authorization
        final String auth = clientKey + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // add request headers
        tokenReq.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        tokenReq.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        tokenReq.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(tokenReq)) {
                String responseStr = EntityUtils.toString(response.getEntity());

                if (response.getCode() == HttpStatus.SC_OK) {
                    // received success response
                    return new JSONObject(responseStr);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


        } catch (IOException | JSONException e) {

            throw new OAuth2ServiceException("Error occurred while sending token request", e);
        }
        throw new OAuth2ServiceException("Invalid response received for token request");

    }

    /**
     * @param iamBaseUrl
     * @param clientKey
     * @param clientSecret
     * @param scopes
     * @return
     * @throws OAuth2ServiceException
     */

    public String getAccessTokenByClientCredentialsGrantType(String iamBaseUrl, String clientKey, String clientSecret,
                                                             String scopes)
            throws OAuth2ServiceException {

        // generate access token request params
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(OAuthConstant.GRANT_TYPE, "client_credentials"));
        params.add(new BasicNameValuePair(OAuthConstant.CLIENT_ID, clientKey));
        params.add(new BasicNameValuePair(OAuthConstant.OAUTH_SCOPE, scopes));

        JSONObject response = sendTokenRequest(iamBaseUrl, clientKey, clientSecret, params);
        return response.get(OAuthConstant.ACCESS_TOKEN).toString();
    }

    /**
     * Revoke token
     *
     * @param iamBaseUrl
     * @param oAuthRevocationRequestDTO
     * @return OAuthRevocationResponseDTO
     * @throws OAuth2ServiceException
     */
    public OAuthRevocationResponseDTO revokeToken(String iamBaseUrl,
                                                  OAuthRevocationRequestDTO oAuthRevocationRequestDTO) throws
            OAuth2ServiceException, OAuth2ServiceException {
        HttpPost revokeReq = new HttpPost(iamBaseUrl + OAuthConstant.PATH_REVOKE);

        // generating basic authorization
        final String auth = oAuthRevocationRequestDTO.getConsumerKey() + ":" +
                oAuthRevocationRequestDTO.getConsumerSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // add request headers
        revokeReq.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        revokeReq.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // add request params
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(OAuthConstant.TOKEN, oAuthRevocationRequestDTO.getToken()));
        revokeReq.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(revokeReq)) {

                OAuthRevocationResponseDTO oAuthRevocationResponseDTO = new OAuthRevocationResponseDTO();
                oAuthRevocationResponseDTO.setError(response.getCode() != HttpStatus.SC_OK);
                oAuthRevocationResponseDTO.setErrorMsg(response.getReasonPhrase());
                return oAuthRevocationResponseDTO;
            }
        } catch (IOException e) {
            throw new OAuth2ServiceException("Error occurred while revoking token", e);
        }
    }

    /**
     * Introspect token
     *
     * @param iamBaseUrl
     * @param clientKey
     * @param clientSecret
     * @param token
     * @return JSONObject
     * @throws IOException
     * @throws OAuth2ServiceException
     * @throws OAuth2ServiceException
     */

    public JSONObject introspectToken(String iamBaseUrl, String clientKey, String clientSecret, String token)
            throws IOException, OAuth2ServiceException, OAuth2ServiceException {
        HttpPost tokenReq = new HttpPost(iamBaseUrl + OAuthConstant.PATH_INTROSPECT);

        // generating basic authorization
        final String auth = clientKey + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // add request headers
        tokenReq.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        tokenReq.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // add request params
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(OAuthConstant.TOKEN, token));
        tokenReq.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(tokenReq)) {
                String responseStr = EntityUtils.toString(response.getEntity());

                if (response.getCode() == HttpStatus.SC_OK) {
                    // received success response
                    return new JSONObject(responseStr);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        throw new OAuth2ServiceException("Invalid response received for token introspect request");

    }

}
