package org.pilov.skripton

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.command.WriteCommandAction
import org.pilov.skripton.settings.SkriptonSettings
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TransformTextAction(private val scriptData: SkriptonSettings.ScriptData) : AnAction(scriptData.name) {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel

        if (!selectionModel.hasSelection()) return

        val selectedText = selectionModel.selectedText ?: return
        val content = SkriptonSettings.getInstance().scripts[scriptData.id ?: 0].content

        val transformedText = runBashScript(content, selectedText)

        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd

        WriteCommandAction.runWriteCommandAction(event.project) {
            document.replaceString(start, end, transformedText)
        }
    }

    private fun runBashScript(script: String, input: String): String {
        val process = ProcessBuilder("bash", "-c", script)
            .redirectErrorStream(true)
            .start()

        BufferedWriter(OutputStreamWriter(process.outputStream)).use { writer ->
            writer.write(input)
            writer.flush()
        }

        return InputStreamReader(process.inputStream).readText().trim()
    }
}
