/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgAdmin.java
 *
 * Created on 04 Des 13, 12:59:34
 */
package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSCekReferensiDokterDPJP1;
import bridging.BPJSCekReferensiPenyakit;
import bridging.BPJSCekRiwayatPelayanan;
import bridging.BPJSCekRiwayatRujukanTerakhir;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author Kode
 */
public class DlgRegistrasiSEPPertama extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps3;
    private ResultSet rs, rs3;
    private ApiBPJS api = new ApiBPJS();
    private BPJSCekReferensiDokterDPJP1 dokter = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit penyakit = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS poli = new DlgCariPoliBPJS(null, true);
    private DlgCariPoli polimapping = new DlgCariPoli(null, true);
    private DlgCariDokter2 doktermapping = new DlgCariDokter2(null, true);
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private BPJSCekRiwayatRujukanTerakhir rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekRiwayatPelayanan historiPelayanan = new BPJSCekRiwayatPelayanan(null, true);
    private String umur = "0",
        sttsumur = "Th",
        hari = "",
        kode_dokter = "",
        kode_poli = "",
        nama_instansi,
        alamat_instansi,
        kabupaten,
        propinsi,
        kontak,
        email,
        kdkel = "",
        kdkec = "",
        kdkab = "",
        kdprop = "",
        nosisrute = "",
        BASENOREG = "",
        URUTNOREG = "",
        URLAPIBPJS = "",
        klg = "SAUDARA",
        statuspasien = "",
        pengurutan = "",
        tahun = "",
        bulan = "",
        posisitahun = "",
        awalantahun = "",
        awalanbulan = "",
        no_ktp = "",
        tmp_lahir = "",
        nm_ibu = "",
        alamat = "",
        pekerjaan = "",
        no_tlp = "",
        tglkkl = "0000-00-00",
        umurdaftar = "0",
        namakeluarga = "",
        no_peserta = "",
        kelurahan = "",
        kecamatan = "",
        datajam = "",
        jamselesai = "",
        jammulai = "",
        kabupatenpj = "",
        hariawal = "",
        requestJson,
        URL = "",
        nosep = "",
        user = "",
        prb = "",
        peserta = "",
        kodedokterreg = "",
        kodepolireg = "",
        status = "Baru",
        utc = "",
        jeniskunjungan = "",
        nomorreg = "",
        AUTOBUKAAPLIKASI = koneksiDB.AUTOBUKAAPLIKASI().trim().toLowerCase(),
        URLAPLIKASIFINGERPRINTBPJS = koneksiDB.URLAPLIKASIFINGERPRINTBPJS(),
        URLFINGERPRINTBPJS = koneksiDB.URLFINGERPRINTBPJS(),
        USERFINGERPRINTBPJS = koneksiDB.USERFINGERPRINTBPJS(),
        PASSFINGERPRINTBPJS = koneksiDB.PASSFINGERPRINTBPJS(),
        PRINTER_REGISTRASI = koneksiDB.PRINTER_REGISTRASI(),
        PRINTER_BARCODE = koneksiDB.PRINTER_BARCODE(),
        tampilkantni = Sequel.cariIsi("select tampilkan_tni_polri from set_tni_polri"),
        aksi = "";
    private int kuota = 0;
    private Properties prop = new Properties();
    private File file;
    private DlgCariPoli poli2 = new DlgCariPoli(null, true);

    private FileWriter fileWriter;
    private String iyem;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;
    private Calendar cal = Calendar.getInstance();
    private boolean statusfinger = false, aplikasiAktif = false, fristaAktif = false;
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode nameNode;
    private int day = cal.get(Calendar.DAY_OF_WEEK);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date parsedDate;

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgRegistrasiSEPPertama(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();
        JumlahBarcode.setDocument(new batasInput((byte) 3).getOnlyAngka(JumlahBarcode));

        try {
            ps = koneksi.prepareStatement(
                "select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari from pasien "
                + "inner join kelurahan inner join kecamatan inner join kabupaten inner join penjab "
                + "on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_pj=penjab.kd_pj "
                + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                + "where pasien.no_rkm_medis=?");
        } catch (Exception ex) {
            System.out.println(ex);
        }

        try {
            ps = koneksi.prepareStatement("select nama_instansi, alamat_instansi, kabupaten, propinsi, aktifkan, wallpaper,kontak,email,logo from setting");
            rs = ps.executeQuery();
            while (rs.next()) {
                nama_instansi = rs.getString("nama_instansi");
                alamat_instansi = rs.getString("alamat_instansi");
                kabupaten = rs.getString("kabupaten");
                propinsi = rs.getString("propinsi");
                kontak = rs.getString("kontak");
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    NmDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    if (JenisPelayanan.getSelectedIndex() == 1) {
                        KdDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        NmDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    }
                    KdDPJP.requestFocus();

                }
            }
        });

        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.getTable().getSelectedRow() != -1) {
                    KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 0).toString());
                    NmPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 1).toString());
                    KdDPJP.requestFocus();

                }
            }
        });

        polimapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (polimapping.getTable().getSelectedRow() != -1) {
                    KdPoliTerapi.setText(polimapping.getTable().getValueAt(polimapping.getTable().getSelectedRow(), 0).toString());
                    NmPoliTerapi.setText(polimapping.getTable().getValueAt(polimapping.getTable().getSelectedRow(), 1).toString());
                    KodeDokterTerapi.requestFocus();

                }
            }
        });

        doktermapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (doktermapping.getTable().getSelectedRow() != -1) {
                    KodeDokterTerapi.setText(doktermapping.getTable().getValueAt(doktermapping.getTable().getSelectedRow(), 0).toString());
                    NmDokterTerapi.setText(doktermapping.getTable().getValueAt(doktermapping.getTable().getSelectedRow(), 1).toString());
                    KodeDokterTerapi.requestFocus();

                }
            }
        });

        penyakit.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (penyakit.getTable().getSelectedRow() != -1) {

                    KdPenyakit.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString());
                    NmPenyakit.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 2).toString());
                    KdPenyakit.requestFocus();

                }
            }
        });

        rujukanterakhir.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (rujukanterakhir.getTable().getSelectedRow() != -1) {
                    KdPenyakit.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 0).toString());
                    NmPenyakit.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 1).toString());
                    NoRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 2).toString());
                    KdPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 3).toString());
                    NmPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 4).toString());
                    KdPpkRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 6).toString());
                    NmPpkRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 7).toString());
                    Valid.SetTgl(TanggalRujuk, rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 5).toString());
                    Catatan.requestFocus();
                }
            }
        });

        historiPelayanan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (historiPelayanan.getTable().getSelectedRow() != -1) {
                    if ((historiPelayanan.getTable().getSelectedColumn() == 6) || (historiPelayanan.getTable().getSelectedColumn() == 7)) {
                        NoRujukan.setText(historiPelayanan.getTable().getValueAt(historiPelayanan.getTable().getSelectedRow(), historiPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                NoRujukan.requestFocus();
            }
        });

        URUTNOREG = koneksiDB.URUTNOREG();
        BASENOREG = koneksiDB.BASENOREG();
        URLAPIBPJS = koneksiDB.URLAPIBPJS();
        URLFINGERPRINTBPJS = koneksiDB.URLFINGERPRINTBPJS();
        USERFINGERPRINTBPJS = koneksiDB.USERFINGERPRINTBPJS();
        PASSFINGERPRINTBPJS = koneksiDB.PASSFINGERPRINTBPJS();
        URLAPLIKASIFINGERPRINTBPJS = koneksiDB.URLAPLIKASIFINGERPRINTBPJS();

        KdPPK.setText(Sequel.cariIsi("select setting.kode_ppk from setting"));
        NmPPK.setText(Sequel.cariIsi("select setting.nama_instansi from setting"));
        JumlahBarcode.setText("3");
        isForm();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LblKdPoli = new component.Label();
        LblKdDokter = new component.Label();
        NoReg = new component.TextBox();
        NoRawat = new component.TextBox();
        Biaya = new component.TextBox();
        TAlmt = new component.Label();
        TPngJwb = new component.Label();
        THbngn = new component.Label();
        NoTelpPasien = new component.Label();
        kdpoli = new widget.TextBox();
        TBiaya = new widget.TextBox();
        Kdpnj = new widget.TextBox();
        nmpnj = new widget.TextBox();
        TNoRw = new widget.TextBox();
        NoRujukMasuk = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        WindowAksi = new javax.swing.JDialog();
        internalFrame1 = new widget.InternalFrame();
        pwUserId = new widget.PasswordBox();
        pwPass = new widget.PasswordBox();
        btnAksiKonfirmasi = new widget.Button();
        btnAksiBatal = new widget.Button();
        label1 = new widget.Label();
        label2 = new widget.Label();
        label3 = new widget.Label();
        lblTerapi = new widget.Label();
        KdPoliTerapi = new widget.TextBox();
        KodeDokterTerapi = new widget.TextBox();
        NmDokterTerapi = new widget.TextBox();
        NmPoliTerapi = new widget.TextBox();
        btnPoliTerapi = new widget.Button();
        btnDokterTerapi = new widget.Button();
        jPanel1 = new component.Panel();
        panelAtas = new component.Panel();
        panelUtama = new javax.swing.JPanel();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        NoKartu = new widget.TextBox();
        jLabel20 = new widget.Label();
        TanggalSEP = new widget.Tanggal();
        jLabel22 = new widget.Label();
        TanggalRujuk = new widget.Tanggal();
        jLabel23 = new widget.Label();
        NoRujukan = new widget.TextBox();
        jLabel10 = new widget.Label();
        KdPpkRujukan = new widget.TextBox();
        NmPpkRujukan = new widget.TextBox();
        jLabel11 = new widget.Label();
        KdPenyakit = new widget.TextBox();
        NmPenyakit = new widget.TextBox();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        LabelPoli = new widget.Label();
        LabelKelas = new widget.Label();
        Kelas = new widget.ComboBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel18 = new widget.Label();
        JK = new widget.TextBox();
        jLabel24 = new widget.Label();
        JenisPeserta = new widget.TextBox();
        jLabel25 = new widget.Label();
        Status = new widget.TextBox();
        jLabel27 = new widget.Label();
        AsalRujukan = new widget.ComboBox();
        NoTelp = new widget.TextBox();
        Katarak = new widget.ComboBox();
        jLabel37 = new widget.Label();
        LabelPoli2 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        btnCariDokter = new widget.Button();
        jLabel56 = new widget.Label();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        NoSKDP = new widget.TextBox();
        jLabel26 = new widget.Label();
        NIK = new widget.TextBox();
        jLabel7 = new widget.Label();
        btnCariPoli = new widget.Button();
        btnCariDiagnosaAwal = new widget.Button();
        btnRiwayatRujukan = new widget.Button();
        btnRiwayatPelayananBPJS = new widget.Button();
        btnFingerprint = new widget.Button();
        btnFrista = new widget.Button();
        panelTambahan = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        form = new javax.swing.JPanel();
        jLabel13 = new widget.Label();
        JenisPelayanan = new widget.ComboBox();
        jLabel42 = new widget.Label();
        TujuanKunjungan = new widget.ComboBox();
        jLabel43 = new widget.Label();
        FlagProsedur = new widget.ComboBox();
        jLabel44 = new widget.Label();
        Penunjang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        AsesmenPoli = new widget.ComboBox();
        LabelPoli7 = new widget.Label();
        KdDPJPLayanan = new widget.TextBox();
        NmDPJPLayanan = new widget.TextBox();
        jLabel9 = new widget.Label();
        KdPPK = new widget.TextBox();
        NmPPK = new widget.TextBox();
        jLabel55 = new widget.Label();
        LakaLantas = new widget.ComboBox();
        jLabel38 = new widget.Label();
        TanggalKKL = new widget.Tanggal();
        jLabel36 = new widget.Label();
        Keterangan = new widget.TextBox();
        jLabel40 = new widget.Label();
        Suplesi = new widget.ComboBox();
        jLabel41 = new widget.Label();
        NoSEPSuplesi = new widget.TextBox();
        LabelPoli3 = new widget.Label();
        KdPropinsi = new widget.TextBox();
        NmPropinsi = new widget.TextBox();
        LabelPoli4 = new widget.Label();
        KdKabupaten = new widget.TextBox();
        NmKabupaten = new widget.TextBox();
        LabelPoli5 = new widget.Label();
        KdKecamatan = new widget.TextBox();
        NmKecamatan = new widget.TextBox();
        jLabel14 = new widget.Label();
        Catatan = new widget.TextBox();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        jLabel15 = new widget.Label();
        JumlahBarcode = new widget.TextBox();
        panelBawah = new javax.swing.JPanel();
        btnSimpan = new component.Button();
        btnKeluar = new component.Button();

        LblKdPoli.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblKdPoli.setText("Norm");
        LblKdPoli.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LblKdPoli.setPreferredSize(new java.awt.Dimension(20, 14));

        LblKdDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblKdDokter.setText("Norm");
        LblKdDokter.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LblKdDokter.setPreferredSize(new java.awt.Dimension(20, 14));

        NoReg.setPreferredSize(new java.awt.Dimension(320, 30));
        NoReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoRegActionPerformed(evt);
            }
        });
        NoReg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRegKeyPressed(evt);
            }
        });

        NoRawat.setPreferredSize(new java.awt.Dimension(320, 30));
        NoRawat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoRawatActionPerformed(evt);
            }
        });
        NoRawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRawatKeyPressed(evt);
            }
        });

        Biaya.setPreferredSize(new java.awt.Dimension(320, 30));
        Biaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BiayaActionPerformed(evt);
            }
        });
        Biaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BiayaKeyPressed(evt);
            }
        });

        TAlmt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TAlmt.setText("Norm");
        TAlmt.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        TAlmt.setPreferredSize(new java.awt.Dimension(20, 14));

        TPngJwb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TPngJwb.setText("Norm");
        TPngJwb.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        TPngJwb.setPreferredSize(new java.awt.Dimension(20, 14));

        THbngn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        THbngn.setText("Norm");
        THbngn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        THbngn.setPreferredSize(new java.awt.Dimension(20, 14));

        NoTelpPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        NoTelpPasien.setText("Norm");
        NoTelpPasien.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        NoTelpPasien.setPreferredSize(new java.awt.Dimension(20, 14));

        kdpoli.setHighlighter(null);
        kdpoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpoliKeyPressed(evt);
            }
        });

        TBiaya.setText("0");
        TBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBiayaKeyPressed(evt);
            }
        });

        Kdpnj.setHighlighter(null);
        Kdpnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdpnjKeyPressed(evt);
            }
        });

        nmpnj.setHighlighter(null);
        nmpnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nmpnjKeyPressed(evt);
            }
        });

        TNoRw.setText("0");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });

        NoRujukMasuk.setText("0");
        NoRujukMasuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRujukMasukKeyPressed(evt);
            }
        });

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(95, 23));
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });

        WindowAksi.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowAksi.setModal(true);
        WindowAksi.setUndecorated(true);
        WindowAksi.setResizable(false);

        internalFrame1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pwUserId.setForeground(new java.awt.Color(40, 40, 40));
        pwUserId.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        pwUserId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwUserIdKeyPressed(evt);
            }
        });
        internalFrame1.add(pwUserId, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 230, 23));

        pwPass.setForeground(new java.awt.Color(40, 40, 40));
        pwPass.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pwPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwPassKeyPressed(evt);
            }
        });
        internalFrame1.add(pwPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 230, 23));

        btnAksiKonfirmasi.setText("Konfirmasi");
        btnAksiKonfirmasi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAksiKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiKonfirmasiActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiKonfirmasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, -1, -1));

        btnAksiBatal.setText("Batal");
        btnAksiBatal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAksiBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiBatalActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        label1.setText("User ID :");
        label1.setFocusable(false);
        label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        internalFrame1.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 120, 23));

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        internalFrame1.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 120, 23));

        label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label3.setText("Konfirmasi Aksi");
        label3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        internalFrame1.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 400, -1));

        WindowAksi.getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        lblTerapi.setForeground(new java.awt.Color(0, 131, 62));
        lblTerapi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTerapi.setText("Terapi / Rehabilitasi Medik");
        lblTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lblTerapi.setPreferredSize(new java.awt.Dimension(55, 23));

        KdPoliTerapi.setEditable(false);
        KdPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KdPoliTerapi.setHighlighter(null);

        KodeDokterTerapi.setEditable(false);
        KodeDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KodeDokterTerapi.setHighlighter(null);

        NmDokterTerapi.setEditable(false);
        NmDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        NmDokterTerapi.setHighlighter(null);

        NmPoliTerapi.setEditable(false);
        NmPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        NmPoliTerapi.setHighlighter(null);

        btnPoliTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnPoliTerapi.setMnemonic('X');
        btnPoliTerapi.setToolTipText("Alt+X");
        btnPoliTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnPoliTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPoliTerapiActionPerformed(evt);
            }
        });
        btnPoliTerapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPoliTerapiKeyPressed(evt);
            }
        });

        btnDokterTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDokterTerapi.setMnemonic('X');
        btnDokterTerapi.setToolTipText("Alt+X");
        btnDokterTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDokterTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterTerapiActionPerformed(evt);
            }
        });
        btnDokterTerapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDokterTerapiKeyPressed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(238, 238, 255));
        setForeground(new java.awt.Color(0, 131, 62));
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "DATA ELIGIBILITAS PESERTA JKN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(0, 131, 62));
        jPanel1.setMinimumSize(new java.awt.Dimension(543, 106));
        jPanel1.setPreferredSize(new java.awt.Dimension(543, 106));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        panelAtas.setBackground(new java.awt.Color(238, 238, 255));
        panelAtas.setBorder(null);
        panelAtas.setForeground(new java.awt.Color(0, 131, 62));
        panelAtas.setPreferredSize(new java.awt.Dimension(390, 290));
        panelAtas.setLayout(new java.awt.BorderLayout());

        panelUtama.setBackground(new java.awt.Color(238, 238, 255));
        panelUtama.setForeground(new java.awt.Color(0, 131, 62));
        panelUtama.setMinimumSize(new java.awt.Dimension(533, 290));
        panelUtama.setPreferredSize(new java.awt.Dimension(533, 310));
        panelUtama.setLayout(null);

        TPasien.setEditable(false);
        TPasien.setBackground(new java.awt.Color(245, 250, 240));
        TPasien.setHighlighter(null);
        panelUtama.add(TPasien);
        TPasien.setBounds(340, 10, 690, 30);

        TNoRM.setEditable(false);
        TNoRM.setBackground(new java.awt.Color(245, 250, 240));
        TNoRM.setHighlighter(null);
        TNoRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRMActionPerformed(evt);
            }
        });
        panelUtama.add(TNoRM);
        TNoRM.setBounds(230, 10, 110, 30);

        NoKartu.setEditable(false);
        NoKartu.setBackground(new java.awt.Color(255, 255, 153));
        NoKartu.setHighlighter(null);
        panelUtama.add(NoKartu);
        NoKartu.setBounds(730, 100, 300, 30);

        jLabel20.setForeground(new java.awt.Color(0, 131, 62));
        jLabel20.setText("Tgl. SEP :");
        jLabel20.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel20);
        jLabel20.setBounds(625, 190, 100, 30);

        TanggalSEP.setEditable(false);
        TanggalSEP.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
        TanggalSEP.setDisplayFormat("dd-MM-yyyy");
        TanggalSEP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalSEP.setOpaque(false);
        TanggalSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        TanggalSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalSEPKeyPressed(evt);
            }
        });
        panelUtama.add(TanggalSEP);
        TanggalSEP.setBounds(730, 190, 170, 30);

        jLabel22.setForeground(new java.awt.Color(0, 131, 62));
        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel22);
        jLabel22.setBounds(625, 160, 100, 30);

        TanggalRujuk.setEditable(false);
        TanggalRujuk.setForeground(new java.awt.Color(50, 70, 50));
        TanggalRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
        TanggalRujuk.setDisplayFormat("dd-MM-yyyy");
        TanggalRujuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalRujuk.setOpaque(false);
        TanggalRujuk.setPreferredSize(new java.awt.Dimension(95, 23));
        TanggalRujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalRujukKeyPressed(evt);
            }
        });
        panelUtama.add(TanggalRujuk);
        TanggalRujuk.setBounds(730, 160, 170, 30);

        jLabel23.setForeground(new java.awt.Color(0, 131, 62));
        jLabel23.setText("No. SKDP / Surat Kontrol :");
        jLabel23.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel23);
        jLabel23.setBounds(75, 70, 150, 30);

        NoRujukan.setEditable(false);
        NoRujukan.setBackground(new java.awt.Color(255, 255, 153));
        NoRujukan.setHighlighter(null);
        NoRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRujukanKeyPressed(evt);
            }
        });
        panelUtama.add(NoRujukan);
        NoRujukan.setBounds(230, 100, 340, 30);

        jLabel10.setForeground(new java.awt.Color(0, 131, 62));
        jLabel10.setText("PPK Rujukan :");
        jLabel10.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel10);
        jLabel10.setBounds(75, 130, 150, 30);

        KdPpkRujukan.setEditable(false);
        KdPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        KdPpkRujukan.setHighlighter(null);
        panelUtama.add(KdPpkRujukan);
        KdPpkRujukan.setBounds(230, 130, 80, 30);

        NmPpkRujukan.setEditable(false);
        NmPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        NmPpkRujukan.setHighlighter(null);
        panelUtama.add(NmPpkRujukan);
        NmPpkRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setForeground(new java.awt.Color(0, 131, 62));
        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel11);
        jLabel11.setBounds(75, 160, 150, 30);

        KdPenyakit.setEditable(false);
        KdPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        KdPenyakit.setHighlighter(null);
        panelUtama.add(KdPenyakit);
        KdPenyakit.setBounds(230, 160, 80, 30);

        NmPenyakit.setEditable(false);
        NmPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        NmPenyakit.setHighlighter(null);
        panelUtama.add(NmPenyakit);
        NmPenyakit.setBounds(310, 160, 260, 30);

        NmPoli.setEditable(false);
        NmPoli.setBackground(new java.awt.Color(255, 255, 153));
        NmPoli.setHighlighter(null);
        panelUtama.add(NmPoli);
        NmPoli.setBounds(310, 190, 260, 30);

        KdPoli.setEditable(false);
        KdPoli.setBackground(new java.awt.Color(255, 255, 153));
        KdPoli.setHighlighter(null);
        panelUtama.add(KdPoli);
        KdPoli.setBounds(230, 190, 80, 30);

        LabelPoli.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(LabelPoli);
        LabelPoli.setBounds(75, 190, 150, 30);

        LabelKelas.setForeground(new java.awt.Color(0, 131, 62));
        LabelKelas.setText("Kelas :");
        LabelKelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(LabelKelas);
        LabelKelas.setBounds(75, 250, 150, 30);

        Kelas.setForeground(new java.awt.Color(0, 131, 62));
        Kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        Kelas.setSelectedIndex(2);
        Kelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Kelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KelasKeyPressed(evt);
            }
        });
        panelUtama.add(Kelas);
        Kelas.setBounds(230, 250, 150, 30);

        jLabel8.setForeground(new java.awt.Color(0, 131, 62));
        jLabel8.setText("Data Pasien :");
        jLabel8.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel8);
        jLabel8.setBounds(75, 10, 150, 30);

        TglLahir.setEditable(false);
        TglLahir.setBackground(new java.awt.Color(245, 250, 240));
        TglLahir.setHighlighter(null);
        panelUtama.add(TglLahir);
        TglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setForeground(new java.awt.Color(0, 131, 62));
        jLabel18.setText("J. K. :");
        jLabel18.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(jLabel18);
        jLabel18.setBounds(910, 40, 30, 30);

        JK.setEditable(false);
        JK.setBackground(new java.awt.Color(245, 250, 240));
        JK.setHighlighter(null);
        panelUtama.add(JK);
        JK.setBounds(940, 40, 90, 30);

        jLabel24.setForeground(new java.awt.Color(0, 131, 62));
        jLabel24.setText("Jenis Peserta :");
        jLabel24.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel24);
        jLabel24.setBounds(625, 40, 100, 30);

        JenisPeserta.setEditable(false);
        JenisPeserta.setBackground(new java.awt.Color(245, 250, 240));
        JenisPeserta.setHighlighter(null);
        panelUtama.add(JenisPeserta);
        JenisPeserta.setBounds(730, 40, 173, 30);

        jLabel25.setForeground(new java.awt.Color(0, 131, 62));
        jLabel25.setText("Status :");
        jLabel25.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel25);
        jLabel25.setBounds(370, 40, 50, 30);

        Status.setEditable(false);
        Status.setBackground(new java.awt.Color(245, 250, 240));
        Status.setHighlighter(null);
        panelUtama.add(Status);
        Status.setBounds(420, 40, 150, 30);

        jLabel27.setForeground(new java.awt.Color(0, 131, 62));
        jLabel27.setText("Asal Rujukan :");
        jLabel27.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(jLabel27);
        jLabel27.setBounds(625, 130, 100, 30);

        AsalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        AsalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        AsalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        AsalRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsalRujukanKeyPressed(evt);
            }
        });
        panelUtama.add(AsalRujukan);
        AsalRujukan.setBounds(730, 130, 170, 30);

        NoTelp.setHighlighter(null);
        NoTelp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpKeyPressed(evt);
            }
        });
        panelUtama.add(NoTelp);
        NoTelp.setBounds(730, 250, 170, 30);

        Katarak.setForeground(new java.awt.Color(0, 131, 62));
        Katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Katarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        Katarak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KatarakKeyPressed(evt);
            }
        });
        panelUtama.add(Katarak);
        Katarak.setBounds(730, 220, 170, 30);

        jLabel37.setForeground(new java.awt.Color(0, 131, 62));
        jLabel37.setText("Katarak :");
        jLabel37.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(jLabel37);
        jLabel37.setBounds(625, 220, 100, 30);

        LabelPoli2.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli2.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(LabelPoli2);
        LabelPoli2.setBounds(75, 220, 150, 30);

        KdDPJP.setEditable(false);
        KdDPJP.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJP.setHighlighter(null);
        panelUtama.add(KdDPJP);
        KdDPJP.setBounds(230, 220, 80, 30);

        NmDPJP.setEditable(false);
        NmDPJP.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJP.setHighlighter(null);
        panelUtama.add(NmDPJP);
        NmDPJP.setBounds(310, 220, 260, 30);

        btnCariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariDokter.setMnemonic('X');
        btnCariDokter.setToolTipText("Alt+X");
        btnCariDokter.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDokterActionPerformed(evt);
            }
        });
        btnCariDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCariDokterKeyPressed(evt);
            }
        });
        panelUtama.add(btnCariDokter);
        btnCariDokter.setBounds(570, 220, 40, 30);

        jLabel56.setForeground(new java.awt.Color(0, 131, 62));
        jLabel56.setText("No. Telp :");
        jLabel56.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel56);
        jLabel56.setBounds(625, 250, 100, 30);

        jLabel12.setForeground(new java.awt.Color(0, 131, 62));
        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel12);
        jLabel12.setBounds(75, 40, 150, 30);

        jLabel6.setForeground(new java.awt.Color(0, 131, 62));
        jLabel6.setText("NIK :");
        jLabel6.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(jLabel6);
        jLabel6.setBounds(625, 70, 100, 30);

        NoSKDP.setEditable(false);
        NoSKDP.setBackground(new java.awt.Color(255, 255, 153));
        NoSKDP.setHighlighter(null);
        NoSKDP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSKDPKeyPressed(evt);
            }
        });
        panelUtama.add(NoSKDP);
        NoSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setForeground(new java.awt.Color(0, 131, 62));
        jLabel26.setText("No. Rujukan :");
        jLabel26.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel26);
        jLabel26.setBounds(75, 100, 150, 30);

        NIK.setEditable(false);
        NIK.setBackground(new java.awt.Color(255, 255, 153));
        NIK.setHighlighter(null);
        panelUtama.add(NIK);
        NIK.setBounds(730, 70, 300, 30);

        jLabel7.setForeground(new java.awt.Color(0, 131, 62));
        jLabel7.setText("No. Peserta :");
        jLabel7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        panelUtama.add(jLabel7);
        jLabel7.setBounds(625, 100, 100, 30);

        btnCariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariPoli.setMnemonic('X');
        btnCariPoli.setToolTipText("Alt+X");
        btnCariPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPoliActionPerformed(evt);
            }
        });
        btnCariPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCariPoliKeyPressed(evt);
            }
        });
        panelUtama.add(btnCariPoli);
        btnCariPoli.setBounds(570, 190, 40, 30);

        btnCariDiagnosaAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariDiagnosaAwal.setMnemonic('X');
        btnCariDiagnosaAwal.setToolTipText("Alt+X");
        btnCariDiagnosaAwal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariDiagnosaAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDiagnosaAwalActionPerformed(evt);
            }
        });
        btnCariDiagnosaAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCariDiagnosaAwalKeyPressed(evt);
            }
        });
        panelUtama.add(btnCariDiagnosaAwal);
        btnCariDiagnosaAwal.setBounds(570, 160, 40, 30);

        btnRiwayatRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatRujukan.setMnemonic('X');
        btnRiwayatRujukan.setToolTipText("Alt+X");
        btnRiwayatRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnRiwayatRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatRujukanActionPerformed(evt);
            }
        });
        btnRiwayatRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRiwayatRujukanKeyPressed(evt);
            }
        });
        panelUtama.add(btnRiwayatRujukan);
        btnRiwayatRujukan.setBounds(570, 100, 40, 30);

        btnRiwayatPelayananBPJS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayananBPJS.setMnemonic('X');
        btnRiwayatPelayananBPJS.setText("Riwayat Layanan BPJS");
        btnRiwayatPelayananBPJS.setToolTipText("Alt+X");
        btnRiwayatPelayananBPJS.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnRiwayatPelayananBPJS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRiwayatPelayananBPJS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananBPJSActionPerformed(evt);
            }
        });
        btnRiwayatPelayananBPJS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRiwayatPelayananBPJSKeyPressed(evt);
            }
        });
        panelUtama.add(btnRiwayatPelayananBPJS);
        btnRiwayatPelayananBPJS.setBounds(980, 220, 220, 30);

        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setMnemonic('X');
        btnFingerprint.setText("Fingerprint");
        btnFingerprint.setToolTipText("Alt+X");
        btnFingerprint.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnFingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFingerprint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerprintActionPerformed(evt);
            }
        });
        btnFingerprint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnFingerprintKeyPressed(evt);
            }
        });
        panelUtama.add(btnFingerprint);
        btnFingerprint.setBounds(1080, 100, 100, 80);

        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-scan.png"))); // NOI18N
        btnFrista.setMnemonic('X');
        btnFrista.setText("FRISTA");
        btnFrista.setToolTipText("Alt+X");
        btnFrista.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnFrista.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFrista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFrista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFristaActionPerformed(evt);
            }
        });
        btnFrista.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnFristaKeyPressed(evt);
            }
        });
        panelUtama.add(btnFrista);
        btnFrista.setBounds(1080, 10, 100, 80);

        panelAtas.add(panelUtama, java.awt.BorderLayout.PAGE_START);

        panelTambahan.setBackground(new java.awt.Color(238, 238, 255));
        panelTambahan.setForeground(new java.awt.Color(0, 131, 62));
        panelTambahan.setLayout(new java.awt.BorderLayout());

        ChkInput.setBackground(new java.awt.Color(238, 238, 255));
        ChkInput.setBorder(null);
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Informasi tambahan :.");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 30));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        panelTambahan.add(ChkInput, java.awt.BorderLayout.PAGE_START);

        form.setBackground(new java.awt.Color(238, 238, 255));
        form.setForeground(new java.awt.Color(0, 131, 62));
        form.setPreferredSize(new java.awt.Dimension(533, 120));
        form.setLayout(null);

        jLabel13.setForeground(new java.awt.Color(0, 131, 62));
        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel13);
        jLabel13.setBounds(75, 10, 150, 30);

        JenisPelayanan.setBackground(new java.awt.Color(255, 255, 153));
        JenisPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        JenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        JenisPelayanan.setSelectedIndex(1);
        JenisPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        JenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JenisPelayananItemStateChanged(evt);
            }
        });
        JenisPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisPelayananKeyPressed(evt);
            }
        });
        form.add(JenisPelayanan);
        JenisPelayanan.setBounds(230, 10, 150, 30);

        jLabel42.setForeground(new java.awt.Color(0, 131, 62));
        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel42);
        jLabel42.setBounds(75, 40, 150, 30);

        TujuanKunjungan.setBackground(new java.awt.Color(255, 255, 153));
        TujuanKunjungan.setForeground(new java.awt.Color(0, 131, 62));
        TujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        TujuanKunjungan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TujuanKunjunganItemStateChanged(evt);
            }
        });
        TujuanKunjungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TujuanKunjunganKeyPressed(evt);
            }
        });
        form.add(TujuanKunjungan);
        TujuanKunjungan.setBounds(230, 40, 340, 30);

        jLabel43.setForeground(new java.awt.Color(0, 131, 62));
        jLabel43.setText("Flag Prosedur :");
        jLabel43.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel43);
        jLabel43.setBounds(75, 70, 150, 30);

        FlagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        FlagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        FlagProsedur.setEnabled(false);
        FlagProsedur.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        FlagProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FlagProsedurKeyPressed(evt);
            }
        });
        form.add(FlagProsedur);
        FlagProsedur.setBounds(230, 70, 340, 30);

        jLabel44.setForeground(new java.awt.Color(0, 131, 62));
        jLabel44.setText("Penunjang :");
        jLabel44.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel44);
        jLabel44.setBounds(75, 100, 150, 30);

        Penunjang.setForeground(new java.awt.Color(0, 131, 62));
        Penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        Penunjang.setEnabled(false);
        Penunjang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Penunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenunjangKeyPressed(evt);
            }
        });
        form.add(Penunjang);
        Penunjang.setBounds(230, 100, 340, 30);

        jLabel45.setForeground(new java.awt.Color(0, 131, 62));
        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel45);
        jLabel45.setBounds(75, 130, 150, 30);

        AsesmenPoli.setBackground(new java.awt.Color(255, 255, 153));
        AsesmenPoli.setForeground(new java.awt.Color(0, 131, 62));
        AsesmenPoli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        AsesmenPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        AsesmenPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsesmenPoliKeyPressed(evt);
            }
        });
        form.add(AsesmenPoli);
        AsesmenPoli.setBounds(230, 130, 340, 30);

        LabelPoli7.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(LabelPoli7);
        LabelPoli7.setBounds(75, 160, 150, 30);

        KdDPJPLayanan.setEditable(false);
        KdDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJPLayanan.setHighlighter(null);
        form.add(KdDPJPLayanan);
        KdDPJPLayanan.setBounds(230, 160, 80, 30);

        NmDPJPLayanan.setEditable(false);
        NmDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJPLayanan.setHighlighter(null);
        form.add(NmDPJPLayanan);
        NmDPJPLayanan.setBounds(310, 160, 260, 30);

        jLabel9.setForeground(new java.awt.Color(0, 131, 62));
        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel9);
        jLabel9.setBounds(75, 190, 150, 30);

        KdPPK.setEditable(false);
        KdPPK.setBackground(new java.awt.Color(245, 250, 240));
        KdPPK.setHighlighter(null);
        form.add(KdPPK);
        KdPPK.setBounds(230, 190, 80, 30);

        NmPPK.setEditable(false);
        NmPPK.setBackground(new java.awt.Color(245, 250, 240));
        NmPPK.setHighlighter(null);
        form.add(NmPPK);
        NmPPK.setBounds(310, 190, 260, 30);

        jLabel55.setForeground(new java.awt.Color(0, 131, 62));
        jLabel55.setText("Laka Lantas :");
        jLabel55.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(jLabel55);
        jLabel55.setBounds(625, 10, 100, 30);

        LakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        LakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        LakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        LakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LakaLantasItemStateChanged(evt);
            }
        });
        LakaLantas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LakaLantasKeyPressed(evt);
            }
        });
        form.add(LakaLantas);
        LakaLantas.setBounds(730, 10, 170, 30);

        jLabel38.setForeground(new java.awt.Color(0, 131, 62));
        jLabel38.setText("Tgl KLL :");
        jLabel38.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel38);
        jLabel38.setBounds(625, 40, 100, 30);

        TanggalKKL.setEditable(false);
        TanggalKKL.setForeground(new java.awt.Color(50, 70, 50));
        TanggalKKL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
        TanggalKKL.setDisplayFormat("dd-MM-yyyy");
        TanggalKKL.setEnabled(false);
        TanggalKKL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalKKL.setOpaque(false);
        TanggalKKL.setPreferredSize(new java.awt.Dimension(64, 25));
        TanggalKKL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKKLKeyPressed(evt);
            }
        });
        form.add(TanggalKKL);
        TanggalKKL.setBounds(730, 40, 170, 30);

        jLabel36.setForeground(new java.awt.Color(0, 131, 62));
        jLabel36.setText("Keterangan :");
        jLabel36.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel36);
        jLabel36.setBounds(625, 70, 100, 30);

        Keterangan.setEditable(false);
        Keterangan.setHighlighter(null);
        Keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeteranganKeyPressed(evt);
            }
        });
        form.add(Keterangan);
        Keterangan.setBounds(730, 70, 300, 30);

        jLabel40.setForeground(new java.awt.Color(0, 131, 62));
        jLabel40.setText("Suplesi :");
        jLabel40.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(jLabel40);
        jLabel40.setBounds(625, 100, 100, 30);

        Suplesi.setForeground(new java.awt.Color(0, 131, 62));
        Suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Suplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        Suplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuplesiKeyPressed(evt);
            }
        });
        form.add(Suplesi);
        Suplesi.setBounds(730, 100, 90, 30);

        jLabel41.setForeground(new java.awt.Color(0, 131, 62));
        jLabel41.setText("No. SEP :");
        jLabel41.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel41);
        jLabel41.setBounds(820, 100, 68, 30);

        NoSEPSuplesi.setHighlighter(null);
        NoSEPSuplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSEPSuplesiKeyPressed(evt);
            }
        });
        form.add(NoSEPSuplesi);
        NoSEPSuplesi.setBounds(890, 100, 140, 30);

        LabelPoli3.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli3.setText("Propinsi KLL :");
        LabelPoli3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(LabelPoli3);
        LabelPoli3.setBounds(625, 130, 100, 30);

        KdPropinsi.setEditable(false);
        KdPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        KdPropinsi.setHighlighter(null);
        form.add(KdPropinsi);
        KdPropinsi.setBounds(730, 130, 55, 30);

        NmPropinsi.setEditable(false);
        NmPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        NmPropinsi.setHighlighter(null);
        form.add(NmPropinsi);
        NmPropinsi.setBounds(790, 130, 240, 30);

        LabelPoli4.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli4.setText("Kabupaten KLL :");
        LabelPoli4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(LabelPoli4);
        LabelPoli4.setBounds(625, 160, 100, 30);

        KdKabupaten.setEditable(false);
        KdKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        KdKabupaten.setHighlighter(null);
        form.add(KdKabupaten);
        KdKabupaten.setBounds(730, 160, 55, 30);

        NmKabupaten.setEditable(false);
        NmKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        NmKabupaten.setHighlighter(null);
        form.add(NmKabupaten);
        NmKabupaten.setBounds(790, 160, 240, 30);

        LabelPoli5.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli5.setText("Kecamatan KLL :");
        LabelPoli5.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(LabelPoli5);
        LabelPoli5.setBounds(625, 190, 100, 30);

        KdKecamatan.setEditable(false);
        KdKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        KdKecamatan.setHighlighter(null);
        form.add(KdKecamatan);
        KdKecamatan.setBounds(730, 190, 55, 30);

        NmKecamatan.setEditable(false);
        NmKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        NmKecamatan.setHighlighter(null);
        form.add(NmKecamatan);
        NmKecamatan.setBounds(790, 190, 240, 30);

        jLabel14.setForeground(new java.awt.Color(0, 131, 62));
        jLabel14.setText("Catatan :");
        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(jLabel14);
        jLabel14.setBounds(625, 220, 100, 30);

        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        Catatan.setHighlighter(null);
        Catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanKeyPressed(evt);
            }
        });
        form.add(Catatan);
        Catatan.setBounds(730, 220, 300, 30);

        btnApprovalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnApprovalFP.setMnemonic('X');
        btnApprovalFP.setText("Approval FP BPJS");
        btnApprovalFP.setToolTipText("Alt+X");
        btnApprovalFP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnApprovalFP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnApprovalFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApprovalFPActionPerformed(evt);
            }
        });
        btnApprovalFP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnApprovalFPKeyPressed(evt);
            }
        });
        form.add(btnApprovalFP);
        btnApprovalFP.setBounds(1080, 190, 190, 50);

        btnPengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnPengajuanFP.setMnemonic('X');
        btnPengajuanFP.setText("Pengajuan FP BPJS");
        btnPengajuanFP.setToolTipText("Alt+X");
        btnPengajuanFP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnPengajuanFP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPengajuanFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPengajuanFPActionPerformed(evt);
            }
        });
        btnPengajuanFP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPengajuanFPKeyPressed(evt);
            }
        });
        form.add(btnPengajuanFP);
        btnPengajuanFP.setBounds(1080, 120, 190, 50);

        jLabel15.setForeground(new java.awt.Color(0, 131, 62));
        jLabel15.setText("Jumlah Barcode :");
        jLabel15.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        form.add(jLabel15);
        jLabel15.setBounds(75, 220, 150, 30);

        JumlahBarcode.setText("3");
        JumlahBarcode.setHighlighter(null);
        JumlahBarcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JumlahBarcodeKeyPressed(evt);
            }
        });
        form.add(JumlahBarcode);
        JumlahBarcode.setBounds(230, 220, 50, 30);

        panelTambahan.add(form, java.awt.BorderLayout.CENTER);

        panelAtas.add(panelTambahan, java.awt.BorderLayout.CENTER);

        jPanel1.add(panelAtas, java.awt.BorderLayout.CENTER);

        panelBawah.setBackground(new java.awt.Color(238, 238, 255));
        panelBawah.setMinimumSize(new java.awt.Dimension(533, 120));
        panelBawah.setPreferredSize(new java.awt.Dimension(533, 120));

        btnSimpan.setForeground(new java.awt.Color(0, 131, 62));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("Konfirmasi");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 45));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        btnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSimpanKeyPressed(evt);
            }
        });
        panelBawah.add(btnSimpan);

        btnKeluar.setForeground(new java.awt.Color(0, 131, 62));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText("Batal");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKeluar.setPreferredSize(new java.awt.Dimension(300, 45));
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        btnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnKeluarKeyPressed(evt);
            }
        });
        panelBawah.add(btnKeluar);

        jPanel1.add(panelBawah, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void NoRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegActionPerformed

    private void NoRegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRegKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegKeyPressed

    private void NoRawatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRawatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatActionPerformed

    private void NoRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRawatKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatKeyPressed

    private void BiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaActionPerformed

    private void BiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BiayaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaKeyPressed

    private void btnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnKeluarActionPerformed(null);
        }
    }//GEN-LAST:event_btnKeluarKeyPressed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_btnSimpanKeyPressed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(NoKartu.getText());
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (NoKartu.getText().trim().equals("")) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
        } else if (Sequel.cariIntegerSmc("select count(*) from pasien where no_rkm_medis = ?", TNoRM.getText()) < 1) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, no RM tidak sesuai");
        } else if (KdPpkRujukan.getText().trim().equals("") || NmPpkRujukan.getText().trim().equals("")) {
            Valid.textKosong(KdPpkRujukan, "PPK Rujukan");
        } else if (KdPPK.getText().trim().equals("") || NmPPK.getText().trim().equals("")) {
            Valid.textKosong(KdPPK, "PPK Pelayanan");
        } else if (KdPenyakit.getText().trim().equals("") || NmPenyakit.getText().trim().equals("")) {
            Valid.textKosong(KdPenyakit, "Diagnosa");
        } else if (Catatan.getText().trim().equals("")) {
            Valid.textKosong(Catatan, "Catatan");
        } else if ((JenisPelayanan.getSelectedIndex() == 1) && (KdPoli.getText().trim().equals("") || NmPoli.getText().trim().equals(""))) {
            Valid.textKosong(KdPoli, "Poli Tujuan");
        } else if ((LakaLantas.getSelectedIndex() == 1) && Keterangan.getText().equals("")) {
            Valid.textKosong(Keterangan, "Keterangan");
        } else if (KdDPJP.getText().trim().equals("") || NmDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "DPJP");
        } else if (!statusfinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", TglLahir.getText()) >= 17 && JenisPelayanan.getSelectedIndex() != 0 && !KdPoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint");
            bukaAplikasiValidasi();
        } else {
            if (!KdPoliTerapi.getText().equals("")) {
                kodepolireg = KdPoliTerapi.getText();
            } else {
                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", KdPoli.getText());
            }

            if (!KodeDokterTerapi.getText().equals("")) {
                kodedokterreg = KodeDokterTerapi.getText();
            } else {
                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
            }

            isPoli();
            isCekPasien();
            isNumber();

            // cek apabila pasien sudah pernah diregistrasikan sebelumnya
            if (Sequel.cariIntegerSmc("select count(*) from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodepolireg, kodedokterreg, Kdpnj.getText()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Maaf, Telah terdaftar pemeriksaan hari ini. Mohon konfirmasi ke Bagian Admisi");
                emptTeks();
            } else {
                if (!registerPasien()) {
                    JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat pendaftaran pasien!");
                    this.setCursor(Cursor.getDefaultCursor());

                    return;
                }

                if (JenisPelayanan.getSelectedIndex() == 0) {
                    insertSEP();
                } else if (JenisPelayanan.getSelectedIndex() == 1) {
                    if (NmPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", no_peserta, JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(TanggalSEP)) >= 3) {
                            JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                SimpanAntrianOnSite();
                            }
                            insertSEP();
                        }
                    } else if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", no_peserta, JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(TanggalSEP)) >= 1) {
                            JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                SimpanAntrianOnSite();
                            }
                            insertSEP();
                        }
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCariDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCariDokterKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariDokterKeyPressed

    private void btnCariDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDokterActionPerformed
        dokter.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        dokter.setLocationRelativeTo(jPanel1);
        dokter.carinamadokter(KdPoli.getText(), NmPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_btnCariDokterActionPerformed

    private void AsesmenPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsesmenPoliKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AsesmenPoliKeyPressed

    private void PenunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PenunjangKeyPressed

    private void FlagProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FlagProsedurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_FlagProsedurKeyPressed

    private void TujuanKunjunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TujuanKunjunganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TujuanKunjunganKeyPressed

    private void TujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TujuanKunjunganItemStateChanged
        if (TujuanKunjungan.getSelectedIndex() == 0) {
            FlagProsedur.setEnabled(false);
            FlagProsedur.setSelectedIndex(0);
            Penunjang.setEnabled(false);
            Penunjang.setSelectedIndex(0);
            AsesmenPoli.setEnabled(true);
        } else {
            if (TujuanKunjungan.getSelectedIndex() == 1) {
                AsesmenPoli.setSelectedIndex(0);
                AsesmenPoli.setEnabled(false);
            } else {
                AsesmenPoli.setEnabled(true);
            }
            if (FlagProsedur.getSelectedIndex() == 0) {
                FlagProsedur.setSelectedIndex(2);
            }
            FlagProsedur.setEnabled(true);
            if (Penunjang.getSelectedIndex() == 0) {
                Penunjang.setSelectedIndex(10);
            }
            Penunjang.setEnabled(true);
        }
    }//GEN-LAST:event_TujuanKunjunganItemStateChanged

    private void NoSEPSuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSEPSuplesiKeyPressed

    }//GEN-LAST:event_NoSEPSuplesiKeyPressed

    private void SuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuplesiKeyPressed
        Valid.pindah(evt, Keterangan, NoSEPSuplesi);
    }//GEN-LAST:event_SuplesiKeyPressed

    private void KeteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeteranganKeyPressed
        Valid.pindah(evt, TanggalKKL, Suplesi);
    }//GEN-LAST:event_KeteranganKeyPressed

    private void TanggalKKLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKKLKeyPressed
        Valid.pindah(evt, LakaLantas, Keterangan);
    }//GEN-LAST:event_TanggalKKLKeyPressed

    private void KatarakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KatarakKeyPressed
        Valid.pindah(evt, Catatan, NoTelp);
    }//GEN-LAST:event_KatarakKeyPressed

    private void NoTelpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpKeyPressed
        Valid.pindah(evt, Katarak, LakaLantas);
    }//GEN-LAST:event_NoTelpKeyPressed

    private void AsalRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsalRujukanKeyPressed

    }//GEN-LAST:event_AsalRujukanKeyPressed

    private void LakaLantasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LakaLantasKeyPressed
        Valid.pindah(evt, NoTelp, TanggalKKL);
    }//GEN-LAST:event_LakaLantasKeyPressed

    private void LakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LakaLantasItemStateChanged
        if (LakaLantas.getSelectedIndex() == 0) {
            TanggalKKL.setEnabled(false);
            Keterangan.setEditable(false);
            Keterangan.setText("");
        } else {
            TanggalKKL.setEnabled(true);
            Keterangan.setEditable(true);
        }
    }//GEN-LAST:event_LakaLantasItemStateChanged

    private void KelasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KelasKeyPressed

    }//GEN-LAST:event_KelasKeyPressed

    private void JenisPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisPelayananKeyPressed

    }//GEN-LAST:event_JenisPelayananKeyPressed

    private void JenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JenisPelayananItemStateChanged
        if (JenisPelayanan.getSelectedIndex() == 0) {
            KdPoli.setText("");
            NmPoli.setText("");
            LabelPoli.setVisible(false);
            KdPoli.setVisible(false);
            NmPoli.setVisible(false);

            KdDPJPLayanan.setText("");
            NmDPJPLayanan.setText("");
            btnCariDokter.setEnabled(false);
        } else if (JenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            KdPoli.setVisible(true);
            NmPoli.setVisible(true);

            btnCariDokter.setEnabled(true);
        }
    }//GEN-LAST:event_JenisPelayananItemStateChanged

    private void CatatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanKeyPressed

    }//GEN-LAST:event_CatatanKeyPressed

    private void NoRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRujukanKeyPressed

    }//GEN-LAST:event_NoRujukanKeyPressed

    private void TanggalRujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalRujukKeyPressed
        Valid.pindah(evt, NoRujukan, TanggalSEP);
    }//GEN-LAST:event_TanggalRujukKeyPressed

    private void TanggalSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalSEPKeyPressed
        Valid.pindah(evt, TanggalRujuk, AsalRujukan);
    }//GEN-LAST:event_TanggalSEPKeyPressed

    private void TNoRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMActionPerformed

    private void kdpoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpoliKeyPressed

    }//GEN-LAST:event_kdpoliKeyPressed

    private void TBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBiayaKeyPressed

    }//GEN-LAST:event_TBiayaKeyPressed

    private void KdpnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdpnjKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdpnjKeyPressed

    private void nmpnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nmpnjKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nmpnjKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRwKeyPressed

    private void NoRujukMasukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRujukMasukKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRujukMasukKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalKeyPressed

    private void NoSKDPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSKDPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoSKDPKeyPressed

    private void btnCariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPoliActionPerformed
        poli.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        poli.tampil();
        poli.setLocationRelativeTo(jPanel1);
        poli.setVisible(true);
    }//GEN-LAST:event_btnCariPoliActionPerformed

    private void btnCariPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCariPoliKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariPoliKeyPressed

    private void btnCariDiagnosaAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDiagnosaAwalActionPerformed
        penyakit.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        penyakit.setLocationRelativeTo(jPanel1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnCariDiagnosaAwalActionPerformed

    private void btnCariDiagnosaAwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCariDiagnosaAwalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariDiagnosaAwalKeyPressed

    private void btnRiwayatRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatRujukanActionPerformed
        if (NoKartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
            rujukanterakhir.setLocationRelativeTo(jPanel1);
            rujukanterakhir.tampil(NoKartu.getText(), TPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnRiwayatRujukanActionPerformed

    private void btnRiwayatRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRiwayatRujukanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatRujukanKeyPressed

    private void btnRiwayatPelayananBPJSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananBPJSActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        historiPelayanan.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        historiPelayanan.setLocationRelativeTo(jPanel1);
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPelayananBPJSActionPerformed

    private void btnRiwayatPelayananBPJSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananBPJSKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatPelayananBPJSKeyPressed

    private void btnDokterTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterTerapiActionPerformed
        doktermapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        doktermapping.tampilDokterTerapi(KdDPJPLayanan.getText());
        doktermapping.setLocationRelativeTo(jPanel1);
        doktermapping.setVisible(true);
    }//GEN-LAST:event_btnDokterTerapiActionPerformed

    private void btnDokterTerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDokterTerapiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDokterTerapiKeyPressed

    private void btnPoliTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoliTerapiActionPerformed
        polimapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        polimapping.tampilPoliMapping(KdPoli.getText());
        polimapping.setLocationRelativeTo(jPanel1);
        polimapping.setVisible(true);
    }//GEN-LAST:event_btnPoliTerapiActionPerformed

    private void btnPoliTerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPoliTerapiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPoliTerapiKeyPressed

    private void btnApprovalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApprovalFPActionPerformed
        resetAksi();
        if (! NoKartu.getText().isBlank()) {
            aksi = "Approval";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnApprovalFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnApprovalFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnApprovalFPActionPerformed(null);
        }
    }//GEN-LAST:event_btnApprovalFPKeyPressed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (! NoKartu.getText().isBlank()) {
            aksi = "Pengajuan";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnPengajuanFPActionPerformed

    private void btnPengajuanFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPengajuanFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnPengajuanFPActionPerformed(null);
        }
    }//GEN-LAST:event_btnPengajuanFPKeyPressed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try {
                ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1");
                try {
                    ps.setString(1, new String(pwUserId.getPassword()));
                    ps.setString(2, new String(pwPass.getPassword()));
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (aksi.equals("Pengajuan")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                URL = URLAPIBPJS + "/Sep/pengajuanSEP";
                                requestJson = " {"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + NoKartu.getText() + "\","
                                    + "\"tglSep\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                                    + "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\","
                                    + "\"user\": \"NoRM:" + TNoRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(rootPane, "Pengajuan Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else if (aksi.equals("Approval")) {
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                URL = URLAPIBPJS + "/Sep/aprovalSEP";
                                requestJson = " {"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + NoKartu.getText() + "\","
                                    + "\"tglSep\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                                    + "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\","
                                    + "\"user\": \"NoRM:" + TNoRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(rootPane, "Approval Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void pwUserIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwUserIdKeyPressed
        Valid.pindah(evt, btnAksiBatal, pwPass);
    }//GEN-LAST:event_pwUserIdKeyPressed

    private void pwPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwPassKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAksiKonfirmasiActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            pwUserId.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            btnAksiKonfirmasi.requestFocus();
        }
    }//GEN-LAST:event_pwPassKeyPressed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        resetAksi();
        WindowAksi.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void JumlahBarcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JumlahBarcodeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JumlahBarcodeKeyPressed

    private void btnFingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerprintActionPerformed
        bukaAplikasiFingerprint();
    }//GEN-LAST:event_btnFingerprintActionPerformed

    private void btnFingerprintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnFingerprintKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFingerprintKeyPressed

    private void btnFristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFristaActionPerformed
        bukaAplikasiFrista();
    }//GEN-LAST:event_btnFristaActionPerformed

    private void btnFristaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnFristaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFristaKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRegistrasiSEPPertama dialog = new DlgRegistrasiSEPPertama(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.ComboBox AsalRujukan;
    private widget.ComboBox AsesmenPoli;
    private component.TextBox Biaya;
    private widget.TextBox Catatan;
    private widget.CekBox ChkInput;
    private widget.ComboBox FlagProsedur;
    private widget.TextBox JK;
    private widget.ComboBox JenisPelayanan;
    private widget.TextBox JenisPeserta;
    private widget.TextBox JumlahBarcode;
    private widget.ComboBox Katarak;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdDPJPLayanan;
    private widget.TextBox KdKabupaten;
    private widget.TextBox KdKecamatan;
    private widget.TextBox KdPPK;
    private widget.TextBox KdPenyakit;
    private widget.TextBox KdPoli;
    private widget.TextBox KdPoliTerapi;
    private widget.TextBox KdPpkRujukan;
    private widget.TextBox KdPropinsi;
    private widget.TextBox Kdpnj;
    private widget.ComboBox Kelas;
    private widget.TextBox Keterangan;
    private widget.TextBox KodeDokterTerapi;
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.ComboBox LakaLantas;
    private component.Label LblKdDokter;
    private component.Label LblKdPoli;
    private widget.TextBox NIK;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmDPJPLayanan;
    private widget.TextBox NmDokterTerapi;
    private widget.TextBox NmKabupaten;
    private widget.TextBox NmKecamatan;
    private widget.TextBox NmPPK;
    private widget.TextBox NmPenyakit;
    private widget.TextBox NmPoli;
    private widget.TextBox NmPoliTerapi;
    private widget.TextBox NmPpkRujukan;
    private widget.TextBox NmPropinsi;
    private widget.TextBox NoKartu;
    private component.TextBox NoRawat;
    private component.TextBox NoReg;
    private widget.TextBox NoRujukMasuk;
    private widget.TextBox NoRujukan;
    private widget.TextBox NoSEPSuplesi;
    private widget.TextBox NoSKDP;
    private widget.TextBox NoTelp;
    private component.Label NoTelpPasien;
    private widget.ComboBox Penunjang;
    private widget.TextBox Status;
    private widget.ComboBox Suplesi;
    private component.Label TAlmt;
    private widget.TextBox TBiaya;
    private component.Label THbngn;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private component.Label TPngJwb;
    private widget.Tanggal Tanggal;
    private widget.Tanggal TanggalKKL;
    private widget.Tanggal TanggalRujuk;
    private widget.Tanggal TanggalSEP;
    private widget.TextBox TglLahir;
    private widget.ComboBox TujuanKunjungan;
    private javax.swing.JDialog WindowAksi;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnApprovalFP;
    private widget.Button btnCariDiagnosaAwal;
    private widget.Button btnCariDokter;
    private widget.Button btnCariPoli;
    private widget.Button btnDokterTerapi;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private component.Button btnKeluar;
    private widget.Button btnPengajuanFP;
    private widget.Button btnPoliTerapi;
    private widget.Button btnRiwayatPelayananBPJS;
    private widget.Button btnRiwayatRujukan;
    private component.Button btnSimpan;
    private javax.swing.JPanel form;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel18;
    private widget.Label jLabel20;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private component.Panel jPanel1;
    private widget.TextBox kdpoli;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label lblTerapi;
    private widget.TextBox nmpnj;
    private component.Panel panelAtas;
    private javax.swing.JPanel panelBawah;
    private javax.swing.JPanel panelTambahan;
    private javax.swing.JPanel panelUtama;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    // End of variables declaration//GEN-END:variables

    private void isNumber() {
        switch (URUTNOREG) {
            case "poli":
                NoReg.setText(
                    Sequel.cariIsiSmc(
                        "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                        kodepolireg, Valid.getTglSmc(TanggalSEP)
                    )
                );
                break;
            case "dokter":
                NoReg.setText(
                    Sequel.cariIsiSmc(
                        "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                        kodedokterreg, Valid.getTglSmc(TanggalSEP)
                    )
                );
                break;
            case "dokter + poli":
                NoReg.setText(
                    Sequel.cariIsiSmc(
                        "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, Valid.getTglSmc(TanggalSEP)
                    )
                );
                break;
            default:
                NoReg.setText(
                    Sequel.cariIsiSmc(
                        "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, Valid.getTglSmc(TanggalSEP)
                    )
                );
                break;
        }

        TNoRw.setText(
            Sequel.cariIsiSmc(
                "select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?",
                Valid.getTglSmc(TanggalSEP)
            )
        );
    }

    private void tentukanHari() {
        try {
            java.sql.Date hariperiksa = java.sql.Date.valueOf(Valid.getTglSmc(TanggalSEP));
            cal.setTime(hariperiksa);
            day = cal.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case 1:
                    hari = "AKHAD";
                    break;
                case 2:
                    hari = "SENIN";
                    break;
                case 3:
                    hari = "SELASA";
                    break;
                case 4:
                    hari = "RABU";
                    break;
                case 5:
                    hari = "KAMIS";
                    break;
                case 6:
                    hari = "JUMAT";
                    break;
                case 7:
                    hari = "SABTU";
                    break;
                default:
                    break;
            }
            System.out.println(hari);

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

    }

    private void isCekPasien() {
        try {
            ps3 = koneksi.prepareStatement("select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun,pasien.no_peserta, "
                + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,pasien.no_ktp,pasien.no_tlp "
                + "from pasien inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel "
                + "inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab "
                + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                + "where pasien.no_rkm_medis=?");
            try {
                ps3.setString(1, Valid.SetTgl(TanggalSEP.getSelectedItem() + ""));
                ps3.setString(2, TNoRM.getText());
                rs = ps3.executeQuery();
                while (rs.next()) {
                    TAlmt.setText(rs.getString("asal"));
                    TPngJwb.setText(rs.getString("namakeluarga"));
                    THbngn.setText(rs.getString("keluarga"));
                    NoTelpPasien.setText(rs.getString("no_tlp"));
                    umur = "0";
                    sttsumur = "Th";
                    statuspasien = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umur = rs.getString("tahun");
                        sttsumur = "Th";
                    } else if (rs.getInt("tahun") == 0) {
                        if (rs.getInt("bulan") > 0) {
                            umur = rs.getString("bulan");
                            sttsumur = "Bl";
                        } else if (rs.getInt("bulan") == 0) {
                            umur = rs.getString("hari");
                            sttsumur = "Hr";
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps3 != null) {
                    ps3.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        status = "Baru";
        if (Sequel.cariInteger("select count(*) from reg_periksa where no_rkm_medis = ? and kd_poli = ?", TNoRM.getText(), kodepolireg) > 0) {
            status = "Lama";
        }

    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", TNoRw.getText());
        param.put("parameter", noSEP);
        param.put("namars", Sequel.cariIsi("select setting.nama_instansi from setting limit 1"));
        param.put("kotars", Sequel.cariIsi("select setting.kabupaten from setting limit 1"));

        if (JenisPelayanan.getSelectedIndex() == 0) {
            Valid.printReport("rptBridgingSEPAPM1.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            Valid.MyReport("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            Valid.printReport("rptBridgingSEPAPM2.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            Valid.MyReport("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }

        Valid.printReport("rptBarcodeRawatAPM.jasper", koneksiDB.PRINTER_BARCODE(), "::[ Barcode Perawatan ]::", Integer.parseInt(JumlahBarcode.getText().trim()), param);
        Valid.MyReport("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param);
    }

    private void insertSEP() {
        try {
            tglkkl = "0000-00-00";
            if (LakaLantas.getSelectedIndex() > 0) {
                tglkkl = Valid.SetTgl(TanggalKKL.getSelectedItem() + "");
            }
            utc = String.valueOf(api.GetUTCdatetimeAsString());

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            URL = URLAPIBPJS + "/SEP/2.0/insert";
            requestJson = "{"
                + "\"request\":{"
                + "\"t_sep\":{"
                + "\"noKartu\":\"" + NoKartu.getText() + "\","
                + "\"tglSep\":\"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                + "\"ppkPelayanan\":\"" + KdPPK.getText() + "\","
                + "\"jnsPelayanan\":\"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"klsRawat\":{"
                + "\"klsRawatHak\":\"" + Kelas.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"klsRawatNaik\":\"\","
                + "\"pembiayaan\":\"\","
                + "\"penanggungJawab\":\"\""
                + "},"
                + "\"noMR\":\"" + TNoRM.getText() + "\","
                + "\"rujukan\": {"
                + "\"asalRujukan\":\"" + AsalRujukan.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"tglRujukan\":\"" + Valid.SetTgl(TanggalRujuk.getSelectedItem() + "") + "\","
                + "\"noRujukan\":\"" + NoRujukan.getText() + "\","
                + "\"ppkRujukan\":\"" + KdPpkRujukan.getText() + "\""
                + "},"
                + "\"catatan\":\"" + Catatan.getText() + "\","
                + "\"diagAwal\":\"" + KdPenyakit.getText() + "\","
                + "\"poli\": {"
                + "\"tujuan\": \"" + KdPoli.getText() + "\","
                + "\"eksekutif\": \"0\""
                + "},"
                + "\"cob\": {"
                + "\"cob\": \"0\""
                + "},"
                + "\"katarak\": {"
                + "\"katarak\": \"" + Katarak.getSelectedItem().toString().substring(0, 1) + "\""
                + "},"
                + "\"jaminan\": {"
                + "\"lakaLantas\":\"" + LakaLantas.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"penjamin\": {"
                + "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\","
                + "\"keterangan\": \"" + Keterangan.getText() + "\","
                + "\"suplesi\": {"
                + "\"suplesi\": \"" + Suplesi.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"noSepSuplesi\": \"" + NoSEPSuplesi.getText() + "\","
                + "\"lokasiLaka\": {"
                + "\"kdPropinsi\": \"" + KdPropinsi.getText() + "\","
                + "\"kdKabupaten\": \"" + KdKabupaten.getText() + "\","
                + "\"kdKecamatan\": \"" + KdKecamatan.getText() + "\""
                + "}"
                + "}"
                + "}"
                + "},"
                + "\"tujuanKunj\": \"" + TujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\","
                + "\"flagProcedure\": \"" + (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\","
                + "\"kdPenunjang\": \"" + (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : "") + "\","
                + "\"assesmentPel\": \"" + (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : "") + "\","
                + "\"skdp\": {"
                + "\"noSurat\": \"" + NoSKDP.getText() + "\","
                + "\"kodeDPJP\": \"" + KdDPJP.getText() + "\""
                + "},"
                + "\"dpjpLayan\": \"" + (KdDPJPLayanan.getText().equals("") ? "" : KdDPJPLayanan.getText()) + "\","
                + "\"noTelp\": \"" + NoTelp.getText() + "\","
                + "\"user\":\"" + NoKartu.getText() + "\""
                + "}"
                + "}"
                + "}";

            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");

            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            JOptionPane.showMessageDialog(rootPane, "Respon BPJS : " + nameNode.path("message").asText());

            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                System.out.println("SEP berhasil terbit!");
                System.out.println("No. SEP: " + response.asText());

                String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", Valid.getTglSmc(TanggalSEP), TNoRM.getText(), kodepolireg, kodedokterreg);

                if (isNoRawat == null || (!isNoRawat.equals(TNoRw.getText()))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + TNoRw.getText());
                    System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(TanggalSEP));
                    System.out.println("No. Antrian: " + NoReg.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("No. RM: " + TNoRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Poli: " + kodepolireg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("======================================================");

                    return;
                }

                Sequel.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    TNoRw.getText(),
                    Valid.getTglSmc(TanggalSEP),
                    Valid.SetTgl(TanggalRujuk.getSelectedItem().toString()),
                    NoRujukan.getText(),
                    KdPpkRujukan.getText(),
                    NmPpkRujukan.getText(),
                    KdPPK.getText(),
                    NmPPK.getText(),
                    JenisPelayanan.getSelectedItem().toString().substring(0, 1),
                    Catatan.getText(),
                    KdPenyakit.getText(),
                    NmPenyakit.getText(),
                    KdPoli.getText(),
                    NmPoli.getText(),
                    Kelas.getSelectedItem().toString().substring(0, 1),
                    "",
                    "",
                    "",
                    LakaLantas.getSelectedItem().toString().substring(0, 1),
                    TNoRM.getText(),
                    TNoRM.getText(),
                    TPasien.getText(),
                    TglLahir.getText(),
                    JenisPeserta.getText(),
                    JK.getText(),
                    NoKartu.getText(),
                    "0000-00-00 00:00:00",
                    AsalRujukan.getSelectedItem().toString(),
                    "0. Tidak",
                    "0. Tidak",
                    NoTelp.getText(),
                    Katarak.getSelectedItem().toString(),
                    tglkkl,
                    Keterangan.getText(),
                    Suplesi.getSelectedItem().toString(),
                    NoSEPSuplesi.getText(),
                    KdPropinsi.getText(),
                    NmPropinsi.getText(),
                    KdKabupaten.getText(),
                    NmKabupaten.getText(),
                    KdKecamatan.getText(),
                    NmKecamatan.getText(),
                    NoSKDP.getText(),
                    KdDPJP.getText(),
                    NmDPJP.getText(),
                    TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                    (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                    (Penunjang.getSelectedIndex() > 0 ? String.valueOf(Penunjang.getSelectedIndex()) : ""),
                    (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
                    KdDPJPLayanan.getText(),
                    NmDPJPLayanan.getText()
                );

                if (!simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }

                if (JenisPelayanan.getSelectedIndex() == 1) {
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.getTglSmc(TanggalSEP), response.asText());
                }

                if (!prb.equals("")) {
                    Sequel.menyimpanSmc("bpjs_prb", null, response.asText(), prb);

                    prb = "";
                }

                if (Sequel.cariIntegerSmc(
                    "select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodedokterreg, kodepolireg
                ) == 1) {
                    Sequel.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodedokterreg, kodepolireg);
                }

                cetakRegistrasi(response.asText());

                emptTeks();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void cekFinger(String noka) {
        statusfinger = false;

        if (!NoKartu.getText().equals("")) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                URL = URLAPIBPJS + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + Valid.getTglSmc(TanggalSEP);
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("kodecekstatus : " + nameNode.path("code").asText());
                // System.out.println("message : "+nameNode.path("message").asText());
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    if (response.path("kode").asText().equals("1")) {
                        if (response.path("status").asText().contains(Sequel.cariIsi("select current_date()"))) {
                            statusfinger = true;
                        } else {
                            statusfinger = false;
                            JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Bridging : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih data peserta!");
        }
    }

    public void tampilKunjunganPertama(String noKartu) {
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
        KdPoliTerapi.setVisible(false);
        NmPoliTerapi.setVisible(false);
        KodeDokterTerapi.setVisible(false);
        NmDokterTerapi.setVisible(false);
        btnPoliTerapi.setVisible(false);
        btnDokterTerapi.setVisible(false);
        lblTerapi.setVisible(false);
        try {
            URL = URLAPIBPJS + "/Rujukan/Peserta/" + noKartu;
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                AsalRujukan.setSelectedIndex(0);
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                NoRujukan.setText(response.path("noKunjungan").asText());
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                    case "1":
                        Kelas.setSelectedIndex(0);
                        break;
                    case "2":
                        Kelas.setSelectedIndex(1);
                        break;
                    case "3":
                        Kelas.setSelectedIndex(2);
                        break;
                    default:
                        break;
                }
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                TPasien.setText(response.path("peserta").path("nama").asText());
                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                TNoRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", NoKartu.getText()));
                NIK.setText(response.path("peserta").path("nik").asText());
                if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                    NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                }
                JK.setText(response.path("peserta").path("sex").asText());
                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                isPoli();
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                    NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                JOptionPane.showMessageDialog(rootPane, "Pesan Pencarian Rujukan FKTP : " + nameNode.path("message").asText());
                try {
                    URL = URLAPIBPJS + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                    nameNode = root.path("metaData");
                    peserta = "";
                    if (nameNode.path("code").asText().equals("200")) {
                        AsalRujukan.setSelectedIndex(1);
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                        NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                        NoRujukan.setText(response.path("noKunjungan").asText());
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                            case "1":
                                Kelas.setSelectedIndex(0);
                                break;
                            case "2":
                                Kelas.setSelectedIndex(1);
                                break;
                            case "3":
                                Kelas.setSelectedIndex(2);
                                break;
                            default:
                                break;
                        }
                        prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                        TPasien.setText(response.path("peserta").path("nama").asText());
                        NoKartu.setText(response.path("peserta").path("noKartu").asText());
                        TNoRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", NoKartu.getText()));
                        NIK.setText(response.path("peserta").path("nik").asText());
                        if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                            NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        }
                        JK.setText(response.path("peserta").path("sex").asText());
                        Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                        NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                        JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                        NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                            NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        }
                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                        AsalRujukan.setSelectedIndex(1);
                        isNumber();
                        Kdpnj.setText("BPJ");
                        nmpnj.setText("BPJS");
                        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                    } else {
                        emptTeks();
                        JOptionPane.showMessageDialog(rootPane, "Pesan Pencarian Rujukan FKRTL : " + nameNode.path("message").asText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi Peserta : " + ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
        try {
            ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?");
            try {
                switch (day) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }
                ps.setString(1, kdpoli.getText());
                ps.setString(2, hari);
                rs = ps.executeQuery();
                if (rs.next()) {
                    KdDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    NmDPJP.setText(rs.getString("nm_dokter_bpjs"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilKunjunganBedaPoli(String noKartu) {
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
        KdPoliTerapi.setVisible(true);
        NmPoliTerapi.setVisible(true);
        KodeDokterTerapi.setVisible(true);
        NmDokterTerapi.setVisible(true);
        btnPoliTerapi.setVisible(true);
        btnDokterTerapi.setVisible(true);
        lblTerapi.setVisible(true);
        TujuanKunjungan.setSelectedIndex(0);
        FlagProsedur.setSelectedIndex(0);
        Penunjang.setSelectedIndex(0);
        AsesmenPoli.setSelectedIndex(1);
        try {
            URL = URLAPIBPJS + "/Rujukan/Peserta/" + noKartu;
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                AsalRujukan.setSelectedIndex(0);
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                NoRujukan.setText(response.path("noKunjungan").asText());
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                    case "1":
                        Kelas.setSelectedIndex(0);
                        break;
                    case "2":
                        Kelas.setSelectedIndex(1);
                        break;
                    case "3":
                        Kelas.setSelectedIndex(2);
                        break;
                    default:
                        break;
                }
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                TPasien.setText(response.path("peserta").path("nama").asText());
                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                TNoRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", NoKartu.getText()));
                NIK.setText(response.path("peserta").path("nik").asText());
                if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                    NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                }
                JK.setText(response.path("peserta").path("sex").asText());
                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                isPoli();
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                    NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                try {
                    URL = URLAPIBPJS + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                    nameNode = root.path("metaData");
                    peserta = "";
                    if (nameNode.path("code").asText().equals("200")) {
                        AsalRujukan.setSelectedIndex(1);
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                        NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                        NoRujukan.setText(response.path("noKunjungan").asText());
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                            case "1":
                                Kelas.setSelectedIndex(0);
                                break;
                            case "2":
                                Kelas.setSelectedIndex(1);
                                break;
                            case "3":
                                Kelas.setSelectedIndex(2);
                                break;
                            default:
                                break;
                        }
                        prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                        TPasien.setText(response.path("peserta").path("nama").asText());
                        NoKartu.setText(response.path("peserta").path("noKartu").asText());
                        TNoRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", NoKartu.getText()));
                        NIK.setText(response.path("peserta").path("nik").asText());
                        if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                            NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        }
                        JK.setText(response.path("peserta").path("sex").asText());
                        Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                        NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                        JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kdpoli.setText(Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                        kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                        NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                            NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        }
                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                        AsalRujukan.setSelectedIndex(1);
                        isNumber();
                        Kdpnj.setText("BPJ");
                        nmpnj.setText("BPJS");
                        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                    } else {
                        emptTeks();
                        System.out.println("Pesan pencarian rujukan FKTL : " + nameNode.path("message").asText());
                        JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi Peserta : " + ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
        try {
            ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?");
            try {
                switch (day) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }
                ps.setString(1, kdpoli.getText());
                ps.setString(2, hari);
                rs = ps.executeQuery();
                if (rs.next()) {
                    KdDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    NmDPJP.setText(rs.getString("nm_dokter_bpjs"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilKontrol(String noSKDP) {
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
        KdPoliTerapi.setVisible(false);
        NmPoliTerapi.setVisible(false);
        KodeDokterTerapi.setVisible(false);
        NmDokterTerapi.setVisible(false);
        btnPoliTerapi.setVisible(false);
        btnDokterTerapi.setVisible(false);
        lblTerapi.setVisible(false);
        try (PreparedStatement pskontrol = koneksi.prepareStatement(
            "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_kartu, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat "
            + "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
        )) {
            pskontrol.setString(1, noSKDP);
            try (ResultSet rskontrol = pskontrol.executeQuery()) {
                if (rskontrol.next()) {
                    if (!rskontrol.getString("tgl_rencana").equals(Valid.getTglSmc(TanggalSEP))) {
                        updateSuratKontrol(
                            rskontrol.getString("no_surat"), rskontrol.getString("no_sep"), rskontrol.getString("no_kartu"), Valid.getTglSmc(TanggalSEP),
                            rskontrol.getString("kd_dokter_bpjs"), rskontrol.getString("nm_dokter_bpjs"), rskontrol.getString("kd_poli_bpjs"), rskontrol.getString("nm_poli_bpjs")
                        );
                    }
                    if (rskontrol.getString("jnspelayanan").equals("1")) {
                        try {
                            URL = URLAPIBPJS + "/Peserta/nokartu/" + rskontrol.getString("no_kartu") + "/tglSEP/" + Valid.getTglSmc(TanggalSEP);
                            utc = String.valueOf(api.GetUTCdatetimeAsString());
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            requestEntity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                            nameNode = root.path("metaData");
                            System.out.println("URL : " + URL);
                            peserta = "";
                            if (nameNode.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                                KdPenyakit.setText("Z09.8");
                                NmPenyakit.setText("Z09.8 - Follow-up examination after other treatment for other conditions");
                                NoRujukan.setText(rskontrol.getString("no_sep"));
                                TujuanKunjungan.setSelectedIndex(0);
                                FlagProsedur.setSelectedIndex(0);
                                Penunjang.setSelectedIndex(0);
                                AsesmenPoli.setSelectedIndex(0);
                                AsalRujukan.setSelectedIndex(1);
                                KdPoli.setText(rskontrol.getString("kd_poli_bpjs"));
                                NmPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                KdDPJP.setText(rskontrol.getString("kd_dokter_bpjs"));
                                NmDPJP.setText(rskontrol.getString("nm_dokter_bpjs"));
                                KdDPJPLayanan.setText(KdDPJP.getText());
                                NmDPJPLayanan.setText(NmDPJP.getText());
                                kdpoli.setText(Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", KdPoli.getText()));
                                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", KdPoli.getText());
                                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                                NoSKDP.setText(rskontrol.getString("no_surat"));
                                switch (rskontrol.getString("klsrawat")) {
                                    case "1":
                                        Kelas.setSelectedIndex(0);
                                        break;
                                    case "2":
                                        Kelas.setSelectedIndex(1);
                                        break;
                                    case "3":
                                        Kelas.setSelectedIndex(2);
                                        break;
                                    default:
                                        break;
                                }
                                prb = response.path("informasi").path("prolanisPRB").asText();
                                if (prb.contains("null")) {
                                    prb = "";
                                }
                                peserta = response.path("jenisPeserta").path("keterangan").asText();
                                TPasien.setText(response.path("nama").asText());
                                NoKartu.setText(response.path("noKartu").asText());
                                TNoRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", NoKartu.getText()));
                                NIK.setText(response.path("nik").asText());
                                if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                                    NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                }
                                JK.setText(response.path("sex").asText());
                                Status.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                                TglLahir.setText(response.path("tglLahir").asText());
                                JenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                                KdPpkRujukan.setText(Sequel.cariIsiSmc("select kode_ppk from setting"));
                                NmPpkRujukan.setText(Sequel.cariIsiSmc("select nama_instansi from setting"));
                                isNumber();
                                Kdpnj.setText("BPJ");
                                nmpnj.setText("BPJS");
                                Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                                    NoTelp.setText(response.path("mr").path("noTelepon").asText());
                                }
                            } else {
                                emptTeks();
                                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi Peserta : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                            }
                        }
                    } else {
                        try {
                            if (rskontrol.getString("asal_rujukan").equals("1")) {
                                URL = URLAPIBPJS + "/Rujukan/" + rskontrol.getString("no_rujukan");
                            } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                URL = URLAPIBPJS + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                            }
                            utc = String.valueOf(api.GetUTCdatetimeAsString());
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            requestEntity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                            nameNode = root.path("metaData");
                            System.out.println("URL : " + URL);
                            peserta = "";
                            if (nameNode.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                                NoRujukan.setText(response.path("noKunjungan").asText());
                                NoSKDP.setText(rskontrol.getString("no_surat"));
                                KdPoli.setText(rskontrol.getString("kd_poli_bpjs"));
                                NmPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                KdDPJP.setText(rskontrol.getString("kd_dokter_bpjs"));
                                NmDPJP.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kdpoli.setText(Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", KdPoli.getText()));
                                KdDPJPLayanan.setText(KdDPJP.getText());
                                NmDPJPLayanan.setText(NmDPJP.getText());
                                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", KdPoli.getText());
                                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                                TujuanKunjungan.setSelectedIndex(2);
                                FlagProsedur.setSelectedIndex(0);
                                Penunjang.setSelectedIndex(0);
                                AsesmenPoli.setSelectedIndex(5);
                                if (rskontrol.getString("asal_rujukan").equals("2")) {
                                    AsalRujukan.setSelectedIndex(1);
                                } else {
                                    AsalRujukan.setSelectedIndex(0);
                                }
                                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                                    case "1":
                                        Kelas.setSelectedIndex(0);
                                        break;
                                    case "2":
                                        Kelas.setSelectedIndex(1);
                                        break;
                                    case "3":
                                        Kelas.setSelectedIndex(2);
                                        break;
                                    default:
                                        break;
                                }
                                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText();
                                if (prb.contains("null")) {
                                    prb = "";
                                }
                                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                                TPasien.setText(response.path("peserta").path("nama").asText());
                                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                                TNoRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", NoKartu.getText()));
                                NIK.setText(response.path("peserta").path("nik").asText());
                                if (NIK.getText().contains("null") || NIK.getText().isBlank()) {
                                    NIK.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                }
                                JK.setText(response.path("peserta").path("sex").asText());
                                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                                isPoli();
                                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                                isNumber();
                                Kdpnj.setText("BPJ");
                                nmpnj.setText("BPJS");
                                Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                                    NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                                }
                            } else {
                                emptTeks();
                                System.out.println("Pesan pencarian rujukan : " + nameNode.path("message").asText());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi Peserta : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(rootPane, "Maaf, Data surat kontrol tidak ditemukan...!!!");
        }
    }
    
    private void SimpanAntrianOnSite() {
        int angkaantrean = Integer.parseInt(NoReg.getText());
        jeniskunjungan = "1";
        String nomorreferensi = NoRujukan.getText();
        String responsecode = "200";
        if ((!NoRujukan.getText().equals("")) || (!NoSKDP.getText().equals(""))) {
            if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("")) {
                if (AsalRujukan.getSelectedIndex() == 0) {
                    jeniskunjungan = "1";
                    nomorreferensi = NoRujukan.getText();
                } else {
                    jeniskunjungan = "4";
                    nomorreferensi = NoRujukan.getText();
                }
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                jeniskunjungan = "3";
                nomorreferensi = NoSKDP.getText();
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("4. Atas Instruksi RS")) {
                jeniskunjungan = "2";
                nomorreferensi = NoRujukan.getText();
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("1. Poli spesialis tidak tersedia pada hari sebelumnya")) {
                jeniskunjungan = "2";
                nomorreferensi = NoRujukan.getText();
            } else {
                if (TujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && AsesmenPoli.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                    jeniskunjungan = "3";
                    nomorreferensi = NoSKDP.getText();
                } else {
                    jeniskunjungan = "2";
                    nomorreferensi = NoRujukan.getText();
                }
            }

            try {
                switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }

                try (PreparedStatement ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?")) {
                    ps.setString(1, hari);
                    ps.setString(2, kodepolireg);
                    ps.setString(3, kodedokterreg);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            jammulai = rs.getString("jam_mulai");
                            jamselesai = rs.getString("jam_selesai");
                            kuota = rs.getInt("kuota");
                            datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", Valid.getTglSmc(TanggalSEP), jammulai, String.valueOf(Integer.parseInt(NoReg.getText()) * 5));
                            parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datajam);
                        } else {
                            System.out.println("Jadwal tidak ditemukan...!!!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif jadwal : " + e);
                }

                if (!jeniskunjungan.isBlank() && !nomorreferensi.isBlank()) {
                    requestJson = "{"
                        + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                        + "\"jenispasien\": \"JKN\","
                        + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                        + "\"nik\": \"" + NIK.getText() + "\","
                        + "\"nohp\": \"" + NoTelp.getText() + "\","
                        + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                        + "\"namapoli\": \"" + NmPoli.getText() + "\","
                        + "\"pasienbaru\": 0,"
                        + "\"norm\": \"" + TNoRM.getText() + "\","
                        + "\"tanggalperiksa\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
                        + "\"kodedokter\": " + KdDPJP.getText() + ","
                        + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                        + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                        + "\"jeniskunjungan\": " + jeniskunjungan + ","
                        + "\"nomorreferensi\": \"" + nomorreferensi + "\","
                        + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                        + "\"angkaantrean\": " + angkaantrean + ","
                        + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                        + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                        + "\"kuotajkn\": " + kuota + ","
                        + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                        + "\"kuotanonjkn\": " + kuota + ","
                        + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                        + "}";
                    System.out.println("JSON : " + requestJson);
                    URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    System.out.println("URL : " + URL);
                    System.out.print("addantrean " + TNoRw.getText() + " : ");
                    try {
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        responsecode = nameNode.path("code").asText();
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                        System.out.println(nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (HttpClientErrorException e) {
                        responsecode = e.getStatusCode().toString();
                        System.out.println(e.getMessage());
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                    } catch (HttpServerErrorException e) {
                        responsecode = e.getStatusCode().toString();
                        System.out.println(e.getMessage());
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                if (!responsecode.equals("200")) {
                    if (!NoSKDP.getText().equals("")) {
                        try {
                            utc = String.valueOf(api.GetUTCdatetimeAsString());
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            requestJson = "{"
                                + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                                + "\"jenispasien\": \"JKN\","
                                + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                                + "\"nik\": \"" + NIK.getText() + "\","
                                + "\"nohp\": \"" + NoTelp.getText() + "\","
                                + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                                + "\"namapoli\": \"" + NmPoli.getText() + "\","
                                + "\"pasienbaru\": 0,"
                                + "\"norm\": \"" + TNoRM.getText() + "\","
                                + "\"tanggalperiksa\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
                                + "\"kodedokter\": " + KdDPJP.getText() + ","
                                + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                                + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                                + "\"jeniskunjungan\": " + jeniskunjungan + ","
                                + "\"nomorreferensi\": \"" + NoSKDP.getText() + "\","
                                + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                                + "\"angkaantrean\": " + angkaantrean + ","
                                + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                                + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotajkn\": " + kuota + ","
                                + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotanonjkn\": " + kuota + ","
                                + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                                + "}";
                            requestEntity = new HttpEntity(requestJson, headers);
                            URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL : " + URL);
                            System.out.println(requestEntity);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                            nameNode = root.path("metadata");
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                            System.out.println("respon WS BPJS Kirim Pakai SKDP : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                        } catch (HttpClientErrorException e) {
                            responsecode = e.getStatusCode().toString();
                            System.out.println(e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                        } catch (HttpServerErrorException e) {
                            responsecode = e.getStatusCode().toString();
                            System.out.println(e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                        } catch (Exception e) {
                            System.out.println("Notif SKDP : " + e);
                        }
                    }

                    if (!NoRujukan.getText().equals("")) {
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            utc = String.valueOf(api.GetUTCdatetimeAsString());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            requestJson = "{"
                                + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                                + "\"jenispasien\": \"JKN\","
                                + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                                + "\"nik\": \"" + NIK.getText() + "\","
                                + "\"nohp\": \"" + NoTelp.getText() + "\","
                                + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                                + "\"namapoli\": \"" + NmPoli.getText() + "\","
                                + "\"pasienbaru\": 0,"
                                + "\"norm\": \"" + TNoRM.getText() + "\","
                                + "\"tanggalperiksa\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
                                + "\"kodedokter\": " + KdDPJP.getText() + ","
                                + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                                + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                                + "\"jeniskunjungan\": " + jeniskunjungan + ","
                                + "\"nomorreferensi\": \"" + NoRujukan.getText() + "\","
                                + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                                + "\"angkaantrean\": " + angkaantrean + ","
                                + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                                + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotajkn\": " + kuota + ","
                                + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotanonjkn\": " + kuota + ","
                                + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                                + "}";
                            System.out.println("JSON : " + requestJson + "\n");
                            requestEntity = new HttpEntity(requestJson, headers);
                            URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL Kirim Pakai No.Rujuk : " + URL);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                            nameNode = root.path("metadata");
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                            System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                        } catch (HttpClientErrorException e) {
                            responsecode = e.getStatusCode().toString();
                            System.out.println(e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                        } catch (HttpServerErrorException e) {
                            responsecode = e.getStatusCode().toString();
                            System.out.println(e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                        } catch (Exception e) {
                            System.out.println("Notif No.Rujuk : " + e);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void SimpanAntrianOnSite2() {
        int angkaantrean = Integer.parseInt(NoReg.getText());
        if ((!NoRujukan.getText().equals("")) || (!NoSKDP.getText().equals(""))) {
            if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("")) {
                if (AsalRujukan.getSelectedIndex() == 0) {
                    jeniskunjungan = "1";
                } else {
                    jeniskunjungan = "4";
                }
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                jeniskunjungan = "3";
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("4. Atas Instruksi RS")) {
                jeniskunjungan = "2";
            } else if (TujuanKunjungan.getSelectedItem().toString().equals("0. Normal") && FlagProsedur.getSelectedItem().toString().equals("") && Penunjang.getSelectedItem().toString().equals("") && AsesmenPoli.getSelectedItem().toString().equals("1. Poli spesialis tidak tersedia pada hari sebelumnya")) {
                jeniskunjungan = "2";
            } else {
                if (TujuanKunjungan.getSelectedItem().toString().equals("2. Konsul Dokter") && AsesmenPoli.getSelectedItem().toString().equals("5. Tujuan Kontrol")) {
                    jeniskunjungan = "3";
                } else {
                    jeniskunjungan = "2";
                }
            }

            try {
                day = cal.get(Calendar.DAY_OF_WEEK);
                switch (day) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }

                ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?");
                try {
                    ps.setString(1, hari);
                    ps.setString(2, kodepolireg);
                    ps.setString(3, kodedokterreg);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        jammulai = rs.getString("jam_mulai");
                        jamselesai = rs.getString("jam_selesai");
                        kuota = rs.getInt("kuota");
                        datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", Valid.getTglSmc(TanggalSEP), jammulai, String.valueOf(Integer.parseInt(NoReg.getText()) * 5));
                        parsedDate = dateFormat.parse(datajam);
                    } else {
                        System.out.println("Jadwal tidak ditemukan...!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif jadwal: " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }

                if (!NoSKDP.getText().equals("")) {
                    try {
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestJson = "{"
                            + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                            + "\"jenispasien\": \"JKN\","
                            + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                            + "\"nik\": \"" + NIK.getText() + "\","
                            + "\"nohp\": \"" + NoTelp.getText() + "\","
                            + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                            + "\"namapoli\": \"" + NmPoli.getText() + "\","
                            + "\"pasienbaru\": 0,"
                            + "\"norm\": \"" + TNoRM.getText() + "\","
                            + "\"tanggalperiksa\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                            + "\"kodedokter\": " + KdDPJP.getText() + ","
                            + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                            + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                            + "\"jeniskunjungan\": " + jeniskunjungan + ","
                            + "\"nomorreferensi\": \"" + NoSKDP.getText() + "\","
                            + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                            + "\"angkaantrean\": " + angkaantrean + ","
                            + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                            + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                            + "\"kuotajkn\": " + kuota + ","
                            + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                            + "\"kuotanonjkn\": " + kuota + ","
                            + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                            + "}";
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL : " + URL);
                        System.out.println(requestEntity);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                        System.out.println("respon WS BPJS Kirim Pakai SKDP : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (Exception e) {
                        System.out.println("Notif SKDP : " + e);
                    }
                }
                
                if (!NoRujukan.getText().equals("")) {
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestJson = "{"
                            + "\"kodebooking\": \"" + TNoRw.getText() + "\","
                            + "\"jenispasien\": \"JKN\","
                            + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                            + "\"nik\": \"" + NIK.getText() + "\","
                            + "\"nohp\": \"" + NoTelp.getText() + "\","
                            + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                            + "\"namapoli\": \"" + NmPoli.getText() + "\","
                            + "\"pasienbaru\": 0,"
                            + "\"norm\": \"" + TNoRM.getText() + "\","
                            + "\"tanggalperiksa\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                            + "\"kodedokter\": " + KdDPJP.getText() + ","
                            + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                            + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                            + "\"jeniskunjungan\": " + jeniskunjungan + ","
                            + "\"nomorreferensi\": \"" + NoRujukan.getText() + "\","
                            + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                            + "\"angkaantrean\": " + angkaantrean + ","
                            + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                            + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                            + "\"kuotajkn\": " + kuota + ","
                            + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                            + "\"kuotanonjkn\": " + kuota + ","
                            + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                            + "}";
                        System.out.println("JSON : " + requestJson + "\n");
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL Kirim Pakai No.Rujuk : " + URL);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                        System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (Exception e) {
                        System.out.println("Notif No.Rujuk : " + e);
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void emptTeks() {
        TPasien.setText("");
        TanggalSEP.setDate(new Date());
        TanggalRujuk.setDate(new Date());
        TglLahir.setText("");
        NoKartu.setText("");
        JenisPeserta.setText("");
        Status.setText("");
        JK.setText("");
        NoRujukan.setText("");
        KdPpkRujukan.setText("");
        NmPpkRujukan.setText("");
        JenisPelayanan.setSelectedIndex(1);
        Catatan.setText("");
        KdPenyakit.setText("");
        NmPenyakit.setText("");
        KdPoli.setText("");
        NmPoli.setText("");
        Kelas.setSelectedIndex(2);
        LakaLantas.setSelectedIndex(0);
        TNoRM.setText("");
        KdDPJP.setText("");
        NmDPJP.setText("");
        Keterangan.setText("");
        NoSEPSuplesi.setText("");
        KdPropinsi.setText("");
        NmPropinsi.setText("");
        KdKabupaten.setText("");
        NmKabupaten.setText("");
        KdKecamatan.setText("");
        NmKecamatan.setText("");
        Katarak.setSelectedIndex(0);
        Suplesi.setSelectedIndex(0);
        TanggalKKL.setDate(new Date());
        TanggalKKL.setEnabled(false);
        Keterangan.setEditable(false);
        TujuanKunjungan.setSelectedIndex(0);
        FlagProsedur.setSelectedIndex(0);
        FlagProsedur.setEnabled(false);
        Penunjang.setSelectedIndex(0);
        Penunjang.setEnabled(false);
        AsesmenPoli.setSelectedIndex(0);
        AsesmenPoli.setEnabled(true);
        KdDPJPLayanan.setText("");
        NmDPJPLayanan.setText("");
        btnCariDokter.setEnabled(true);
        NoRujukan.requestFocus();
        kodepolireg = "";
        kodedokterreg = "";
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
        JumlahBarcode.setText("3");
        resetAksi();
    }

    private void isPoli() {
        try {
            ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ? order by nm_poli");
            try {
                ps.setString(1, kodepolireg);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (statuspasien.equals("Lama")) {
                        TBiaya.setText(rs.getString("registrasilama"));
                    } else {
                        TBiaya.setText(rs.getString("registrasi"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Cari Poli : " + e);
        }
    }

    private void bukaAplikasiValidasi() {
        if (AUTOBUKAAPLIKASI.equals("frista")) {
            bukaAplikasiFrista();
        } else {
            bukaAplikasiFingerprint();
        }
    }

    private void bukaAplikasiFingerprint() {
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. kartu peserta tidak ada..!!");

            return;
        }

        toFront();

        try {
            aplikasiAktif = false;
            User32 u32 = User32.INSTANCE;

            u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                char[] windowText = new char[512];
                u32.GetWindowText(hwnd, windowText, 512);
                String wText = Native.toString(windowText);

                if (wText.isEmpty()) {
                    return true;
                }

                if (wText.contains("Registrasi Sidik Jari")) {
                    DlgRegistrasiSEPPertama.this.aplikasiAktif = true;
                    u32.SetForegroundWindow(hwnd);
                }

                return true;
            }, Pointer.NULL);

            Robot r = new Robot();
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;

            if (aplikasiAktif) {
                Thread.sleep(1000);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(500);

                ss = new StringSelection(NoKartu.getText().trim());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            } else {
                Runtime.getRuntime().exec(koneksiDB.URLAPLIKASIFINGERPRINTBPJS());
                Thread.sleep(2000);
                ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);

                ss = new StringSelection(koneksiDB.PASSFINGERPRINTBPJS());
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(1000);

                ss = new StringSelection(NoKartu.getText().trim());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void bukaAplikasiFrista() {
        if (NIK.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No. kartu peserta tidak ada..!!");
            return;
        }
        toFront();
        try {
            fristaAktif = false;
            User32 u32 = User32.INSTANCE;
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

            u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                char[] windowText = new char[512];
                u32.GetWindowText(hwnd, windowText, 512);
                String wText = Native.toString(windowText);

                if (wText.toLowerCase().contains("face recognition bpjs kesehatan")) {
                    DlgRegistrasiSEPPertama.this.fristaAktif = true;
                    u32.ShowWindow(hwnd, User32.SW_RESTORE);
                    u32.SetForegroundWindow(hwnd);
                    return false;
                }

                return true;
            }, Pointer.NULL);

            Robot r = new Robot();
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;

            if (fristaAktif) {
                Thread.sleep(1000);
                r.mouseMove(d.width / 2, d.height / 2);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(200);

                ss = new StringSelection(NIK.getText());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            } else {
                Runtime.getRuntime().exec(koneksiDB.URLAPLIKASIFRISTABPJS());
                Thread.sleep(5000);

                ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);

                ss = new StringSelection(koneksiDB.PASSFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                r.keyPress(KeyEvent.VK_SPACE);
                r.keyRelease(KeyEvent.VK_SPACE);
                Thread.sleep(3000);

                r.mouseMove(d.width / 2, d.height / 2);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                ss = new StringSelection(NIK.getText());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void updateSuratKontrol(String noSKDP, String noSEP, String tglKontrol, String noKartuPeserta) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");

            return;
        }

        String kodePoliKontrol = Sequel.cariIsiSmc("select kd_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
            namaPoliKontrol = Sequel.cariIsiSmc("select nm_poli_bpjs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoliKontrol),
            kodeDokterKontrol = Sequel.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
            namaDokterKontrol = Sequel.cariIsiSmc("select nm_dokter_bpjs from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokterKontrol);

        try {
            utc = String.valueOf(api.GetUTCdatetimeAsString());

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            URL = URLAPIBPJS + "/RencanaKontrol/Update";

            requestJson = "{"
                + "\"request\": {"
                + "\"noSuratKontrol\":\"" + noSKDP + "\","
                + "\"noSEP\":\"" + noSEP + "\","
                + "\"kodeDokter\":\"" + kodeDokterKontrol + "\","
                + "\"poliKontrol\":\"" + kodePoliKontrol + "\","
                + "\"tglRencanaKontrol\":\"" + tglKontrol + "\","
                + "\"user\":\"" + noKartuPeserta + "\""
                + "}"
                + "}";

            System.out.println("JSON : " + requestJson);

            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());

            if (nameNode.path("code").asText().equals("200")) {
                System.out.println("Respon BPJS : " + nameNode.path("message").asText());

                Sequel.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?",
                    "no_surat = ?",
                    tglKontrol, kodeDokterKontrol, namaDokterKontrol, kodePoliKontrol, namaPoliKontrol,
                    noSKDP
                );
            } else {
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void updateSuratKontrol(String noSKDP, String noSEP, String noKartu, String tanggalPeriksa, String kodeDPJP, String namaDPJP, String kodePoli, String namaPoli) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");
            return;
        }
        try {
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            URL = URLAPIBPJS + "/RencanaKontrol/Update";
            requestJson = "{"
                + "\"request\": {"
                + "\"noSuratKontrol\":\"" + noSKDP + "\","
                + "\"noSEP\":\"" + noSEP + "\","
                + "\"kodeDokter\":\"" + kodeDPJP + "\","
                + "\"poliKontrol\":\"" + kodePoli + "\","
                + "\"tglRencanaKontrol\":\"" + tanggalPeriksa + "\","
                + "\"user\":\"" + noKartu + "\""
                + "}"
                + "}";
            System.out.println("JSON : " + requestJson);
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                Sequel.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?", "no_surat = ?",
                    tanggalPeriksa, kodeDPJP, namaDPJP, kodePoli, namaPoli, noSKDP
                );
            } else {
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
    private boolean registerPasien() {
        int coba = 0, maxCoba = 5;

        System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + TNoRw.getText());

        while (coba < maxCoba && (!Sequel.menyimpantfSmc("reg_periksa", null,
            NoReg.getText(), TNoRw.getText(), Valid.getTglSmc(TanggalSEP),
            Sequel.cariIsi("select current_time()"), kodedokterreg, TNoRM.getText(), kodepolireg,
            TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
            statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status))) {
            isNumber();
            System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + TNoRw.getText());

            coba++;
        }

        String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", Valid.getTglSmc(TanggalSEP), TNoRM.getText(), kodepolireg, kodedokterreg);

        if (coba == maxCoba && (isNoRawat == null || !isNoRawat.equals(TNoRw.getText()))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
            System.out.println("No. Rawat: " + TNoRw.getText());
            System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(TanggalSEP));
            System.out.println("No. Antrian: " + NoReg.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("No. RM: " + TNoRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("Kode Poli: " + kodepolireg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
            System.out.println("======================================================");

            return false;
        }

        updateUmurPasien();

        return true;
    }

    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;

        NoRujukMasuk.setText(
            Sequel.cariIsiSmc(
                "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                Valid.getTglSmc(TanggalSEP), Valid.getTglSmc(TanggalSEP)
            )
        );

        System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat: " + NoRujukMasuk.getText());

        while (coba < maxCoba && (!Sequel.menyimpantfSmc("rujuk_masuk", null,
            TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(),
            "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-", "-", NoRujukMasuk.getText()
        ))) {
            NoRujukMasuk.setText(
                Sequel.cariIsiSmc(
                    "select concat('BR/', date_format(?, '%Y/%m/%d/'), lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                    Valid.getTglSmc(TanggalSEP), Valid.getTglSmc(TanggalSEP)
                )
            );

            System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat balasan: " + NoRujukMasuk.getText());

            coba++;
        }

        String isNoRujukMasuk = Sequel.cariIsiSmc("select rujuk_masuk.no_balasan from rujuk_masuk where rujuk_masuk.no_rawat = ?", TNoRw.getText());

        if (coba == maxCoba && (isNoRujukMasuk == null || (!isNoRujukMasuk.equals(NoRujukMasuk.getText())))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat memproses rujukan masuk dengan detail berikut:");
            System.out.println("No. Surat: " + NoRujukMasuk.getText());
            System.out.println("No. Rawat: " + TNoRw.getText());
            System.out.println("======================================================");

            return false;
        }

        return true;
    }

    private void updateUmurPasien() {
        Sequel.mengupdateSmc("pasien",
            "no_tlp = ?, no_ktp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
            "no_rkm_medis = ?",
            NoTelp.getText(), NIK.getText(), TNoRM.getText()
        );
    }
    
    private void resetAksi() {
        pwUserId.setText("");
        pwPass.setText("");
        aksi = "";
    }
    
    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            panelTambahan.setPreferredSize(new Dimension(WIDTH, 290));
            form.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            panelTambahan.setPreferredSize(new Dimension(WIDTH, 30));
            form.setVisible(false);
            ChkInput.setVisible(true);
        }
    }
}
