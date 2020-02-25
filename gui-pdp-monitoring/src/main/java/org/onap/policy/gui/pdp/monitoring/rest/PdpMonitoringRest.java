/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.gui.pdp.monitoring.rest;

import lombok.NonNull;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to launch the services. It creates a Jetty embedded web server and runs the
 * services.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 */
public class PdpMonitoringRest {
    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMonitoringRest.class);

    // The HTTP server exposing JAX-RS resources defined in this application.
    private HttpServletServer server;

    /**
     * Starts the HTTP server for the Pdp services client on the default base URI and with the
     * default REST packages.
     */
    public PdpMonitoringRest() {
        this(new PdpMonitoringRestParameters());
    }

    /**
     * Starts the HTTP server for the Pdp services client.
     *
     * @param parameters The Pdp parameters to use to start the server.
     * @return
     */
    public PdpMonitoringRest(@NonNull final PdpMonitoringRestParameters parameters) {

        LOGGER.debug("Pdp Monitoring starting . . .");
        server = HttpServletServerFactoryInstance.getServerFactory().build("PDP Monitoring", false,
                parameters.getServerHost(), parameters.getRestPort(), parameters.getContextPath(), false, false);
        server.addServletPackage(parameters.getDefaultRestPath(), parameters.getRestPackage());
        server.addDefaultServlet(null, PdpMonitoringRest.class.getClassLoader().getResource("webapp").toExternalForm());
        server.start();
        LOGGER.debug("Pdp Monitoring started");
    }



    /**
     * Shut down the web server.
     */
    public void shutdown() {
        LOGGER.debug("Pdp Monitoring . . .");
        server.shutdown();
        LOGGER.debug("Pdp Monitoring shut down");
    }
}
