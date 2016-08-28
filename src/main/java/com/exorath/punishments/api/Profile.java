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

import com.exorath.exodata.api.ExoDocument;
import com.exorath.punishments.impl.IProfile;
import com.mongodb.client.result.UpdateResult;
import rx.Observable;

/**
 * Created by toonsev on 8/27/2016.
 */
public interface Profile {
    /**
     * Gets an observable that emits all punishments ever received by this player of the given punishmentId type.
     * onCompleted will be called on the final punishment.
     * The order of these punishments is undetermined.
     *
     * @param punishmentId the id of this punishment type (for example 'ban', 'kick', 'warn')
     * @return all punishments ever received by this player of the given punishmentId type
     */
    Observable<Punishment> getPunishments(String punishmentId);

    /**
     * Gets an observable that emits the last punishment this user received of the specific punishmentId type.
     *
     * @param punishmentId the id of this punishment type (for example 'ban', 'kick', 'warn')
     * @return the last punishment this user received of the specific punishmentId type
     */
    Observable<Punishment> getLastPunishment(String punishmentId);

    /**
     * Gets an observable that emits the last <i>amount</i> punishments of a specific punishmentId type (or all punishments of that type if there are not enough)
     *
     * @param punishmentId the id of this punishment type (for example 'ban', 'kick', 'warn')
     * @param amount       the maximum amount of punishments that should be returned
     * @return the last <i>amount</i> punishments of a specific punishmentId type
     */
    Observable<Punishment> getLastPunishments(String punishmentId, int amount);

    /**
     * Gets an observable that emits a boolean that indicates whether or not the player has an active punishment of the provided punishmentId type.
     * <p>
     * Behavior: This method will check the last punishment received of the punishmentId type and if it is expired or pardoned, the player is not punished, otherwise he is (this means that new punishments override older ones!).
     *
     * @param punishmentId the id of this punishment type (for example 'ban', 'kick', 'warn')
     * @returnan observable that emits a boolean that indicates whether or not the player has an active punishment of the provided punishmentId type
     */
    Observable<Boolean> isPunished(String punishmentId);

    /**
     * Grants a certain punishment to the profile.
     * THIS OPERATION IS COLD, MAKE SURE TO SUBSCRIBE TO THE UPDATE RESULTS FOR IT TO HAVE EFFECT.
     *
     * @param punishmentId the id of this punishment type (for example 'ban', 'kick', 'warn')
     * @param punishment   the actual punishment to grant
     * @return the updateResults of this operation.
     */
    Observable<UpdateResult> punish(String punishmentId, Punishment punishment);


    /**
     * Creates a new Profile instance, does not involve any io.
     *
     * @param profileDocument profile's punishments document. This grants the profile access to the database.
     * @return a new Profile instance, ready to read and write data from and to the ExoDocument
     */
    static Profile create(ExoDocument profileDocument) {
        return new IProfile(profileDocument);
    }
}
