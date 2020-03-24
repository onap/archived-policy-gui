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

import $ from 'jquery';
import * as sut from "../MonitoringUtils";
import * as PdpInformation from "../PdpInformation";
import * as PdpStatisticsSummary from "../PdpStatisticsSummary";
import * as PdpListView from '../PdpListView';

const requestURL = "http://localhost:7979";

const serviceData = {
    useHttps: "http",
    hostname: "localhost",
    port: 7979,
    username: "username",
    password: "password",
};

test('ajax_get ok', () => {
    const callback = jest.fn();
    $.ajax = jest.fn().mockImplementation((args) => { args.success(); });
    sut.ajax_get(requestURL, callback, serviceData.useHttps, serviceData.hostname, serviceData.port,
        serviceData.username, serviceData.password, {}, null);
    expect(callback).toHaveBeenCalled();
});

test('ajax_get error', () => {
    const errorCallback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation(
        args => {
            args.error(jqXHR, null, null);
        }
    );
    sut.ajax_get(requestURL, null, serviceData.useHttps, serviceData.hostname, serviceData.port,
        serviceData.username, serviceData.password, {}, errorCallback);
    expect(errorCallback).toHaveBeenCalled();
});

test('ajax_get_statistics ok', () => {
    const callback = jest.fn();
    $.ajax = jest.fn().mockImplementation((args) => { args.success(); });
    sut.ajax_get_statistics(requestURL, callback, serviceData.useHttps, serviceData.hostname, serviceData.port,
        serviceData.username, serviceData.password, "", {}, null);
    expect(callback).toHaveBeenCalled();
});

test('ajax_get_statistics error', () => {
    const errorCallback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation(
        args => {
            args.error(jqXHR, null, null);
        }
    );
    PdpInformation.createEngineServiceTable = jest.fn();
    PdpStatisticsSummary.createEngineSummaryTable = jest.fn();
    sut.ajax_get_statistics(requestURL, null, serviceData.useHttps, serviceData.hostname, serviceData.port,
        serviceData.username, serviceData.password, "", {}, errorCallback);

    expect(errorCallback).toHaveBeenCalled();
    expect(PdpInformation.createEngineServiceTable).toHaveBeenCalled();
    expect(PdpStatisticsSummary.createEngineSummaryTable).toHaveBeenCalled();
});

test('getEngineURL popup dialog', () => {
    window.localStorage.clear();
    sut.getEngineURL("message");
    $('#submit').click();
    expect($("papDialogDiv")).toHaveLength(1);
});

test('getEngineURL read from localStorage', () => {
    window.localStorage.setItem("pap-monitor-services", JSON.stringify(serviceData));
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

    $.ajax = jest.fn().mockImplementation((args) => { args.success(data); });
    PdpListView.RenderPdpList = jest.fn();
    sut.getEngineURL("message");
    expect(PdpListView.RenderPdpList.mock.calls[0][0]).toHaveLength(1);
});
