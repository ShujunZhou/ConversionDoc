package cn.shu.dealwithdata;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by shu on 2017/3/16.
 */
public class ParseData {

    private static int rows;
    private static int columns;

    public static <T> void newWordDoc(Class<T> tClass, List<T> objList, String filename) throws Exception {
        //构建文档对象
        XWPFDocument document = createDoc();

        //构建表格
        XWPFTable xTable = createTable(tClass, objList, document);
        //设置表头数据
        createTableHeader(tClass, xTable);
        //插入正文数据
        insertContext(tClass, xTable, objList);

        //写入文件
        String separator = File.separator;
        FileOutputStream fos = new FileOutputStream(new File("d:" + separator + "java&wordTest", filename + ".doc"));
        document.write(fos);
        fos.close();
    }


    //创建文档对象
    private static <T> XWPFDocument createDoc() {
        XWPFDocument document = new XWPFDocument();
        return document;
    }


    //构建表格
    private static <T> XWPFTable createTable(Class<T> tClass, List<T> objList, XWPFDocument xd) {
        tableRowsColumns(tClass, objList); //设置表格的行数和列数
        XWPFTable xTable = xd.createTable(rows + 1, columns); //需要多构造一行，用于表头

        return xTable;
    }

    //创建表头
    private static  <T> void createTableHeader(Class<T> tClass, XWPFTable xTable) {
        Field[] fields = tClass.getDeclaredFields(); //获取字段集合
        XWPFTableRow xRow = xTable.getRow(0); //获取表格第一行

        //填充表头信息。
        for (int i = 0; i < fields.length; ++i) {
            xRow.getCell(i).setText(fields[i].getName());
        }
    }

    //插入数据库正文
    private static <T> void insertContext(Class<T> tClass, XWPFTable xTable, List<T> objList) {
        tableRowsColumns(tClass, objList);
        Field[] fields = tClass.getDeclaredFields();

        try {
            XWPFTableRow xRow;
            for (int i = 0; i < rows - 1; ++i) {
                xRow = xTable.getRow(i + 1); //获取行从1 到 rows - 2(包含rows-2);
                T obj = objList.get(i);   //从0到rows - 2,最后一行单独处理
                for (int j = 0; j < columns; ++j) {
                    fields[j].setAccessible(true);
                    xRow.getCell(j).setText(fields[j].get(obj) + "");
                }
            }

            // 单独处理最后一行
            T obj = objList.get(rows -1);
            xRow = xTable.getRow(rows); //获取最后一行
            for (int j = 0; j < columns; ++j) {
                xRow.getCell(j).setText("\r\n" + fields[j].get(obj) + "");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //获取行数和列数
    private static <T> void tableRowsColumns(Class<T> tClass, List<T> objList) {
        Field[] fields = tClass.getDeclaredFields();
        rows = objList.size();
        columns = fields.length;
    }
}
