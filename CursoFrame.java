package com.mycompany.gestorbd2;


import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CursoFrame extends JFrame {
    private JTextField txtNombre, txtCodigo;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar;
    private int idSeleccionado = -1;

    public CursoFrame() {
        FlatLightLaf.install();
        setTitle("Gestión de Cursos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20,20,20,20));
        formPanel.setBackground(new Color(245,245,245));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridx = 0; c.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), c);
        c.gridx = 1;
        formPanel.add(txtNombre = new JTextField(), c);

        c.gridx = 0; c.gridy = 1;
        formPanel.add(new JLabel("Código:"), c);
        c.gridx = 1;
        formPanel.add(txtCodigo = new JTextField(), c);

        // Form buttons
        JPanel btnPanel = new JPanel();
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnPanel.add(btnNuevo);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        c.gridy = 2; c.gridx = 0; c.gridwidth = 2;
        formPanel.add(btnPanel, c);

        // Table panel
        model = new DefaultTableModel(new Object[]{"ID","Nombre","Código"},0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        add(panel);

        listarCursos();

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                if(!e.getValueIsAdjusting()) seleccionarFila();
            }
        });
    }

    private void listarCursos() {
        model.setRowCount(0);
        try (Connection con = new ConexionOracle().conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_curso, Nombre, Codigo FROM Curso")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_curso"),
                    rs.getString("Nombre"),
                    rs.getString("Codigo")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardar() {
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Curso (Nombre, Codigo) VALUES (?, ?)")) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCodigo.getText());
            ps.executeUpdate();
            listarCursos();
            limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) return;
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Curso SET Nombre=?, Codigo=? WHERE id_curso=?")) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCodigo.getText());
            ps.setInt(3, idSeleccionado);
            ps.executeUpdate();
            listarCursos();
            limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        // Eliminar inscripciones de este curso
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Inscripcion WHERE curso=?")) {
            ps.setInt(1, idSeleccionado);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Eliminar curso
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Curso WHERE id_curso=?")) {
            ps.setInt(1, idSeleccionado);
            ps.executeUpdate();
            listarCursos();
            limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seleccionarFila() {
        int row = table.getSelectedRow();
        if (row != -1) {
            idSeleccionado = (int) model.getValueAt(row, 0);
            txtNombre.setText((String) model.getValueAt(row, 1));
            txtCodigo.setText((String) model.getValueAt(row, 2));
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtCodigo.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CursoFrame().setVisible(true));
    }
}