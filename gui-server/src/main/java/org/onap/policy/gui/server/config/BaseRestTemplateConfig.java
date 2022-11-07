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
import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import lombok.Setter;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class BaseRestTemplateConfig {
    private static final Logger LOG = LoggerFactory.getLogger(BaseRestTemplateConfig.class);

    @Setter
    private boolean disableSslValidation;

    @Setter
    private boolean disableSslHostnameCheck;

    @Value("${server.ssl.trust-store:#{null}}")
    protected Resource trustStore;

    @Value("${server.ssl.trust-store-password:#{null}}")
    protected char[] trustStorePassword;

    @PostConstruct
    private void validateProperties() {
        if (trustStore == null && !disableSslValidation) {
            throw new IllegalArgumentException("server.ssl.trust-store must be set if SSL validation is enabled");
        }
        if (disableSslValidation && !disableSslHostnameCheck) {
            LOG.info("Disabling SSL hostname check as SSL validation is disabled");
            disableSslHostnameCheck = true;
        }
    }

    /**
     * Returns a RestTemplate, optionally disabling SSL hostname check or disabling SSL validation entirely.
     */
    protected RestTemplate getRestTemplate() throws GeneralSecurityException, IOException {
        SSLContext sslContext;
        if (disableSslValidation) {
            sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustAllStrategy()).build();
        } else {
            sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore.getURL(), trustStorePassword).build();
        }

        HostnameVerifier hostnameVerifier;
        if (disableSslHostnameCheck) {
            hostnameVerifier = new NoopHostnameVerifier();
        } else {
            hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
        }

        var csf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        var httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
