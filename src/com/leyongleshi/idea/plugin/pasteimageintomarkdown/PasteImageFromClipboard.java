package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;

import static com.leyongleshi.idea.plugin.pasteimageintomarkdown.ImageUtils.getImageFromClipboard;
import static com.leyongleshi.idea.plugin.pasteimageintomarkdown.ImageUtils.toBufferedImage;


public class PasteImageFromClipboard extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Editor ed = e.getData(PlatformDataKeys.EDITOR);
        if (ed == null) {
            return;
        }

        Image imageFromClipboard = getImageFromClipboard();
        if (imageFromClipboard == null) {
            return;
        }
        BufferedImage bufferedImage = toBufferedImage(imageFromClipboard);
        if (bufferedImage == null) {
            return;
        }

        //读取存储路径
        String imageSaveLocationValue = PropertiesComponent.getInstance().getValue(Constants.IMAGE_SAVE_LOCATION);
        if (imageSaveLocationValue == null || imageSaveLocationValue.trim().length() == 0) {
            throw new RuntimeException("please fill infos in settings");
        }

        if (!"LOCAL".equalsIgnoreCase(imageSaveLocationValue) && !"QINIU".equalsIgnoreCase(imageSaveLocationValue)) {
            throw new RuntimeException("not support " + imageSaveLocationValue.toLowerCase());
        }

        if ("LOCAL".equalsIgnoreCase(imageSaveLocationValue)) {
            String localRelativeDirPath = PropertiesComponent.getInstance().getValue(Constants.LOCAL_RELATIVE_DIR_PATH);
            if (isEmpty(localRelativeDirPath)) {
                throw new RuntimeException("please set RELATIVE_DIRECTORY_PATH in settings");
            }

            //save image to disk
            @SystemIndependent String projectFilePath = ed.getProject().getBasePath();
            File imageSaveDir = new File(projectFilePath, localRelativeDirPath);
            if (!imageSaveDir.exists() || !imageSaveDir.isDirectory()) {
                if (!imageSaveDir.mkdirs()) {
                    throw new RuntimeException("cannot mkdir:" + imageSaveDir.getAbsolutePath());
                }
            }
            File imgFile = new File(imageSaveDir, System.nanoTime() + ".png");
            ImageUtils.saveImage(bufferedImage, imgFile);

            //refresh
            VirtualFile fileByPath = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(imgFile);
            assert fileByPath != null;

            //add file to vcs
            AbstractVcs usedVcs = ProjectLevelVcsManager.getInstance(ed.getProject()).getVcsFor(fileByPath);
            if (usedVcs != null && usedVcs.getCheckinEnvironment() != null) {
                usedVcs.getCheckinEnvironment().scheduleUnversionedFilesForAddition(Collections.singletonList(fileByPath));
            }

            //calc the relative path between current editor file and imgFile
            Document currentDoc = FileEditorManager.getInstance(ed.getProject()).getSelectedTextEditor().getDocument();
            VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);
            File curDocument = new File(currentFile.getPath());
            String imgRelativeUrl = curDocument.getParentFile().toPath().relativize(imgFile.toPath()).toFile().toString().replace('\\', '/');

            //insert img url to md file
            insertImageElement(ed, imgRelativeUrl);
            return;
        }

        if ("QINIU".equalsIgnoreCase(imageSaveLocationValue)) {
            String qiniuImgUrlPrefix = PropertiesComponent.getInstance().getValue(Constants.QINIU_IMG_URL_PREFIX);
            String qiniuBucketName = PropertiesComponent.getInstance().getValue(Constants.QINIU_BUCKET_NAME);
            String qiniuAccessKey = PropertiesComponent.getInstance().getValue(Constants.QINIU_ACCESS_KEY);
            String qiniuSecretKey = PropertiesComponent.getInstance().getValue(Constants.QINIU_SECRET_KEY);

            if (isEmpty(qiniuImgUrlPrefix)) {
                throw new RuntimeException("please set IMG_URL_PREFIX in settings");
            }
            if (isEmpty(qiniuBucketName)) {
                throw new RuntimeException("please set BUCKET_NAME in settings");
            }
            if (isEmpty(qiniuAccessKey)) {
                throw new RuntimeException("please set ACCESS_KEY in settings");
            }
            if (isEmpty(qiniuSecretKey)) {
                throw new RuntimeException("please set SECRET_KEY in settings");
            }

            if (!qiniuImgUrlPrefix.endsWith("/")) {
                qiniuImgUrlPrefix += "/";
            }
            QiniuHelper qiniuHelper = new QiniuHelper(qiniuAccessKey, qiniuSecretKey, qiniuBucketName, qiniuImgUrlPrefix, 3);
            String imgUrl = qiniuHelper.upload(bufferedImage, "markdown/" + System.nanoTime() + ".png");
            insertImageElement(ed, imgUrl);
        }
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
