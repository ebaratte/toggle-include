package com.etienne.toggleinclude;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;


public class ConvertInclude extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                String selectedText = ConvertUtils.getCurrentLine(editor);
                String result = ConvertUtils.convert(selectedText);
                if (!result.equals(selectedText)) {
                    ConvertUtils.replaceCurrentLine(editor, result);
                }
            }
        });
    }
}
