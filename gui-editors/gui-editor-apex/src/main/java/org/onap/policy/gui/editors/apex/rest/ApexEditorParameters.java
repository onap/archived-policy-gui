/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.Data;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Data
@Generated
public class ApexEditorParameters {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEditorParameters.class);

    /** The default port for connecting to the Web editor on. */
    public static final int DEFAULT_REST_PORT = 18989;

    /** The connection is held up until killed on demand. */
    public static final int INFINITY_TIME_TO_LIVE = -1;

    // Base URI the HTTP server will listen on
    private static final String DEFAULT_SERVER_URI_PREFIX = "http://";
    /** The server listens on localhost by default. */
    public static final String DEFAULT_SERVER_URI_ROOT = "localhost";
    private static final String DEFAULT_REST_PATH = "apexservices/";
    private static final String DEFAULT_STATIC_PATH = "/";

    // Constants for port checks
    private static final int MIN_USER_PORT = 1024;
    private static final int MAX_USER_PORT = 65535;

    // Package that will field REST requests
    private static final String[] DEFAULT_PACKAGES = new String[] { "org.onap.policy.gui.editors.apex.rest" };

    // The editor parameters
    private boolean help = false;
    private int restPort = DEFAULT_REST_PORT;
    private long timeToLive = INFINITY_TIME_TO_LIVE;
    private String listenAddress = DEFAULT_SERVER_URI_ROOT;
    private String uploadUrl = null;
    private String uploadUserid = null;

    /**
     * Validate.
     *
     * @return the string
     */
    public String validate() {
        return validatePort() + validateTimeToLive() + validateUrl() + validateUploadUrl() + validateUploadUserid();
    }

    /**
     * Gets the base URI.
     *
     * @return the base URI
     */
    public URI getBaseUri() {
        return URI.create(DEFAULT_SERVER_URI_PREFIX + listenAddress + ':' + restPort + "/" + DEFAULT_REST_PATH);
    }

    /**
     * Gets the REST packages.
     *
     * @return the REST packages
     */
    public String[] getRestPackages() {
        return DEFAULT_PACKAGES;
    }

    /**
     * Gets the static path.
     *
     * @return the static path
     */
    public String getStaticPath() {
        return DEFAULT_STATIC_PATH;
    }

    /**
     * Validate port.
     *
     * @return a warning string, or an empty string
     */
    private String validatePort() {
        if (restPort < MIN_USER_PORT || restPort > MAX_USER_PORT) {
            return "port must be between " + MIN_USER_PORT + " and " + MAX_USER_PORT + "\n";
        } else {
            return "";
        }
    }

    /**
     * Validate URL.
     *
     * @return a warning string, or an empty string
     */
    private String validateUrl() {
        try {
            new URI(getBaseUri().toString()).parseServerAuthority();
            return "";
        } catch (final URISyntaxException e) {
            String message = "listen address is not valid. " + e.getMessage() + "\n";
            LOGGER.warn(message, e);
            return message;
        }
    }

    /**
     * Validate time to live.
     *
     * @return the string
     */
    private String validateTimeToLive() {
        if (timeToLive < -1) {
            return "time to live must be greater than -1 (set to -1 to wait forever)\n";
        } else {
            return "";
        }
    }

    private String validateUploadUrl() {
        if (!StringUtils.isBlank(uploadUrl)) {
            try {
                new URL(uploadUrl);
            } catch (MalformedURLException murle) {
                return "Specified upload-url parameter is an invalid URL" + murle.getMessage() + "\n";
            }
        }
        return "";
    }

    private String validateUploadUserid() {
        if (!StringUtils.isEmpty(uploadUrl) && StringUtils.isEmpty(uploadUserid)) {
            return "upload-userid parameter must be specified if the upload-url parameter is specified\n";
        } else {
            return "";
        }
    }
}
