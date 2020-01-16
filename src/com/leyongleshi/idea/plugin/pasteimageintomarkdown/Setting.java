package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author pengqingsong
 * @date 2020/1/7
 * @desc
 */
public class Setting implements Configurable {
    private JPanel panel1;
    private JTextField qiniuImgUrlPrefixField;
    private JTextField qiniuAccessKeyField;
    private JTextField qiniuSecretKeyField;
    private JTextField qiniuBucketNameField;
    private JTabbedPane saveImgPanel;
    private JLabel imgSaveLocationLabel;
    private JTextField localRelativeDirPathField;
    private JTextField aliyunAccessKeySecret;
    private JTextField aliyunEndPoint;
    private JTextField aliyunAccessKeyId;
    private JTextField aliyunBucketName;
    private JTextField aliyunFolder;
    private String imageSaveLocation = "LOCAL";

    public Setting() {
        saveImgPanel.addChangeListener(e -> {
            JTabbedPane source = (JTabbedPane) e.getSource();
            int selectedIndex = source.getSelectedIndex();
            onImageSaveLocationChanged(selectedIndex);
        });
    }


    private void onImageSaveLocationChanged(int selectedIndex) {
        if (selectedIndex == 0) {
            imageSaveLocation = "LOCAL";
        } else if (selectedIndex == 1) {
            imageSaveLocation = "QINIU";
        } else {
            imageSaveLocation = "ALIYUN";
        }
        imgSaveLocationLabel.setText(imageSaveLocation);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PasteImagesIntoMarkdown";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel1;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {

        PropertiesComponent.getInstance().setValue(Constants.IMAGE_SAVE_LOCATION, imageSaveLocation);

        //local
        PropertiesComponent.getInstance().setValue(Constants.LOCAL_RELATIVE_DIR_PATH, localRelativeDirPathField.getText());

        //qiniu
        PropertiesComponent.getInstance().setValue(Constants.QINIU_ACCESS_KEY, qiniuAccessKeyField.getText());
        PropertiesComponent.getInstance().setValue(Constants.QINIU_SECRET_KEY, qiniuSecretKeyField.getText());
        PropertiesComponent.getInstance().setValue(Constants.QINIU_IMG_URL_PREFIX, qiniuImgUrlPrefixField.getText());
        PropertiesComponent.getInstance().setValue(Constants.QINIU_BUCKET_NAME, qiniuBucketNameField.getText());

        //aliyun
        PropertiesComponent.getInstance().setValue(Constants.ALIYUN_ACCESS_KEY_SECRET, aliyunAccessKeySecret.getText());
        PropertiesComponent.getInstance().setValue(Constants.ALIYUN_END_POINT, aliyunEndPoint.getText());
        PropertiesComponent.getInstance().setValue(Constants.ALIYUN_ACCESS_KEY_ID, aliyunAccessKeyId.getText());
        PropertiesComponent.getInstance().setValue(Constants.ALIYUN_BUCKET_NAME, aliyunBucketName.getText());
        PropertiesComponent.getInstance().setValue(Constants.ALIYUN_FOLDER, aliyunFolder.getText());
    }


    @Override
    public void reset() {
        String imageSaveLocationValue = PropertiesComponent.getInstance().getValue(Constants.IMAGE_SAVE_LOCATION);
        if (imageSaveLocationValue == null || imageSaveLocationValue.trim().length() == 0) {
            imageSaveLocation = "LOCAL";
        } else {
            imageSaveLocation = imageSaveLocationValue;
        }

        if ("LOCAL".equalsIgnoreCase(imageSaveLocation)) {
            saveImgPanel.setSelectedIndex(0);
            onImageSaveLocationChanged(0);
        } else if ("QINIU".equalsIgnoreCase(imageSaveLocation)) {
            saveImgPanel.setSelectedIndex(1);
            onImageSaveLocationChanged(1);
        } else {
            saveImgPanel.setSelectedIndex(2);
            onImageSaveLocationChanged(2);
        }

        String localRelativeDirPath = PropertiesComponent.getInstance().getValue(Constants.LOCAL_RELATIVE_DIR_PATH);
        if(localRelativeDirPath == null || localRelativeDirPath.trim().length() == 0){
            localRelativeDirPath = "images";
        }
        localRelativeDirPathField.setText(localRelativeDirPath);

        //qiniu
        qiniuImgUrlPrefixField.setText(PropertiesComponent.getInstance().getValue(Constants.QINIU_IMG_URL_PREFIX));
        qiniuAccessKeyField.setText(PropertiesComponent.getInstance().getValue(Constants.QINIU_ACCESS_KEY));
        qiniuSecretKeyField.setText(PropertiesComponent.getInstance().getValue(Constants.QINIU_SECRET_KEY));
        qiniuBucketNameField.setText(PropertiesComponent.getInstance().getValue(Constants.QINIU_BUCKET_NAME));

        //aliyun
        aliyunAccessKeySecret.setText(PropertiesComponent.getInstance().getValue(Constants.ALIYUN_ACCESS_KEY_SECRET));
        aliyunEndPoint.setText(PropertiesComponent.getInstance().getValue(Constants.ALIYUN_END_POINT));
        aliyunAccessKeyId.setText(PropertiesComponent.getInstance().getValue(Constants.ALIYUN_ACCESS_KEY_ID));
        aliyunBucketName.setText(PropertiesComponent.getInstance().getValue(Constants.ALIYUN_BUCKET_NAME));
        aliyunFolder.setText(PropertiesComponent.getInstance().getValue(Constants.ALIYUN_FOLDER));
    }
}
