package com.mycompany.gestorbd2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionOracle {

    private final String DRIVER = "oracle.jdbc.OracleDriver";
    private final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String USER = "RODOLFO_ORTEGA";
    private final String PASSWORD = "123RODO";

    public Connection conectar() {
        try {
            Class.forName(DRIVER);
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            conexion.setAutoCommit(true); // ¡NECESARIO!
            return conexion;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
            return null;
        }
    }
}