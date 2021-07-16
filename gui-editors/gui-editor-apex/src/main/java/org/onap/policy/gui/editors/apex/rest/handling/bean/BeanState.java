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

import java.util.Arrays;
import java.util.Map;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * The State Bean.
 */
@XmlType
@Getter
public class BeanState extends BeanBase {

    private String name = null;
    private BeanKeyRef trigger = null;
    @Setter
    private BeanKeyRef defaultTask = null;
    private BeanKeyRef[] contexts = null;
    private BeanLogic taskSelectionLogic = null;
    private Map<String, BeanStateTaskRef> tasks = null;
    private Map<String, BeanLogic> finalizers = null;
    private Map<String, BeanStateOutput> stateOutputs = null;

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "State [name=" + name + ", trigger=" + trigger + ", defaultTask=" + defaultTask + ", contexts="
            + Arrays.toString(contexts) + ", taskSelectionLogic=" + taskSelectionLogic + ", tasks=" + tasks
            + ", finalizers=" + finalizers + ", stateOutputs=" + stateOutputs + "]";
    }

}
