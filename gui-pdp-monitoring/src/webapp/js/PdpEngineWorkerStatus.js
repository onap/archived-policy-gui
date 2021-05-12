/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import $ from "jquery";
import { createEngineTable } from "./MonitoringTable";
import { config } from "./MonitoringConfig";
import { createChart, updateChart } from "./MonitoringChart";
/*
 * Create an Engine Status Table and its charts
 */
function createEngineStatusTable(id, startStopStatus) {
    var tableId = config.engineStatus.tableId;
    var headers = config.engineStatus.headers;

    // Create a wrapper div for both the table and the charts
    var wrapper = document.createElement("div");
    wrapper.setAttribute("id", id + "_wrapper");
    wrapper.setAttribute("class", "wrapper");
    $("." + config.engineStatus.parent).append(wrapper);

    // Create the table
    var table = createEngineTable($(wrapper), id, headers.map(function(a) {
        return a.title;
    }));
    var tableRow = document.createElement("tr");
    var tableData = "";
    for (let h of headers) {
        tableData += "<td id=" + tableId + "_" + h.id + "></td>";
    }
    tableRow.innerHTML = tableData;
    // var actionTD = $(tableRow).find("#" + tableId + "_action");
    // var checked = (startStopStatus === "STOPPED") ? "checked" : "";
    var chartWrapper = document.createElement("div");
    chartWrapper.setAttribute("id", "chartWrapper");

    $(table).children("#engineTableBody").append(tableRow);

    var expand = document.createElement("i");
    expand.setAttribute("class", "ebIcon ebIcon_rowExpanded ebIcon_large ebIcon_interactive expandIcon");
    $(expand).click(function() {
        if ($(chartWrapper).is(":visible")) {
            expand.setAttribute("class", "ebIcon ebIcon_rowCollapsed ebIcon_large ebIcon_interactive expandIcon");
        } else {
            expand.setAttribute("class", "ebIcon ebIcon_rowExpanded ebIcon_large ebIcon_interactive expandIcon");
        }
        $(chartWrapper).slideToggle();
    }.bind(window));
    $(wrapper).append(expand);
    $(wrapper).append(chartWrapper);
    return table;
}

/*
 * Check for any changes in the Engine Status Table data and its charts and
 * update only where necessary
 */
function setEngineStatusData(engineStatusData, changed) {
    var tableId = config.engineStatus.tableId;
    var headers = config.engineStatus.headers.map(function(a) {
        return a.id;
    });
    for (let esd of engineStatusData) {
        var id = tableId + "_" + esd.id;
        var existingTable = undefined;
        for (let est of window.engineStatusTables) {
            if (id === est.getAttribute("id")) {
                existingTable = est;
            }
        }

        var data = [ esd.timestamp, id.split("_")[1], esd.status,
                esd.lastMessage, esd.upTime,
                esd.policyExecutions ];

        var table = existingTable;
        // If no table already exists for the engine, add one
        if (!table || changed) {
            table = createEngineStatusTable(id, esd.status);
            table.setAttribute("id", id);
            table.style["margin-bottom"] = "10px";
            table.style.display = "inline-block";
            window.engineStatusTables.push(table);
        }

        // Update data in table
        for (let h in headers) {
            var td = $(table).find("#" + tableId + "_" + headers[h]);
            if (td.html() !== data[h]) {
                $(table).find("#" + tableId + "_" + headers[h]).html(data[h]);
            }
        }

        // Update charts
        var wrapper = $(table).parent();
        var chartWrapper = $(wrapper).find("#chartWrapper")

        var chartConfig = config.engineChart.lastPolicyDurationChart;
        var lastPolicyDurationChart = wrapper.find("#" + chartConfig.parent)[0];
        if (lastPolicyDurationChart) {
            updateChart(lastPolicyDurationChart, JSON.parse(esd.lastPolicyDuration),
                    chartConfig.nodeColour);
        } else {
            chartConfig = config.engineChart.lastPolicyDurationChart;
            var lastPolicyDurationDiv = document.createElement("div");
            lastPolicyDurationDiv.setAttribute("id", chartConfig.parent);
            lastPolicyDurationDiv.setAttribute("class", "papChart");

            createChart(esd.lastPolicyDuration, lastPolicyDurationDiv,
                    chartConfig.title, chartConfig.unit, chartConfig.lineStroke, chartConfig.nodeColour);
            $(chartWrapper).append(lastPolicyDurationDiv);
        }

        chartConfig = config.engineChart.averagePolicyDurationChart;
        var averagePolicyDurationChart = wrapper.find("#" + chartConfig.parent)[0];
        if (averagePolicyDurationChart) {
            updateChart(averagePolicyDurationChart, JSON.parse(esd.averagePolicyDuration),
                    chartConfig.nodeColour);
        } else {
            chartConfig = config.engineChart.averagePolicyDurationChart;
            var averagePolicyDurationDiv = document.createElement("div");
            averagePolicyDurationDiv.setAttribute("id", chartConfig.parent);
            averagePolicyDurationDiv.setAttribute("class", "papChart");
            createChart(esd.averagePolicyDuration, averagePolicyDurationDiv,
                    chartConfig.title, chartConfig.unit, chartConfig.lineStroke, chartConfig.nodeColour);
            $(chartWrapper).append(averagePolicyDurationDiv);
        }

    }
}

export { setEngineStatusData, };
