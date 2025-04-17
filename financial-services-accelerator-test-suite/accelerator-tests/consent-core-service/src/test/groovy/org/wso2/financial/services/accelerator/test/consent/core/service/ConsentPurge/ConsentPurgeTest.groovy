package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentPurge

import org.junit.Before
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentPurgeTest extends ConsentCoreServiceTest {

    @BeforeClass
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH
    }

    @Test
    void "Test valid Consent ID"() {


        doConsentCreation(CCSConsentPayload.initiationPayload);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()
        doConsentPurge(consentId)

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
    }


    @Test
    void "Test invalid Consent ID"() {


        consentId = "INVALID"
        doConsentPurge(consentId)

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_404)
    }



    @Test
    void "Verify Purged consent with consent Retrieval"() {

        doConsentCreation(CCSConsentPayload.initiationPayload);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()
        doConsentPurge(consentId)

        doConsentRetrieval(false, false)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_404)
    }


}
