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

package org.onap.policy.gui.editors.apex.rest.handling.plugin.upload;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

class UploadPolicyRequestDtoTest {
    private final Random random = new Random();

    @Test
    void testId() {
        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        final var id = random.nextLong();
        uploadPolicyRequestDto.setId(id);
        assertThat(uploadPolicyRequestDto.getId()).isEqualTo(id);
    }

    @Test
    void testUserId() {
        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        final var id = RandomStringUtils.randomAlphanumeric(5);
        uploadPolicyRequestDto.setUserId(id);
        assertThat(uploadPolicyRequestDto.getUserId()).isEqualTo(id);
    }

    @Test
    void testFileName() {
        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        final var filename = RandomStringUtils.randomAlphabetic(6);
        uploadPolicyRequestDto.setFilename(filename);
        assertThat(uploadPolicyRequestDto.getFilename()).isEqualTo(filename);
    }

    @Test
    void testFileData() {
        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        final var fileData = RandomStringUtils.randomAlphabetic(6);
        uploadPolicyRequestDto.setFileData(fileData);
        assertThat(uploadPolicyRequestDto.getFileData()).isEqualTo(fileData);
    }
}
