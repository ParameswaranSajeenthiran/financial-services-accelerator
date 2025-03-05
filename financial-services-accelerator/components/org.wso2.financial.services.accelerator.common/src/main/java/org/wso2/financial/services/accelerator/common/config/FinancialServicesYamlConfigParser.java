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
package org.wso2.financial.services.accelerator.common.config;

import org.wso2.financial.services.accelerator.common.exception.FinancialServicesRuntimeException;
import org.wso2.financial.services.accelerator.common.policy.FSPolicy;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Config parser to read the financial-services.yaml.
 */
public class FinancialServicesYamlConfigParser {

    private static final String CONFIG_FILE_NAME = "financial-services.yaml";

    public static Map<String, Object> parseConfig() {
        // Get the Carbon Home system property
        String carbonHome = System.getProperty("carbon.home");
        if (carbonHome == null) {
            throw new FinancialServicesRuntimeException("Carbon home is not set.");
        }

        // Construct the file path
        Path filePath = Paths.get(carbonHome, "repository", "conf", CONFIG_FILE_NAME);

        // Ensure the file exists before attempting to read
        if (!Files.exists(filePath)) {
            throw new FinancialServicesRuntimeException("Financial Services YAML configuration file not found at: " +
                    filePath);
        }

        try {
            // Read YAML file into a String
            String yamlContent = Files.readString(filePath);
            LoaderOptions options = new LoaderOptions();
            options.setMaxAliasesForCollections(100);
            Yaml yaml = new Yaml(options);
            return yaml.load(yamlContent);
        } catch (IOException e) {
            throw new FinancialServicesRuntimeException("Error reading YAML configuration file: " + filePath, e);
        }
    }

    public static List<FSPolicy> getPolicies(String apiName, String path, String operation, String flow) {
        List<FSPolicy> policyWithPropertiesList = new ArrayList<>();
        Map<String, Object> config = parseConfig();

        List<Map<String, Object>> apis = (List<Map<String, Object>>) config.get("apis");
        if (apis == null) {
            return policyWithPropertiesList;
        }

        // Find the API by name
        Optional<Map<String, Object>> apiOpt = apis.stream()
                .filter(api -> apiName.equals(api.get("name")))
                .findFirst();
        if (apiOpt.isEmpty()) {
            return policyWithPropertiesList;
        }

        Map<String, Object> api = apiOpt.get();
        Map<String, Object> paths = (Map<String, Object>) api.get("paths");
        if (paths == null) {
            return policyWithPropertiesList;
        }

        // Find the path, operation, and flow
        Map<String, Object> pathConfig = (Map<String, Object>) paths.get(path);
        if (pathConfig == null) {
            return policyWithPropertiesList;
        }

        Map<String, Object> operationFlow = (Map<String, Object>) pathConfig.get(operation.toLowerCase(Locale.ROOT));
        if (operationFlow == null) {
            return policyWithPropertiesList;
        }

        Map<String, Object> flowMap = (Map<String, Object>) operationFlow.get(flow);
        if (flowMap == null) {
            return policyWithPropertiesList;
        }

        List<Object> policies = (List<Object>) flowMap.get("policies");
        if (policies == null) {
            return policyWithPropertiesList;
        }

        Map<String, Object> components = (Map<String, Object>) config.get("components");
        if (components == null) {
            return policyWithPropertiesList;
        }

        // Create policy instances
        for (Object policyDefinition : policies) {
            String policyClassName = (String) ((Map<String, Object>) policyDefinition).get("class");
            Map<String, Object> parameters = (Map<String, Object>)
                    ((Map<String, Object>) policyDefinition).get("parameters");

            try {
                Class<?> policyClass = Class.forName(policyClassName);
                Constructor<?> constructor = policyClass.getConstructor();
                FSPolicy policyInstance = (FSPolicy) constructor.newInstance();
                policyInstance.setPropertyMap(parameters);
                policyWithPropertiesList.add(policyInstance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Error instantiating policy class: " + policyClassName, e);
            }
        }

        return policyWithPropertiesList;
    }
}
