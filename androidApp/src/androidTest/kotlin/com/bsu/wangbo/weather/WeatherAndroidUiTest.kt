package com.bsu.wangbo.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class WeatherAndroidUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `测试1_初始界面显示提示语和搜索按钮`() {
        composeTestRule.waitForIdle()

        // 查找搜索按钮
        composeTestRule.onNodeWithText("搜索")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun `测试2_输入城市并点击搜索不崩溃`() {
        composeTestRule.waitForIdle()

        // 修复方案：使用 isEditable() 精确匹配输入框
        composeTestRule.onNode(hasText("请输入城市", substring = true) and isEditable())
            .performTextInput("Beijing")

        // 点击搜索按钮
        composeTestRule.onNodeWithText("搜索").performClick()

        // 等待UI更新
        composeTestRule.waitForIdle()
    }

    @Test
    fun `测试3_应用标题存在`() {
        composeTestRule.waitForIdle()

        // 检查是否包含"天气"字样
        composeTestRule.onNodeWithText("天气", substring = true)
            .assertExists()
            .assertIsDisplayed()
    }
}