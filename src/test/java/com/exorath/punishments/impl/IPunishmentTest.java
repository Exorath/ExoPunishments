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

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by toonsev on 8/28/2016.
 */
public class IPunishmentTest {
    private static final String REASON = "You've been naughty";
    private static final Date PUNISH_DATE = Calendar.getInstance().getTime();
    private static final Date EXPIRES_DATE = new Date(Calendar.getInstance().getTime().getTime() + 1_000_000);
    private static final Boolean PARDONED = false;
    private static final Document METADATA = new Document("punisher", "Toon Sevrin");
    IPunishment punishmentAllFieldsSet = null;

    @Before
    public void setup(){
        punishmentAllFieldsSet = new IPunishment(REASON, PUNISH_DATE, EXPIRES_DATE, PARDONED, METADATA);
    }

    @Test
    public void getReasonEqualsReasonTest(){
        assertEquals(REASON, punishmentAllFieldsSet.getReason());
    }

    @Test
    public void getPunishDateEqualsPunishDateTest(){
        assertEquals(PUNISH_DATE, punishmentAllFieldsSet.getPunishDate());
    }

    @Test
    public void getExpiryDateEqualsExpiryDateTest(){
        assertEquals(EXPIRES_DATE, punishmentAllFieldsSet.getExpiryDate());
    }

    @Test
    public void isPardonedEqualsPardonedTest(){
        assertEquals(PARDONED, punishmentAllFieldsSet.isPardoned());
    }
    @Test
    public void getMetadataEqualsMetadataTest(){
        assertEquals(METADATA, punishmentAllFieldsSet.getMetadata());
    }
    @Test
    public void toDocumentEqualsDocumentTest(){
        Document document = new Document(IPunishment.REASON_KEY, REASON)
                .append(IPunishment.EXPIRY_DATE_KEY, EXPIRES_DATE)
                .append(IPunishment.PUNISH_DATE_KEY, PUNISH_DATE)
                .append(IPunishment.PARDONED_KEY, PARDONED)
                .append(IPunishment.METADATA_KEY, METADATA);
        assertEquals(document, punishmentAllFieldsSet.toDocument());
    }

    @Test
    public void isPunishedReturnsTrueWhenExpiryDateIsNotExpiredAndNotPardonedTest(){
        assertTrue(punishmentAllFieldsSet.isPunished());
    }
    @Test
    public void isPunishedReturnsFalseWhenExpiryDateIsNotExpiredAndPardonedTest(){
        IPunishment punishment = new IPunishment(null, Calendar.getInstance().getTime(),
                new Date(Calendar.getInstance().getTime().getTime() + 5000l), true, null);
        assertFalse(punishment.isPunished());
    }

    @Test
    public void isPunishedReturnsTrueIfNoExpiryDateSetAndNoPardonedSetWhenPardonedTest(){
        IPunishment punishment = new IPunishment(null, Calendar.getInstance().getTime(),
                null, null, null);
        assertTrue(punishment.isPunished());
    }
    @Test
    public void isPunishedReturnsTrueIfNoExpiryDateSetAndPardonedSetToFalsePardonedTest(){
        IPunishment punishment = new IPunishment(null, Calendar.getInstance().getTime(),
                null, null, null);
        assertTrue(punishment.isPunished());
    }
}
