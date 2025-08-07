package com.mycompany.gestorbd2;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class App extends JFrame {

    public App() {
        FlatLightLaf.install();
        setTitle("Sistema de Gestión Escolar");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Crear instancias de tus ventanas originales
        EstudianteFrame estudiantes = new EstudianteFrame();
        CursoFrame cursos = new CursoFrame();
        InscripcionFrame inscripciones = new InscripcionFrame();

        // Ocultar las ventanas independientes
        estudiantes.setVisible(false);
        cursos.setVisible(false);
        inscripciones.setVisible(false);

        // Agregar sus contenidos como pestañas
        tabs.add("Estudiantes", estudiantes.getContentPane());
        tabs.add("Cursos", cursos.getContentPane());
        tabs.add("Inscripciones", inscripciones.getContentPane());

        setContentPane(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
