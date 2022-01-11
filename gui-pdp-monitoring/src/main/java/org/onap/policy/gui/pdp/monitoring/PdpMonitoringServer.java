/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020 AT&T
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
public class PdpMonitoringServer {
    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMonitoringServer.class);

    // The HTTP server exposing JAX-RS resources defined in this application.
    private HttpServletServer jerseyServer;

    /**
     * Starts the HTTP server for the Pdp statistics monitoring on the default base URI and with the
     * default REST packages.
     */
    public PdpMonitoringServer() {
        this(new PdpMonitoringServerParameters());
    }

    /**
     * Starts the HTTP server for the Pdp statistics monitoring GUI.
     *
     * @param parameters The Pdp parameters to use to start the server.
     */
    public PdpMonitoringServer(@NonNull final PdpMonitoringServerParameters parameters) {
        LOGGER.debug("Pdp Monitoring starting . . .");

        jerseyServer = HttpServletServerFactoryInstance.getServerFactory().build("PDP Monitoring Rest Server", false,
                parameters.getServerHost(), parameters.getPort(), parameters.getContextPath(), false, true);
        jerseyServer.addServletPackage(parameters.getDefaultRestPath(), parameters.getRestPackage());
        jerseyServer.start();

        LOGGER.debug("Pdp Monitoring started");
    }

    /**
     * Shut down the web server.
     *
     * @param restPort port number of jersey server
     */
    public void shutdown(int restPort) {
        LOGGER.debug("Pdp Monitoring . . .");
        HttpServletServerFactoryInstance.getServerFactory().destroy(restPort);
        LOGGER.debug("Pdp Monitoring shut down");
    }
}