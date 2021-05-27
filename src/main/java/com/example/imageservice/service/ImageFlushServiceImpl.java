package com.example.imageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here?
public class ImageFlushServiceImpl {



}
