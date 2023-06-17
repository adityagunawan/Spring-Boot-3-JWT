package com.sinarmas.hauling.system.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeneralResponse<T> {
    private String code;
    private String message;
    private T data;

    public GeneralResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
