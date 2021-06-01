package com.example.imageservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.imageservice.model.RequestUrlStrategy;
import com.example.imageservice.request.RequestController;
import com.example.imageservice.service.BucketServiceImpl;
import com.example.imageservice.service.ImageFlushServiceImpl;
import com.example.imageservice.service.ImageServiceImpl;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

  private static File image;
  private static String referenceName;
  private static String referencePath;
  private static RequestUrlStrategy requestUrl;

  @Autowired
  private RequestController controller;
  @InjectMocks
  private BucketServiceImpl service;

  @MockBean
  private BucketServiceImpl bucketService;
  @MockBean
  private ImageFlushServiceImpl imageFlushService;
  @MockBean
  private ImageServiceImpl imageService;

  @BeforeTestClass
  public void setUp() {
    requestUrl = new RequestUrlStrategy("thumbnail", null, referenceName);
    image = new File(
        "/Users/Chiara/IntelliJProjects/image-service/mockS3Bucket/thumbnail/" + referencePath);
  }

  @Test
  public void contextLoads() {
    assertTrue(Objects.nonNull(controller));
  }

  @Test
  public void showExistingImage() {
    referenceName = "boat.png";
    referencePath = "boat/boat.png";
    setUp();
    when(imageService.handleRequest("show", requestUrl)).thenReturn(image);
    File result = imageService.handleRequest("show", requestUrl);
    assertThat(FilenameUtils.getBaseName(result.getName())).isEqualTo("boat");
    assertThat(FilenameUtils.getExtension(result.getName())).isEqualTo("png");
  }

  @Test
  public void showResizedImageFromOriginalInBucket() {
    referenceName = "cat.png";
    referencePath = "cat/cat.png";
    setUp();
    when(imageService.handleRequest("show", requestUrl)).thenReturn(image);
    assertTrue(Files.notExists(image.toPath()));
    File result = imageService.handleRequest("show", requestUrl);
    assertEquals(result.getPath(), image.toPath().toString());
    assertThat(FilenameUtils.getBaseName(result.getName())).isEqualTo("cat");
    assertThat(FilenameUtils.getExtension(result.getName())).isEqualTo("png");
  }

  @Test
  public void showResizedImageFromOriginalOnSource() {
    referenceName = "tulips.png";
    referencePath = "tuli/tulips.png";
    setUp();
    when(imageService.handleRequest("show", requestUrl)).thenReturn(image);
    String originalImagePath = image.getPath().replace("thumbnail", "original");
    assertTrue(Files.notExists(Paths.get(originalImagePath)));
    assertTrue(Files.notExists(image.toPath()));
    File result = imageService.handleRequest("show", requestUrl);
    assertEquals(result.getPath(), image.toPath().toString());
    assertThat(FilenameUtils.getBaseName(result.getName())).isEqualTo("tulips");
    assertThat(FilenameUtils.getExtension(result.getName())).isEqualTo("png");
  }

  @Test
  public void testS3DirectoryNaming() {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put(
        "%2F027%2F790%2F13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg",
        "_027/_790/_027_790_13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg");
    valuesMap.put("abcdefghij.jpg", "abcd/efgh/abcdefghij.jpg");
    valuesMap.put("abcde.jpg", "abcd/abcde.jpg");
    valuesMap.put("/somedir/anotherdir/abcdef.jpg",
        "_som/edir/_somedir_anotherdir_abcdef.jpg");

    valuesMap.forEach((endpoint, expected) -> {
      requestUrl = new RequestUrlStrategy("thumbnail", null, endpoint);
      String path = "mockS3Bucket/thumbnail/";
      assertEquals(service.getS3DirectoryPath(requestUrl, path), path.concat(expected));
    });
  }

  @Test
  public void testFlushOneType() {
    String endpoint = "/image/flush/thumbnail/?reference=%2F027%2F790%2F13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg";
  }

  @Test
  public void testFlushAllTypes() {
    String endpoint = "/image/flush/original/?reference=%2F027%2F790%2F13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg";
  }

}
