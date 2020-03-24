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

import * as sut from "../MonitoringMain";

import * as MonitoringChart from "../MonitoringChart";
import * as MonitoringUtils from "../MonitoringUtils";
import * as PdpInformation from "../PdpInformation";
import * as PdpStatisticsSummary from "../PdpStatisticsSummary";
import * as PdpEngineWorkerStatus from "../PdpEngineWorkerStatus";

test("test ready", () => {
    MonitoringChart.initTooltip = jest.fn();
    MonitoringUtils.setUpPage = jest.fn();
    MonitoringUtils.getEngineURL = jest.fn();
    sut.readyCallback();
    expect(MonitoringChart.initTooltip).toHaveBeenCalled();
    expect(MonitoringUtils.setUpPage).toHaveBeenCalled();
    expect(MonitoringUtils.getEngineURL).toHaveBeenCalled();
});

test("test service callback", () => {
    const storedData = {
        useHttps: "http",
        hostname: "localhost",
        port: 7979,
        username: "username",
        password: "password",
    };

    const data = {
        server: "localhost",
        port: 7979,
    }
    window.localStorage.setItem("pap-monitor-services", JSON.stringify(storedData));
    PdpInformation.setEngineServiceData = jest.fn();
    PdpStatisticsSummary.setEngineSummaryData = jest.fn();
    PdpEngineWorkerStatus.setEngineStatusData = jest.fn();
    sut.servicesCallback(data);
    expect(PdpInformation.setEngineServiceData).toHaveBeenCalled();
    expect(PdpStatisticsSummary.setEngineSummaryData).toHaveBeenCalled();
    expect(PdpEngineWorkerStatus.setEngineStatusData).toHaveBeenCalled();
});