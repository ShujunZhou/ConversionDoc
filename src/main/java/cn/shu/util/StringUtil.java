package cn.shu.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by shu on 2017/3/8.
 */
//字符串工具类
public final class StringUtil {
    //判断字符串是否为空
    public final static boolean isEmpty(String str) {
        if (null != str) {
            str = str.trim(); //删除字符串的前置空格和后置空格
        }
        return StringUtils.isEmpty(str);
    }

    //判断字符串是否非空
    public final static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
