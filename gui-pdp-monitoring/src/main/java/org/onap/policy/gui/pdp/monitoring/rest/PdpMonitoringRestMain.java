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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.ToString;
import org.onap.policy.common.parameters.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for Pdp Restful Monitoring.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 */
@ToString
public class PdpMonitoringRestMain {
    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMonitoringRestMain.class);

    // Recurring string constants
    private static final String REST_ENDPOINT_PREFIX = "Pdp Monitoring REST endpoint (";

    // Services state
    public enum ServicesState {
        STOPPED, READY, INITIALIZING, RUNNING
    }

    @Getter
    private ServicesState state = ServicesState.STOPPED;

    // The parameters for the client
    private PdpMonitoringRestParameters parameters = null;

    // The Pdp services client this class is running
    private PdpMonitoringRest pdpMonitoringRest = null;

    /**
     * Constructor, kicks off the rest service.
     *
     * @param args The command line arguments for the RESTful service
     */
    public PdpMonitoringRestMain(final String[] args) {

        // Client parameter parsing
        final PdpMonitoringRestParameterParser parser = new PdpMonitoringRestParameterParser();

        try {
            // Get and check the parameters
            parameters = parser.parse(args);
        } catch (final PdpMonitoringRestParameterException e) {
            throw new PdpMonitoringRestParameterException(REST_ENDPOINT_PREFIX + this + ") parameter error, "
                    + e.getMessage() + '\n' + parser.getHelp(PdpMonitoringRestMain.class.getName()), e);
        }

        if (parameters.isHelpSet()) {
            throw new PdpMonitoringRestParameterException(parser.getHelp(PdpMonitoringRestMain.class.getName()));
        }

        // Validate the parameters
        final ValidationResult validationResult = parameters.validate();
        if (!validationResult.isValid()) {
            throw new PdpMonitoringRestParameterException(
                    REST_ENDPOINT_PREFIX + this + ") parameters invalid, " + validationResult.getResult() + '\n'
                            + parser.getHelp(PdpMonitoringRestMain.class.getName()));
        }

        state = ServicesState.READY;
    }

    /**
     * Initialize the rest service.
     */
    public void init() {
        LOGGER.info(REST_ENDPOINT_PREFIX + "{}) starting at {} . . .", this, parameters.getBaseUri());

        try {
            state = ServicesState.INITIALIZING;

            // Start the REST service
            pdpMonitoringRest = new PdpMonitoringRest(parameters);

            // Add a shutdown hook to shut down the rest services when the process is exiting
            Runtime.getRuntime().addShutdownHook(new Thread(new PdpServicesShutdownHook()));

            state = ServicesState.RUNNING;

            if (parameters.getTimeToLive() == PdpMonitoringRestParameters.INFINITY_TIME_TO_LIVE) {
                LOGGER.info(REST_ENDPOINT_PREFIX + "{}) starting at {} . . .", this, parameters.getBaseUri());
            } else {
                LOGGER.info(REST_ENDPOINT_PREFIX + "{}) started", this);
            }

            // Find out how long is left to wait
            long timeRemaining = parameters.getTimeToLive();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            if (timeRemaining >= 0) {
                countDownLatch.await(timeRemaining, TimeUnit.SECONDS);
            } else {
                countDownLatch.await();
            }
        } catch (final Exception e) {
            LOGGER.warn(REST_ENDPOINT_PREFIX + "{}) failed with error: {}", this, e);
        } finally {
            shutdown();
        }

    }

    /**
     * Explicitly shut down the services.
     */
    public void shutdown() {
        if (pdpMonitoringRest != null) {
            LOGGER.info(REST_ENDPOINT_PREFIX + "{}) shutting down", this);
            pdpMonitoringRest.shutdown();
        }
        state = ServicesState.STOPPED;
        LOGGER.info(REST_ENDPOINT_PREFIX + "{}) shutting down", this);
    }

    /**
     * This class is a shutdown hook for the Pdp services command.
     */
    private class PdpServicesShutdownHook implements Runnable {
        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            shutdown();
        }
    }

    /**
     * Main method, main entry point for command.
     *
     * @param args The command line arguments for the client
     */
    public static void main(final String[] args) {
        try {
            final PdpMonitoringRestMain restMain = new PdpMonitoringRestMain(args);
            restMain.init();
        } catch (final Exception e) {
            LOGGER.error("start failed", e);
        }
    }
}
