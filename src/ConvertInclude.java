import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertInclude extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        final Logger log = Logger.getInstance("ConvertInclude");


        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                SelectionModel selectionModel = editor.getSelectionModel();
                selectionModel.selectLineAtCaret();
                String selectedText = selectionModel.getSelectedText();
                String result = selectedText;

                log.debug("Running convert include action on " + selectedText);
                Pattern quotes = Pattern.compile("(\\s*#\\s*include\\s*)\"([^\"]*)\"(.*)$", Pattern.DOTALL | Pattern.MULTILINE);
                Matcher m = quotes.matcher(selectedText);
                if (m.matches()) {
                    log.debug("Matches double quotes");
                    result = m.group(1) + "<" + m.group(2) + ">" + m.group(3);

                } else {
                    Pattern angles = Pattern.compile("^(\\s*#\\s*include\\s*)<([^>]*)>(.*)$", Pattern.DOTALL | Pattern.MULTILINE);
                    m = angles.matcher(selectedText);
                    if (m.matches()) {
                        log.debug("Matches angles");
                        result = m.group(1) + "\"" + m.group(2) + "\"" + m.group(3);
                    }
                }
                if (!result.equals(selectedText)) {
                    editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), result);
                }
                selectionModel.removeSelection();
            }
        });
    }
}
