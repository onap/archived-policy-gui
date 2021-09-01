/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

function RenderPdpList(treeArr, className) {
    var $ = treeArr,
        root = document.createDocumentFragment(),
        childLevel = 0
        var index=''
        var isNode=false
    function insertChildren(parentNode, traverseArr, subGroup) {

        for(let value of traverseArr) {
            if(parentNode === root) {
                childLevel = 0
            }
            var currentLi = document.createElement('li')
            currentLi.setAttribute('level', childLevel)
            if(value.children && value.children.length > 0) {
                var title = document.createElement('div')
                var triangle = document.createElement('i')
                var text = document.createElement('p')
                currentLi.classList.add('parentNode')
                title.classList.add('title')
                triangle.classList.add('triangle')
                text.innerText = value.title
                title.appendChild(triangle)
                title.appendChild(text)
                currentLi.appendChild(title)
                childLevel++
                if(isNode) index=""
                if(subGroup !== null){
                    index+= subGroup+"/"
                }
                insertChildren(currentLi, value.children, value.title)
            }else {
                var a = document.createElement('a')
                a.setAttribute('href',"#"+index+subGroup+"/"+value.title)
                a.classList.add('pdps__link')
                a.textContent= value.title
                currentLi.appendChild(a)
                isNode=true
            }
            parentNode.appendChild(currentLi)
        }
    }
    insertChildren(root, $, null)
    document.querySelector('ul.' + className + '').appendChild(root)
}

function highlightSelected (id){
    const resultsArr = Array.from(document.querySelectorAll('.pdps__link'));
    resultsArr.forEach(el => {
        el.classList.remove('pdps__link--active');
    });
    document.querySelector(`.pdps__link[href*="${id}"]`).classList.add('pdps__link--active');
}

export { RenderPdpList, highlightSelected, };