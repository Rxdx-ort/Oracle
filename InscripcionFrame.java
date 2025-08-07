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
import java.text.SimpleDateFormat;

public class InscripcionFrame extends JFrame {
    private JComboBox<Item> cbEstudiante, cbCurso;
    private JSpinner spFecha;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnNuevo, btnGuardar, btnEliminar;
    private int idSeleccionado = -1;

    public InscripcionFrame() {
        FlatLightLaf.install();
        setTitle("Gestión de Inscripciones");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700,500);
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

        cbEstudiante = new JComboBox<>();
        cbCurso = new JComboBox<>();
        spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "yyyy-MM-dd"));
        cargarCombos();

        String[] labels = {"Estudiante", "Curso", "Fecha"};
        JComponent[] fields = {cbEstudiante, cbCurso, spFecha};
        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            formPanel.add(new JLabel(labels[i] + ":"), c);
            c.gridx = 1;
            formPanel.add(fields[i], c);
        }

        JPanel btnPanel = new JPanel();
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnPanel.add(btnNuevo);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnEliminar);
        c.gridy = labels.length; c.gridx = 0; c.gridwidth = 2;
        formPanel.add(btnPanel, c);

        // Table panel
        model = new DefaultTableModel(new Object[]{"ID","Estudiante","Curso","Fecha"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        add(panel);

        listarInscripciones();

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                if(!e.getValueIsAdjusting()) seleccionarFila();
            }
        });
    }

    private void cargarCombos() {
        try (Connection con = new ConexionOracle().conectar();
             Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT id_Estudiante, Nombre, Apellido1 FROM Estudiante");
            while (rs.next()) {
                cbEstudiante.addItem(new Item(rs.getInt("id_Estudiante"), rs.getString("Nombre")+" "+rs.getString("Apellido1")));
            }
            rs = st.executeQuery("SELECT id_curso, Nombre FROM Curso");
            while (rs.next()) {
                cbCurso.addItem(new Item(rs.getInt("id_curso"), rs.getString("Nombre")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarInscripciones() {
        model.setRowCount(0);
        try (Connection con = new ConexionOracle().conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT i.id_Inscripcion, e.Nombre || ' ' || e.Apellido1 AS Estudiante, c.Nombre AS Curso, i.Fecha_ins " +
                 "FROM Inscripcion i " +
                 "JOIN Estudiante e ON i.Estudiante=e.id_Estudiante " +
                 "JOIN Curso c ON i.curso=c.id_curso")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_Inscripcion"),
                    rs.getString("Estudiante"),
                    rs.getString("Curso"),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("Fecha_ins"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardar() {
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO Inscripcion (Fecha_ins, curso, Estudiante) VALUES (?, ?, ?)")) {
            ps.setDate(1, new java.sql.Date(((java.util.Date)spFecha.getValue()).getTime()));
            ps.setInt(2, ((Item)cbCurso.getSelectedItem()).id);
            ps.setInt(3, ((Item)cbEstudiante.getSelectedItem()).id);
            ps.executeUpdate();
            listarInscripciones();
            limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        try (Connection con = new ConexionOracle().conectar();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM Inscripcion WHERE id_Inscripcion=?")) {
            ps.setInt(1, idSeleccionado);
            ps.executeUpdate();
            listarInscripciones();
            limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seleccionarFila() {
        int row = table.getSelectedRow();
        if (row != -1) {
            idSeleccionado = (int) model.getValueAt(row, 0);
            String est = (String) model.getValueAt(row, 1);
            String cur = (String) model.getValueAt(row, 2);
            String fecha = (String) model.getValueAt(row, 3);
            // Seleccionar estudiante
            for (int i = 0; i < cbEstudiante.getItemCount(); i++) {
                if (cbEstudiante.getItemAt(i).toString().equals(est)) {
                    cbEstudiante.setSelectedIndex(i);
                    break;
                }
            }
            // Seleccionar curso
            for (int i = 0; i < cbCurso.getItemCount(); i++) {
                if (cbCurso.getItemAt(i).toString().equals(cur)) {
                    cbCurso.setSelectedIndex(i);
                    break;
                }
            }
            try {
                java.util.Date d = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
                spFecha.setValue(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        cbEstudiante.setSelectedIndex(-1);
        cbCurso.setSelectedIndex(-1);
        spFecha.setValue(new java.util.Date());
        table.clearSelection();
    }

    // Clase auxiliar para JComboBox
    static class Item {
        int id;
        String nombre;
        Item(int id, String nombre) { this.id = id; this.nombre = nombre; }
        public String toString() { return nombre; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InscripcionFrame().setVisible(true));
    }
}