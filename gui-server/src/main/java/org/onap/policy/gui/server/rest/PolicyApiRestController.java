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

package org.onap.policy.gui.server.rest;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("${runtime-ui.policy.mapping-path}")
public class PolicyApiRestController extends BaseRestController {
    /**
     * Set the mapping parameters for the REST controller.
     *
     * @param mappingPath The mapping path to map from
     * @param url The URL path to map to
     */
    @Value("{runtime-ui.policy}")
    public void setSslFlags(
        @Value("${runtime-ui.policy.mapping-path}") String mappingPath,
        @Value("${runtime-ui.policy.url}") URI url) {
        super.setMappingPath(mappingPath);
        super.setUrl(url);
    }

    /**
     * Set the REST template for the REST controller.
     *
     * @param restTemplate The REST template
     */
    @Autowired
    public void setControllerRestTemplate(
        @Qualifier("policyApiRestTemplate") RestTemplate restTemplate) {
        super.setRestTemplate(restTemplate);
    }

    /**
     * Proxy rest calls to ACM runtime.
     */
    @Override
    @RequestMapping("/**")
    public ResponseEntity<String> mirrorRest(@RequestBody(required = false) String body,
                                             @RequestHeader HttpHeaders headers,
                                             HttpMethod method,
                                             HttpServletRequest request) {
        return super.mirrorRest(body, headers, method, request);
    }
}
