package org.wso2.financial.services.accelerator.test.consent.core.service.ConsentStatusUpdate

import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.consent.core.service.ConsentCoreServiceTest
import org.wso2.financial.services.accelerator.test.framework.FSConnectorTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentBulkStatusUpdate extends  ConsentCoreServiceTest {

    @BeforeTest
    void init(){
        ccsConsentPath = ConnectorTestConstants.CCS_CONSENT_PATH
        doBulkConsentInitiation()

        // store counts

    }

    @Test
    void "Test Bulk Update by clientID"(){
        String clientID = randomPayloadsWithCount.get("clientIdCount").keySet()[0]
        print(clientID)

        String updatedStatus = "updated"

        doBulkConsentStatusUpdate(CCSConsentPayload.queryByClientIdAndBulkStatusUpdatePayload(clientID, updatedStatus))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

    }
    @Test
    void "Test Bulk Update by consentType"(){
        String consentType = randomPayloadsWithCount.get("consentTypeCount").keySet()[0]
        print(consentType)

        String updatedStatus = "updated"

        doBulkConsentStatusUpdate(CCSConsentPayload.queryByConsentTypeAndBulkStatusUpdatePayload(consentType, updatedStatus))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)

        doConsentSearch(null, consentType, null, null, null, null, null,null);
        List<String> consentStatuses = consentResponse.jsonPath().getList("currentStatus")
        // assert whether the count is equal to the number of consents with the consentType
        Assert.assertEquals(consentStatuses.size(), randomPayloadsWithCount.get("consentTypeCount").get(consentType))
        // assert whether all are of the consentType
        Assert.assertTrue(consentStatuses.every { it == updatedStatus })


    }
    @Test
    void "Test Bulk Update by currentStatus"(){
        String currentStatus = randomPayloadsWithCount.get("statusCount").keySet()[0]
        print(currentStatus)

        String updatedStatus = "updated"

        doConsentSearch(currentStatus, null, null, null, null, null, null,null);
        List <String> consentIds = consentResponse.jsonPath().getList("consentID")



        doBulkConsentStatusUpdate(CCSConsentPayload.queryByApplicableStatusesAndBulkStatusUpdatePayload(currentStatus, updatedStatus))
        Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)


        // get all consentIds with the currentStatus

        for (String consentId_ : consentIds) {
            consentId=consentId_
            doConsentRetrieval(false,false)
            Assert.assertEquals(consentResponse.getStatusCode(), ConnectorTestConstants.STATUS_CODE_200)
            Assert.assertEquals(TestUtil.parseResponseBody(consentResponse, "currentStatus").toString(), updatedStatus)
        }



    }



}
