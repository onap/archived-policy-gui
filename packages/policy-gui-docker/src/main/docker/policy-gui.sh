#!/usr/bin/env sh
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2021 Nordix Foundation.
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

trap 'exit 0' SIGTERM

JAVA_HOME=/usr/lib/jvm/java-11-openjdk/

echo "Starting gui-editor-apex"
$JAVA_HOME/bin/java -jar "$POLICY_HOME/lib/gui-editor-apex-uber.jar" -p 18989 &

echo "Starting gui-pdp-monitoring"
$JAVA_HOME/bin/java -jar "$POLICY_HOME/lib/gui-pdp-monitoring-uber.jar" -p 18999 &

echo "Starting nginx"
envsubst '${CLAMP_REST_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf
nginx -g "daemon on;"

wait
