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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.net.ssl.SSLPeerUnverifiedException;
import org.junit.jupiter.api.Test;
import org.onap.policy.gui.server.test.util.hello.HelloWorldApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * In this test, we verify that SSL validation and hostname check are enabled
 * by default. Thus we explicitly set the Spring properties
 * runtime-ui.acm.disable-ssl-validation and runtime-ui.acm.disable-ssl-hostname-check as false.
 * Since our keystore cert has a hostname 'helloworld' and our test request is
 * to localhost, the request will fail with an SSLPeerUnverifiedException, as
 * the SSL cert name does not match the server name 'localhost'.
 */
@SpringBootTest(
    classes = { HelloWorldApplication.class, AcmRuntimeRestTemplateConfig.class },
    properties = {
        "server.ssl.enabled=true",
        "server.ssl.key-store=file:src/test/resources/helloworld-keystore.jks",
        "server.ssl.key-store-password=changeit",
        "server.ssl.trust-store=file:src/test/resources/helloworld-truststore.jks",
        "server.ssl.trust-store-password=changeit"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcmRuntimeRestTemplateConfig5Test {

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("acmRuntimeRestTemplate")
    private RestTemplate restTemplate;

    @Test
    void testSslValidationIsEnabledByDefault() {
        var helloUrl = "https://localhost:" + port + "/";
        Exception e = assertThrows(RestClientException.class,
            () -> restTemplate.getForEntity(helloUrl, String.class));
        assertTrue(e.getCause() instanceof SSLPeerUnverifiedException);
    }
}
