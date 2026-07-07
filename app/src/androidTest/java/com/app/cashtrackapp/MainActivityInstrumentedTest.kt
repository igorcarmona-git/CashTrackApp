package com.app.cashtrackapp

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchApp_displaysEntryFormAfterSplash() {
        waitForEntryForm()

        composeRule.onNodeWithText("Tipo").assertIsDisplayed()
        composeRule.onNodeWithText("Valor (R$)").assertIsDisplayed()
        composeRule.onNodeWithText("Descrição").assertIsDisplayed()
        composeRule.onNodeWithText("Data Lançamento").assertIsDisplayed()
        composeRule.onNodeWithText("Lançar").assertIsDisplayed()
        composeRule.onNodeWithTag("type_credit").assertIsSelected()
        composeRule.onNodeWithTag("type_debit").assertIsNotSelected()
    }

    @Test
    fun entryForm_selectsDebitType() {
        waitForEntryForm()

        composeRule.onNodeWithTag("type_debit").performClick()

        composeRule.onNodeWithTag("type_credit").assertIsNotSelected()
        composeRule.onNodeWithTag("type_debit").assertIsSelected()
    }

    @Test
    fun entryForm_formatsValueAsBrlWhileTyping() {
        waitForEntryForm()

        composeRule.onNodeWithTag("entry_value").performTextInput("25")

        composeRule.onNodeWithTag("entry_value")
            .assert(hasStateDescription("R$ 25,00"))
    }

    @Test
    fun entryForm_clearButtonResetsValueDescriptionAndType() {
        waitForEntryForm()

        composeRule.onNodeWithTag("type_debit").performClick()
        composeRule.onNodeWithTag("entry_value").performTextInput("25")
        composeRule.onNodeWithTag("entry_description").performTextInput("Mercado")
        composeRule.onNodeWithTag("clear_entry").performClick()

        composeRule.onNodeWithTag("type_credit").assertIsSelected()
        composeRule.onNodeWithTag("type_debit").assertIsNotSelected()
        composeRule.onNodeWithTag("entry_value").assert(hasStateDescription(""))
        composeRule.onNodeWithTag("entry_description").assert(hasEditableText(""))
    }

    @Test
    fun submitWithoutValue_showsValidationError() {
        waitForEntryForm()

        composeRule.onNodeWithTag("entry_description").performTextInput("Mercado")
        composeRule.onNodeWithTag("submit_entry").performClick()

        waitForText("Valor deve ser maior que 0")
        composeRule.onNodeWithText("Valor deve ser maior que 0").assertIsDisplayed()
    }

    @Test
    fun transactionsNavigation_opensListAndReturnsToEntryForm() {
        waitForEntryForm()

        composeRule.onNodeWithTag("show_transactions").performClick()

        waitForText("Lançamentos")
        composeRule.onNodeWithText("Lançamentos").assertIsDisplayed()
        composeRule.onNodeWithText("Voltar").performClick()

        waitForEntryForm()
        composeRule.onNodeWithText("Lançar").assertIsDisplayed()
    }

    private fun waitForEntryForm() {
        waitForText("Lançar")
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun hasStateDescription(expectedValue: String): SemanticsMatcher {
        return SemanticsMatcher.expectValue(
            SemanticsProperties.StateDescription,
            expectedValue
        )
    }

    private fun hasEditableText(expectedValue: String): SemanticsMatcher {
        return SemanticsMatcher.expectValue(
            SemanticsProperties.EditableText,
            AnnotatedString(expectedValue)
        )
    }
}
