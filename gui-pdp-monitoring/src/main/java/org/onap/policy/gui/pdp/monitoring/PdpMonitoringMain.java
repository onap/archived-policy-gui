/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for Pdp Statistics Monitoring.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 */
@ToString
public class PdpMonitoringMain {
    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMonitoringMain.class);

    // Recurring string constants
    private static final String PDP_MONITORING_PREFIX = "Pdp Monitoring GUI (";

    // Services state
    public enum ServicesState {
        STOPPED, READY, INITIALIZING, RUNNING
    }

    @Getter
    private ServicesState state = ServicesState.STOPPED;

    // The parameters for the Server
    private PdpMonitoringServerParameters parameters = null;

    // The Pdp Monitoring services this class is running
    private PdpMonitoringServer pdpMonitoringServer = null;

    private CountDownLatch startedLatch = new CountDownLatch(1);

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    /**
     * Constructor, kicks off the GUI service.
     *
     * @param args The command line arguments for the RESTful service
     */
    public PdpMonitoringMain(final String[] args) {

        // Server parameter parsing
        final var parser = new PdpMonitoringServerParameterParser();

        try {
            // Get and check the parameters
            parameters = parser.parse(args);
        } catch (final PdpMonitoringServerParameterException e) {
            throw new PdpMonitoringServerParameterException(PDP_MONITORING_PREFIX + this + ") parameter error, "
                    + e.getMessage() + '\n' + parser.getHelp(PdpMonitoringMain.class.getName()), e);
        }

        if (parameters.isHelpSet()) {
            throw new PdpMonitoringServerParameterException(parser.getHelp(PdpMonitoringMain.class.getName()));
        }

        // Validate the parameters
        final var validationResult = parameters.validate();
        if (!validationResult.isValid()) {
            throw new PdpMonitoringServerParameterException(
                    PDP_MONITORING_PREFIX + this + ") parameters invalid, " + validationResult.getResult() + '\n'
                            + parser.getHelp(PdpMonitoringMain.class.getName()));
        }

        state = ServicesState.READY;
    }

    /**
     * Initialize the rest service.
     */
    public void init() {
        LOGGER.info(PDP_MONITORING_PREFIX + "{}) starting at {} . . .", this, parameters.getBaseUri());

        try {
            state = ServicesState.INITIALIZING;

            // Start the Pdp Monitoring service
            pdpMonitoringServer = new PdpMonitoringServer(parameters);

            // Add a shutdown hook to shut down the servlet services when the process is exiting
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            state = ServicesState.RUNNING;

            if (parameters.getTimeToLive() == PdpMonitoringServerParameters.INFINITY_TIME_TO_LIVE) {
                LOGGER.info(PDP_MONITORING_PREFIX + "{}) starting at {} . . .", this, parameters.getTimeToLive());
            } else {
                LOGGER.info(PDP_MONITORING_PREFIX + "{}) started", this);
            }

            startedLatch.countDown();

            // Find out how long is left to wait
            long timeRemaining = parameters.getTimeToLive();
            if (timeRemaining >= 0) {
                if (!shutdownLatch.await(timeRemaining, TimeUnit.SECONDS)) {
                    shutdown();
                }
            } else {
                shutdownLatch.await();
            }
        } catch (final Exception e) {
            LOGGER.warn("{} failed with error", this, e);
        } finally {
            shutdown();
        }

    }

    /**
     * Waits for the service to enter the running state.
     *
     * @param timeout time to wait
     * @param unit time units
     * @return {@code true} if the service started within the specified time
     * @throws InterruptedException if an interrupt occurs
     */
    protected boolean awaitStart(long timeout, TimeUnit unit) throws InterruptedException {
        return startedLatch.await(timeout, unit);
    }

    /**
     * Explicitly shut down the services.
     */
    public void shutdown() {
        if (pdpMonitoringServer != null) {
            LOGGER.info(PDP_MONITORING_PREFIX + "{}) shutting down", this);
            pdpMonitoringServer.shutdown(parameters.getPort());
        }
        shutdownLatch.countDown();
        state = ServicesState.STOPPED;
        LOGGER.info(PDP_MONITORING_PREFIX + "{}) shutting down", this);
    }

    /**
     * Main method, main entry point for command.
     *
     * @param args The command line arguments for the GUI
     */
    public static void main(final String[] args) {
        try {
            final var main = new PdpMonitoringMain(args);
            main.init();
        } catch (final Exception e) {
            LOGGER.error("start failed", e);
        }
    }
}
