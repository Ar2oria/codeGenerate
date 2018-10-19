package com.veitch.code.bean;

import com.veitch.code.enums.OutPathKey;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class TableWapper {

    @Setter
    @Getter
    private Table table;

    @Setter
    @Getter
    private String pojoPackage;

    @Setter
    @Getter
    private String voPackage;

    @Setter
    @Getter
    private String daoPackage;

    @Setter
    @Getter
    private String servicePackage;

    @Setter
    @Getter
    private String serviceImplPackage;

    @Setter
    @Getter
    private Map<OutPathKey, String> outPathMap;

    public TableWapper(Table t) {
        table = t;
    }
}
