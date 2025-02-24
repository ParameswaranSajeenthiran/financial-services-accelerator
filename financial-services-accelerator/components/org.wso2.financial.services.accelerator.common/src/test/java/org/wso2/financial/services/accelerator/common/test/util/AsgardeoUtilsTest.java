package org.wso2.financial.services.accelerator.common.test.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.financial.services.accelerator.common.exception.OAuth2ServiceException;
import org.wso2.financial.services.accelerator.common.services.OAuth2Service;
import org.wso2.financial.services.accelerator.common.util.AsgardeoUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * AsgardeoUtilsTest.
 */
public class AsgardeoUtilsTest {
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
    private static final String tenant = "sajeenthiran2";
    OAuth2Service uut;

    private static final Log LOG = LogFactory.getLog(AsgardeoUtilsTest.class);

    @BeforeClass
    public void init() {
        uut = OAuth2Service.getInstance();

    }

    @Test
    public void testIsFAPIApplication() {
        try {
            assert AsgardeoUtils.isFAPIApplication("fEQKd2AzjGqPVtKXxy4hOzQpLaAa", tenant);
        } catch (Exception e) {
            LOG.error("Error occurred while testing isFAPIApplication", e);
        }
    }

    @Test
    public void testGetAppIdByClientId() {
        try {
            assert Objects.equals("e92c3009-0295-468d-8c55-42d734eb5890",
                    AsgardeoUtils.getAppIdByClientId("fEQKd2AzjGqPVtKXxy4hOzQpLaAa",
                            "sajeenthiran2").get("id"));

        } catch (Exception e) {
            LOG.error("Error occurred while testing getAppIdByClientId", e);
        }
    }

    @Test
    public void testGetUserNameByUserId() {
        try {

            assert Objects.equals(AsgardeoUtils.getUserNameByUserId("91d54919-40d4-4982-b39a-fbc690e5b6dd",
                    tenant), "DEFAULT/psajeendran@gmail.com");
        } catch (Exception e) {
            LOG.error("Error occurred while testing getUserNameByUserId", e);
        }
    }

    @Test
    public void testGetClientSecretByClientId() {
        try {

            assert Objects.equals(CLIENT_SECRET, AsgardeoUtils.getClientSecretByClientId(CLIENT_KEY, tenant));
        } catch (Exception e) {
            LOG.error("Error occurred while testing getClientSecretByClientId", e);

        }
    }

    @Test
    public void testRevokeBulkToken() throws UnsupportedEncodingException, OAuth2ServiceException {
        // Test implementation


        List<String> accessTokens = new ArrayList<>();
        String accessToken = uut.getAccessTokenByClientCredentialsGrantType(IAM_BASE_URL, CLIENT_KEY,
                CLIENT_SECRET, DUMMY_SCOPE);
        accessTokens.add(accessToken);

        OAuthRevocationRequestDTO revokeRequestDTO = new OAuthRevocationRequestDTO();
        revokeRequestDTO.setConsumerKey(CLIENT_KEY);
        AsgardeoUtils.revokeTokens(accessTokens, revokeRequestDTO, tenant);

    }
}
