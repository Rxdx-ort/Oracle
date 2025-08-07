package com.mycompany.gestorbd2;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EstudianteFrame extends JFrame {
    private JTextField txtNombre, txtApe1, txtApe2, txtCorreo, txtCarrera;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar;
    private int idSeleccionado = -1;

    public EstudianteFrame() {
        FlatLightLaf.install();
        setTitle("Gestión de Estudiantes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20,20,20,20));
        formPanel.setBackground(new Color(245,245,245));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        String[] labels = { "Nombre", "Apellido Paterno", "Apellido Materno", "Correo", "Carrera" };
        JTextField[] fields = new JTextField[5];
        for(int i=0; i<5; i++) {
            c.gridy = i;
            c.gridx = 0;
            formPanel.add(new JLabel(labels[i] + ":"), c);
            c.gridx = 1;
            formPanel.add(fields[i] = new JTextField(), c);
        }
        txtNombre = fields[0]; txtApe1 = fields[1]; txtApe2 = fields[2];
        txtCorreo = fields[3]; txtCarrera = fields[4];

        JPanel formButtons = new JPanel();
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        formButtons.add(btnNuevo);
        formButtons.add(btnGuardar);
        formButtons.add(btnEditar);
        formButtons.add(btnEliminar);
        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        formPanel.add(formButtons, c);

        model = new DefaultTableModel(new Object[]{"ID","Nombre","Apellido1","Apellido2","Correo","Carrera"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);

        listarEstudiantes();

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });
    }

    private void listarEstudiantes() {
        model.setRowCount(0);
        try (Connection con = new ConexionOracle().conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_Estudiante, Nombre, Apellido1, Apellido2, Correo, Carrera FROM Estudiante")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_Estudiante"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido1"),
                        rs.getString("Apellido2"),
                        rs.getString("Correo"),
                        rs.getString("Carrera")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    private void guardar() {
        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
            return;
        }
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Estudiante (Nombre, Apellido1, Apellido2, Correo, Carrera) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtApe1.getText());
            ps.setString(3, txtApe2.getText());
            ps.setString(4, txtCorreo.getText());
            ps.setString(5, txtCarrera.getText());
            ps.executeUpdate();
            listarEstudiantes();
            limpiar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante para editar.");
            return;
        }
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Estudiante SET Nombre=?, Apellido1=?, Apellido2=?, Correo=?, Carrera=? WHERE id_Estudiante=?")) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtApe1.getText());
            ps.setString(3, txtApe2.getText());
            ps.setString(4, txtCorreo.getText());
            ps.setString(5, txtCarrera.getText());
            ps.setInt(6, idSeleccionado);
            ps.executeUpdate();
            listarEstudiantes();
            limpiar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante para eliminar.");
            return;
        }
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps1 = con.prepareStatement("DELETE FROM Inscripcion WHERE Estudiante=?");
             PreparedStatement ps2 = con.prepareStatement("DELETE FROM Estudiante WHERE id_Estudiante=?")) {
            ps1.setInt(1, idSeleccionado);
            ps1.executeUpdate();
            ps2.setInt(1, idSeleccionado);
            ps2.executeUpdate();
            listarEstudiantes();
            limpiar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void seleccionarFila() {
        int row = table.getSelectedRow();
        if (row != -1) {
            idSeleccionado = (int) model.getValueAt(row, 0);
            txtNombre.setText((String) model.getValueAt(row, 1));
            txtApe1.setText((String) model.getValueAt(row, 2));
            txtApe2.setText((String) model.getValueAt(row, 3));
            txtCorreo.setText((String) model.getValueAt(row, 4));
            txtCarrera.setText((String) model.getValueAt(row, 5));
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtApe1.setText("");
        txtApe2.setText("");
        txtCorreo.setText("");
        txtCarrera.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstudianteFrame().setVisible(true));
    }
}