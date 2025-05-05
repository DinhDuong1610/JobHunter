package com.dinhduong.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dinhduong.jobhunter.domain.response.file.ResUploadFileDTO;
import com.dinhduong.jobhunter.service.FileService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "png", "pdf", "docx");
        boolean isValid = allowedExtensions.stream()
                .anyMatch(item -> fileName.toLowerCase().endsWith(item.toLowerCase()));
        if (!isValid) {
            throw new StorageException(
                    "File extension is not supported. Only allowed extensions are: " + allowedExtensions.toString());
        }

        // create a directory if it doesn't exist
        this.fileService.createDirectory(baseURI + folder);
        // save the file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO();
        res.setFileName(uploadFile);
        res.setUploadedAt(Instant.now());

        return ResponseEntity.ok().body(res);
    }
}
