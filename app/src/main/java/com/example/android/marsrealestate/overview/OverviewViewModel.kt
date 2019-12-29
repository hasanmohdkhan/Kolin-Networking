/*
 * Copyright 2018, The Android Open Source Project
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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
enum class MarsApiStatus { LOADING, ERROR, DONE }
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    //private val _status = MutableLiveData<String>()
    private val _status = MutableLiveData<MarsApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    // encapsulated LiveData<MarsProperty> properties:
    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    //Add variables for a coroutine Job and a CoroutineScope using the Main Dispatcher:
    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)



    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()

    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    /*Add a function to set _navigateToSelectedProperty to marsProperty and
      initiate navigation to the detail screen on button click:
    */
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }
    /*
    * and you'll need to add displayPropertyDetailsComplete() to
    * set _navigateToSelectedProperty to false once navigation is
    * completed to prevent unwanted extra navigations:
    *
    * */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }


    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {

        coroutineScope.launch {

        var getPropertyDeffered  = MarsApi.retrofitService.getProperties()
            try {
                _status.value = MarsApiStatus.LOADING

                val listResult =  getPropertyDeffered.await()
                _status.value = MarsApiStatus.DONE
                _properties.value = listResult
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }



        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
