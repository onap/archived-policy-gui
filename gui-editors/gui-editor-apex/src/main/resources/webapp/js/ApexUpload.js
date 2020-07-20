/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
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

$(document).ready(function () {
  $.subscribe("/config/ready", enableUpload);

  function enableUpload() {
    const menuFileUpload = $('#menuFileUpload');
    const isUploadEnabled = configObj.getConfig("plugin.policy.upload.enable");
    if (isUploadEnabled === "true" || isUploadEnabled === true) {
      menuFileUpload.show();
    } else {
      menuFileUpload.hide();
    }
  }

});

const uploadPlugin = {
  dialogDiv: $('#main-dialog'),
  openDialog: function () {
    this.dialogDiv.load('../upload/dialog.html');
  },

  upload: function (data, successCallback, errorCallback) {
    const requestURL = restRootURL + "/Model/Upload";
    $.ajax({
      type: 'POST',
      url: requestURL,
      data: data,
      contentType: false,
      processData: false
    }).done(function (data) {
      pageControl_successStatus(data);
      if (typeof successCallback === typeof Function) {
        successCallback(data);
      }
    }).fail(function (jqXHR, textStatus, errorThrown) {
      pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
      if (typeof errorCallback === typeof Function) {
        errorCallback(jqXHR, textStatus, errorThrown);
      }
    });
  }
}
