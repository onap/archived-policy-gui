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

import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.onap.policy.gui.server.filters.ClientSslHeaderFilter.SSL_CERT_HEADER_NAME;
import static org.onap.policy.gui.server.filters.ClientSslHeaderFilter.X509_ATTRIBUTE_NAME;
import static org.onap.policy.gui.server.util.X509CertificateEncoder.urlDecodeCert;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onap.policy.gui.server.test.util.KeyStoreHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ClientSslHeaderFilterTest {

    /*
     * If the client does not supply an SSL cert, the filter should not set
     * the X-SSL-Cert header.
     */
    @Test
    void testNoClientCert_noHeader() throws ServletException, IOException {
        // Create a request without client SSL cert.
        HttpServletRequest inRequest =  new MockHttpServletRequest();

        // Apply the filter.
        HttpServletRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should not contain cert header.
        assertFalse(containsCertHeader(outRequest.getHeaderNames()));
        assertNull(outRequest.getHeader(SSL_CERT_HEADER_NAME));
        assertEquals(Collections.emptyEnumeration(), outRequest.getHeaders(SSL_CERT_HEADER_NAME));
    }

    /*
     * If the client does supply an SSL cert, the filter should set the
     * X-SSL-Cert header with the encoded SSL cert.
     */
    @Test
    void testValidClientCert_hasHeader() throws Exception {
        // Load valid cert from key store.
        X509Certificate validCert = KeyStoreHelper.loadValidCert();

        // Create a request with a valid client SSL cert.
        MockHttpServletRequest inRequest = new MockHttpServletRequest();
        inRequest.setAttribute(X509_ATTRIBUTE_NAME, new X509Certificate[] { validCert });

        // Apply the filter.
        HttpServletRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should contain a cert header.
        assertTrue(containsCertHeader(outRequest.getHeaderNames()));

        // Check if the cert header parses back to the original cert.
        String headerValue = outRequest.getHeader(SSL_CERT_HEADER_NAME);
        assertEquals(validCert, urlDecodeCert(headerValue));

        // Verify the getHeaders method also returns cert.
        assertEquals(headerValue, outRequest.getHeaders(SSL_CERT_HEADER_NAME).nextElement());
    }

    /*
     * If the client supplies an expired SSL cert, the filter should not set
     * the X-SSL-Cert header.
     */
    @Test
    void testExpiredClientCert_noHeader() throws Exception {
        // Load expired cert from key store.
        X509Certificate expiredCert = KeyStoreHelper.loadExpiredCert();

        // Create a request with an expired client SSL cert.
        MockHttpServletRequest inRequest = new MockHttpServletRequest();
        inRequest.setAttribute(X509_ATTRIBUTE_NAME, new X509Certificate[] { expiredCert });

        // Apply the filter.
        HttpServletRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should not contain a cert header.
        assertFalse(containsCertHeader(outRequest.getHeaderNames()));
        assertNull(outRequest.getHeader(SSL_CERT_HEADER_NAME));
        assertEquals(Collections.emptyEnumeration(), outRequest.getHeaders(SSL_CERT_HEADER_NAME));
    }

    /*
     * This test is needed to prevent a security vulnerability where a
     * malicious user does not authenticate using client cert, but defines the
     * X-SSL-Cert header themselves, thus gaining access without having the
     * corresponding private key.
     * We thus test that an incoming X-SSL-Cert header is sanitized.
     */
    @Test
    void existingCertHeaderIsSanitized() throws Exception {
        // Create a request with X-SSL-Cert header predefined.
        MockHttpServletRequest inRequest = new MockHttpServletRequest();
        inRequest.addHeader(SSL_CERT_HEADER_NAME, "somevalue");

        // Apply the filter.
        HttpServletRequest outRequest = applyRequestFilter(inRequest);

        // The modified request should not contain a cert header.
        assertFalse(containsCertHeader(outRequest.getHeaderNames()));
        assertNull(outRequest.getHeader(SSL_CERT_HEADER_NAME));
        assertEquals(Collections.emptyEnumeration(), outRequest.getHeaders(SSL_CERT_HEADER_NAME));
    }

    /*
     * This test verifies that existing HTTP headers are preserved
     *  (including multi-value headers).
     */
    @Test
    void otherHeadersAreStillAccessible() throws Exception {
        // Load valid cert from key store.
        X509Certificate validCert = KeyStoreHelper.loadValidCert();

        // Create a request with a valid client SSL cert and some existing headers.
        MockHttpServletRequest inRequest = new MockHttpServletRequest();
        inRequest.setAttribute(X509_ATTRIBUTE_NAME, new X509Certificate[] { validCert });
        inRequest.addHeader("User-Agent", "Jupiter");
        inRequest.addHeader("Accept-Language", "en-US");
        inRequest.addHeader("Accept-Language", "en-IE");

        // Apply the filter.
        HttpServletRequest outRequest = applyRequestFilter(inRequest);

        // The modified request contains the new cert header and the existing headers.
        assertTrue(
            isEqualCollection(
                List.of("Accept-Language", "User-Agent", SSL_CERT_HEADER_NAME),
                Collections.list(outRequest.getHeaderNames())));

        // Verify getHeader method returns correct value.
        String userAgent = outRequest.getHeader("User-Agent");
        assertEquals("Jupiter", userAgent);

        // Verify getHeaders method returns correct values.
        Enumeration<String> acceptLanguages = outRequest.getHeaders("Accept-Language");
        assertEquals("en-US", acceptLanguages.nextElement());
        assertEquals("en-IE", acceptLanguages.nextElement());
        assertFalse(acceptLanguages.hasMoreElements());
    }

    /**
     * Apply the ClientSslToHeaderFilter to the input request,
     * and return the modified request.
     */
    private HttpServletRequest applyRequestFilter(HttpServletRequest request) throws ServletException, IOException {
        HttpServletResponse response = new MockHttpServletResponse();

        // The filter calls filterChain::doFilter after processing the request,
        // so capture the HttpServletRequest argument from filterChain::doFilter.
        FilterChain filterChain = mock(FilterChain.class);
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(requestCaptor.capture(), eq(response));

        // Apply the filter.
        Filter filter = new ClientSslHeaderFilter();
        filter.doFilter(request, response, filterChain);

        // Return the modified HttpServletRequest.
        return requestCaptor.getValue();
    }

    /**
     * Check if an Enumeration of header names contains the certificate header.
     * Note HTTP header names are case insensitive.
     */
    private boolean containsCertHeader(Enumeration<String> headers) {
        while (headers.hasMoreElements()) {
            if (headers.nextElement().equalsIgnoreCase(SSL_CERT_HEADER_NAME)) {
                return true;
            }
        }
        return false;
    }
}
