package com.example.lunchtray.model

import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lunchtray.MainActivity
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat

internal class OrderViewModelTest {

    @Test
    fun calculateTaxAndTotal() {

        /**
         * Fails currently, because using LiveData, which requires context
         */
        //val context = ApplicationProvider.getApplicationContext<MainActivity>()
        val vm = OrderViewModel()

        vm.resetOrder()
        vm.setEntree("chili") // price(chili) = 4.00
        vm.setAccompaniment("pickles") // price(pickles) = 0.50
        vm.setSide("salad") // price(salad) = 2.50

        vm.calculateTaxAndTotal()

        // total = 4.00 + 2.50 + 0.50 = 7.00
        // tax = 7.00 * 0.08 = 0.56
        val expectedTotal =
            NumberFormat
                .getCurrencyInstance()
                .format(7.00)
        val expectedTax =
            NumberFormat
                .getCurrencyInstance()
                .format(0.56)

        assertEquals(vm.total.value, expectedTotal)
        assertEquals(vm.tax.value, expectedTax)

    }
}