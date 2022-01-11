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
import ControlLoopService from "./ControlLoopService";

describe("Verify GetControlLoopInstantiation", () => {
  let response;
  let name = "name";
  let template = "template";
  global.fetch = jest.fn(() =>
    Promise.resolve({
      status: "Success",
      ok: true,
    })
  );
  response = ControlLoopService.getControlLoopInstantiation(name, template);
  it("Test getControlLoopInstantiation", () => {
    expect(response.status).toEqual(response.status);
  });
});
describe("Verify DeleteInstantiation", () => {
  let response;
  let name = "name";
  let version = "version";
  global.fetch = jest.fn(() =>
    Promise.resolve({
      status: "Success",
      ok: true,
    })
  );
  response = ControlLoopService.deleteInstantiation(name, version);
  it("Test deleteInstantiation", () => {
    expect(response.status).toEqual(response.status);
  });
});
