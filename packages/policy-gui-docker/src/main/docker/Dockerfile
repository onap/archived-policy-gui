#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------

FROM onap/policy-jre-alpine:2.3.0

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/gui

ENV POLICY_LOGS=$POLICY_LOGS
ENV POLICY_HOME=$POLICY_HOME/gui
ENV CLAMP_REST_URL=http://0.0.0.0

RUN mkdir -p $POLICY_HOME $POLICY_LOGS $POLICY_HOME/bin $POLICY_HOME/lib && \
    chown -R policy:policy $POLICY_HOME $POLICY_LOGS && \
    apk update && \
    apk add --no-cache gettext nginx

WORKDIR $POLICY_HOME
COPY policy-gui.sh ./bin/
COPY /maven/gui-editor-apex-uber.jar ./lib/
COPY /maven/gui-pdp-monitoring-uber.jar ./lib/
COPY etc/ssl/clamp.key /etc/ssl/clamp.key
COPY etc/ssl/clamp.pem /etc/ssl/clamp.pem
COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY nginx/index.html /usr/share/nginx/html/
ADD /maven/gui-clamp.tar.gz /usr/share/nginx/html/
RUN rm /etc/nginx/conf.d/default.conf && \
    ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log && \
    touch /var/run/nginx.pid && \
    chown -R policy:policy . /etc/nginx /usr/share/nginx /var/lib/nginx /var/log/nginx /var/run/nginx.pid && \
    chmod 755 bin/*.sh

USER policy
WORKDIR $POLICY_HOME/bin
ENTRYPOINT [ "./policy-gui.sh" ]
EXPOSE 2443
