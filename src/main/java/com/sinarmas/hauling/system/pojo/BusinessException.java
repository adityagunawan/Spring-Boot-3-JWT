package com.sinarmas.hauling.system.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor
@Getter
@Setter
public class BusinessException extends Exception{
    private HttpStatusCode httpStatus;
    private String code;
    private String message;

    public BusinessException(HttpStatusCode httpStatus,String code, String message){
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
