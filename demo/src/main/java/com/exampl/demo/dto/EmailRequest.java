package com.exampl.demo.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmailRequest {
    private String email;
    private String title;
    private boolean isSpam;
    private String reason;
    private String time;
    private String method;


}
