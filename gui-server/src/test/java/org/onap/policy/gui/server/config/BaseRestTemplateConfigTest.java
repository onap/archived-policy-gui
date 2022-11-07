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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.gui.server.test.util.hello.HelloWorldApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

/**
 * This class setups up the REST templates for testing.
 */
@SpringBootTest(
    classes = {
        HelloWorldApplication.class,
        AcmRuntimeRestTemplateConfig.class,
        PolicyApiRestTemplateConfig.class
    },
    properties = {
        "server.ssl.enabled=false",
        "runtime-ui.acm.disable-ssl-validation=true",
        "runtime-ui.policy.disable-ssl-validation=true"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class BaseRestTemplateConfigTest {
    @Getter
    @LocalServerPort
    private int port;

    @Getter
    @Autowired
    @Qualifier("acmRuntimeRestTemplate")
    private RestTemplate acmRuntimeRestTemplate;

    @Getter
    @Autowired
    @Qualifier("policyApiRestTemplate")
    private RestTemplate policyApiRestTemplate;

    @Getter
    List<RestTemplate> restTemplateList = new ArrayList<>();

    @BeforeEach
    public void beforeTest() {
        restTemplateList.add(acmRuntimeRestTemplate);
        restTemplateList.add(policyApiRestTemplate);
    }

    @Test
    void testResttemplatesAreSet() {
        restTemplateList.forEach(restTemplate -> {
            assertThat(restTemplate).isNotNull();
        });
    }
}
