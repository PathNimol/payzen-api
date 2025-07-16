package org.aub.payrollapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aub.payrollapi.model.entity.FileMetadata;
import org.aub.payrollapi.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileImplement implements FileService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public FileMetadata fileUpload(MultipartFile file) {
        try {
            // Generate unique filename
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());

            // Upload file
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .acl(ObjectCannedACL.PUBLIC_READ) // Set ACL to public read if you want the file to be accessible publicly
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            // Generate public file URL (requires correct bucket policy or use presigned URL instead)
            String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

            return FileMetadata.builder()
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @SneakyThrows
    @Override
    public InputStream getFileByFileName(String fileName) {
        try {
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build()
            );
            return s3Object;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file from S3", e);
        }
    }

}

