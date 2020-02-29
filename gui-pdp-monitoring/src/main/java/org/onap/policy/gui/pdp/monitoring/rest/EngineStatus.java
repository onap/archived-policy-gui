/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.gui.pdp.monitoring.rest;

import lombok.Setter;


/**
 * A POJO class to record engine worker status.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 *
 */
@Setter
@SuppressWarnings("unused")
class EngineStatus {
    private String timestamp;
    private String id;
    private String status;
    private String lastMessage;
    private Object upTime;
    private Object policyExecutions;
    private String lastPolicyDuration;
    private String averagePolicyDuration;
}
