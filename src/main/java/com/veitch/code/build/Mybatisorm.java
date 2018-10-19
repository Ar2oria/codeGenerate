package com.veitch.code.build;

import com.veitch.code.base.BuildFactory;
import com.veitch.code.bean.TableWapper;
import com.veitch.code.enums.OutPathKey;
import com.veitch.code.vo.GeneratorVo;
import com.veitch.code.vo.ResponResult;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Mybatisorm {

    public static final List<BuildFactory> BUILD_LIST = new ArrayList<>();
    // buffer大小
    static final int BUFFER = 8192;

    private static final String PROJECT_PATH = "/Users/lhc/generatorCode/";

    static {
        BUILD_LIST.add(new BuildDao());
        BUILD_LIST.add(new BuildXml());
        BUILD_LIST.add(new BuildBean());
        BUILD_LIST.add(new BuildService());
        BUILD_LIST.add(new BuildServiceImpl());
        BUILD_LIST.add(new BuildQueryVoBean());
    }

    public static ResponResult generateCode(GeneratorVo generatorVo) throws Exception {
        ResponResult responResult = new ResponResult();
        final String srcPath = "src" + new Date().getTime();
        final String genrateFilePath = generatorVo.getContextPath() + srcPath;
        String packageName = generatorVo.getModelPath();
        String java = "/src/main/java/";
        String resource = "/src/main/resource/";
        String dir = packageName.replace('.', '/');
        TablesBuilder builder = new TablesBuilder();
        Map<OutPathKey, String> outPathMap = new HashMap<>();
        builder.setJdbcClass("com.mysql.jdbc.Driver");//驱动
        String connect = generatorVo.getConnection();
        String connectTemp = "jdbc:mysql://%s:%s/%s";
        if (!connect.startsWith("jdbc:mysql://")) {
            connect = String.format(connectTemp, generatorVo.getConnection(), generatorVo.getPort(), generatorVo.getDataBase());
        }
        builder.setUrl(connect);//数据库链接
        builder.setName(generatorVo.getUserId());//数据库用户名
        builder.setPwd(generatorVo.getUserPass());//数据库密码
        builder.setPojoPackage(packageName + ".entity");//pojo包地址
        builder.setVoPackage(packageName + ".vo");
        builder.setDaoPackage(packageName + ".dao");//dao包地址
        builder.setServicePackage(packageName + ".service");
        builder.setServiceImplPackage(packageName + ".service.impl");
        outPathMap.put(OutPathKey.DEFULT, genrateFilePath);
        outPathMap.put(OutPathKey.DO, genrateFilePath + java + dir + "/entity/");
        outPathMap.put(OutPathKey.VO, genrateFilePath + java + dir + "/vo/");
        outPathMap.put(OutPathKey.DAO, genrateFilePath + java + dir + "/dao/");
        outPathMap.put(OutPathKey.XML, genrateFilePath + resource + dir + "/mapper/");
        outPathMap.put(OutPathKey.SERVICE, genrateFilePath + java + dir + "/service/");
        outPathMap.put(OutPathKey.SERVICE_IMPL, genrateFilePath + java + dir + "/service/impl/");
        builder.setOutPathMap(outPathMap);//生成文件地址

        for (String table : generatorVo.getTableNames()) {
            builder.setTableName(table);//数据库表名 %全部

            List<TableWapper> tables = builder.build();
            for (TableWapper t : tables) {
                for (BuildFactory b : Mybatisorm.BUILD_LIST) {
                    b.buildTable(t);
                }
            }
        }
        // 压缩文件生成压缩包
        fileToZip(genrateFilePath, generatorVo.getContextPath() + "/tmp", srcPath);
        new Thread(new Runnable() {
            /** 执行完毕后删除冗余文件*/
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    File dirFile = new File(genrateFilePath);
                    File zipFile = new File(generatorVo.getContextPath() + "/tmp" + "/" + srcPath + ".zip");
                    deleteDir(dirFile);
                    deleteDir(zipFile);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        responResult.setRspCode("000000");
        responResult.setZipName(srcPath + ".zip");
        return responResult;
    }

    /**
     * 执行压缩操作
     *
     * @param sourceFilePath 被压缩的文件/文件夹
     */
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File file = new File(sourceFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            File baseZipPath = new File(zipFilePath);
            if (!baseZipPath.exists()) {
                baseZipPath.mkdirs();
            }
            File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
            fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compressByType(file, out, basedir);
            out.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
        return flag;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 判断是目录还是文件，根据类型（文件/文件夹）执行不同的压缩方法
     *
     * @param file
     * @param out
     * @param basedir
     */
    private static void compressByType(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            compressDirectory(file, out, basedir);
        } else {
            compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     *
     * @param dir
     * @param out
     * @param basedir
     */
    private static void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compressByType(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     *
     * @param file
     * @param out
     * @param basedir
     */
    private static void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        BufferedInputStream bis = null;
        FileInputStream fileInputStream = null;
        try {
            String[] arr = basedir.split("/");
            String dirStr = "src/";
            if (arr[0].contains("src")) {
                for (int i = 1; i < arr.length; i++) {
                    dirStr += arr[i] + "/";
                }
            }
            fileInputStream = new FileInputStream(file);
            bis = new BufferedInputStream(fileInputStream);
            ZipEntry entry = new ZipEntry(dirStr + file.getName());
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(fileInputStream);
        }
    }

}
