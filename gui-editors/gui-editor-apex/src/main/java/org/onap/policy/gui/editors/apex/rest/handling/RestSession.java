/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.gui.editors.apex.rest.handling;

import java.util.Map;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.coder.StandardYamlCoder;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.gui.editors.apex.rest.handling.plugin.upload.PolicyUploadHandler;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaServiceTemplate;
import org.onap.policy.models.tosca.utils.ToscaUtils;

/**
 * This class represents an ongoing editor session in the Apex editor and holds the information for the session.
 *
 */
public class RestSession {
    // The default TOSCA wrapper for an APEX policy
    private static final String APEX_TOSCA_POLICY_TEMPLATE = "templates/ApexToscaPolicyTemplate.yaml";

    // Recurring string constants
    private static final String ENGINE_SERVICE_PARAMETERS = "engineServiceParameters";
    private static final String POLICY_TYPE_IMPL = "policy_type_impl";

    // The ID of the session
    private int sessionId;

    // The TOSCA Service Template of the session
    private ToscaServiceTemplate toscaServiceTemplate;

    // The Apex policy model of the session
    private ApexModel apexModel;

    // The Apex policy model being edited
    private ApexModel apexModelEdited;

    /**
     * Constructor, create the session.
     *
     * @param sessionId the session ID of the session
     * @throws ApexRuntimeException when the APEX TOSCA template file cannot be loaded
     */
    public RestSession(final int sessionId) {
        this.sessionId = sessionId;

        try {
            this.toscaServiceTemplate = new StandardYamlCoder()
                .decode(ResourceUtils.getResourceAsString(APEX_TOSCA_POLICY_TEMPLATE), ToscaServiceTemplate.class);
        } catch (CoderException e) {
            throw new ApexRuntimeException("could not load default APEX TOSCA template " + APEX_TOSCA_POLICY_TEMPLATE,
                e);
        }

        this.apexModel = new ApexModelFactory().createApexModel(null, true);
    }

    /**
     * Load the policy model from a TOSCA Service Template.
     *
     * @param toscaServiceTemplateString The TOSCA service template string
     * @return the result of the lading operation
     */
    public ApexApiResult loadFromString(final String toscaServiceTemplateString) {
        try {
            if (toscaServiceTemplateString.startsWith("{")) {
                toscaServiceTemplate = new StandardCoder().decode(toscaServiceTemplateString,
                    ToscaServiceTemplate.class);
            } else {
                toscaServiceTemplate = new StandardYamlCoder().decode(toscaServiceTemplateString,
                    ToscaServiceTemplate.class);
            }
        } catch (Exception e) {
            return new ApexApiResult(Result.FAILED, "incoming model is not a TOSCA Service template", e);
        }

        if (!ToscaUtils.doPoliciesExist(new JpaToscaServiceTemplate(toscaServiceTemplate))) {
            return new ApexApiResult(Result.FAILED, "no policies found on incoming TOSCA service template");
        }

        @SuppressWarnings("unchecked")
        var apexEngineServiceParameterMap = (Map<String, Object>) toscaServiceTemplate
            .getToscaTopologyTemplate().getPoliciesAsMap().values().iterator().next().getProperties()
            .get(ENGINE_SERVICE_PARAMETERS);

        String apexModelString;
        try {
            apexModelString = new StandardCoder().encode(apexEngineServiceParameterMap.get(POLICY_TYPE_IMPL));
        } catch (CoderException e) {
            return new ApexApiResult(Result.FAILED, "APEX model not found TOSCA Service template", e);
        }

        return apexModelEdited.loadFromString(apexModelString);
    }

    /**
     * Commence making changes to the Apex model.
     *
     * @return the result of the edit commencement operation
     */
    public synchronized ApexApiResult editModel() {
        if (apexModelEdited != null) {
            return new ApexApiResult(Result.FAILED, "model is already being edited");
        }

        apexModelEdited = apexModel.clone();
        return new ApexApiResult();
    }

    /**
     * Commit the changes to the Apex model.
     *
     * @return the result of the commit operation
     */
    public synchronized ApexApiResult commitChanges() {
        if (apexModelEdited == null) {
            return new ApexApiResult(Result.FAILED, "model is not being edited");
        }

        apexModel = apexModelEdited;
        apexModelEdited = null;
        return new ApexApiResult();
    }

    /**
     * Discard the changes to the Apex model.
     *
     * @return the result of the discard operation
     */
    public synchronized ApexApiResult discardChanges() {
        if (apexModelEdited == null) {
            return new ApexApiResult(Result.FAILED, "model is not being edited");
        }

        apexModelEdited = null;
        return new ApexApiResult();
    }

    /**
     * Download the apex model as a TOSCA service template YAML string.
     *
     * @return the apex model as a TOSCA service template YAML string
     */
    public ApexApiResult downloadModel() {
        ApexModel apexModelToDownload = (apexModelEdited == null ? apexModel : apexModelEdited);

        ToscaPolicy ap = toscaServiceTemplate.getToscaTopologyTemplate().getPoliciesAsMap().values().iterator().next();

        @SuppressWarnings("unchecked")
        var apexEngineServiceParameterMap = (Map<String, Object>) ap.getProperties().get(ENGINE_SERVICE_PARAMETERS);

        Object decoded = null;
        try {
            decoded = new StandardCoder().decode(apexModelToDownload.listModel().getMessage(), Object.class);
        } catch (Exception e) {
            return new ApexApiResult(Result.FAILED, "insertion of APEX model into TOSCA Service template failed", e);
        }

        apexEngineServiceParameterMap.put(POLICY_TYPE_IMPL, decoded);

        var result = new ApexApiResult();
        try {
            result.addMessage(new StandardYamlCoder().encode(toscaServiceTemplate));
        } catch (CoderException e) {
            return new ApexApiResult(Result.FAILED, "encoding of TOSCA Service template into YAML failed", e);
        }

        return result;
    }

    /**
     * Upload the apex model as a TOSCA service template YAML string to the configured URL.
     *
     * @return a result indicating if the upload was successful or not
     */
    public ApexApiResult uploadModel() {
        // Get the model in TOSCA format
        ApexApiResult result = downloadModel();
        if (result.isNok()) {
            return result;
        }

        ApexModel apexModelBeingUploaded = (apexModelEdited == null ? apexModel : apexModelEdited);

        AxArtifactKey policyModelKey = apexModelBeingUploaded.getPolicyModel().getKey();

        var policyModelUUid = apexModelBeingUploaded.getPolicyModel().getKeyInformation().get(policyModelKey)
            .getUuid().toString();
        return new PolicyUploadHandler().doUpload(result.getMessage(), policyModelKey, policyModelUUid);
    }

    /**
     * Finish a session by committing or discarding the changes.
     *
     * @param commitFlag if true, commit changes otherwise discard them
     */
    public void finishSession(boolean commitFlag) {
        if (commitFlag) {
            commitChanges();
        } else {
            discardChanges();
        }
    }

    /**
     * Get the session ID of the session.
     *
     * @return the sessionId
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Get the Apex model of the session.
     *
     * @return the apexModel
     */
    public ApexModel getApexModel() {
        return apexModel;
    }

    /**
     * Get the edited Apex model of the session.
     *
     * @return the apexModel
     */
    public ApexModel getApexModelEdited() {
        return apexModelEdited;
    }
}
