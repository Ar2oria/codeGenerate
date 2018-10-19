package com.veitch.code.build;

import com.veitch.code.bean.Column;
import com.veitch.code.bean.Table;
import com.veitch.code.bean.TableWapper;
import com.veitch.code.enums.OutPathKey;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据表生成
 *
 * @author lhc
 */
public class TablesBuilder {

    private static final String MYSQL_CLASS = "com.mysql.jdbc.Driver";

    //数据库驱动
    @Setter
    @Getter
    private String jdbcClass = MYSQL_CLASS;

    //数据库链接
    @Setter
    @Getter
    private String url;

    //数据库用户名
    @Setter
    @Getter
    private String name;

    //数据库密码
    @Setter
    @Getter
    private String pwd;

    //pojo包地址
    @Setter
    @Getter
    private String pojoPackage;

    //vo包地址
    @Setter
    @Getter
    private String voPackage;

    //dao包地址
    @Setter
    @Getter
    private String daoPackage;

    //service包地址
    @Setter
    @Getter
    private String servicePackage;

    //serviceImpl包地址
    @Setter
    @Getter
    private String serviceImplPackage;

    //数据库表名 %全部
    @Setter
    @Getter
    private String tableName;

    //生成文件路径
    @Setter
    @Getter
    private Map<OutPathKey, String> outPathMap;

    /**
     * 获取数据库表信息
     *
     * @return
     * @throws Exception
     */
    public List<TableWapper> build() throws Exception {
        Connection conn = getConnection();
        //获取描述对象   这个对象包含了conn所连接的数据库的详细信息
        DatabaseMetaData mDatabaseMetaData = conn.getMetaData();
        //检索数据库中表的类型
        ResultSet tableRet = mDatabaseMetaData.getTables(null, null, tableName, new String[]{"TABLE"});

        List<TableWapper> tables = new ArrayList<TableWapper>();
        while (tableRet.next()) {
            TableWapper tableWapper = new TableWapper(new Table());
            tableWapper.setDaoPackage(daoPackage);
            tableWapper.setPojoPackage(pojoPackage);
            tableWapper.setVoPackage(voPackage);
            tableWapper.setServicePackage(servicePackage);
            tableWapper.setServiceImplPackage(serviceImplPackage);
            tableWapper.setOutPathMap(outPathMap);
            tables.add(tableWapper);
            String tableName = tableRet.getString("TABLE_NAME");
            System.out.print("数据库表名称:" + tableName);
            ResultSet rs = mDatabaseMetaData.getPrimaryKeys(null, null, tableName);
            rs.next();
            // 获取主键
            String privateKey = null;
            try {
                privateKey = rs.getString("COLUMN_NAME");
            } catch (SQLException e) {
                throw new RuntimeException("数据表需要有主键");
            }

            String tableRemarks = tableRet.getString("REMARKS");
            System.out.print("数据库表注释:" + tableRemarks);

            tableWapper.getTable().setName(tableName);
            // 获取数据库表设计信息
            ResultSet columnsResultSet = mDatabaseMetaData.getColumns(null, "%", tableName, "%");
            while (columnsResultSet.next()) {
                Column column = new Column();
                // 字段名称
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                if (columnName.equals(privateKey)) {
                    column.setPrimkey(true);
                }
                // 字段类型
                String columnType = columnsResultSet.getString("TYPE_NAME");
                // 字段注释
                String remark = columnsResultSet.getString("REMARKS");
                // 字段长度
                int datasize = columnsResultSet.getInt("COLUMN_SIZE");
                // 字段默认值
                int digits = columnsResultSet.getInt("DECIMAL_DIGITS");
                int nullable = columnsResultSet.getInt("NULLABLE");

                column.setRemark(remark);
                column.setName(columnName);
                column.setType(columnType);
                tableWapper.getTable().addColumn(column);
                System.out.println("字段名称:" + columnName);
                System.out.println("字段长度:" + datasize);
                System.out.println("字段类型:" + columnType);
                System.out.println("字段注释:" + remark);
                // System.out.print("java类型：" + rsd.getColumnClassName(i + 1));
                // System.out.print("  数据库类型:" + rsd.getColumnTypeName(i + 1));
                //
                // System.out.print("  字段长度:" + rsd.getColumnDisplaySize(i +
                // 1));
                System.out.println(columnName + " " + columnType + " " + datasize + " " + digits + " " + nullable);
            }
        }
        return tables;
    }

    /**
     * 连接数据库
     *
     * @return
     * @throws Exception
     */
    private Connection getConnection() throws Exception {
        Class.forName(jdbcClass);
        return DriverManager.getConnection(url, name, pwd);
    }
}
