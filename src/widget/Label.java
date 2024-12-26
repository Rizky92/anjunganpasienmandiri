package widget;

import java.awt.Color;

/**
 *
 * @author usu
 */
public class Label extends usu.widget.Label {
    private static final long serialVersionUID = 1L;

    public Label() {
        super();
        setForeground(new Color(50, 50, 50));
        setFont(new java.awt.Font("Inter", java.awt.Font.BOLD, 11));
        setHorizontalAlignment(RIGHT);
        setVerticalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(CENTER);
    }
}
