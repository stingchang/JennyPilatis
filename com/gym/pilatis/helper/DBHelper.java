package com.gym.pilatis.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DBHelper {
    private static final String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/gym?characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String username = "root";
    private static final String password = "Caimima4e";

    public static DBHelper INSTANCE = new DBHelper();

    private DBHelper() {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ResultSet run(String query) {
        try {
            Connection con = DriverManager.getConnection(url, username, password);
        
                 return con.createStatement().executeQuery(query); 
//            return stmt.executeQuery(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.print("Query : '" + query + "'");
            e.printStackTrace();
        }
        return null;
    }

    // insert, delete, update
    public int runUpdate(String query) {
        int rowAffected = 0;
        try (Connection con = DriverManager.getConnection(url, username, password);
                Statement stmt = con.createStatement();) {
            rowAffected = stmt.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("Update sql failed : " + query);
        }
        return rowAffected;
    }

    /*
     * Helper functions
     */

    public String prep(String query) {
        return "'" + query + "'";
    }
    public String prep(Object query) {
        return "'" + query.toString() + "'";
    }

    public Optional<Integer> getInt(String query) {
        try (Connection con = DriverManager.getConnection(url, username, password);
                Statement stmt = con.createStatement()) {

            ResultSet r = stmt.executeQuery(query);
            r.next();
            return Optional.of(r.getInt(1));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.print("Query : '" + query + "'");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public int getIntOrDefault(String query, int defaultValue) {
        return getInt(query).orElse(defaultValue);
    }

    public Optional<Double> getDouble(String query) {
        try (Connection con = DriverManager.getConnection(url, username, password);
                Statement stmt = con.createStatement()) {
            ;

            ResultSet r = stmt.executeQuery(query);
            r.next();
            return Optional.of(r.getDouble(1));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.print("Query : '" + query + "'");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public double getDoubleOrDefault(String query, double defaultValue) {
        return getDouble(query).orElse(defaultValue);
    }

    public Optional<String> getString(String query) {
        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement stmt = con.createStatement();
             ResultSet r = stmt.executeQuery(query);) {

            r.next();
            return Optional.of(r.getString(1));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.print("Query : '" + query + "'");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getStringOrDefault(String query, String defaultValue) {
        return getString(query).orElse(defaultValue);
    }

    public Optional<Integer> getNewID(String query, String columnName) {
        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement stmt = con.createStatement();) {

            PreparedStatement preparedStatement = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                int key = rs.getInt(1);
                return Optional.of(key);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.print("Query : '" + query + "'");
            e.printStackTrace();
            return Optional.of(null);
        }
        return Optional.of(null);
    }

    public int getNewIdOrDefault(String query, int defaultValue) {
        return getInt(query).orElse(defaultValue);
    }

    public boolean hasRecord(String query) {
        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement stmt = con.createStatement();) {

            boolean hasRecord = stmt.executeQuery(query).next();
            stmt.close();
            return hasRecord;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(query);
            e.printStackTrace();
        }
        return false;
    }
}
