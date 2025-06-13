package widget;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class ButtonMenu extends JButton {
    public ButtonMenu() {
        super();
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setIconTextGap(16);
        setPreferredSize(new Dimension(200, 90));
    }
}
