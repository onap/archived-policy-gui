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

package org.onap.policy.gui.editors.apex.rest.handling.converter.tosca;

import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICY_TYPE_IMPL;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.PROPERTIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOPOLOGY_TEMPLATE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.JsonObject;
import java.util.Optional;
import lombok.Getter;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;

/**
 * Handles the conversion from policy JSON to policy YAML.
 */
public class PolicyToscaConverter {

    private final StandardCoder standardCoder;

    public PolicyToscaConverter(final StandardCoder standardCoder) {
        this.standardCoder = standardCoder;
    }

    /**
     * Converts the policy model to TOSCA, by merging the 3 given JSON models.
     *
     * @param policyModelJsonString the policy model JSON
     * @param apexConfigJsonString the apex config JSON
     * @param toscaTemplateJsonString the base TOSCA template in JSON format
     * @return the merged policy model in YAML format
     * @throws PolicyToscaConverterException when a JSON string could not be parsed to JsonObject or the resulting JSON
     *     could not be parsed to YAML.
     */
    public Optional<String> convert(final String policyModelJsonString, final String apexConfigJsonString,
                                    final String toscaTemplateJsonString) throws PolicyToscaConverterException {
        final JsonObject apexConfigJson = decodeToJson(apexConfigJsonString);
        final JsonObject policyModelJson = decodeToJson(policyModelJsonString);
        final JsonObject toscaTemplateJson = decodeToJson(toscaTemplateJsonString);

        final JsonObject toscaPolicyProperties = toscaTemplateJson.get(TOPOLOGY_TEMPLATE.getKey()).getAsJsonObject();
        final JsonObject toscaPolicy = toscaPolicyProperties.get(POLICIES.getKey()).getAsJsonArray()
            .get(0).getAsJsonObject();
        final JsonObject toscaProperties = toscaPolicy.get(toscaPolicy.keySet().toArray()[0].toString())
            .getAsJsonObject()
            .get(PROPERTIES.getKey()).getAsJsonObject();

        apexConfigJson.entrySet().forEach(entry -> {
            if (ENGINE_SERVICE_PARAMETERS.getKey().equals(entry.getKey())) {
                entry.getValue().getAsJsonObject().add(POLICY_TYPE_IMPL.getKey(), policyModelJson);
            }
            toscaProperties.add(entry.getKey(), entry.getValue());
        });

        final String toscaPolicyString = encodeToString(toscaTemplateJson);
        return Optional.ofNullable(convertJsonToYaml(toscaPolicyString));
    }

    private String encodeToString(final JsonObject toscaTemplateJson) throws PolicyToscaConverterException {
        try {
            return standardCoder.encode(toscaTemplateJson);
        } catch (final CoderException e) {
            throw new PolicyToscaConverterException(
                String.format("Could not convert JSON to JSON string:%n%s", toscaTemplateJson), e);
        }
    }

    private JsonObject decodeToJson(final String jsonString) throws PolicyToscaConverterException {
        try {
            return standardCoder.decode(jsonString, JsonObject.class);
        } catch (final CoderException e) {
            throw new PolicyToscaConverterException(
                String.format("Could not convert JSON string to JSON:%n%s", jsonString), e);
        }
    }

    private String convertJsonToYaml(final String json) throws PolicyToscaConverterException {
        try {
            final JsonNode jsonNodeTree = new ObjectMapper().readTree(json);
            return new YAMLMapper().writeValueAsString(jsonNodeTree);
        } catch (final JsonProcessingException e) {
            throw new PolicyToscaConverterException(
                String.format("Could not convert JSON string to YAML:%n%s", json), e);
        }
    }

    @Getter
    public enum ToscaKey {
        TOPOLOGY_TEMPLATE("topology_template"),
        TOSCA_DEFINITIONS_VERSION("tosca_definitions_version"),
        PROPERTIES("properties"),
        ENGINE_SERVICE_PARAMETERS("engineServiceParameters"),
        POLICY_TYPE_IMPL("policy_type_impl"),
        POLICIES("policies");

        private final String key;

        ToscaKey(final String key) {
            this.key = key;
        }
    }

}
