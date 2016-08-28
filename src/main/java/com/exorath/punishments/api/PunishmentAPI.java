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
import com.exorath.punishments.impl.IPunishmentAPI;

/**
 * Created by toonsev on 8/19/2016.
 */
public interface PunishmentAPI {
    /**
     * Gets the Profile of a user, this method invokes no io and will always return a profile.
     * @param uniqueId the unique id of the user
     * @return the Profile of the user
     */
    Profile getProfile(String uniqueId);

    /**
     * Creates a new instance of {@link PunishmentAPI}. This method should not invoke any io.
     * @param collection
     * @return
     */
    static PunishmentAPI create(ExoCollection collection){
        return new IPunishmentAPI(collection);
    }
}
