package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentRevoke

import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentRevokeTest extends ConsentCoreServiceTest {

    @BeforeClass
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH


    }

    @Test
    void "Verify Revoke Consent"() {
        // Revoke
        doConsentCreation(CCSConsentPayload.initiationPayload)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()
        doConsentRevoke(CCSConsentPayload.revokeConsentPayload(CCSConsentPayload.TEST_UPDATE_REASON,
                CCSConsentPayload.TEST_USER_ID_A))
        Assert.assertEquals( consentResponse.getStatusCode() , ConnectorTestConstants.STATUS_CODE_200)

    }

    @Test(dependsOnMethods = "Verify Revoke Consent")
    void "Verify Revoke revoked Consent"() {
        // Revoke
        doConsentRevoke(CCSConsentPayload.revokeConsentPayload(CCSConsentPayload.TEST_UPDATE_REASON,
                CCSConsentPayload.TEST_USER_ID_A))
        Assert.assertEquals( consentResponse.getStatusCode() , ConnectorTestConstants.STATUS_CODE_400)

    }


}
