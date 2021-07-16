/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest.handling.bean;

import java.util.Map;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.ToString;

/**
 * The Event Bean.
 */
@XmlType
@Getter
@ToString
public class BeanEvent extends BeanBase {
    private String name = null;
    private String version = null;
    private String nameSpace = null;
    private String source = null;
    private String target = null;
    private String uuid = null;
    private String description = null;
    private Map<String, BeanField> parameters = null;

    /**
     * Gets the parameter.
     *
     * @param ps the parameter string
     * @return the parameter
     */
    public BeanField getParameter(final String ps) {
        if (parameters != null) {
            return parameters.get(ps);
        }
        return null;
    }
}
