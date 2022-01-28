/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest.handling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;

class KeyInfoHandlerTest {
    private final Random random = new Random();
    private KeyInfoHandler handler;

    @BeforeEach
    void setUp() {
        handler = new KeyInfoHandler();
    }

    @Test
    void testExecuteRestCommand() {
        final var sessionId = random.nextInt();
        final var session = new RestSession(sessionId);
        final var commandType = RestCommandType.EVENT;
        final var command = RestCommand.ANALYSE;

        final var actual = handler.executeRestCommand(session, commandType, command);

        assertThat(actual.getResult()).isEqualTo(ApexApiResult.Result.FAILED);
        assertThat(actual.getMessage()).contains(Integer.toString(sessionId));
        assertThat(actual.getMessage()).contains(commandType.toString());
        assertThat(actual.getMessage()).contains(command.toString());
    }

    @Test
    void testExecuteRestCommandWithJsonString() {
        final var sessionId = random.nextInt();
        final var session = new RestSession(sessionId);
        final var commandType = RestCommandType.EVENT;
        final var command = RestCommand.ANALYSE;
        final var emptyString = "";

        final var actual = handler.executeRestCommand(session, commandType, command, emptyString);

        assertThat(actual.getResult()).isEqualTo(ApexApiResult.Result.FAILED);
        assertThat(actual.getMessage()).contains(Integer.toString(sessionId));
        assertThat(actual.getMessage()).contains(commandType.toString());
        assertThat(actual.getMessage()).contains(command.toString());
    }

    @Test
    void testExecuteRestCommandWithNameAndVersion() {
        final var sessionId = random.nextInt();
        final var session = new RestSession(sessionId);
        final var commandType = RestCommandType.EVENT;
        final var command = RestCommand.ANALYSE;
        final var name = "";
        final var version = "";

        final var actual = handler.executeRestCommand(session, commandType, command, name, version);

        assertThat(actual.getResult()).isEqualTo(ApexApiResult.Result.FAILED);
        assertThat(actual.getMessage()).contains(Integer.toString(sessionId));
        assertThat(actual.getMessage()).contains(commandType.toString());
        assertThat(actual.getMessage()).contains(command.toString());
    }

    @Test
    void testExecuteRestCommandWithNameAndVersion2() {
        final var session = Mockito.mock(RestSession.class);
        final var commandType = RestCommandType.KEY_INFO;
        final var command = RestCommand.LIST;
        final var name = "";
        final var version = "version";
        final var expected = new ApexApiResult();
        final var apexModel = Mockito.mock(ApexModel.class);

        Mockito.when(session.getApexModel()).thenReturn(apexModel);
        Mockito.when(apexModel.listKeyInformation(null, version)).thenReturn(expected);

        final var actual = handler.executeRestCommand(session, commandType, command, name, version);

        assertThat(actual).isEqualTo(expected);
    }
}
