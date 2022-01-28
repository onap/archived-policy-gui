/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest.handling;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestSessionTest {

    private int sessionId;
    private RestSession restSession;

    @BeforeEach
    void setUp() {
        sessionId = new Random().nextInt();
        restSession = new RestSession(sessionId);
    }

    @Test
    void testGetSessionId() {
        final var actual = restSession.getSessionId();
        assertThat(actual).isEqualTo(sessionId);
    }

    @Test
    void testCommitChangesNoChanges() {
        final var apexApiResult = restSession.commitChanges();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    void testCommitChanges() {
        restSession.editModel();
        final var apexApiResult = restSession.commitChanges();
        assertThat(apexApiResult.isOk()).isTrue();
    }

    @Test
    void testDiscardChangesNotEdited() {
        final var apexApiResult = restSession.discardChanges();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    void testDiscardChanges() {
        restSession.editModel();
        final var apexApiResult = restSession.discardChanges();
        assertThat(apexApiResult.isOk()).isTrue();
        assertThat(restSession.getApexModelEdited()).isNull();
    }

    @Test
    void testDownloadModel() {
        final var actual = restSession.downloadModel();
        assertThat(actual.isOk()).isTrue();
    }

    @Test
    void testEditModel() {
        final var original = restSession.getApexModelEdited();
        final var apexApiResult = restSession.editModel();
        final var apexModelEdited = restSession.getApexModelEdited();
        final var apexModel = restSession.getApexModel();
        assertThat(apexApiResult.isOk()).isTrue();
        assertThat(original).isNull();
        assertThat(apexModelEdited).isNotNull();
        assertThat(apexModel).isNotNull();
    }

    @Test
    void testEditModelAlreadyEdited() {
        restSession.editModel();
        final var apexApiResult = restSession.editModel();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    void testLoadFromString() throws IOException {
        restSession.editModel();
        final var toscaPath = Path.of("src/test/resources/models/PolicyModel.yaml");
        final var toscaString = Files.readString(toscaPath);
        final var apexApiResult = restSession.loadFromString(toscaString);
        assertThat(apexApiResult.isOk()).isTrue();
        final var apexModelEdited = restSession.getApexModelEdited();
        assertThat(apexModelEdited).isNotNull();
    }

    @Test
    void testLoadFromStringNoPolicies() throws IOException {
        restSession.editModel();
        final var toscaPath = Path.of("src/test/resources/models/PolicyModelNoPolicies.yaml");
        final var toscaString = Files.readString(toscaPath);
        final var apexApiResult = restSession.loadFromString(toscaString);
        assertThat(apexApiResult.isNok()).isTrue();
        assertThat(apexApiResult.getMessage()).contains("no policies");
    }
}
