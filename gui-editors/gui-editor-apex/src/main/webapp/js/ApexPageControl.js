/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import {
    contextSchemaTab_activate,
    contextSchemaTab_deactivate
} from "./ApexContextSchemaTab";
import {eventTab_activate, eventTab_deactivate} from "./ApexEventTab";
import {
    contextAlbumTab_activate,
    contextAlbumTab_deactivate
} from "./ApexContextAlbumTab";
import {taskTab_activate, taskTab_deactivate} from "./ApexTaskTab";
import {policyTab_activate, policyTab_deactivate} from "./ApexPolicyTab";
import {
    keyInformationTab_activate,
    keyInformationTab_deactivate
} from "./ApexKeyInformationTab";
import {clearLocalStorage} from "./ApexMain";

function pageControl_modelMode(name, version, fileName) {
    $('#modelName').html(name);
    $('#modelVersion').html(version);
    $('#modelFileName').html(fileName ? fileName : "N/A");

    $("#menuFileNew").addClass("disabled");
    $("#menuFileOpen").addClass("disabled");
    $("#menuFileDownload").removeClass("disabled");
    $("#menuFileUpload").removeClass("disabled");
    $("#menuFileClear").removeClass("disabled");
    $("#menuModelAnalyse").removeClass("disabled");
    $("#menuModelValidate").removeClass("disabled");
    $("#menuModelCompare").removeClass("disabled");
    $("#menuModelSplit").removeClass("disabled");
    $("#menuModelMerge").removeClass("disabled");
    $("#menuConceptsContextSchemas").removeClass("disabled");
    $("#menuConceptsEvents").removeClass("disabled");
    $("#menuConceptsContextAlbums").removeClass("disabled");
    $("#menuConceptsTasks").removeClass("disabled");
    $("#menuConceptsPolicies").removeClass("disabled");
    $("#menuConceptsKeyInformation").removeClass("disabled");

    $(".modelTag").show("slide", {
        direction : "left"
    }, 200);

    try{
        $("#mainTabs").tabs({
            classes : {
                "ui-tabs-tab" : "ui-tabs-tab-custom",
                "ui-tabs-active" : "ui-tabs-active-custom",
                "ui-tabs-anchor" : "ui-tabs-anchor-custom"
            },
            disabled : false,
            activate : function(event, ui) {
                localStorage.setItem("apex_tab_index", ui.newTab.index());
            }
        });
    }
    catch(err){
        console.error('TypeError! Failed to get tabs function');
    }
    

    contextSchemaTab_activate();
    eventTab_activate();
    contextAlbumTab_activate();
    taskTab_activate();
    policyTab_activate();
    keyInformationTab_activate();
    showPlaceholder(false);
}

function pageControl_noModelMode() {
    $(".modelTag").hide("slide", {
        direction : "left"
    }, 200);

    $('#modelName').html("N/A");
    $('#modelVersion').html("N/A");
    $('#modelFileName').html("N/A");

    $("#menuFileNew").removeClass("disabled");
    $("#menuFileOpen").removeClass("disabled");
    $("#menuFileDownload").addClass("disabled");
    $("#menuFileUpload").addClass("disabled");
    $("#menuFileClear").addClass("disabled");
    $("#menuModelAnalyse").addClass("disabled");
    $("#menuModelValidate").addClass("disabled");
    $("#menuModelCompare").addClass("disabled");
    $("#menuModelSplit").addClass("disabled");
    $("#menuModelMerge").addClass("disabled");
    $("#menuConceptsContextSchemas").addClass("disabled");
    $("#menuConceptsEvents").addClass("disabled");
    $("#menuConceptsContextAlbums").addClass("disabled");
    $("#menuConceptsTasks").addClass("disabled");
    $("#menuConceptsPolicies").addClass("disabled");
    $("#menuConceptsKeyInformation").addClass("disabled");

    contextSchemaTab_deactivate();
    eventTab_deactivate();
    contextAlbumTab_deactivate();
    taskTab_deactivate();
    policyTab_deactivate();
    keyInformationTab_deactivate();
    try{
        $("#mainTabs").tabs({
            classes : {
                "ui-tabs-tab" : "ui-tabs-tab-custom",
                "ui-tabs-active" : "ui-tabs-active-custom",
                "ui-tabs-anchor" : "ui-tabs-anchor-custom"
            },
            disabled : [ 0, 1, 2, 3, 4, 5 ]
        });
    }
    catch(err){
        console.error('TypeError! Failed to get tabs function');
    }
    showPlaceholder(true);
}

function pageControl_busyMode() {
    $("#menuFile").addClass("disabled");
    $("#menuFileNew").addClass("disabled");
    $("#menuFileOpen").addClass("disabled");
    $("#menuFileDownload").addClass("disabled");
    $("#menuFileClear").addClass("disabled");
    $("#menuModelAnalyse").addClass("disabled");
    $("#menuModelValidate").addClass("disabled");
    $("#menuModelCompare").addClass("disabled");
    $("#menuModelSplit").addClass("disabled");
    $("#menuModelMerge").addClass("disabled");
    $("#menuConceptsContextSchemas").addClass("disabled");
    $("#menuConceptsEvents").addClass("disabled");
    $("#menuConceptsContextAlbums").addClass("disabled");
    $("#menuConceptsTasks").addClass("disabled");
    $("#menuConceptsPolicies").addClass("disabled");
    $("#menuConceptsKeyInformation").addClass("disabled");
    try{
        $("#mainTabs").tabs({
            classes : {
                "ui-tabs-tab" : "ui-tabs-tab-custom",
                "ui-tabs-active" : "ui-tabs-active-custom",
                "ui-tabs-anchor" : "ui-tabs-anchor-custom"
            },
            disabled : false
        });
    }
    catch(err){
        console.error('TypeError! Failed to get tabs function');
    }
    contextSchemaTab_activate();
    eventTab_activate();
    contextAlbumTab_activate();
    taskTab_activate();
    policyTab_activate();
    keyInformationTab_activate();
    showPlaceholder(false);
}

function pageControl_readyMode() {
    $("#menuFile").removeClass("disabled");
    $("#menuFileNew").removeClass("disabled");
    $("#menuFileOpen").removeClass("disabled");
    $("#menuFileDownload").removeClass("disabled");
    $("#menuFileClear").removeClass("disabled");
    $("#menuModelAnalyse").removeClass("disabled");
    $("#menuModelValidate").removeClass("disabled");
    $("#menuModelCompare").removeClass("disabled");
    $("#menuModelSplit").removeClass("disabled");
    $("#menuModelMerge").removeClass("disabled");
    $("#menuConceptsContextSchemas").removeClass("disabled");
    $("#menuConceptsEvents").removeClass("disabled");
    $("#menuConceptsContextAlbums").removeClass("disabled");
    $("#menuConceptsTasks").removeClass("disabled");
    $("#menuConceptsPolicies").removeClass("disabled");
    $("#menuConceptsKeyInformation").removeClass("disabled");

    contextSchemaTab_deactivate();
    eventTab_deactivate();
    contextAlbumTab_deactivate();
    taskTab_deactivate();
    policyTab_deactivate();
    keyInformationTab_deactivate();
    try{
        $("#mainTabs").tabs({
            classes : {
                "ui-tabs-tab" : "ui-tabs-tab-custom",
                "ui-tabs-active" : "ui-tabs-active-custom",
                "ui-tabs-anchor" : "ui-tabs-anchor-custom"
            },
            disabled : [ 0, 1, 2, 3, 4, 5 ]
        });
    }
    catch(err){
        console.error('TypeError! Failed to get tabs function');
    }
    showPlaceholder(true);
}

function pageControl_successStatus(data) {
    $('#statusString').html(data.result);
    $("#statusMessageTable").empty();

    if (data.ok) {
        $('#statusString').css("color", "green");
        $('#ebInlineMessage-iconHolder-icon').attr("class", "ebIcon ebIcon_big ebIcon_tick");
    } else {
        $('#statusString').css("color", "red");
        $('#ebInlineMessage-iconHolder-icon').attr("class", "ebIcon ebIcon_big ebIcon_error");
        for (let value of data.messages) {
            $("#statusMessageTable").append("<tr><td>" + value + "</td></tr>");
        }
        // A session with session ID "0" does not exist
        var sessionDoesNotExistStringStart = "A session with session ID ";
        var sessionDoesNotExistStringEnd = " does not exist";
        if (data.messages[0].indexOf(sessionDoesNotExistStringStart) !== -1
                && data.messages[0].indexOf(sessionDoesNotExistStringEnd) !== -1) {
            clearLocalStorage();
            location.reload();
        }
    }
}

function pageControl_status(data) {
    $('#statusString').html(data.result);
    $("#statusMessageTable").empty();

    if (data.ok) {
        $('#statusString').css("color", "green");
        $('#ebInlineMessage-iconHolder-icon').attr("class", "ebIcon ebIcon_big ebIcon_tick");
    } else {
        $('#statusString').css("color", "red");
        $('#ebInlineMessage-iconHolder-icon').attr("class", "ebIcon ebIcon_big ebIcon_error");
    }
}

function pageControl_restError(requestURL, jqXHR, textStatus, errorThrown) {
    $('#statusString').html("REST_ERROR");
    $('#statusString').css("color", "red");
    $('#ebInlineMessage-iconHolder-icon').attr("class", "ebIcon ebIcon_big ebIcon_error");

    $("#statusMessageTable").empty();
    $("#statusMessageTable").append(
            "<tr><td>request \"" + requestURL + "\" returned  \"" + textStatus + "\" " + jqXHR.status + " \""
                    + errorThrown + "\"</td></tr>");

}

function pageControl_recursiveDisable(el, disableValue, visibleValue) {
    try {
        el.readOnly = disableValue;
        el.style.visibility = (visibleValue ? "visible" : "hidden");
    } catch (E) {
    }

    if (el.childNodes && el.childNodes.length > 0) {
        for (let value of el.childNodes) {
            recursiveDisable(value, disableValue, visibleValue);
        }
    }
}

function showPlaceholder(show) {
    if (show) {
        $(".placeholder").show();
    } else {
        $(".placeholder").hide();
    }
}

export {
    showPlaceholder,
    pageControl_busyMode,
    pageControl_modelMode,
    pageControl_noModelMode,
    pageControl_readyMode,
    pageControl_recursiveDisable,
    pageControl_restError,
    pageControl_status,
    pageControl_successStatus
}
