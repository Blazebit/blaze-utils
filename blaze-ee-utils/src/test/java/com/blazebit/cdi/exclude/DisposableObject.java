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

import javax.enterprise.inject.Typed;

/**
 * @author Christian Beikov
 */
@Typed
public class DisposableObject implements StringHolder {

    private final String string;

    // For CDI
    public DisposableObject() {
        this.string = null;
    }

    public DisposableObject(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }
}
