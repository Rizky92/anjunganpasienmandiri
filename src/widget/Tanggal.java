/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package widget;

import java.awt.Color;
import uz.ncipro.calendar.JDateTimePicker;

/**
 *
 * @author khanzasoft
 */
public final class Tanggal extends JDateTimePicker {

    public Tanggal() {
        super();
        setForeground(new Color(0, 131, 62));
        setBackground(new Color(255, 255, 255));
        setFont(new java.awt.Font("Inter", java.awt.Font.BOLD, 14));
    }

}
