package com.mycompany.losatuendos.gui;
import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.modelo.Prenda;
import com.mycompany.losatuendos.modelo.TipoPrenda;
import com.mycompany.losatuendos.modelo.Vestido;
import com.mycompany.losatuendos.modelo.Traje;
import com.mycompany.losatuendos.modelo.Disfraz;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelPrendas extends JPanel implements PanelRecargable {
    private final FacadeAlquiler facade;
    private final JFrame ventanaPadre;
    private JTextField txtCodigo, txtNombre, txtDescripcion, txtLongitud, txtAccesorios;
    private JCheckBox chkPedreria;
    private JLabel lblLongitud, lblPedreria, lblAccesorios;
    private JComboBox<TipoPrenda> comboTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelPrendas(FacadeAlquiler facade, JFrame ventanaPadre) {
        this.facade = facade;
        this.ventanaPadre = ventanaPadre;
        initComponents();
        recargar();
    }
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Tema.BLANCO);
        //Formulario
        JPanel formPanel = GuiUtil.crearPanelConBorde("Registrar prenda");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboTipo = GuiUtil.crearCombo(TipoPrenda.values());
        comboTipo.addActionListener(e -> actualizarVisibilidadCampos());
        txtCodigo = GuiUtil.crearCampo(15);
        txtNombre = GuiUtil.crearCampo(20);
        txtDescripcion = GuiUtil.crearCampo(25);
        txtLongitud = GuiUtil.crearCampo(15);
        txtAccesorios = GuiUtil.crearCampo(20);
        chkPedreria = new JCheckBox();
        chkPedreria.setBackground(Tema.BLANCO);
        chkPedreria.setForeground(Tema.TEXTO_OSCURO);
        lblLongitud = GuiUtil.crearEtiqueta("Longitud (corta/media/larga):");
        lblPedreria = GuiUtil.crearEtiqueta("Tiene pedreria:");
        lblAccesorios = GuiUtil.crearEtiqueta("Tipo de accesorios:");

        int y = 0;
        agregarCampo(formPanel, gbc, y++, "Tipo:", comboTipo);
        agregarCampo(formPanel, gbc, y++, "Codigo:", txtCodigo);
        agregarCampo(formPanel, gbc, y++, "Nombre:", txtNombre);
        agregarCampo(formPanel, gbc, y++, "Descripcion:", txtDescripcion);

        // Campos especificos que se muestran/ocultan segun el tipo
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        formPanel.add(lblLongitud, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(txtLongitud, gbc);
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        formPanel.add(lblPedreria, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(chkPedreria, gbc);
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        formPanel.add(lblAccesorios, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(txtAccesorios, gbc);
        y++;
        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        botones.setOpaque(false);
        JButton btnGuardar = GuiUtil.crearBotonPrincipal("Guardar");
        JButton btnLimpiar = GuiUtil.crearBotonCancelar("Limpiar");
        btnGuardar.addActionListener(e -> guardarPrenda());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        botones.add(btnGuardar);
        botones.add(btnLimpiar);
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        formPanel.add(botones, gbc);
        add(formPanel, BorderLayout.NORTH);
      
        String[] columnas = {"ID", "Codigo", "Tipo", "Nombre", "Detalle", "Disponible"};
        modeloTabla = GuiUtil.crearModeloTabla(columnas);
        tabla = new JTable(modeloTabla);
        GuiUtil.configurarTabla(tabla);
        JScrollPane scroll = GuiUtil.crearScrollConBorde(tabla, "Prendas registradas");
        add(scroll, BorderLayout.CENTER);
        actualizarVisibilidadCampos();
    }
    private void actualizarVisibilidadCampos() {
        TipoPrenda tipo = (TipoPrenda) comboTipo.getSelectedItem();
        boolean esVestido = tipo == TipoPrenda.VESTIDO;
        boolean esTraje = tipo == TipoPrenda.TRAJE;
        lblLongitud.setVisible(esVestido);
        txtLongitud.setVisible(esVestido);
        lblPedreria.setVisible(esVestido);
        chkPedreria.setVisible(esVestido);
        lblAccesorios.setVisible(esTraje);
        txtAccesorios.setVisible(esTraje);
        revalidate();
        repaint();
    }
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int y,
                              String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        panel.add(GuiUtil.crearEtiqueta(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campo, gbc);
    }
    private void guardarPrenda() {
        TipoPrenda tipo = (TipoPrenda) comboTipo.getSelectedItem();
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            GuiUtil.mostrarError(this, "Codigo y nombre son obligatorios.");
            return;
        }
        String atributoExtra = null;
        boolean atributoBool = false;
        if (tipo == TipoPrenda.VESTIDO) {
            atributoExtra = txtLongitud.getText().trim();
            if (atributoExtra.isEmpty()) {
                GuiUtil.mostrarError(this, "La longitud es obligatoria para vestidos.");
                return;
            }
            atributoBool = chkPedreria.isSelected();
        } else if (tipo == TipoPrenda.TRAJE) {
            atributoExtra = txtAccesorios.getText().trim();
            if (atributoExtra.isEmpty()) {
                GuiUtil.mostrarError(this, "El tipo de accesorios es obligatorio para trajes.");
                return;
            }
        }
        try {
            Prenda p = facade.registrarPrenda(tipo, codigo, nombre, descripcion,
                    atributoExtra, atributoBool);
            GuiUtil.mostrarInfo(this, "Prenda registrada: " + p.getCodigo());
            limpiarFormulario();
            recargar();
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al registrar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            GuiUtil.mostrarError(this, e.getMessage());
        }
    }
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtLongitud.setText("");
        txtAccesorios.setText("");
        chkPedreria.setSelected(false);
        comboTipo.setSelectedIndex(0);
        txtCodigo.requestFocusInWindow();
    }
    @Override
    public void recargar() {
        modeloTabla.setRowCount(0);
        try {
            List<Prenda> prendas = facade.listarPrendas();
            for (Prenda p : prendas) {
                String detalle = "";
                if (p instanceof Vestido) {
                    Vestido v = (Vestido) p;
                    detalle = "long=" + v.getLongitud() + ", pedreria=" + (v.isTienePedreria() ? "si" : "no");
                } else if (p instanceof Traje) {
                    Traje t = (Traje) p;
                    detalle = "accesorios=" + t.getTipoAccesorios();
                } else if (p instanceof Disfraz) {
                    detalle = "disfraz";
                }
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getTipo().name(),
                    p.getNombre(), detalle, p.isDisponible() ? "Si" : "No"
                });
            }
        } catch (SQLException e) {
            GuiUtil.mostrarError(this, "Error al cargar prendas: " + e.getMessage());
        }
    }
}
