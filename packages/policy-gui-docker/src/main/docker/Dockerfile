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

FROM onap/policy-jre-alpine:2.3.1

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/gui

ENV POLICY_LOGS=$POLICY_LOGS
ENV POLICY_HOME=$POLICY_HOME/gui

RUN mkdir -p $POLICY_HOME $POLICY_LOGS $POLICY_HOME/bin $POLICY_HOME/lib && \
    chown -R policy:policy $POLICY_HOME $POLICY_LOGS && \
    apk update && \
    apk add --no-cache nginx

COPY --chown=policy:policy etc/ /etc/
COPY --chown=policy:policy policy-gui.sh $POLICY_HOME/bin/
ADD --chown=policy:policy index.html /maven/gui-clamp-html.tar.gz /usr/share/nginx/html/
RUN ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log && \
    touch /var/run/nginx.pid && \
    chown -R policy:policy /etc/nginx /usr/share/nginx /var/lib/nginx /var/log/nginx /var/run/nginx.pid && \
    chmod 755 $POLICY_HOME/bin/*.sh

USER policy
WORKDIR $POLICY_HOME/bin
ENTRYPOINT [ "./policy-gui.sh" ]
EXPOSE 2443
