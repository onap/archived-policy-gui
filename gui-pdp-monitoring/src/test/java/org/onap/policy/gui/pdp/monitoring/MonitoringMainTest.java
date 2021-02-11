/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test the periodic event manager utility.
 */
public class MonitoringMainTest {
    @Test
    public void testMonitoringServerBad() {
        try {
            final String[] eventArgs = {"-z"};

            PdpMonitoringMain.main(eventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringServerOk() {
        try {
            final String[] eventArgs = {"-t", "1"};

            PdpMonitoringMain.main(eventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringServerBadOptions() {
        final String[] eventArgs = {"-zabbu"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class)
                .hasMessageContaining("parameter error, invalid command line arguments specified");

    }

    @Test
    public void testMonitoringServerHelp() {
        final String[] eventArgs = {"-h"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class).hasMessageContaining(
                "usage: org.onap.policy.gui.pdp.monitoring.PdpMonitoringMain [options...]");
    }

    @Test
    public void testMonitoringServerPortBad() {
        final String[] eventArgs = {"-p", "hello"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class)
                .hasMessageContaining("error parsing argument \"port\"");
    }

    @Test
    public void testMonitoringServerPortNegative() {
        final String[] eventArgs = {"-p", "-1"};
        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class).hasMessageContaining(
                "item \"port\" value \"-1\" INVALID, is below the minimum value: 1024");
    }

    @Test
    public void testMonitoringServerTtlTooSmall() {
        final String[] eventArgs = {"-t", "-2"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class)
                .hasMessageContaining("item \"timeToLive\" value \"-2\" INVALID, is below the minimum value: -1");
    }

    @Test
    public void testMonitoringServerTooManyPars() {
        final String[] eventArgs = {"-t", "10", "-p", "12344", "aaa", "bbb"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class)
                .hasMessageContaining("parameter error, too many command line arguments specified : [aaa, bbb]");
    }

    @Test
    public void testMonitoringServerTtlNotNumber() {
        final String[] eventArgs = {"-t", "timetolive"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class).hasMessageContaining(
                "parameter error, error parsing argument \"time-to-live\"");
    }

    @Test
    public void testMonitoringServerPortTooBig() {
        final String[] eventArgs = {"-p", "65536"};

        Throwable thrown = catchThrowable(() -> new PdpMonitoringMain(eventArgs));
        assertThat(thrown).isInstanceOf(PdpMonitoringServerParameterException.class)
                .hasMessageContaining("item \"port\" value \"65536\" INVALID, exceeds the maximum value: 65534");
    }

    @Test
    public void testMonitoringOneSecStart() {
        final String[] eventArgs = {"-t", "1"};

        try {
            PdpMonitoringMain monRestMain = new PdpMonitoringMain(eventArgs);
            monRestMain.init();
            monRestMain.shutdown();

        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testMonitoringForeverStart() {
        final String[] eventArgs = {"-t", "-1"};

        PdpMonitoringMain monRestMain = new PdpMonitoringMain(eventArgs);

        Thread monThread = new Thread() {
            @Override
            public void run() {
                monRestMain.init();
            }
        };

        try {
            monThread.start();
            assertThat(monRestMain.awaitStart(3, TimeUnit.SECONDS)).isTrue();
            monRestMain.shutdown();
        } catch (Exception ex) {
            monRestMain.shutdown();
            fail("test should not throw an exception");
        }
    }
}
