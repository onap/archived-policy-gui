/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.gui.editors.apex.rest;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.Test;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;

public class UploadPluginConfigParametersTest {

    @Test
    public void setupPropertiesTest() {
        final String expectedUrl = "aUrl";
        final boolean expectedEnabled = true;
        System.setProperty(PolicyUploadPluginConfigKey.URL.getKey(), expectedUrl);
        System.setProperty(PolicyUploadPluginConfigKey.ENABLE.getKey(), String.valueOf(expectedEnabled));
        final UploadPluginConfigParameters uploadPluginConfigParameters = new UploadPluginConfigParameters();
        final String url = uploadPluginConfigParameters.getUrl();
        final Boolean isEnabled = uploadPluginConfigParameters.isEnabled();
        assertThat(url).isEqualTo(expectedUrl);
        assertThat(isEnabled).isEqualTo(expectedEnabled);
    }

    @Test
    public void testGetValue() {
        final String expectedUrl = "aUrl";
        final boolean expectedEnabled = true;
        System.setProperty(PolicyUploadPluginConfigKey.URL.getKey(), expectedUrl);
        System.setProperty(PolicyUploadPluginConfigKey.ENABLE.getKey(), String.valueOf(expectedEnabled));
        UploadPluginConfigParameters uploadPluginConfigParameters = new UploadPluginConfigParameters();
        Optional<String> actualUrl = uploadPluginConfigParameters.getValue(PolicyUploadPluginConfigKey.URL);
        assertThat(actualUrl).isPresent().contains(expectedUrl);
        Optional<Boolean> actualEnabled = uploadPluginConfigParameters.getValue(PolicyUploadPluginConfigKey.ENABLE);
        assertThat(actualEnabled).isPresent().contains(expectedEnabled);

        System.clearProperty(PolicyUploadPluginConfigKey.URL.getKey());
        uploadPluginConfigParameters = new UploadPluginConfigParameters();
        actualUrl = uploadPluginConfigParameters.getValue(PolicyUploadPluginConfigKey.URL);
        assertThat(actualUrl).isNotPresent();

        System.clearProperty(PolicyUploadPluginConfigKey.ENABLE.getKey());
        uploadPluginConfigParameters = new UploadPluginConfigParameters();
        actualEnabled = uploadPluginConfigParameters.getValue(PolicyUploadPluginConfigKey.ENABLE);
        assertThat(actualEnabled).isPresent();
        assertThat(actualEnabled.get()).isFalse();
    }

    @Test
    public void testValidate() {
        final String expectedUrl = "aUrl";
        final boolean expectedEnabled = true;
        System.setProperty(PolicyUploadPluginConfigKey.URL.getKey(), expectedUrl);
        System.setProperty(PolicyUploadPluginConfigKey.ENABLE.getKey(), String.valueOf(expectedEnabled));
        UploadPluginConfigParameters uploadPluginConfigParameters = new UploadPluginConfigParameters();
        assertThat(uploadPluginConfigParameters.isValid()).isTrue();

        System.clearProperty(PolicyUploadPluginConfigKey.URL.getKey());
        uploadPluginConfigParameters = new UploadPluginConfigParameters();
        assertThat(uploadPluginConfigParameters.isValid()).isFalse();

        System.clearProperty(PolicyUploadPluginConfigKey.ENABLE.getKey());
        uploadPluginConfigParameters = new UploadPluginConfigParameters();
        assertThat(uploadPluginConfigParameters.isValid()).isTrue();
    }
}