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

package com.exorath.punishments;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.junit.After;
import org.junit.Before;

import java.net.UnknownHostException;

/**
 * Created by toonsev on 8/28/2016.
 */
public class MongoTest {
    private MongodForTestsFactory factory = null;

    @Before
    public void setup() throws Exception {
        factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
    }

    public MongoClient getClient() throws UnknownHostException {
        return factory.newMongo();
    }
    @After
    public void cleanup() {
        if (factory != null)
            factory.shutdown();
    }
}
