/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khanzahmsanjungan;

import com.formdev.flatlaf.FlatLightLaf;
import fungsi.koneksiDB;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class KhanzaHMSAnjungan {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            System.setProperty("flatlaf.animation", "true");
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> {
            new HalamanUtamaDepan().setVisible(true);
            for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
                System.out.println("Printer ditemukan: " + ps.getName());
                if (ps.getName().equals(koneksiDB.PRINTER_BARCODE())) {
                    System.out.println("Setting PRINTER_BARCODE menggunakan printer: " + ps.getName());
                }
                if (ps.getName().equals(koneksiDB.PRINTER_REGISTRASI())) {
                    System.out.println("Setting PRINTER_REGISTRASI menggunakan printer: " + ps.getName());
                }
                if (ps.getName().equals(koneksiDB.PRINTER_ANTRIAN())) {
                    System.out.println("Setting PRINTER_ANTRIAN menggunakan printer: " + ps.getName());
                }
            }
        });
    }
}
