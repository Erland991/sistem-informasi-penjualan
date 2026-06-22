package com.mycompany.sisteminformasipenjualan;

import java.sql.*;
import javax.swing.JOptionPane;

public class Koneksi {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB   = "db_tokoberkahjaya";
    private static final String USER = "root";
    private static final String PASS = "";  // ganti jika ada password

    public static Connection getKoneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB
                       + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
            return DriverManager.getConnection(url, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Koneksi database GAGAL!\n" + e.getMessage(),
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
