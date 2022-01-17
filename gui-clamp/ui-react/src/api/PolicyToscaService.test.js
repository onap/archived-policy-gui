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
import PolicyToscaService from "./PolicyToscaService";

describe("Verify GetToscaPolicyModels", () => {
  let response;
  let expectedResponse;
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModels();
    });
    it("Test getToscaPolicyModels returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Empty Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModels();
    });
    it("Test getToscaPolicyModels returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify GetToscaPolicyModelYaml", () => {
  let response;
  let expectedResponse;
  let modelType = "type";
  let version = "version";
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModelYaml(
        modelType,
        version
      );
    });
    it("Test getToscaPolicyModelYaml returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Empty Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModelYaml(
        modelType,
        version
      );
    });
    it("Test getToscaPolicyModelYaml returns empty response", () => {
      expect(response).toEqual("");
    });
  });
});
describe("Verify GetToscaPolicyModel", () => {
  let response;
  let expectedResponse;
  let modelType = "type";
  let version = "version";
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModel(
        modelType,
        version
      );
    });
    it("Test getToscaPolicyModel returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Empty Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await PolicyToscaService.getToscaPolicyModel(
        modelType,
        version
      );
    });
    it("Test getToscaPolicyModel returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
