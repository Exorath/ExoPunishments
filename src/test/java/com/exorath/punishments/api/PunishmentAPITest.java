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

import com.exorath.exodata.api.ExoCollection;
import com.github.fakemongo.Fongo;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by toonsev on 8/28/2016.
 */
public class PunishmentAPITest {

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionReturnedIfPunishmentAPICreatedWithNullParameter(){
        PunishmentAPI.create(null);
    }
    @Test
    public void createNotNullTest(){
        assertNotNull(PunishmentAPI.create(ExoCollection.create(new Fongo("fongodb").getDatabase("db").getCollection("coll"))));
    }
}
