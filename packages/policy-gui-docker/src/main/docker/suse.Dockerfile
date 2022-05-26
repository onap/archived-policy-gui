#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------

FROM opensuse/leap:15.3

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/gui

ENV POLICY_LOGS=$POLICY_LOGS
ENV POLICY_HOME=/opt/app/policy/gui
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
ENV JAVA_HOME=/usr/lib64/jvm/java-11-openjdk-11

RUN zypper -n -q install --no-recommends gzip java-11-openjdk-headless netcat-openbsd tar && \
    zypper -n -q update; zypper -n -q clean --all && \
    groupadd --system policy && \
    useradd --system --shell /bin/sh -G policy policy && \
    mkdir -p /app $POLICY_LOGS $POLICY_HOME $POLICY_HOME/bin $POLICY_HOME/etc/ssl && \
    chown -R policy:policy /app $POLICY_HOME $POLICY_LOGS

COPY --chown=policy:policy /maven/app.jar /app/
COPY --chown=policy:policy policy-gui.sh $POLICY_HOME/bin/
COPY --chown=policy:policy config/application.yml $POLICY_HOME/etc/
COPY --chown=policy:policy config/logback.xml $POLICY_HOME/etc/
RUN chmod 755 $POLICY_HOME/bin/*.sh

USER policy
WORKDIR $POLICY_HOME/bin
ENTRYPOINT [ "./policy-gui.sh" ]
EXPOSE 2443
