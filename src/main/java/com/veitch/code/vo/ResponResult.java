package com.veitch.code.vo;

import lombok.Data;

/**
 * 请求返回对象封装
 */
@Data
public class ResponResult {

    /**
     * 状态码
     */
    private String rspCode;

    /**
     * zip包名称
     */
    private String zipName;

}
