package widget;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPasswordField;

/**
 *
 * @author usu
 */
public class PasswordBox extends JPasswordField {
    public PasswordBox() {
        super();
        setSelectionColor(Color.BLUE.brighter());
        setCaretColor(Color.red);
        setFont(getFont().deriveFont(Font.BOLD, 12));
        setForeground(Color.WHITE);
        setHorizontalAlignment(LEFT);
    }
}
