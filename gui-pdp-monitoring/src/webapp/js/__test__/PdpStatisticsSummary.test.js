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

import * as PdpEngineSummary from "../PdpStatisticsSummary";

var data = [{averagePolicyDuration : [
    { timestamp: 1584979433034, value: 95 },
    { timestamp: 1584979428034, value: 78 },
    { timestamp: 1584979423034, value: 71 },
    { timestamp: 1584979418034, value: 79 },
    { timestamp: 1584979413034, value: 21 }]}]

test("create engine summary table", () => {
    PdpEngineSummary.createEngineSummaryTable();
    expect(document.getElementById("string")).toBeDefined();
})

test("set engine service data", () => {
    PdpEngineSummary.setEngineSummaryData(data, null, 1, 2, 3, 1, 2, 3)
    expect(document.getElementById("engineSummaryTable")).toBeDefined();
})