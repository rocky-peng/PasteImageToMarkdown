package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/14 14:05
 * Description:
 */
public class MySetting implements Configurable {
    private JTextField qiniuAccesskey;
    private JTextField qiniuSecreteKey;
    private JPanel jPanel;
    private JTextField qiniuImgUrlPrefix;
    private JTextField qiniuBucketName;

    @Nls
    @Override
    public String getDisplayName() {
        return "PasteImageToMarkdown";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return jPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue("qiniu_access_key", qiniuAccesskey.getText());
        PropertiesComponent.getInstance().setValue("qiniu_secret_key", qiniuSecreteKey.getText());
        PropertiesComponent.getInstance().setValue("qiniu_img_url_prefix", qiniuImgUrlPrefix.getText());
        PropertiesComponent.getInstance().setValue("qiniu_bucket_name", qiniuBucketName.getText());
    }

    @Override
    public void reset() {
        qiniuImgUrlPrefix.setText(null);
        qiniuAccesskey.setText(null);
        qiniuSecreteKey.setText(null);
        qiniuBucketName.setText(null);
    }

    @Override
    public void disposeUIResources() {
    }
}
