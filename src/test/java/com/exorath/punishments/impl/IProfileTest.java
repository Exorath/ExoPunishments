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
import com.exorath.punishments.MongoTest;
import com.exorath.punishments.api.Punishment;
import com.sun.jna.platform.win32.OaIdl;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Created by toonsev on 8/28/2016.
 */
public class IProfileTest extends MongoTest {
    private static final String REASON = "You've been naughty!";
    private static final Date PUNISH_DATE = Calendar.getInstance().getTime();
    private String id;
    private ExoDocument document;
    private Document punishmentDocument;
    private IProfile profile;

    @Before
    public void setup() throws Exception {
        super.setup();
        id = UUID.randomUUID().toString();
        document = ExoDocument.create(getClient().getDatabase("db").getCollection("coll"), id);
        punishmentDocument = new Document(Punishment.REASON_KEY, REASON).append(Punishment.PUNISH_DATE_KEY, PUNISH_DATE);
        profile = new IProfile(document);
    }


    @Test(timeout = 3000)
    public void getLastPunishmentObservableNotNullTest() {
        assertNotNull(profile.getLastPunishment("bans"));
    }

    @Test(timeout = 3000)
    public void getLastPunishmentCompletesTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        profile.getLastPunishment("bans").toBlocking().subscribe(p -> {
        }, err -> {
            err.printStackTrace();
        }, () -> {
            completed.set(true);
        });
        assertTrue(completed.get());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentNotFoundIfNotAddedTest() {
        AtomicBoolean found = new AtomicBoolean(false);
        profile.getLastPunishment("bans").toBlocking().subscribe(p -> {
            found.set(true);
        }, err -> {
            err.printStackTrace();
        }, () -> {
        });
        assertFalse(found.get());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentFoundIfAddedTest() {
        AtomicBoolean found = new AtomicBoolean(false);
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        profile.getLastPunishment("bans").toBlocking().subscribe(p -> found.set(true));
        assertTrue(found.get());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentIsLastPunishmentTest() {
        AtomicBoolean found = new AtomicBoolean(false);
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        punishmentDocument.put(Punishment.REASON_KEY, "Second reason!");
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        Punishment fetched = profile.getLastPunishment("bans").toBlocking().first();
        assertEquals(REASON, fetched.getReason());
        assertEquals(PUNISH_DATE, fetched.getPunishDate());
        assertNull(fetched.getExpiryDate());
        assertNull(fetched.getMetadata());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentsReturnsLastPunishmentsTest() {
        AtomicBoolean reason1found = new AtomicBoolean(false);
        AtomicBoolean reason2found = new AtomicBoolean(false);

        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        punishmentDocument.put(Punishment.REASON_KEY, "Second reason!");
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();

        profile.getLastPunishments("bans",2).toBlocking().forEach(punishment -> {
            if(punishment.getReason().equals(REASON))
                reason1found.set(true);
            else if(punishment.getReason().equals("Second reason!"))
                reason2found.set(true);
        });

        assertTrue(reason1found.get() && reason2found.get());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentsCompletesTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        profile.getLastPunishments("bans", 3).toBlocking().subscribe(p -> {}, err -> {err.printStackTrace();}, () -> {
            completed.set(true);
        });
        assertTrue(completed.get());
    }

    @Test(timeout = 3000)
    public void getLastPunishmentsReturnsOnePunishmentIfOnlyOnePunishmentWasMadeTest() {
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        assertEquals((Integer) 1, profile.getLastPunishments("bans", 2).count().toBlocking().single());
    }

    @Test(timeout = 3000)
    public void getPunishmentsReturnsAddedPunishmentsTest() {
        AtomicBoolean reason1found = new AtomicBoolean(false);
        AtomicBoolean reason2found = new AtomicBoolean(false);

        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        punishmentDocument.put(Punishment.REASON_KEY, "Second reason!");
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();

        profile.getPunishments("bans").toBlocking().forEach(punishment -> {
            if(punishment.getReason().equals(REASON))
                reason1found.set(true);
            else if(punishment.getReason().equals("Second reason!"))
                reason2found.set(true);
        });
        assertTrue(reason1found.get() && reason2found.get());
    }
    @Test(timeout = 3000)
    public void getPunishmentsCompletesTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        profile.getPunishments("bans").toBlocking().subscribe(p -> {}, err -> {err.printStackTrace();}, () -> {
            completed.set(true);
        });
        assertTrue(completed.get());
    }
    @Test(timeout = 3000)
    public void isPunishedReturnsFalseIfNoPunishmentWasGrantedTest() {
        assertFalse(profile.isPunished("bans").toBlocking().first());
    }

    @Test(timeout = 3000)
    public void isPunishedReturnsTrueIfAPunishmentWasGrantedTest() {
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument).toBlocking().subscribe();
        assertTrue(profile.isPunished("bans").toBlocking().first());
    }

    @Test(timeout = 3000)
    public void isPunishedReturnsFalseIfAPunishmentWasPardonedGrantedTest() {
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument.append(Punishment.PARDONED_KEY, true)).toBlocking().subscribe();
        assertFalse(profile.isPunished("bans").toBlocking().first());
    }

    @Test(timeout = 3000)
    public void isPunishedReturnsFalseIfAPunishmentHasExpiredTest() {
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument.append(Punishment.EXPIRY_DATE_KEY, new Date(1))).toBlocking().subscribe();
        assertFalse(profile.isPunished("bans").toBlocking().first());
    }

    @Test(timeout = 3000)
    public void isPunishedReturnsTrueIfAPunishmentHasNotExpiredTest() {
        document.push(IProfile.PUNISHMENTS_KEY + ".bans", punishmentDocument.append(Punishment.EXPIRY_DATE_KEY, new Date(Calendar.getInstance().getTime().getTime() + 5000))).toBlocking().subscribe();
        assertTrue(profile.isPunished("bans").toBlocking().first());
    }
    @Test(timeout = 3000)
    public void isPunishedCompletesTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        profile.isPunished("bans").toBlocking().subscribe(p -> {}, err -> {err.printStackTrace();}, () -> {
            completed.set(true);
        });
        assertTrue(completed.get());
    }
    @Test(timeout = 3000)
    public void punishAddsDocumentToDatabaseTest() {
        profile.punish("bans", Punishment.fromDocument(punishmentDocument)).toBlocking().subscribe();
        Document doc = (Document) document.fetch().toBlocking().first().get(IProfile.PUNISHMENTS_KEY, Document.class).get("bans", List.class).get(0);
        assertEquals(punishmentDocument, doc);
    }

    @Test(timeout = 3000)
    public void punishCompletesTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        profile.punish("bans", Punishment.fromDocument(punishmentDocument)).toBlocking().subscribe(p -> {}, err -> {err.printStackTrace();}, () -> {
            completed.set(true);
        });
        assertTrue(completed.get());
    }

    @Test(timeout = 3000)
    public void punishObservableNotNullTestTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        assertNotNull(profile.punish("bans", Punishment.fromDocument(punishmentDocument)).toBlocking().first());
    }

    @Test(timeout = 3000)
    public void punishNotNullTestTest() {
        AtomicBoolean completed = new AtomicBoolean(false);
        assertNotNull(profile.punish("bans", Punishment.fromDocument(punishmentDocument)).toBlocking().first());
    }
}
