package com.example.drawingapplication

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.junit.Test

import org.junit.Assert.*
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.data.DrawingRepository
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.mockito.Mockito.mock

class DrawingApplicationUnitTest {

    @Test
    fun changeColorCorrectly(){
        // Test initial color is set correctly
        val vm = DrawingViewModel()
        assertEquals(Color.Black, vm.penColorReadOnly.value)

        // Test changing color updates correctly
        vm.changePenColor(Color.Blue)
        assertEquals(Color.Blue, vm.penColorReadOnly.value)
    }

    @Test
    fun changeSizeCorrectly(){
        // Test initial size is set correctly
        val vm = DrawingViewModel()
        assertEquals(8f, vm.penSizeReadOnly.value)

        // Test changing size updates correctly
        vm.changePenSize(20f)
        assertEquals(20f, vm.penSizeReadOnly.value)
    }

    @Test
    fun changeShapeCorrectly(){
        // Test initial shape is set correctly
        val vm = DrawingViewModel()
        assertEquals("circle", vm.penShapeReadOnly.value)

        // Test changing shape updates correctly
        vm.changePenShape("rectangle")
        assertEquals("rectangle", vm.penShapeReadOnly.value)
    }

    @Test
    fun toggleAndChangePenOptionsCorrectly(){
        // Test that no options are shown initially
        val vm = DrawingViewModel()
        assertEquals(DrawingViewModel.PenOptions.NONE, vm.penOptionsShownReadOnly.value)

        // Test showing pen options shown updates correctly
        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.SHAPE)
        assertEquals(DrawingViewModel.PenOptions.SHAPE, vm.penOptionsShownReadOnly.value)

        // Test changing pen options from one to another updates correctly
        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.COLOR)
        assertEquals(DrawingViewModel.PenOptions.COLOR, vm.penOptionsShownReadOnly.value)

        // Test selecting the currently selected pen options toggles the menu off
        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.COLOR)
        assertEquals(DrawingViewModel.PenOptions.NONE, vm.penOptionsShownReadOnly.value)
    }

}