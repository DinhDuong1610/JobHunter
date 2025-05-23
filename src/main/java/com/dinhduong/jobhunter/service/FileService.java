package com.dinhduong.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());

        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectories(tmpDir.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Folder already exists");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        String finalName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException, IOException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File tmpDir = new File(path.toString());

        if (!tmpDir.exists() || !tmpDir.isFile()) {
            return 0;
        }

        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, IOException, FileNotFoundException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File tmpDir = new File(path.toString());

        if (!tmpDir.exists() || !tmpDir.isFile()) {
            throw new FileNotFoundException("File not found");
        }

        return new InputStreamResource(new FileInputStream(tmpDir));
    }
}
