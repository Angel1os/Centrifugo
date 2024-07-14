package com.angellos.push.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@AllArgsConstructor
@Data
public class ObjectNotValidException extends RuntimeException{
    Set<String> errorMessages;
}
