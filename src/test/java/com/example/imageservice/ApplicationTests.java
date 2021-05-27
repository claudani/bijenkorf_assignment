package com.example.imageservice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.imageservice.model.S3DirectoryStrategy;
import com.example.imageservice.request.RequestController;
import com.example.imageservice.service.ImageServiceImpl;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

  @Autowired
  private RequestController controller;
  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private ImageServiceImpl imageService;

  @Test
  public void contextLoads() {
    assertTrue(Objects.nonNull(controller));
  }

  @Test
  public void S3DirectoryStrategyTest() {
    String fileName1 = "abcdefghij.jpg";
    String fileName2 = "abcde.jpg";
    String fileName3 = "/somedir/anotherdir/abcdef.jpg";

//    S3DirectoryStrategy.builder().build();
//  	assertTrue(1=1, true);
  }

  /*@Test
	public void testImageResize() {
  	imageService.resizedImage();
	}*/

  /*@Test
	public void testImageFlush() {
  	imageService.flush();
	}*/

}
