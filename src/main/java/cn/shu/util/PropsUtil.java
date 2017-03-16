package cn.shu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shu on 2017/3/6.
 */
//属性文件工具类
public final class PropsUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    public static Properties loadProperties(String fileName) {
        Properties properties = null;
        InputStream stream = null;

        try {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (null == stream) {
                throw new FileNotFoundException(fileName + "is Not Found");
            }
            properties = new Properties();
            properties.load(stream);
        } catch (IOException e) {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return properties;
    }

    public static String getString(Properties properties, String key) {
        return getString(properties, key, "");
    }

    //获取字符型属性，设置默认值
    public static String getString(Properties properties, String key, String defaultValue) {
        String value = defaultValue;
        if (properties.containsKey(key)) {
            value = properties.getProperty(key);
        }
        return value;
    }

    //不含默认值
    public static int getInt(Properties properties, String key) {
        return getInt(properties, key, 0);
    }

    //获取数值型属性
    public static int getInt(Properties properties, String key, int defaultValue) {
        int value = defaultValue;
        if (properties.containsKey(key)) {
            value = CastUtil.caseInt(key, defaultValue);
        }

        return value;
    }

    //不含默认值
    public static boolean getBool(Properties properties, String key) {
        return getBool(properties, key, false);
    }

    //获取boolean类型的值
    public static boolean getBool(Properties properties, String key, boolean defaultValue) {
        boolean value = false;
        if (properties.contains(key)) {
            value = CastUtil.caseBoolean(key, false);
        }
        return value;
    }
}

