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

import com.exorath.exodata.api.ExoCollection;
import com.exorath.punishments.api.Profile;
import com.exorath.punishments.api.PunishmentAPI;


/**
 * Created by toonsev on 8/20/2016.
 */
public class IPunishmentAPI implements PunishmentAPI {
    private ExoCollection collection;

    public IPunishmentAPI(ExoCollection collection) {
        if(collection == null)
            throw new IllegalArgumentException("Failed to create the PunishmentAPI because the provided ExoCollection parameter is null.");
        this.collection = collection;
    }

    @Override
    public Profile getProfile(String uniqueId) {
        return Profile.create(collection.getDocument(uniqueId));
    }
}
