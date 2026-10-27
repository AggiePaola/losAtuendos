package com.mycompany.losatuendos.gui;

import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.modelo.Cliente;
import com.mycompany.losatuendos.modelo.Empleado;
import com.mycompany.losatuendos.modelo.Prenda;
import com.mycompany.losatuendos.modelo.ServicioAlquiler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Panel de servicios de alquiler.
 *
 * Permite crear servicios de alquiler seleccionando cliente, empleado,
 * prendas y fechas (Builder y Composite a traves de la fachada). Muestra los
 * servicios en una tabla y permite consultar alquileres vigentes por cliente
 * y devolver servicios.
 */
public class PanelServicios extends JPanel implements PanelRecargable {

    private final FacadeAlquiler facade;
    private final JFrame ventanaPadre;

    private JComboBox<ComboItem> comboClientes;
    private JComboBox<ComboItem> comboEmpleados;
    private JList<ComboItem> listaPrendas;
    private JTextField txtFechaAlquiler, txtFechaDevolucion, txtPrecio;
    private JTable tablaServicios;
    private DefaultTableModel modeloTabla;
    private JTextField txtConsultaCliente;

    public PanelServicios(FacadeAlquiler facade, JFrame ventanaPadre) {
        this.facade = facade;
        this.ventanaPadre = ventanaPadre;
        initComponents();
        recargar();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Tema.BLANCO);

        // ---- Formulario superior ----
        JPanel formPanel = GuiUtil.crearPanelConBorde("Crear servicio de alquiler");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboClientes = new JComboBox<>();
        comboClientes.setPreferredSize(new Dimension(250, 26));
        comboEmpleados = new JComboBox<>();
        comboEmpleados.setPreferredSize(new Dimension(250, 26));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hoy = sdf.format(Calendar.getInstance().getTime());
        Calendar manana = Calendar.getInstance();
        manana.add(Calendar.DAY_OF_MONTH, 5);
        String devolucion = sdf.format(manana.getTime());

        txtFechaAlquiler = GuiUtil.crearCampo(10);
        txtFechaAlquiler.setText(hoy);
        txtFechaDevolucion = GuiUtil.crearCampo(10);
        txtFechaDevolucion.setText(devolucion);
        txtPrecio = GuiUtil.crearCampo(10);
        txtPrecio.setText("0");

        // Lista de prendas con seleccion multiple
        listaPrendas = new JList<>();
        listaPrendas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaPrendas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        listaPrendas.setBackground(Tema.BLANCO);
        listaPrendas.setForeground(Tema.TEXTO_OSCURO);
        listaPrendas.setSelectionBackground(Tema.NARANJA_PASTEL);
        listaPrendas.setSelectionForeground(Tema.TEXTO_OSCURO);
        JScrollPane scrollPrendas = new JScrollPane(listaPrendas);
        scrollPrendas.setPreferredSize(new Dimension(250, 80));

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        formPanel.add(GuiUtil.crearEtiqueta("Cliente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        formPanel.add(comboClientes, gbc);
        gbc.gridwidth = 1;
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(GuiUtil.crearEtiqueta("Empleado:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        formPanel.add(comboEmpleados, gbc);
        gbc.gridwidth = 1;
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(GuiUtil.crearEtiqueta("Prendas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        formPanel.add(scrollPrendas, gbc);
        gbc.gridwidth = 1;
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(GuiUtil.crearEtiqueta("Fecha alquiler:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFechaAlquiler, gbc);
        gbc.gridx = 2;
        formPanel.add(GuiUtil.crearEtiqueta("(yyyy-MM-dd)"), gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(GuiUtil.crearEtiqueta("Fecha devolucion:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFechaDevolucion, gbc);
        gbc.gridx = 2;
        formPanel.add(GuiUtil.crearEtiqueta("(yyyy-MM-dd)"), gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(GuiUtil.crearEtiqueta("Precio total:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPrecio, gbc);
        y++;

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        botones.setOpaque(false);
        JButton btnCrear = GuiUtil.crearBotonPrincipal("Crear alquiler");
        JButton btnDevolver = GuiUtil.crearBotonCancelar("Devolver servicio");
        JButton btnLimpiar = GuiUtil.crearBotonCancelar("Limpiar");
        btnCrear.addActionListener(e -> crearServicio());
        btnDevolver.addActionListener(e -> devolverServicio());
        btnLimpiar.addActionListener(e -> limpiarSeleccion());
        botones.add(btnCrear);
        botones.add(btnDevolver);
        botones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 3;
        formPanel.add(botones, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ---- Tabla de servicios ----
        String[] columnas = {"ID", "Consec.", "F. Alquiler", "F. Devolucion",
                "Cliente", "Empleado", "Estado", "Prendas", "Precio"};
        modeloTabla = GuiUtil.crearModeloTabla(columnas);
        tablaServicios = new JTable(modeloTabla);
        GuiUtil.configurarTabla(tablaServicios);
        JScrollPane scrollTabla = GuiUtil.crearScrollConBorde(tablaServicios, "Servicios de alquiler");

        // ---- Panel de consulta de vigentes ----
        JPanel panelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelConsulta.setBackground(Tema.BLANCO);
        panelConsulta.setBorder(BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(Tema.NARANJA_CLARO, 1, true),
                "Consultar alquileres vigentes por cliente",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                Tema.NARANJA_OSCURO));
        panelConsulta.add(GuiUtil.crearEtiqueta("ID cliente:"));
        txtConsultaCliente = GuiUtil.crearCampo(8);
        panelConsulta.add(txtConsultaCliente);
        JButton btnConsultar = GuiUtil.crearBotonPrincipal("Consultar");
        btnConsultar.addActionListener(e -> consultarVigentes());
        panelConsulta.add(btnConsultar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Tema.BLANCO);
        sur.add(panelConsulta, BorderLayout.NORTH);
        sur.add(scrollTabla, BorderLayout.CENTER);
        add(sur, BorderLayout.CENTER);
    }

    /**
     * Crea un servicio de alquiler validando los datos.
     */
    private void crearServicio() {
        ComboItem itemCliente = (ComboItem) comboClientes.getSelectedItem();
        ComboItem itemEmpleado = (ComboItem) comboEmpleados.getSelectedItem();
        if (itemCliente == null || itemEmpleado == null) {
            GuiUtil.mostrarError(this, "Debe seleccionar un cliente y un empleado.");
            return;
        }

        List<ComboItem> seleccionadas = listaPrendas.getSelectedValuesList();
        if (seleccionadas.isEmpty()) {
            GuiUtil.mostrarError(this, "Debe seleccionar al menos una prenda.");
            return;
        }

        String fechaAlquiler = txtFechaAlquiler.getText().trim();
        String fechaDevolucion = txtFechaDevolucion.getText().trim();
        if (fechaAlquiler.isEmpty() || fechaDevolucion.isEmpty()) {
            GuiUtil.mostrarError(this, "Las fechas son obligatorias.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            GuiUtil.mostrarError(this, "El precio debe ser un numero valido.");
            return;
        }

        List<Integer> idsPrendas = new ArrayList<>();
        for (ComboItem item : seleccionadas) {
            idsPrendas.add(item.getId());
        }

        try {
            ServicioAlquiler s = facade.crearServicioAlquiler(
                    itemCliente.getId(), itemEmpleado.getId(),
                    idsPrendas, fechaAlquiler, fechaDevolucion, precio);
            GuiUtil.mostrarInfo(this, "Servicio creado. Consecutivo: "
                    + s.getNumeroConsecutivo());
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al crear servicio: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            GuiUtil.mostrarError(this, e.getMessage());
        } catch (IllegalStateException e) {
            GuiUtil.mostrarError(this, e.getMessage());
        }
    }

    /**
     * Devuelve el servicio seleccionado en la tabla.
     */
    private void devolverServicio() {
        int fila = tablaServicios.getSelectedRow();
        if (fila < 0) {
            GuiUtil.mostrarError(this, "Seleccione un servicio en la tabla.");
            return;
        }

        int idServicio = (int) modeloTabla.getValueAt(tablaServicios.convertRowIndexToModel(fila), 0);
        String estado = (String) modeloTabla.getValueAt(tablaServicios.convertRowIndexToModel(fila), 6);

        if (!"ACTIVO".equals(estado)) {
            GuiUtil.mostrarError(this, "Solo se pueden devolver servicios activos.");
            return;
        }

        // Pedir prendas prioritarias
        String input = JOptionPane.showInputDialog(this,
                "IDs de prendas con prioridad especial (separados por coma, o vacio):",
                "Devolver servicio", JOptionPane.QUESTION_MESSAGE);
        List<Integer> prioritarias = new ArrayList<>();
        if (input != null && !input.trim().isEmpty()) {
            for (String s : input.split(",")) {
                try {
                    prioritarias.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }

        String fecha = JOptionPane.showInputDialog(this,
                "Fecha de devolucion (yyyy-MM-dd):",
                "Devolver servicio", JOptionPane.QUESTION_MESSAGE);
        if (fecha == null || fecha.trim().isEmpty()) {
            GuiUtil.mostrarError(this, "La fecha de devolucion es obligatoria.");
            return;
        }

        try {
            facade.devolverServicio(idServicio, fecha.trim(), prioritarias);
            GuiUtil.mostrarInfo(this, "Servicio devuelto. Prendas enviadas a lavanderia.");
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            GuiUtil.mostrarError(this, e.getMessage());
        }
    }

    /**
     * Consulta los alquileres vigentes del cliente indicado.
     */
    private void consultarVigentes() {
        String idStr = txtConsultaCliente.getText().trim();
        if (idStr.isEmpty()) {
            GuiUtil.mostrarError(this, "Ingrese el ID del cliente.");
            return;
        }
        try {
            int idCliente = Integer.parseInt(idStr);
            List<ServicioAlquiler> vigentes = facade.alquileresVigentesPorCliente(idCliente);
            modeloTabla.setRowCount(0);
            for (ServicioAlquiler s : vigentes) {
                agregarFilaServicio(s);
            }
            if (vigentes.isEmpty()) {
                GuiUtil.mostrarInfo(this, "No hay alquileres vigentes para el cliente " + idCliente);
            }
        } catch (NumberFormatException e) {
            GuiUtil.mostrarError(this, "El ID debe ser numerico.");
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error: " + e.getMessage());
        }
    }

    private void limpiarSeleccion() {
        comboClientes.setSelectedIndex(0);
        comboEmpleados.setSelectedIndex(0);
        listaPrendas.clearSelection();
        txtPrecio.setText("0");
    }

    @Override
    public void recargar() {
        cargarCombos();
        cargarTablaServicios();
    }

    private void cargarCombos() {
        // Clientes
        comboClientes.removeAllItems();
        try {
            for (Cliente c : facade.listarClientes()) {
                comboClientes.addItem(new ComboItem(c.getId(), c.nombreCompleto() + " (doc:" + c.getDocumento() + ")"));
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error cargando clientes: " + e.getMessage());
        }

        // Empleados
        comboEmpleados.removeAllItems();
        try {
            for (Empleado e : facade.listarEmpleados()) {
                comboEmpleados.addItem(new ComboItem(e.getId(), e.nombreCompleto() + " (" + e.getCargo() + ")"));
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error cargando empleados: " + e.getMessage());
        }

        // Prendas
        DefaultListModel<ComboItem> modeloLista = new DefaultListModel<>();
        try {
            for (Prenda p : facade.listarPrendas()) {
                String texto = "[" + p.getId() + "] " + p.getCodigo() + " - " + p.getNombre()
                        + " (" + p.getTipo().name() + ")"
                        + (p.isDisponible() ? "" : " [NO DISPONIBLE]");
                modeloLista.addElement(new ComboItem(p.getId(), texto));
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error cargando prendas: " + e.getMessage());
        }
        listaPrendas.setModel(modeloLista);
    }

    private void cargarTablaServicios() {
        modeloTabla.setRowCount(0);
        try {
            for (ServicioAlquiler s : facade.listarServicios()) {
                agregarFilaServicio(s);
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error cargando servicios: " + e.getMessage());
        }
    }

    private void agregarFilaServicio(ServicioAlquiler s) {
        modeloTabla.addRow(new Object[]{
            s.getId(), s.getNumeroConsecutivo(), s.getFechaAlquiler(),
            s.getFechaDevolucion(),
            s.getCliente() != null ? s.getCliente().nombreCompleto() : "?",
            s.getEmpleado() != null ? s.getEmpleado().nombreCompleto() : "?",
            s.getEstado().name(), s.getListaPrendas().size(),
            String.format("%.0f", s.getPrecioTotal())
        });
    }

    /**
     * Item de combo/lista que guarda id + texto.
     */
    private static class ComboItem {
        private final int id;
        private final String texto;

        ComboItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        int getId() { return id; }

        @Override
        public String toString() { return texto; }
    }
}
