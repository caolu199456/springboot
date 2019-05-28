package com.example.util.common;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CL on 2016/10/14.
 */
public class JDBCUtils {
    private static String url = "jdbc:mysql://192.168.1.211:3306/changchong_pay";
    private static String name = "com.mysql.jdbc.Driver";
    private static String user = "changchong";
    private static String password = "123456";

    public static void config(String url, String driverName, String user, String password) {
        JDBCUtils.url = url;
        JDBCUtils.name = driverName;
        JDBCUtils.user = user;
        JDBCUtils.password = password;
    }

    public static boolean isActive(){
        Connection connection = null;
        try {
            Class.forName(name);
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            return false;
        }finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(name);
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }
    /**
     * 创建一张表
     * @param connection 数据库连接 需要手动关闭
     * @param createTableSql 创建表的句子  例如create table user(id varchar(255) primary key,name varchar(255),age int)
     * @throws Exception
     */
    public static void createTable(Connection connection,String createTableSql) throws Exception {
        Statement statement = null;

        try {
            statement = connection.createStatement();
            statement.executeUpdate(createTableSql);//创建一个表，两列
        } catch (SQLException e) {
            throw new Exception("创建SQLite表失败");
        }finally {
            closeConn(null, statement, null);
        }
    }

    /**
     * 增删改
     * @param connection 数据库连接 需要手动关闭
     * @param sql 增删改的句子  例如insert into user(id,name,age) values(?,?,?)
     * @param params 值["1","zhang",10]
     * @throws Exception
     */
    public static void executeUpdate(Connection connection,String sql,Object[] params) throws Exception {
        PreparedStatement preparedStatement = null;
        connection.setAutoCommit(false);
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                preparedStatement.setObject(i+1,params[i]);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("更新SQLite数据表失败");
        }finally {
            connection.commit();
            closeConn(null, preparedStatement, null);
        }
    }
    /**
     * 批量增删改
     * @param connection 数据库连接 需要手动关闭
     * @param sql 增删改的句子  例如insert into user(id,name,age) values(?,?,?)
     * @param params 值[["1","zhang",10],["1","cao",100]]
     * @throws Exception
     */
    public static void batchExecuteUpdate(Connection connection,String sql,Object[][] params) throws Exception {
        PreparedStatement preparedStatement = null;
        connection.setAutoCommit(false);
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                for (int j = 0; params[j] != null && j < params[i].length; j++) {
                    preparedStatement.setObject(j + 1, params[i][j]);

                }
                preparedStatement.addBatch();
                if (i % 100 == 0) {
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("更新SQLite数据表失败");
        }finally {
            connection.commit();
            closeConn(null, preparedStatement, null);
        }
    }
    /**
     * 查询一条记录或者多条记录
     * @param connection 数据库连接 需要手动关闭
     * @param sql 查询的句子  例如select * from user where id=?
     * @param params 值["1"]
     * @throws Exception
     */
    public static List<Map<String,Object>> find(Connection connection, String sql, Object[] params) throws Exception {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {

                preparedStatement.setObject(i+1, params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rss = resultSet.getMetaData();
            int columnCount = rss.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> dataMap = new LinkedHashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    Object object = resultSet.getObject(i+1);
                    dataMap.put(rss.getColumnName(i+1), object);
                }
                results.add(dataMap);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("查询SQLite数据表失败");
        }finally {
            closeConn(null,preparedStatement,resultSet);
            return results;
        }
    }

    public static void closeConn(Connection connection,Statement st,ResultSet rs) throws SQLException {
        if (connection != null) {
            connection.close();
        }
        if (rs != null) {
            rs.close();
        }
        if (st != null) {
            st.close();
        }
    }
    public static void main(String[] args) throws Exception {
        Connection conn = getConnection();
        List<Map<String, Object>> maps = find(conn, "select * from admin", null);
        closeConn(conn,null,null);
    }

}
