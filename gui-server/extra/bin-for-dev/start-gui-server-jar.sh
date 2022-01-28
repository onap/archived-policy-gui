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
# This script changes directory so that application.yml, keystore,
# and truststore will be in current directory before running jar.
SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
pushd "$SCRIPTDIR" || exit
java -Dspring.profiles.active=dev -jar ../../target/gui-server-*.jar
popd || exit
