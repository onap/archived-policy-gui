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

package org.onap.policy.gui.server.config;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.onap.policy.gui.server.test.util.hello.HelloWorldApplication;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * In this test, server.ssl.trust-store is unset while SSL validation is enabled.
 * An BeanCreationException should be thrown on application startup.
 */
@SpringBootTest(
    classes = {
        HelloWorldApplication.class
    }
)
class RestTemplateTrustStoreUnsetTest {
    BaseRestTemplateConfig[] restTemplateConfigArray = {
        new AcmRuntimeRestTemplateConfig(),
        new PolicyApiRestTemplateConfig(),
        new PolicyPapRestTemplateConfig()
    };

    @Test
    void expectExceptionWithNoTrustStore(ApplicationContext context) {
        Arrays.asList(restTemplateConfigArray).forEach(restTemplateConfig -> {
            // Manually autowire the bean so we can test PostConstruct logic.
            AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
            factory.autowireBean(restTemplateConfig);

            // Enable SSL validation, but provide no trust store.
            ReflectionTestUtils.setField(restTemplateConfig, "disableSslValidation", false);

            // Expect exception when creating bean.
            assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> factory.initializeBean(restTemplateConfig, "dummyRestTemplate"))
                .withMessageContaining("server.ssl.trust-store must be set");
        });
    }
}
