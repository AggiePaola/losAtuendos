package com.mycompany.losatuendos.gui;
import javax.swing.border.LineBorder;
import java.awt.*;

public final class Tema {
    private Tema() {}
    public static final Color NARANJA_PRINCIPAL = new Color(230, 111, 0);
    public static final Color NARANJA_OSCURO = new Color(200, 90, 0);
    public static final Color NARANJA_CLARO = new Color(255, 152, 51);
    public static final Color NARANJA_PASTEL = new Color(255, 224, 178);
    public static final Color BLANCO = Color.WHITE;
    public static final Color GRIS_CLARO = new Color(245, 245, 245);
    public static final Color GRIS_BORDE = new Color(210, 210, 210);
    public static final Color TEXTO_OSCURO = new Color(33, 33, 33);
    public static final Color TEXTO_CLARO = Color.WHITE;
    public static final Color MENU_FONDO = NARANJA_PRINCIPAL;
    public static final Color MENU_FONDO_HOVER = NARANJA_OSCURO;
    public static final Color MENU_BOTON = new Color(255, 167, 38);
    public static final Color MENU_BOTON_ACTIVO = NARANJA_OSCURO;

    public static boolean esFondoClaro(Color fondo) {
        double luminancia = 0.299 * fondo.getRed()
                          + 0.587 * fondo.getGreen()
                          + 0.114 * fondo.getBlue();
        return luminancia > 140;
    }
    public static Color textoSegunFondo(Color fondo) {
        return esFondoClaro(fondo) ? TEXTO_OSCURO : TEXTO_CLARO;
    }
    public static LineBorder bordeNaranja() {
        return new LineBorder(NARANJA_OSCURO, 1);
    }
}
