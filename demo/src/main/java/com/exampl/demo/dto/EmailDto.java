package com.exampl.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    private String sender;
    private String title;
    private String content;
    private String method;
}
