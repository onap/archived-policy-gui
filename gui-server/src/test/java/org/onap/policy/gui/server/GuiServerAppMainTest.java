/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.gui.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * In this test, we check that application can start via main() method.
 */
class GuiServerAppMainTest {

    @Test
    void whenMainIsCalled_thenNoExceptions() {
        String[] args = {
            "--server.port=0", // use random available port
            "--clamp.url=https://clamp-backend:8443/",
            "--clamp.disable-ssl-validation=true"
        };
        assertDoesNotThrow(() -> GuiServerApplication.main(args));
    }
}
