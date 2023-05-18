package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.ExceptionHandling;
import it.adbconstructions.adb_api.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class StorageController extends ExceptionHandling {

    @Autowired
    private StorageService service;

    @PostMapping("/upload/{username}")
    public ResponseEntity<String> uploadFile(@PathVariable String username, @RequestParam(value = "file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(service.uploadFile(username, file), HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/default")
    public ResponseEntity<String> getDefaultFile() {
        return new ResponseEntity<>(service.getDefaultFileUrl(), HttpStatus.OK);
    }

    @GetMapping("/image/{username}")
    public ResponseEntity<String> getFile(@PathVariable String username) {
        return new ResponseEntity<>(service.getFileUrl(username), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteFile(@PathVariable String username) {
        return new ResponseEntity<>(service.deleteFile(username), HttpStatus.OK);
    }
}
