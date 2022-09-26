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

package org.onap.policy.gui.server.test.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.function.Executable;

public class KeyStoreHelper {
    /*
     * The proxy test keystore contains certs with:
     * - alias "valid": self-signed cert which expires circa year 2050
     * - alias "expired": self-signed cert which expired in year 2000
     */
    private static final String KEY_STORE_PATH = "src/test/resources/keystore-proxytest.jks";
    private static final String KEY_STORE_PASSWORD = "changeit";
    private static final String KEY_STORE_TYPE = "JKS";
    private static final String CERT_ALIAS_VALID = "valid";
    private static final String CERT_ALIAS_EXPIRED = "expired";

    /**
     * Load a valid certificate from the test keystore.
     */
    public static X509Certificate loadValidCert() throws CouldNotLoadCertificateException {
        X509Certificate cert = loadCertFromKeyStore(CERT_ALIAS_VALID);
        assertDoesNotThrow((Executable) cert::checkValidity);
        return cert;
    }

    /**
     * Load an expired certificate from the test keystore.
     */
    public static X509Certificate loadExpiredCert() throws CouldNotLoadCertificateException {
        X509Certificate cert = loadCertFromKeyStore(CERT_ALIAS_EXPIRED);
        assertThrows(CertificateExpiredException.class, cert::checkValidity);
        return cert;
    }

    /**
     * Load a certificate with given alias from the test keystore.
     */
    private static X509Certificate loadCertFromKeyStore(String certAlias) throws CouldNotLoadCertificateException {
        try {
            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
            ks.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
            X509Certificate cert = (X509Certificate) ks.getCertificate(certAlias);
            if (cert == null) {
                throw new CouldNotLoadCertificateException("Alias does not exist or does not contain a certificate.");
            }
            return cert;
        } catch (Exception e) {
            throw new CouldNotLoadCertificateException(
                "Could not load cert with alias '" + certAlias + "' from test keystore.", e);
        }
    }

    /**
     * Exception class for KeyStoreHelper methods.
     */
    public static class CouldNotLoadCertificateException extends java.lang.Exception {
        private static final long serialVersionUID = 4858081258043045085L;

        protected CouldNotLoadCertificateException(String errorMessage) {
            super(errorMessage);
        }

        protected CouldNotLoadCertificateException(String errorMessage, Throwable err) {
            super(errorMessage, err);
        }
    }
}
