package org.pilov.skripton.settings

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.util.IconLoader
import org.pilov.skripton.TransformTextAction

@Service
class DynamicActionManager {
    init {
        registerActions()
    }

    companion object {
        fun getInstance(): DynamicActionManager {
            return service()
        }
    }
    fun registerActions() {
        val actionManager = ActionManager.getInstance()
        val scripts = SkriptonSettings.getInstance().scripts

        val mainGroup = actionManager.getAction("ToolsMenu") as? DefaultActionGroup
        println(if (mainGroup == null) "mainGroup not found" else "mainGroup found")

        if (mainGroup == null) {
            throw IllegalArgumentException("Main group can not be null")
        }

        val subGroupId = "Skripton.Group"
        var subGroup = actionManager.getAction(subGroupId) as? DefaultActionGroup
        if (subGroup == null) {
            subGroup = DefaultActionGroup()
            actionManager.registerAction(subGroupId, subGroup)
        } else {
            mainGroup.remove(subGroup)
        }
        subGroup.templatePresentation.icon = IconLoader.getIcon("META-INF/pluginIcon16.svg", this::class.java)
        subGroup.removeAll()

        scripts.forEach { script ->
            val actionId = "Skripton.${script.id}"

            if (actionManager.getAction(actionId) == null) {
                val action = TransformTextAction(script)
                actionManager.registerAction(actionId, action)
            }

            subGroup.addAction(actionManager.getAction(actionId))
        }

        mainGroup.addAction(subGroup)
    }
}
