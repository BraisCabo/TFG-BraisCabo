package com.tfg.brais.Service.ComplementaryServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Service
public class FileService {

    @Value("${file-dir}")
    private String fileDir;

    public ResponseEntity<Resource> compressDirectory(String filePath, String zipFileName, String subjectPath) {
        try {
            Path zipPath = Paths.get(fileDir, filePath);
            Path zippedPath = Paths.get(fileDir, subjectPath, zipFileName);
            ZipUtil.pack(new File(zipPath.toString()), new File(zippedPath.toString()));
            ResponseEntity<Resource> downloadFile = downloadFile(Paths.get(subjectPath, zipFileName));
            return downloadFile;
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    public void saveFile(MultipartFile fileToSavFile, Path finalPath)
            throws IOException {
        if (fileToSavFile != null && !fileToSavFile.isEmpty()) {
            Files.createDirectories(Paths.get(fileDir, finalPath.toString()));
            Files.copy(fileToSavFile.getInputStream(),
                    Paths.get(fileDir, finalPath.toString(), fileToSavFile.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new IOException();
        }
    }

    public ResponseEntity<Resource> downloadFile(Path filePath) {
        Resource resource = new FileSystemResource(Paths.get(fileDir, filePath.toString()));
        if (resource.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", resource.getFilename());
            return ResponseEntity.ok().headers(headers).body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public void createTextFile(String filePath, String fileName, List<String> questions, List<String> answers)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            sb.append(i + ". " + questions.get(i) + "\n");
            sb.append(answers.get(i) + "\n\n\n");
        }
        Files.createDirectories(Paths.get(fileDir, filePath.toString()));
        Files.write(
                Paths.get(fileDir, filePath.toString(), fileName),
                sb.toString().getBytes(),
                StandardOpenOption.CREATE);
    }

    public void deleteDirectory(String filePath) throws IOException {
        Path deletePath = Paths.get(fileDir, filePath);
        Files.walk(deletePath)
                    .sorted((a, b) -> b.compareTo(a)) // Ordena de forma descendente para borrar primero los archivos/directorios internos
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Manejo de errores
                            e.printStackTrace();
                        }
                    });
            Files.deleteIfExists(deletePath);
    }
}
