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

import static org.onap.policy.gui.server.filters.ClientSslHeaderFilter.SSL_CERT_HEADER_NAME;
import static org.onap.policy.gui.server.test.util.X509RequestPostProcessor.x509;
import static org.onap.policy.gui.server.util.X509CertificateEncoder.urlEncodeCert;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.cert.X509Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.gui.server.test.util.KeyStoreHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
    properties = {
        "clamp.url=https://clamp-backend:8443/",
        "clamp.disable-ssl-validation=true"
    })
@AutoConfigureMockMvc
class ClampRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Qualifier("clampRestTemplate")
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testStaticContentUrls() throws Exception {
        mvc.perform(get("/clamp/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/clamp/index.html"));

        mvc.perform(get("/clamp"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clamp/"));
    }

    /*
     * This is a happy path test to verify that calls to /clamp/restservices/**
     * are relayed to the clamp backend, and that the backend receives the
     * client certificate encoded in a header. More extensive tests of the
     * certificate cert filter are in ClientSslHeaderFilterTest.
     */
    @Test
    void testClampProxyWithClientCert() throws Exception {
        X509Certificate cert = KeyStoreHelper.loadValidCert();

        mockServer.expect(
            requestTo("https://clamp-backend:8443/restservices/junit/test"))
            .andExpect(header(SSL_CERT_HEADER_NAME, urlEncodeCert(cert)))
            .andRespond(withStatus(HttpStatus.OK).body("admin"));

        mvc.perform(
            get("/clamp/restservices/junit/test")
                .with(x509(cert)))
            .andExpect(status().isOk())
            .andExpect(content().string("admin"));

        mockServer.verify();
    }

    /*
     * This test verifies that HTTP headers are preserved for requests to the
     * clamp backend (including multi-value headers).
     */
    @Test
    void verifyClampProxyPassesHeaders() throws Exception {
        // Single value header
        final String userAgent = "User-Agent";
        final String userAgentValue = "JUnit";
        // Multi value header
        final String acceptLanguage = "Accept-Language";
        final String enUs = "en-US";
        final String enIe = "en-IE";

        mockServer.expect(
            requestTo("https://clamp-backend:8443/restservices/junit/test"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(userAgent, userAgentValue))
            .andExpect(header(acceptLanguage, enUs, enIe))
            .andRespond(withStatus(HttpStatus.OK));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(userAgent, userAgentValue);
        requestHeaders.add(acceptLanguage, enUs);
        requestHeaders.add(acceptLanguage, enIe);
        mvc.perform(
            get("/clamp/restservices/junit/test")
                .headers(requestHeaders))
            .andExpect(status().isOk());

        mockServer.verify();
    }

    /*
     * This test verifies that error messages from the clamp backend are
     * delivered to the client (as opposed to 500 "Internal Server Error").
     */
    @Test
    void verifyClampProxyReturnsBackendErrorCode() throws Exception {
        final String errorMessage = "This appliance cannot brew coffee";

        mockServer.expect(
            requestTo("https://clamp-backend:8443/restservices/coffee"))
            .andRespond(withStatus(HttpStatus.I_AM_A_TEAPOT).body(errorMessage));

        mvc.perform(
            post("/clamp/restservices/coffee"))
            .andExpect(status().is(HttpStatus.I_AM_A_TEAPOT.value()))
            .andExpect(content().string(errorMessage));

        mockServer.verify();
    }
}