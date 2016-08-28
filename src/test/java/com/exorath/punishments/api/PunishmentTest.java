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

import org.bson.Document;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by toonsev on 8/28/2016.
 */
public class PunishmentTest {
    @Test
    public void createNotNullTest(){
        assertNotNull(Punishment.create("reason", new Date(123), null, null, null));
    }

    @Test
    public void fromDocumentNotNullTest(){
        assertNotNull(Punishment.fromDocument(new Document(Punishment.REASON_KEY, "reason").append(Punishment.EXPIRY_DATE_KEY, new Date(123))));
    }
}
