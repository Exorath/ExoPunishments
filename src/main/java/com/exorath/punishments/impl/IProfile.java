/*
 * Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.punishments.impl;

import com.exorath.exodata.api.ExoDocument;
import com.exorath.punishments.api.Profile;
import com.exorath.punishments.api.Punishment;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.List;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.slice;

/**
 * Created by toonsev on 8/27/2016.
 */
public class IProfile implements Profile {
    public static final String PUNISHMENTS_KEY = "punishments";
    private ExoDocument document;

    public IProfile(ExoDocument document) {
        this.document = document;
    }

    @Override
    public Observable<Punishment> getPunishments(String punishmentId) {
        Observable<Document> rootDocument =  fetchDocument(include(getPunishmentRootKey(punishmentId)));
        return getPunishments(rootDocument, punishmentId);
    }

    @Override
    public Observable<Punishment> getLastPunishment(String punishmentId) {
        return getLastPunishments(punishmentId, -1);
    }
    private Observable<Punishment> getPunishments(Observable<Document> rootDocumentObservable, String punishmentId){
        Observable<Document> punishmentsDocument = mapRootDocumentToPunishmentsDocument(rootDocumentObservable);
        Observable<Document> punishmentDocuments = mapPunishmentsDocumentToPunishmentDocuments(punishmentsDocument, punishmentId);
        return mapPunishmentDocumentsToPunishments(punishmentDocuments);
    }

    @Override
    public Observable<Punishment> getLastPunishments(String punishmentId, int amount) {
        Observable<Document> rootDocument =  fetchDocument(fields(include(getPunishmentRootKey(punishmentId)), slice(getPunishmentRootKey(punishmentId), -amount)));
        return getPunishments(rootDocument, punishmentId);
    }

    @Override
    public Observable<Boolean> isPunished(String punishmentId) {
        return getLastPunishment(punishmentId).map(punishment -> punishment.isPunished()).startWith(false).last();
    }

    @Override
    public Observable<UpdateResult> punish(String punishmentId, Punishment punishment) {
        return document.push(getPunishmentRootKey(punishmentId), punishment.toDocument());
    }

    private Observable<Punishment> mapPunishmentDocumentsToPunishments(Observable<Document> punishmentDocuments){
        return punishmentDocuments.map(punishmentDoc -> Punishment.fromDocument(punishmentDoc));
    }

    private Observable<Document> mapPunishmentsDocumentToPunishmentDocuments(Observable<Document> punishmentsDocument, String punishmentId) {
        return punishmentsDocument.filter(punishmentsDoc -> punishmentsDoc.containsKey(punishmentId)) //Make sure the punishments document contains the specific punishmentId.
                .map(punishmentsDoc -> punishmentsDoc.get(punishmentId, List.class)) //Map to the specific punishmentId array
                .flatMap(punishmentDocs -> {//Flatmap the array to single documents
                    Observable<Document> returned = Observable.create(subscriber -> {
                        if (punishmentDocs != null)
                            punishmentDocs.stream().filter(element -> element instanceof Document).forEach(punishmentDoc -> subscriber.onNext((Document) punishmentDoc));
                        subscriber.onCompleted();
                    });
                    return returned;
                });
    }


    private Observable<Document> mapRootDocumentToPunishmentsDocument(Observable<Document> rootDocument) {
        return rootDocument.filter(rootDoc -> rootDoc.containsKey(PUNISHMENTS_KEY)) //Make sure the document has the PUNISHMENTS_KEY
                .map(rootDoc -> rootDoc.get(PUNISHMENTS_KEY, Document.class));//Map to the PUNISHMENTS_KEY Document
    }

    private Observable<Document> fetchDocument(Bson projection) {
        return document.fetch(projection);
    }

    private List<Document> getPunishmentDocument(Document rootDocument, String punishmentId) {
        Document punishmentsDocument = getPunishmentsDocument(rootDocument);
        return punishmentsDocument == null ? null : punishmentsDocument.get(punishmentId, List.class);
    }

    private Document getPunishmentsDocument(Document rootDocument) {
        return rootDocument.get(PUNISHMENTS_KEY, Document.class);
    }

    private String getPunishmentRootKey(String punishmentId){
        return PUNISHMENTS_KEY + "." + punishmentId;
    }
}
