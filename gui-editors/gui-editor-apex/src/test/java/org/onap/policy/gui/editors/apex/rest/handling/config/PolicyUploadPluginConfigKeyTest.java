/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.gui.editors.apex.rest.handling.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PolicyUploadPluginConfigKeyTest {

    @Test
    public void getKeyUrl() {
        final var actual = PolicyUploadPluginConfigKey.URL.getKey();
        assertThat(actual).isEqualTo("plugin.policy.upload.url");
    }

    @Test
    public void getTypeUrl() {
        final var actual = PolicyUploadPluginConfigKey.URL.getType();
        assertThat(actual).isEqualTo(String.class);
    }

    @Test
    public void getKeyEnable() {
        final var actual = PolicyUploadPluginConfigKey.ENABLE.getKey();
        assertThat(actual).isEqualTo("plugin.policy.upload.enable");
    }

    @Test
    public void getTypeEnable() {
        final var actual = PolicyUploadPluginConfigKey.ENABLE.getType();
        assertThat(actual).isEqualTo(Boolean.class);
    }
}
