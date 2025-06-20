package widget;

import com.formdev.flatlaf.extras.components.FlatButton;
import java.awt.Color;

/**
 *
 * @author usu
 */
public class Button extends FlatButton {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public Button() {
        super();
        setFont(new java.awt.Font("Inter", 1, 12));
        setBackground(new Color(255, 238, 238));
        setForeground(new Color(0, 131, 62));
    }
}
