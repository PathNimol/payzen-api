package org.aub.payrollapi.service;

import org.aub.payrollapi.model.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    FileMetadata fileUpload(MultipartFile file);

    InputStream getFileByFileName(String fileName);
}
