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

import org.apache.commons.lang3.StringUtils;
import org.onap.policy.gui.server.filters.ClientSslHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegistrationConfig {
    @Value("${runtime-ui.policy-api.mapping-path}")
    private String policyApiMappingPath;

    @Value("${runtime-ui.policy-pap.mapping-path}")
    private String policyPapMappingPath;

    @Value("${runtime-ui.acm.mapping-path}")
    private String acmRuntimeMappingPath;

    /**
     * Registers ClientSslToHeaderFilter for the mapped URLs.
     */
    @Bean
    public FilterRegistrationBean<ClientSslHeaderFilter> clientSslHeaderFilter() {
        FilterRegistrationBean<ClientSslHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ClientSslHeaderFilter());
        registrationBean.addUrlPatterns(
            StringUtils.stripEnd(policyApiMappingPath, "/")  + "/*",
            StringUtils.stripEnd(policyPapMappingPath, "/")  + "/*",
            StringUtils.stripEnd(acmRuntimeMappingPath, "/")  + "/*"
        );
        return registrationBean;
    }

}
