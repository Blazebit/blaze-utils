/*
 * Copyright 2014 Blazebit.
 *
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
 */

package com.blazebit.cdi.exclude;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Christian Beikov
 */
@ApplicationScoped
public class TestBean {

    @Inject
    ExcludeInterface1 excludeInterface1;

    @Inject
    ExcludeInterface2 excludeInterface2;

    @Inject
    StringHolder disposableObject;

    @Inject
    Integer integer;

    public ExcludeInterface1 getExcludeInterface1() {
        return excludeInterface1;
    }

    public ExcludeInterface2 getExcludeInterface2() {
        return excludeInterface2;
    }

    public StringHolder getDisposableObject() {
        return disposableObject;
    }

    public Integer getInteger() {
        return integer;
    }
}
