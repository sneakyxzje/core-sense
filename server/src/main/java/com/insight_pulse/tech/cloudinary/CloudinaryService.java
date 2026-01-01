package com.insight_pulse.tech.cloudinary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
public Map<String, Object> uploadFile(MultipartFile file) {
    try {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) originalFilename = "file_upload.pdf";
        String extension = "";
        String nameWithoutExt = originalFilename;
        int dotIndex = originalFilename.lastIndexOf(".");
        if(dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
            nameWithoutExt = originalFilename.substring(0, dotIndex);
        }
        String resourceType = "auto";
        Map<String, Object> params = new HashMap<>();
        
        if (extension.equalsIgnoreCase(".pdf")) {
            resourceType = "raw";
            String safeName = nameWithoutExt.replaceAll("[^a-zA-Z0-9]", "_");
            String publicId = safeName + "_" + System.currentTimeMillis() + extension;
            params.put("public_id", publicId);
        }

        params.put("resource_type", resourceType);
        params.put("use_filename", true);
        params.put("unique_filename", true); 
        return cloudinary.uploader().upload(file.getBytes(), params);
    } catch (IOException ex) {
        throw new RuntimeException("Upload thất bại: " + ex.getMessage());
    }
}
}
