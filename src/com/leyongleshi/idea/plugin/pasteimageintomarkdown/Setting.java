package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

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
    private JTextField tencentSecretId;
    private JTextField tencentSecretKey;
    private JTextField tencentRegion;
    private JTextField tencentBucketName;
    private JLabel aliyunSiteLabel;
    private JLabel tencentCloudSiteLabel;
    private JLabel qiniuSiteLabel;
    private JLabel gonggao;
    private JLabel gonggaoLabel;
    private String imageSaveLocation = "LOCAL";

    public static void main(String[] args) {
        JFrame frame = new JFrame("demo");
        frame.setContentPane(new Setting().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Setting() {
        aliyunSiteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("https://www.aliyun.com/minisite/goods?userCode=j9pkgcae"));
                } catch (Exception ex) {

                }
            }
        });
        tencentCloudSiteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("https://url.cn/5F2M7Np"));
                } catch (Exception ex) {

                }
            }
        });
        qiniuSiteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("https://portal.qiniu.com/qvm/active?code=1381239317kEE2y"));
                } catch (Exception ex) {

                }
            }
        });
        saveImgPanel.addChangeListener(e -> {
            JTabbedPane source = (JTabbedPane) e.getSource();
            int selectedIndex = source.getSelectedIndex();
            onImageSaveLocationChanged(selectedIndex);
        });

        JLabel finalGonggao = this.gonggao;
        JLabel finalGonggaoLable = this.gonggaoLabel;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String gonggao =  HttpRequestUtils.get("https://github.com/rocky-peng/PasteImageToMarkdown/releases/download/gonggao/gonggao.txt");
                    gonggao = gonggao.trim();
                    finalGonggaoLable.setText("注意：");
                    finalGonggao.setText(gonggao);
                }catch (Exception e){
                }
            }
        }).start();
    }


    private void onImageSaveLocationChanged(int selectedIndex) {
        if (selectedIndex == 0) {
            imageSaveLocation = "LOCAL";
        } else if (selectedIndex == 1) {
            imageSaveLocation = "QINIU";
        } else if (selectedIndex == 2) {
            imageSaveLocation = "ALIYUN";
        } else {
            imageSaveLocation = "TENCENT";
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

        //tencent
        PropertiesComponent.getInstance().setValue(Constants.TENCENT_SECRET_ID, tencentSecretId.getText());
        PropertiesComponent.getInstance().setValue(Constants.TENCENT_SECRET_KEY, tencentSecretKey.getText());
        PropertiesComponent.getInstance().setValue(Constants.TENCENT_REGION, tencentRegion.getText());
        PropertiesComponent.getInstance().setValue(Constants.TENCENT_BUCKET_NAME, tencentBucketName.getText());
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
        } else if ("ALIYUN".equalsIgnoreCase(imageSaveLocation)) {
            saveImgPanel.setSelectedIndex(2);
            onImageSaveLocationChanged(2);
        } else {
            saveImgPanel.setSelectedIndex(3);
            onImageSaveLocationChanged(3);
        }

        String localRelativeDirPath = PropertiesComponent.getInstance().getValue(Constants.LOCAL_RELATIVE_DIR_PATH);
        if (localRelativeDirPath == null || localRelativeDirPath.trim().length() == 0) {
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

        //tencent
        tencentSecretId.setText(PropertiesComponent.getInstance().getValue(Constants.TENCENT_SECRET_ID));
        tencentSecretKey.setText(PropertiesComponent.getInstance().getValue(Constants.TENCENT_SECRET_KEY));
        tencentRegion.setText(PropertiesComponent.getInstance().getValue(Constants.TENCENT_REGION));
        tencentBucketName.setText(PropertiesComponent.getInstance().getValue(Constants.TENCENT_BUCKET_NAME));
    }
}
