/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.gui.editors.apex.rest.handling;

import java.io.IOException;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The class represents the root resource exposed at the base URL<br>
 * The url to access this resource would be in the form {@code <baseURL>/policy/gui/v1/apex/editor/<session>/....},
 * for example: {@code http://localhost:8080/policy/gui/v1/apex/editor/109/ContextSchema/Update}<br>
 *
 * <b>Note:</b> An allocated {@code Session} identifier must be included in (almost) all requests. Models for different
 * {@code Session} identifiers are completely isolated from one another.
 *
 * <b>Note:</b> To create a new {@code Session}, and have a new session ID allocated use a GET request to
 * {@code <baseURL>/policy/gui/v1/apex/editor/-1/Session/Create}
 */
@RestController
@RequestMapping("/policy/gui/v1/apex/editor")
public class ApexEditorRestResource implements RestCommandHandler {

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEditorRestResource.class);

    // Location of the periodic event template
    private static final String PERIODIC_EVENT_TEMPLATE = "src/main/resources/templates/PeriodicEventTemplate.json";

    // Recurring string constants
    private static final String REST_COMMAND_NOT_RECOGNISED = "REST command not recognised";
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";
    private static final String SESSION_CREATE = "Session/Create";
    private static final String SESSION_CREATE_NOT_OK = "Session/Create: Not OK";

    // The session handler for sessions on the Apex editor
    private final RestSessionHandler sessionHandler;

    // Handlers for the various parts of an Apex model
    private final ModelHandler modelHandler;
    private final KeyInfoHandler keyInfoHandler;
    private final ContextSchemaHandler contextSchemaHandler;
    private final ContextAlbumHandler contextAlbumHandler;
    private final EventHandler eventHandler;
    private final TaskHandler taskHandler;
    private final PolicyHandler policyHandler;

    /**
     * Autowired constructor.
     */
    @Autowired
    public ApexEditorRestResource(RestSessionHandler sessionHandler,
                                  ModelHandler modelHandler,
                                  KeyInfoHandler keyInfoHandler,
                                  ContextSchemaHandler contextSchemaHandler,
                                  ContextAlbumHandler contextAlbumHandler,
                                  EventHandler eventHandler,
                                  TaskHandler taskHandler,
                                  PolicyHandler policyHandler) {
        this.sessionHandler = sessionHandler;
        this.modelHandler = modelHandler;
        this.keyInfoHandler = keyInfoHandler;
        this.contextSchemaHandler = contextSchemaHandler;
        this.contextAlbumHandler = contextAlbumHandler;
        this.eventHandler = eventHandler;
        this.taskHandler = taskHandler;
        this.policyHandler = policyHandler;
    }

    /**
     * Creates a new session. Always call this method with sessionID -1, whereby a new sessionID will be allocated. If
     * successful the new sessionID will be available in the first message in the result.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}. This includes the session id
     *         for this session.
     */
    @GetMapping("/{sessionId}/Session/Create")
    public ApexApiResult createSession(@PathVariable final int sessionId) {
        if (sessionId != -1) {
            return new ApexApiResult(Result.FAILED, "Session ID must be set to -1 to create sessions: " + sessionId);
        }

        var result = new ApexApiResult();
        sessionHandler.createSession(result);
        return result;
    }

    /**
     * Load the model from a JSON string for this session.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. The returned value(s) will be similar to {@code AxPolicyModel},
     *                   with merged {@code AxKeyInfo} for the root object.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/Model/Load")
    public ApexApiResult loadFromString(@PathVariable final int sessionId,
                                        @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.LOAD, jsonString);
    }

    /**
     * Analyse the model and return analysis results. If successful the analysis results will be available in the
     * messages in the result.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Model/Analyse")
    public ApexApiResult analyse(@PathVariable final int sessionId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.ANALYSE);
    }

    /**
     * Validate the model and return validation results. If successful the validation results will be available in the
     * messages in the result.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Model/Validate")
    public ApexApiResult validate(@PathVariable final int sessionId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.VALIDATE);
    }

    /**
     * Creates the new model model for this session.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed containing the new model. See {@code BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/Model/Create")
    public ApexApiResult createModel(@PathVariable final int sessionId,
                                     @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.CREATE, jsonString);
    }

    /**
     * Update the model for this session.
     *processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.CREATE, jsonString);
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed containing the updated model. See {@code BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/Model/Update")
    public ApexApiResult updateModel(@PathVariable final int sessionId,
                                     @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.UPDATE, jsonString);
    }

    /**
     * Gets the key for the model for this session. If successful the model key will be available in the first message
     * in the result. See {@code AxKey}
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Model/GetKey")
    public ApexApiResult getModelKey(@PathVariable final int sessionId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.GET_KEY);
    }

    /**
     * Retrieve the model for this session. If successful the model will be available in the first message in the
     * result. The returned value will be similar to a {@code AxPolicyModel}, with merged {@code AxKeyInfo} for the root
     * object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Model/Get")
    public ApexApiResult listModel(@PathVariable final int sessionId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.LIST);
    }

    /**
     * Download the model for this session as a String.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return the model represented as a YAML string. See {@code AxPolicyModel}
     */
    @GetMapping(value = "/{sessionId}/Model/Download", produces = MediaType.TEXT_PLAIN_VALUE)
    public String downloadModel(@PathVariable final int sessionId) {
        ApexApiResult result = processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.DOWNLOAD);
        if (result != null && result.isOk()) {
            return result.getMessage();
        } else {
            return null;
        }
    }

    /**
     * Uploads a TOSCA Policy Model to a configured endpoint.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param userId    the userId to use for upload. If blank, the Spring
     *                  config parameter "apex-editor.upload-userid" is used.
     * @return an ApexAPIResult that contains the operation status and success/error messages
     */
    @GetMapping("/{sessionId}/Model/Upload")
    public ApexApiResult uploadModel(@PathVariable final int sessionId,
                                     @RequestParam(required = false) final String userId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.UPLOAD, userId);
    }

    /**
     * Delete the model for this session.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/Model/Delete")
    public ApexApiResult deleteModel(@PathVariable final int sessionId) {
        return processRestCommand(sessionId, RestCommandType.MODEL, RestCommand.DELETE);
    }

    /**
     * List key information with the given key names/versions. If successful the result(s) will be available in the
     * result messages. See {@code AxKeyInfo}
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/KeyInformation/Get")
    public ApexApiResult listKeyInformation(@PathVariable final int sessionId,
                                            @RequestParam(required = false) final String name,
        @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.KEY_INFO, RestCommand.LIST, name, version);
    }

    /**
     * Creates a context schema with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/ContextSchema/Create")
    public ApexApiResult createContextSchema(@PathVariable final int sessionId,
                                             @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_SCHEMA, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a context schema with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/ContextSchema/Update")
    public ApexApiResult updateContextSchema(@PathVariable final int sessionId,
                                             @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_SCHEMA, RestCommand.UPDATE, jsonString);
    }

    /**
     * List context schemas with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@code AxContextSchema}, with merged {@code AxKeyInfo}
     * for the root object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/ContextSchema/Get")
    public ApexApiResult listContextSchemas(@PathVariable final int sessionId,
                                            @RequestParam(required = false) final String name,
                                            @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_SCHEMA, RestCommand.LIST, name, version);
    }

    /**
     * Delete context schemas with the given key names/versions.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/ContextSchema/Delete")
    public ApexApiResult deleteContextSchema(@PathVariable final int sessionId,
                                             @RequestParam(required = false) final String name,
                                             @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_SCHEMA, RestCommand.DELETE, name, version);
    }

    /**
     * Validate context schemas with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Validate/ContextSchema")
    public ApexApiResult validateContextSchemas(@PathVariable final int sessionId,
                                                @RequestParam(required = false) final String name,
                                                @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_SCHEMA, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates a context album with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/ContextAlbum/Create")
    public ApexApiResult createContextAlbum(@PathVariable final int sessionId,
                                            @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_ALBUM, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a context album with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/ContextAlbum/Update")
    public ApexApiResult updateContextAlbum(@PathVariable final int sessionId,
                                            @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_ALBUM, RestCommand.UPDATE, jsonString);
    }

    /**
     * List context albums with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@code AxContextAlbum}, with merged {@code AxKeyInfo}
     * for the root object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/ContextAlbum/Get")
    public ApexApiResult listContextAlbums(@PathVariable final int sessionId,
                                           @RequestParam(required = false) final String name,
                                           @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_ALBUM, RestCommand.LIST, name, version);
    }

    /**
     * Delete context albums with the given key names/versions.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/ContextAlbum/Delete")
    public ApexApiResult deleteContextAlbum(@PathVariable final int sessionId,
                                            @RequestParam(required = false) final String name,
                                            @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_ALBUM, RestCommand.DELETE, name, version);
    }

    /**
     * Validate context albums with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Validate/ContextAlbum")
    public ApexApiResult validateContextAlbums(@PathVariable final int sessionId,
                                               @RequestParam(required = false) final String name,
                                               @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.CONTEXT_ALBUM, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates an event with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/Event/Create")
    public ApexApiResult createEvent(@PathVariable final int sessionId,
                                     @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.CREATE, jsonString);
    }

    /**
     * Update an event with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/Event/Update")
    public ApexApiResult updateEvent(@PathVariable final int sessionId,
                                     @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.UPDATE, jsonString);
    }

    /**
     * List events with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxEvent}, with merged {@code AxKeyInfo} for the root
     * object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Event/Get")
    public ApexApiResult listEvent(@PathVariable final int sessionId,
                                   @RequestParam(required = false) final String name,
                                   @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.LIST, name, version);
    }

    /**
     * Delete events with the given key names/versions.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/Event/Delete")
    public ApexApiResult deleteEvent(@PathVariable final int sessionId,
                                     @RequestParam(required = false) final String name,
                                     @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.DELETE, name, version);
    }

    /**
     * Validate events with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Validate/Event")
    public ApexApiResult validateEvent(@PathVariable final int sessionId,
                                       @RequestParam(required = false) final String name,
                                       @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates a task with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/Task/Create")
    public ApexApiResult createTask(@PathVariable final int sessionId,
                                    @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.TASK, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a task with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed. See {@code BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/Task/Update")
    public ApexApiResult updateTask(@PathVariable final int sessionId,
                                    @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.TASK, RestCommand.UPDATE, jsonString);
    }

    /**
     * List tasks with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxTask}, with merged {@code AxKeyInfo} for the root
     * object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Task/Get")
    public ApexApiResult listTask(@PathVariable final int sessionId,
                                  @RequestParam(required = false) final String name,
                                  @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.TASK, RestCommand.LIST, name, version);
    }

    /**
     * Delete tasks with the given key names/versions.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/Task/Delete")
    public ApexApiResult deleteTask(@PathVariable final int sessionId,
                                    @RequestParam(required = false) final String name,
                                    @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.TASK, RestCommand.DELETE, name, version);
    }

    /**
     * Validate tasks with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Validate/Task")
    public ApexApiResult validateTask(@PathVariable final int sessionId,
                                      @RequestParam(required = false) final String name,
                                      @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.TASK, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates a policy with the information in the JSON string passed.
     *
     * @param sessionId  the ID of this session. This gets injected from the URL.
     * @param jsonString the JSON string to be parsed See {@code BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PostMapping("/{sessionId}/Policy/Create")
    public ApexApiResult createPolicy(@PathVariable final int sessionId,
                                      @RequestBody(required = false) final String jsonString) {
        return processRestCommand(sessionId, RestCommandType.POLICY, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a policy with the information in the JSON string passed.
     *
     * @param sessionId          the ID of this session. This gets injected from the URL.
     * @param firstStatePeriodic indicates if periodic event should be created and added to model
     * @param jsonString         the JSON string to be parsed. See {@code BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PutMapping("/{sessionId}/Policy/Update")
    public ApexApiResult updatePolicy(@PathVariable final int sessionId,
                                      @RequestParam(required = false) final boolean firstStatePeriodic,
                                      @RequestBody(required = false) final String jsonString) {
        ApexApiResult result = processRestCommand(sessionId, RestCommandType.POLICY, RestCommand.UPDATE, jsonString);
        if (result != null && result.isOk() && firstStatePeriodic) {
            result = createPeriodicEvent(sessionId);
        }
        return result;
    }

    /**
     * List policies with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxPolicy}, with merged {@code AxKeyInfo} for the root
     * object.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GetMapping("/{sessionId}/Policy/Get")
    public ApexApiResult listPolicy(@PathVariable final int sessionId,
                                    @RequestParam(required = false) final String name,
                                    @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.POLICY, RestCommand.LIST, name, version);
    }

    /**
     * Delete policies with the given key names/versions.
     *
     * @param sessionId the ID of this session. This gets injected from the URL.
     * @param name      the name to search for. If null or empty, then all names will be queried
     * @param version   the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DeleteMapping("/{sessionId}/Policy/Delete")
    public ApexApiResult deletePolicy(@PathVariable final int sessionId,
                                      @RequestParam(required = false) final String name,
                                      @RequestParam(required = false) final String version) {
        return processRestCommand(sessionId, RestCommandType.POLICY, RestCommand.DELETE, name, version);
    }

    /**
     * This method routes REST commands that take no parameters to their caller.
     *
     * @param sessionId   the Apex editor session ID
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final int sessionId, final RestCommandType commandType,
                                             final RestCommand command) {
        LOGGER.entry(commandType);
        try {
            var result = new ApexApiResult();
            RestSession session = sessionHandler.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method routes REST commands that take a JSON string to their caller.
     *
     * @param sessionId   the Apex editor session ID
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @param jsonString  the JSON string received in the REST request
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final int sessionId, final RestCommandType commandType,
                                             final RestCommand command, final String jsonString) {
        LOGGER.entry(commandType, jsonString);
        try {
            var result = new ApexApiResult();
            RestSession session = sessionHandler.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command, jsonString);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method routes REST commands that take a name and version to their caller.
     *
     * @param sessionId   the Apex editor session ID
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @param name        the name received in the REST request
     * @param version     the name received in the REST request
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final int sessionId, final RestCommandType commandType,
                                             final RestCommand command, final String name, final String version) {
        LOGGER.entry(commandType, name, version);
        try {
            var result = new ApexApiResult();
            RestSession session = sessionHandler.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command, name, version);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method invokes callers to run REST commands that take no parameters.
     *
     * @param session     the Apex editor session
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                                            final RestCommand command) {
        switch (commandType) {
            case MODEL:
                return modelHandler.executeRestCommand(session, commandType, command);
            case KEY_INFO:
                return keyInfoHandler.executeRestCommand(session, commandType, command);
            case CONTEXT_SCHEMA:
                return contextSchemaHandler.executeRestCommand(session, commandType, command);
            case CONTEXT_ALBUM:
                return contextAlbumHandler.executeRestCommand(session, commandType, command);
            case EVENT:
                return eventHandler.executeRestCommand(session, commandType, command);
            case TASK:
                return taskHandler.executeRestCommand(session, commandType, command);
            case POLICY:
                return policyHandler.executeRestCommand(session, commandType, command);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * This method invokes callers to run REST commands that take a JSON string.
     *
     * @param session     the Apex editor session
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @param jsonString  the JSON string received in the REST request
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                                            final RestCommand command, final String jsonString) {
        switch (commandType) {
            case MODEL:
                return modelHandler.executeRestCommand(session, commandType, command, jsonString);
            case KEY_INFO:
                return keyInfoHandler.executeRestCommand(session, commandType, command, jsonString);
            case CONTEXT_SCHEMA:
                return contextSchemaHandler.executeRestCommand(session, commandType, command, jsonString);
            case CONTEXT_ALBUM:
                return contextAlbumHandler.executeRestCommand(session, commandType, command, jsonString);
            case EVENT:
                return eventHandler.executeRestCommand(session, commandType, command, jsonString);
            case TASK:
                return taskHandler.executeRestCommand(session, commandType, command, jsonString);
            case POLICY:
                return policyHandler.executeRestCommand(session, commandType, command, jsonString);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * This method invokes callers to run REST commands that take a name and version.
     *
     * @param session     the Apex editor session
     * @param commandType the type of REST command to process
     * @param command     the REST command to process
     * @param name        the name received in the REST request
     * @param version     the name received in the REST request
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                                            final RestCommand command, final String name, final String version) {
        switch (commandType) {
            case MODEL:
                return modelHandler.executeRestCommand(session, commandType, command, name, version);
            case KEY_INFO:
                return keyInfoHandler.executeRestCommand(session, commandType, command, name, version);
            case CONTEXT_SCHEMA:
                return contextSchemaHandler.executeRestCommand(session, commandType, command, name, version);
            case CONTEXT_ALBUM:
                return contextAlbumHandler.executeRestCommand(session, commandType, command, name, version);
            case EVENT:
                return eventHandler.executeRestCommand(session, commandType, command, name, version);
            case TASK:
                return taskHandler.executeRestCommand(session, commandType, command, name, version);
            case POLICY:
                return policyHandler.executeRestCommand(session, commandType, command, name, version);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * Create a periodic event from the periodic event template.
     */
    private ApexApiResult createPeriodicEvent(final int sessionId) {
        String periodicEventJsonString;
        try {
            periodicEventJsonString = TextFileUtils.getTextFileAsString(PERIODIC_EVENT_TEMPLATE);
        } catch (IOException ioException) {
            String message = "read of periodic event template from " + PERIODIC_EVENT_TEMPLATE + "failed: "
                + ioException.getMessage();
            LOGGER.debug(message, ioException);
            return new ApexApiResult(Result.FAILED, message);
        }

        return processRestCommand(sessionId, RestCommandType.EVENT, RestCommand.CREATE, periodicEventJsonString);
    }

}
