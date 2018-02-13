package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectDB {
    // 싱글톤 패턴으로 사용 하기위 한 코드들
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public ConnectDB() {

    }

    private String jdbcUrl = "jdbc:mysql://localhost:3306/DB이름"; // MySQL 계정 "jdbc:mysql://localhost:3306/DB이름"
    private String dbId = "자신의 계정"; // MySQL 계정 "로컬일 경우 root"
    private String dbPw = "자신의 비밀번호"; // 비밀번호 "mysql 설치 시 설정한 비밀번호"
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private PreparedStatement pstmt2 = null;
    private ResultSet rs = null;
    private String sql = "";
    private String sql2 = "";
    String returns = "";
    String returns2 = "";

    // 데이터베이스와 통신하기 위한 코드가 들어있는 메서드
    public String joindb(String id, String pwd) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            sql = "select id from oc22table where id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("id").equals(id)) { // 이미 아이디가 있는 경우
                    returns = "id";
                }
            } else { // 입력한 아이디가 없는 경우
                sql2 = "insert into oc22table values(?,?)";
                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, id);
                pstmt2.setString(2, pwd);
                pstmt2.executeUpdate();

                returns = "ok";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    public String logindb(String id, String pwd) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            sql = "select id,pw from oc22table where id=? and pw=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("id").equals(id) && rs.getString("pw").equals(pwd)) {
                    returns2 = "true";// 로그인 가능
                } else {
                    returns2 = "false"; // 로그인 실패
                }
            } else {
                returns2 = "noId"; // 아이디 또는 비밀번호 존재 X
            }

        } catch (Exception e) {

        } finally {if (rs != null)try {rs.close();} catch (SQLException ex) {}
            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
        }
        return returns2;
    }
}