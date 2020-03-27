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

import * as PdpListView from "../PdpListView";
const pdpArray = [];

test("render pdp list", () => {
    const data = {
    groups: [{
        pdpSubgroups: [{
            pdpType: "apex",
            pdpInstances: [{
                instanceId: "apex-pdp1",
             }],
            }]
        }
        ],
    };

    for (let i = 0; i < data.groups.length; i++) {
        var map = {};
        map.title = data.groups[i].name;
        map.children = [];
        (data.groups[i].pdpSubgroups).forEach((pdpSubgroup, index) => {
            map.children[index] = {};
            map.children[index].title = pdpSubgroup.pdpType;
            const instanceId = [];
            pdpSubgroup.pdpInstances.forEach(pdpInstance => {
                var instanceIdMap = {};
                instanceIdMap.title = pdpInstance.instanceId;
                instanceId.push(instanceIdMap)
            });
            map.children[index].children = instanceId;
        });
    pdpArray.push(map);
    };

    document.body.innerHTML = '<ul class="pdps__list"></ul>';
    PdpListView.RenderPdpList(pdpArray, "pdps__list");
    expect(document.querySelector('a.' + 'pdps__link').innerHTML).toBe("apex-pdp1");
});

test("high light selected", () => {
    PdpListView.highlightSelected(1);
    expect(document.querySelector(`.pdps__link[href*="${1}"]`)).toBeDefined();
})