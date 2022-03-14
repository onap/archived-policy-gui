#!/bin/bash
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2022 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#
SCRIPT_DIR=$(dirname "${BASH_SOURCE[0]}")
KEYSTORE_PATH=$(realpath "$SCRIPT_DIR/demo-clamp-keystore.p12")
TRUSTSTORE_PATH=$(realpath "$SCRIPT_DIR/demo-clamp-truststore.jks")
LOGBACK_PATH=$(realpath "$SCRIPT_DIR/config/dev/logback.xml")

# Note hostname 'policy-clamp-be' is mapped to host-gateway (i.e. host's localhost)
docker run \
  --publish 2443:2443 \
  --add-host policy-clamp-be:host-gateway \
  --env "CLAMP_URL=https://policy-clamp-be:8443" \
  --env "CLAMP_DISABLE_SSL_VALIDATION=true" \
  --env "KEYSTORE_PASSWD=changeit" \
  --env "TRUSTSTORE_PASSWD=changeit" \
  --volume "$KEYSTORE_PATH:/opt/app/policy/gui/etc/mounted/policy-keystore" \
  --volume "$TRUSTSTORE_PATH:/opt/app/policy/gui/etc/mounted/policy-truststore" \
  --volume "$LOGBACK_PATH:/opt/app/policy/gui/etc/mounted/logback.xml" \
  --rm \
  onap/policy-gui:latest
