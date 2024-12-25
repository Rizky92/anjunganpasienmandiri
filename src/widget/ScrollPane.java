package widget;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

/**
 *
 * @author usu
 */
public class ScrollPane extends JScrollPane {

    public ScrollPane() {
        super();
        setOpaque(false);
        setBorder(new LineBorder(new Color(239, 244, 234)));
        getVerticalScrollBar().setUnitIncrement(20);
    }
}
