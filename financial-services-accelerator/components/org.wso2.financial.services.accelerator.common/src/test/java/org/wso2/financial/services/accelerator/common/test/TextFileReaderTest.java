/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.financial.services.accelerator.common.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.financial.services.accelerator.common.config.TextFileReader;

import java.io.IOException;

/**
 * Text file reader test.
 */
public class TextFileReaderTest {

    private static final Log logger = LogFactory.getLog(TextFileReader.class);

    @Test
    public void testReadFile() {

        try {
            TextFileReader textFileReader = TextFileReader.getInstance();
            textFileReader.setDirectoryPath("src/test/resources");
            String file = textFileReader.readFile("testFile.js");
            Assert.assertNotNull(file);
        } catch (IOException e) {
            logger.error("Error while reading file", e);
        }
    }

    @Test
    public void testRetrieveFileFromMap() {

        try {
            TextFileReader textFileReader = TextFileReader.getInstance();
            String file = textFileReader.readFile("testFile.js");
            Assert.assertNotNull(file);
        } catch (IOException e) {
            logger.error("Error while reading file", e);
        }
    }

    @Test(description = "test whether empty string is returned when trying to retrieve non existing file")
    public void testRetrieveWrongFile() {

        try {
            TextFileReader textFileReader = TextFileReader.getInstance();
            textFileReader.setDirectoryPath("src/test/resources");
            String file = textFileReader.readFile("testFileOne.js");
            Assert.assertEquals(file, "");
        } catch (IOException e) {
            logger.error("Error while reading file", e);
        }
    }

}
