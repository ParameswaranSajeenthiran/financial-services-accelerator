<!--
 ~ Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 ~
 ~ WSO2 LLC. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied. See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->

# WSO2 Financial Services Accelerator 

Financial Services Accelerator is a collection of technologies that increases the speed and reduces the complexity of adopting open banking compliance.

### Building from the source

If you want to build the Financial Services Accelerator from the source code:

1. Install Java8 or above.
2. Install [Apache Maven 3.0.5](https://maven.apache.org/download.cgi) or above.
3. Get the Financial Services Accelerator from [this repository](https://github.com/wso2/financial-services-accelerator.git) by **cloning** or **downloading** the repository as a zip.
    * To **clone the solution**, copy the URL and execute the following command in a command prompt.
      `git clone <the copiedURL>`. After cloning, checkout to the **main** branch.
    * To **download the repository**, select the **main** branch first, then click **Download ZIP** and unzip the downloaded file.
4. Navigate to the cloned/downloaded repository using a command prompt and run the relevant Maven command:

| Command                             | Description                                                                                                                                                                                                |
|:------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```mvn install```                   | This starts building the repository without cleaning the folders.                                                                                                                                          |
| ```mvn clean install```             | This cleans the folders and starts building the repository from scratch.                                                                                                                                   |
| ```mvn clean install -P solution``` | This cleans the folders and starts building the repository and the accelerator packs from scratch, finally it creates the accelerator zip files containing the artifacts required to setup the deployment. |

5. Once the packs are created, navigate to the relevant folder to get the accelerator for each product:

| Product                                 | Toolkit Path                                                    |
|:----------------------------------------|:----------------------------------------------------------------|
| ```Identity Server Accelerator```       | `/financial-services-accelerator/accelerators/fs-is/target`     |
| ```API Manager Accelerator```           | `/financial-services-accelerator/accelerators/fs-apim/target`   |

### Installing and Running OB Accelerators

Please refer the following README files to run the products:

| Product                           | Instructions Path                         |
|:----------------------------------|:------------------------------------------|
| ```Identity Server Accelerator``` | `/wso2-fsiam-accelerator-4.0.0/README.md` |
| ```API Manager Accelerator```     | `/wso2-fsam-accelerator-4.0.0/README.md`  |

### Reporting Issues

We encourage you to report issues, documentation faults, and feature requests regarding the Financial Services Accelerator through the [WSO2 Financial Services Accelerator Issue Tracker](https://github.com/wso2/financial-services-accelerator/issues).

### License

This source is licensed under the Apache License Version 2.0 ([LICENSE](LICENSE)).
