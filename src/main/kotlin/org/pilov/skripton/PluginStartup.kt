package org.pilov.skripton

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.pilov.skripton.settings.DynamicActionManager

class PluginStartup : ProjectActivity {
    override suspend fun execute(project: Project) {
        DynamicActionManager.getInstance().registerActions()
    }
}
