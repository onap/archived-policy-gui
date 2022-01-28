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

package org.onap.policy.gui.server.util;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Helper methods for encoding/decoding X509Certificates from PEM strings and URL-encoded PEM strings.
 */
public class X509CertificateEncoder {
    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n";
    private static final String END_CERT = "\n-----END CERTIFICATE-----";

    private X509CertificateEncoder() {}

    /**
     * Returns a PEM string from an X509Certificate.
     */
    public static String getPemFromCert(X509Certificate cert) throws CertificateEncodingException {
        return BEGIN_CERT + Base64.getEncoder().encodeToString(cert.getEncoded()) + END_CERT;
    }

    /**
     * Returns an X509Certificate from a PEM string.
     */
    public static X509Certificate getCertFromPem(String pem) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(pem.getBytes()));
    }

    /**
     * Returns URL-encoded PEM string from an X509Certificate, suitable as a HTTP header.
     */
    public static String urlEncodeCert(X509Certificate cert) throws CertificateEncodingException {
        return URLEncoder.encode(getPemFromCert(cert), StandardCharsets.UTF_8);
    }

    /**
     * Returns an X509Certificate from a URL-encoded PEM string.
     */
    public static X509Certificate urlDecodeCert(String encodedPem) throws CertificateException {
        return getCertFromPem(URLDecoder.decode(encodedPem, StandardCharsets.UTF_8));
    }
}
