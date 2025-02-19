/*
 * Copyright (C) 2021 The Android Open Source Project.
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
package com.example.lunchtray.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    // Map of menu items
    val menuItems = DataSource.menuItems

    // Default values for item prices
    private var previousEntreePrice = 0.0
    private var previousSidePrice = 0.0
    private var previousAccompanimentPrice = 0.0

    // Default tax rate
    private val taxRate = 0.08

    // Entree for the order
    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree

    // Side for the order
    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side

    // Accompaniment for the order.
    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment

    // Subtotal for the order
    private val _subtotal = MutableLiveData(0.0)
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Total cost of the order
    private val _total = MutableLiveData(0.0)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Tax for the order
    private val _tax = MutableLiveData(0.0)
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set the entree for the order.
     */
    fun setEntree(entree: String) {
        if (_entree.value != null) {
            // if _entree.value is not null, set the previous entree price to the current
            //  entree price.
            previousEntreePrice = _entree.value!!.price
        }
        if (_subtotal.value != null) {
            // if _subtotal.value is not null subtract the previous entree price from the current
            //  subtotal value. This ensures that we only charge for the currently selected entree.
            _subtotal.value = _subtotal.value!!.minus(previousEntreePrice)
        }
        // set the current entree value to the menu item corresponding to the passed in string
        _entree.value = menuItems[entree]
        // update the subtotal to reflect the price of the selected entree.
        _entree.value?.let { updateSubtotal(it.price) }
    }

    /**
     * Set the side for the order.
     */
    fun setSide(side: String) {
        if (_side.value != null) {
            // if _side.value is not null, set the previous side price to the current side price.
            previousSidePrice = _side.value!!.price
        }
        if (_subtotal.value != null) {
            // if _subtotal.value is not null subtract the previous side price from the current
            //  subtotal value. This ensures that we only charge for the currently selected side.
            _subtotal.value = _subtotal.value?.minus(previousSidePrice)
        }
        // set the current side value to the menu item corresponding to the passed in string
        _side.value = menuItems[side]
        // update the subtotal to reflect the price of the selected side.
        _side.value?.let { updateSubtotal(it.price) }
    }

    /**
     * Set the accompaniment for the order.
     */
    fun setAccompaniment(accompaniment: String) {
        if (_accompaniment.value != null) {
            // if _accompaniment.value is not null, set the previous accompaniment price to the
            //  current accompaniment price.
            previousAccompanimentPrice = _accompaniment.value!!.price
        }
        if (_accompaniment.value != null) {
            // if _accompaniment.value is not null subtract the previous accompaniment price from
            //  the current subtotal value. This ensures that we only charge for the currently selected
            //  accompaniment.
            _subtotal.value = _subtotal.value?.minus(previousAccompanimentPrice)
        }

        // set the current accompaniment value to the menu item corresponding to the passed in
        //  string
        _accompaniment.value = menuItems[accompaniment]
        // update the subtotal to reflect the price of the selected accompaniment.
        _accompaniment.value?.let { updateSubtotal(it.price) }
    }

    /**
     * Update subtotal value.
     */
    private fun updateSubtotal(itemPrice: Double) {
        if (_subtotal.value != null) {
            // if _subtotal.value is not null, update it to reflect the price of the recently
            //  added item.
            _subtotal.value = _subtotal.value!!.plus(itemPrice)
        } else {
            //  Otherwise, set _subtotal.value to equal the price of the item.
            _subtotal.value = itemPrice
        }
        // calculate the tax and resulting total
        calculateTaxAndTotal()
    }

    /**
     * Calculate tax and update total.
     */
    fun calculateTaxAndTotal() {
        // set _tax.value based on the subtotal and the tax rate.
        _tax.value = _subtotal.value?.times(taxRate)
        // set the total based on the subtotal and _tax.value.
        _tax.value?.let { _total.value = _subtotal.value?.plus(it) }
    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        _entree.postValue(null)
        _side.postValue(null)
        _accompaniment.postValue(null)
        _subtotal.postValue(0.0)
        _total.postValue(0.0)
        _tax.postValue(0.0)
    }
}
