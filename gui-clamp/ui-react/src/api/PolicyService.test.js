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
import PolicyService from "./PolicyService";

describe("Verify GetPoliciesList", () => {
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
      response = await PolicyService.getPoliciesList();
    });
    it("Test getPoliciesList returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Error Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Failed",
          ok: false,
        })
      );
      response = PolicyService.getPoliciesList();
    });
    it("Test getPoliciesList returns correct error", () => {
      expect(response).rejects.toEqual(
        new Error("HTTP Failed," + { name: "loop1" })
      );
    });
  });
});
describe("Verify CreateNewPolicy", () => {
  let response;
  let expectedResponse;
  let modelType = "modelType";
  let modelVersion = "modelVersion";
  let policyName = "policyName";
  let policyVersion = "policyVersion";
  let json = {};
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyService.createNewPolicy(
        modelType,
        modelVersion,
        policyName,
        policyVersion,
        json
      );
    });
    it("Test createNewPolicy returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Error Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Failed",
          ok: false,
        })
      );
      response = PolicyService.createNewPolicy(
        modelType,
        modelVersion,
        policyName,
        policyVersion,
        json
      );
    });
    it("Test getPoliciesList returns correct error", () => {
      expect(response).rejects.toEqual(
        new Error("HTTP Failed," + { name: "loop1" })
      );
    });
  });
});
describe("Verify DeletePolicy", () => {
  let response;
  let expectedResponse;
  let modelType = "modelType";
  let modelVersion = "modelVersion";
  let policyName = "policyName";
  let policyVersion = "policyVersion";
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyService.deletePolicy(
        modelType,
        modelVersion,
        policyName,
        policyVersion
      );
    });
    it("Test deletePolicy returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Error Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Failed",
          ok: false,
        })
      );
      response = PolicyService.deletePolicy(
        modelType,
        modelVersion,
        policyName,
        policyVersion
      );
    });
    it("Test deletePolicy returns correct error", () => {
      expect(response).rejects.toEqual(
        new Error("HTTP Failed," + { name: "loop1" })
      );
    });
  });
});
describe("Verify UpdatePdpDeployment", () => {
  let response;
  let expectedResponse;
  let operationList = "operation";
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyService.updatePdpDeployment(operationList);
    });
    it("Test updatePdpDeployment returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Error Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Failed",
          ok: false,
        })
      );
      response = PolicyService.updatePdpDeployment(operationList);
    });
    it("Test updatePdpDeployment returns correct error", () => {
      expect(response).rejects.toEqual(
        new Error("HTTP Failed," + { name: "loop1" })
      );
    });
  });
});
describe("Verify SendNewPolicyModel", () => {
  let response;
  let expectedResponse;
  let policyModel = "model";
  describe("Non-Empty Json Check", () => {
    expectedResponse = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await PolicyService.sendNewPolicyModel(policyModel);
    });
    it("Test sendNewPolicyModel returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Error Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve({ name: "loop1" }),
          status: "Failed",
          ok: false,
        })
      );
      response = PolicyService.sendNewPolicyModel(policyModel);
    });
    it("Test sendNewPolicyModel returns correct error", () => {
      expect(response).rejects.toEqual(
        new Error("HTTP Failed," + { name: "loop1" })
      );
    });
  });
});
