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

package org.onap.policy.gui.server.filters;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class ClientSslToHeaderFilterTest {
    /*
     * The proxy test keystore contains certs with:
     * - alias "valid": self-signed cert, which expires circa year 2050
     * - alias "expired": self-signed cert, which expired in year 2000
     */
    private static final String KEY_STORE_PATH = "src/test/resources/keystore-proxytest.jks";
    private static final String KEY_STORE_PASSWORD = "changeit";
    private static final String KEY_STORE_TYPE = "JKS";
    public static final String CERT_ALIAS_VALID = "valid";
    public static final String CERT_ALIAS_EXPIRED = "expired";

    // An inconsequential URL for the mock http requests.
    private static final String DUMMY_URL = "https://policy-gui/clamp/restservices/blah";

    // Name of header containing encoded SSL cert - also used in clamp repo's ClampCadiFilter
    public static final String X_SSL_CERT = "X-SSL-Cert";

    @Test
    void testNoClientCert_noHeader() {
        // Create a request without client SSL cert.
        MockServerHttpRequest inRequest = buildMockHttpRequest();

        // Apply the filter.
        ServerHttpRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should not contain cert header.
        assertFalse(outRequest.getHeaders().containsKey(X_SSL_CERT));
    }

    @Test
    void testValidClientCert_hasHeader() throws Exception {
        // Load valid cert from key store.
        X509Certificate validCert = loadCertFromKeyStore(CERT_ALIAS_VALID);
        assertNotNull(validCert);
        assertDoesNotThrow((Executable) validCert::checkValidity);

        // Create a request with a valid client SSL cert.
        MockServerHttpRequest inRequest = buildMockHttpRequestWithClientCert(validCert);

        // Apply the filter.
        ServerHttpRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should contain a cert header.
        assertTrue(outRequest.getHeaders().containsKey(X_SSL_CERT));

        // Check if cert parses back to the original cert.
        String headerValue = Objects.requireNonNull(outRequest.getHeaders().get(X_SSL_CERT)).get(0);
        String pemCert = URLDecoder.decode(headerValue, StandardCharsets.UTF_8);
        assertEquals(validCert, getCertFromPem(pemCert));
    }

    @Test
    void testExpiredClientCert_noHeader() throws Exception {
        // Load expired cert from key store.
        X509Certificate expiredCert = loadCertFromKeyStore(CERT_ALIAS_EXPIRED);
        assertNotNull(expiredCert);
        assertThrows(CertificateExpiredException.class, (Executable) expiredCert::checkValidity);

        // Create a request with an expired client SSL cert.
        MockServerHttpRequest inRequest = buildMockHttpRequestWithClientCert(expiredCert);

        // Apply the filter.
        ServerHttpRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should not contain a cert header.
        assertFalse(outRequest.getHeaders().containsKey(X_SSL_CERT));
    }

    /**
     * Applies the ClientSslToHeaderFilter to the mock input request, and returns modified output request.
     * @param request Input MockServerHttpRequest
     * @return Modified ServerHttpRequest
     */
    private ServerHttpRequest applyRequestFilter(MockServerHttpRequest request) {
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(filterChain.filter(captor.capture())).thenReturn(Mono.empty());

        GlobalFilter filter = new ClientSslToHeaderFilter();
        filter.filter(exchange, filterChain);

        ServerWebExchange outExchange = captor.getValue();
        return outExchange.getRequest();
    }

    /**
     * Loads a certificate with given alias from the test keystore.
     */
    private X509Certificate loadCertFromKeyStore(String certAlias)
        throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
        ks.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        return (X509Certificate) ks.getCertificate(certAlias);
    }

    /**
     * Builds a certificate from a PEM string.
     */
    private X509Certificate getCertFromPem(String pem) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(pem.getBytes()));
    }

    /**
     * Builds a MockServerHttpRequest (http get DUMMY_URL) without client SSL cert for use in tests.
     */
    private MockServerHttpRequest buildMockHttpRequest() {
        return buildMockHttpRequestWithClientCert(null);
    }

    /**
     * Builds a MockServerHttpRequest (http get DUMMY_URL) with client SSL cert for use in tests.
     */
    private MockServerHttpRequest buildMockHttpRequestWithClientCert(X509Certificate cert) {
        MockServerHttpRequest.BaseBuilder<?> requestBuilder = MockServerHttpRequest.get(DUMMY_URL);
        if (cert != null) {
            requestBuilder.sslInfo(new SslInfo() {
                @Override
                public String getSessionId() {
                    return null;
                }

                @Override
                public X509Certificate[] getPeerCertificates() {
                    return new X509Certificate[] { cert };
                }
            });
        }
        return requestBuilder.build();
    }

}
