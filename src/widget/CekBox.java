/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package widget;

import java.awt.Color;
import javax.swing.JCheckBox;

/**
 *
 * @author dosen3
 */
public class CekBox extends JCheckBox {

    private static final long serialVersionUID = 1L;

    public CekBox() {
        super();
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(140, 90, 140));
        setFocusPainted(false);
        setBorder(javax.swing.BorderFactory.createLineBorder(new Color(235, 130, 235)));
        setOpaque(false);
        setSize(WIDTH, 23);
    }
}
