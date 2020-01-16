package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author pengqingsong
 * @date 2020/1/16
 * @desc
 */
public class AliyunOSSHelper {

    private String bucketName;

    private OSSClient ossClient;

    public AliyunOSSHelper(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.bucketName = bucketName;
        this.ossClient = (OSSClient) new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String upload(InputStream ins, String filePathName) {
        if (filePathName.startsWith("/")) {
            filePathName = filePathName.substring(1);
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePathName, ins);
        ossClient.putObject(putObjectRequest);
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
        URL url = ossClient.generatePresignedUrl(bucketName, filePathName, expiration);
        return url.toString();
    }

    public String upload(File file, String filePathName) {
        try {
            return upload(new FileInputStream(file), filePathName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String upload(BufferedImage image, String filePathName) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", result);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return upload(new ByteArrayInputStream(result.toByteArray()), filePathName);
    }

}
