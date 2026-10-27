package com.mycompany.losatuendos.gui;

import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel de gestion de clientes.
 *
 * Permite registrar clientes, listarlos en una tabla y consultar sus datos.
 */
public class PanelClientes extends JPanel implements PanelRecargable {

    private final FacadeAlquiler facade;
    private final JFrame ventanaPadre;

    private JTextField txtDocumento, txtNombres, txtApellidos, txtTelefono, txtDireccion;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelClientes(FacadeAlquiler facade, JFrame ventanaPadre) {
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
        JPanel formPanel = GuiUtil.crearPanelConBorde("Registrar cliente");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDocumento = GuiUtil.crearCampo(15);
        txtNombres = GuiUtil.crearCampo(20);
        txtApellidos = GuiUtil.crearCampo(20);
        txtTelefono = GuiUtil.crearCampo(15);
        txtDireccion = GuiUtil.crearCampo(25);

        int y = 0;
        agregarCampo(formPanel, gbc, y++, "Documento:", txtDocumento);
        agregarCampo(formPanel, gbc, y++, "Nombres:", txtNombres);
        agregarCampo(formPanel, gbc, y++, "Apellidos:", txtApellidos);
        agregarCampo(formPanel, gbc, y++, "Telefono:", txtTelefono);
        agregarCampo(formPanel, gbc, y++, "Direccion:", txtDireccion);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        botones.setOpaque(false);
        JButton btnGuardar = GuiUtil.crearBotonPrincipal("Guardar");
        JButton btnLimpiar = GuiUtil.crearBotonCancelar("Limpiar");
        btnGuardar.addActionListener(e -> guardarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        botones.add(btnGuardar);
        botones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        formPanel.add(botones, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ---- Tabla ----
        String[] columnas = {"ID", "Documento", "Nombres", "Apellidos", "Telefono", "Direccion"};
        modeloTabla = GuiUtil.crearModeloTabla(columnas);
        tabla = new JTable(modeloTabla);
        GuiUtil.configurarTabla(tabla);
        JScrollPane scroll = GuiUtil.crearScrollConBorde(tabla, "Clientes registrados");
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int y,
                              String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        panel.add(GuiUtil.crearEtiqueta(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void guardarCliente() {
        String documento = txtDocumento.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (documento.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
            GuiUtil.mostrarError(this, "Documento, nombres y apellidos son obligatorios.");
            return;
        }

        try {
            Cliente c = new Cliente(documento, nombres, apellidos, telefono, direccion);
            facade.registrarCliente(c);
            GuiUtil.mostrarInfo(this, "Cliente registrado con ID " + c.getId());
            limpiarFormulario();
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al registrar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            GuiUtil.mostrarError(this, e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtDocumento.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtDocumento.requestFocusInWindow();
    }

    @Override
    public void recargar() {
        modeloTabla.setRowCount(0);
        try {
            List<Cliente> clientes = facade.listarClientes();
            for (Cliente c : clientes) {
                modeloTabla.addRow(new Object[]{
                    c.getId(), c.getDocumento(), c.getNombres(),
                    c.getApellidos(), c.getTelefono(), c.getDireccion()
                });
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al cargar clientes: " + e.getMessage());
        }
    }
}
