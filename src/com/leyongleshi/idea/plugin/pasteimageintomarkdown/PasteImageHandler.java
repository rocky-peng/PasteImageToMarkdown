package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Producer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author pengqingsong
 * @date 2020/1/7
 * @desc
 */
public class PasteImageHandler extends EditorActionHandler implements com.intellij.openapi.editor.actionSystem.EditorTextInsertHandler {
    private final EditorActionHandler myOriginalHandler;

    public PasteImageHandler(EditorActionHandler originalAction) {
        myOriginalHandler = originalAction;
    }

    private AnActionEvent createAnEvent(AnAction action, @NotNull DataContext context) {
        Presentation presentation = action.getTemplatePresentation().clone();
        return new AnActionEvent(null, context, ActionPlaces.UNKNOWN, presentation, ActionManager.getInstance(), 0);
    }

    @Override
    public void doExecute(final Editor editor, Caret caret, final DataContext dataContext) {
        if (editor instanceof EditorEx) {
            VirtualFile virtualFile = ((EditorEx) editor).getVirtualFile();
            if (virtualFile != null) {
                FileType fileType = virtualFile.getFileType();
                if ("Markdown".equals(fileType.getName())) {
                    Image imageFromClipboard = ImageUtils.getImageFromClipboard();
                    if (imageFromClipboard != null) {
                        assert caret == null : "Invocation of 'paste' operation for specific caret is not supported";
                        PasteImageFromClipboard action = new PasteImageFromClipboard();
                        AnActionEvent event = createAnEvent(action, dataContext);
                        action.actionPerformed(event);
                        return;
                    }
                }
            }
        }

        if (myOriginalHandler != null) {
            myOriginalHandler.execute(editor, caret, dataContext);
        }
    }

    @Override
    public void execute(Editor editor, DataContext dataContext, Producer<Transferable> producer) {

    }
}
