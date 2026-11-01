package com.mycompany.losatuendos.gui;
import com.mycompany.losatuendos.facade.FacadeAlquiler;
import com.mycompany.losatuendos.persistencia.EsquemaBD;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

public class VentanaPrincipal extends JFrame {
    private final FacadeAlquiler facade;
    private final CardLayout cardLayout;
    private final JPanel panelCentro;
    private JButton botonActivo;

    public VentanaPrincipal() {
        super("Los Atuendos - Sistema de Alquiler de Vestuario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 620);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.BLANCO);
        EsquemaBD.crearSiNoExiste();
        facade = new FacadeAlquiler();
        setLayout(new BorderLayout());
        JPanel menuLateral = crearMenuLateral();
        add(menuLateral, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelCentro = new JPanel(cardLayout);
        panelCentro.setBackground(Tema.BLANCO);
        add(panelCentro, BorderLayout.CENTER);
        panelCentro.add(new PanelClientes(facade, this), "clientes");
        panelCentro.add(new PanelEmpleados(facade, this), "empleados");
        panelCentro.add(new PanelPrendas(facade, this), "prendas");
        panelCentro.add(new PanelServicios(facade, this), "servicios");
        panelCentro.add(new PanelLavanderia(facade, this), "lavandería");

        panelCentro.add(crearPanelInicio(), "inicio");
        cardLayout.show(panelCentro, "inicio");
        botonActivo = null;
    }

    private ImageIcon cargarLogo() {
        String rutaRelativa = "src" + File.separator + "main" + File.separator + "1280px-Compensar_logo.svg.png";
        File archivo = new File(rutaRelativa);
        if (archivo.exists()) {
            ImageIcon icono = new ImageIcon(archivo.getAbsolutePath());
            Image img = icono.getImage();
            return new ImageIcon(img.getScaledInstance(180, 60, Image.SCALE_SMOOTH));
        }
        URL url = getClass().getClassLoader().getResource("1280px-Compensar_logo.svg.png");
        if (url != null) {
            ImageIcon icono = new ImageIcon(url);
            Image img = icono.getImage();
            return new ImageIcon(img.getScaledInstance(180, 60, Image.SCALE_SMOOTH));
        }
        return null;
    }
    private JPanel crearPanelInicio() {
        JPanel inicio = new JPanel(new BorderLayout());
        inicio.setBackground(Tema.BLANCO);
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Tema.BLANCO);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        ImageIcon logo = cargarLogo();
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo);
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            panelSuperior.add(lblLogo, BorderLayout.NORTH);
        }
        JLabel titulo = new JLabel("Los Atuendos", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(Tema.NARANJA_OSCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        panelSuperior.add(titulo, BorderLayout.CENTER);
        JLabel subtitulo = new JLabel(
                "Sistema de Administración de Alquiler de Vestuario",
                SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(Tema.TEXTO_OSCURO);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panelSuperior.add(subtitulo, BorderLayout.SOUTH);
        inicio.add(panelSuperior, BorderLayout.NORTH);
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Tema.BLANCO);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 20, 80));
        JLabel lblModulos = new JLabel("Modulos disponibles:");
        lblModulos.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblModulos.setForeground(Tema.NARANJA_OSCURO);
        infoPanel.add(lblModulos);
        String[] modulos = {
            "  Clientes",
            "  Empleados",
            "  Prendas (Vestidos, Trajes, Disfraces)",
            "  Servicios de Alquiler",
            "  Lavanderia (Tandas y Prioridades)"
        };
        for (String m : modulos) {
            JLabel lbl = new JLabel(m);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setForeground(Tema.TEXTO_OSCURO);
            infoPanel.add(lbl);
        }
        infoPanel.add(new JLabel(""));
        JLabel patrones = new JLabel("Hecho por Angie Rocha - Patrones de diseño");
        patrones.setFont(new Font("SansSerif", Font.ITALIC, 12));
        patrones.setForeground(Tema.NARANJA_PRINCIPAL);
        infoPanel.add(patrones);
        inicio.add(infoPanel, BorderLayout.CENTER);
        return inicio;
    }
    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.NARANJA_PRINCIPAL);
        menu.setPreferredSize(new Dimension(190, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        ImageIcon logo = cargarLogo();
        if (logo != null) {
            JLabel lblLogoMenu = new JLabel(logo);
            lblLogoMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblLogoMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 5));
            menu.add(lblLogoMenu);
        } else {
            JLabel tituloMenu = new JLabel("  Los Atuendos");
            tituloMenu.setForeground(Tema.TEXTO_CLARO);
            tituloMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
            tituloMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            tituloMenu.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            menu.add(tituloMenu);
        }

        menu.add(crearSeparador());
        menu.add(Box.createRigidArea(new Dimension(0, 8)));
        String[][] botones = {
            {"clientes", "Clientes"},
            {"empleados", "Empleados"},
            {"prendas", "Prendas"},
            {"servicios", "Servicios"},
            {"lavanderia", "Lavanderia"}
        };
        for (String[] b : botones) {
            JButton btn = crearBotonMenu(b[0], b[1]);
            menu.add(btn);
            menu.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        menu.add(Box.createVerticalGlue());
        menu.add(crearSeparador());
        menu.add(Box.createRigidArea(new Dimension(0, 8)));
        JButton btnSalir = crearBotonMenu("salir", "Salir");
        menu.add(btnSalir);
        return menu;
    }
    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(255, 255, 255, 100));
        sep.setBackground(new Color(255, 255, 255, 100));
        sep.setMaximumSize(new Dimension(174, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JButton crearBotonMenu(String clave, String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(174, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(Tema.MENU_BOTON);
        btn.setForeground(Tema.TEXTO_OSCURO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botonActivo != btn) {
                    btn.setBackground(Tema.NARANJA_CLARO);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (botonActivo != btn) {
                    btn.setBackground(Tema.MENU_BOTON);
                }
            }
        });
        btn.addActionListener(e -> {
            if (clave.equals("salir")) {
                System.exit(0);
            } else {
                mostrarPanel(clave);
                resaltarBoton(btn);
            }
        });
        return btn;
    }
    private void mostrarPanel(String clave) {
        cardLayout.show(panelCentro, clave);
        Component[] componentes = panelCentro.getComponents();
        for (Component c : componentes) {
            if (c instanceof PanelRecargable && c.isVisible()) {
                ((PanelRecargable) c).recargar();
            }
        }
    }
    private void resaltarBoton(JButton btn) {
        if (botonActivo != null) {
            botonActivo.setBackground(Tema.MENU_BOTON);
            botonActivo.setForeground(Tema.TEXTO_OSCURO);
        }
        btn.setBackground(Tema.MENU_BOTON_ACTIVO);
        btn.setForeground(Tema.TEXTO_CLARO);
        botonActivo = btn;
    }
    public FacadeAlquiler getFacade() {
        return facade;
    }
}
