package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentAmendment

import groovy.json.JsonOutput
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.AccountsRequestPayloads
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentAmendmentTest extends ConsentCoreServiceTest {



    @BeforeClass
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH

    }

    @Test
    void "Amend receipt"() {
        doConsentCreation(CCSConsentPayload.initiationPayload)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()



         String[] permissionsArray = [
                ConnectorTestConstants.READ_ACCOUNTS_BASIC,
                ConnectorTestConstants.READ_ACCOUNTS_DETAIL,
                ConnectorTestConstants.READ_TRANSACTIONS_DETAIL
        ]
        // amend receipt
        String updatedReceipt = """
            {
                "Data":{
                "Permissions": ${JsonOutput.toJson(permissionsArray)},
                "ExpirationDateTime":"${ConnectorTestConstants.expirationInstant}",
                "TransactionFromDateTime":"${ConnectorTestConstants.fromInstant}",
                "TransactionToDateTime":"${ConnectorTestConstants.toInstant}"
            },
                "Risk":{

                }
            }
            """.stripIndent()


        doConsentAmendment(CCSConsentPayload.amendConsentPayloadWithoutAuthorization(
                JsonOutput.toJson(updatedReceipt).toString(),CCSConsentPayload.
                TEST_VALIDITY_PERIOD,CCSConsentPayload.TEST_RECURRING_INDICATOR, CCSConsentPayload.
                TEST_CONSENT_ATTRIBUTES,
               ))


        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        // assert updated receipt
        doConsentRetrieval(false, false)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "receipt").toString(),
                updatedReceipt)


    }

    @Test
    void "Amend validityPeriod"() {
        doConsentCreation(CCSConsentPayload.initiationPayload)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String updatedValidityPeriod = "40"
        doConsentAmendment(CCSConsentPayload.amendConsentPayloadWithoutAuthorization(
                CCSConsentPayload.TEST_RECEIPT,
                updatedValidityPeriod, CCSConsentPayload.TEST_RECURRING_INDICATOR, CCSConsentPayload.
                TEST_CONSENT_ATTRIBUTES))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        // assert updated validity period
        doConsentRetrieval(false, false)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "validityPeriod").toString(),
                updatedValidityPeriod)
    }

    @Test
    void "Amend Consent Attributes"(){

        doConsentCreation(CCSConsentPayload.initiationPayload)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String updatedConsentAttributes =
            """
                {
                    "testAttributeA": "testValueA",
                    "testAttributeB": "updatedTestValueB",
                    "testAttributeC": "testValueC"
                }
        """.stripIndent()

        doConsentAmendment(CCSConsentPayload.amendConsentPayloadWithoutAuthorization(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                updatedConsentAttributes
                 ))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        // assert updated consent attributes
        doConsentRetrieval(false, true)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "consentAttributes.testAttributeB").toString(),
                "updatedTestValueB")
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "consentAttributes.testAttributeA").toString(),
                "testValueA")
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "consentAttributes.testAttributeC").toString(),
                "testValueC")
    }

    @Test
    void "Add Resource"(){

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String authId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authId").toString()
        String resourceMappingId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0]." + "resourceMappingId").toString()
        String resourceConsentMappingStatus = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0].consentMappingStatus").toString()



        String existingResource = CCSConsentPayload.sampleAmendResource(resourceMappingId,
                resourceConsentMappingStatus,CCSConsentPayload.TEST_RESOURCE_A1)

        String newResource = CCSConsentPayload.sampleNewResource(CCSConsentPayload.TEST_RESOURCE_B1)

        String resourceArray = """
                [
                    ${existingResource},
                    ${newResource}
                ]
        """.stripIndent()

        String updatedAuthorizationResource = CCSConsentPayload.sampleAmendAuthorizationResource( authId,
                CCSConsentPayload.
                TEST_AUTHORIZATION_STATUS_A,CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A, CCSConsentPayload.
                TEST_USER_ID_A,
                resourceArray)



        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource}]"""))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        // assert updated resource
        doConsentRetrieval(true, true)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)



        String existingResourceIndex;
        String newResourceIndex;

        // get index of existing resource
        if (TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[0].resourceMappingId").
                toString().equals(resourceMappingId)){
            existingResourceIndex = 0
            newResourceIndex = 1
        }else{
            existingResourceIndex = 1
            newResourceIndex = 0
        }



        Assert.assertNotNull(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[${existingResourceIndex}].resource").toString())
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[${existingResourceIndex}].consentMappingStatus").toString(),
                resourceConsentMappingStatus)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[${existingResourceIndex}].resourceMappingId").toString(),
                resourceMappingId)


        Assert.assertNotNull(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[${newResourceIndex}].resource").toString())
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[${newResourceIndex}].consentMappingStatus").toString(),
                'active')

        Assert.assertNotNull(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[0]." +
                "resourceMappingId"))

    }

    @Test
    void "Delete Resource"(){
        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String resourceMappingId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0]." + "resourceMappingId").toString()
        String resourceArray = """
                [  ]
        """.stripIndent()
        String authId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authId").toString()

        String updatedAuthorizationResource = CCSConsentPayload.sampleAmendAuthorizationResource( authId,
                CCSConsentPayload.
                        TEST_AUTHORIZATION_STATUS_A,CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A, CCSConsentPayload.
                TEST_USER_ID_A,
                resourceArray)



        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource}]"""))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        // assert updated resource
        doConsentRetrieval(true, true)

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        Assert.assertNotNull(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0].resource").toString())
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[0]." +
                "consentMappingStatus").toString(),
                'inactive')
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0].resources[0]." +
                "resourceMappingId").toString(),
                resourceMappingId)
    }

    @Test
    void "Amend Authorization Status"(){

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String authId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authId").toString()
        String resourceMappingId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0]." + "resourceMappingId").toString()
        String resourceConsentMappingStatus = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0].consentMappingStatus").toString()

        String resourceArray = """
                [
                    ${CCSConsentPayload.sampleAmendResource(resourceMappingId,
                            resourceConsentMappingStatus,CCSConsentPayload.TEST_RESOURCE_A1)}
                ]
        """.stripIndent()
        String updatedAuthorizationResource = CCSConsentPayload.sampleAmendAuthorizationResource( authId,
                CCSConsentPayload.
                        TEST_AUTHORIZATION_STATUS_B,CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A, CCSConsentPayload.
                TEST_USER_ID_A,
                resourceArray)
        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource}]"""))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)


        // assert updated authorization status
        doConsentRetrieval(true, true)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authorizationStatus").toString(),
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_B)

    }





}
