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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Test;
import org.onap.policy.gui.server.test.util.KeyStoreHelper;

class X509CertificateEncoderTest {

    @Test
    void testPemEncoder() throws KeyStoreHelper.CouldNotLoadCertificateException, CertificateException {
        X509Certificate loadedCert = KeyStoreHelper.loadValidCert();
        String pem = X509CertificateEncoder.getPemFromCert(loadedCert);
        X509Certificate certFromPem = X509CertificateEncoder.getCertFromPem(pem);
        assertEquals(loadedCert, certFromPem);
    }

    @Test
    void testUrlEncoder() throws KeyStoreHelper.CouldNotLoadCertificateException, CertificateException {
        X509Certificate loadedCert = KeyStoreHelper.loadValidCert();
        String encodedCert = X509CertificateEncoder.urlEncodeCert(loadedCert);
        X509Certificate decodedCert = X509CertificateEncoder.urlDecodeCert(encodedCert);
        assertEquals(loadedCert, decodedCert);
    }
}
