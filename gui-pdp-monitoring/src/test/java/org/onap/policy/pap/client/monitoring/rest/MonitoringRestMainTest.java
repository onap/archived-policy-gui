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

package org.onap.policy.pap.client.monitoring.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.onap.policy.gui.pdp.monitoring.rest.PdpMonitoringRest;
import org.onap.policy.gui.pdp.monitoring.rest.PdpMonitoringRestMain;
import org.onap.policy.gui.pdp.monitoring.rest.PdpMonitoringRestParameterException;

/**
 * Test the periodic event manager utility.
 */
public class MonitoringRestMainTest {
    @Test
    public void testMonitoringClientBad() {
        try {
            final String[] eventArgs = {"-z"};

            PdpMonitoringRestMain.main(eventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringClientOk() {
        try {
            final String[] eventArgs = {"-t", "1"};

            PdpMonitoringRestMain.main(eventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringClientBadOptions() {
        final String[] eventArgs = {"-zabbu"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class)
                .hasMessageContaining("invalid command line arguments specified : Unrecognized option: -zabbu");

    }

    @Test
    public void testMonitoringClientHelp() {
        final String[] eventArgs = {"-h"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class).hasMessageContaining(
                "usage: org.onap.policy.gui.pdp.monitoring.rest.PdpMonitoringRestMain [options...]");
    }

    @Test
    public void testMonitoringClientPortBad() {
        final String[] eventArgs = {"-p", "hello"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class)
                .hasMessageContaining("parameter error, error parsing argument \"port\" :For input string: \"hello\"");
    }

    @Test
    public void testMonitoringClientPortNegative() {
        final String[] eventArgs = {"-p", "-1"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class).hasMessageContaining(
                "parameters invalid, port must be greater than 1023 and less than 65536");
    }

    @Test
    public void testMonitoringClientTtlTooSmall() {
        final String[] eventArgs = {"-t", "-2"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class)
                .hasMessageContaining("time to live must be greater than -1 (set to -1 to wait forever)");
    }

    @Test
    public void testMonitoringClientTooManyPars() {
        final String[] eventArgs = {"-t", "10", "-p", "12344", "aaa", "bbb"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class)
                .hasMessageContaining("parameter error, too many command line arguments specified : [aaa, bbb]");
    }

    @Test
    public void testMonitoringClientTtlNotNumber() {
        final String[] eventArgs = {"-t", "timetolive"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class).hasMessageContaining(
                "parameter error, error parsing argument \"time-to-live\" :" + "For input string: \"timetolive\"");
    }

    @Test
    public void testMonitoringClientPortTooBig() {
        final String[] eventArgs = {"-p", "65536"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringRestMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringRestParameterException.class)
                .hasMessageContaining("port must be greater than 1023 and less than 65536");
    }

    @Test
    public void testMonitoringClientDefaultPars() {
        try {
            PdpMonitoringRest monRest = new PdpMonitoringRest();
            monRest.shutdown();

        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringOneSecStart() {
        final String[] eventArgs = {"-t", "1"};

        try {
            PdpMonitoringRestMain monRestMain = new PdpMonitoringRestMain(eventArgs);
            monRestMain.init();
            monRestMain.shutdown();

        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringForeverStart() {
        final String[] eventArgs = {"-t", "-1"};

        PdpMonitoringRestMain monRestMain = new PdpMonitoringRestMain(eventArgs);

        Thread monThread = new Thread() {
            @Override
            public void run() {
                monRestMain.init();
            }
        };

        try {
            monThread.start();
            Thread.sleep(2000);
            monRestMain.shutdown();
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }
}
