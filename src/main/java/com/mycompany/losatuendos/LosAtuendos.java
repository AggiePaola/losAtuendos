package com.mycompany.losatuendos;

import com.mycompany.losatuendos.gui.VentanaPrincipal;

import javax.swing.SwingUtilities;
public class LosAtuendos {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
