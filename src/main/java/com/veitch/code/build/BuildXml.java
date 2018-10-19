package com.veitch.code.build;

import com.veitch.code.base.AbstractBuildFactory;
import com.veitch.code.bean.Column;
import com.veitch.code.bean.TableWapper;
import com.veitch.code.bean.XmlBean;
import com.veitch.code.enums.OutPathKey;
import com.veitch.code.util.CamelUtils;
import com.veitch.code.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * comments:  构件Mappering
 * since Date： 2016/11/16 15:23
 */
public class BuildXml extends AbstractBuildFactory {

    static Map<String, String> jdbcType2JavaTypeMap = new HashMap<String, String>();

    static {
        jdbcType2JavaTypeMap.put("CHAR", "String");
        jdbcType2JavaTypeMap.put("VARCHAR", "String");
        jdbcType2JavaTypeMap.put("LONGVARCHAR", "String");
        jdbcType2JavaTypeMap.put("NUMERIC", "java.math.BigDecimal");
        jdbcType2JavaTypeMap.put("DECIMAL", "java.math.BigDecimal");
        jdbcType2JavaTypeMap.put("BIT", "boolean");
        jdbcType2JavaTypeMap.put("BOOLEAN", "boolean");
        jdbcType2JavaTypeMap.put("TINYINT", "Byte");
        jdbcType2JavaTypeMap.put("INT", "int");
        jdbcType2JavaTypeMap.put("SMALLINT", "Short");
        jdbcType2JavaTypeMap.put("INTEGER", "Integer");
        jdbcType2JavaTypeMap.put("BIGINT", "Long");
        jdbcType2JavaTypeMap.put("REAL", "float");
        jdbcType2JavaTypeMap.put("FLOAT", "float");
        jdbcType2JavaTypeMap.put("DOUBLE", "double");
        jdbcType2JavaTypeMap.put("BINARY", "byte[]");
        jdbcType2JavaTypeMap.put("LONGVARBINARY", "byte[]");
        jdbcType2JavaTypeMap.put("DATE", "java.sql.Date");
        jdbcType2JavaTypeMap.put("TIME", "java.sql.Time");
        jdbcType2JavaTypeMap.put("DATE", "java.sql.Date");
        jdbcType2JavaTypeMap.put("TIMESTAMP", "java.sql.Timestamp");
        jdbcType2JavaTypeMap.put("CLOB", "java.sql.Clob");
        jdbcType2JavaTypeMap.put("BLOB", "java.sql.Blob");
    }


    @Override
    public OutPathKey getOutPath() {
        return OutPathKey.XML;
    }

    @Override
    public void buildTable(TableWapper tableWapper) {
        try {
            List<XmlBean> list = new ArrayList<XmlBean>();

            String outPath = tableWapper.getOutPathMap().get(OutPathKey.DEFULT);
            if (tableWapper.getOutPathMap().get(getOutPath()) != null) {
                outPath = tableWapper.getOutPathMap().get(getOutPath());
            }
            List<Column> columns = tableWapper.getTable().getColumns();
            String table_name = tableWapper.getTable().getName();
            String pojoPackage = tableWapper.getPojoPackage();
            String voPackage = tableWapper.getVoPackage();
            String daoPackage = tableWapper.getDaoPackage();
            String tableName = Util.getHumpName(tableWapper.getTable().getName().toLowerCase().replace("app_", ""));
            String TableName = Util.getUpperHumpName(tableWapper.getTable().getName().toLowerCase().replace("app_", ""));
            String idType = "String";
            String idName = "";
            String idProName = "";
            String idParameterType = "";
            String idJdbcType = "";
            for (Column column : columns) {
                XmlBean xmlBean = new XmlBean();
                xmlBean.setJdbcType(column.getType());
                xmlBean.setColumnName(column.getName());
                xmlBean.setPropertyName(CamelUtils.camelName(column.getName().toLowerCase()));
                if (column.getType().equalsIgnoreCase("DATE")) {
                    xmlBean.setType("DATE");
                } else if (column.getType().equalsIgnoreCase("DATETIME")
                        || column.getType().equalsIgnoreCase("TIMESTAMP")) {
                    xmlBean.setType("TIMESTAMP");
                    xmlBean.setJdbcType("TIMESTAMP");
                } else if (column.getType().equalsIgnoreCase("TIME")) {
                    xmlBean.setType("DATE");
                } else if (column.getType().equalsIgnoreCase("VARCHAR")
                        || column.getType().equalsIgnoreCase("CHAR")) {
                    xmlBean.setType("STRING");
                } else if (column.getType().equalsIgnoreCase("LONGBLOB")) {
                    xmlBean.setType("byte[]");
                    xmlBean.setJdbcType("LONGVARBINARY");
                } else if (column.getType().equalsIgnoreCase("LONGTEXT")) {
                    xmlBean.setType("STRING");
                    xmlBean.setJdbcType("LONGVARCHAR");
                } else if (column.getType().equalsIgnoreCase("BLOB")) {
                    xmlBean.setType("byte[]");
                    xmlBean.setJdbcType("BLOB");
                } else if (column.getType().equalsIgnoreCase("ENUM")) {
                    xmlBean.setType("STRING");
                } else if (column.getType().equalsIgnoreCase("BIGINT")
                        || column.getType().equalsIgnoreCase("DOUBLE")
                        || column.getType().equalsIgnoreCase("FLOAT")
                        || column.getType().equalsIgnoreCase("INTEGER")) {
                    xmlBean.setType("INT");
                } else if (column.getType().equalsIgnoreCase("INT")
                        || column.getType().equalsIgnoreCase("INT UNSIGNED")) {
                    xmlBean.setType("INT");
                    xmlBean.setJdbcType("INTEGER");
                } else if (column.getType().equalsIgnoreCase("TEXT")) {
                    xmlBean.setType("STRING");
                    xmlBean.setJdbcType("CLOB");
                }

                if (column.isPrimkey()) {
                    idName = CamelUtils.camelName(column.getName().toLowerCase());
                    idJdbcType = column.getType();
                    idProName = CamelUtils.camelName(idName.toLowerCase());
                    if (column.getType().equalsIgnoreCase("BIGINT")) {
                        idParameterType = "java.lang.Long";
                        idType = "Long";
                    } else if (column.getType().equalsIgnoreCase("INT")
                            || column.getType().equalsIgnoreCase("TINYINT")) {
                        idParameterType = "java.lang.Integer";
                        idType = "Integer";
                        idJdbcType = "INTEGER";
                    } else {
                        idParameterType = "java.lang.String";
                        idType = "String";
                    }
                }
                list.add(xmlBean);

            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("list", list);
            tableName = TableName.replace("Tab", "");
            map.put("tableName", tableName);
            map.put("TableName", TableName);
            map.put("table_name", table_name);
            map.put("TABLE_NAME", table_name.toUpperCase());
            map.put("pojoPackage", pojoPackage);
            map.put("pojoName", pojoPackage + "." + TableName);
            map.put("queryVoName", voPackage + "." + "Query" + TableName + "VO");
            map.put("daoPackage", daoPackage);
            map.put("daoName", daoPackage + "." + TableName + "Dao");
            map.put("idType", idType);
            map.put("idName", idName);
            map.put("idProName", idProName);
            map.put("idJdbcType", idJdbcType);
            map.put("idParameterType", idParameterType);
            Util.writeCode("basesqlmap", map, outPath + "Base" + tableName + "Mapper.xml/");
            Util.writeCode("sqlmap", map, outPath + tableName + "Mapper.xml/");

        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File |
        }
    }
}
