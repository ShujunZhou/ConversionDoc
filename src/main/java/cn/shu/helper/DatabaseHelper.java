package cn.shu.helper;

import cn.shu.util.CollectionUtil;
import cn.shu.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by shu on 2017/3/8.
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    private final static QueryRunner QUERY_RUNNER;
    private static final BasicDataSource DATA_SOURCE;
    //确保一个线程只有一个Connection，使用ThreadLocal来存放本地线程变量。
    // 也就是说，将当前线程中的Connection放入ThreadLocal来存放本地线程变量
    private final static ThreadLocal<Connection> CONNECTION_HOLDER;

    static {
        CONNECTION_HOLDER = new ThreadLocal<>();

        QUERY_RUNNER = new QueryRunner();

        Properties properties = PropsUtil.loadProperties("config.properties");
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }

    //获取连接
    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        try {
            if (null == conn) {
                conn = DATA_SOURCE.getConnection();
            }
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
            throw new RuntimeException(e);
        } finally {
            CONNECTION_HOLDER.set(conn); //很重要。通过ThreadLocal管理连接
        }
        return conn;
    }
    //使用数据库连接池后，不需要关闭方法
//    //关闭链接
//    private static void closeConnection() {
//        Connection connection = CONNECTION_HOLDER.get();
//        if (null != connection) {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                LOGGER.error("close connection failure", e);
//            } finally {
//                CONNECTION_HOLDER.remove();
//            }
//        }
//    }

    //查询实体列表
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> list = null;

        Connection conn = getConnection();
        try {
            list = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entry failure", e);
            throw new RuntimeException(e);
        }

        return list;
    }

    //多表的联结查询
    public List<Map<String, Object>> queryEntityMap(String sql, Object... params) {
        List<Map<String, Object>> maps = null;

        Connection conn = getConnection();
        try {
            maps = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        }

        return maps;
    }

    //查询单个实体
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entry = null;

        Connection conn = getConnection();
        try {
            entry = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entry failure", e);
            throw new RuntimeException(e);
        }

        return entry;
    }

    //更新操作，包括删除，更新，插入
    private static int updateEntity(String sql, Object... params) {
        int rows = 0;
        Connection conn = getConnection();
        try {
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("update failure", e);
            throw new RuntimeException(e);
        }

        return rows;
    }

    //插入实体
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity : fieldMap is empty");
            return false;
        }

        String sql = "INSERT INTO " + getTableName(entityClass);
        //insert into table(id, name, email) values(?, ?, ?, ?);
        StringBuilder column = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String key : fieldMap.keySet()) {
            column.append(key).append(", ");
            values.append("?").append(", ");
        }

        column.replace(column.lastIndexOf(", "), column.length(), ")"); //替换最后一个多余的逗号
        values.replace(values.lastIndexOf(", "), values.length(), ")"); //同上

        sql += column + " VALUES" + values;

        Object[] params = fieldMap.keySet().toArray();//转换为数组

        return (updateEntity(sql, params) == 1);
    }

    //更新实体
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity : fieldMap is empty");
            return false;
        }

        //update customer set name = ? where id = ?;
        String sql = "update " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();

        for (String key : fieldMap.keySet()) {
            if (!key.equals("id")) {
                columns.append(key).append("=?, ");
            }
        }

        columns.replace(columns.lastIndexOf(","), columns.length(), "");

        sql += columns + "where id = ?";
        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return (updateEntity(sql, params) == 1);
    }


    //删除实体(批量删除)
    public static <T> boolean deleteEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not delete entity : fieldMap is empty");
            return false;
        }
        //delete from customer where id = ?, name=?;
        String sql = "delete from " + getTableName(entityClass) + " where ";
        StringBuilder values = new StringBuilder();
        for (String key : fieldMap.keySet()) {
            values.append(key).append("=?, ");
        }

        values.replace(values.lastIndexOf(","), values.length(), "");
        List<Object> list = new ArrayList<>();
        list.addAll(fieldMap.values());
        Object[] params = list.toArray();

        return (updateEntity(sql, params) == 1);
    }

    //根据id删除数据
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "delete from " + getTableName(entityClass) + " where id =?";
        return (updateEntity(sql, id) == 1);
    }

    //返回表名
    private static <T> String getTableName(Class<T> entityClass) {
        //返回在源代码中给定的基础类的简单名称。 如果底层类是匿名的，则返回一个空字符串。
        return entityClass.getSimpleName();
    }

    //执行sql文件。做测试用
    public static void executeSqlFile(String filePath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));

        try {
            String sql;
            while ((sql = bf.readLine()) != null) {
                updateEntity(sql);
            }
        } catch (IOException e) {
            LOGGER.error("SQL execute failure", e);
            throw new RuntimeException(e);
        }
    }
}
