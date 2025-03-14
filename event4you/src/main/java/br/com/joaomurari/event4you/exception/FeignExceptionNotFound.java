package br.com.joaomurari.event4you.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FeignExceptionNotFound extends RuntimeException {

    public FeignExceptionNotFound(String msg) {
        super(msg);
    }
}
