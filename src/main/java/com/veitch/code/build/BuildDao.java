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

/**
 *
 * comments:  构建持久层
 * since Date： 2016/11/16 15:23
 */
public class BuildDao extends AbstractBuildFactory {

    @Override
    public OutPathKey getOutPath() {
        return OutPathKey.DAO;
    }

    @Override
    public void buildTable(TableWapper tableWapper) {

        String daoPackage = tableWapper.getDaoPackage();
        String pojoPackage = tableWapper.getPojoPackage();
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
        bigDoName = bigDoName.replace("Tab", "");
        bigDaoName = bigDaoName.replace("Tab", "");

        char letters[] = new char[tableName.length()];
        for (int i = 0; i < tableName.length(); i++) {
            char letter = tableName.charAt(i);
            if (letter >= 'a' && letter <= 'z')
                letter = (char) (letter - 32);
            letters[i] = letter;
        }

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
                break;
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("daoPackage", daoPackage);
        map.put("pojoPackage", pojoPackage);
        map.put("bigDoName", bigDoName);
        map.put("minDoName", minDoName);
        map.put("bigDaoName", bigDaoName);
        map.put("headName", headName);
        map.put("idType", idType);
        map.put("date", Util.parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("tableName", new String(letters));
        Util.writeCode("dao", map, outPath + bigDaoName + ".java/");
    }
}
