package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentRetrieval

import groovy.json.JsonOutput
import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.AccountsRequestPayloads
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentRetrievalTest extends ConsentCoreServiceTest {

    @BeforeTest
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH;


    }


    @Test
    void "Consent Retrieval withAuthorizationResources = false & withAttributes = false "() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(false, false);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        String retrievedAuthResources = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES);
        Assert.assertNull(retrievedAuthResources)

        String retrievedAttributes = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_ATTRIBUTES);
        Assert.assertNull(retrievedAttributes)


    }


    @Test
    void "Consent Retrieval withAuthorizationResources = false & withAttributes = false Validation "() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(false, false);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        String retrievedAuthResources = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES);
        Assert.assertNull(retrievedAuthResources)

        String retrievedAttributes = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_ATTRIBUTES);
        Assert.assertNull(retrievedAttributes)

        // parse consent initiation payload and validate the retrieved payload

        String retrievedConsentType = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_TYPE).
                toString()
        Assert.assertEquals(retrievedConsentType, CCSConsentPayload.TEST_CONSENT_TYPE)

        String retrievedCurrentStatus = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CURRENT_STATUS).
                toString()
        Assert.assertEquals(retrievedCurrentStatus, CCSConsentPayload.TEST_CURRENT_STATUS)

        String retrievedRecurringIndicator = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_RECURRING_INDICATOR).
                toString()
        Assert.assertEquals(retrievedRecurringIndicator, CCSConsentPayload.TEST_RECURRING_INDICATOR)

        String retrievedValidityPeriod = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_VALIDITY_PERIOD).
                toString()
        Assert.assertEquals(retrievedValidityPeriod, CCSConsentPayload.TEST_VALIDITY_PERIOD)

        String retrievedReceipt = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_RECEIPT).
                toString()
        Assert.assertEquals(retrievedReceipt, AccountsRequestPayloads.initiationPayload)

        String retrievedClientID = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CLIENT_ID).
                toString()
        Assert.assertEquals(retrievedClientID, CCSConsentPayload.TEST_CLIENT_ID)


    }

    @Test
    void "Consent Retrieval withAuthorizationResources = true & withAttributes = false "() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(true, false);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // assert whether authorizationResources present in the payload

        String retrievedAuthResources = JsonOutput.toJson(TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES));
        Assert.assertNotNull(retrievedAuthResources)

        String retrievedAttributes = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_ATTRIBUTES);
        Assert.assertNull(retrievedAttributes)

    }

    @Test
    void "Consent Retrieval withAuthorizationResources = true & withAttributes = false Validation "() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(true, false);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // check whether the retrieved auth resources are equal to the expected auth resources

        int indexOfAuthResourceA;
        int indexOfAuthResourceB;

        if (TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES + "[0]." +
                ConnectorTestConstants.CSS_USER_ID).toString() ==
                CCSConsentPayload.TEST_USER_ID_A) {
            indexOfAuthResourceA = 0;
            indexOfAuthResourceB = 1;
        } else {
            indexOfAuthResourceA = 1;
            indexOfAuthResourceB = 0;
        }

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceA}].authorizationStatus").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_A)

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceA}].authorizationType").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A)

        // parse resource from Authorization resources and validate
        String retrievedResourceA1AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceA}].resources[0].resource.accountID").toString()
        String retrievedResourceA2AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceA}].resources[1].resource.accountID").toString()

        // assert whether the retrieved resources includes in the expected resources

        ArrayList<String> expectedResourcesA = new ArrayList<String>();
        expectedResourcesA.add(CCSConsentPayload.TEST_ACCOUNT_ID_A1);
        expectedResourcesA.add(CCSConsentPayload.TEST_ACCOUNT_ID_A2);

        Assert.assertTrue(expectedResourcesA.contains(retrievedResourceA1AccountId))
        Assert.assertTrue(expectedResourcesA.contains(retrievedResourceA2AccountId))


        // auth resource B
        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceB}].authorizationStatus").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_B)

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceB}].authorizationType").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_TYPE_B)

        // parse resource from Authorization resources and validate

        String retrievedResourceB1AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceB}].resources[0].resource.accountID").toString()
        String retrievedResourceB2AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceB}].resources[1].resource.accountID").toString()

        // assert whether the retrieved resources includes in the expected resources

        ArrayList<String> expectedResources = new ArrayList<String>();
        expectedResources.add(CCSConsentPayload.TEST_ACCOUNT_ID_B1);
        expectedResources.add(CCSConsentPayload.TEST_ACCOUNT_ID_B2);

        Assert.assertTrue(expectedResources.contains(retrievedResourceB1AccountId))
        Assert.assertTrue(expectedResources.contains(retrievedResourceB2AccountId))


    }

    @Test
    void "Consent Retrieval  isWithAuthorization = false and isWithAttribute = true"() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(false, true);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // assert whether attributes present in the payload

        String retrievedAttributes = JsonOutput.toJson(TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_ATTRIBUTES));
        Assert.assertNotNull(retrievedAttributes)

        String retrievedAuthResources = TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES);
        Assert.assertNull(retrievedAuthResources)

    }

    @Test
    void "Consent Retrieval  withAuthorizationResources = false and withAttribute = true Validation"() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(false, true);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        print("response")
        println(consentResponse.toString())
        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // validate the retrieved attributes
        String retrievedTestAttributeAValue = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_ATTRIBUTES +
                ".testAttributeA").toString()

        Assert.assertEquals(retrievedTestAttributeAValue, CCSConsentPayload.TEST_ATTRIBUTE_A)

        String retrievedTestAttributeBValue = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_ATTRIBUTES +
                ".testAttributeB").toString()

        Assert.assertEquals(retrievedTestAttributeBValue, CCSConsentPayload.TEST_ATTRIBUTE_B)

    }

    @Test
    void "Consent Retrieval  withAuthorizationResources = true and withAttribute = true"() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(true, true);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // assert whether attributes present in the payload

        String retrievedAttributes = JsonOutput.toJson(TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_ATTRIBUTES));
        Assert.assertNotNull(retrievedAttributes)

        // assert whether authorizationResources present in the payload

        String retrievedAuthResources = JsonOutput.toJson(TestUtil.parseResponseBody(consentResponse,
                ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES));
        Assert.assertNotNull(retrievedAuthResources)

    }

    @Test
    void "Consent Retrieval  withAuthorizationResources = true and withAttribute = true Validation"() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrieval(true, true);
        String retrievedConsentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).
                toString()

        Assert.assertEquals(consentId, retrievedConsentId)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        // validate the retrieved attributes
        String retrievedTestAttributeAValue = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_ATTRIBUTES +
                ".testAttributeA").toString()

        Assert.assertEquals(retrievedTestAttributeAValue, CCSConsentPayload.TEST_ATTRIBUTE_A)

        String retrievedTestAttributeBValue = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_ATTRIBUTES +
                ".testAttributeB").toString()

        Assert.assertEquals(retrievedTestAttributeBValue, CCSConsentPayload.TEST_ATTRIBUTE_B)

        // check whether the retrieved auth resources are equal to the expected auth resources

        int indexOfAuthResourceA;
        int indexOfAuthResourceB;

        if (TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES + "[0]." +
                ConnectorTestConstants.CSS_USER_ID).toString() ==
                CCSConsentPayload.TEST_USER_ID_A) {
            indexOfAuthResourceA = 0;
            indexOfAuthResourceB = 1;
        } else {
            indexOfAuthResourceA = 1;
            indexOfAuthResourceB = 0;
        }

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceA}].authorizationStatus").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_A)

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceA}].authorizationType").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A)

        // parse resource from Authorization resources and validate
        String retrievedResourceA1AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceA}].resources[0].resource.accountID").toString()
        String retrievedResourceA2AccountId  = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceA}].resources[1].resource.accountID").toString()

        // assert whether the retrieved resources includes in the expected resources

        ArrayList<String> expectedResourcesA = new ArrayList<String>();





        expectedResourcesA.add(CCSConsentPayload.TEST_ACCOUNT_ID_A1);
        expectedResourcesA.add(CCSConsentPayload.TEST_ACCOUNT_ID_A2);

        Assert.assertTrue(expectedResourcesA.contains(retrievedResourceA1AccountId))
        Assert.assertTrue(expectedResourcesA.contains(retrievedResourceA2AccountId))


        // auth resource B
        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceB}].authorizationStatus").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_B)

        Assert.assertEquals(
                TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_AUTHORIZATION_RESOURCES +
                        "[${indexOfAuthResourceB}].authorizationType").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_TYPE_B)

        // parse resource from Authorization resources and validate

        String retrievedResourceB1AccountId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceB}].resources[0].resource.accountID").toString()
        String retrievedResourceB2AccountId  = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.
                CSS_AUTHORIZATION_RESOURCES +
                "[${indexOfAuthResourceB}].resources[1].resource.accountID").toString()


        // assert whether the retrieved resources includes in the expected resources

        ArrayList<String> expectedResources = new ArrayList<String>();
        expectedResources.add(CCSConsentPayload.TEST_ACCOUNT_ID_B1);
        expectedResources.add(CCSConsentPayload.TEST_ACCOUNT_ID_B2);

        Assert.assertTrue(expectedResources.contains(retrievedResourceB1AccountId))

        Assert.assertTrue(expectedResources.contains(retrievedResourceB2AccountId))

    }

    // org mismatch
    @Test
    void "Consent Retrieval with Org Mismatch"() {
        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources);
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrievalWithWrongOrgInfo(false, false);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)

        // check whether the error message contains the expected error message "OrgInfo"
        Assert.assertTrue(consentResponse.getBody().asString().contains("OrgInfo"))

    }

    // orgInfo = null
    @Test
    void "Consent Retrieval with OrgInfo = null with Consent Initiation OrgInfo =ull "() {

        // created a consent without OrgInfo
        doConsentCreationWithoutOrgInfo(CCSConsentPayload.initiationPayloadWithAuthResources)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrievalWithOutOrgInfo(false, false);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)


    }

    @Test
    void "Consent Retrieval with OrgInfo = null with Consent Initiation OrgInfo = TEST_ORG"() {

        // created a consent without OrgInfo
        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResources)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        doConsentRetrievalWithOutOrgInfo(false, false);
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_400)


    }



}


