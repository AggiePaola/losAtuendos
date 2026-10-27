package com.mycompany.losatuendos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BotonRedondeado extends JButton {

    private final Color colorFondo;
    private final Color colorHover;
    private final int radio;
    private boolean hover;

    public BotonRedondeado(String texto, Color colorFondo, int radio) {
        super(texto);
        this.colorFondo = colorFondo;
        this.colorHover = colorFondo.darker();
        this.radio = radio;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setForeground(Tema.textoSegunFondo(colorFondo));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(6, 14, 6, 14));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo = hover ? colorHover : colorFondo;
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo.darker());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        g2.dispose();
    }
}
