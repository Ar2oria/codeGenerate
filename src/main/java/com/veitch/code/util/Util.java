package com.veitch.code.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Util {

    private final static String TEMPLATE_PATH = "template/mysql/mybatis";

    /**
     * 取得首字母大写的驼峰名称
     *
     * @param name
     * @return
     */
    public static String getUpperHumpName(String name) {
//		String n = name.toLowerCase();
        String n = name;
        String[] ns = n.split("_");
        StringBuilder humpName = new StringBuilder(name.length());
        for (int i = 0; i < ns.length; i++) {
            humpName.append(ns[i].substring(0, 1).toUpperCase());
            humpName.append(ns[i].substring(1));
        }
        return humpName.toString();
    }

    /**
     * 取得首字母小写的驼峰名称
     *
     * @param name
     * @return
     */
    public static String getHumpName(String name) {
//		String n = name.toLowerCase();
        String n = name;
        String[] ns = n.split("_");
        StringBuilder humpName = new StringBuilder(name.length());
        for (int i = 0; i < ns.length; i++) {
            if (i == 0) {
                humpName.append(ns[i]);
            } else {
                humpName.append(ns[i].substring(0, 1).toUpperCase());
                humpName.append(ns[i].substring(1));
            }
        }
        return humpName.toString();
    }

    /**
     * 根据指定的vm生成文件
     *
     * @param vmName
     * @param map
     * @param outPath
     */
    public static void writeCode(String vmName, Map<String, Object> map, String outPath) {
        VelocityEngine velocityEngine = createVelocityEngine();
        Template template = velocityEngine.getTemplate(String.format(TEMPLATE_PATH + "/%s.vm", vmName));
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }
        Writer w = null;
        try {
            File file = new File(outPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            w = new FileWriter(outPath);
            template.merge(velocityContext, w);
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(w);
        }
    }

    private static VelocityEngine createVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("input.encoding", "UTF-8");
        velocityEngine.setProperty("output.encoding", "UTF-8");
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
        return velocityEngine;
    }

    /**
     * 日期转换(Date转为String)
     *
     * @param date
     * @param format
     * @return
     */
    public static String parseDate(Date date, String format) {
        if (StringUtils.isBlank(format) || date == null)
            return null;
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static void main(String[] args) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("name", "json");
//		Util.writeCode("test", map, "E:/AAAA/test.txt");
        System.out.println(parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}
