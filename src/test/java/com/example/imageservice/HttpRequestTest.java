package com.example.imageservice;

import com.example.imageservice.service.ImageServiceImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private ImageServiceImpl imageService;

  private static final String testGetImageUrl = "https://10.0.2.2:8080/image/show/thumbnail/dept-blazer/?reference=%2F027%2F790%2F13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg";

  @Test
  public void greetingShouldReturnDefaultMessage() throws Exception {
    assertThat(this.restTemplate.getForObject(testGetImageUrl, String.class))
        .contains("Hello, World");
  }

}
