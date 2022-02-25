#!/usr/bin/env sh
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2021-2022 Nordix Foundation.
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

JAVA_HOME=/usr/lib/jvm/java-11-openjdk/
export KEYSTORE="${KEYSTORE:-$POLICY_HOME/etc/ssl/policy-keystore}"
export TRUSTSTORE="${TRUSTSTORE:-$POLICY_HOME/etc/ssl/policy-truststore}"
export KEYSTORE_PASSWD="${KEYSTORE_PASSWD:-Pol1cy_0nap}"
export TRUSTSTORE_PASSWD="${TRUSTSTORE_PASSWD:-Pol1cy_0nap}"

if [ -f "${POLICY_HOME}/etc/mounted/policy-truststore" ]; then
    echo "overriding policy-truststore"
    cp -f "${POLICY_HOME}"/etc/mounted/policy-truststore "${TRUSTSTORE}"
fi

if [ -f "${POLICY_HOME}/etc/mounted/policy-keystore" ]; then
    echo "overriding policy-keystore"
    cp -f "${POLICY_HOME}"/etc/mounted/policy-keystore "${KEYSTORE}"
fi

if [ -f "${POLICY_HOME}/etc/mounted/logback.xml" ]; then
    echo "overriding logback xml files"
    cp -f "${POLICY_HOME}"/etc/mounted/logback*.xml "${POLICY_HOME}"/etc/
fi

$JAVA_HOME/bin/java \
  -Dlogging.config="${POLICY_HOME}/etc/logback.xml" \
  -jar /app/app.jar \
  --spring.config.location="${POLICY_HOME}/etc/application.yml"
