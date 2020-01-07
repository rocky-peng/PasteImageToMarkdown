package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.leyongleshi.idea.plugin.pasteimageintomarkdown.ImageUtils.getImageFromClipboard;
import static com.leyongleshi.idea.plugin.pasteimageintomarkdown.ImageUtils.toBufferedImage;


public class PasteImageFromClipboard extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor ed = e.getData(PlatformDataKeys.EDITOR);
        if (ed == null) {
            return;
        }

        if (!(ed instanceof EditorEx)) {
            return;
        }

        VirtualFile virtualFile = ((EditorEx) ed).getVirtualFile();
        if (virtualFile == null) {
            return;
        }

        FileType fileType = virtualFile.getFileType();
        if (!"Markdown".equals(fileType.getName())) {
            return;
        }
        Image imageFromClipboard = getImageFromClipboard();
        if (imageFromClipboard == null) {
            DialogBuilder builder = new DialogBuilder();
            builder.setCenterPanel(new JLabel("Clipboard does not contain any image"));
            builder.setDimensionServiceKey("PasteImageFromClipboard.NoImage");
            builder.setTitle("No image in Clipboard");
            builder.removeAllActions();
            builder.addOkAction();
            builder.show();
            return;
        }

        BufferedImage bufferedImage = toBufferedImage(imageFromClipboard);
        if (bufferedImage == null) {
            return;
        }
        String qiniuImgUrlPrefix = PropertiesComponent.getInstance().getValue("qiniu_img_url_prefix");
        String qiniuBucketName = PropertiesComponent.getInstance().getValue("qiniu_bucket_name");
        String qiniuAccessKey = PropertiesComponent.getInstance().getValue("qiniu_access_key");
        String qiniuSecretKey = PropertiesComponent.getInstance().getValue("qiniu_secret_key");

        if (isEmpty(qiniuImgUrlPrefix) ) {
            throw new RuntimeException("请前往设置中填写七牛相关信息: URL_PREFIX");
        }
        if(isEmpty(qiniuBucketName) ){
            throw new RuntimeException("请前往设置中填写七牛相关信息：BUCKET_NAME");
        }
        if( isEmpty(qiniuAccessKey) ){
            throw new RuntimeException("请前往设置中填写七牛相关信息：ACCESS_KEY");
        }
        if(isEmpty(qiniuSecretKey)){
            throw new RuntimeException("请前往设置中填写七牛相关信息：SECRET_KEY");
        }

        if (!qiniuImgUrlPrefix.endsWith("/")) {
            qiniuImgUrlPrefix += "/";
        }
        QiniuHelper qiniuHelper = new QiniuHelper(qiniuAccessKey, qiniuSecretKey, qiniuBucketName, qiniuImgUrlPrefix, 3);
        String imgUrl = qiniuHelper.upload(bufferedImage, "markdown/" + System.nanoTime() + ".png");
        insertImageElement(ed, imgUrl);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    private void insertImageElement(final @NotNull Editor editor, String imageurl) {
        String picUrl = "![](" + imageurl + ")";
        Runnable r = () -> EditorModificationUtil.insertStringAtCaret(editor, picUrl);
        WriteCommandAction.runWriteCommandAction(editor.getProject(), r);
    }
}
