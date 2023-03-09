/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redkale.demo.base;

import java.io.*;
import java.util.*;

import org.redkale.service.Service;
import org.redkale.source.*;

import static org.redkale.source.AbstractDataSource.*;

import org.redkale.util.AnyValue.DefaultAnyValue;

/**
 * @author zhangjx
 */
public class AutoClassCreator {

    private static final String currentPkg = AutoClassCreator.class.getPackage().getName();

    private static final String jdbc_url = "jdbc:mysql://localhost:3306/redemo_platf?autoReconnect=true&amp;characterEncoding=utf8";//数据库url

    private static final String jdbc_user = "root"; //数据库用户名

    private static final String jdbc_pwd = ""; //数据库密码

    public static void main(String[] args) throws Exception {
        //与base同级的包名
        String pkg = currentPkg.substring(0, currentPkg.lastIndexOf('.') + 1) + "user";
        //类名
        final String entityClass = "UserDetail";
        //父类名
        final String superEntityClass = "";
        //Entity内容
        loadEntity(pkg, entityClass, superEntityClass);
    }

    private static void loadEntity(String pkg, String className, String superClassName) throws Exception {
        //源码内容
        String entityBody = createEntityContent(pkg, className, superClassName);
        final File entityFile = new File("src/" + pkg.replace('.', '/') + "/" + className + ".java");
        if (entityFile.isFile()) {
            throw new RuntimeException(className + ".java 已经存在");
        }
        FileOutputStream out = new FileOutputStream(entityFile);
        out.write(entityBody.getBytes("UTF-8"));
        out.close();
    }

    private static String createEntityContent(String pkg, String className, String superClassName) throws Exception {
        DefaultAnyValue prop = new DefaultAnyValue();
        prop.addValue(DATA_SOURCE_URL, jdbc_url);
        prop.addValue(DATA_SOURCE_USER, jdbc_user);
        prop.addValue(DATA_SOURCE_PASSWORD, jdbc_pwd);
        DataSqlSource source = new DataJdbcSource();
        ((Service) source).init(prop);

        final StringBuilder sb = new StringBuilder();
        final StringBuilder tostring = new StringBuilder();
        final StringBuilder tableComment = new StringBuilder();
        final Map<String, String> uniques = new HashMap<>();
        final Map<String, String> indexes = new HashMap<>();
        final List<String> columns = new ArrayList<>();
        final Set<String> superColumns = new HashSet<>();
        source.directQuery("SHOW CREATE TABLE " + className.toLowerCase(), (DataResultSet tcs) -> {
            try {
                tcs.next();
                final String createSql = (String) tcs.getObject(2);
                for (String str : createSql.split("\n")) {
                    str = str.trim();
                    if (str.startsWith("`")) {
                        str = str.substring(str.indexOf('`') + 1);
                        columns.add(str.substring(0, str.indexOf('`')));
                    } else if (str.startsWith("UNIQUE KEY ")) {
                        str = str.substring(str.indexOf('`') + 1);
                        uniques.put(str.substring(0, str.indexOf('`')), str.substring(str.indexOf('(') + 1, str.indexOf(')')));
                    } else if (str.startsWith("KEY ")) {
                        str = str.substring(str.indexOf('`') + 1);
                        indexes.put(str.substring(0, str.indexOf('`')), str.substring(str.indexOf('(') + 1, str.indexOf(')')));
                    }
                }
                int pos = createSql.indexOf("COMMENT='");
                if (pos > 0) {
                    tableComment.append(createSql.substring(pos + "COMMENT='".length(), createSql.lastIndexOf('\'')));
                } else {
                    tableComment.append("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        });

        if (superClassName != null && !superClassName.isEmpty()) {
            source.directQuery("SELECT * FROM information_schema.columns WHERE  table_name = '" + superClassName.toLowerCase() + "'", (DataResultSet rs) -> {
                try {
                    while (rs.next()) {
                        superColumns.add((String) rs.getObject("COLUMN_NAME"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            });
        }
        source.directQuery("SELECT * FROM information_schema.columns WHERE  table_name = '" + className.toLowerCase() + "'", (DataResultSet rs) -> {
            try {
                sb.append("package " + pkg + ";" + "\r\n\r\n");
                sb.append("import org.redkale.persistence.*;\r\n");
                //sb.append("import org.redkale.util.*;\r\n");
                if (superClassName == null || superClassName.isEmpty()) {
                    try {
                        Class.forName("org.redkale.demo.base.BaseEntity");
                        sb.append("import org.redkale.demo.base.BaseEntity;\r\n");
                    } catch (Throwable t) {
                        sb.append("import org.redkale.convert.json.*;\r\n");
                        tostring.append("\r\n    @Override\r\n    public String toString() {\r\n");
                        tostring.append("        return JsonConvert.root().convertTo(this);\r\n");
                        tostring.append("    }\r\n");
                    }
                }
                sb.append("\r\n/**\r\n"
                        + " *\r\n"
                        + " * @author " + System.getProperty("user.name") + "\r\n"
                        + " */\r\n");
                //if (classname.contains("Info")) sb.append("@Cacheable\r\n");
                sb.append("@Table(comment = \"" + tableComment + "\"");
                if (!uniques.isEmpty()) {
                    sb.append("\r\n        , uniqueConstraints = {");
                    boolean first = true;
                    for (Map.Entry<String, String> en : uniques.entrySet()) {
                        if (!first) {
                            sb.append(", ");
                        }
                        sb.append("@UniqueConstraint(name = \"" + en.getKey() + "\", columnNames = {" + en.getValue().replace('`', '"') + "})");
                        first = false;
                    }
                    sb.append("}");
                }
                if (!indexes.isEmpty()) {
                    sb.append("\r\n        , indexes = {");
                    boolean first = true;
                    for (Map.Entry<String, String> en : indexes.entrySet()) {
                        if (!first) {
                            sb.append(", ");
                        }
                        sb.append("@Index(name = \"" + en.getKey() + "\", columnList = \"" + en.getValue().replace("`", "") + "\")");
                        first = false;
                    }
                    sb.append("}");
                }
                sb.append(")\r\n");
                sb.append("public class " + className
                        + (superClassName != null && !superClassName.isEmpty() ? (" extends " + superClassName) : (tostring.length() == 0 ? " extends BaseEntity" : " implements java.io.Serializable")) + " {\r\n\r\n");
                Map<String, StringBuilder> columnMap = new HashMap<>();
                Map<String, StringBuilder> getsetMap = new HashMap<>();
                while (rs.next()) {
                    String column = (String) rs.getObject("COLUMN_NAME");
                    String type = ((String) rs.getObject("DATA_TYPE")).toUpperCase();
                    String remark = (String) rs.getObject("COLUMN_COMMENT");
                    String def = (String) rs.getObject("COLUMN_DEFAULT");
                    String key = (String) rs.getObject("COLUMN_KEY");
                    StringBuilder fieldSB = new StringBuilder();
                    if (key != null && key.contains("PRI")) {
                        fieldSB.append("    @Id");
                    } else if (superColumns.contains(column)) {  //跳过被继承的重复字段
                        continue;
                    }
                    fieldSB.append("\r\n");

                    int length = 0;
                    int precision = 0;
                    int scale = 0;
                    String ctype = "NULL";
                    String precisionStr = (String) rs.getObject("NUMERIC_PRECISION");
                    String scaleStr = (String) rs.getObject("NUMERIC_SCALE");
                    if ("INT".equalsIgnoreCase(type)) {
                        ctype = "int";
                    } else if ("BIGINT".equalsIgnoreCase(type)) {
                        ctype = "long";
                    } else if ("SMALLINT".equalsIgnoreCase(type)) {
                        ctype = "short";
                    } else if ("FLOAT".equalsIgnoreCase(type)) {
                        ctype = "float";
                    } else if ("DECIMAL".equalsIgnoreCase(type)) {
                        ctype = "float";
                        precision = precisionStr == null ? 0 : Integer.parseInt(precisionStr);
                        scale = scaleStr == null ? 0 : Integer.parseInt(scaleStr);
                    } else if ("DOUBLE".equalsIgnoreCase(type)) {
                        ctype = "double";
                        precision = precisionStr == null ? 0 : Integer.parseInt(precisionStr);
                        scale = scaleStr == null ? 0 : Integer.parseInt(scaleStr);
                    } else if ("VARCHAR".equalsIgnoreCase(type)) {
                        ctype = "String";
                        String maxsize = (String) rs.getObject("CHARACTER_MAXIMUM_LENGTH");
                        length = maxsize == null ? 0 : Integer.parseInt(maxsize);
                    } else if (type.contains("TEXT")) {
                        ctype = "String";
                    } else if (type.contains("BLOB")) {
                        ctype = "byte[]";
                    }
                    fieldSB.append("    @Column(");
                    if ("createTime".equals(column)) {
                        fieldSB.append("updatable = false, ");
                    }
                    if (length > 0) {
                        fieldSB.append("length = ").append(length).append(", ");
                    }
                    if (precision > 0) {
                        fieldSB.append("precision = ").append(precision).append(", ");
                    }
                    if (scale > 0) {
                        fieldSB.append("scale = ").append(scale).append(", ");
                    }
                    fieldSB.append("comment = \"" + remark.replace('"', '\'') + "\")\r\n");

                    fieldSB.append("    private " + ctype + " " + column);
                    if (def != null && !"0".equals(def)) {
                        String d = def.replace('\'', '\"');
                        fieldSB.append(" = ").append(d.isEmpty() ? "\"\"" : d.toString());
                        if ("float".equals(ctype)) {
                            fieldSB.append("f");
                        }
                    } else if ("String".equals(ctype)) {
                        fieldSB.append(" = \"\"");
                    }
                    fieldSB.append(";\r\n");

                    char[] chs2 = column.toCharArray();
                    chs2[0] = Character.toUpperCase(chs2[0]);
                    String sgname = new String(chs2);

                    StringBuilder setGetSB = new StringBuilder();
                    setGetSB.append("\r\n    public void set" + sgname + "(" + ctype + " " + column + ") {\r\n");
                    setGetSB.append("        this." + column + " = " + column + ";\r\n");
                    setGetSB.append("    }\r\n");

                    setGetSB.append("\r\n    public " + ctype + " get" + sgname + "() {\r\n");
                    setGetSB.append("        return this." + column + ";\r\n");
                    setGetSB.append("    }\r\n");
                    columnMap.put(column, fieldSB);
                    getsetMap.put(column, setGetSB);
                }
                List<StringBuilder> list = new ArrayList<>();
                for (String column : columns) {
                    if (superColumns.contains(column)) {
                        continue;
                    }
                    list.add(columnMap.get(column));
                }
                for (String column : columns) {
                    if (superColumns.contains(column)) {
                        continue;
                    }
                    list.add(getsetMap.get(column));
                }
                for (StringBuilder item : list) {
                    sb.append(item);
                }
                sb.append(tostring);
                sb.append("}\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        });
        return sb.toString();
    }
}
