package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentHistoryRetrieval

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

import java.time.OffsetDateTime

class ConsentHistoryRetrieval extends ConsentCoreServiceTest {
    @BeforeClass
    void init() {
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH
        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

    }

    @Test(groups = "Track History of Amendments for a consent")
    void "Test valid Consent ID"() {
        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        assert consentResponse.getStatusCode() == ConnectorTestConstants.STATUS_CODE_200

        List<String> consentIDs = consentResponse.jsonPath().getList("consentID")

        print(consentIDs)
        // assert whether all are of the clientID
        Assert.assertTrue(consentIDs.every { it == consentId })
    }

    @Test(priority = 99)
    void "Test invalid Consent ID"() {
        doConsentHistoryRetrieval("INVALID" ,"true",null,null,null)
        assert consentResponse.getStatusCode() == ConnectorTestConstants.STATUS_CODE_400
    }

    @Test(dependsOnMethods="Test valid Consent ID", groups = "Track History of Amendments for a consent")
    void "Test receipt amendment History"(){
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
                TEST_VALIDITY_PERIOD,CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES
        ))
        Assert.assertEquals( consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        String currentReceipt = TestUtil.parseResponseBody(consentResponse, "[0].detailedConsentResource.receipt")
        Assert.assertEquals( currentReceipt, updatedReceipt)


        String previousReceipt =  TestUtil.parseResponseBody(consentResponse, "[0]." +
                "changedAttributesJsonDataMap." +
                "ConsentData")
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> previousReceiptMap = objectMapper.readValue(previousReceipt, Map.class);
        print(previousReceiptMap)
        // assert whether the previous receipt is equal to the original receipt
        Assert.assertEquals(JsonOutput.toJson(previousReceiptMap.get("RECEIPT")).toString(),  CCSConsentPayload.
                TEST_RECEIPT)

    }

    @Test(dependsOnMethods = "Test receipt amendment History" , groups = "Track History of Amendments for a consent")

    void "Test Consent Attributes Amendment"(){
        String updatedConsentAttributes = """
                                     {
                                              "testAttributeA": "testAttributeAAmended",
                                              "testAttributeB": "${CCSConsentPayload.TEST_ATTRIBUTE_B}"
                                            }

                                """.stripIndent()

        doConsentAmendment(CCSConsentPayload.amendConsentPayloadWithoutAuthorization(
                CCSConsentPayload.TEST_RECEIPT,CCSConsentPayload.
                TEST_VALIDITY_PERIOD,CCSConsentPayload.TEST_RECURRING_INDICATOR,
                updatedConsentAttributes
        ))
        Assert.assertEquals( consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        String currentConsentAttributes = TestUtil.parseResponseBody(consentResponse, "[0].detailedConsentResource." +
                "consentAttributes.testAttributeA")
        Assert.assertEquals( currentConsentAttributes, "testAttributeAAmended")
        String previousConsentAttributes =  TestUtil.parseResponseBody(consentResponse, "[0]." +
                "changedAttributesJsonDataMap." +
                "ConsentAttributesData")
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> previousConsentAttributesMap = objectMapper.readValue(previousConsentAttributes, Map.class);
        print(previousConsentAttributesMap)
        Assert.assertEquals( previousConsentAttributesMap.get('testAttributeA'), "testValueA")

    }

    @Test (dependsOnMethods = "Test Consent Attributes Amendment" , groups = "Track History of Amendments for a consent")
    void "Test Amend Validity period"(){
        String updatedValidityPeriod = "40"
        doConsentAmendment(CCSConsentPayload.amendConsentPayloadWithoutAuthorization(
                CCSConsentPayload.TEST_RECEIPT,
                updatedValidityPeriod, CCSConsentPayload.TEST_RECURRING_INDICATOR, CCSConsentPayload.
                TEST_CONSENT_ATTRIBUTES))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        String currentValidityPeriod = TestUtil.parseResponseBody(consentResponse, "[0].detailedConsentResource." +
                "validityPeriod")
        Assert.assertEquals(currentValidityPeriod, updatedValidityPeriod)
        String previousValidityPeriod =  TestUtil.parseResponseBody(consentResponse, "[0]." +
                "changedAttributesJsonDataMap." +
                "ConsentData")
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> previousValidityPeriodMap = objectMapper.readValue(previousValidityPeriod, Map.class);
        print(previousValidityPeriodMap)
        Assert.assertEquals(previousValidityPeriodMap.get('VALIDITY_TIME'), CCSConsentPayload.TEST_VALIDITY_PERIOD)



    }

    @Test (priority = 99)
    void "Test add resource"(){

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
        doConsentHistoryRetrieval(consentId ,"true",null,null,null)



    }

    @Test
    void "Test currentStatus change"(){
        doConsentStatusUpdate(CCSConsentPayload.statusUpdatePayload)
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        String currentStatus = TestUtil.parseResponseBody(consentResponse, "[0].detailedConsentResource." +
                "currentStatus")
        Assert.assertEquals(currentStatus, CCSConsentPayload.TEST_UPDATED_STATUS)
        String previousStatus =  TestUtil.parseResponseBody(consentResponse, "[0]." +
                "changedAttributesJsonDataMap." +
                "ConsentData")
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> previousStatusMap = objectMapper.readValue(previousStatus, Map.class);
        print(previousStatusMap)
        Assert.assertEquals(previousStatusMap.get('CURRENT_STATUS'), CCSConsentPayload.TEST_CURRENT_STATUS)
    }


    @Test(priority = 99)
    void "Test Authorization Status Update"(){

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()
        String authId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authId").toString()
        String resourceMappingId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0]." + "resourceMappingId").toString()
        String resourceConsentMappingStatus = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0].consentMappingStatus").toString()

        String updatedAuthorizationResource = CCSConsentPayload.sampleAmendAuthorizationResource( authId,
                CCSConsentPayload.
                        TEST_AUTHORIZATION_STATUS_B,CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A, CCSConsentPayload.
                TEST_USER_ID_A,
                """[${CCSConsentPayload.sampleAmendResource(resourceMappingId,
                        resourceConsentMappingStatus,CCSConsentPayload.TEST_RESOURCE_A1)}]""")

        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource}]"""))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
        String currentAuthorizationStatus = TestUtil.parseResponseBody(consentResponse, "[0].detailedConsentResource." +
                "authorizationResources[0].authorizationStatus")
        Assert.assertEquals(currentAuthorizationStatus, CCSConsentPayload.TEST_AUTHORIZATION_STATUS_B)
        String previousAuthorizationStatusMap =  consentResponse.jsonPath().get("[0]." +
                "changedAttributesJsonDataMap.ConsentAuthResourceData."+authId)

        print(previousAuthorizationStatusMap)
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> previousAuthorizationResourceMap = objectMapper.readValue(previousAuthorizationStatusMap,
                Map.
                class);


        print(previousAuthorizationStatusMap)
        Assert.assertEquals(previousAuthorizationResourceMap.get("AUTHORIZATION_STATUS"), CCSConsentPayload.
                TEST_AUTHORIZATION_STATUS_A)


    }

    @Test(priority = 99)
    void "Add new Resource"(){

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

        doConsentHistoryRetrieval(consentId ,"true",null,null,null)




    }

    @Test(priority  = 99)
    void "Test Delete Resource"() {

        doConsentCreationWithImplicitAuth(CCSConsentPayload.initiationPayloadWithAuthResource)
        consentId = TestUtil.parseResponseBody(consentResponse, ConnectorTestConstants.CSS_CONSENT_ID).toString()

        String resourceMappingId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "resources[0]." + "resourceMappingId").toString()
        String resourceArray = """
                [  ]
        """.stripIndent()
        String authId = TestUtil.parseResponseBody(consentResponse, "authorizationResources[0]." +
                "authId").toString()

        String updatedAuthorizationResource = CCSConsentPayload.sampleAmendAuthorizationResource(authId,
                CCSConsentPayload.
                        TEST_AUTHORIZATION_STATUS_A, CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A, CCSConsentPayload.
                TEST_USER_ID_A,
                resourceArray)


        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource}]"""))

        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        doConsentHistoryRetrieval(consentId ,"true",null,null,null)
    }


    @Test(priority  = 99)
    void "Test Add Authorization Resource"(){
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

        resourceArray = """
                [
                    ${newResource}
                ]
        """.stripIndent()
        String newAuthorizationResource = CCSConsentPayload.sampleNewAmendAuthorizationResource(
                CCSConsentPayload.TEST_AUTHORIZATION_STATUS_A,CCSConsentPayload.TEST_AUTHORIZATION_TYPE_A,
                CCSConsentPayload.TEST_USER_ID_A,resourceArray)
        doConsentAmendment(CCSConsentPayload.amendConsentPayload(
                CCSConsentPayload.TEST_RECEIPT,
                CCSConsentPayload.TEST_VALIDITY_PERIOD, CCSConsentPayload.TEST_RECURRING_INDICATOR,
                CCSConsentPayload.TEST_CONSENT_ATTRIBUTES,
                """[${updatedAuthorizationResource},${newAuthorizationResource}]"""))

        doConsentHistoryRetrieval(consentId, "true",null,null,null)
    }

    @Test(dependsOnGroups = "Track History of Amendments for a consent")
    void "Test FromDateTime and ToDateTime"(){

        doConsentHistoryRetrieval(consentId ,"true",null, OffsetDateTime.now().minusSeconds(1).toInstant().toEpochMilli().
                toString(),
                OffsetDateTime.now().plusMinutes(1).toInstant().toEpochMilli().
                        toString())
        Assert.assertEquals(consentResponse.getStatusCode() ,ConnectorTestConstants.STATUS_CODE_200);
        List<String> timestamps = consentResponse.jsonPath().getList("timestamp")

        print(timestamps)
        Assert.assertTrue(timestamps.every { it.toLong()<= OffsetDateTime.now().plusMinutes(1).toInstant().toEpochMilli()})
        Assert.assertTrue(timestamps.every { it.toLong()>= OffsetDateTime.now().minusSeconds(1).toInstant().
                toEpochMilli() })

    }

}
