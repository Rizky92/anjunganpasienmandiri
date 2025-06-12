package widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author usu
 */
public class ButtonBig extends JButton {

    public ButtonBig() {
        super();
        setBackground(new Color(238, 238, 255));
        setForeground(new Color(0, 131, 62));
        setFont(new Font("Inter", Font.BOLD, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setIconTextGap(16);
        setPreferredSize(new Dimension(200, 90));
    }
}
