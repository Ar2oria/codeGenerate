package com.veitch.code.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表信息
 *
 *
 * comments:  对此类的描述，可以引用系统设计中的描述
 * since Date： 2016/11/16 14:54
 */
public class Table {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private List<Column> columns = new ArrayList<Column>();

    public void addColumn(Column c) {
        columns.add(c);
    }
}
