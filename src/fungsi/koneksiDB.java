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
    private static String var = "";

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
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return prop.getProperty("PRINTER_REGISTRASI");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String PRINTER_BARCODE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return prop.getProperty("PRINTER_BARCODE");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String PRINTER_ANTRIAN() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PRINTER_ANTRIAN", "");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static int PRINTJUMLAHBARCODE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return Integer.parseInt(prop.getProperty("PRINTJUMLAHBARCODE", "3"));
        } catch (Exception e) {
            return 3;
        }
    }
    
    public static String URLFINGERPRINTBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return prop.getProperty("URLFINGERPRINTBPJS");
        } catch (Exception e) {
            return "";
        }
    }

    public static String URLAPLIKASIFINGERPRINTBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return prop.getProperty("URLAPLIKASIFINGERPRINTBPJS");
        } catch (Exception e) {
            return "";
        }
    }

    public static String USERFINGERPRINTBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return EnkripsiAES.decrypt(prop.getProperty("USERFINGERPRINTBPJS"));
        } catch (Exception e) {
            return "";
        }
    }

    public static String PASSFINGERPRINTBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return EnkripsiAES.decrypt(prop.getProperty("PASSWORDFINGERPRINTBPJS"));
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String URLAPLIKASIFRISTABPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/apm.xml"));
            return prop.getProperty("URLAPLIKASIFRISTABPJS");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String AUTOBUKAAPLIKASI() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("AUTOBUKAAPLIKASI");
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
    
    public static String[] TOMBOLDIMATIKAN() {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("TOMBOLDIMATIKAN", "").trim().toLowerCase().replaceAll("\\s+", "").split(",");
        } catch (Exception e) {
            return null;
        }
    }

    public static String HOST() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("HOST"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String DATABASE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("DATABASE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PORT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PORT"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USER() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("USER"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CARICEPAT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("CARICEPAT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String HOSTHYBRIDWEB() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("HOSTHYBRIDWEB"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String HYBRIDWEB() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("HYBRIDWEB");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PORTWEB() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("PORTWEB");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String ANTRIAN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("ANTRIAN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String ALARMAPOTEK() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("ALARMAPOTEK");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String FORMALARMAPOTEK() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("FORMALARMAPOTEK");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String ALARMLAB() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("ALARMLAB");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String FORMALARMLAB() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("FORMALARMLAB");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String ALARMRADIOLOGI() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("ALARMRADIOLOGI");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String FORMALARMRADIOLOGI() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("FORMALARMRADIOLOGI");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String ALARMRSISRUTE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("ALARMRSISRUTE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String MENUTRANSPARAN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUTRANSPARAN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPIBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIBPJS");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPIMOBILEJKN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIMOBILEJKN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String SECRETKEYAPIBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIBPJS"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USERKEYAPIBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIBPJS"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CONSIDAPIBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIBPJS"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String SECRETKEYAPIMOBILEJKN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIMOBILEJKN"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USERKEYAPIMOBILEJKN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIMOBILEJKN"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CONSIDAPIMOBILEJKN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIMOBILEJKN"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPIAPLICARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIAPLICARE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String SECRETKEYAPIAPLICARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIAPLICARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CONSIDAPIAPLICARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIAPLICARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPIPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIPCARE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String SECRETKEYAPIPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIPCARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CONSIDAPIPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIPCARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSPCARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USERPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("USERPCARE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String DIVREGPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("DIVREGPCARE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String KACABPCARE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("KACABPCARE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPISISRUTE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPISISRUTE");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String IDSISRUTE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("IDSISRUTE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSSISRUTE() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSSISRUTE"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPISIRS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPISIRS");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String IDSIRS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("IDSIRS"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSSIRS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSSIRS"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPISITT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPISITT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String IDSITT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("IDSITT"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSSITT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSSITT"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String KABUPATENSITT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("KABUPATENSITT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String KAMARAKTIFRANAP() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("KAMARAKTIFRANAP");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String DOKTERAKTIFKASIRRALAN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("DOKTERAKTIFKASIRRALAN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String POLIAKTIFKASIRRALAN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("POLIAKTIFKASIRRALAN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String RUANGANAKTIFINVENTARIS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("RUANGANAKTIFINVENTARIS");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String BASENOREG() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("BASENOREG");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URUTNOREG() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URUTNOREG");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String JADWALDOKTERDIREGISTRASI() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("JADWALDOKTERDIREGISTRASI");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String IPPRINTERTRACER() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("IPPRINTERTRACER");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLAPIINHEALTH() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIINHEALTH");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String TOKENINHEALTH() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("TOKENINHEALTH"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PEMBULATANHARGAOBAT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("PEMBULATANHARGAOBAT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String AKTIFKANBATCHOBAT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("AKTIFKANBATCHOBAT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String CETAKRINCIANOBAT() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("CETAKRINCIANOBAT");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String AKTIFKANBILLINGPARSIAL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("AKTIFKANBILLINGPARSIAL");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLDUKCAPILJAKARTA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLDUKCAPILJAKARTA");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USERDUKCAPILJAKARTA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("USERDUKCAPILJAKARTA"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSDUKCAPILJAKARTA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSDUKCAPILJAKARTA"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String VAR1DUKCAPILJAKARTA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("VAR1DUKCAPILJAKARTA");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String VAR2DUKCAPILJAKARTA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("VAR2DUKCAPILJAKARTA");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String URLDUKCAPIL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLDUKCAPIL");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String USERDUKCAPIL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("USERDUKCAPIL"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String PASSDUKCAPIL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("PASSDUKCAPIL"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String IPUSERDUKCAPIL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("IPUSERDUKCAPIL");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String AKTIFKANTRACKSQL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            
            return EnkripsiAES.decrypt(prop.getProperty("AKTIFKANTRACKSQL"));
        } catch (Exception e) {
            return "";
        }
    }

    public static String HOSTWSLICA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("HOSTWSLICA");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String KEYWSLICA() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("KEYWSLICA"));
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

}
