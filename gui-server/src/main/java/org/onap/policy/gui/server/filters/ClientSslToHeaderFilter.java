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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Spring Cloud Gateway Filter which encodes a client SSL certificate into X-SSL-Cert HTTP header.
 * This is needed as CLAMP runtime uses AAF for auth, and AAF uses client certificate authentication.
 * Since REST requests from CLAMP GUI to CLAMP runtime are proxied using Spring Cloud Gateway, the
 * proxy request needs to attach a copy of the client SSL cert (as the proxy could not know the
 * client's private key).
 * CLAMP has a corresponding filter called ClampCadiFilter which decodes the X-SSL-Cert header.
 */
@Component
public class ClientSslToHeaderFilter implements GlobalFilter {
    private final Logger logger = LoggerFactory.getLogger(ClientSslToHeaderFilter.class);

    private static final String SSL_CERT_HEADER_NAME = "X-SSL-Cert";
    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n";
    private static final String END_CERT = "\n-----END CERTIFICATE-----";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // If a client SSL certificate present in client request,
        // add 'X-SSL-Cert' HTTP header to the outgoing proxy request.
        ServerHttpRequest req = exchange.getRequest();
        SslInfo info = req.getSslInfo();
        if (info != null) {
            X509Certificate[] certs = info.getPeerCertificates();
            if (certs != null && certs.length > 0) {
                try {
                    certs[0].checkValidity();
                    String pemCert = BEGIN_CERT + Base64.getEncoder().encodeToString(certs[0].getEncoded()) + END_CERT;
                    String encodedCert = URLEncoder.encode(pemCert, StandardCharsets.UTF_8);
                    ServerHttpRequest newRequest = req.mutate().header(SSL_CERT_HEADER_NAME, encodedCert).build();
                    return chain.filter(exchange.mutate().request(newRequest).build());
                } catch (CertificateEncodingException e) {
                    logger.error("Error encoding client SSL cert", e);
                } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                    logger.info("Client SSL cert expired", e);
                }
            }
        }
        return chain.filter(exchange);
    }
}
