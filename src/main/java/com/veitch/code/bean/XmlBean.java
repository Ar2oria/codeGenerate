package com.veitch.code.bean;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * comments:  对此类的描述，可以引用系统设计中的描述
 * since Date： 2016/11/16 15:21
 */
public class XmlBean {

    @Setter
    @Getter
    private String propertyName;

    @Setter
    @Getter
    private String columnName;

    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private String jdbcType;
}
