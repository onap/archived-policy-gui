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

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;

@Getter
public class UploadPluginConfigParameters implements ParameterGroup {

    public static final String GROUP_NAME = "UploadParameters";
    @Setter
    private String name;
    private boolean isEnabled;
    private String url;

    public UploadPluginConfigParameters() {
        this.name = GROUP_NAME;
        initProperties();
    }

    private void initProperties() {
        final String isEnabledProperty = System.getProperty(PolicyUploadPluginConfigKey.ENABLE.getKey());
        isEnabled = Boolean.parseBoolean(isEnabledProperty);
        url = System.getProperty(PolicyUploadPluginConfigKey.URL.getKey());
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);
        if (isEnabled && StringUtils.isEmpty(url)) {
            result.setResult("url", ValidationStatus.INVALID,
                String.format("The URL for the upload endpoint must be provided as the java property '%s'",
                    PolicyUploadPluginConfigKey.URL.getKey()));
        }

        return result;
    }

    /**
     * Gets a property value based on the key and type.
     *
     * @param <T> represents the class type
     * @param key the property key
     * @return the property value if it exists
     */
    public <T> Optional<T> getValue(final PolicyUploadPluginConfigKey key) {
        final Class<?> type = key.getType();
        if (key == PolicyUploadPluginConfigKey.URL && type.isInstance(url)) {
            return (Optional<T>) Optional.of(type.cast(url));
        }
        if (key == PolicyUploadPluginConfigKey.ENABLE && type.isInstance(isEnabled)) {
            return (Optional<T>) Optional.of(type.cast(isEnabled));
        }
        return Optional.empty();
    }

}
