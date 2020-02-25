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

package org.onap.policy.gui.pdp.monitoring;

import lombok.NonNull;
import org.eclipse.jetty.servlets.CrossOriginFilter;
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

    // The HTTP server exposing static resources defined in this application.
    private HttpServletServer staticResourceServer;

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
     * @return
     */
    public PdpMonitoringServer(@NonNull final PdpMonitoringServerParameters parameters) {

        LOGGER.debug("Pdp Monitoring starting . . .");

        jerseyServer = HttpServletServerFactoryInstance.getServerFactory().build("PDP Monitoring Rest Server", false,
                parameters.getServerHost(), parameters.getDefaultRestPort(), parameters.getContextPath(), false, true);
        jerseyServer.addServletPackage(parameters.getDefaultRestPath(), parameters.getRestPackage());
        jerseyServer.addFilterClass(parameters.getDefaultRestPath(), CrossOriginFilter.class.getName());
        jerseyServer.start();

        staticResourceServer = HttpServletServerFactoryInstance.getServerFactory().buildStaticResourceServer(
                "PDP Monitoring Html Server", false, parameters.getServerHost(), parameters.getPort(),
                parameters.getContextPath(), true);
        staticResourceServer.addServletResource(null,
                PdpMonitoringServer.class.getClassLoader().getResource("webapp").toExternalForm());
        staticResourceServer.start();

        LOGGER.debug("Pdp Monitoring started");
    }



    /**
     * Shut down the web server.
     */
    public void shutdown() {
        LOGGER.debug("Pdp Monitoring . . .");
        HttpServletServerFactoryInstance.getServerFactory().destroy();
        LOGGER.debug("Pdp Monitoring shut down");
    }
}
