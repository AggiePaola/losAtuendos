package com.mycompany.losatuendos.gui;

import com.mycompany.losatuendos.decorator.Priorizable;
import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.persistencia.TandaLavanderia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel de gestion de lavanderia.
 *
 * Muestra las prendas pendientes de lavado (con su prioridad aplicada via
 * Decorator), permite organizar tandas con una capacidad definida por el
 * usuario y marcar prendas como lavadas.
 */
public class PanelLavanderia extends JPanel implements PanelRecargable {

    private final FacadeAlquiler facade;
    private final JFrame ventanaPadre;

    private JTable tablaPendientes;
    private DefaultTableModel modeloPendientes;
    private JTable tablaTandas;
    private DefaultTableModel modeloTandas;
    private JTextField txtCapacidad;

    public PanelLavanderia(FacadeAlquiler facade, JFrame ventanaPadre) {
        this.facade = facade;
        this.ventanaPadre = ventanaPadre;
        initComponents();
        recargar();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Tema.BLANCO);

        // ---- Panel de pendientes ----
        JPanel panelPendientes = GuiUtil.crearPanelConBorde("Prendas pendientes de lavanderia");
        panelPendientes.setLayout(new BorderLayout(5, 5));

        String[] colPendientes = {"ID", "Codigo", "Nombre", "Prioridad", "Nivel"};
        modeloPendientes = GuiUtil.crearModeloTabla(colPendientes);
        tablaPendientes = new JTable(modeloPendientes);
        GuiUtil.configurarTabla(tablaPendientes);
        JScrollPane scrollPendientes = new JScrollPane(tablaPendientes);
        scrollPendientes.setPreferredSize(new Dimension(400, 150));
        panelPendientes.add(scrollPendientes, BorderLayout.CENTER);

        // Botones de pendientes
        JPanel botonesPendientes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        botonesPendientes.setOpaque(false);
        JButton btnMarcarLavada = GuiUtil.crearBotonPrincipal("Marcar como lavada");
        btnMarcarLavada.addActionListener(e -> marcarLavada());
        botonesPendientes.add(btnMarcarLavada);
        panelPendientes.add(botonesPendientes, BorderLayout.SOUTH);

        // ---- Panel de tandas ----
        JPanel panelTandas = GuiUtil.crearPanelConBorde("Organizar tandas");
        panelTandas.setLayout(new BorderLayout(5, 5));

        JPanel controlesTanda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controlesTanda.setOpaque(false);
        controlesTanda.add(GuiUtil.crearEtiqueta("Capacidad por tanda:"));
        txtCapacidad = GuiUtil.crearCampo(5);
        txtCapacidad.setText("5");
        controlesTanda.add(txtCapacidad);
        JButton btnOrganizar = GuiUtil.crearBotonPrincipal("Organizar tandas");
        btnOrganizar.addActionListener(e -> organizarTandas());
        controlesTanda.add(btnOrganizar);
        JButton btnRecargar = GuiUtil.crearBotonCancelar("Recargar");
        btnRecargar.addActionListener(e -> recargar());
        controlesTanda.add(btnRecargar);
        panelTandas.add(controlesTanda, BorderLayout.NORTH);

        String[] colTandas = {"Tanda", "Prendas", "Capacidad"};
        modeloTandas = GuiUtil.crearModeloTabla(colTandas);
        tablaTandas = new JTable(modeloTandas);
        GuiUtil.configurarTabla(tablaTandas);
        JScrollPane scrollTandas = new JScrollPane(tablaTandas);
        scrollTandas.setPreferredSize(new Dimension(300, 150));
        panelTandas.add(scrollTandas, BorderLayout.CENTER);

        // ---- Distribucion ----
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                panelPendientes, panelTandas);
        split.setDividerLocation(250);
        split.setBackground(Tema.BLANCO);
        add(split, BorderLayout.CENTER);
    }

    /**
     * Marca la prenda seleccionada como lavada.
     */
    private void marcarLavada() {
        int fila = tablaPendientes.getSelectedRow();
        if (fila < 0) {
            GuiUtil.mostrarError(this, "Seleccione una prenda de la tabla.");
            return;
        }
        int idPrenda = (int) modeloPendientes.getValueAt(
                tablaPendientes.convertRowIndexToModel(fila), 0);

        if (!GuiUtil.confirmar(this, "Confirmar que la prenda " + idPrenda + " fue lavada?")) {
            return;
        }

        try {
            facade.marcarPrendaLavada(idPrenda);
            GuiUtil.mostrarInfo(this, "Prenda lavada y disponible nuevamente.");
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error: " + e.getMessage());
        }
    }

    /**
     * Organiza las prendas pendientes en tandas.
     */
    private void organizarTandas() {
        int capacidad;
        try {
            capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            if (capacidad <= 0) {
                GuiUtil.mostrarError(this, "La capacidad debe ser mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            GuiUtil.mostrarError(this, "La capacidad debe ser un numero entero.");
            return;
        }

        try {
            List<TandaLavanderia> tandas = facade.organizarTandas(capacidad);
            modeloTandas.setRowCount(0);
            if (tandas.isEmpty()) {
                GuiUtil.mostrarInfo(this, "No hay prendas pendientes para organizar.");
            } else {
                for (TandaLavanderia t : tandas) {
                    modeloTandas.addRow(new Object[]{
                        t.getNumero(), t.cantidad(), t.getCapacidad()
                    });
                }
                GuiUtil.mostrarInfo(this, "Se organizaron " + tandas.size() + " tanda(s).");
            }
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error: " + e.getMessage());
        }
    }

    @Override
    public void recargar() {
        // Pendientes
        modeloPendientes.setRowCount(0);
        try {
            List<Priorizable> pendientes = facade.listarPendientesLavanderia();
            for (Priorizable p : pendientes) {
                modeloPendientes.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getNombre(),
                    p.getPrioridad().name(), p.getNivelPrioridad()
                });
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al cargar pendientes: " + e.getMessage());
        }

        // Historial de tandas
        modeloTandas.setRowCount(0);
        try {
            for (TandaLavanderia t : facade.historialTandas()) {
                modeloTandas.addRow(new Object[]{
                    t.getNumero(), t.cantidad(), t.getCapacidad()
                });
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al cargar tandas: " + e.getMessage());
        }
    }
}
