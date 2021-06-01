package com.example.imageservice.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.imageservice.model.RequestUrlStrategy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here?
public class BucketServiceImpl {

  @Value("${service.awsAccessKey}")
  private String awsAccessKey;
  @Value("${service.awsSecretKey}")
  private String awsSecretKey;

  public String getS3DirectoryPath(RequestUrlStrategy requestUrl, String path) {
    String uniqueFileName = requestUrl.getReference().replace("/", "_");
    if (FilenameUtils.getBaseName(uniqueFileName).length() > 3) {
      path = path.concat(uniqueFileName.substring(0, 4) + "/");
      if (FilenameUtils.getBaseName(uniqueFileName).length() > 7) {
        path = path.concat(uniqueFileName.substring(4, 8) + "/");
      }
    } else {
      path = path.concat(FilenameUtils.getBaseName(uniqueFileName) + "/");
    }
    return path.concat(uniqueFileName);
  }

  void createMissingDirectories(String imagePath) {
    File f = new File(imagePath);
    if (Objects.nonNull(f.getParentFile())) {
      f.getParentFile().mkdirs();
    }
  }

  void saveImage(Path source, Path destination) {
    try {
      Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) { //TODO - handle exception better
      e.printStackTrace();
    }
  }

  /* ------------------------------------------------------------------------------------------- */
  // Code to use in implementation using a AWS S3 Bucket => change return methods from 'File' to 'S3Object'
/*  private S3Object getOptimizedS3Image() {
    configureAwsClient();
    if (!s3client.doesObjectExist(bucketName, optimizedImage)) {
      optimizeFromOriginalImage();
    }
    return s3client.getObject(bucketName, optimizedImage);
  }*/

  private void configureAwsClient() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    AmazonS3 s3client = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(Regions.EU_CENTRAL_1)
        .build();
  }

}
