package com.veitch.code.build;

import com.veitch.code.base.AbstractBuildFactory;
import com.veitch.code.bean.Column;
import com.veitch.code.bean.Table;
import com.veitch.code.bean.TableWapper;
import com.veitch.code.enums.OutPathKey;
import com.veitch.code.util.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.veitch.code.util.Util.parseDate;

/**
 *
 * comments:  构件服务
 * since Date： 2016-11-16 15:21
 */
public class BuildService extends AbstractBuildFactory {

    @Override
    public OutPathKey getOutPath() {
        return OutPathKey.SERVICE;
    }

    @Override
    public void buildTable(TableWapper tableWapper) {

        String daoPackage = tableWapper.getDaoPackage();
        String pojoPackage = tableWapper.getPojoPackage();
        String servicePackage = tableWapper.getServicePackage();

        String outPath = tableWapper.getOutPathMap().get(OutPathKey.DEFULT);
        if (tableWapper.getOutPathMap().get(getOutPath()) != null) {
            outPath = tableWapper.getOutPathMap().get(getOutPath());
        }
        Table table = tableWapper.getTable();
        String tableName = table.getName().toLowerCase().replace("app_", "");
        String headName = Util.getUpperHumpName(tableName);
        String minDoName = Util.getHumpName(tableName);
        String bigDoName = Util.getUpperHumpName(tableName);
        String bigDaoName = Util.getUpperHumpName(tableName) + "Dao";
        String minDaoName = Util.getHumpName(tableName) + "Dao";

        String bigServiceName = "I" + Util.getUpperHumpName(tableName) + "Service";

        String idType = "String";
        List<Column> columnList = table.getColumns();
        for (Column column : columnList) {
            if (column.isPrimkey()) {
                if (column.getType().equalsIgnoreCase("BIGINT")) {
                    idType = "Long";
                } else if (column.getType().equalsIgnoreCase("INT") || column.getType().equalsIgnoreCase("TINYINT")) {
                    idType = "Integer";
                } else {
                    idType = "String";
                }
            }
            break;
        }


        Map<String, Object> map = new HashMap<>();
        map.put("pojoPackage", pojoPackage);
        map.put("daoPackage", daoPackage);
        map.put("servicePackage", servicePackage);
        map.put("bigDoName", bigDoName);
        map.put("minDoName", minDoName);
        map.put("bigDaoName", bigDaoName);
        map.put("minDaoName", minDaoName);
        map.put("bigServiceName", bigServiceName);
        map.put("headName", headName);
        map.put("idType", idType);
        map.put("date", parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        Util.writeCode("service", map, outPath + bigServiceName + ".java/");
    }
}
