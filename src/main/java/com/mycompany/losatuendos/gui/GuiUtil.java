package com.mycompany.losatuendos.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/* Utilidades de interfaz grafica con la paleta institucional naranja/blanco.
 */
public final class GuiUtil {

    private GuiUtil() {}

    public static JTextField crearCampo(int columnas) {
        JTextField field = new JTextField(columnas);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(Tema.BLANCO);
        field.setForeground(Tema.TEXTO_OSCURO);
        field.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(Tema.NARANJA_CLARO, 1, true),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return field;
    }
    public static JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Tema.TEXTO_OSCURO);
        return label;
    }
    public static JButton crearBotonPrincipal(String texto) {
        return new BotonRedondeado(texto, Tema.NARANJA_PRINCIPAL, 14);
    }
    public static JButton crearBotonCancelar(String texto) {
        return new BotonRedondeado(texto, new Color(130, 130, 130), 14);
    }
    public static JButton crearBotonPeligro(String texto) {
        return new BotonRedondeado(texto, new Color(200, 60, 60), 14);
    }
    public static JButton crearBoton(String texto, Color color) {
        return new BotonRedondeado(texto, color, 14);
    }
    public static JPanel crearPanelConBorde(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(Tema.NARANJA_CLARO, 1, true),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                Tema.NARANJA_OSCURO));
        return panel;
    }

    public static DefaultTableModel crearModeloTabla(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void configurarTabla(JTable tabla) {
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(Tema.NARANJA_PASTEL);
        tabla.setSelectionForeground(Tema.TEXTO_OSCURO);
        tabla.setGridColor(Tema.GRIS_BORDE);
        tabla.setBackground(Tema.BLANCO);
        tabla.setForeground(Tema.TEXTO_OSCURO);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(Tema.NARANJA_PRINCIPAL);
        tabla.getTableHeader().setForeground(Tema.TEXTO_CLARO);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centro);
        }
    }
    public static JScrollPane crearScrollConBorde(JTable tabla, String titulo) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(Tema.BLANCO);
        scroll.setBorder(BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(Tema.NARANJA_CLARO, 1, true),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                Tema.NARANJA_OSCURO));
        return scroll;
    }
    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    public static void mostrarInfo(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    public static <T> JComboBox<T> crearCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Tema.BLANCO);
        combo.setForeground(Tema.TEXTO_OSCURO);
        return combo;
    }
    public static JPanel crearContenedor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
}
