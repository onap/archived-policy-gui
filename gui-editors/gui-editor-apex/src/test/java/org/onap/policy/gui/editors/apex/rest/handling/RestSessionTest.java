/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.gui.editors.apex.rest.ApexEditorMain;

public class RestSessionTest {

    private int sessionId;
    private RestSession restSession;

    @BeforeClass
    public static void beforeClass() {
        // Initialize ApexEditor
        final String[] args = {"--time-to-live", "10", "--port", "12321", "--listen", "127.0.0.1"};
        final var outBaStream = new ByteArrayOutputStream();
        final var outStream = new PrintStream(outBaStream);
        new ApexEditorMain(args, outStream);
    }

    @Before
    public void setUp() {
        sessionId = new Random().nextInt();
        restSession = new RestSession(sessionId);
    }

    @Test
    public void testGetSessionId() {
        final var actual = restSession.getSessionId();
        assertThat(actual).isEqualTo(sessionId);
    }

    @Test
    public void testCommitChangesNoChanges() {
        final var apexApiResult = restSession.commitChanges();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    public void testCommitChanges() {
        restSession.editModel();
        final var apexApiResult = restSession.commitChanges();
        assertThat(apexApiResult.isOk()).isTrue();
    }

    @Test
    public void testDiscardChangesNotEdited() {
        final var apexApiResult = restSession.discardChanges();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    public void testDiscardChanges() {
        restSession.editModel();
        final var apexApiResult = restSession.discardChanges();
        assertThat(apexApiResult.isOk()).isTrue();
        assertThat(restSession.getApexModelEdited()).isNull();
    }

    @Test
    public void testDownloadModel() {
        final var actual = restSession.downloadModel();
        assertThat(actual.isOk()).isTrue();
    }

    @Test
    public void testEditModel() {
        final var original = restSession.getApexModelEdited();
        final var apexApiResult = restSession.editModel();
        final var apexModelEdited = restSession.getApexModelEdited();
        final var apexModel = restSession.getApexModel();
        assertThat(apexApiResult.isOk()).isTrue();
        assertThat(original).isNull();
        assertThat(apexModel).isNotNull();
    }

    @Test
    public void testEditModelAlreadyEdited() {
        restSession.editModel();
        final var apexApiResult = restSession.editModel();
        assertThat(apexApiResult.isNok()).isTrue();
    }

    @Test
    public void testLoadFromString() throws IOException {
        restSession.editModel();
        final var toscaPath = Path.of("src/test/resources/models/PolicyModel.yaml");
        final var toscaString = Files.readString(toscaPath);
        final var apexApiResult = restSession.loadFromString(toscaString);
        assertThat(apexApiResult.isOk()).isTrue();
        final var apexModelEdited = restSession.getApexModelEdited();
        assertThat(apexModelEdited).isNotNull();
    }

    @Test
    public void testLoadFromStringNoPolicies() throws IOException {
        restSession.editModel();
        final var toscaPath = Path.of("src/test/resources/models/PolicyModelNoPolicies.yaml");
        final var toscaString = Files.readString(toscaPath);
        final var apexApiResult = restSession.loadFromString(toscaString);
        assertThat(apexApiResult.isNok()).isTrue();
        assertThat(apexApiResult.getMessage()).contains("no policies");
    }

    @Test
    public void testUploadModel() throws IOException {
        restSession.editModel();
        final var toscaPath = Path.of("src/test/resources/models/PolicyModel.yaml");
        final var toscaString = Files.readString(toscaPath);
        restSession.loadFromString(toscaString);
        final var apexApiResult = restSession.uploadModel();
        assertThat(apexApiResult.isNok()).isTrue();
        assertThat(apexApiResult.getMessage()).contains("Model upload is disabled");
    }
}
