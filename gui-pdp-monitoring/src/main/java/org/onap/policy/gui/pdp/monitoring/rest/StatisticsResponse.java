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

import java.util.List;
import lombok.Setter;

/**
 * A POJO class to record Pdp statistics.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 *
 */
@Setter
@SuppressWarnings("unused")
class StatisticsResponse {
    private String engineId;
    private String server;
    private String port;
    private String healthStatus;
    private String pdpState;
    private String timeStamp;
    private Object policyDeployCount;
    private Object policyDeploySuccessCount;
    private Object policyDeployFailCount;
    private Object policyExecutedCount;
    private Object policyExecutedSuccessCount;
    private Object policyExecutedFailCount;
    private List<EngineStatus> status;
}
