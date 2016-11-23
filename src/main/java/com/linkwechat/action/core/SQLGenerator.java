package com.linkwechat.action.core;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkwechat.action.annotation.Column;
import com.linkwechat.action.annotation.Id;
import com.linkwechat.action.annotation.NotDBColumn;
import com.linkwechat.action.annotation.Table;
import com.linkwechat.action.util.DateUtils;
import com.linkwechat.action.util.StringUtils;

/**
 * SQL生成器
 * 
 * @author linkwechat linkwechat@foxmail.com
 * @version 1.0
 */
public class SQLGenerator {

    private static final Log logger = LogFactory.getLog(SQLGenerator.class);

    private static String charset = "UTF-8";

    public static String getCharset() {
        return charset;
    }

    public static void setCharset(String charset) {
        SQLGenerator.charset = charset;
    }

    /**
     * 转义正则特殊字符
     * 
     * @param keyword
     *            关键词
     * @return String
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "'", "\"" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * SQL语句编码
     * 
     * @param sql
     *            待编码SQL语句
     * @return String
     */
    public static String encodeSQL(String sql) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(sql)) {
            return new String(sql.getBytes(charset), charset);
        }
        return sql;
    }

    /**
     * 获取对象中指定字段的值
     * 
     * @param obj
     *            待获取对象
     * @param fieldName
     *            字段 名称
     * @return String
     */
    public static String getObjFieldValue(Object obj, String fieldName) throws Exception {
        if (obj == null || fieldName == null || fieldName.equals("")) {
            return null;
        }

        // 将属性的首字符大写，构造get方法
        String method = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            // 调用getter方法获取属性值
            Method m = obj.getClass().getMethod(method);
            Object value = m.invoke(obj);

            String valueStr = null;
            if (value == null) {
                return "null";
            } else if (value instanceof String) {
                valueStr = "'" + escapeExprSpecialWord(String.valueOf(value)) + "'";
            } else if (value instanceof Short || value instanceof Integer || value instanceof Long
                    || value instanceof Float || value instanceof Double) {
                valueStr = String.valueOf(value);
            } else if (value instanceof Boolean) {
                if ((Boolean) value) {
                    valueStr = "1";
                } else {
                    valueStr = "0";
                }
            }
            if (value instanceof Date) {
                valueStr = "'" + DateUtils.getMySQLDate((Date) value) + "'";
            }

            return valueStr;
        } catch (Exception e) {
            logger.error("Execute " + obj.getClass().getName() + "." + method + "() error!", e);
            throw e;
        }
    }

    /**
     * 获取对象中SQL字段和取值
     * 
     * @param obj
     *            待获取对象
     * @return Map&lt;String,String&gt;
     */
    public static Map<String, String> getSQLColumns(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, String> sqlColumns = new LinkedHashMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("serialVersionUID")) {
                continue;
            } else if (field.isAnnotationPresent(NotDBColumn.class)) {
                continue;
            } else if (field.isAnnotationPresent(Column.class)) {
                String value = getObjFieldValue(obj, field.getName());
                if (value == null) {
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                String key = column.name();
                sqlColumns.put(key, value);
            } else {
                String value = getObjFieldValue(obj, field.getName());
                if (value == null) {
                    continue;
                }
                String key = field.getName();
                sqlColumns.put(key, value);
            }
        }
        return sqlColumns;
    }

    /**
     * 获取对象中SQL主键的字段和取值
     * 
     * @param obj
     *            待获取对象
     * @return Map&lt;String,String&gt;
     */
    public static Map<String, String> getSQLPrimaryColumns(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, String> sqlPrimaryColumns = new LinkedHashMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                if (field.isAnnotationPresent(NotDBColumn.class)) {
                    continue;
                } else if (field.isAnnotationPresent(Column.class)) {
                    String value = getObjFieldValue(obj, field.getName());
                    if (value == null) {
                        continue;
                    }
                    Column column = field.getAnnotation(Column.class);
                    String key = column.name();
                    sqlPrimaryColumns.put(key, value);
                } else {
                    String value = getObjFieldValue(obj, field.getName());
                    if (value == null) {
                        continue;
                    }
                    String key = field.getName();
                    sqlPrimaryColumns.put(key, value);
                }
            }
        }
        return sqlPrimaryColumns;
    }

    /**
     * 依据数据库对象创建插入的SQL
     * 
     * @param objList
     *            数据库对象列表
     * @return List&lt;String&gt;
     */
    public static List<String> createInsertSQL(List<?> objList) throws Exception {
        if (objList == null || objList.size() == 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>();
        for (Object obj : objList) {
            if (!obj.getClass().isAnnotationPresent(Table.class)) {
                continue;
            }
            Table table = obj.getClass().getAnnotation(Table.class);

            Map<String, String> sqlColumns = getSQLColumns(obj);
            if (sqlColumns == null || sqlColumns.size() == 0) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("insert into `").append(table.name()).append("` ");

            StringBuilder sbKey = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();
            for (String column : sqlColumns.keySet()) {
                sbKey.append("`").append(column).append("`, ");
                sbValue.append(sqlColumns.get(column)).append(", ");
            }

            if (sbKey.length() > 0 && sbValue.length() > 0) {
                String keys = sbKey.substring(0, sbKey.length() - 2);
                String values = sbValue.substring(0, sbValue.length() - 2);
                sb.append("(").append(keys).append(") values (").append(values).append(");");
                sqlList.add(sb.toString());
            }
        }
        return sqlList;
    }

    /**
     * 依据数据库对象创建删除的SQL
     * 
     * @param objList
     *            数据库对象列表
     * @return List&lt;String&gt;
     */
    public static List<String> createDeleteSQL(List<?> objList) throws Exception {
        if (objList == null || objList.size() == 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>();
        for (Object obj : objList) {
            if (!obj.getClass().isAnnotationPresent(Table.class)) {
                continue;
            }
            Table table = obj.getClass().getAnnotation(Table.class);

            Map<String, String> sqlPrimaryColumns = getSQLPrimaryColumns(obj);
            if (sqlPrimaryColumns == null || sqlPrimaryColumns.size() == 0) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("delete from `").append(table.name()).append("` where ");

            StringBuilder sbPrimaryKey = new StringBuilder();
            for (String column : sqlPrimaryColumns.keySet()) {
                sbPrimaryKey.append("`").append(column).append("` = ").append(sqlPrimaryColumns.get(column))
                        .append(" and");
            }

            if (sbPrimaryKey.length() > 4) {
                String keys = sbPrimaryKey.substring(0, sbPrimaryKey.length() - 4);
                sb.append(keys).append(";");
                sqlList.add(sb.toString());
            }
        }
        return sqlList;
    }

    /**
     * 依据数据库对象创建查询的SQL
     * 
     * @param objList
     *            数据库对象列表
     * @return List&lt;String&gt;
     */
    public static List<String> createSelectSQL(List<?> objList) throws Exception {
        if (objList == null || objList.size() == 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>();
        for (Object obj : objList) {
            if (!obj.getClass().isAnnotationPresent(Table.class)) {
                continue;
            }
            Table table = obj.getClass().getAnnotation(Table.class);

            Map<String, String> sqlPrimaryColumns = getSQLPrimaryColumns(obj);
            if (sqlPrimaryColumns == null || sqlPrimaryColumns.size() == 0) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("select * from `").append(table.name()).append("` where ");

            StringBuilder sbPrimaryKey = new StringBuilder();
            for (String column : sqlPrimaryColumns.keySet()) {
                sbPrimaryKey.append("`").append(column).append("` = ").append(sqlPrimaryColumns.get(column))
                        .append(" and");
            }

            if (sbPrimaryKey.length() > 4) {
                String keys = sbPrimaryKey.substring(0, sbPrimaryKey.length() - 4);
                sb.append(keys).append(";");
                sqlList.add(sb.toString());
            }
        }
        return sqlList;
    }

    /**
     * 依据数据库对象创建更新的SQL
     * 
     * @param objList
     *            数据库对象列表
     * @return List&lt;String&gt;
     */
    public static List<String> createUpdateSQL(List<?> objList) throws Exception {
        if (objList == null || objList.size() == 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>();
        for (Object obj : objList) {
            if (!obj.getClass().isAnnotationPresent(Table.class)) {
                continue;
            }
            Table table = obj.getClass().getAnnotation(Table.class);

            Map<String, String> sqlColumns = getSQLColumns(obj);
            if (sqlColumns == null || sqlColumns.size() == 0) {
                continue;
            }

            Map<String, String> sqlPrimaryColumns = getSQLPrimaryColumns(obj);
            if (sqlPrimaryColumns == null || sqlPrimaryColumns.size() == 0) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("update `").append(table.name()).append("` set ");

            // 依据SQL主键生成查询条件同时移除SQL主键的更新
            StringBuilder sbPrimaryKey = new StringBuilder();
            for (String column : sqlPrimaryColumns.keySet()) {
                sbPrimaryKey.append("`").append(column).append("` = ").append(sqlPrimaryColumns.get(column))
                        .append(" and");
                sqlColumns.remove(column);
            }

            StringBuilder sbKey = new StringBuilder();
            for (String column : sqlColumns.keySet()) {
                sbKey.append("`").append(column).append("` = ").append(sqlColumns.get(column)).append(", ");
            }

            if (sbKey.length() > 0 && sbPrimaryKey.length() > 4) {
                String keys1 = sbKey.substring(0, sbKey.length() - 2);
                String keys2 = sbPrimaryKey.substring(0, sbPrimaryKey.length() - 4);
                sb.append(keys1).append(" where ").append(keys2).append(";");
                sqlList.add(sb.toString());
            }
        }
        return sqlList;
    }
}
