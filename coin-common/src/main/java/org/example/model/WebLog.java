package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebLog {
    private String description;
    private String username;
    private String spendTime;
    private String basePath;
    private String uri;
    private String url;
    private String method;
    private String ip;
    private String parameter;
    private Object result;
}
