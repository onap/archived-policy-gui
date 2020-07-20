/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.gui.editors.apex.rest.handling.converter.tosca;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
public class ProcessedTemplate {

    private String content;
    private final Set<String> errorSet;

    public ProcessedTemplate() {
        errorSet = new HashSet<>();
    }

    /**
     * Adds the given error messages to the errors collection.
     *
     * @param errorSet a set of error messages
     */
    public void addToErrors(final Set<String> errorSet) {
        if (CollectionUtils.isEmpty(errorSet)) {
            return;
        }

        this.errorSet.addAll(errorSet);
    }

    /**
     * Checks if the processed template is valid.
     *
     * @return {@code true} if the content is valid, {@code false} otherwise
     */
    public boolean isValid() {
        return CollectionUtils.isEmpty(errorSet);
    }
}
