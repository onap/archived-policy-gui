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

import java.security.cert.X509Certificate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * X509RequestPostProcessor is a test helper class for use with Spring MockMvc.
 * It allows setting X509 certificates to a MockHttpServletRequest.
 */
public final class X509RequestPostProcessor implements RequestPostProcessor {
    private final X509Certificate[] certificates;

    public X509RequestPostProcessor(X509Certificate... certificates) {
        this.certificates = certificates;
    }

    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setAttribute("javax.servlet.request.X509Certificate", this.certificates);
        return request;
    }

    public static RequestPostProcessor x509(X509Certificate... certificates) {
        return new X509RequestPostProcessor(certificates);
    }
}
