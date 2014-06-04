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

package com.blazebit.persistence.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ccbem
 */
@Entity
public class Person {
    private long id;
    private Document partnerDocument;
    private Set<Document> ownedDocuments = new HashSet<Document>();
    
    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne
    public Document getPartnerDocument() {
        return partnerDocument;
    }

    public void setPartnerDocument(Document partnerDocument) {
        this.partnerDocument = partnerDocument;
    }

    @OneToMany
    public Set<Document> getOwnedDocuments() {
        return ownedDocuments;
    }

    public void setOwnedDocuments(Set<Document> ownedDocuments) {
        this.ownedDocuments = ownedDocuments;
    }
    
    
}
