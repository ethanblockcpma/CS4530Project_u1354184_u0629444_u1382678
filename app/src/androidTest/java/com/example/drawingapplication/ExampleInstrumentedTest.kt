package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapplication.data.DrawingDatabase
import com.example.drawingapplication.data.DrawingRepository
import androidx.navigation.testing.TestNavHostController


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import com.example.drawingapplication.view.DrawingCanvas
import com.example.drawingapplication.view.MainScreen
import kotlinx.coroutines.runBlocking
import com.example.drawingapplication.data.Drawing
import com.example.drawingapplication.navigation.AppNavHost
import kotlinx.coroutines.flow.first

import org.junit.After
import org.junit.Rule


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var vm : DrawingViewModel
    private lateinit var db : DrawingDatabase
    private lateinit var repo : DrawingRepository

    private lateinit var navController : TestNavHostController


    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = DrawingDatabase.getDatabase(context)
        repo = DrawingRepository(db.drawingDao())
        vm = DrawingViewModel(repo)

    }

    @After
    fun testsDone(){
        db.clearAllTables()
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

    //* runBlocking from chatGPT
    @Test
    fun insertDrawingDB() = runBlocking {
        val drawing = Drawing(
            title = "test",
            filePath = "/test/path.png"
        )

        repo.insertDrawing(drawing)
        val drawings = repo.allDrawings.first()
        assertEquals(1, drawings.size)
        assertEquals("test", drawings[0].title)

    }

    @Test
    fun insertMultiDrawings() = runBlocking {
        repo.insertDrawing(Drawing(title="test1",filePath="/path1.png"))
        repo.insertDrawing(Drawing(title="test2",filePath="/path2.png"))
        repo.insertDrawing(Drawing(title="test3",filePath="/path3.png"))

        Thread.sleep(200)

        val drawings = repo.allDrawings.first()
        assertEquals(3, drawings.size)

    }

    // ui tests

    @Test
    fun mainScreenDisplaysTitle() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                MainScreen(navController, vm)
            }
        }

        composeTestRule.onNodeWithText("My Drawings").assertIsDisplayed()
    }

    @Test
    fun mainScreenDisplaysButton(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                MainScreen(navController, vm)
            }

        }

        composeTestRule.onNodeWithText("New Drawing").assertIsDisplayed()

    }

    @Test
    fun drawingScreenDisplaysAllPenOption(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }
        }

        composeTestRule.onNodeWithText("Colors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shape").assertIsDisplayed()
        composeTestRule.onNodeWithText("Size").assertIsDisplayed()
    }

    @Test
    fun drawScreenDisplayNavButtons(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }

        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun drawScreenColorMenuNotVisible(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }

        }

        composeTestRule.onAllNodesWithContentDescription("").fetchSemanticsNodes().size
    }

    @Test
    fun drawScreenClickColorMenu(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }

        }

        composeTestRule.onNodeWithText("Colors").performClick()
        composeTestRule.waitForIdle()
    }


    @Test
    fun drawScreenCluckShapeMenu(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }

        }

        composeTestRule.onNodeWithText("Shape").performClick()
        composeTestRule.waitForIdle()
        //shapes now on screen, can assert is displayed
        composeTestRule.onNodeWithText("circle").assertIsDisplayed()
        composeTestRule.onNodeWithText("rectangle").assertIsDisplayed()
        composeTestRule.onNodeWithText("oval").assertIsDisplayed()
    }

    @Test
    fun drawScreenToggleWorks(){
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                DrawingCanvas(navController, vm)
            }

        }

        composeTestRule.onNodeWithText("Shape").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("circle").assertIsDisplayed()
        //click again, confirm shape is no longer there
        composeTestRule.onNodeWithText("Shape").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("circle").assertDoesNotExist()

    }


    // nav test

    @Test
    fun mainToDrawingCanvas() {
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                AppNavHost(navController, vm, startDestination = "main")
            }

        }

        composeTestRule.onNodeWithText("New Drawing").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Drawing").performClick()
        composeTestRule.waitForIdle()

        // after thats clicked, we should be on next screen, able to see 'shape'
        composeTestRule.onNodeWithText("Shape").assertIsDisplayed()
    }

    @Test
    fun drawingCanvasToMain() {
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                AppNavHost(navController, vm, startDestination = "drawing")
            }

        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        // after thats clicked, we should be on next screen, able to see 'shape'
        composeTestRule.onNodeWithText("New Drawing").assertIsDisplayed()
    }

    @Test
    fun splashToMain() {
        composeTestRule.setContent{
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DrawingApplicationTheme {
                AppNavHost(navController, vm, startDestination = "splash")
            }

        }

        composeTestRule.onNodeWithText("Drawing App").assertIsDisplayed()
        composeTestRule.mainClock.advanceTimeBy(2500)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("New Drawing").assertIsDisplayed()
    }



    /**
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.drawingapplication", appContext.packageName)
    }*/
}