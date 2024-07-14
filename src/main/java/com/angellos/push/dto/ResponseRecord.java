package com.angellos.push.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;

public record ResponseRecord(
        String message,
        int statusCode,
        Object data,
        ZonedDateTime dateTime
) {
    public ResponseEntity<ResponseRecord> toResponseEntity(){
        return new ResponseEntity<>(this, HttpStatus.valueOf(this.statusCode));
    }
}
