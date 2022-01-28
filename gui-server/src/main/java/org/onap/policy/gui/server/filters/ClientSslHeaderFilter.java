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

import static org.onap.policy.gui.server.util.X509CertificateEncoder.urlEncodeCert;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter which encodes a client SSL certificate into X-SSL-Cert HTTP header.
 * CLAMP has a corresponding filter called ClampCadiFilter which decodes the
 * header. This is needed as CLAMP runtime uses AAF for auth, and AAF uses
 * client cert authentication. Since REST requests from CLAMP GUI to CLAMP
 * runtime are proxied in gui-server, the proxy needs to attach a copy of the
 * client SSL cert, as the proxy could not know the client's private key.
 */
@Order(1)
public class ClientSslHeaderFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ClientSslHeaderFilter.class);

    // Name of attribute containing request SSL cert.
    public static final String X509_ATTRIBUTE_NAME = "javax.servlet.request.X509Certificate";

    // Name of header containing encoded SSL cert - also used in clamp's ClampCadiFilter.
    public static final String SSL_CERT_HEADER_NAME = "X-SSL-Cert";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var wrappedRequest = new ClientSslHeaderRequestWrapper(request);
        var certs = (X509Certificate[]) request.getAttribute(X509_ATTRIBUTE_NAME);
        if (certs != null && certs.length > 0) {
            try {
                certs[0].checkValidity();
                wrappedRequest.setSslCertHeader(urlEncodeCert(certs[0]));
            } catch (CertificateEncodingException e) {
                LOG.error("Error encoding client SSL cert", e);
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                LOG.info("Client SSL cert expired", e);
            }
        }
        filterChain.doFilter(wrappedRequest, response);
    }

    /*
     * This class wraps a HttpServletRequest so that X-SSL-Cert header can be added.
     */
    private static class ClientSslHeaderRequestWrapper extends HttpServletRequestWrapper {
        private String encodedSslCert = null;

        public ClientSslHeaderRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void setSslCertHeader(String encodedSslCert) {
            this.encodedSslCert = encodedSslCert;
        }

        /**
         * Returns the value of the specified request header as a String.
         * The header name is case insensitive.
         */
        @Override
        public String getHeader(String name) {
            if (SSL_CERT_HEADER_NAME.equalsIgnoreCase(name)) {
                return encodedSslCert;
            } else {
                return super.getHeader(name);
            }
        }

        /**
         * Returns all the values of the specified request header as an Enumeration
         * of String objects.
         * Some headers, such as Accept-Language can be sent by clients as several
         * headers each with a different value rather than sending the header as a
         * comma separated list. The header name is case insensitive.
         */
        @Override
        public Enumeration<String> getHeaders(String name) {
            if (SSL_CERT_HEADER_NAME.equalsIgnoreCase(name)) {
                if (encodedSslCert != null) {
                    return Collections.enumeration(Collections.singletonList(encodedSslCert));
                } else {
                    return Collections.emptyEnumeration();
                }
            } else {
                return super.getHeaders(name);
            }
        }

        /**
         * Returns an enumeration of all the header names this request contains.
         * If the request has no headers, this method returns an empty enumeration.
         */
        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            names.addAll(Collections.list(super.getHeaderNames()));
            if (encodedSslCert != null) {
                names.add(SSL_CERT_HEADER_NAME);
            } else {
                // This is needed to prevent an exploit where a user passes their own
                // X-SSL-Cert header, possibly bypassing client cert verification.
                names.remove(SSL_CERT_HEADER_NAME);
            }
            return Collections.enumeration(names);
        }
    }
}
