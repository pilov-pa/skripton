package org.pilov.skripton.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.Service
import com.intellij.openapi.application.ApplicationManager

@Service
@State(name = "SkriptonSettings", storages = [Storage("SkriptonSettings.xml")])
class SkriptonSettings : PersistentStateComponent<SkriptonSettings.State> {

    data class ScriptData(var id: Int? = null, var name: String = "NewScript", var content: String = "#!/bin/bash\necho 'Hello'")

    data class State(var scripts: MutableList<ScriptData> = mutableListOf())

    private var myState: State = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var scripts: MutableList<ScriptData>
        get() = myState.scripts
        set(value) {
            myState.scripts = value
        }

    companion object {
        fun getInstance(): SkriptonSettings {
            return ApplicationManager.getApplication().getService(SkriptonSettings::class.java)
        }
    }
}
