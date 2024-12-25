package widget;

import java.awt.Color;
import java.awt.Insets;

/**
 *
 * @author usu
 */
public class Button extends usu.widget.ButtonGlass {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public Button() {
        super();
        setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
        setForeground(new Color(50, 50, 50));
        setGlassColor(new Color(0, 131, 62));
        setMargin(new Insets(2, 7, 2, 7));
        setIconTextGap(1);
        setRoundRect(false);
    }
}
