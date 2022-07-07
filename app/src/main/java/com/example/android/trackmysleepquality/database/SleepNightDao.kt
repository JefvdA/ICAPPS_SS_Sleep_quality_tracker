/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepNightDao {
    @Query("SELECT * from tblSleepNights ORDER BY id DESC")
    fun selectAll(): LiveData<List<SleepNight>>

    @Query("SELECT * FROM tblSleepNights WHERE id=:id")
    suspend fun selectById(id: Int): SleepNight?

    @Query("SELECT * FROM tblSleepNights ORDER BY id DESC LIMIT 1")
    suspend fun selectLatest(): SleepNight?

    @Insert
    suspend fun insert(sleepNight: SleepNight)

    @Update
    suspend fun update(sleepNight: SleepNight)

    @Query("DELETE FROM tblSleepNights")
    suspend fun deleteAll()
}