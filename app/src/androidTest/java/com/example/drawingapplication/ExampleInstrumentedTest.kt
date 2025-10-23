package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapplication.data.DrawingDatabase
import com.example.drawingapplication.data.DrawingRepository

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import androidx.compose.ui.graphics.Color


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var vm : DrawingViewModel

    @Before
    fun setup(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = DrawingDatabase.getDatabase(context)
        val repo = DrawingRepository(db.drawingDao())
        vm = DrawingViewModel(repo)
    }

    @Test
    fun changeColorCorrectly(){
        assertEquals(Color.Black, vm.penColorReadOnly.value)
        vm.changePenColor(Color.Blue)
        assertEquals(Color.Blue, vm.penColorReadOnly.value)
    }

    @Test
    fun changeSizeCorrectly() {
        assertEquals(8f, vm.penSizeReadOnly.value)
        vm.changePenSize(20f)
        assertEquals(20f, vm.penSizeReadOnly.value)
    }

    @Test
    fun changeShapeCorrectly() {
        assertEquals("circle", vm.penShapeReadOnly.value)
        vm.changePenShape("rectangle")
        assertEquals("rectangle", vm.penShapeReadOnly.value)
    }

    @Test
    fun togglePenOptionsCorrectly() {
        assertEquals(DrawingViewModel.PenOptions.NONE, vm.penOptionsShownReadOnly.value)
        vm.changeOrTogglePenOptions(DrawingViewModel.PenOptions.SHAPE)
        assertEquals(DrawingViewModel.PenOptions.SHAPE, vm.penOptionsShownReadOnly.value)
    }

    @Test
    fun startStrokeCreatesNewStroke() {
        vm.startStoke(Offset(10f, 20f))
        assertEquals(1, vm.strokesReadOnly.value.size)
    }

    @Test
    fun addToStrokeAddsPoints() {
        vm.startStoke(Offset(10f, 20f))
        vm.addToStroke(Offset(15f, 25f))
        assertEquals(2, vm.strokesReadOnly.value[0].size)
    }



    /**
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.drawingapplication", appContext.packageName)
    }*/
}