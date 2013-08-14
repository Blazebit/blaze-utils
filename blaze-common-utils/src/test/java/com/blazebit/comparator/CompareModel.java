/*
 * Copyright 2013 Blazebit.
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
package com.blazebit.comparator;

/**
 * This is the compare model used for the comparator tests with a clean
 * implementation on hash equals and compare method.
 *
 * @author Thomas Herzog
 */
public class CompareModel implements Comparable<CompareModel> {

    private final String value;
    private final CompareModel model;
    /**
     * The path to the CompareModel string value
     */
    static final String PATH_VALUE = "value";
    /**
     * The path to the CompareModel model (CompareModel) value
     */
    static final String PATH_MODEL = "model";
    /**
     * The path to the CompareModel model string value
     */
    static final String PATH_MODEL_VALUE = "model.value";
    /**
     * An invalid path which deos not resolve to anything
     */
    static final String PATH_INVALID = "esgsgsdfgsdfgg";

    /**
     * @param value 
     */
    public CompareModel(final String value) {
        this.value = value;
        model = null;
    }

    /**
     * @param model 
     */
    public CompareModel(final CompareModel model) {
        if (model != null) {
            this.value = model.toString();
            this.model = model;
        } else {
            this.value = null;
            this.model = null;
        }
    }

    public String getValue() {
        return value;
    }

    public CompareModel getModel() {
        return model;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompareModel other = (CompareModel) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        if (this.model != other.model && (this.model == null || !this.model.equals(other.model))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value != null ? value : model != null ? model.toString() : super.toString();
    }

    @Override
    public int compareTo(CompareModel o) {
        return value != null ? value.compareTo(o.getValue()) : model != null ? model.compareTo(o.getModel()) : 0;
    }
}
