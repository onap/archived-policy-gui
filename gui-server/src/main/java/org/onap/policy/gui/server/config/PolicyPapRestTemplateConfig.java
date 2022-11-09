/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.gui.server.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PolicyPapRestTemplateConfig extends BaseRestTemplateConfig {

    /**
     * Set the SSL validation flags on the template.
     *
     * @param disableSslValidation Turn off SSL altogether on this REST interface
     * @param disableSslHostnameCheck Turn off SSL host name checking
     */
    @Value("{runtime-ui.policy-pap}")
    public void setSslFlags(
        @Value("${runtime-ui.policy-pap.disable-ssl-validation:false}") boolean disableSslValidation,
        @Value("${runtime-ui.policy-pap.disable-ssl-hostname-check:false}") boolean disableSslHostnameCheck) {
        super.setDisableSslValidation(disableSslValidation);
        super.setDisableSslHostnameCheck(disableSslHostnameCheck);
    }

    /**
     * Returns a RestTemplate, optionally disabling SSL host name check or disabling SSL validation entirely.
     */
    @Bean
    public RestTemplate policyPapRestTemplate() throws GeneralSecurityException, IOException {
        return super.getRestTemplate();
    }
}
