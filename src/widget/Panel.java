/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package widget;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author root
 */
public class Panel extends JPanel {
    public Panel() {
        setBackground(new Color(238, 238, 255));
        setBorder(new LineBorder(new Color(245, 255, 240)));
    }
}
