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
import LoopService from "./LoopService";

describe("Verify GetLoopNames", () => {
  let response;
  let loopNameJson;
  describe("Non-Empty Json Check", () => {
    loopNameJson = { name: "loop1" };
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve({ name: "loop1" }),
        status: "Success",
        ok: true,
      })
    );
    beforeEach(async () => {
      response = await LoopService.getLoopNames();
    });
    it("Test getLoopNames returns correct json", () => {
      expect(response).toEqual(loopNameJson);
    });
  });
  describe("Empty Json Check", () => {
    loopNameJson = {};
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve({}),
        status: "Failed",
        ok: false,
      })
    );
    beforeEach(async () => {
      response = await LoopService.getLoopNames();
    });
    it("Test getLoopNames returns empty json", () => {
      expect(response).toEqual(loopNameJson);
    });
  });
  describe("Error during API call", () => {
    loopNameJson = {};
    beforeEach(async () => {
      response = await LoopService.getLoopNames();
    });
    it("Test getLoopNames returns empty json", () => {
      expect(response).toEqual(loopNameJson);
    });
  });
});

describe("Verify CreateLoop", () => {
  let response;
  let loopNameJson;
  let loopName = "loop1";
  let templateName = "template1";
  describe("Non-Empty Json Check", () => {
    loopNameJson = { name: "loop1" };
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await LoopService.createLoop(loopName, templateName);
    });

    it("Test createLoop returns correct json", () => {
      expect(response).toEqual(loopNameJson);
    });
  });
  describe("Error during API call", () => {
    let emptyResponse = "";
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve(""),
        })
      );
      response = await LoopService.createLoop("", "");
    });
    it("Test createLoop returns empty", () => {
      expect(response).toEqual(emptyResponse);
    });
  });
});
describe("Verify GetLoop", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  describe("Non-Empty Json Check", () => {
    beforeEach(async () => {
      expected = { name: "loop1" };
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.getLoop(name);
    });

    it("Test getLoop returns correct json", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = {};
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({}),
        })
      );
      actualResponse = await LoopService.getLoop(name);
    });
    it("Test getLoop returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify SetMicroServiceProperties", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let jsonData = {};
  describe("Non-Empty Text Check", () => {
    beforeEach(async () => {
      expected = "data";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve("data"),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.setMicroServiceProperties(
        name,
        jsonData
      );
    });

    it("Test setMicroServiceProperties returns correct text", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = "";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve(""),
        })
      );
      actualResponse = await LoopService.setMicroServiceProperties(
        name,
        jsonData
      );
    });
    it("Test setMicroServiceProperties returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify SetOperationalPolicyProperties", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let jsonData = {};
  describe("Non-Empty Text Check", () => {
    beforeEach(async () => {
      expected = "data";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve("data"),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.setOperationalPolicyProperties(
        name,
        jsonData
      );
    });

    it("Test setOperationalPolicyProperties returns correct text", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = "";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve(""),
        })
      );
      actualResponse = await LoopService.setOperationalPolicyProperties(
        name,
        jsonData
      );
    });
    it("Test setOperationalPolicyProperties returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify UpdateGlobalProperties", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let jsonData = {};
  describe("Non-Empty Text Check", () => {
    beforeEach(async () => {
      expected = "data";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve("data"),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.updateGlobalProperties(name, jsonData);
    });

    it("Test updateGlobalProperties returns correct text", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = "";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve(""),
        })
      );
      actualResponse = await LoopService.updateGlobalProperties(name, jsonData);
    });
    it("Test updateGlobalProperties returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify RefreshOperationalPolicyJson", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let policyName = "policy";
  describe("Non-Empty Json Check", () => {
    beforeEach(async () => {
      expected = { name: "loop1" };
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.refreshOperationalPolicyJson(
        name,
        policyName
      );
    });

    it("Test refreshOperationalPolicyJson returns correct json", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = {};
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({}),
        })
      );
      actualResponse = await LoopService.refreshOperationalPolicyJson(
        name,
        policyName
      );
    });
    it("Test refreshOperationalPolicyJson returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify RefreshMicroServicePolicyJson", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let policyName = "policy";
  describe("Non-Empty Json Check", () => {
    beforeEach(async () => {
      expected = { name: "loop1" };
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.refreshMicroServicePolicyJson(
        name,
        policyName
      );
    });

    it("Test refreshMicroServicePolicyJson returns correct json", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = {};
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({}),
        })
      );
      actualResponse = await LoopService.refreshMicroServicePolicyJson(
        name,
        policyName
      );
    });
    it("Test refreshMicroServicePolicyJson returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify AddOperationalPolicyType", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let type = "type";
  let version = "version";
  describe("Non-Empty Json Check", () => {
    beforeEach(async () => {
      expected = { name: "loop1" };
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.addOperationalPolicyType(
        name,
        type,
        version
      );
    });

    it("Test addOperationalPolicyType returns correct json", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = new Error("error");
      global.fetch = jest.fn(() =>
        Promise.resolve({
          text: () => Promise.resolve(expected),
        })
      );
      actualResponse = await LoopService.addOperationalPolicyType(
        name,
        type,
        version
      );
    });
    it("Test addOperationalPolicyType returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
describe("Verify RemoveOperationalPolicyType", () => {
  let actualResponse;
  let expected;
  let name = "loop1";
  let type = "type";
  let version = "version";
  let policyName = "policy";
  describe("Non-Empty Json Check", () => {
    beforeEach(async () => {
      expected = { name: "loop1" };
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      actualResponse = await LoopService.removeOperationalPolicyType(
        name,
        type,
        version,
        policyName
      );
    });

    it("Test removeOperationalPolicyType returns correct json", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
  describe("Error during API call", () => {
    beforeEach(async () => {
      expected = {};
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({}),
        })
      );
      actualResponse = await LoopService.removeOperationalPolicyType(
        name,
        type,
        version,
        policyName
      );
    });
    it("Test removeOperationalPolicyType returns empty", () => {
      expect(actualResponse).toEqual(expected);
    });
  });
});
