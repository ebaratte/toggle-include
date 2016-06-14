package com.etienne.toggleinclude;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConvertUtils {

    private static final Pattern QUOTES = Pattern.compile("(\\s*#\\s*include\\s*)\"([^\"]*)\"(.*)$", Pattern.DOTALL | Pattern.MULTILINE);
    private static final Pattern ANGLES = Pattern.compile("^(\\s*#\\s*include\\s*)<([^>]*)>(.*)$", Pattern.DOTALL | Pattern.MULTILINE);

    static String getCurrentLine(Editor editor) {
        if (editor == null)
            return "";

        CaretModel cm = editor.getCaretModel();


        return editor.getDocument().getText(TextRange.create(
                cm.getVisualLineStart(), cm.getVisualLineEnd())
        );
    }

    static void replaceCurrentLine(Editor editor, String text) {
        SelectionModel selectionModel = editor.getSelectionModel();
        selectionModel.selectLineAtCaret();
        editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), text);
        selectionModel.removeSelection();
    }

    static boolean angles(String text) {
        return ANGLES.matcher(text).matches();
    }
    static boolean quotes(String text) {
        return QUOTES.matcher(text).matches();
    }
    static boolean matches(String text) {
        return quotes(text) || angles(text);
    }

    static String headerName(String text) {
        Matcher m = QUOTES.matcher(text);
        if (m.matches()) {
            return m.group(2);
        } else {
            m = ANGLES.matcher(text);
            if (m.matches()) {
                return m.group(2);
            }
        }
        return "";
    }

    static String convert(String text) {
        Matcher m = QUOTES.matcher(text);
        if (m.matches()) {
            return m.group(1) + "<" + m.group(2) + ">" + m.group(3);
        } else {
            m = ANGLES.matcher(text);
            if (m.matches()) {
                return m.group(1) + "\"" + m.group(2) + "\"" + m.group(3);
            }
        }
        return text;
    }

    static String toAngles(String text) {
        Matcher m = QUOTES.matcher(text);
        if (m.matches()) {
            return m.group(1) + "<" + m.group(2) + ">" + m.group(3);
        }
        return text;
    }
    static String toQuotes(String text) {
        Matcher m = ANGLES.matcher(text);
        if (m.matches()) {
            return m.group(1) + "\"" + m.group(2) + "\"" + m.group(3);
        }
        return text;
    }

}
