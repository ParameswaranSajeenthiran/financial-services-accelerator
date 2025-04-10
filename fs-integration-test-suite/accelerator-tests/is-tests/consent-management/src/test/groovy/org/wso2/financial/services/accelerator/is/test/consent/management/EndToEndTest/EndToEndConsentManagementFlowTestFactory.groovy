/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.financial.services.accelerator.is.test.consent.management.EndToEndTest

import org.testng.annotations.Factory
import org.wso2.financial.services.accelerator.test.framework.constant.ConsentDataProviders

/**
 * Test Factory for end to end consent Management.
 */
class EndToEndConsentManagementFlowTestFactory {

    @Factory(dataProvider = "ConsentTypes", dataProviderClass = ConsentDataProviders.class)
    Object[] getTestClasses(Map<String, String> map) {

        return [new EndToEndConsentManagementFlowTest(map)]
    }
}
