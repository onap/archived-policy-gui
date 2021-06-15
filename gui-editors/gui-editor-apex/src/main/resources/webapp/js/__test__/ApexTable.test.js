/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

const ApexTable = require("../ApexTable")

test("Test createTable", () => {
    const expected = document.createElement("table");
    expected.id = "my-id";
    expected.className = "apexTable ebTable elTablelib-Table-table ebTable_striped";

    const actual = ApexTable.createTable(expected.id);
    expect(actual).toEqual(actual);
});

test("Test setRowHover", () => {
    const element = {
        className: null,
        onmouseover: null,
        onmouseout: null
    };

    ApexTable.setRowHover(element);

    expect(element.className).toBe("ebTableRow");
    expect(typeof element.onmouseover).toBe("function");
    expect(typeof element.onmouseout).toBe("function");
});
