/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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
import LoopActionService from "./LoopActionService";

describe("Verify PerformAction", () => {
  let response;
  let cl_name = "name";
  let uiAction = "action";
  describe("Response Ok Check", () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        status: "Success",
        ok: true,
      })
    );
    beforeEach(async () => {
      response = await LoopActionService.performAction(cl_name, uiAction);
    });
    it("Test performAction returns correct response status", () => {
      expect(response.ok).toEqual(true);
    });
  });
});
describe("Verify RefreshStatus", () => {
  let response;
  let cl_name = "name";
  describe("Response Ok Check", () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve({ name: "name" }),
        status: "Success",
        ok: true,
      })
    );
    beforeEach(async () => {
      response = await LoopActionService.refreshStatus(cl_name);
    });
    it("Test refreshStatus returns correct response", () => {
      expect(response).toEqual({ name: "name" });
    });
  });
});
