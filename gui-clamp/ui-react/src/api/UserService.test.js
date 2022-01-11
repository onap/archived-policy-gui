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
import UserService from "./UserService";

describe("Verify Login", () => {
  let response;
  let expectedResponse;
  describe("Login Username Check", () => {
    expectedResponse = { name: "name" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "name" }),
          status: "Success",
          ok: true,
        })
      );
      response = await UserService.login();
    });
    it("Test login returns correct username", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Anonymouse Login Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await UserService.login();
    });
    it("Test login returns Anonymous user", () => {
      expect(response).toEqual("Anonymous");
    });
  });
});
describe("Verify GetUserInfo", () => {
  let response;
  let expectedResponse;
  describe("Test Correct Username", () => {
    expectedResponse = { name: "name" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "name" }),
          status: "Success",
          ok: true,
        })
      );
      response = await UserService.getUserInfo();
    });
    it("Test getUserInfo returns correct username", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Empty User Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await UserService.getUserInfo();
    });
    it("Test getUserInfo returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
