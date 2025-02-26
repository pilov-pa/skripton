package org.pilov.skripton.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.OnePixelSplitter
import javax.swing.*
import java.awt.*

class SkriptonConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private val editorPanel = EditorPanel()
    private val scriptListModel = DefaultListModel<SkriptonSettings.ScriptData>()
    private val listPanel = ListPanel(scriptListModel)
    private var originalScripts = mutableListOf<SkriptonSettings.ScriptData>()
    private var modified = false // Флаг изменений

    override fun getDisplayName(): String = "Skripton Settings"

    override fun createComponent(): JComponent {
        if (settingsPanel == null) {
            settingsPanel = JPanel(BorderLayout())

            // Левая панель (список скриптов + кнопки)
            val leftPanel = JPanel(BorderLayout())
            listPanel.onSelectScript = { script -> loadSelectedScript(script) }
            listPanel.onAddScript = { addScript() }
            listPanel.onRemoveScript = { script -> removeScript(script) }

            leftPanel.add(listPanel, BorderLayout.CENTER)

            editorPanel.onChange = { updateScript(); }

            val rightPanel = JPanel(BorderLayout())
            editorPanel.isVisible = false

            rightPanel.add(editorPanel, BorderLayout.CENTER)

            settingsPanel = OnePixelSplitter(false, "MyPanelSplitter", 0.3f).apply {
                firstComponent = leftPanel
                secondComponent = rightPanel
            }
        }
        loadScripts()
        return settingsPanel!!
    }

    private fun updateScript() {
        val selectedIndex = listPanel.scriptList.selectedIndex
        if (selectedIndex >= 0) {
            val newScriptCode = editorPanel.codeField.text.trim()
            val newName = editorPanel.nameField.text.trim()
            val originalScript = originalScripts[selectedIndex]
            if (originalScript.name == newName && originalScript.content == newScriptCode) {
                return
            }
            originalScripts[selectedIndex] = SkriptonSettings.ScriptData(selectedIndex, newName, newScriptCode)
            scriptListModel.setElementAt(originalScripts[selectedIndex], selectedIndex)
            markModified()
        }
    }

    override fun isModified(): Boolean {
        return modified
    }

    override fun apply() {
        saveChanges()
        DynamicActionManager.getInstance().registerActions()
        editorPanel.updateShortcutDisplay()
    }

    override fun reset() {
        loadScripts()
        modified = false
    }

    private fun loadScripts() {
        scriptListModel.clear()
        originalScripts = SkriptonSettings.getInstance().scripts.map { it.copy() }.toMutableList()
        originalScripts.forEach { scriptListModel.addElement(it) }
        if (scriptListModel.size() > 0) listPanel.scriptList.selectedIndex = 0
        modified = false
    }

    private fun loadSelectedScript(index: Int) {
        if (index < 0) {
            editorPanel.isVisible = false
            return
        }
        editorPanel.isVisible = true
        editorPanel.loadScript(originalScripts[index])
    }

    private fun markModified() {
        modified = true
    }

    private fun saveChanges() {
        val currentScripts = mutableListOf<SkriptonSettings.ScriptData>()
        for (i in 0 until scriptListModel.size()) {
            val name = if (i == listPanel.scriptList.selectedIndex) editorPanel.nameField.text else originalScripts[i].name
            val content = if (i == listPanel.scriptList.selectedIndex) editorPanel.codeField.text else originalScripts[i].content
            currentScripts.add(SkriptonSettings.ScriptData(i, name, content))
        }
        SkriptonSettings.getInstance().scripts = currentScripts
        originalScripts = currentScripts.map { it.copy() }.toMutableList()
        modified = false
    }

    private fun addScript() {
        val id = if (listPanel.scriptList.selectedIndex < 0) 0 else {
            originalScripts.maxOf { it.id ?: 0 } + 1
        }
        val newScript = SkriptonSettings.ScriptData(id)
        originalScripts.add(newScript)
        scriptListModel.addElement(newScript)
        listPanel.scriptList.selectedIndex = scriptListModel.size() - 1
        markModified()
    }

    private fun removeScript(selectedIndex: Int) {
        if (selectedIndex >= 0) {
            originalScripts.removeAt(selectedIndex)
            scriptListModel.remove(selectedIndex)
            if (scriptListModel.size() > 0) {
                if (selectedIndex > 0) {
                    listPanel.scriptList.selectedIndex = selectedIndex - 1
                } else {
                    listPanel.scriptList.selectedIndex = 0
                }
            } else {
                editorPanel.isVisible = false
            }
            markModified()
        }
    }
}
