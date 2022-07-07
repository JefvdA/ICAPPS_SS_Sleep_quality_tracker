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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.database.SleepNightDao
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepNightDao,
    application: Application) : AndroidViewModel(application) {

    private var tonight = MutableLiveData<SleepNight?>()

    var isTracking = MutableLiveData(false)

    init {
        initializeTonight()
    }

    companion object {
        val TAG = this::class.java.simpleName
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            with(getTonightFromDatabase()) {
                tonight.postValue(this)
                isTracking.postValue(this != null)
            }
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        var tonight = database.selectLatest()
        if(tonight?.startTimeInMillis != tonight?.endTimeInMillis){
            tonight = null
        }
        return tonight
    }

    fun onStartTracking() {
        viewModelScope.launch {
            isTracking.postValue(true)
            database.insert(SleepNight())
            initializeTonight()
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            isTracking.postValue(false)
            val oldTonight = tonight.value ?: return@launch
            oldTonight.endTimeInMillis = System.currentTimeMillis()
            database.update(oldTonight)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            database.deleteAll()
            initializeTonight()
        }
    }
}

