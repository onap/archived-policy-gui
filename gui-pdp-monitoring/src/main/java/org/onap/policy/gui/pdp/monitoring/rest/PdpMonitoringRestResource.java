/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

package org.onap.policy.gui.pdp.monitoring.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.models.pdp.concepts.Pdp;
import org.onap.policy.models.pdp.concepts.PdpEngineWorkerStatistics;
import org.onap.policy.models.pdp.concepts.PdpGroups;
import org.onap.policy.models.pdp.concepts.PdpStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class represents the root resource exposed at the base URL<br>
 * The url to access this resource would be in the form {@code <baseURL>/rest/....} <br>
 * For example: a GET request to the following URL
 * {@code http://localhost:18989/papservices/rest/?hostName=localhost&port=12345}
 *
 * <b>Note:</b> An allocated {@code hostName} and {@code port} query parameter must be included in
 * all requests. Datasets for different {@code hostName} are completely isolated from one another.
 *
 * @author Yehui Wang (yehui.wang@est.tech)
 */
@Path("monitoring/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PdpMonitoringRestResource {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMonitoringRestResource.class);
    // Set up a map separated by host and engine for the data
    private static final Map<String, HashMap<String, List<Counter>>> cache = new HashMap<>();

    // Set the maximum number of stored data entries to be stored for each engine
    private static final int MAX_CACHED_ENTITIES = 50;

    private static Gson gson = new Gson();

    /**
     * Query Pdps.
     *
     * @param useHttps use Http or not
     * @param hostname hostname the host name of the engine service to connect to.
     * @param port port the port number of the engine service to connect to.
     * @param username user name
     * @param password password
     * @return a Response object containing Pdps in JSON
     * @throws HttpClientConfigException exception
     */
    @GET
    public Response getPdps(@QueryParam("useHttps") final String useHttps,
            @QueryParam("hostname") final String hostname, @QueryParam("port") final int port,
            @QueryParam("username") final String username, @QueryParam("password") final String password)
            throws HttpClientConfigException {

        return Response
                .ok(getHttpClient(useHttps, hostname, port, username, password, "policy/pap/v1/pdps").get().getEntity(),
                        MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Query Pdp statistics.
     *
     * @param useHttps use Http or not
     * @param hostname the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @param username user name
     * @param password password
     * @param id PdpGroupName/PdpSubGroup/PdpIntanceID
     * @return a Response object containing the Pdp status and context data in JSON
     * @throws HttpClientConfigException exception
     * @throws CoderException Coder exception
     */
    @GET
    @Path("statistics/")
    public Response getStatistics(@QueryParam("useHttps") final String useHttps,
            @QueryParam("hostname") final String hostname, @QueryParam("port") final int port,
            @QueryParam("username") final String username, @QueryParam("password") final String password,
            @QueryParam("id") final String id) throws HttpClientConfigException, CoderException {

        var pdpGroups = getHttpClient(useHttps, hostname, port, username, password, "policy/pap/v1/pdps").get()
                .readEntity(PdpGroups.class);
        String groupName;
        String subGroup;
        String instanceId;
        String[] idArray = id.split("/");
        if (idArray.length == 3) {
            groupName = idArray[0];
            subGroup = idArray[1];
            instanceId = idArray[2];
        } else {
            throw new IllegalArgumentException("Cannot parse groupName, subGroup and instanceId from " + id);
        }

        Pdp pdp = pdpGroups.getGroups().stream().filter(group -> group.getName().equals(groupName))
                .flatMap(group -> group.getPdpSubgroups().stream().filter(sub -> sub.getPdpType().equals(subGroup)))
                .flatMap(sub -> sub.getPdpInstances().stream()
                        .filter(instance -> instance.getInstanceId().equals(instanceId)))
                .filter(Objects::nonNull).findFirst().orElseThrow();

        final var responseObject = new StatisticsResponse();

        // Engine Service data
        responseObject.setEngineId(pdp.getInstanceId());
        responseObject.setServer(hostname);
        responseObject.setPort(Integer.toString(port));
        responseObject.setHealthStatus(pdp.getHealthy().name());
        responseObject.setPdpState(pdp.getPdpState().name());

        String statisticsEntity = getHttpClient(useHttps, hostname, port, username, password,
                "policy/pap/v1/pdps/statistics/" + id + "?recordCount=1").get().readEntity(String.class);
        Map<String, Map<String, List<PdpStatistics>>> pdpStats = gson.fromJson(statisticsEntity,
                new TypeToken<Map<String, Map<String, List<PdpStatistics>>>>() {}.getType());

        final List<EngineStatus> engineStatusList = new ArrayList<>();

        if (!pdpStats.isEmpty()) {
            PdpStatistics pdpStatistics = pdpStats.get(groupName).get(subGroup).get(0);
            responseObject.setTimeStamp(pdpStatistics.getTimeStamp().toString());
            responseObject.setPolicyDeployCount(pdpStatistics.getPolicyDeployCount());
            responseObject.setPolicyDeploySuccessCount(pdpStatistics.getPolicyDeploySuccessCount());
            responseObject.setPolicyDeployFailCount(pdpStatistics.getPolicyDeployFailCount());
            responseObject.setPolicyExecutedCount(pdpStatistics.getPolicyExecutedCount());
            responseObject.setPolicyExecutedSuccessCount(pdpStatistics.getPolicyExecutedSuccessCount());
            responseObject.setPolicyExecutedFailCount(pdpStatistics.getPolicyExecutedFailCount());

            // Engine Status data
            for (final PdpEngineWorkerStatistics engineStats : pdpStatistics.getEngineStats()) {
                try {
                    final EngineStatus engineStatusObject = new EngineStatus();
                    engineStatusObject.setTimestamp(pdpStatistics.getTimeStamp().toString());
                    engineStatusObject.setId(engineStats.getEngineId());
                    engineStatusObject.setStatus(engineStats.getEngineWorkerState().name());
                    engineStatusObject.setLastMessage(new Date(engineStats.getEngineTimeStamp()).toString());
                    engineStatusObject.setUpTime(engineStats.getUpTime());
                    engineStatusObject.setPolicyExecutions(engineStats.getEventCount());
                    engineStatusObject.setLastPolicyDuration(gson.toJson(
                            getValuesFromCache(id, engineStats.getEngineId() + "_last_policy_duration",
                                    pdpStatistics.getTimeStamp().getEpochSecond(), engineStats.getLastExecutionTime()),
                            List.class));
                    engineStatusObject.setAveragePolicyDuration(
                            gson.toJson(getValuesFromCache(id, engineStats.getEngineId() + "_average_policy_duration",
                                    pdpStatistics.getTimeStamp().getEpochSecond(),
                                    (long) engineStats.getAverageExecutionTime()), List.class));
                    engineStatusList.add(engineStatusObject);
                } catch (final RuntimeException e) {
                    LOGGER.warn("Error getting status of engine with ID " + engineStats.getEngineId() + "<br>", e);
                }
            }
        } else {
            responseObject.setTimeStamp("N/A");
            responseObject.setPolicyDeployCount("N/A");
            responseObject.setPolicyDeploySuccessCount("N/A");
            responseObject.setPolicyDeployFailCount("N/A");
            responseObject.setPolicyExecutedCount("N/A");
            responseObject.setPolicyExecutedSuccessCount("N/A");
            responseObject.setPolicyExecutedFailCount("N/A");
        }

        responseObject.setStatus(engineStatusList);
        return Response.ok(new StandardCoder().encode(responseObject), MediaType.APPLICATION_JSON).build();
    }

    private HttpClient getHttpClient(String useHttps, String hostname, int port, String username, String password,
            String basePath) throws HttpClientConfigException {
        var busParams = new BusTopicParams();
        busParams.setClientName("pdp-monitoring");
        busParams.setHostname(hostname);
        busParams.setManaged(false);
        busParams.setPassword(password);
        busParams.setPort(port);
        busParams.setUseHttps(useHttps.equals("https"));
        busParams.setUserName(username);
        busParams.setBasePath(basePath);
        return HttpClientFactoryInstance.getClientFactory().build(busParams);
    }

    /**
     * This method takes in the latest data entry for an engine, adds it to an existing data set and
     * returns the full map for that host and engine.
     *
     * @param uri the pdp uri
     * @param id the engines id
     * @param timestamp the timestamp of the latest data entry
     * @param latestValue the value of the latest data entry
     * @return a list of {@code Counter} objects for that engine
     */
    private synchronized List<Counter> getValuesFromCache(final String uri, final String id, final long timestamp,
            final long latestValue) {

        Map<String, List<Counter>> engineStatus = cache.computeIfAbsent(uri, k -> new HashMap<>());

        List<Counter> valueList = engineStatus.computeIfAbsent(id, k -> new SlidingWindowList<>(MAX_CACHED_ENTITIES));

        valueList.add(new Counter(timestamp, latestValue));

        return valueList;
    }

    /**
     * A list of values that uses a FIFO sliding window of a fixed size.
     */
    @EqualsAndHashCode(callSuper = true)
    public class SlidingWindowList<V> extends LinkedList<V> {
        private static final long serialVersionUID = -7187277916025957447L;

        private final int maxEntries;

        public SlidingWindowList(final int maxEntries) {
            this.maxEntries = maxEntries;
        }

        @Override
        public boolean add(final V elm) {
            if (this.size() > (maxEntries - 1)) {
                this.removeFirst();
            }
            return super.add(elm);
        }
    }

    /**
     * A class used to storing a single data entry for an engine.
     */
    @Getter
    public class Counter {
        private final long timestamp;
        private final long value;

        public Counter(final long timestamp, final long value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }

}
