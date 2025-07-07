package widget;

import com.formdev.flatlaf.extras.components.FlatComboBox;
import java.awt.Color;
import java.awt.Font;

public class ComboBox extends FlatComboBox {
    public ComboBox() {
        setFont(new Font("Inter Medium", Font.PLAIN, 12));
        setForeground(new Color(0, 131, 62));
        setBackground(new Color(255, 255, 255));
        setSize(WIDTH, 35);
    }
}
