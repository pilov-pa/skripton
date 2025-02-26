package org.pilov.skripton.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Shortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.KeymapManagerListener
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.keymap.impl.ui.KeymapPanel
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.ActionLink
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ShortcutPanel : JPanel(BorderLayout()), Disposable {
    var actionId: String? = null
        set(value) {
            field = value
            updateShortcutDisplay()
        }
    private val actionManager = ActionManager.getInstance()
    private val keymap: Keymap = KeymapManager.getInstance().activeKeymap
    private val keymapLink: ActionLink = ActionLink("Not set") {
        goToKeymap()
    }
    private val keymapLinkNotSaved: JLabel = JLabel("Script is not saved")

    override fun dispose() {}

    init {
        val messageBus = ApplicationManager.getApplication().messageBus
            .connect(this)

        messageBus.subscribe(KeymapManagerListener.TOPIC, object : KeymapManagerListener {
            override fun activeKeymapChanged(keymap: Keymap?) {
                updateShortcutDisplay()
            }
        })

        add(JLabel("Shortcut", SwingConstants.RIGHT), BorderLayout.NORTH)
        add(keymapLink, BorderLayout.CENTER)
        add(keymapLinkNotSaved, BorderLayout.SOUTH)
    }

    fun updateShortcutDisplay() {
        if (actionManager.getAction(actionId ?: "") == null) {
            keymapLink.isVisible = false
            keymapLinkNotSaved.isVisible = true
            return
        }
        keymapLink.isVisible = true
        keymapLinkNotSaved.isVisible = false
        val shortcuts: Array<Shortcut> = keymap.getShortcuts(actionId)
        val shortcutText = if (shortcuts.isNotEmpty()) {
            KeymapUtil.getShortcutText(shortcuts[0])
        } else {
            "Not set"
        }
        keymapLink.text = shortcutText
    }

    private fun goToKeymap() {
        val keymapPanel = KeymapPanel()
        val selectAction = Runnable { keymapPanel.selectAction(actionId) }
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        ShowSettingsUtil.getInstance().editConfigurable(project, keymapPanel, selectAction)
    }
}