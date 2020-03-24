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

import * as PdpEngineWorkerStatus from "../PdpEngineWorkerStatus";
import * as MonitoringChart from "../MonitoringChart";

var id = jest.fn(() => {
    return 1;
})
var startStopStatus = jest.fn(() => {
    return "STOPPED";
})

test("set engine status data", () => {
    var temp = MonitoringChart.generateRandomData();
    var engineStatusData = [{id : 1, lastPolicyDuration : 1, averagePolicyDuration: 1}];
    var changed = jest.fn(() => {
        return false;
    });

    engineStatusData[0].lastPolicyDuration = temp;
    engineStatusData[0].averagePolicyDuration = temp;
    window.engineStatusTables = [];
    PdpEngineWorkerStatus.setEngineStatusData(engineStatusData, changed);

    expect(window.engineStatusTables[0]).toBeDefined();
});