package com.murlodin.fca.actions

import FCAGenerator
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.utils.vfs.createFile
import com.murlodin.fca.ui.FeatureGenerateDialog

class FlutterCleanArchitectureAction : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = FeatureGenerateDialog(actionEvent.project)
        if (dialog.showAndGet()) {
            generate(actionEvent.dataContext, dialog.getName(), dialog.splitSource())
        }
    }


    private fun generate(dataContext: DataContext, root: String?, splitSource: Boolean?) {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val selected = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return

        var folder = if (selected.isDirectory) selected else selected.parent
        WriteCommandAction.runWriteCommandAction(project) {
            if (root != null && root.isNotBlank()) {
                val result = FCAGenerator.createFolder(
                    project, folder, root
                ) ?: return@runWriteCommandAction
                folder = result[root]

            }

            FCAGenerator.createDomainFolder(
                project,
                folder,
            )

            FCAGenerator.createPresentationFolder(
                project,
                folder,
            )

            FCAGenerator.createDataFolder(
                project,
                folder,
            )


        }
    }
}