package widget;

import com.formdev.flatlaf.extras.components.FlatTextField;
import java.awt.Color;
import java.awt.Font;

public class TextField extends FlatTextField {
    public TextField() {
        setFont(new Font("Inter Medium", Font.PLAIN, 12));
        setSelectionColor(new Color(0, 131, 62));
        setSelectedTextColor(new Color(255, 255, 255));
        setForeground(new Color(0, 131, 62));
        setBackground(new Color(255, 255, 255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH, 35);
    }
}
