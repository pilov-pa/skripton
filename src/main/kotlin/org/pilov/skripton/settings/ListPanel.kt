package org.pilov.skripton.settings

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*
import javax.swing.event.ListSelectionEvent

class ListPanel(scriptListModel: DefaultListModel<SkriptonSettings.ScriptData>): JPanel(BorderLayout()) {

    val scriptList = JBList(scriptListModel).apply {
        cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
                if (value is SkriptonSettings.ScriptData) {
                    renderer.text = "${value.id}: ${value.name}"
                }
                return renderer
            }
        }
    }

    var onAddScript: (() -> Unit)? = null
    var onRemoveScript: ((index: Int) -> Unit)? = null
    var onSelectScript: ((index: Int) -> Unit)? = null

    init {
        scriptList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        scriptList.addListSelectionListener { e: ListSelectionEvent ->
            if (!e.valueIsAdjusting) {
                onSelectScript?.invoke(scriptList.selectedIndex)
            }
        }

        val decorator = ToolbarDecorator.createDecorator(scriptList)
            .setAddAction {
                onAddScript?.invoke()
            }
            .setRemoveAction {
                val index = scriptList.selectedIndex
                if (index >= 0) {
                    onRemoveScript?.invoke(index)
                }
            }

        add(decorator.createPanel(), BorderLayout.CENTER)
    }
}
