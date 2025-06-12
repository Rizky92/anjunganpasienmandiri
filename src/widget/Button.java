package widget;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.JButton;

/**
 *
 * @author usu
 */
public class Button extends JButton {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public Button() {
        super();
        setFont(new java.awt.Font("Tahoma", 1, 11));
        setForeground(new Color(50, 50, 50));
        setMargin(new Insets(2, 7, 2, 7));
        setIconTextGap(1);
    }
}
