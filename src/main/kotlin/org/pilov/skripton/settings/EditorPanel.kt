package org.pilov.skripton.settings

import com.intellij.openapi.Disposable
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class EditorPanel : JPanel(BorderLayout()), Disposable {

    private var id: Int? = null
    var isLoading = false

    private val shortcutPanel = ShortcutPanel()
    val nameField = JBTextField(20)
    val codeField = JBTextArea()

    private val actionId get() = "Skripton.${id}"

    var onChange: (() -> Unit)? = null

    override fun dispose() {}

    init {
        border = IdeBorderFactory.createEmptyBorder(JBUI.insets(8))

        val topPanel = JPanel(BorderLayout()).apply {
            add(createNamePanel(), BorderLayout.WEST)
            add(shortcutPanel, BorderLayout.EAST)
        }

        val documentListener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                if (!isLoading) onChange?.invoke()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                if (!isLoading) onChange?.invoke()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                if (!isLoading) onChange?.invoke()
            }
        }

        nameField.document.addDocumentListener(documentListener)
        codeField.document.addDocumentListener(documentListener)

        val centerPanel = JPanel(BorderLayout()).apply {
            border = IdeBorderFactory.createTitledBorder("Script", false)
            add(JScrollPane(codeField), BorderLayout.CENTER)
        }

        add(topPanel, BorderLayout.NORTH)
        add(centerPanel, BorderLayout.CENTER)
    }

    private fun createNamePanel(): JPanel {
        val namePanel = JPanel(BorderLayout())
        namePanel.add(JLabel("Script Name"), BorderLayout.NORTH)
        namePanel.add(nameField, BorderLayout.CENTER)
        return namePanel
    }

    fun loadScript(data: SkriptonSettings.ScriptData) {
        isLoading = true
        try {
            if (nameField.text != data.name)
                nameField.text = data.name
            if (codeField.text != data.content)
                codeField.text = data.content
            if (id != data.id)
                id = data.id
        } finally {
            isLoading = false
        }
        shortcutPanel.actionId = actionId
    }

    fun updateShortcutDisplay() {
        shortcutPanel.updateShortcutDisplay()
    }
}
