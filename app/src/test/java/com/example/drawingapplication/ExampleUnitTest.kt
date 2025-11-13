package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.drawingapplication.data.DrawingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class DrawingApplicationUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var vm: DrawingViewModel
    private lateinit var mockRepository: DrawingRepository

    @Before
    fun setup() {
        mockRepository = mock(DrawingRepository::class.java)
        `when`(mockRepository.allDrawings).thenReturn(flowOf(emptyList()))
        vm = DrawingViewModel(mockRepository)
    }

    @Test
    fun changeColorCorrectly() {
        // Test initial color is set correctly
        assertEquals(Color.Black, vm.penColorReadOnly.value)

        // Test changing color updates correctly
        vm.changePenColor(Color.Blue)
        assertEquals(Color.Blue, vm.penColorReadOnly.value)
    }

    @Test
    fun changeSizeCorrectly() {
        // Test initial size is set correctly
        assertEquals(8f, vm.penSizeReadOnly.value)

        // Test changing size updates correctly
        vm.changePenSize(20f)
        assertEquals(20f, vm.penSizeReadOnly.value)
    }

    @Test
    fun changeShapeCorrectly() {
        // Test initial shape is set correctly
        assertEquals("circle", vm.penShapeReadOnly.value)

        // Test changing shape updates correctly
        vm.changePenShape("rectangle")
        assertEquals("rectangle", vm.penShapeReadOnly.value)
    }

    @Test
    fun toggleAndChangePenOptionsCorrectly() {
        // Test that no options are shown initially
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

    @Test
    fun startStroke_addsToCurrentStrokeAndStrokes() {
        assertEquals(emptyList<List<DrawingPoint>>(), vm.strokesReadOnly.value)
        assertEquals(emptyList<DrawingPoint>(), vm.currentStrokeReadOnly.value)

        val offset = Offset(10f, 10f)
        vm.startStoke(offset)

        assertEquals(1, vm.strokesReadOnly.value.size)
        assertEquals(1, vm.strokesReadOnly.value[0].size)
        assertEquals(offset, vm.strokesReadOnly.value[0][0].offset)

        assertEquals(1, vm.currentStrokeReadOnly.value.size)
        assertEquals(offset, vm.currentStrokeReadOnly.value[0].offset)
    }

    @Test
    fun addToStroke_addsPointToCurrentStroke() {
        val startOffset = Offset(10f, 10f)
        vm.startStoke(startOffset)

        val secondOffset = Offset(20f, 20f)
        vm.addToStroke(secondOffset)

        assertEquals(1, vm.strokesReadOnly.value.size)
        assertEquals(2, vm.strokesReadOnly.value[0].size)
        assertEquals(secondOffset, vm.strokesReadOnly.value[0][1].offset)

        assertEquals(2, vm.currentStrokeReadOnly.value.size)
        assertEquals(secondOffset, vm.currentStrokeReadOnly.value[1].offset)
    }

    @Test
    fun endStroke_clearsCurrentStroke() {
        val offset = Offset(10f, 10f)
        vm.startStoke(offset)
        assertNotEquals(emptyList<DrawingPoint>(), vm.currentStrokeReadOnly.value)

        vm.endStroke()
        assertEquals(emptyList<DrawingPoint>(), vm.currentStrokeReadOnly.value)
        // strokes should remain
        assertEquals(1, vm.strokesReadOnly.value.size)
    }

}