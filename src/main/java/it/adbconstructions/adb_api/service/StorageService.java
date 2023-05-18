package it.adbconstructions.adb_api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(String username, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String fileName = "images/" + username + "/" + username + "." + extension;
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertMultiPartFileToFile(username,file)));
        return "File uploaded : " + fileName;
    }

    public String uploadMyProfileImage(String username, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String fileName = "images/" + username + "/" + username + "." + extension;
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertMultiPartFileToFile(username,file)));
        String objectUrl = s3Client.getUrl(bucketName, fileName).toString();
        return objectUrl;
    }

    public String uploadProjectImage(String projectCode, MultipartFile file,String id) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String fileName = "projects/" + projectCode + "/" + projectCode + "_" + id +"/" + projectCode + "_" + id + "." + extension;
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertMultiPartFileToFile(projectCode,file)));
        return extractObjectUrlProjectImage(projectCode);
    }

    public String extractObjectUrl(String username) {
        String urlQuery = getFileUrl(username);
        try {
            URL url = new URL(urlQuery);
            String bucketName = url.getHost().split("\\.")[0];
            String objectKey = url.getPath().substring(1);
            return "https://s3.amazonaws.com/" + bucketName + "/" + objectKey;
        } catch (Exception e) {
            // handle invalid URL
            return null;
        }
    }

    public String extractObjectUrlProjectImage(String projectCode) {
        String urlQuery = getProjectFileUrl(projectCode);
        try {
            URL url = new URL(urlQuery);
            String bucketName = url.getHost().split("\\.")[0];
            String objectKey = url.getPath().substring(1);
            return "https://s3.amazonaws.com/" + bucketName + "/" + objectKey;
        } catch (Exception e) {
            // handle invalid URL
            return null;
        }
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDefaultFileUrl() {
        URL url = s3Client.getUrl(bucketName, "images/default-profile.jpeg");
        return url.toString();
    }

    public String getFileUrl(String username) {
        return getFirstFileUrlFromFolder(bucketName, "images/" + username);
    }

    public String getProjectFileUrl(String projectCode) {
        return getFirstFileUrlFromFolder(bucketName, "projects/" + projectCode);
    }

    public String getFirstFileUrlFromFolder(String bucketName, String folderName) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folderName + "/");
        List<S3ObjectSummary> objectSummaries = s3Client.listObjectsV2(request).getObjectSummaries();
        if (!objectSummaries.isEmpty()) {
            String key = objectSummaries.get(0).getKey();
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key);
            URL url = s3Client.generatePresignedUrl(urlRequest);
            return url.toString();
        } else {
            return null;
        }
    }


    public String deleteFile(String username) {
        String folderName = "images/" + username + "/";
        deleteFolder(bucketName, folderName);
        //s3Client.deleteObject(bucketName, folderName);
        return folderName + " removed ...";
    }

    public String deleteProjectFile(String projectCode,String id) {
        try {
            String folderName = "projects/" + projectCode + "/" + projectCode + "_" +id +"/";
            deleteFolder(bucketName, folderName);
            //s3Client.deleteObject(bucketName, folderName);
            return folderName + " removed ...";
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public void deleteFolder(String bucketName, String folderName) {
        String folderKey = folderName.endsWith("/") ? folderName : folderName + "/";

        // List the objects in the folder
        ObjectListing objectListing = s3Client.listObjects(bucketName, folderKey);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        // Create a list of object keys to delete
        List<String> keysToDelete = new ArrayList<>();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            keysToDelete.add(objectSummary.getKey());
        }

        // Delete the objects
        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keysToDelete.toArray(new String[0]));
        s3Client.deleteObjects(deleteRequest);
    }


    private File convertMultiPartFileToFile(String username, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        File tempFile = File.createTempFile(username, extension);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("Error converting multipartFile to file");
            throw e;
        }

        return tempFile;
    }

}
