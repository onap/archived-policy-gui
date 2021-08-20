/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

package org.onap.policy.gui.pdp.monitoring;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.annotations.Max;
import org.onap.policy.common.parameters.annotations.Min;

/**
 * This class reads and handles command line parameters to the Pdp Monitoring services.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 */
@ToString
@Getter
@Setter
public class PdpMonitoringServerParameters {
    public static final int DEFAULT_PORT = 18999;
    public static final int INFINITY_TIME_TO_LIVE = -1;

    // Base URI the HTTP server will listen on
    private static final String DEFAULT_SERVER_URI_ROOT = "http://0.0.0.0:";
    private static final String DEFAULT_REST_PATH = "papservices/";
    private static final String DEFAULT_CONTEXT_PATH = "/";
    private static final String SERVER_HOST = "0.0.0.0";
    // Package that will field REST requests
    private static final String DEFAULT_REST_PACKAGE = "org.onap.policy.gui.pdp.monitoring.rest";

    // The services parameters
    private boolean helpSet = false;

    @Min(1024)
    @Max(65534)
    private int port = DEFAULT_PORT;

    @Min(-1)
    private long timeToLive = INFINITY_TIME_TO_LIVE;

    /**
     * Validate the parameters.
     *
     * @return the result of the validation
     */
    public ValidationResult validate() {
        return new BeanValidator().validateTop(PdpMonitoringServerParameters.class.getSimpleName(), this);
    }

    public URI getBaseUri() {
        return URI.create(DEFAULT_SERVER_URI_ROOT + port + "/" + DEFAULT_REST_PATH);
    }

    public String getRestPackage() {
        return DEFAULT_REST_PACKAGE;
    }

    public String getContextPath() {
        return DEFAULT_CONTEXT_PATH;
    }

    public String getServerHost() {
        return SERVER_HOST;
    }

    public String getDefaultRestPath() {
        return "/" + DEFAULT_REST_PATH;
    }
}
