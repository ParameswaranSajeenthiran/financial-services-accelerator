package org.wso2.financial.services.accelerator.common.test.services;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.financial.services.accelerator.common.exception.OAuth2ServiceException;
import org.wso2.financial.services.accelerator.common.services.OAuth2Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * OAuthServiceTest.
 */
public class OAuthServiceTest {
    private static final String IAM_BASE_URL = "https://api.asgardeo.io/t/sajeenthiran2";
    private static final String CLIENT_KEY = "D3nfKNP70fOCjF60djhC8XgYLGAa";
    private static final String CLIENT_SECRET = "VcC0SDj6pbb8aj1YcaMMf9rw_KYix4SxUcCK2WaKufIa";
    private static final String DUMMY_SCOPE = "internal_org_application_mgt_update internal_org_application_mgt_view " +
            "internal_dcr_create internal_dcr_delete internal_dcr_view internal_dcr_update " +
            "internal_organization_create internal_organization_view internal_organization_update " +
            "internal_organization_delete internal_user_mgt_create internal_user_mgt_list" +
            " internal_user_mgt_view internal_user_mgt_delete internal_user_mgt_update " +
            "internal_user_association_delete" +
            " internal_user_association_view";
    OAuth2Service uut;

    @BeforeClass
    public void init() {
        uut = OAuth2Service.getInstance();

    }

    @Test
    public void testSendClientCredentialsTokenRequest() throws  UnsupportedEncodingException, OAuth2ServiceException {

        // Test implementation
        String accessToken = uut.getAccessTokenByClientCredentialsGrantType(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET,
                DUMMY_SCOPE);

        // Test assertions
        assert accessToken != null;


    }

    @Test
    public void testRevokeToken () throws IOException, OAuth2ServiceException {
        // Test implementation

        String accessToken = uut.getAccessTokenByClientCredentialsGrantType(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET,
                DUMMY_SCOPE);

        OAuthRevocationRequestDTO revokeRequestDTO = new OAuthRevocationRequestDTO();
        revokeRequestDTO.setConsumerKey(CLIENT_KEY);
        revokeRequestDTO.setConsumerSecret(CLIENT_SECRET);
        revokeRequestDTO.setToken(accessToken);


        JSONObject responseBeforeRevocation = uut.introspectToken(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET,
                revokeRequestDTO.getToken());

        assert responseBeforeRevocation.get("active").equals(true);

       uut.revokeToken(IAM_BASE_URL, revokeRequestDTO);

        // send token introspection request
        JSONObject response = uut.introspectToken(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET, revokeRequestDTO.getToken());

        // Test assertions
//        assert response.get("active").equals(false);
    }

}
