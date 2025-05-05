package com.dinhduong.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dinhduong.jobhunter.domain.response.file.ResUploadFileDTO;
import com.dinhduong.jobhunter.service.FileService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;

import io.micrometer.core.ipc.http.HttpSender.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException {

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
