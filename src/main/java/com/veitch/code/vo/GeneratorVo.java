package com.veitch.code.vo;

import lombok.Data;

/**
 * 代码生成对象
 */
@Data
public class GeneratorVo {

    private String connection;

    private String dataBase;

    private String port;

    private String userId;

    private String userPass;

    private String modelPath;

    private String[] tableNames;

    private String contextPath;
}