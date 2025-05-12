package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentStatusUpdate

import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentStatusUpdate extends ConsentCoreServiceTest{

    @BeforeTest
    void init(){
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH
        doConsentCreation(CCSConsentPayload.initiationPayload)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

    }

    @Test
    void "Update consent status"() {

        doConsentStatusUpdate(CCSConsentPayload.statusUpdatePayload)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // assert updated status
        doConsentRetrieval();
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "currentStatus").toString(),
                CCSConsentPayload.TEST_UPDATED_STATUS)

    }

    @Test(priority = 99)
    void "Update consent status with non-existing consentId"() {
        consentId = CCSConsentPayload.INVALID_CONSENT_ID;
        doConsentStatusUpdate( CCSConsentPayload.statusUpdatePayload)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_404)
    }

    @Test
    void "Update consent status with invalid payload"() {
        doConsentStatusUpdate(CCSConsentPayload.invalidPayloadWithoutReceipt)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }

    @Test
    void "Update consent status without status"() {
        doConsentStatusUpdate (TestUtil.removeKey(CCSConsentPayload.statusUpdatePayload, "status"))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }

    // empty status
    @Test
    void "Update consent status with empty status"() {
        doConsentStatusUpdate (TestUtil.replaceValueWithEmptyString(
                CCSConsentPayload.statusUpdatePayload, "status"))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }

    @Test
    void "Update consent status without userID"() {
        doConsentStatusUpdate (TestUtil.removeKey(CCSConsentPayload.statusUpdatePayload, "userId"))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }

    // empty userID
    @Test
    void "Update consent status with empty userID"() {
        doConsentStatusUpdate (TestUtil.replaceValueWithEmptyString(
                CCSConsentPayload.statusUpdatePayload, "userId"))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }

    @Test
    void "Update consent status without reason"() {
        doConsentStatusUpdate (TestUtil.removeKey(CCSConsentPayload.statusUpdatePayload, "reason"))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)
    }



}
