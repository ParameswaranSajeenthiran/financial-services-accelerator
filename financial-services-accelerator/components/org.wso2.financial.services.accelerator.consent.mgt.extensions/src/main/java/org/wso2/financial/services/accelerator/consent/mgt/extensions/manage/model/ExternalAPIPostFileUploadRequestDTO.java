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
 * ExternalAPIPostFileUploadRequestDTO
 */
public class ExternalAPIPostFileUploadRequestDTO {

    private String consentId;
    private String fileUploadedCreatedTime;


    public ExternalAPIPostFileUploadRequestDTO(String consentId, String fileUploadedCreatedTime) {
        this.consentId = consentId;
        this.fileUploadedCreatedTime = fileUploadedCreatedTime;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getFileUploadedCreatedTime() {
        return fileUploadedCreatedTime;
    }

    public void setFileUploadedCreatedTime(String fileUploadedCreatedTime) {
        this.fileUploadedCreatedTime = fileUploadedCreatedTime;
    }
}
