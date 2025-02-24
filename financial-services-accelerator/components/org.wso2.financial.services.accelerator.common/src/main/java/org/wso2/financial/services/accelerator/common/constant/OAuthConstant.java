package org.wso2.financial.services.accelerator.common.constant;

/**
 * Constants required for scp webapp.
 */
public class OAuthConstant {


    private OAuthConstant() {
        // No public instances
    }

    // OAUTH Constants
    public static final String CLIENT_ID = "client_id";
    public static final String GRANT_TYPE = "grant_type";
    public static final String OAUTH_SCOPE = "scope";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER = "Bearer ";
    public static final String USER_AGENT = "User-Agent";
    public static final String TOKEN = "token";

    public static final String ACCESS_TOKEN = "access_token";
    // Paths
    public static final String PATH_TOKEN = "/oauth2/token";
    public static final String PATH_REVOKE = "/oauth2/revoke";
    public static final String PATH_INTROSPECT = "/oauth2/introspect";

}
