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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
    properties = {
        "runtime-ui.policy.mapping-path=/runtime-ui/policy/restservices/",
        "runtime-ui.policy.url=https://policy-api:9876/",
        "runtime-ui.policy.disable-ssl-validation=true",
        "runtime-ui.acm.mapping-path=/runtime-ui/acm/restservices/",
        "runtime-ui.acm.url=https://runtime-acm:8443/",
        "runtime-ui.acm.disable-ssl-validation=true"
    })
@AutoConfigureMockMvc
class PolicyApiRestControllerTest extends BaseRestControllerTest {
    @Autowired
    public void setBaseMockServer(@Qualifier("policyApiRestTemplate") RestTemplate restTemplate) {
        super.setMockServer(MockRestServiceServer.createServer(restTemplate));
    }

    @Autowired
    public void setBaseMapping(
        @Value("${runtime-ui.policy.mapping-path}") String mappingPath,
        @Value("${runtime-ui.policy.url}") URI url) {
        super.setMappingPath(mappingPath);
        super.setUrl(url);
    }
}
