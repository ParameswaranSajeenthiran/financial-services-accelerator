/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.financial.services.accelerator.consent.mgt.extensions.manage.model;

/**
 * ExternalAPIPreConsentGenerateRequestDTO
 */
public class ExternalAPIPreConsentGenerateRequestDTO {

    //Payload can either be a JSONObject or a JSONArray
    private Object consentPayload;
    private String resourcePath;

    public ExternalAPIPreConsentGenerateRequestDTO(ConsentManageData consentManageData) {

        this.consentPayload = consentManageData.getPayload();
        this.resourcePath = consentManageData.getRequestPath();
    }

    public Object getConsentPayload() {
        return consentPayload;
    }

    public void setConsentPayload(Object consentPayload) {
        this.consentPayload = consentPayload;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

}
