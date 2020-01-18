package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * @author chengliang
 * @date 2020/1/18 13:37
 */
public class TencentOSSHelper {

    private String bucketName;

    private COSClient cosClient;

    public TencentOSSHelper(String secretId, String secretKey, String region, String bucketName){
        COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        this.cosClient = new COSClient(cred, clientConfig);
        this.bucketName = bucketName;
    }

    public String upload(InputStream inputStream, String filePathName){
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePathName, inputStream, new ObjectMetadata());
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
        URL url = cosClient.generatePresignedUrl(bucketName,filePathName, expiration);
        //组装url,经过测试，与阿里云不同，腾讯云不能通过url.toString()获得url，但是能够通过规则http:// + 图片地址 + / + 地址获得url
        String uriOutput = url.getProtocol() + "://" + url.getHost() + "/"+ filePathName;
        return uriOutput;
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
