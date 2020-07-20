/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.gui.editors.apex.rest.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.Config;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;

@TestInstance(Lifecycle.PER_CLASS)
class ConfigurationRestResourceTest extends JerseyTest {

    private Config config;

    @Override
    protected Application configure() {
        config = mock(Config.class);
        return new ResourceConfig(ConfigurationRestResource.class).register(new AbstractBinder() {

            @Override
            protected void configure() {
                bind(config).to(Config.class).ranked(2);
            }
        });
    }

    // workaround for JerseyTest + JUnit5
    @BeforeAll
    void before() throws Exception {
        super.setUp();
    }

    // workaround for JerseyTest + JUnit5
    @AfterAll
    void after() throws Exception {
        super.tearDown();
    }

    @Test
    void testShowSuccess() {
        final String isEnabled = "true";
        when(config.getOptionalValue(PolicyUploadPluginConfigKey.ENABLE.getKey(), String.class))
            .thenReturn(Optional.of(isEnabled));
        final String aUrl = "url";
        when(config.getOptionalValue(PolicyUploadPluginConfigKey.URL.getKey(), String.class))
            .thenReturn(Optional.of(aUrl));
        Response response = target("/editor/config").request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        final ApexApiResult apexApiResult = response.readEntity(ApexApiResult.class);
        assertEquals(Result.SUCCESS, apexApiResult.getResult());
        final List<String> messageList = apexApiResult.getMessages();
        assertNotNull(messageList);
        assertFalse(messageList.isEmpty());
        assertEquals(2, messageList.size());
        final String urlResponse = String.format("{\"%s\": \"%s\"}", PolicyUploadPluginConfigKey.URL.getKey(), aUrl);
        final String isEnabledResponse =
            String.format("{\"%s\": \"%s\"}", PolicyUploadPluginConfigKey.ENABLE.getKey(), isEnabled);
        assertTrue(messageList.contains(urlResponse));
        assertTrue(messageList.contains(isEnabledResponse));

    }
}