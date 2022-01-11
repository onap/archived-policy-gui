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
import TemplateService from "./TemplateService";

describe("Verify GetLoopNames", () => {
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
      response = await TemplateService.getLoopNames();
    });
    it("Test getLoopNames returns correct json", () => {
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
      response = await TemplateService.getLoopNames();
    });
    it("Test getLoopNames returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify GetAllLoopTemplates", () => {
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
      response = await TemplateService.getAllLoopTemplates();
    });
    it("Test getAllLoopTemplates returns correct json", () => {
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
      response = await TemplateService.getAllLoopTemplates();
    });
    it("Test getAllLoopTemplates returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify GetDictionary", () => {
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
      response = await TemplateService.getDictionary();
    });
    it("Test getDictionary returns correct json", () => {
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
      response = await TemplateService.getDictionary();
    });
    it("Test getDictionary returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify GetDictionaryElements", () => {
  let response;
  let expectedResponse;
  let dictionaryName = "name";
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
      response = await TemplateService.getDictionaryElements(dictionaryName);
    });
    it("Test getDictionaryElements returns correct json", () => {
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
      response = await TemplateService.getDictionaryElements(dictionaryName);
    });
    it("Test getDictionaryElements returns empty response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify InsDictionary", () => {
  let response;
  let expectedResponse;
  let json = "{}";
  describe("Return Success Response Check", () => {
    expectedResponse = "Success";
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          json: () => Promise.resolve({ name: "loop1" }),
          status: "Success",
          ok: true,
        })
      );
      response = await TemplateService.insDictionary(json);
    });
    it("Test insDictionary returns correct json", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
  describe("Return Failed Response Check", () => {
    beforeEach(async () => {
      expectedResponse = "Failed";
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await TemplateService.insDictionary(json);
    });
    it("Test insDictionary returns empty response", () => {
      expect(response).toEqual(expectedResponse);
    });
  });
});
describe("Verify InsDictionaryElements", () => {
  let response;
  let json = "{}";
  describe("Return Success Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Success",
          ok: true,
        })
      );
      response = await TemplateService.insDictionaryElements(json);
    });
    it("Test insDictionaryElements returns success response", () => {
      expect(response).toEqual("Success");
    });
  });
  describe("Return Failed Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await TemplateService.insDictionaryElements(json);
    });
    it("Test insDictionaryElements returns failed response", () => {
      expect(response).toEqual("Failed");
    });
  });
});
describe("Verify DeleteDictionary", () => {
  let response;
  let dictionaryName = "name";
  describe("Return Success Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Success",
          ok: true,
        })
      );
      response = await TemplateService.deleteDictionary(dictionaryName);
    });
    it("Test deleteDictionary returns success response", () => {
      expect(response).toEqual("Success");
    });
  });
  describe("Return Failed Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await TemplateService.deleteDictionary(dictionaryName);
    });
    it("Test deleteDictionary returns failed response", () => {
      expect(response).toEqual({});
    });
  });
});
describe("Verify DeleteDictionaryElements", () => {
  let response;
  let dictionaryData = "name";
  describe("Return Success Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Success",
          ok: true,
        })
      );
      response = await TemplateService.deleteDictionaryElements(dictionaryData);
    });
    it("Test deleteDictionaryElements returns success response", () => {
      expect(response).toEqual("Success");
    });
  });
  describe("Return Failed Response Check", () => {
    beforeEach(async () => {
      global.fetch = jest.fn(() =>
        Promise.resolve({
          status: "Failed",
          ok: false,
        })
      );
      response = await TemplateService.deleteDictionaryElements(dictionaryData);
    });
    it("Test deleteDictionaryElements returns failed response", () => {
      expect(response).toEqual({});
    });
  });
});
