package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentInitiationTest

import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentInitiationTest extends ConsentCoreServiceTest {

    @BeforeClass
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH;

    }

    @Test
    void "Initiate valid consent"() {

        doConsentCreation(CCSConsentPayload.initiationPayload);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        Assert.assertNotNull(consentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_201)

    }

    @Test
    void "Initiate consent without clientID"() {

        doConsentCreation(TestUtil.removeKey(CCSConsentPayload.initiationPayload, "clientId" ));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400);

    }

    @Test
    void "Initiate consent without receipt"() {

        doConsentCreation(CCSConsentPayload.invalidPayloadWithoutReceipt);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }

    @Test
    void "Initiate consent without consentType"() {

        doConsentCreation(TestUtil.removeKey(CCSConsentPayload.initiationPayload, "consentType"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }

    @Test
    void "Initiate consent without currentStatus"() {

        doConsentCreation(TestUtil.removeKey(CCSConsentPayload.initiationPayload, "currentStatus"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }

    @Test
    void "Initiate consent without expiryTime"() {

        doConsentCreation(TestUtil.removeKey(CCSConsentPayload.initiationPayload, "expiryTime"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }



    @Test
    void "Initiate consent without consentAttributes"() {

        doConsentCreation(CCSConsentPayload.payloadWithoutConsentAttributes);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_201)

    }

    @Test
    void "Initiate consent with empty clientID"() {

        doConsentCreation( TestUtil.replaceValueWithEmptyString(CCSConsentPayload.initiationPayload,
                "clientId"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }

    @Test
    void "Initiate consent with empty consentType"() {

        doConsentCreation(TestUtil.replaceValueWithEmptyString(CCSConsentPayload.initiationPayload,
                "consentType"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }

    @Test
    void "Initiate consent with empty currentStatus"() {

        doConsentCreation(TestUtil.replaceValueWithEmptyString(CCSConsentPayload.initiationPayload,
                "currentStatus"));
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }


    @Test
    void "Initiate consent with invalid recurringIndicator type"() {

        doConsentCreation(CCSConsentPayload.payloadWithInvalidRecurringIndicatorType);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

    }



    @Test
    void "Initiate consent with Implicit = True"(){
        doConsentCreation(CCSConsentPayload.initiationPayloadWithAuthResources);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_201)
    }


    @Test
    void "Initiate consent with Implicit = True and without AuthStatus"(){
        doConsentCreation( TestUtil.removeKey(CCSConsentPayload.
                initiationPayloadWithAuthResources,"authorizationStatus"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

        // assert error message contains "authorizationStatus"
        Assert.assertTrue(consentResponse.getBody().asString().contains("authorizationStatus"))
    }

    // without authorization type
    @Test
    void "Initiate consent with Implicit = True and without AuthType"(){
        doConsentCreation( TestUtil.removeKey(CCSConsentPayload.
                initiationPayloadWithAuthResources,"authorizationType"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

        // assert error message contains "authorizationType"
        Assert.assertTrue(consentResponse.getBody().asString().contains("authorizationType"))
    }

    // without userID
    @Test
    void "Initiate consent with Implicit = True and without userID"(){
        doConsentCreation( TestUtil.removeKey(CCSConsentPayload.
                initiationPayloadWithAuthResources,"userID"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_201)

        // assert error message contains "userID"
        Assert.assertTrue(consentResponse.getBody().asString().contains("userId"))
    }



    // with empty authorization status
    @Test
    void "Initiate consent with Implicit = True and with empty AuthStatus"(){
        doConsentCreationWithImplicitAuth( TestUtil.replaceValueWithEmptyString(CCSConsentPayload.
                initiationPayloadWithAuthResources,"authorizationStatus"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

        // assert error message contains "authorizationStatus"
        Assert.assertTrue(consentResponse.getBody().asString().contains("authorizationStatus"))
    }

    // with empty authorization type
    @Test
    void "Initiate consent with Implicit = True and with empty AuthType"(){
        doConsentCreationWithImplicitAuth( TestUtil.replaceValueWithEmptyString(CCSConsentPayload.
                initiationPayloadWithAuthResources,"authorizationType"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

        // assert error message contains "authorizationType"
        Assert.assertTrue(consentResponse.getBody().asString().contains("authorizationType"))
    }

    // with empty userID
    @Test
    void "Initiate consent with Implicit = True and with empty userID"(){
        doConsentCreationWithImplicitAuth( TestUtil.replaceValueWithEmptyString(CCSConsentPayload.
                initiationPayloadWithAuthResources,"userID"));

        // assert status code is 400
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_201)

        // assert error message contains "userID"
        Assert.assertTrue(consentResponse.getBody().asString().contains("userId"))
    }



}
