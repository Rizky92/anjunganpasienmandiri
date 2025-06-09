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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author Kode
 */
public class DlgRegistrasiSEPMobileJKN extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final ApiBPJS api = new ApiBPJS();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private BPJSCekReferensiDokterDPJP1 dokter = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit penyakit = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS poli = new DlgCariPoliBPJS(null, true);
    private DlgCariPoli polimapping = new DlgCariPoli(null, true);
    private DlgCariDokter2 doktermapping = new DlgCariDokter2(null, true);
    private BPJSCekRiwayatRujukanTerakhir rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekRiwayatPelayanan historiPelayanan = new BPJSCekRiwayatPelayanan(null, true);
    private String hari = "",
        aksi = "",
        tglkkl = "0000-00-00",
        datajam = "",
        jamselesai = "",
        jammulai = "",
        requestJson,
        URL = "",
        prb = "",
        nobooking = "",
        kodedokterreg = "",
        kodepolireg = "",
        utc = "",
        jeniskunjungan = "";

    private final String URLAPIBPJS = koneksiDB.URLAPIBPJS(),
        AUTOBUKAAPLIKASI = koneksiDB.AUTOBUKAAPLIKASI(),
        URLAPLIKASIFINGERPRINTBPJS = koneksiDB.URLAPLIKASIFINGERPRINTBPJS(),
        USERFINGERPRINTBPJS = koneksiDB.USERFINGERPRINTBPJS(),
        PASSFINGERPRINTBPJS = koneksiDB.PASSFINGERPRINTBPJS();
    private int kuota = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
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
    public DlgRegistrasiSEPMobileJKN(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();
        JumlahBarcode.setDocument(new batasInput((byte) 3).getOnlyAngka(JumlahBarcode));
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

        KdPPK.setText(Sequel.cariIsi("select setting.kode_ppk from setting"));
        NmPPK.setText(Sequel.cariIsi("select setting.nama_instansi from setting"));
        JumlahBarcode.setText("3");
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
        jPanel1 = new component.Panel();
        jPanel2 = new component.Panel();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        NoKartu = new widget.TextBox();
        jLabel20 = new widget.Label();
        TanggalSEP = new widget.Tanggal();
        jLabel22 = new widget.Label();
        TanggalRujuk = new widget.Tanggal();
        jLabel23 = new widget.Label();
        NoRujukan = new widget.TextBox();
        jLabel9 = new widget.Label();
        KdPPK = new widget.TextBox();
        NmPPK = new widget.TextBox();
        jLabel10 = new widget.Label();
        KdPpkRujukan = new widget.TextBox();
        NmPpkRujukan = new widget.TextBox();
        jLabel11 = new widget.Label();
        KdPenyakit = new widget.TextBox();
        NmPenyakit = new widget.TextBox();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        LabelPoli = new widget.Label();
        jLabel13 = new widget.Label();
        jLabel14 = new widget.Label();
        Catatan = new widget.TextBox();
        JenisPelayanan = new widget.ComboBox();
        LabelKelas = new widget.Label();
        Kelas = new widget.ComboBox();
        LakaLantas = new widget.ComboBox();
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
        jLabel38 = new widget.Label();
        TanggalKKL = new widget.Tanggal();
        LabelPoli2 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel36 = new widget.Label();
        Keterangan = new widget.TextBox();
        jLabel40 = new widget.Label();
        Suplesi = new widget.ComboBox();
        NoSEPSuplesi = new widget.TextBox();
        jLabel41 = new widget.Label();
        LabelPoli3 = new widget.Label();
        KdPropinsi = new widget.TextBox();
        NmPropinsi = new widget.TextBox();
        LabelPoli4 = new widget.Label();
        KdKabupaten = new widget.TextBox();
        NmKabupaten = new widget.TextBox();
        LabelPoli5 = new widget.Label();
        KdKecamatan = new widget.TextBox();
        NmKecamatan = new widget.TextBox();
        jLabel42 = new widget.Label();
        TujuanKunjungan = new widget.ComboBox();
        FlagProsedur = new widget.ComboBox();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        Penunjang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        AsesmenPoli = new widget.ComboBox();
        lblTerapi = new widget.Label();
        KdDPJPLayanan = new widget.TextBox();
        NmDPJPLayanan = new widget.TextBox();
        btnDPJPLayanan = new widget.Button();
        jLabel55 = new widget.Label();
        lblNoRawat = new widget.Label();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        NoSKDP = new widget.TextBox();
        jLabel26 = new widget.Label();
        NIK = new widget.TextBox();
        jLabel7 = new widget.Label();
        btnDPJPLayanan1 = new widget.Button();
        btnDiagnosaAwal = new widget.Button();
        btnCariNoRujukan = new widget.Button();
        btnRiwayatPelayanan = new widget.Button();
        KodeDokterTerapi = new widget.TextBox();
        KdPoliTerapi = new widget.TextBox();
        NmPoliTerapi = new widget.TextBox();
        NmDokterTerapi = new widget.TextBox();
        btnDokterTerapi = new widget.Button();
        btnPoliTerapi = new widget.Button();
        LabelPoli7 = new widget.Label();
        jLabel57 = new widget.Label();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        jLabel15 = new widget.Label();
        JumlahBarcode = new widget.TextBox();
        jPanel3 = new javax.swing.JPanel();
        btnSimpan = new component.Button();
        btnFingerPrint = new component.Button();
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

        NoRawat.setPreferredSize(new java.awt.Dimension(320, 30));

        Biaya.setPreferredSize(new java.awt.Dimension(320, 30));

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

        TBiaya.setText("0");

        Kdpnj.setHighlighter(null);

        nmpnj.setHighlighter(null);

        TNoRw.setText("0");

        NoRujukMasuk.setText("0");

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-09-2024" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(95, 23));

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "DATA ELIGIBILITAS PESERTA JKN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(543, 106));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        jPanel2.setBackground(new java.awt.Color(238, 238, 255));
        jPanel2.setForeground(new java.awt.Color(0, 131, 62));
        jPanel2.setPreferredSize(new java.awt.Dimension(390, 120));
        jPanel2.setLayout(null);

        TPasien.setEditable(false);
        TPasien.setBackground(new java.awt.Color(245, 250, 240));
        TPasien.setHighlighter(null);
        jPanel2.add(TPasien);
        TPasien.setBounds(340, 10, 230, 30);

        TNoRM.setEditable(false);
        TNoRM.setBackground(new java.awt.Color(245, 250, 240));
        TNoRM.setHighlighter(null);
        jPanel2.add(TNoRM);
        TNoRM.setBounds(230, 10, 110, 30);

        NoKartu.setEditable(false);
        NoKartu.setBackground(new java.awt.Color(255, 255, 153));
        NoKartu.setHighlighter(null);
        jPanel2.add(NoKartu);
        NoKartu.setBounds(730, 70, 300, 30);

        jLabel20.setForeground(new java.awt.Color(0, 131, 62));
        jLabel20.setText("Tgl. SEP :");
        jLabel20.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel20);
        jLabel20.setBounds(625, 130, 100, 30);

        TanggalSEP.setEditable(false);
        TanggalSEP.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-09-2024" }));
        TanggalSEP.setDisplayFormat("dd-MM-yyyy");
        TanggalSEP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalSEP.setOpaque(false);
        TanggalSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        jPanel2.add(TanggalSEP);
        TanggalSEP.setBounds(730, 130, 170, 30);

        jLabel22.setForeground(new java.awt.Color(0, 131, 62));
        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel22);
        jLabel22.setBounds(625, 160, 100, 30);

        TanggalRujuk.setEditable(false);
        TanggalRujuk.setForeground(new java.awt.Color(50, 70, 50));
        TanggalRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-09-2024" }));
        TanggalRujuk.setDisplayFormat("dd-MM-yyyy");
        TanggalRujuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalRujuk.setOpaque(false);
        TanggalRujuk.setPreferredSize(new java.awt.Dimension(95, 23));
        jPanel2.add(TanggalRujuk);
        TanggalRujuk.setBounds(730, 160, 170, 30);

        jLabel23.setForeground(new java.awt.Color(0, 131, 62));
        jLabel23.setText("No. Surat Kontrol :");
        jLabel23.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel23);
        jLabel23.setBounds(95, 70, 130, 30);

        NoRujukan.setEditable(false);
        NoRujukan.setBackground(new java.awt.Color(255, 255, 153));
        NoRujukan.setHighlighter(null);
        jPanel2.add(NoRujukan);
        NoRujukan.setBounds(230, 100, 340, 30);

        jLabel9.setForeground(new java.awt.Color(0, 131, 62));
        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel9);
        jLabel9.setBounds(95, 250, 130, 30);

        KdPPK.setEditable(false);
        KdPPK.setBackground(new java.awt.Color(245, 250, 240));
        KdPPK.setHighlighter(null);
        jPanel2.add(KdPPK);
        KdPPK.setBounds(230, 250, 80, 30);

        NmPPK.setEditable(false);
        NmPPK.setBackground(new java.awt.Color(245, 250, 240));
        NmPPK.setHighlighter(null);
        jPanel2.add(NmPPK);
        NmPPK.setBounds(310, 250, 260, 30);

        jLabel10.setForeground(new java.awt.Color(0, 131, 62));
        jLabel10.setText("PPK Rujukan :");
        jLabel10.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel10);
        jLabel10.setBounds(95, 130, 130, 30);

        KdPpkRujukan.setEditable(false);
        KdPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        KdPpkRujukan.setHighlighter(null);
        jPanel2.add(KdPpkRujukan);
        KdPpkRujukan.setBounds(230, 130, 80, 30);

        NmPpkRujukan.setEditable(false);
        NmPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        NmPpkRujukan.setHighlighter(null);
        jPanel2.add(NmPpkRujukan);
        NmPpkRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setForeground(new java.awt.Color(0, 131, 62));
        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel11);
        jLabel11.setBounds(95, 160, 130, 30);

        KdPenyakit.setEditable(false);
        KdPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        KdPenyakit.setHighlighter(null);
        jPanel2.add(KdPenyakit);
        KdPenyakit.setBounds(230, 160, 80, 30);

        NmPenyakit.setEditable(false);
        NmPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        NmPenyakit.setHighlighter(null);
        jPanel2.add(NmPenyakit);
        NmPenyakit.setBounds(310, 160, 260, 30);

        NmPoli.setEditable(false);
        NmPoli.setBackground(new java.awt.Color(255, 255, 153));
        NmPoli.setHighlighter(null);
        jPanel2.add(NmPoli);
        NmPoli.setBounds(310, 190, 260, 30);

        KdPoli.setEditable(false);
        KdPoli.setBackground(new java.awt.Color(255, 255, 153));
        KdPoli.setHighlighter(null);
        jPanel2.add(KdPoli);
        KdPoli.setBounds(230, 190, 80, 30);

        LabelPoli.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(LabelPoli);
        LabelPoli.setBounds(95, 190, 130, 30);

        jLabel13.setForeground(new java.awt.Color(0, 131, 62));
        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel13);
        jLabel13.setBounds(95, 280, 130, 30);

        jLabel14.setForeground(new java.awt.Color(0, 131, 62));
        jLabel14.setText("Catatan :");
        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel14);
        jLabel14.setBounds(625, 460, 100, 30);

        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        Catatan.setHighlighter(null);
        jPanel2.add(Catatan);
        Catatan.setBounds(730, 460, 300, 30);

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
        jPanel2.add(JenisPelayanan);
        JenisPelayanan.setBounds(230, 280, 110, 30);

        LabelKelas.setForeground(new java.awt.Color(0, 131, 62));
        LabelKelas.setText("Kelas :");
        LabelKelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(LabelKelas);
        LabelKelas.setBounds(350, 280, 50, 30);

        Kelas.setForeground(new java.awt.Color(0, 131, 62));
        Kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        Kelas.setSelectedIndex(2);
        Kelas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(Kelas);
        Kelas.setBounds(400, 280, 100, 30);

        LakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        LakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        LakaLantas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        LakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LakaLantasItemStateChanged(evt);
            }
        });
        jPanel2.add(LakaLantas);
        LakaLantas.setBounds(730, 250, 170, 30);

        jLabel8.setForeground(new java.awt.Color(0, 131, 62));
        jLabel8.setText("Data Pasien :");
        jLabel8.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel8);
        jLabel8.setBounds(95, 10, 130, 30);

        TglLahir.setEditable(false);
        TglLahir.setBackground(new java.awt.Color(245, 250, 240));
        TglLahir.setHighlighter(null);
        jPanel2.add(TglLahir);
        TglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setForeground(new java.awt.Color(0, 131, 62));
        jLabel18.setText("J. K. :");
        jLabel18.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(910, 10, 30, 30);

        JK.setEditable(false);
        JK.setBackground(new java.awt.Color(245, 250, 240));
        JK.setHighlighter(null);
        jPanel2.add(JK);
        JK.setBounds(940, 10, 90, 30);

        jLabel24.setForeground(new java.awt.Color(0, 131, 62));
        jLabel24.setText("Peserta :");
        jLabel24.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel24);
        jLabel24.setBounds(625, 10, 100, 30);

        JenisPeserta.setEditable(false);
        JenisPeserta.setBackground(new java.awt.Color(245, 250, 240));
        JenisPeserta.setHighlighter(null);
        jPanel2.add(JenisPeserta);
        JenisPeserta.setBounds(730, 10, 173, 30);

        jLabel25.setForeground(new java.awt.Color(0, 131, 62));
        jLabel25.setText("Status :");
        jLabel25.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel25);
        jLabel25.setBounds(370, 40, 50, 30);

        Status.setEditable(false);
        Status.setBackground(new java.awt.Color(245, 250, 240));
        Status.setHighlighter(null);
        jPanel2.add(Status);
        Status.setBounds(420, 40, 150, 30);

        jLabel27.setForeground(new java.awt.Color(0, 131, 62));
        jLabel27.setText("Asal Rujukan :");
        jLabel27.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel27);
        jLabel27.setBounds(625, 100, 100, 30);

        AsalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        AsalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        AsalRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(AsalRujukan);
        AsalRujukan.setBounds(730, 100, 170, 30);

        NoTelp.setHighlighter(null);
        jPanel2.add(NoTelp);
        NoTelp.setBounds(730, 190, 170, 30);

        Katarak.setForeground(new java.awt.Color(0, 131, 62));
        Katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Katarak.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        jPanel2.add(Katarak);
        Katarak.setBounds(730, 220, 170, 30);

        jLabel37.setForeground(new java.awt.Color(0, 131, 62));
        jLabel37.setText("Katarak :");
        jLabel37.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel37);
        jLabel37.setBounds(625, 220, 100, 30);

        jLabel38.setForeground(new java.awt.Color(0, 131, 62));
        jLabel38.setText("Tgl. KLL :");
        jLabel38.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel38);
        jLabel38.setBounds(625, 280, 100, 30);

        TanggalKKL.setEditable(false);
        TanggalKKL.setForeground(new java.awt.Color(50, 70, 50));
        TanggalKKL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-09-2024" }));
        TanggalKKL.setDisplayFormat("dd-MM-yyyy");
        TanggalKKL.setEnabled(false);
        TanggalKKL.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TanggalKKL.setOpaque(false);
        TanggalKKL.setPreferredSize(new java.awt.Dimension(64, 25));
        jPanel2.add(TanggalKKL);
        TanggalKKL.setBounds(730, 280, 170, 30);

        LabelPoli2.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli2.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(LabelPoli2);
        LabelPoli2.setBounds(95, 220, 130, 30);

        KdDPJP.setEditable(false);
        KdDPJP.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJP.setHighlighter(null);
        jPanel2.add(KdDPJP);
        KdDPJP.setBounds(230, 220, 80, 30);

        NmDPJP.setEditable(false);
        NmDPJP.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJP.setHighlighter(null);
        jPanel2.add(NmDPJP);
        NmDPJP.setBounds(310, 220, 260, 30);

        jLabel36.setForeground(new java.awt.Color(0, 131, 62));
        jLabel36.setText("Keterangan :");
        jLabel36.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel36);
        jLabel36.setBounds(625, 310, 100, 30);

        Keterangan.setEditable(false);
        Keterangan.setHighlighter(null);
        jPanel2.add(Keterangan);
        Keterangan.setBounds(730, 310, 300, 30);

        jLabel40.setForeground(new java.awt.Color(0, 131, 62));
        jLabel40.setText("Suplesi :");
        jLabel40.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel40);
        jLabel40.setBounds(625, 340, 100, 30);

        Suplesi.setForeground(new java.awt.Color(0, 131, 62));
        Suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Suplesi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        jPanel2.add(Suplesi);
        Suplesi.setBounds(730, 340, 90, 30);

        NoSEPSuplesi.setHighlighter(null);
        jPanel2.add(NoSEPSuplesi);
        NoSEPSuplesi.setBounds(890, 340, 140, 30);

        jLabel41.setForeground(new java.awt.Color(0, 131, 62));
        jLabel41.setText("No. SEP :");
        jLabel41.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel41);
        jLabel41.setBounds(820, 340, 68, 30);

        LabelPoli3.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli3.setText("Propinsi KLL :");
        LabelPoli3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(LabelPoli3);
        LabelPoli3.setBounds(625, 370, 100, 30);

        KdPropinsi.setEditable(false);
        KdPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        KdPropinsi.setHighlighter(null);
        jPanel2.add(KdPropinsi);
        KdPropinsi.setBounds(730, 370, 60, 30);

        NmPropinsi.setEditable(false);
        NmPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        NmPropinsi.setHighlighter(null);
        jPanel2.add(NmPropinsi);
        NmPropinsi.setBounds(790, 370, 240, 30);

        LabelPoli4.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli4.setText("Kabupaten KLL :");
        LabelPoli4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(LabelPoli4);
        LabelPoli4.setBounds(625, 400, 100, 30);

        KdKabupaten.setEditable(false);
        KdKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        KdKabupaten.setHighlighter(null);
        jPanel2.add(KdKabupaten);
        KdKabupaten.setBounds(730, 400, 60, 30);

        NmKabupaten.setEditable(false);
        NmKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        NmKabupaten.setHighlighter(null);
        jPanel2.add(NmKabupaten);
        NmKabupaten.setBounds(790, 400, 240, 30);

        LabelPoli5.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli5.setText("Kecamatan KLL :");
        LabelPoli5.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(LabelPoli5);
        LabelPoli5.setBounds(625, 430, 100, 30);

        KdKecamatan.setEditable(false);
        KdKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        KdKecamatan.setHighlighter(null);
        jPanel2.add(KdKecamatan);
        KdKecamatan.setBounds(730, 430, 60, 30);

        NmKecamatan.setEditable(false);
        NmKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        NmKecamatan.setHighlighter(null);
        jPanel2.add(NmKecamatan);
        NmKecamatan.setBounds(790, 430, 240, 30);

        jLabel42.setForeground(new java.awt.Color(0, 131, 62));
        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel42);
        jLabel42.setBounds(95, 310, 130, 30);

        TujuanKunjungan.setBackground(new java.awt.Color(255, 255, 153));
        TujuanKunjungan.setForeground(new java.awt.Color(0, 131, 62));
        TujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        TujuanKunjungan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TujuanKunjunganItemStateChanged(evt);
            }
        });
        jPanel2.add(TujuanKunjungan);
        TujuanKunjungan.setBounds(230, 310, 340, 30);

        FlagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        FlagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        FlagProsedur.setEnabled(false);
        FlagProsedur.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(FlagProsedur);
        FlagProsedur.setBounds(230, 340, 340, 30);

        jLabel43.setForeground(new java.awt.Color(0, 131, 62));
        jLabel43.setText("Flag Prosedur :");
        jLabel43.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel43);
        jLabel43.setBounds(95, 340, 130, 30);

        jLabel44.setForeground(new java.awt.Color(0, 131, 62));
        jLabel44.setText("Penunjang :");
        jLabel44.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel44);
        jLabel44.setBounds(95, 370, 130, 30);

        Penunjang.setForeground(new java.awt.Color(0, 131, 62));
        Penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        Penunjang.setEnabled(false);
        Penunjang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(Penunjang);
        Penunjang.setBounds(230, 370, 340, 30);

        jLabel45.setForeground(new java.awt.Color(0, 131, 62));
        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel45);
        jLabel45.setBounds(95, 400, 130, 30);

        AsesmenPoli.setBackground(new java.awt.Color(255, 255, 153));
        AsesmenPoli.setForeground(new java.awt.Color(0, 131, 62));
        AsesmenPoli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        AsesmenPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(AsesmenPoli);
        AsesmenPoli.setBounds(230, 400, 340, 30);

        lblTerapi.setForeground(new java.awt.Color(0, 131, 62));
        lblTerapi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTerapi.setText("Terapi / Rehabilitasi Medik");
        lblTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lblTerapi.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(lblTerapi);
        lblTerapi.setBounds(230, 470, 340, 20);

        KdDPJPLayanan.setEditable(false);
        KdDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJPLayanan.setHighlighter(null);
        jPanel2.add(KdDPJPLayanan);
        KdDPJPLayanan.setBounds(230, 430, 80, 30);

        NmDPJPLayanan.setEditable(false);
        NmDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJPLayanan.setHighlighter(null);
        jPanel2.add(NmDPJPLayanan);
        NmDPJPLayanan.setBounds(310, 430, 260, 30);

        btnDPJPLayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDPJPLayanan.setMnemonic('X');
        btnDPJPLayanan.setToolTipText("Alt+X");
        btnDPJPLayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDPJPLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDPJPLayananActionPerformed(evt);
            }
        });
        jPanel2.add(btnDPJPLayanan);
        btnDPJPLayanan.setBounds(570, 220, 40, 30);

        jLabel55.setForeground(new java.awt.Color(0, 131, 62));
        jLabel55.setText("Laka Lantas :");
        jLabel55.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel55);
        jLabel55.setBounds(625, 250, 100, 30);

        lblNoRawat.setForeground(new java.awt.Color(0, 131, 62));
        lblNoRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRawat.setText("No Reg");
        lblNoRawat.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lblNoRawat.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(lblNoRawat);
        lblNoRawat.setBounds(1040, 190, 220, 30);

        jLabel12.setForeground(new java.awt.Color(0, 131, 62));
        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel12);
        jLabel12.setBounds(95, 40, 130, 30);

        jLabel6.setForeground(new java.awt.Color(0, 131, 62));
        jLabel6.setText("NIK :");
        jLabel6.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel6);
        jLabel6.setBounds(625, 40, 100, 30);

        NoSKDP.setEditable(false);
        NoSKDP.setBackground(new java.awt.Color(255, 255, 153));
        NoSKDP.setHighlighter(null);
        jPanel2.add(NoSKDP);
        NoSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setForeground(new java.awt.Color(0, 131, 62));
        jLabel26.setText("No. Rujukan :");
        jLabel26.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel26);
        jLabel26.setBounds(95, 100, 130, 30);

        NIK.setEditable(false);
        NIK.setBackground(new java.awt.Color(255, 255, 153));
        NIK.setHighlighter(null);
        jPanel2.add(NIK);
        NIK.setBounds(730, 40, 300, 30);

        jLabel7.setForeground(new java.awt.Color(0, 131, 62));
        jLabel7.setText("No. Kartu :");
        jLabel7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel7);
        jLabel7.setBounds(625, 70, 100, 30);

        btnDPJPLayanan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDPJPLayanan1.setMnemonic('X');
        btnDPJPLayanan1.setToolTipText("Alt+X");
        btnDPJPLayanan1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDPJPLayanan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDPJPLayanan1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDPJPLayanan1);
        btnDPJPLayanan1.setBounds(570, 190, 40, 30);

        btnDiagnosaAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal.setMnemonic('X');
        btnDiagnosaAwal.setToolTipText("Alt+X");
        btnDiagnosaAwal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwalActionPerformed(evt);
            }
        });
        jPanel2.add(btnDiagnosaAwal);
        btnDiagnosaAwal.setBounds(570, 160, 40, 30);

        btnCariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariNoRujukan.setMnemonic('X');
        btnCariNoRujukan.setToolTipText("Alt+X");
        btnCariNoRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariNoRujukanActionPerformed(evt);
            }
        });
        jPanel2.add(btnCariNoRujukan);
        btnCariNoRujukan.setBounds(570, 100, 40, 30);

        btnRiwayatPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayanan.setMnemonic('X');
        btnRiwayatPelayanan.setText("Riwayat Layanan BPJS");
        btnRiwayatPelayanan.setToolTipText("Alt+X");
        btnRiwayatPelayanan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnRiwayatPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananActionPerformed(evt);
            }
        });
        jPanel2.add(btnRiwayatPelayanan);
        btnRiwayatPelayanan.setBounds(1040, 160, 220, 30);

        KodeDokterTerapi.setEditable(false);
        KodeDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KodeDokterTerapi.setHighlighter(null);
        jPanel2.add(KodeDokterTerapi);
        KodeDokterTerapi.setBounds(230, 520, 80, 30);

        KdPoliTerapi.setEditable(false);
        KdPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KdPoliTerapi.setHighlighter(null);
        jPanel2.add(KdPoliTerapi);
        KdPoliTerapi.setBounds(230, 490, 80, 30);

        NmPoliTerapi.setEditable(false);
        NmPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        NmPoliTerapi.setHighlighter(null);
        jPanel2.add(NmPoliTerapi);
        NmPoliTerapi.setBounds(310, 490, 260, 30);

        NmDokterTerapi.setEditable(false);
        NmDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        NmDokterTerapi.setHighlighter(null);
        jPanel2.add(NmDokterTerapi);
        NmDokterTerapi.setBounds(310, 520, 260, 30);

        btnDokterTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDokterTerapi.setMnemonic('X');
        btnDokterTerapi.setToolTipText("Alt+X");
        btnDokterTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDokterTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterTerapiActionPerformed(evt);
            }
        });
        jPanel2.add(btnDokterTerapi);
        btnDokterTerapi.setBounds(570, 520, 40, 30);

        btnPoliTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnPoliTerapi.setMnemonic('X');
        btnPoliTerapi.setToolTipText("Alt+X");
        btnPoliTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnPoliTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPoliTerapiActionPerformed(evt);
            }
        });
        jPanel2.add(btnPoliTerapi);
        btnPoliTerapi.setBounds(570, 490, 40, 30);

        LabelPoli7.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(LabelPoli7);
        LabelPoli7.setBounds(95, 430, 130, 30);

        jLabel57.setForeground(new java.awt.Color(0, 131, 62));
        jLabel57.setText("No. Telp :");
        jLabel57.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel57.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel57);
        jLabel57.setBounds(625, 190, 100, 30);

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
        jPanel2.add(btnApprovalFP);
        btnApprovalFP.setBounds(1040, 300, 190, 50);

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
        jPanel2.add(btnPengajuanFP);
        btnPengajuanFP.setBounds(1040, 240, 190, 50);

        jLabel15.setForeground(new java.awt.Color(0, 131, 62));
        jLabel15.setText("Jumlah Barcode :");
        jLabel15.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jPanel2.add(jLabel15);
        jLabel15.setBounds(1040, 70, 110, 30);

        JumlahBarcode.setText("3");
        JumlahBarcode.setHighlighter(null);
        jPanel2.add(JumlahBarcode);
        JumlahBarcode.setBounds(1150, 70, 50, 30);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(238, 238, 255));
        jPanel3.setMinimumSize(new java.awt.Dimension(533, 120));
        jPanel3.setPreferredSize(new java.awt.Dimension(533, 120));

        btnSimpan.setForeground(new java.awt.Color(0, 131, 62));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("Konfirmasi");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
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
        jPanel3.add(btnSimpan);

        btnFingerPrint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerPrint.setMnemonic('K');
        btnFingerPrint.setText("FINGERPRINT BPJS");
        btnFingerPrint.setToolTipText("Alt+K");
        btnFingerPrint.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
        btnFingerPrint.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFingerPrint.setPreferredSize(new java.awt.Dimension(300, 45));
        btnFingerPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerPrintActionPerformed(evt);
            }
        });
        jPanel3.add(btnFingerPrint);

        btnKeluar.setForeground(new java.awt.Color(0, 131, 62));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText("Batal");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
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
        jPanel3.add(btnKeluar);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDPJPLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDPJPLayananActionPerformed
        dokter.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        dokter.setLocationRelativeTo(jPanel1);
        dokter.carinamadokter(KdPoli.getText(), NmPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDPJPLayananActionPerformed

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

    private void JenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JenisPelayananItemStateChanged
        if (JenisPelayanan.getSelectedIndex() == 0) {
            KdPoli.setText("");
            NmPoli.setText("");
            LabelPoli.setVisible(false);
            KdPoli.setVisible(false);
            NmPoli.setVisible(false);

            KdDPJPLayanan.setText("");
            NmDPJPLayanan.setText("");
            btnDPJPLayanan.setEnabled(false);
        } else if (JenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            KdPoli.setVisible(true);
            NmPoli.setVisible(true);

            btnDPJPLayanan.setEnabled(true);
        }
    }//GEN-LAST:event_JenisPelayananItemStateChanged

    private void btnDPJPLayanan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDPJPLayanan1ActionPerformed
        poli.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        poli.tampil();
        poli.setLocationRelativeTo(jPanel1);
        poli.setVisible(true);
    }//GEN-LAST:event_btnDPJPLayanan1ActionPerformed

    private void btnDiagnosaAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwalActionPerformed
        penyakit.setSize(jPanel1.getWidth() - 100, jPanel1.getHeight() - 100);
        penyakit.setLocationRelativeTo(jPanel1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaAwalActionPerformed

    private void btnCariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariNoRujukanActionPerformed
        if (NoKartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
            rujukanterakhir.setLocationRelativeTo(jPanel1);
            rujukanterakhir.tampil(NoKartu.getText(), TPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnCariNoRujukanActionPerformed

    private void btnRiwayatPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        historiPelayanan.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        historiPelayanan.setLocationRelativeTo(jPanel1);
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPelayananActionPerformed

    private void btnDokterTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterTerapiActionPerformed
        doktermapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        doktermapping.tampilDokterTerapi(KdDPJPLayanan.getText());
        doktermapping.setLocationRelativeTo(jPanel1);
        doktermapping.setVisible(true);
    }//GEN-LAST:event_btnDokterTerapiActionPerformed

    private void btnPoliTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoliTerapiActionPerformed
        polimapping.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        polimapping.tampilPoliMapping(KdPoli.getText());
        polimapping.setLocationRelativeTo(jPanel1);
        polimapping.setVisible(true);
    }//GEN-LAST:event_btnPoliTerapiActionPerformed

    private void btnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnKeluarActionPerformed(null);
        }
    }//GEN-LAST:event_btnKeluarKeyPressed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnFingerPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerPrintActionPerformed
        bukaAplikasiValidasi();
    }//GEN-LAST:event_btnFingerPrintActionPerformed

    private void btnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_btnSimpanKeyPressed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger();
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (NoKartu.getText().trim().equals("")) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
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
        } else if (!statusfinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, current_date())", TglLahir.getText()) >= 17 && JenisPelayanan.getSelectedIndex() != 0 && !KdPoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(null, "Maaf, Pasien belum melakukan Fingerprint");
            bukaAplikasiValidasi();
        } else {
            if (!KdPoliTerapi.getText().equals("")) {
                kodepolireg = KdPoliTerapi.getText();
            } else {
                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
            }

            if (!KodeDokterTerapi.getText().equals("")) {
                kodedokterreg = KodeDokterTerapi.getText();
            } else {
                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
            }

            if (JenisPelayanan.getSelectedIndex() == 0) {
                insertSEP();
            } else if (JenisPelayanan.getSelectedIndex() == 1) {
                if (NmPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", NoKartu.getText().trim(), JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.SetTgl(TanggalSEP.getSelectedItem().toString())) >= 3) {
                        JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            SimpanAntrianOnSite();
                        }
                        insertSEP();
                    }
                } else if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", NoKartu.getText().trim(), JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.SetTgl(TanggalSEP.getSelectedItem().toString())) >= 1) {
                        JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            SimpanAntrianOnSite();
                        }
                        insertSEP();
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnApprovalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApprovalFPActionPerformed
        resetAksi();
        if (! NoKartu.getText().isBlank()) {
            aksi = "Approval";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (! NoKartu.getText().isBlank()) {
            aksi = "Pengajuan";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnPengajuanFPActionPerformed

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

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try {
                ps = koneksi.prepareStatement("select aes_decrypt(id_user, 'nur') from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1");
                try {
                    ps.setString(1, new String(pwUserId.getPassword()));
                    ps.setString(2, new String(pwPass.getPassword()));
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (aksi.equals("Pengajuan")) {
                            System.out.println("Aksi " + aksi);
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
                                    + "\"user\": \"" + rs.getString(1) + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else if (aksi.equals("Approval")) {
                            System.out.println("Aksi " + aksi);
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
                                    + "\"user\": \"" + rs.getString(1) + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metaData");
                                System.out.println("code : " + nameNode.path("code").asText());
                                System.out.println("message : " + nameNode.path("message").asText());
                                if (nameNode.path("code").asText().equals("200")) {
                                    JOptionPane.showMessageDialog(null, "Approval Berhasil");
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi Bridging : " + ex);
                                if (ex.toString().contains("UnknownHostException")) {
                                    JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                }
                            }
                        } else {
                            resetAksi();
                            JOptionPane.showMessageDialog(null, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                        }
                    } else {
                        resetAksi();
                        JOptionPane.showMessageDialog(null, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
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
                resetAksi();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        pwUserId.setText("");
        pwPass.setText("");
        aksi = "";
        WindowAksi.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRegistrasiSEPMobileJKN dialog = new DlgRegistrasiSEPMobileJKN(new javax.swing.JFrame(), true);
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
    private widget.Button btnCariNoRujukan;
    private widget.Button btnDPJPLayanan;
    private widget.Button btnDPJPLayanan1;
    private widget.Button btnDiagnosaAwal;
    private widget.Button btnDokterTerapi;
    private component.Button btnFingerPrint;
    private component.Button btnKeluar;
    private widget.Button btnPengajuanFP;
    private widget.Button btnPoliTerapi;
    private widget.Button btnRiwayatPelayanan;
    private component.Button btnSimpan;
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
    private widget.Label jLabel57;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private component.Panel jPanel1;
    private component.Panel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kdpoli;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label lblNoRawat;
    private widget.Label lblTerapi;
    private widget.TextBox nmpnj;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    // End of variables declaration//GEN-END:variables

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
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            tglkkl = "0000-00-00";
            if (LakaLantas.getSelectedIndex() > 0) {
                tglkkl = Valid.SetTgl(TanggalKKL.getSelectedItem() + "");
            }

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
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
            JOptionPane.showMessageDialog(null, "Respon BPJS : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                Sequel.mengupdateSmc("pasien",
                    "no_tlp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
                    "no_rkm_medis = ?",
                    NoTelp.getText(), TNoRM.getText()
                );
                Sequel.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    TNoRw.getText(),
                    Valid.SetTgl(TanggalSEP.getSelectedItem().toString()),
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
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), response.asText());
                }

                if (!prb.equals("")) {
                    Sequel.menyimpanSmc("bpjs_prb", null, response.asText(), prb);
                    prb = "";
                }

                if (Sequel.cariIntegerSmc(
                    "select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                ) == 1) {
                    Sequel.mengupdateSmc("booking_registrasi",
                        "status = 'Terdaftar', waktu_kunjungan = now()",
                        "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?",
                        TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                    );
                }
                cetakRegistrasi(response.asText());
                emptTeks();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void cekFinger() {
        statusfinger = false;

        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No. Kartu BPJS tidak ada..!!");

            return;
        }

        try {
            URL = URLAPIBPJS + "/SEP/FingerPrint/Peserta/" + NoKartu.getText() + "/TglPelayanan/" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString());
            utc = String.valueOf(api.GetUTCdatetimeAsString());

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");

            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());

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
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    public void tampil(String noKartu) {
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
        try (PreparedStatement psjkn = koneksi.prepareStatement(
            "select referensi_mobilejkn_bpjs.*, maping_poli_bpjs.nm_poli_bpjs, maping_poli_bpjs.kd_poli_rs, maping_dokter_dpjpvclaim.nm_dokter_bpjs, maping_dokter_dpjpvclaim.kd_dokter from referensi_mobilejkn_bpjs " +
            "join maping_poli_bpjs on referensi_mobilejkn_bpjs.kodepoli = maping_poli_bpjs.kd_poli_bpjs join maping_dokter_dpjpvclaim on referensi_mobilejkn_bpjs.kodedokter = maping_dokter_dpjpvclaim.kd_dokter_bpjs " +
            "where referensi_mobilejkn_bpjs.nomorkartu = ? and referensi_mobilejkn_bpjs.tanggalperiksa = current_date() and referensi_mobilejkn_bpjs.status in ('Belum', 'Checkin') and tanggalperiksa = current_date() " +
            "and not exists(select * from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat = referensi_mobilejkn_bpjs.no_rawat)"
        )) {
            psjkn.setString(1, noKartu);
            try (ResultSet rsjkn = psjkn.executeQuery()) {
                if (rsjkn.next()) {
                    nobooking = rsjkn.getString("nobooking");
                    jeniskunjungan = rsjkn.getString("jeniskunjungan").substring(0, 1);
                    TNoRw.setText(rsjkn.getString("no_rawat"));
                    lblNoRawat.setText(TNoRw.getText());
                    KdPoli.setText(rsjkn.getString("kodepoli"));
                    NmPoli.setText(rsjkn.getString("nm_poli_bpjs"));
                    kdpoli.setText(rsjkn.getString("kd_poli_rs"));
                    kodepolireg = rsjkn.getString("kd_poli_rs");
                    KdDPJP.setText(rsjkn.getString("kodedokter"));
                    NmDPJP.setText(rsjkn.getString("nm_dokter_bpjs"));
                    kodedokterreg = rsjkn.getString("kd_dokter");
                    KdDPJPLayanan.setText(KdDPJP.getText());
                    NmDPJPLayanan.setText(NmDPJP.getText());
                    NoKartu.setText(rsjkn.getString("nomorkartu"));
                    TNoRM.setText(rsjkn.getString("norm"));
                    NIK.setText(rsjkn.getString("nik"));
                    NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                    if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                        NoTelp.setText(rsjkn.getString("nohp"));
                    }
                    // CEK STATUS PASIEN
                    try {
                        URL = URLAPIBPJS + "/Peserta/nokartu/" + rsjkn.getString("nomorkartu") + "/tglSEP/" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString());
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
                        if (nameNode.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                            switch (response.path("hakKelas").path("kode").asText()) {
                                case "1": Kelas.setSelectedIndex(0); break;
                                case "2": Kelas.setSelectedIndex(1); break;
                                case "3": Kelas.setSelectedIndex(2); break;
                                default: break;
                            }
                            prb = response.path("informasi").path("prolanisPRB").asText();
                            if (prb.contains("null")) {
                                prb = "";
                            }
                            TPasien.setText(response.path("nama").asText());
                            NoKartu.setText(response.path("noKartu").asText());
                            JK.setText(response.path("sex").asText());
                            Status.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                            TglLahir.setText(response.path("tglLahir").asText());
                            JenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                            if (jeniskunjungan.equals("1")) {
                                // RUJUKAN FKTP
                                AsalRujukan.setSelectedIndex(0);
                                try {
                                    URL = URLAPIBPJS + "/Rujukan/" + rsjkn.getString("nomorreferensi");
                                    System.out.println("URL : " + URL);
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
                                    if (nameNode.path("code").asText().equals("200")) {
                                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                        KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                                        NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                                        NoRujukan.setText(response.path("noKunjungan").asText());
                                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                                    } else {
                                        System.out.println("Notif : " + nameNode.asText());
                                        JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                        emptTeks();
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notif : " + e);
                                    if (e.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke Server BPJS terputus...!!!");
                                    }
                                    emptTeks();
                                }
                            } else if (jeniskunjungan.equals("4")) {
                                // RUJUKAN FKTL
                                AsalRujukan.setSelectedIndex(1);
                                try {
                                    URL = URLAPIBPJS + "/Rujukan/RS/" + rsjkn.getString("nomorreferensi");
                                    System.out.println("URL : " + URL);
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
                                    if (nameNode.path("code").asText().equals("200")) {
                                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                        KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                                        NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                                        NoRujukan.setText(response.path("noKunjungan").asText());
                                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                                    } else {
                                        System.out.println("Notif : " + nameNode.asText());
                                        JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                        emptTeks();
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notif : " + e);
                                    if (e.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke Server BPJS terputus...!!!");
                                    }
                                    emptTeks();
                                }
                            } else if (jeniskunjungan.equals("3")) {
                                // CEK JENIS KONTROL DULU
                                try (PreparedStatement pskontrol = koneksi.prepareStatement(
                                    "select bridging_surat_kontrol_bpjs.*, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat " +
                                    "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
                                )) {
                                    pskontrol.setString(1, rsjkn.getString("nomorreferensi"));
                                    try (ResultSet rskontrol = pskontrol.executeQuery()) {
                                        if (rskontrol.next()) {
                                            if (! rskontrol.getString("tgl_rencana").equals(Valid.SetTgl(TanggalSEP.getSelectedItem().toString()))) {
                                                updateSuratKontrol(
                                                    rskontrol.getString("no_surat"), rskontrol.getString("no_sep"), rsjkn.getString("nomorkartu"), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()),
                                                    rsjkn.getString("kodedokter"), rsjkn.getString("nm_dokter_bpjs"), rsjkn.getString("kodepoli"), rsjkn.getString("nm_poli_bpjs")
                                                );
                                            }
                                            if (rskontrol.getString("jnspelayanan").equals("1")) {
                                                // KONTROL POST RANAP
                                                KdPenyakit.setText("Z09.8");
                                                NmPenyakit.setText("Z09.8 - Follow-up examination after other treatment for other conditions");
                                                NoRujukan.setText(rskontrol.getString("no_sep"));
                                                TujuanKunjungan.setSelectedIndex(0);
                                                FlagProsedur.setSelectedIndex(0);
                                                Penunjang.setSelectedIndex(0);
                                                AsesmenPoli.setSelectedIndex(0);
                                                AsalRujukan.setSelectedIndex(1);
                                                NoSKDP.setText(rskontrol.getString("no_surat"));
                                                KdPpkRujukan.setText(Sequel.cariIsiSmc("select kode_ppk from setting"));
                                                NmPpkRujukan.setText(Sequel.cariIsiSmc("select nama_instansi from setting"));
                                            } else {
                                                // KONTROL POLI
                                                try {
                                                    if (rskontrol.getString("asal_rujukan").equals("1")) {
                                                        URL = URLAPIBPJS + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                                        AsalRujukan.setSelectedIndex(0);
                                                    } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                                        URL = URLAPIBPJS + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                                                        AsalRujukan.setSelectedIndex(1);
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
                                                    if (nameNode.path("code").asText().equals("200")) {
                                                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                                        KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                                                        NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                                                        NoRujukan.setText(response.path("noKunjungan").asText());
                                                        NoSKDP.setText(rskontrol.getString("no_surat"));
                                                        TujuanKunjungan.setSelectedIndex(2);
                                                        FlagProsedur.setSelectedIndex(0);
                                                        Penunjang.setSelectedIndex(0);
                                                        AsesmenPoli.setSelectedIndex(5);
                                                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                                                    } else {
                                                        System.out.println("Notif : " + nameNode.asText());
                                                        JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                                        emptTeks();
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Notifikasi Peserta : " + e);
                                                    if (e.toString().contains("UnknownHostException")) {
                                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                                    }
                                                    emptTeks();
                                                }
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                            emptTeks();
                                        }
                                    }
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    emptTeks();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Maaf, antrian JKN tidak ditemukan!\nSilahkan hubungi administrasi.");
                                emptTeks();
                            }
                        } else {
                            System.out.println("Notif : " + nameNode.asText());
                            JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                            emptTeks();
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                        if (e.toString().contains("UnknownHostException")) {
                            JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                        }
                        emptTeks();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Maaf, pasien membatalkan antrian MobileJKN, atau telah menerima pelayanan!\nSilahkan hubungi administrasi.");
                    emptTeks();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Maaf, terjadi kesalahan pada saat mencari rujukan di MobileJKN!\nSilahkan hubungi administrasi.");
            emptTeks();
        }
    }

    public void SimpanAntrianOnSite() {
        if (Sequel.cariExistsSmc("select * from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", nobooking)) {
            Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "referensi_mobilejkn_bpjs.validasi = now(), referensi_mobilejkn_bpjs.status = 'Checkin'", "referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", nobooking);
            Sequel.mengupdateSmc("reg_periksa", "reg_periksa.jam_reg = current_time()", "reg_periksa.no_rawat = ? and stts != 'Batal'", TNoRw.getText());
        }
        try {
            ps = koneksi.prepareStatement(
                "select referensi_mobilejkn_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, poliklinik.nm_poli, dokter.nm_dokter from referensi_mobilejkn_bpjs " +
                "join reg_periksa on referensi_mobilejkn_bpjs.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli join dokter on reg_periksa.kd_dokter = dokter.kd_dokter " +
                "where referensi_mobilejkn_bpjs.statuskirim = 'Belum' and referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn.status = 'Checkin'"
            );
            try {
                ps.setString(1, nobooking);
                rs = ps.executeQuery();
                while (rs.next()) {
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestJson = "{"
                            + "\"kodebooking\": \"" + rs.getString("nobooking") + "\","
                            + "\"jenispasien\": \"JKN\","
                            + "\"nomorkartu\": \"" + rs.getString("nomorkartu") + "\","
                            + "\"nik\": \"" + rs.getString("nik") + "\","
                            + "\"nohp\": \"" + NoTelp.getText().trim() + "\","
                            + "\"kodepoli\": \"" + rs.getString("kodepoli") + "\","
                            + "\"namapoli\": \"" + rs.getString("nm_poli") + "\","
                            + "\"pasienbaru\": " + rs.getString("pasienbaru") + ","
                            + "\"norm\": \"" + rs.getString("no_rkm_medis") + "\","
                            + "\"tanggalperiksa\": \"" + rs.getString("tanggalperiksa") + "\","
                            + "\"kodedokter\": " + rs.getString("kodedokter") + ","
                            + "\"namadokter\": \"" + rs.getString("nm_dokter") + "\","
                            + "\"jampraktek\": \"" + rs.getString("jampraktek") + "\","
                            + "\"jeniskunjungan\": " + rs.getString("jeniskunjungan").substring(0, 1) + ","
                            + "\"nomorreferensi\": \"" + rs.getString("nomorreferensi") + "\","
                            + "\"nomorantrean\": \"" + rs.getString("nomorantrean") + "\","
                            + "\"angkaantrean\": " + rs.getInt("angkaantrean") + ","
                            + "\"estimasidilayani\": " + rs.getString("estimasidilayani") + ","
                            + "\"sisakuotajkn\": " + rs.getInt("sisakuotajkn") + ","
                            + "\"kuotajkn\": " + rs.getInt("kuotajkn") + ","
                            + "\"sisakuotanonjkn\": " + rs.getInt("sisakuotanonjkn") + ","
                            + "\"kuotanonjkn\": " + rs.getInt("kuotanonjkn") + ","
                            + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                            + "}";
                        System.out.println("JSON : " + requestJson);
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL : " + URL);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        Sequel.logTaskid(TNoRw.getText(), rs.getString("nobooking"), "MobileJKN", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                        if (nameNode.path("code").asText().equals("200") || nameNode.path("code").asText().equals("208") || nameNode.path("message").asText().equals("Ok")) {
                            Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "statuskirim = 'Sudah'", "nobooking = ?", rs.getString("nobooking"));
                        }
                        System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                    } catch (Exception ex) {
                        System.out.println("Notifikasi Bridging : " + ex);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Notif Ketersediaan : " + ex);
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
        btnDPJPLayanan.setEnabled(true);
        NoRujukan.requestFocus();
        kodepolireg = "";
        kodedokterreg = "";
        KdPoliTerapi.setText("");
        NmPoliTerapi.setText("");
        KodeDokterTerapi.setText("");
        NmDokterTerapi.setText("");
        JumlahBarcode.setText("3");
        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        Kdpnj.setText("BPJ");
        nmpnj.setText("BPJS");
        jeniskunjungan = "";
        nobooking = "";
        resetAksi();
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
            JOptionPane.showMessageDialog(null, "No. kartu peserta tidak ada..!!");

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
                    DlgRegistrasiSEPMobileJKN.this.aplikasiAktif = true;
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
                    DlgRegistrasiSEPMobileJKN.this.fristaAktif = true;
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
    
    private void updateSuratKontrol(String noSKDP, String noSEP, String noKartu, String tanggalPeriksa, String kodeDPJP, String namaDPJP, String kodePoli, String namaPoli) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");
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
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;
        NoRujukMasuk.setText(
            Sequel.cariIsiSmc(
                "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), Valid.SetTgl(TanggalSEP.getSelectedItem().toString())
            )
        );
        boolean sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
            TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(),
            "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-", "-", NoRujukMasuk.getText()
        );
        while (coba < maxCoba && ! sukses) {
            NoRujukMasuk.setText(
                Sequel.cariIsiSmc(
                    "select concat('BR/', date_format(?, '%Y/%m/%d/'), lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                    Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), Valid.SetTgl(TanggalSEP.getSelectedItem().toString())
                )
            );
            sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
                TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(),
                "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-", "-", NoRujukMasuk.getText()
            );
            coba++;
        }
        return sukses;
    }
    
    private void resetAksi() {
        pwUserId.setText("");
        pwPass.setText("");
        aksi = "";
    }
}
