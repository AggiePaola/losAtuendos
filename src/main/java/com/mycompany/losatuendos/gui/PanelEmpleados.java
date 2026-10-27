package com.mycompany.losatuendos.gui;

import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.modelo.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel de gestion de empleados.
 */
public class PanelEmpleados extends JPanel implements PanelRecargable {

    private final FacadeAlquiler facade;
    private final JFrame ventanaPadre;

    private JTextField txtDocumento, txtNombres, txtApellidos, txtCargo, txtTelefono;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelEmpleados(FacadeAlquiler facade, JFrame ventanaPadre) {
        this.facade = facade;
        this.ventanaPadre = ventanaPadre;
        initComponents();
        recargar();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Tema.BLANCO);

        // ---- Formulario ----
        JPanel formPanel = GuiUtil.crearPanelConBorde("Registrar empleado");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDocumento = GuiUtil.crearCampo(15);
        txtNombres = GuiUtil.crearCampo(20);
        txtApellidos = GuiUtil.crearCampo(20);
        txtCargo = GuiUtil.crearCampo(20);
        txtTelefono = GuiUtil.crearCampo(15);

        int y = 0;
        agregarCampo(formPanel, gbc, y++, "Documento:", txtDocumento);
        agregarCampo(formPanel, gbc, y++, "Nombres:", txtNombres);
        agregarCampo(formPanel, gbc, y++, "Apellidos:", txtApellidos);
        agregarCampo(formPanel, gbc, y++, "Cargo:", txtCargo);
        agregarCampo(formPanel, gbc, y++, "Telefono:", txtTelefono);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        botones.setOpaque(false);
        JButton btnGuardar = GuiUtil.crearBotonPrincipal("Guardar");
        JButton btnLimpiar = GuiUtil.crearBotonCancelar("Limpiar");
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        botones.add(btnGuardar);
        botones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        formPanel.add(botones, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ---- Tabla ----
        String[] columnas = {"ID", "Documento", "Nombres", "Apellidos", "Cargo", "Telefono"};
        modeloTabla = GuiUtil.crearModeloTabla(columnas);
        tabla = new JTable(modeloTabla);
        GuiUtil.configurarTabla(tabla);
        JScrollPane scroll = GuiUtil.crearScrollConBorde(tabla, "Empleados registrados");
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int y,
                              String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        panel.add(GuiUtil.crearEtiqueta(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void guardarEmpleado() {
        String documento = txtDocumento.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String cargo = txtCargo.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (documento.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
            GuiUtil.mostrarError(this, "Documento, nombres y apellidos son obligatorios.");
            return;
        }

        try {
            Empleado e = new Empleado(documento, nombres, apellidos, cargo, telefono);
            facade.registrarEmpleado(e);
            GuiUtil.mostrarInfo(this, "Empleado registrado con ID " + e.getId());
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            GuiUtil.mostrarError(this, "Error al registrar: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            GuiUtil.mostrarError(this, ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtDocumento.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCargo.setText("");
        txtTelefono.setText("");
        txtDocumento.requestFocusInWindow();
    }

    @Override
    public void recargar() {
        modeloTabla.setRowCount(0);
        try {
            List<Empleado> empleados = facade.listarEmpleados();
            for (Empleado e : empleados) {
                modeloTabla.addRow(new Object[]{
                    e.getId(), e.getDocumento(), e.getNombres(),
                    e.getApellidos(), e.getCargo(), e.getTelefono()
                });
            }
        } catch (SQLException ex) {
            GuiUtil.mostrarError(this, "Error al cargar empleados: " + ex.getMessage());
        }
    }
}
