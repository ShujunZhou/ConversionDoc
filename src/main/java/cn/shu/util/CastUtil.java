package cn.shu.util;

/**
 * Created by shu on 2017/3/6.
 */
//转换操做工具类
public final class CastUtil {

    //转为String类
    public static String caseString(Object obj) {
        return caseString(obj, "");
    }

    //转为String类（提供默认值）
    public static String caseString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    //转为double类
    public static Double caseDouble(Object obj) {
        return caseDouble(obj, 0);
    }

    //转为double类型（提供默认值）
    public static double caseDouble(Object obj, double defaultValue) {
        double resultDouble = defaultValue;
        if (obj != null) {
            String caseDouble = caseString(obj); //可能返回字符串或者空字符串
            if (StringUtil.isNotEmpty(caseDouble)) {
                try {
                    resultDouble = Double.parseDouble(caseDouble);
                } catch (NumberFormatException e) { //合理的处理异常
                    resultDouble = defaultValue;
                }
            }
        }
        return resultDouble;
    }


    //转为long类型
    public static long caseLong(Object obj) {
        return caseLong(obj, 0);
    }

    //转为long类型(提供默认值)
    public static long caseLong(Object obj, long defaultValue) {
        long resultLong = defaultValue;

        if (obj != null) {
            String caseLong = caseString(obj); //先解析成字符串，然后在转换类型
            System.out.println("===" + caseLong);
            if (StringUtil.isNotEmpty(caseLong)) {
                try {
                    resultLong = Long.parseLong(caseLong);
                } catch (NumberFormatException e) {
                    resultLong = defaultValue;
                }
            }
        }
        return resultLong;
    }

    public static int caseInt(Object obj) {
        return caseInt(obj, 0);
    }

    //转为int类型（提供默认值）
    public static int caseInt(Object obj, int defaultValue) {
        int resultInt = defaultValue;
        if (obj != null) {
            String caseInt = caseString(obj);
            if (StringUtil.isNotEmpty(caseInt)) {
                try {
                    resultInt = Integer.parseInt(caseInt);
                } catch (NumberFormatException e) {
                    resultInt = defaultValue;
                }
            }
        }
        return resultInt;
    }

    //转为boolean
    public static boolean caseBoolean(Object obj) {
        return caseBoolean(obj, false);
    }

    //转为boolean类型(包含默认值)
    public static boolean caseBoolean(Object obj, boolean defaultValue) {
        boolean resultValue = defaultValue;
        String strim= "adas";

        if  (obj != null) {
            resultValue = Boolean.parseBoolean(caseString(obj));
        }

        return resultValue;
    }
}
