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

// Configuration used for page layout and charts
const config = {
    refresh: 5000,
    engineService: {
        parent: "engineService",
        tableId: "engineServicesTable",
        headers: [{
            title: "Engine Service ID",
            id: "engine_id"
        }, {
            title: "GroupName",
            id: "group_name"
        }, {
            title: "SubGroupName",
            id: "subgroup_name"
        }, {
            title: "HealthStatus",
            id: "health_status"
        }, {
            title: "PdpState",
            id: "pdp_state"
        }, {
            title: "Pap server:port",
            id: "server_port"
        }]
    },
    engineSummary: {
        parent: "engineSummary",
        tableId: "engineSummaryTable",
        headers: [{
            title: "Timestamp",
            id: "timestamp"
        }, {
            title: "Sum of policy deploy",
            id: "policy_deploy"
        }, {
            title: "Sum of policy deploy success",
            id: "policy_deploy_success"
        }, {
            title: "Sum of policy deploy fail",
            id: "policy_deploy_fail"
        }, {
            title: "Sum of policy executions",
            id: "policy_executions"
        }, {
            title: "Sum of policy executions success",
            id: "policy_executions_success"
        }, {
            title: "Sum of policy executions fail",
            id: "policy_executions_fail"
        }, {
            title: "Up Time(ms)",
            id: "up_time"
        }],
        chart: {
            avgPolicyDurationChart: {
                parent: "avgPolicyDuration",
                title: "Average Policy Duration (ms)",
                unit: "ms",
                lineStroke: "#5FBADD",
                nodeColour: "#00A9D4"
            },
        }
    },
    engineStatus: {
        parent: "engineStatus",
        tableId: "engineStatusTable",
        headers: [{
            title: "Timestamp",
            id: "timestamp"
        }, {
            title: "Engine ID",
            id: "engine_id"
        }, {
            title: "Engine Status",
            id: "engine_status"
        }, {
            title: "Last Message",
            id: "last_message"
        }, {
            title: "Up Time (ms)",
            id: "up_time"
        }, {
            title: "Policy Executions",
            id: "policy_executions"
        }]
    },
    engineChart: {
        lastPolicyDurationChart: {
            parent: "lastPolicyDurationChart",
            title: "Last Policy Duration (ms)",
            unit: "ms",
            lineStroke: "#F5A241",
            nodeColour: "#F08A00"
        },
        averagePolicyDurationChart: {
            parent: "averagePolicyDurationChart",
            title: "Average Policy Duration (ms)",
            unit: "ms",
            lineStroke: "#00625F",
            nodeColour: "#007B78"
        }
    }
};

export { config, };