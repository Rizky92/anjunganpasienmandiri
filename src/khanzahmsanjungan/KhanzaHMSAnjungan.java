/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khanzahmsanjungan;

import com.formdev.flatlaf.FlatLightLaf;
import fungsi.koneksiDB;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.File;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author khanzasoft
 */
public class KhanzaHMSAnjungan {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Medium.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Bold.ttf")));
            UIManager.setLookAndFeel(new FlatLightLaf());
            System.setProperty("flatlaf.animation", "true");
            UIManager.put("TitlePane.background", new Color(240, 249, 255));
            UIManager.put("TitlePane.unifiedBackground", false);
            UIManager.put("Panel.background", new Color(240, 249, 255));
            UIManager.put("Table.background", new Color(240, 249, 255));
            UIManager.put("Table.foreground", new Color(0, 131, 62));
            UIManager.put("Table.alternateRowColor", new Color(255, 255, 255));
            UIManager.put("Table.selectionBackground", new Color(0, 131, 62));
            UIManager.put("Table.selectionForeground", new Color(255, 255, 255));
            UIManager.put("Table.cellMargins", new Insets(2, 14, 2, 14));
            UIManager.put("Table.rowHeight", 50);
            UIManager.put("Table.font", new Font("Inter Medium", Font.PLAIN, 18));
            UIManager.put("TableHeader.background", new Color(255, 255, 255));
            UIManager.put("TableHeader.foreground", new Color(0, 131, 62));
            UIManager.put("TableHeader.font", new Font("Inter", Font.BOLD, 14));
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 16);
            UIManager.put("ScrollPane.smoothScrolling", true);
            UIManager.put("Button.arc", 16);
            UIManager.put("Component.arc", 16);
            UIManager.put("CheckBox.arc", 16);
            UIManager.put("ProgressBar.arc", 16);
            UIManager.put("TextComponent.arc", 16);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            HalamanUtama utama = HalamanUtama.getInstance();
            utama.setVisible(true);

            String printerBarcode = null, printerRegistrasi = null, printerAntrian = null;

            for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
                System.out.println("Printer ditemukan: " + ps.getName());

                if (ps.getName().equals(koneksiDB.PRINTER_BARCODE())) {
                    printerBarcode = ps.getName();
                }

                if (ps.getName().equals(koneksiDB.PRINTER_REGISTRASI())) {
                    printerRegistrasi = ps.getName();
                }

                if (ps.getName().equals(koneksiDB.PRINTER_ANTRIAN())) {
                    printerRegistrasi = ps.getName();
                }
            }

            if (printerBarcode != null) {
                System.out.println("Setting PRINTER_BARCODE menggunakan printer: " + printerBarcode);
            }

            if (printerRegistrasi != null) {
                System.out.println("Setting PRINTER_REGISTRASI menggunakan printer: " + printerRegistrasi);
            }

            if (printerRegistrasi != null) {
                System.out.println("Setting PRINTER_ANTRIAN menggunakan printer: " + printerAntrian);
            }
        });
    }
}
