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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.Getter;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.coder.YamlJsonTranslator;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;

/**
 * Handles the conversion from policy JSON to policy YAML.
 */
public class PolicyToscaConverter {

    private final StandardCoder standardCoder;
    private final YamlJsonTranslator yamlJsonTranslator;

    /**
     * Creates a policy tosca converter.
     *
     * @param standardCoder the encoder that will handle JSON conversions
     * @param yamlJsonTranslator the translator that will handle YAML conversions
     */
    public PolicyToscaConverter(final StandardCoder standardCoder, final YamlJsonTranslator yamlJsonTranslator) {
        this.standardCoder = standardCoder;
        this.yamlJsonTranslator = yamlJsonTranslator;
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

        final JsonObject toscaPolicyProperties = readTopologyTemplateAsJsonObject(toscaTemplateJson);
        final JsonObject toscaPolicy = readToscaPolicyAsJsonObject(toscaPolicyProperties);
        final JsonObject toscaProperties = readPolicyPropertiesAsJsonObject(toscaPolicy);

        for (final Entry<String, JsonElement> entry : apexConfigJson.entrySet()) {
            if (ENGINE_SERVICE_PARAMETERS.getKey().equals(entry.getKey())) {
                final JsonObject engineServiceParameters = readEngineServiceParametersAsJsonObject(entry.getValue());
                engineServiceParameters.add(POLICY_TYPE_IMPL.getKey(), policyModelJson);
            }
            toscaProperties.add(entry.getKey(), entry.getValue());
        }
        return Optional.ofNullable(convertToYaml(toscaTemplateJson));
    }

    private JsonObject readTopologyTemplateAsJsonObject(final JsonObject toscaTemplateJson)
        throws PolicyToscaConverterException {

        try {
            return toscaTemplateJson.get(TOPOLOGY_TEMPLATE.getKey()).getAsJsonObject();
        } catch (final Exception e) {
            throw new PolicyToscaConverterException(
                String.format("Could not read the '%s' entry in the Tosca Template", TOPOLOGY_TEMPLATE.getKey()), e);
        }
    }

    private JsonObject readEngineServiceParametersAsJsonObject(final JsonElement engineServiceParametersEntry)
        throws PolicyToscaConverterException {

        try {
            return engineServiceParametersEntry.getAsJsonObject();
        } catch (final Exception e) {
            throw new PolicyToscaConverterException(
                String.format("Could not read the '%s' in the Apex Config", ENGINE_SERVICE_PARAMETERS.getKey()), e);
        }
    }

    private JsonObject readToscaPolicyAsJsonObject(final JsonObject toscaPolicyProperties)
        throws PolicyToscaConverterException {

        try {
            return toscaPolicyProperties.get(POLICIES.getKey()).getAsJsonArray().get(0).getAsJsonObject();
        } catch (final Exception e) {
            throw new PolicyToscaConverterException(
                String.format("Could not read the first policy in the '%s' entry under '%s'",
                    POLICIES.getKey(), TOPOLOGY_TEMPLATE.getKey()), e);
        }
    }

    private JsonObject readPolicyPropertiesAsJsonObject(final JsonObject toscaPolicy)
        throws PolicyToscaConverterException {

        try {
            final String policyObjectKey = toscaPolicy.keySet().iterator().next();
            final JsonObject policyEntry = toscaPolicy.get(policyObjectKey).getAsJsonObject();
            return policyEntry.get(PROPERTIES.getKey()).getAsJsonObject();
        } catch (final Exception e) {
            throw new PolicyToscaConverterException(
                String.format("Could not read the policy '%s' entry", PROPERTIES.getKey()), e);
        }
    }

    private String convertToYaml(final JsonObject jsonObject) throws PolicyToscaConverterException {
        try {
            return yamlJsonTranslator.toYaml(jsonObject);
        } catch (final Exception e) {
            throw new PolicyToscaConverterException(
                String.format("Could not convert JSON Object to YAML:%n%s", jsonObject.toString()), e);
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
