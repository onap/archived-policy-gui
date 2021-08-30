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

import $ from "jquery";
import { initTooltip } from "./MonitoringChart";
import { getEngineURL, clearEngineURL, setUpPage,
    removeChildrenElementsByClass, ajax_get_statistics
} from "./MonitoringUtils";
import { setEngineServiceData } from "./PdpInformation";
import { config } from "./MonitoringConfig";
import { setEngineSummaryData } from "./PdpStatisticsSummary";
import { highlightSelected } from "./PdpListView";
import { setEngineStatusData } from "./PdpEngineWorkerStatus";

function servicesCallback(data){
    var engineURL = localStorage.getItem("pap-monitor-services");
    if (engineURL) {
        engineURL = JSON.parse(engineURL);
        if (engineURL.hostname === data.server && engineURL.port === data.port) {
            setEngineServiceData(data.engineId, window.groupName, window.subGroupName, data.healthStatus, data.pdpState, data.server, data.port);
            setEngineSummaryData(data.status, data.timeStamp, data.policyDeployCount, data.policyDeploySuccessCount, data.policyDeployFailCount, data.policyExecutedCount, data.policyExecutedSuccessCount, data.policyExecutedFailCount);

            if (window.engine_id === data.engineId){
                setEngineStatusData(data.status, false);
            }else{
                removeChildrenElementsByClass(config.engineStatus.parent);
                setEngineStatusData(data.status, true);
            }

            window.engine_id = data.engineId;

            // Make content visible after data has been returned for the first time
            if (!$(".content").is(':visible')) {
                $(".content").fadeIn();
            }

            // Repeat the same request
            setTimeout(function() {
                window.servicesCall = ajax_get_statistics(window.restRootURL + "statistics/", servicesCallback,
                    window.services.useHttps, window.services.hostname, window.services.port,
                    window.services.username, window.services.password, window.id);
            }, config.refresh);
        }
    }
}

/*
 * Called after the DOM is ready
 */
function readyCallback() {
    window.restRootURL = location.protocol
        + "//"
        + window.location.hostname
        + ':' + window.location.port
        + (location.pathname.endsWith("/monitoring/") ? location.pathname.substring(0, location.pathname.indexOf("monitoring/")) : location.pathname)
        + "papservices/monitoring/";
    // Initialize tooltip for the charts
    initTooltip();

    // Set up the structure of the page
    setUpPage(true);

    // Check cookies for engine URL
    getEngineURL();

    // Add click event to config icon for clearing engine URL
    $(".ebSystemBar-config").click(
        function() {
            // Clear the engine URL
            clearEngineURL(true);

            // Request the engine URL
            getEngineURL();
        }
    );

    ['hashchange', 'load'].forEach(event => window.addEventListener(event, function () {
        // Get ID from url
        window.id = window.location.hash.replace('#', '');
        if (window.id !== '') {
            var arr = window.id.split("/");
            window.groupName = arr[0];
            window.subGroupName = arr[1];
            highlightSelected(window.id);
            ajax_get_statistics(restRootURL + "statistics/", servicesCallback,
                window.services.useHttps, window.services.hostname, window.services.port,
                window.services.username, window.services.password, window.id);
        }
    }));
}

$(document).ready(readyCallback);
// Export for unit testing
export { readyCallback, servicesCallback };