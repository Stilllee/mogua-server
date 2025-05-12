/*
package com.fesi6.team1.study_group.global.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class S3FileService {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3FileService(AmazonS3 s3Client, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String path) throws IOException {
        String imageName = String.valueOf(System.currentTimeMillis());
        String fileName = path + "/" + imageName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return imageName;
    }

    public void deleteFile(String filePath) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, filePath));
    }
}
*/