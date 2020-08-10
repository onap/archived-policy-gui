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

package org.onap.policy.gui.editors.apex.rest.handling;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.gui.editors.apex.rest.UploadPluginConfigParameters;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles endpoints for the configuration properties.
 */
@Path("editor/config")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ConfigurationRestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationRestResource.class);
    private static final StandardCoder STANDARD_CODER = new StandardCoder();

    private final UploadPluginConfigParameters uploadConfigParam;

    public ConfigurationRestResource() {
        uploadConfigParam = ParameterService.get(UploadPluginConfigParameters.GROUP_NAME);
    }

    /**
     * Gets the configured properties.
     *
     * @return the properties as JSON in the ApexApiResult messages list.
     */
    @GET
    @Path("")
    public ApexApiResult show() {
        final ApexApiResult result = new ApexApiResult(Result.SUCCESS);

        final Map<String, Object> configMap = Stream.of(PolicyUploadPluginConfigKey.values())
            .filter(key -> uploadConfigParam.getValue(key).isPresent())
            .collect(Collectors.toMap(PolicyUploadPluginConfigKey::getKey,
                configKey -> uploadConfigParam.getValue(configKey).get()));
        try {
            final String encode = STANDARD_CODER.encode(configMap);
            result.addMessage(encode);
        } catch (final CoderException e) {
            result.setResult(Result.FAILED);
            final String errorMsg = "Could not parse configuration parameters as JSON";
            result.addMessage(errorMsg);
            LOGGER.error(errorMsg, e);
        }
        return result;
    }

}
