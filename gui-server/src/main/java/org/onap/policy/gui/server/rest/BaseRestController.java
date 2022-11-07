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

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class BaseRestController {
    @Setter
    private String mappingPath;

    @Setter
    private URI url;

    @Setter
    private RestTemplate restTemplate;

    /**
     * Proxy rest calls to a runtime.
     */
    public ResponseEntity<String> mirrorRest(@RequestBody(required = false) String body,
                                             @RequestHeader HttpHeaders headers,
                                             HttpMethod method,
                                             HttpServletRequest request) {
        // Strip ACM runtime prefix from request URI.
        String requestUri = request.getRequestURI().replaceFirst(mappingPath, "");
        URI uri = UriComponentsBuilder.fromUri(url)
            .path(requestUri)
            .query(request.getQueryString())
            .build(true).toUri();

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(uri, method, httpEntity, String.class);

        } catch (HttpStatusCodeException e) {
            // On error, return the ACM runtime error code instead of 500.
            return ResponseEntity.status(e.getRawStatusCode())
                .headers(e.getResponseHeaders())
                .body(e.getResponseBodyAsString());
        }
    }

}
