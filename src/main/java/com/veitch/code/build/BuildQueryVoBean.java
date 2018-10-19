package com.veitch.code.build;

import com.veitch.code.base.AbstractBuildFactory;
import com.veitch.code.bean.BigAndSmall;
import com.veitch.code.bean.Column;
import com.veitch.code.bean.Table;
import com.veitch.code.bean.TableWapper;
import com.veitch.code.enums.OutPathKey;
import com.veitch.code.util.CamelUtils;
import com.veitch.code.util.Util;

import java.util.*;

import static com.veitch.code.util.Util.parseDate;

/**
 *
 * comments:  构建模型
 * since Date： 2016/11/16 15:22
 */
public class BuildQueryVoBean extends AbstractBuildFactory {

    @Override
    public OutPathKey getOutPath() {
        return OutPathKey.VO;
    }

    @Override
    public void buildTable(TableWapper tableWapper) {
        List<Column> column = new ArrayList<Column>();
        List<BigAndSmall> column2 = new ArrayList<BigAndSmall>();
        Map<String, Object> map = new HashMap<String, Object>();

        Table table = tableWapper.getTable();
        List<Column> columnList = table.getColumns();
        String doName = Util.getUpperHumpName(table.getName().replace("app_", ""));
        String outPath = tableWapper.getOutPathMap().get(OutPathKey.DEFULT);
        if (tableWapper.getOutPathMap().get(getOutPath()) != null) {
            outPath = tableWapper.getOutPathMap().get(getOutPath());
        }
        int hasDate = 0;
        int hasBigDecimal = 0;
        String packageName = tableWapper.getVoPackage();
        for (Column co : columnList) {
            Column colu = new Column();
            colu.setPrimkey(co.isPrimkey());
            BigAndSmall bigSmall = new BigAndSmall();
            String type = co.getType();
            if (type.equalsIgnoreCase("DATETIME") || type.equalsIgnoreCase("DATE") || type.equalsIgnoreCase("TIME")
                    || type.equalsIgnoreCase("TIMESTAMP")) {
                type = "Date";
                hasDate = 1;
            } else if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("CHAR")
                    || type.equalsIgnoreCase("BLOB") || type.equalsIgnoreCase("TEXT")
                    || type.equalsIgnoreCase("LONGBLOB") || type.equalsIgnoreCase("LONGTEXT")
                    || type.equalsIgnoreCase("MEDIUMTEXT")) {
                type = "String";
            } else if (type.equalsIgnoreCase("ENUM")) {
                type = Util.getUpperHumpName(co.getName());
            } else if (type.equalsIgnoreCase("INT UNSIGNED") || type.equalsIgnoreCase("INT")
                    || type.equalsIgnoreCase("TINYINT")) {
                type = "Integer";
            } else if (type.equalsIgnoreCase("BIGINT")) {
                type = "Long";
            } else if (type.equalsIgnoreCase("DOUBLE")) {
                type = "Double";
            } else if (type.equalsIgnoreCase("FLOAT")) {
                type = "Float";
            } else if (type.equalsIgnoreCase("INTEGER")) {
                type = "Integer";
            } else if (type.equalsIgnoreCase("DECIMAL")) {
                type = "BigDecimal";
                hasBigDecimal = 1;
            } else if (type.equalsIgnoreCase("BIT")) {
                type = "Boolean";
            }
//			if(Util.getHumpName(co.getName()).equalsIgnoreCase("id")||
//					Util.getHumpName(co.getName()).equalsIgnoreCase("gmtCreated")||
//					Util.getHumpName(co.getName()).equalsIgnoreCase("gmtModified")) continue;
            colu.setType(type);
            colu.setRemark(co.getRemark());
            colu.setName(CamelUtils.camelName(co.getName().toLowerCase()));
            bigSmall.setBigName(Util.getUpperHumpName(co.getName()));
            bigSmall.setSmallName(Util.getHumpName(co.getName()));
            bigSmall.setType(type);

            column.add(colu);
            column2.add(bigSmall);
        }

        String headName = "Query" + doName.replace("Tab", "") + "VO";
        map.put("columList", column);
        map.put("columList2", column2);
        map.put("packageName", packageName);
        map.put("headName", headName);
        map.put("hasDate", hasDate);
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("date", parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));

        Util.writeCode("queryvobean", map, outPath + headName + ".java/");
    }
}
