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

package com.exorath.punishments.api;

import com.exorath.punishments.impl.IPunishment;
import org.bson.Document;

import java.util.Date;

/**
 * Created by toonsev on 8/27/2016.
 */
public interface Punishment {
    String REASON_KEY = "reason";
    String PUNISH_DATE_KEY = "date";
    String EXPIRY_DATE_KEY = "expires";
    String PARDONED_KEY = "pardoned";
    String METADATA_KEY = "metadata";

    /**
     * Gets the reason this punishment was given, or null if no reason was provided.
     *
     * @return the reason this punishment was given, or null if no reason was provided
     */
    String getReason();

    /**
     * Gets the date this punishment was given.
     *
     * @return the date this punishment was given
     */
    Date getPunishDate();

    /**
     * Gets the date this punishment expires, or null if it never expires (or is not an expiring punishment, like a warning)
     *
     * @return the date this punishment expires, or null if it never expires (or is not an expiring punishment, like a warning)
     */
    Date getExpiryDate();

    /**
     * Gets whether or not this punishment was pardoned, or null if this field was not set (that also means not pardoned)
     *
     * @return whether or not this punishment was pardoned, or null if this field was not set (that also means not pardoned)
     */
    Boolean isPardoned();


    /**
     * Gets the metadata of this punishment, or null if no metadata was provided.
     * Metadata is custom data added by the API consumer, it may contain anything (For example the punisher, severity, server, reporter...)
     *
     * @return the metadata of this punishment, or null if no metadata was provided
     */
    Document getMetadata();

    /**
     * Returns true if this punishment is still active, false if it is not. If pardoned this will always return false. If no expiryDate is set this will return false (if not pardoned).
     *
     * @return true if this punishment is still active, false if it is not
     */
    boolean isPunished();


    /**
     * Returns this Punishment in Document format, ready to be pushed to database.
     * You can turn it back to a punishment with {@link #fromDocument(Document)}.
     *
     * @return a document containing this punishment's information
     */
    Document toDocument();

    static Punishment fromDocument(Document document) {
        return create(document.getString(REASON_KEY), document.getDate(PUNISH_DATE_KEY), document.getDate(EXPIRY_DATE_KEY), document.getBoolean(PARDONED_KEY), document.get(METADATA_KEY, Document.class));
    }

    static Punishment create(String reason, Date punishDate, Date expiryDate, Boolean pardoned, Document metadata) {
        return new IPunishment(reason, punishDate, expiryDate, pardoned, metadata);
    }
}
