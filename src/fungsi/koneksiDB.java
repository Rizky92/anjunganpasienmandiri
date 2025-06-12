/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import AESsecurity.EnkripsiAES;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author khanzasoft
 */
public class koneksiDB {

    private static Connection connection = null;
    private static final Properties prop = new Properties();
    private static final MysqlDataSource dataSource = new MysqlDataSource();

    public koneksiDB() {
    }

    public static Connection condb() {
        try {
            if (connection == null || connection.isClosed()) {
                try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                    prop.loadFromXML(fis);
                    dataSource.setURL("jdbc:mysql://" + EnkripsiAES.decrypt(prop.getProperty("HOST")) + ":" + EnkripsiAES.decrypt(prop.getProperty("PORT")) + "/" + EnkripsiAES.decrypt(prop.getProperty("DATABASE")) + "?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useCompression=true");
                    dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                    dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                    // dataSource.setCachePreparedStatements(true);
                    dataSource.setUseCompression(true);
                    // dataSource.setAutoReconnectForPools(true);
                    // dataSource.setUseLocalSessionState(true);
                    // dataSource.setUseLocalTransactionState(true);

                    int retries = 3;
                    while (retries > 0) {
                        try {
                            connection = dataSource.getConnection();
                            System.out.println("\n"
                                + "  Koneksi Berhasil. Sorry bro loading, silahkan baca dulu.... \n\n"
                                + "  Software ini adalah Software Menejemen Rumah Sakit/Klinik/\n"
                                + "  Puskesmas yang gratis dan boleh digunakan siapa saja tanpa dikenai \n"
                                + "  biaya apapun. Dilarang keras memperjualbelikan/mengambil \n"
                                + "  keuntungan dari Software ini dalam bentuk apapun tanpa seijin pembuat \n"
                                + "  software (Khanza.Soft Media).\n\n"
                                + "  #    ____  ___  __  __  ____   ____    _  __ _                              \n"
                                + "  #   / ___||_ _||  \\/  ||  _ \\ / ___|  | |/ /| |__    __ _  _ __   ____ __ _ \n"
                                + "  #   \\___ \\ | | | |\\/| || |_) |\\___ \\  | ' / | '_ \\  / _` || '_ \\ |_  // _` |\n"
                                + "  #    ___) || | | |  | ||  _ <  ___) | | . \\ | | | || (_| || | | | / /| (_| |\n"
                                + "  #   |____/|___||_|  |_||_| \\_\\|____/  |_|\\_\\|_| |_| \\__,_||_| |_|/___|\\__,_|\n"
                                + "  #                                                                           \n\n"
                                + "  Lisensi yang dianut di software ini https://en.wikipedia.org/wiki/Aladdin_Free_Public_License \n"
                                + "  Informasi dan panduan bisa dicek di halaman https://github.com/mas-elkhanza/SIMRS-Khanza/wiki \n"
                                + "  Bagi yang ingin berdonasi untuk pengembangan aplikasi ini bisa ke BSI 1015369872 atas nama Windiarto");
                            break;
                        } catch (SQLException e) {
                            retries--;
                            JOptionPane.showMessageDialog(null, "Gagal koneksi ke database. Sisa percobaan : " + retries);
                            if (retries == 0) {
                                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal. Silakan periksa koneksi jaringan atau konfigurasi database.");
                                throw new SQLException("Gagal koneksi ke database setelah beberapa percobaan.", e);
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new SQLException("Thread terinterupsi saat mencoba koneksi." + ie);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new SQLException("Notif : " + e);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(koneksiDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    public static String PRINTER_REGISTRASI() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PRINTER_REGISTRASI", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String PRINTER_BARCODE() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PRINTER_BARCODE", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String PRINTER_ANTRIAN() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PRINTER_ANTRIAN", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static int PRINTJUMLAHBARCODE() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return Integer.parseInt(prop.getProperty("PRINTJUMLAHBARCODE", "3").trim());
        } catch (Exception e) {
            return 3;
        }
    }

    public static String URLAPLIKASIFINGERPRINTBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("URLAPLIKASIFINGERPRINTBPJS", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String USERFINGERPRINTBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("USERFINGERPRINTBPJS", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String PASSFINGERPRINTBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("PASSFINGERPRINTBPJS", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String URLAPLIKASIFRISTABPJS() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("URLAPLIKASIFRISTABPJS", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String AUTOBUKAAPLIKASI() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("AUTOBUKAAPLIKASI").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean ANTRIANPREFIXHURUF() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("ANTRIANPREFIXHURUF", "no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] PREFIXHURUFAKTIF() {
        if (!ANTRIANPREFIXHURUF()) {
            return null;
        }
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PREFIXHURUFAKTIF", "").trim().replaceAll("\\s+", "").split(",");
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean AKTIFKANREGISTRASIBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("AKTIFKANREGISTRASIBPJS", "no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean AKTIFKANREGISTRASIEKSEKUTIF() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("AKTIFKANREGISTRASIEKSEKUTIF", "no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    public static String KODEPOLIEKSEKUTIF() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("KODEPOLIEKSEKUTIF", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String JENISBAYARPOLIEKSEKUTIF() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("JENISBAYARPOLIEKSEKUTIF", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String URLAPIBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("URLAPIBPJS", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String SECRETKEYAPIBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIBPJS", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String USERKEYAPIBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIBPJS", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String CONSIDAPIBPJS() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIBPJS", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String URLAPIMOBILEJKN() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("URLAPIMOBILEJKN", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String SECRETKEYAPIMOBILEJKN() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIMOBILEJKN", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String USERKEYAPIMOBILEJKN() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIMOBILEJKN", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String CONSIDAPIMOBILEJKN() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIMOBILEJKN", EnkripsiAES.encrypt(""))).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String BASENOREG() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("BASENOREG", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String URUTNOREG() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("URUTNOREG", "").trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean JADWALDOKTERDIREGISTRASI() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("JADWALDOKTERDIREGISTRASI", "no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean AKTIFKANTRACKSQL() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty("AKTIFKANTRACKSQL", EnkripsiAES.encrypt("no"))).trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }
}
