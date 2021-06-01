package com.example.imageservice.request;

import com.example.imageservice.model.RequestUrlStrategy;
import com.example.imageservice.service.ImageServiceImpl;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class RequestController {

  private final ImageServiceImpl imageService;

  @GetMapping(value = {"/show/{typeName}", "/show/{typeName}/{dummyName}"})
  public ResponseEntity<File> getOptimizedImage(@PathVariable String typeName,
      @PathVariable(required = false) String dummyName,
      @RequestParam("reference") String reference) {
    File result = imageService
        .handleRequest("show", new RequestUrlStrategy(typeName, dummyName, reference));
    log.info("{}", result.getPath());
    return ResponseEntity.ok().body(result);
  }

  @GetMapping("/flush/{typeName}")
  public void flushImage(@PathVariable String typeName,
      @RequestParam("reference") String reference) {
    log.info("{}", typeName);
    imageService.handleRequest("flush", new RequestUrlStrategy(typeName, null, reference));
  }

}
