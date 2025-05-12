package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentSearch

import org.json.JSONObject
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentSearchTest extends  ConsentCoreServiceTest{


    @BeforeClass
    void init(){
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH
        doBulkConsentInitiation()

        // store counts

    }

    @Test
    void "Search consent by currentStatus"() {
        String currentStatus = randomPayloadsWithCount.get("statusCount").keySet()[0]
        print(currentStatus)

        doConsentSearch(currentStatus, null, null, null, null, null, null,null);


        List<String> consentStatuses = consentResponse.jsonPath().getList("currentStatus")

        // assert whether the count is equal to the number of consents with the currentStatus
        Assert.assertEquals(consentStatuses.size(), randomPayloadsWithCount.get("statusCount").get(currentStatus))

        // assert whether all are of the currentStatus
        Assert.assertTrue(consentStatuses.every { it == currentStatus })

    }

    @Test
    void "Search consent by consentType"() {
        String consentType = randomPayloadsWithCount.get("consentTypeCount").keySet()[0]
        print(consentType)

        doConsentSearch(null, consentType, null, null, null, null, null,null);

        List<String> consentTypes = consentResponse.jsonPath().getList("consentType")

        // assert whether the count is equal to the number of consents with the consentType
        Assert.assertEquals(consentTypes.size(), randomPayloadsWithCount.get("consentTypeCount").get(consentType))

        // assert whether all are of the consentType
        Assert.assertTrue(consentTypes.every { it == consentType })

    }

    @Test
    void "Search consent by clientID"() {
        String clientID = randomPayloadsWithCount.get("clientIdCount").keySet()[0]
        print(clientID)

        doConsentSearch(null, null, null, clientID, null, null, null,null);

        List<String> clientIDs = consentResponse.jsonPath().getList("clientId")

        // assert whether all are of the clientID
        Assert.assertTrue(clientIDs.every { it == clientID })
        // assert whether the count is equal to the number of consents with the clientID
//        Assert.assertEquals(clientIDs.size(), randomPayloadsWithCount.get("clientIdCount").get(clientID))



    }

    // userId
    @Test
    void "Search consent by userID"() {
        String userID = randomPayloadsWithCount.get("userIdCount").keySet()[0]
        print(userID)

        doConsentSearch(null, null, userID, null, null, null, null,null);

        List<String> userIDs = consentResponse.jsonPath().getList("authorizationResources.userId")

        // remove []
        userIDs = userIDs.collect { it[0]}

        print(userIDs)
        print(userID)
        // assert whether the count is equal to the number of consents with the userID
        Assert.assertEquals(userIDs.size(), randomPayloadsWithCount.get("userIdCount").get(userID))

        // assert whether all are of the userID
        Assert.assertTrue(userIDs.every { it == userID })

    }

    @Test
    void "Search consent by consentType and currentStatus"() {
        String consentType = randomPayloadsWithCount.get("consentTypeCount").keySet()[0]
        String currentStatus = randomPayloadsWithCount.get("statusCount").keySet()[0]
        print(consentType)
        print(currentStatus)

        doConsentSearch(currentStatus, consentType, null, null, null, null, null,null);

        List<String> consentTypes = consentResponse.jsonPath().getList("consentType")
        List<String> consentStatuses = consentResponse.jsonPath().getList("currentStatus")


        // assert whether all are of the consentType
        Assert.assertTrue(consentTypes.every { it == consentType })


        // assert whether all are of the currentStatus
        Assert.assertTrue(consentStatuses.every { it == currentStatus })

        // assert whether the count is equal to the number of consents with the currentStatus and currentType
        // combination
        Map<String,Integer> combinationCount = randomPayloadsWithCount.get("combinationCount")

        // get keys which include the currentStatus and consentType
        int expectedCount = 0 ;
        combinationCount.keySet().forEach {it->
            if (it.contains(currentStatus) && it.contains(consentType)){
                expectedCount = expectedCount+ combinationCount.get(it)
            }
        }
        Assert.assertEquals(consentStatuses.size(), expectedCount)


    }




}



