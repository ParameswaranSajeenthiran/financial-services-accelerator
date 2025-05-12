package org.wso2.financial.services.accelerator.test.consent.core.service

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.apache.http.client.methods.RequestBuilder
import org.testng.annotations.Test
import org.wso2.financial.services.accelerator.test.framework.FSConnectorTest
import org.wso2.financial.services.accelerator.test.framework.constant.CCSConsentPayload
import org.wso2.financial.services.accelerator.test.framework.constant.ConnectorTestConstants
import org.wso2.financial.services.accelerator.test.framework.utility.TestUtil

class ConsentCoreServiceTest extends  FSConnectorTest {
    String ccsConsentPath
    String orgInfo
    Map<String,Map<String,Integer>> randomPayloadsWithCount;



    /**
     *  CCS consent creation
     **/
    void doConsentCreation(String payload) {

        // creation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .post(ccsConsentPath)


    }

    /**
     *  CCS consent creation without orgInfo
     **/
    void doConsentCreationWithoutOrgInfo(String payload) {

        // creation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .post(ccsConsentPath)

    }

    /**
     *  CCS consent creation
     **/
    void doConsentCreationWithImplicitAuth(String payload) {

        // creation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo",  CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .post(ccsConsentPath)


    }

    /**
     * Consent Retrieval.
     * @param consentId
     */
    void doConsentRetrieval() {

        //initiation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .baseUri(configuration.getCCSServerUrl())
                .get(ccsConsentPath + "/${consentId}")

    }

    /**
     * Consent Retrieval with wrong OrgInfo.
     * @param consentId
     */
    void doConsentRetrievalWithWrongOrgInfo() {

        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", "wrongOrgInfo")
                .baseUri(configuration.getCCSServerUrl())
                .get(ccsConsentPath + "/${consentId}")

    }


    /**
     * Consent Retrieval.
     * @param consentId
     */
    void doConsentRetrievalWithOutOrgInfo() {

        //initiation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .baseUri(configuration.getCCSServerUrl())
                .get(ccsConsentPath + "/${consentId}")

    }

    /**
     * Consent Status Update.
     * @param status
     */
    void doConsentStatusUpdate( String payload) {

        //initiation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .put(ccsConsentPath + "/${consentId}/status")

    }

    /**
     * Bulk Consent Initiation
     * @param status
     */
    void doBulkConsentInitiation( ) {


        CCSConsentPayload ccsConsentPayload = new CCSConsentPayload();
        randomPayloadsWithCount = ccsConsentPayload.getRandomPayloadsWithCount()
        List<String> payloads = randomPayloadsWithCount.get("payloads")
        payloads.forEach {
            doConsentCreationWithImplicitAuth(it)

        }

    }

    /**
     * Consent Search
     * @param consentStatuses
     * @param consentTypes
     * @param userIds
     * @param fromTime
     * @param toTime
     * @param offset
     * @param limit
     */

    void doConsentSearch(String consentStatuses, String consentTypes, String userIds, String clientIds,
                         String fromTime,  String toTime, String offset, String limit) {

        Map<String, String> queryParams = new HashMap<>();
        if (consentStatuses != null) queryParams.put("consentStatuses", consentStatuses);
        if (consentTypes != null) queryParams.put("consentTypes", consentTypes);
        if (userIds != null) queryParams.put("userIds", userIds);
        if (clientIds != null) queryParams.put("clientIds", clientIds);
        if (fromTime != null) queryParams.put("fromTime", fromTime);
        if (toTime != null) queryParams.put("toTime", toTime);
        if (offset != null) queryParams.put("offset", offset);
        if (limit != null) queryParams.put("limit", limit);

        RequestSpecification request = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .baseUri(configuration.getCCSServerUrl());

        queryParams.forEach(request::queryParam);

        consentResponse = request.get(ccsConsentPath);


    }

    /**
     * Consent Purge
     * @param consentId
     */
    void  doConsentPurge (String consentId) {

        //initiation
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .baseUri(configuration.getCCSServerUrl())
                .delete(ccsConsentPath + "/${consentId}")

    }

    /**
     * Consent Purge
     * @param payload
     */
    void doBulkConsentStatusUpdate (String payload){
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .put(ccsConsentPath + "/status")
    }

    void doConsentAmendment (String payload) {
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .put(ccsConsentPath + "/${consentId}")
    }


    /**
     * Consent Search
     * @param consentStatuses
     * @param consentTypes
     * @param userIds
     * @param fromTime
     * @param toTime
     * @param offset
     * @param limit
     */

    void doConsentHistoryRetrieval(String consentId, String detailed, String statusAuditId, String fromTime,
                                   String  toTime) {

        Map<String, String> queryParams = new HashMap<>();
        if (detailed != null) queryParams.put("detailed", detailed);
        if (statusAuditId != null) queryParams.put("statusAuditId", statusAuditId);
        if (fromTime != null) queryParams.put("fromTime", fromTime);
        if (toTime != null) queryParams.put("toTime", toTime);


        RequestSpecification request = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .baseUri(configuration.getCCSServerUrl())


        queryParams.forEach(request::queryParam);

        consentResponse = request.get(ccsConsentPath + "/${consentId}/history");


    }


    void doConsentRevoke(String payload) {
        consentResponse = consentRequestBuilder.buildBasicRequest()
                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
                .body(payload)
                .baseUri(configuration.getCCSServerUrl())
                .post(ccsConsentPath + "/${consentId}/revoke")
    }

//    void doConsentHistoryRetrieval(String consentId){
//        consentResponse = consentRequestBuilder.buildBasicRequest()
//                .header("OrgInfo", CCSConsentPayload.TEST_ORG_ID)
//                .baseUri(configuration.getCCSServerUrl())
//
//                .get(ccsConsentPath + "/${consentId}/history")
//    }
}