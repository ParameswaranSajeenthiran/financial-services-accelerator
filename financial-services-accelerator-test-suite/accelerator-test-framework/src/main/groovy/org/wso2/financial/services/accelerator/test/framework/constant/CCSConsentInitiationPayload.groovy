package org.wso2.financial.services.accelerator.test.framework.constant

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import net.minidev.json.JSONObject

import java.time.Duration
import java.time.OffsetDateTime
import java.util.Random

class CCSConsentPayload {
    public static final String TEST_ORG_ID = "testOrgId"
    public static final String TEST_CLIENT_ID = "testClientId"
    public static final String TEST_CONSENT_TYPE = "Accounts"
    public static final String TEST_CURRENT_STATUS = "awaitingAuthorisation"
    public static final long TEST_EXPIRY_TIME = ((System.currentTimeMillis())/1000)+100;
    public static final String TEST_RECURRING_INDICATOR = "true"
    public static final String TEST_CONSENT_FREQUENCY = "10"
    public static  final String TEST_ATTRIBUTE_A = "testValueA"
    public static  final String TEST_ATTRIBUTE_B = "testValueB"
    public static final String TEST_CONSENT_ATTRIBUTES =
            """
                                    {
                                              "testAttributeA": "${TEST_ATTRIBUTE_A}",
                                              "testAttributeB": "${TEST_ATTRIBUTE_B}"
                                            }
                                     """.stripIndent()
    public static final String TEST_RECEIPT = JsonOutput.toJson(AccountsRequestPayloads.initiationPayload).toString()

    public static final String TEST_CONSENT_ATTRIBUTES_WITH_CONSENT_FREQUENCY =
        """
        {
                  "testAttributeA": "testValueA",
                  "testAttributeB": "testValueB",
                    "consentFrequency": "10"
                }
        """.stripIndent()

    public static final String TEST_AUTHORIZATION_STATUS_A = "created"
    public static final String TEST_AUTHORIZATION_TYPE_A = "OAuth2"
    public static final String TEST_USER_ID_A = "testUserA"


    public static final String TEST_ACCOUNT_ID_A1 = "testAccountA1"
    public static final String TEST_PERMISSION_A1 = "read"
    public static final String TEST_RESOURCE_A1 =
        """
        {
          \"accountID\": \"${TEST_ACCOUNT_ID_A1}\",
          \"permission\": \"${TEST_PERMISSION_A1}\"
        }
        """

    public static final String TEST_ACCOUNT_ID_A2 = "testAccountA2"
    public static final String TEST_PERMISSION_A2 = "write"
    public static final String TEST_RESOURCE_A2 =
        """
        {
         \"accountID\": \"${TEST_ACCOUNT_ID_A2}\",
          \"permission\": \"${TEST_PERMISSION_A2}\"
        }
        """
    public static final String TEST_DEFAULT_AUTHORIZATION_RESOURCE_A =
            """
            {
                      "authorizationStatus": "${TEST_AUTHORIZATION_STATUS_A}",
                      "authorizationType": "${TEST_AUTHORIZATION_TYPE_A}",
                      "userId": "${TEST_USER_ID_A}",
                      "resource":[
                                ${TEST_RESOURCE_A1},
                                ${TEST_RESOURCE_A2}                                              
                                    ]
            }
            """.stripIndent()

    public static final String TEST_DEFAULT_AUTHORIZATION_RESOURCE_C =
            """
            {
                      "authorizationStatus": "${TEST_AUTHORIZATION_STATUS_A}",
                      "authorizationType": "${TEST_AUTHORIZATION_TYPE_A}",
                      "userId": "${TEST_USER_ID_A}",
                      "resource":[
                                ${TEST_RESOURCE_A1}
                                    ]
            }
            """.stripIndent()


    public static final String TEST_AUTHORIZATION_STATUS_B = "authorized"
    public static final String TEST_AUTHORIZATION_TYPE_B = "OAuth2"
    public static final String TEST_USER_ID_B = "testUserB"



    public static final String TEST_ACCOUNT_ID_B1 = "testAccountB1"
    public static final String TEST_PERMISSION_B1 = "read"
    public static final String TEST_RESOURCE_B1 =
        """
        {
          "accountID": "${TEST_ACCOUNT_ID_B1}",
          "permission": "${TEST_PERMISSION_B1}"
        }
        """
    public static final String TEST_ACCOUNT_ID_B2 = "testAccountB2"
    public static final String TEST_PERMISSION_B2 = "write"
    public static final String TEST_RESOURCE_B2 =
        """
        {
          "accountID": "${TEST_ACCOUNT_ID_B2}",
          "permission": "${TEST_PERMISSION_B2}"
        }
        """


    public static final String TEST_DEFAULT_AUTHORIZATION_RESOURCE_B =
        """
        {
                  "authorizationStatus": "${TEST_AUTHORIZATION_STATUS_B}",
                  "authorizationType": "${TEST_AUTHORIZATION_TYPE_B}",
                  "userId": "${TEST_USER_ID_B}",
                  "resource": [
                    ${TEST_RESOURCE_B1},
                    ${TEST_RESOURCE_B2}
                    
                  ]
                }
        """.stripIndent()


    public static String initiationPayload =
        """
        {
          "clientId": "${TEST_CLIENT_ID}",
          "consentType": "${TEST_CONSENT_TYPE}",
          "currentStatus": "${TEST_CURRENT_STATUS}",
          "receipt": ${TEST_RECEIPT},
          "expiryTime": ${TEST_EXPIRY_TIME} ,
          "recurringIndicator": ${TEST_RECURRING_INDICATOR},
          "consentAttributes": ${TEST_CONSENT_ATTRIBUTES}         
        }
        """.stripIndent()

    public static  TEST_AUTHORIZATION_RESOURCES =
            """[
            ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_A},
            ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_B}
    ]"""

    public static String initiationPayloadWithAuthResources =
            """
        {
          "clientId": "${TEST_CLIENT_ID}",
          "consentType": "${TEST_CONSENT_TYPE}",
          "currentStatus": "${TEST_CURRENT_STATUS}",
          "receipt": ${TEST_RECEIPT},
          "expiryTime": ${TEST_EXPIRY_TIME} ,
          "recurringIndicator": ${TEST_RECURRING_INDICATOR},
          "consentAttributes": ${TEST_CONSENT_ATTRIBUTES},
          "authorizationResources": ${TEST_AUTHORIZATION_RESOURCES}
          
        }
        """.stripIndent()

    public static String initiationPayloadWithAuthResource =
            """
        {
          "clientId": "${TEST_CLIENT_ID}",
          "consentType": "${TEST_CONSENT_TYPE}",
          "currentStatus": "${TEST_CURRENT_STATUS}",
          "receipt": ${TEST_RECEIPT},
          "expiryTime": ${TEST_EXPIRY_TIME} ,
          "recurringIndicator": ${TEST_RECURRING_INDICATOR},
          "consentAttributes": ${TEST_CONSENT_ATTRIBUTES},
          "authorizationResources":[
                    ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_C}
            ]

          
        }
        """.stripIndent()


    // payload without receipt
    public static  String invalidPayloadWithoutReceipt =
        """
        {
          "clientId": "${TEST_CLIENT_ID}",
          "consentType": "${TEST_CONSENT_TYPE}",
          "currentStatus": "${TEST_CURRENT_STATUS}",
          "expiryTime": ${(Duration.between(AccountsRequestPayloads.expirationDateTime,
            OffsetDateTime.now()).toDays()
            )} ,
          "recurringIndicator": ${TEST_RECURRING_INDICATOR},
          "consentAttributes": ${TEST_CONSENT_ATTRIBUTES},
          "authorizationResources": [
            ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_A},
            ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_B}
             ]
            
          
        }
        """.stripIndent()


    // payloads with invalid types
    public static String payloadWithInvalidRecurringIndicatorType = """
{
    "clientId": "${TEST_CLIENT_ID}",
    "consentType": "${TEST_CONSENT_TYPE}",
    "currentStatus": "${TEST_CURRENT_STATUS}",
    "receipt": ${JsonOutput.toJson(AccountsRequestPayloads.initiationPayload).toString()},
    "expiryTime": ${(Duration.between(AccountsRequestPayloads.expirationDateTime,
        OffsetDateTime.now()).toDays()
        )} ,
    "recurringIndicator": "invalid",
    "consentAttributes": ${TEST_CONSENT_ATTRIBUTES},
    "authorizationResources": [
        ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_A},
        ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_B}
         ]
    }
    """


        public static String payloadWithInvalidConsentAttributesType = """
    {
    "clientId": "${TEST_CLIENT_ID}",
    "consentType": "${TEST_CONSENT_TYPE}",
    "currentStatus": "${TEST_CURRENT_STATUS}",
    "receipt": ${JsonOutput.toJson(AccountsRequestPayloads.initiationPayload).toString()},
    "expiryTime": ${(Duration.between(AccountsRequestPayloads.expirationDateTime,
        OffsetDateTime.now()).toDays()
        )} ,
    "recurringIndicator": ${TEST_RECURRING_INDICATOR},
    "consentAttributes": "invalid",
    "authorizationResources": [
        ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_A},
        ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_B}
         ]
    }
    """



    public static String payloadWithoutConsentAttributes =
            """
            {
            "clientId": "${TEST_CLIENT_ID}",
            "consentType": "${TEST_CONSENT_TYPE}",
            "currentStatus": "${TEST_CURRENT_STATUS}",
            "receipt": ${JsonOutput.toJson(AccountsRequestPayloads.initiationPayload).toString()},
            "expiryTime": ${TEST_EXPIRY_TIME} ,
            "recurringIndicator": ${TEST_RECURRING_INDICATOR},
            "authorizationResources": [
                ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_A},
                ${TEST_DEFAULT_AUTHORIZATION_RESOURCE_B}
                ]
            }
            """

    // authoirzation resource



    // payload for status update for a consent

    public static String TEST_UPDATED_STATUS = "testUpdatedStatus"
    public static String TEST_UPDATE_REASON = "testReason"
    public static String statusUpdatePayload =
        """
        {
          "status": "${TEST_UPDATED_STATUS}",
          "reason": "${TEST_UPDATE_REASON}",
          "userId": "${TEST_USER_ID_A}"
        }
        """.stripIndent()

    public static String statusUpdatePayloadWithoutStatus =
            """
        {
          "status": 4,
          "reason": "${TEST_UPDATE_REASON}",
          "userId": "${TEST_USER_ID_A}"
        }
        """.stripIndent()


    // invalid consentId
    public static String INVALID_CONSENT_ID = "invalidConsentId"

    private  List<String> TEST_CONSENT_TYPE_BULK = Arrays.asList("Accounts-" + UUID.randomUUID(),
            "Transactions-" + UUID.randomUUID(),
            "Balances-" + UUID.randomUUID());
    private List<String> TEST_CURRENT_STATUS_BULK = Arrays.asList("awaitingAuthorisation-" + UUID.randomUUID(),
            "authorised-"+ UUID.randomUUID(), "rejected-"+ UUID.randomUUID());
    private static final List<Long> TEST_EXPIRY_TIME_LIST = Arrays.asList(
            (System.currentTimeSeconds() + 100),
            (System.currentTimeSeconds() + 200),
            (System.currentTimeSeconds() + 300));
    private static final List<String> TEST_RECURRING_INDICATOR_BULK = Arrays.asList("true", "false");
    private static final List<Map<String, String>> TEST_CONSENT_ATTRIBUTES_BULK = Arrays.asList(
            Collections.singletonMap("attr1", "value1"),
            Collections.singletonMap("attr2", "value2")
    );

    private List<String> TEST_CLIENT_IDS = Arrays.asList("client1-"+ UUID.randomUUID(), "client2-"+ UUID.randomUUID(),
            "client3-"+ UUID.randomUUID());
    private  List<String> TEST_USER_IDS = Arrays.asList("userA-"+ UUID.randomUUID(), "userB-"+ UUID.randomUUID(),
            "userC-"+ UUID.randomUUID());

    private static final Map<String, String> SAMPLE_RECEIPT = Collections.singletonMap("account", "123456789");
    private static final Random RANDOM = new Random();

    private static <T> T getRandomElement(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public  Map getRandomPayloadsWithCount() {
        List<String> payloads = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // Tracking occurrences
        Map<String, Integer> consentTypeCount = new HashMap<>();
        Map<String, Integer> statusCount = new HashMap<>();
        Map<String, Integer> clientIdCount = new HashMap<>();
        Map<String, Integer> userIdCount = new HashMap<>();
        Map<String, Integer> combinationCount = new HashMap<>();

        for (int i = 0; i < 30; i++) {
            try {
                Map<String, Object> payloadMap = new HashMap<>();
                String clientId = getRandomElement(this.TEST_CLIENT_IDS);
                String userId = getRandomElement(this.TEST_USER_IDS);
                String consentType = getRandomElement(this.TEST_CONSENT_TYPE_BULK);
                String currentStatus = getRandomElement(this.TEST_CURRENT_STATUS_BULK);

                payloadMap.put("clientId", clientId);
                payloadMap.put("consentType", consentType);
                payloadMap.put("currentStatus", currentStatus);
                payloadMap.put("receipt", TEST_RECEIPT);
                payloadMap.put("expiryTime", getRandomElement(TEST_EXPIRY_TIME_LIST));
                payloadMap.put("recurringIndicator", getRandomElement(TEST_RECURRING_INDICATOR_BULK));
                payloadMap.put("consentAttributes", getRandomElement(TEST_CONSENT_ATTRIBUTES_BULK));

                ArrayList <Map> authorizationResources = new ArrayList<>();
                Map authorizationResourceA = new HashMap();
                authorizationResourceA.put("authorizationStatus", TEST_AUTHORIZATION_STATUS_A);
                authorizationResourceA.put("authorizationType", TEST_AUTHORIZATION_TYPE_A);
                authorizationResourceA.put("userId", userId);
                JSONObject resource = new JSONObject();
                resource.put("accountID", TEST_ACCOUNT_ID_A1);
                resource.put("permission", TEST_PERMISSION_A1);
                authorizationResourceA.put("resource", Arrays.asList(resource));
                authorizationResources.add(authorizationResourceA);


                payloadMap.put("authorizationResources", authorizationResources)
                // Update counters
                consentTypeCount.put(consentType, consentTypeCount.getOrDefault(consentType, 0) + 1);
                statusCount.put(currentStatus, statusCount.getOrDefault(currentStatus, 0) + 1);
                clientIdCount.put(clientId, clientIdCount.getOrDefault(clientId, 0) + 1);
                userIdCount.put(userId, userIdCount.getOrDefault(userId, 0) + 1);



                String combinationKey = clientId + "-" + userId + "-" + consentType + "-" + currentStatus;
                combinationCount.put(combinationKey, combinationCount.getOrDefault(combinationKey, 0) + 1);

                String jsonPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payloadMap);
                payloads.add(jsonPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       Map<String, Map<String, Integer>> payloadsWithCount = new HashMap<>();
        payloadsWithCount.put("consentTypeCount", consentTypeCount);
        payloadsWithCount.put("statusCount", statusCount);
        payloadsWithCount.put("clientIdCount", clientIdCount);
        payloadsWithCount.put("userIdCount", userIdCount);
        payloadsWithCount.put("combinationCount", combinationCount);
        payloadsWithCount.put("payloads", payloads);

        return payloadsWithCount;
    }
    public static String queryByClientIdAndBulkStatusUpdatePayload (String clientId, String status) {
        return """
            {
            "clientId": "${clientId}",     
          "status": "${status}",
          "reason": "${TEST_UPDATE_REASON}",
          "userId": "${TEST_USER_ID_A}" 
            }
        """
    }

    public static String queryByConsentTypeAndBulkStatusUpdatePayload(String consentType, String status) {
        return """
            {
            "consentType": "${consentType}",
          "status": "${status}",     
          "reason": "${TEST_UPDATE_REASON}",
          "userId": "${TEST_USER_ID_A}" 
            }
        """
    }


    public static String  queryByApplicableStatusesAndBulkStatusUpdatePayload (String applicableStatus, String status) {
        return """
            {
            "applicableStatusesForStateChange": ["${applicableStatus}"],
          "status": "${status}",     
          "reason": "${TEST_UPDATE_REASON}",
          "userId": "${TEST_USER_ID_A}" 
            }
        """
    }



    public static String sampleAmendResource(String resourceMapping, String consentMappingStatus ,String resource){

        return """
        {
            "resource": ${resource},
            "resourceMappingId": "${resourceMapping}",
            "consentMappingStatus": "${consentMappingStatus}"
        }
        """.stripIndent()
    }

    public static sampleNewResource(String resource) {
        return """
        {
            "resource": ${resource}
        }
        """.stripIndent()
    }

    public static String sampleAmendAuthorizationResource(
            String authorizationId, String authorizationStatus, String authorizationType,  String userId,
            String  resource) {
        return """
        {
            "authId": "${authorizationId}",
            "authorizationStatus": "${authorizationStatus}",
            "authorizationType": "${authorizationType}",
            "userId": "${userId}",
            "resource": ${resource}
        }
        """.stripIndent()
    }

    public static String sampleNewAmendAuthorizationResource(
             String authorizationStatus, String authorizationType,  String userId,
            String  resource) {
        return """
        {
            "authorizationStatus": "${authorizationStatus}",
            "authorizationType": "${authorizationType}",
            "userId": "${userId}",
            "resource": ${resource}
        }
        """.stripIndent()
    }

    public static String amendConsentPayload(String receipt, String expiryTime, String recurringIndicator,
                                             String consentAttributes, GString authorizationResources) {

        return """
        {
            "currentStatus": "${TEST_CURRENT_STATUS}",
            "receipt": ${receipt},
            "expiryTime": ${expiryTime} ,
            "consentAttributes": ${consentAttributes},
            "authorizationResources": ${authorizationResources}

        }
        """.stripIndent()
    }

    public static String amendConsentPayloadWithoutAuthorization(String receipt, String expiryTime,
                                                                 String recurringIndicator,
                                                                 String consentAttributes) {

        return """
        {
            "currentStatus": "${TEST_CURRENT_STATUS}",
            "receipt": ${receipt},
            "expiryTime": ${expiryTime} ,
            "consentAttributes": ${consentAttributes}

        }
        """.stripIndent()
    }

    public static String revokeConsentPayload(String reason, String userId) {
        return """
                {
                "reason": "${reason}",
                "userId": "${userId}"
                }
        """.stripIndent()
    }



}
