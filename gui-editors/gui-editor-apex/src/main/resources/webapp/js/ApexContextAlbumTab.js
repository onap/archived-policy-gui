/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
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

import {rightClickMenu_scopePreserver} from './contextMenu';
import {createTable} from './ApexTable';
import {ajax_get} from './ApexAjax';
import {apexUtils_removeElement} from "./ApexUtils";

function contextAlbumTab_reset() {
    contextAlbumTab_deactivate();
    contextAlbumTab_activate();
}

function contextAlbumTab_activate() {
    contextAlbumTab_create();

    var requestURL = window.restRootURL + "/ContextAlbum/Get?name=&version=";

    ajax_get(requestURL, function(data) {
        $("#contextAlbumTableBody").find("tr:gt(0)").remove();

        for (var msg in data2.messages.message) {
            var contextSchema = JSON.parse(msg).apexContextSchema;

            var contextAlbumRow_tr = document.createElement("tr");

            var contextAlbumTableRow =
                "<td>"                                              +
                contextAlbum.key.name + ":"  + contextAlbum.key.version +
                "</td>"                                             +
                "<td>"                                              +
                contextAlbum.scope                                  +
                "</td>"                                             +
                "<td>"                                              +
                contextAlbum.isWritable                             +
                "</td>"                                             +
                "<td>"                                              +
                contextAlbum.itemSchema.name + ":"  + contextAlbum.itemSchema.version +
                "</td>"   ;

            contextAlbumRow_tr.innerHTML = contextAlbumTableRow;
            contextAlbumRow_tr.addEventListener('contextmenu', rightClickMenu_scopePreserver("contextAlbumTabContent", "contextAlbum", contextAlbum.key.name, contextAlbum.key.version));

            $("#contextAlbumTableBody").append(contextAlbumRow_tr);

        }
    });
}

function contextAlbumTab_deactivate() {
    apexUtils_removeElement("contextAlbumTabContent");
}

function contextAlbumTab_create() {
    var contextAlbumTab = document.getElementById("contextAlbumsTab");

    //Testing purposes
    if(contextAlbumTab === null){
        contextAlbumTab = document.createElement('contextAlbumsTab');
    }

    var contextAlbumTabContent = document.getElementById("contextAlbumTabContent");
    if (contextAlbumTabContent != null) {
        return
    }

    contextAlbumTabContent = document.createElement("contextAlbumTabContent");
    contextAlbumTab.appendChild(contextAlbumTabContent);
    contextAlbumTabContent.setAttribute("id", "contextAlbumTabContent");
    contextAlbumTabContent.addEventListener('contextmenu', rightClickMenu_scopePreserver("contextAlbumTabContent", "contextAlbum",null, null));

    var contextAlbumTable = createTable("contextAlbumTable");
    contextAlbumTabContent.appendChild(contextAlbumTable);

    var contextAlbumTableHeader = document.createElement("thead");
    contextAlbumTable.appendChild(contextAlbumTableHeader);
    contextAlbumTableHeader.setAttribute("id", "contextAlbumTableHeader");

    var contextAlbumTableHeaderRow = document.createElement("tr");
    contextAlbumTableHeader.appendChild(contextAlbumTableHeaderRow);
    contextAlbumTableHeaderRow.setAttribute("id", "contextAlbumTableHeaderRow");

    var contextAlbumTableKeyHeader = document.createElement("th");
    contextAlbumTableHeaderRow.appendChild(contextAlbumTableKeyHeader);
    contextAlbumTableKeyHeader.setAttribute("id", "contextAlbumTableKeyHeader");
    contextAlbumTableKeyHeader.appendChild(document.createTextNode("Context Album"));

    var contextAlbumTableScopeHeader = document.createElement("th");
    contextAlbumTableHeaderRow.appendChild(contextAlbumTableScopeHeader);
    contextAlbumTableScopeHeader.setAttribute("id", "contextAlbumTableScopeHeader");
    contextAlbumTableScopeHeader.appendChild(document.createTextNode("Scope"));

    var contextAlbumTableWriteableHeader = document.createElement("th");
    contextAlbumTableHeaderRow.appendChild(contextAlbumTableWriteableHeader);
    contextAlbumTableWriteableHeader.setAttribute("id", "contextAlbumTableWritableHeader");
    contextAlbumTableWriteableHeader.appendChild(document.createTextNode("Writable"));

    var contextAlbumTableItemSchemaHeader = document.createElement("th");
    contextAlbumTableHeaderRow.appendChild(contextAlbumTableItemSchemaHeader);
    contextAlbumTableItemSchemaHeader.setAttribute("id", "contextAlbumTableItemSchemaHeader");
    contextAlbumTableItemSchemaHeader.appendChild(document.createTextNode("Item Schema"));

    var contextAlbumTableBody = document.createElement("tbody");
    contextAlbumTable.appendChild(contextAlbumTableBody);
    contextAlbumTable.setAttribute("id", "contextAlbumTableBody");
}

//Testing purposes
export { contextAlbumTab_reset, contextAlbumTab_create, contextAlbumTab_activate, contextAlbumTab_deactivate };
