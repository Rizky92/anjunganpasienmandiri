package widget;

import com.formdev.flatlaf.extras.components.FlatTable;
import com.formdev.flatlaf.util.ColorFunctions;
import fungsi.WarnaTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author usu
 */
public class Table extends FlatTable {

    private static final long serialVersionUID = 1L;

    public Table() {
        setFont(new Font("Inter Display Medium", Font.PLAIN, 18));
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowHeight(50);
        setSelectionBackground(ColorFunctions.darken(new Color(0, 131, 62), 0.15f));
        setSelectionForeground(ColorFunctions.lighten(new Color(238, 238, 255), 0.1f));
        getTableHeader().setBackground(new Color(255, 255, 255));
        getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, ColorFunctions.lighten(new Color(0, 131, 62), 0.25f)));
        getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        getTableHeader().setForeground(new Color(0, 131, 62));
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
        setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        setDefaultRenderer(Integer.class, new CustomNumberCellRenderer());
        setDefaultRenderer(Double.class, new CustomNumberCellRenderer());
        setDefaultRenderer(Float.class, new CustomNumberCellRenderer());
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                Color background = table.getBackground();
                Color alternateColor = UIManager.getColor("Table.alternateRowColor");
                if (alternateColor != null && row % 2 != 0) {
                    background = alternateColor;
                }
                super.setForeground(table.getForeground());
                super.setBackground(background);
            }

            return c;
        }
    }
    
    static class CustomNumberCellRenderer extends CustomTableCellRenderer {
        public CustomNumberCellRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }
}
