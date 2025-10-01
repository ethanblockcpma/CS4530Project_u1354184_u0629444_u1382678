package com.example.drawingapplication

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.junit.Test

import org.junit.Assert.*
import com.example.drawingapplication.DrawingViewModel
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun colorChangeCorrectly(){
        val vm = DrawingViewModel()

        assertEquals(Color.Black, vm.penColorReadOnly.value)

        vm.changePenColor(Color.Blue)

        assertEquals(Color.Blue, vm.penColorReadOnly.value)
    }

    @Test
    fun changeSizeCorrectly(){
        val vm = DrawingViewModel()
        assertEquals(8f, vm.penSizeReadOnly.value)

        vm.changePenSize(20f)
        assertEquals(20f, vm.penSizeReadOnly.value)
    }

    @Test
    fun changeShapeCorrectly(){
        val vm = DrawingViewModel()
        assertEquals("circle", vm.penShapeReadOnly.value)

        vm.changePenShape("rectangle")
        assertEquals("rectangle", vm.penShapeReadOnly.value)
    }

    @Test
    fun toggleCorrectly(){
        val vm = DrawingViewModel()
        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.COLOR)
        assertEquals(DrawingViewModel.PenOptions.COLOR, vm.penOptionsShownReadOnly.value)

        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.COLOR)

        assertEquals(DrawingViewModel.PenOptions.NONE, vm.penOptionsShownReadOnly.value)
    }

}