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

import com.exorath.punishments.api.Punishment;
import org.bson.Document;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by toonsev on 8/27/2016.
 */
public class IPunishment implements Punishment {
    private String reason;
    private Date punishDate;
    private Date expiryDate;
    private Boolean pardoned;
    private Document metaData;

    public IPunishment(String reason, Date punishDate, Date expiryDate, Boolean pardoned, Document metaData) {
        this.reason = reason;
        this.punishDate = punishDate;
        this.expiryDate = expiryDate;
        this.pardoned = pardoned;
        this.metaData = metaData;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public Date getPunishDate() {
        return punishDate;
    }

    @Override
    public Date getExpiryDate() {
        return expiryDate;
    }

    @Override
    public Boolean isPardoned() {
        return pardoned;
    }

    @Override
    public Document getMetadata() {
        return metaData;
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        if (reason != null)
            document.put(Punishment.REASON_KEY, reason);
        if (punishDate != null)
            document.put(Punishment.PUNISH_DATE_KEY, punishDate);
        if (expiryDate != null)
            document.put(Punishment.EXPIRY_DATE_KEY, expiryDate);
        if (pardoned != null)
            document.put(Punishment.PARDONED_KEY, pardoned);
        if (metaData != null)
            document.put(Punishment.METADATA_KEY, metaData);
        return document;
    }

    @Override
    public boolean isPunished() {
        if (pardoned != null && pardoned == true)
            return false;
        if (expiryDate == null)
            return true;
        return expiryDate.after(Calendar.getInstance().getTime());

    }
}
