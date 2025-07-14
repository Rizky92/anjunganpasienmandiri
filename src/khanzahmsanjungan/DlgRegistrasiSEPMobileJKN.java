package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSCekReferensiDokterDPJP;
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
public class DlgRegistrasiSEPMobileJKN extends widget.Dialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final ApiBPJS api = new ApiBPJS();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private final BPJSCekReferensiDokterDPJP dokter;
    private final BPJSCekReferensiPenyakit penyakit;
    private final DlgCariPoliBPJS poli;
    private final DlgCariPoli polimapping;
    private final DlgCariDokter doktermapping;
    private final BPJSCekRiwayatRujukanTerakhir rujukanterakhir;
    private final BPJSCekRiwayatPelayanan historiPelayanan;
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
     * @param modal
     */
    public DlgRegistrasiSEPMobileJKN(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JumlahBarcode.setDocument(new batasInput((byte) 3).getOnlyAngka(JumlahBarcode));

        dokter = new BPJSCekReferensiDokterDPJP(parent, modal);
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

        poli = new DlgCariPoliBPJS(parent, modal);
        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.hasSelectedRow()) {
                    KdPoli.setText(poli.getSelectedRow(0).toString());
                    NmPoli.setText(poli.getSelectedRow(1).toString());
                    KdDPJP.requestFocus();
                }
            }
        });

        polimapping = new DlgCariPoli(parent, modal);
        polimapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (polimapping.hasSelectedRow()) {
                    KdPoliTerapi.setText(polimapping.getSelectedRow(0).toString());
                    NmPoliTerapi.setText(polimapping.getSelectedRow(1).toString());
                    KodeDokterTerapi.requestFocus();
                }
            }
        });

        doktermapping = new DlgCariDokter(parent, modal);
        doktermapping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (doktermapping.hasSelectedRow()) {
                    KodeDokterTerapi.setText(doktermapping.getSelectedRow(0).toString());
                    NmDokterTerapi.setText(doktermapping.getSelectedRow(1).toString());
                    KodeDokterTerapi.requestFocus();
                }
            }
        });

        penyakit = new BPJSCekReferensiPenyakit(parent, modal);
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

        rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(parent, modal);
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

        historiPelayanan = new BPJSCekRiwayatPelayanan(parent, modal);
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
        isForm();
        panelNumpad1.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LblKdPoli = new widget.Label();
        LblKdDokter = new widget.Label();
        NoReg = new widget.TextField();
        NoRawat = new widget.TextField();
        Biaya = new widget.TextField();
        TAlmt = new widget.Label();
        TPngJwb = new widget.Label();
        THbngn = new widget.Label();
        NoTelpPasien = new widget.Label();
        kdpoli = new widget.TextField();
        TBiaya = new widget.TextField();
        Kdpnj = new widget.TextField();
        nmpnj = new widget.TextField();
        TNoRw = new widget.TextField();
        NoRujukMasuk = new widget.TextField();
        Tanggal = new widget.Tanggal();
        WindowAksi = new widget.Dialog();
        judulAksi = new widget.Label();
        panelTengahAksi = new widget.Panel();
        userAksi = new widget.PasswordField();
        passAksi = new widget.PasswordField();
        label1 = new widget.Label();
        label2 = new widget.Label();
        panelBawahAksi = new widget.Panel();
        btnAksiKonfirmasi = new widget.Button();
        btnAksiBatal = new widget.Button();
        lblTerapi = new widget.Label();
        KdPoliTerapi = new widget.TextField();
        NmPoliTerapi = new widget.TextField();
        btnPoliTerapi = new widget.Button();
        btnDokterTerapi = new widget.Button();
        NmDokterTerapi = new widget.TextField();
        KodeDokterTerapi = new widget.TextField();
        panelAtas = new widget.Panel();
        label4 = new widget.Label();
        panelTengah = new widget.Panel();
        panel1 = new widget.Panel();
        TPasien = new widget.TextField();
        TNoRM = new widget.TextField();
        NoKartu = new widget.TextField();
        jLabel20 = new widget.Label();
        TanggalSEP = new widget.Tanggal();
        jLabel22 = new widget.Label();
        TanggalRujuk = new widget.Tanggal();
        jLabel23 = new widget.Label();
        NoRujukan = new widget.TextField();
        jLabel10 = new widget.Label();
        KdPpkRujukan = new widget.TextField();
        NmPpkRujukan = new widget.TextField();
        jLabel11 = new widget.Label();
        KdPenyakit = new widget.TextField();
        NmPenyakit = new widget.TextField();
        NmPoli = new widget.TextField();
        KdPoli = new widget.TextField();
        LabelPoli = new widget.Label();
        LabelKelas = new widget.Label();
        Kelas = new widget.ComboBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextField();
        jLabel18 = new widget.Label();
        JK = new widget.TextField();
        jLabel24 = new widget.Label();
        JenisPeserta = new widget.TextField();
        jLabel25 = new widget.Label();
        Status = new widget.TextField();
        jLabel27 = new widget.Label();
        AsalRujukan = new widget.ComboBox();
        NoTelp = new widget.TextField();
        Katarak = new widget.ComboBox();
        jLabel37 = new widget.Label();
        LabelPoli2 = new widget.Label();
        KdDPJP = new widget.TextField();
        NmDPJP = new widget.TextField();
        btnCariDokter = new widget.Button();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        NoSKDP = new widget.TextField();
        jLabel26 = new widget.Label();
        NIK = new widget.TextField();
        jLabel7 = new widget.Label();
        btnCariPoli = new widget.Button();
        btnDiagnosaAwal = new widget.Button();
        btnCariNoRujukan = new widget.Button();
        btnRiwayatPelayanan = new widget.Button();
        jLabel57 = new widget.Label();
        btnFingerprint = new widget.Button();
        btnFrista = new widget.Button();
        panelNumpad1 = new widget.Numpad();
        panel2 = new widget.Panel();
        form = new widget.Panel();
        jLabel13 = new widget.Label();
        JenisPelayanan = new widget.ComboBox();
        jLabel55 = new widget.Label();
        LakaLantas = new widget.ComboBox();
        TanggalKKL = new widget.Tanggal();
        jLabel38 = new widget.Label();
        TujuanKunjungan = new widget.ComboBox();
        jLabel42 = new widget.Label();
        jLabel43 = new widget.Label();
        FlagProsedur = new widget.ComboBox();
        jLabel36 = new widget.Label();
        Keterangan = new widget.TextField();
        NoSEPSuplesi = new widget.TextField();
        jLabel41 = new widget.Label();
        Suplesi = new widget.ComboBox();
        jLabel40 = new widget.Label();
        Penunjang = new widget.ComboBox();
        jLabel44 = new widget.Label();
        jLabel45 = new widget.Label();
        AsesmenPoli = new widget.ComboBox();
        LabelPoli3 = new widget.Label();
        KdPropinsi = new widget.TextField();
        NmPropinsi = new widget.TextField();
        NmKabupaten = new widget.TextField();
        KdKabupaten = new widget.TextField();
        LabelPoli4 = new widget.Label();
        NmDPJPLayanan = new widget.TextField();
        LabelPoli7 = new widget.Label();
        jLabel9 = new widget.Label();
        KdPPK = new widget.TextField();
        NmPPK = new widget.TextField();
        LabelPoli5 = new widget.Label();
        KdKecamatan = new widget.TextField();
        NmKecamatan = new widget.TextField();
        Catatan = new widget.TextField();
        jLabel14 = new widget.Label();
        JumlahBarcode = new widget.TextField();
        jLabel15 = new widget.Label();
        KdDPJPLayanan = new widget.TextField();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        lblNoRawat = new widget.Label();
        ChkInput = new widget.PaneToggle();
        panelBawah = new widget.Panel();
        btnSimpan = new widget.Button();
        btnKeluar = new widget.Button();

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
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        Tanggal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(95, 23));

        WindowAksi.setUndecorated(false);

        judulAksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        judulAksi.setText("KONFIRMASI AKSI");
        judulAksi.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        judulAksi.setPreferredSize(new java.awt.Dimension(400, 30));
        WindowAksi.getContentPane().add(judulAksi, java.awt.BorderLayout.PAGE_START);

        panelTengahAksi.setOpaque(false);
        panelTengahAksi.setLayout(null);

        userAksi.setForeground(new java.awt.Color(40, 40, 40));
        userAksi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        userAksi.setPreferredSize(new java.awt.Dimension(270, 30));
        userAksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                userAksiKeyPressed(evt);
            }
        });
        panelTengahAksi.add(userAksi);
        userAksi.setBounds(120, 30, 270, 30);

        passAksi.setForeground(new java.awt.Color(40, 40, 40));
        passAksi.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        passAksi.setPreferredSize(new java.awt.Dimension(270, 30));
        passAksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passAksiKeyPressed(evt);
            }
        });
        panelTengahAksi.add(passAksi);
        passAksi.setBounds(120, 70, 270, 30);

        label1.setText("User ID :");
        label1.setFocusable(false);
        label1.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
        label1.setPreferredSize(new java.awt.Dimension(110, 30));
        panelTengahAksi.add(label1);
        label1.setBounds(0, 30, 110, 30);

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
        label2.setPreferredSize(new java.awt.Dimension(110, 30));
        panelTengahAksi.add(label2);
        label2.setBounds(0, 70, 110, 30);

        WindowAksi.getContentPane().add(panelTengahAksi, java.awt.BorderLayout.CENTER);

        panelBawahAksi.setOpaque(false);
        panelBawahAksi.setPreferredSize(new java.awt.Dimension(50, 50));

        btnAksiKonfirmasi.setText("KONFIRMASI");
        btnAksiKonfirmasi.setFont(new java.awt.Font("Inter", 1, 14)); // NOI18N
        btnAksiKonfirmasi.setPreferredSize(new java.awt.Dimension(140, 35));
        btnAksiKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiKonfirmasiActionPerformed(evt);
            }
        });
        panelBawahAksi.add(btnAksiKonfirmasi);

        btnAksiBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnAksiBatal.setForeground(new java.awt.Color(255, 23, 26));
        btnAksiBatal.setText("Batal");
        btnAksiBatal.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        btnAksiBatal.setPreferredSize(new java.awt.Dimension(90, 35));
        btnAksiBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiBatalActionPerformed(evt);
            }
        });
        panelBawahAksi.add(btnAksiBatal);

        WindowAksi.getContentPane().add(panelBawahAksi, java.awt.BorderLayout.PAGE_END);

        lblTerapi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTerapi.setText("Terapi / Rehabilitasi Medik");
        lblTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lblTerapi.setPreferredSize(new java.awt.Dimension(55, 23));

        KdPoliTerapi.setEditable(false);
        KdPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KdPoliTerapi.setHighlighter(null);

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

        btnDokterTerapi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDokterTerapi.setMnemonic('X');
        btnDokterTerapi.setToolTipText("Alt+X");
        btnDokterTerapi.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDokterTerapi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterTerapiActionPerformed(evt);
            }
        });

        NmDokterTerapi.setEditable(false);
        NmDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        NmDokterTerapi.setHighlighter(null);

        KodeDokterTerapi.setEditable(false);
        KodeDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KodeDokterTerapi.setHighlighter(null);

        panelAtas.setPreferredSize(new java.awt.Dimension(400, 40));
        panelAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 10));

        label4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label4.setText("DATA ELIGIBILITAS PESERTA JKN");
        label4.setFocusable(false);
        label4.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        panelAtas.add(label4);

        getContentPane().add(panelAtas, java.awt.BorderLayout.PAGE_START);

        panelTengah.setPreferredSize(new java.awt.Dimension(390, 120));
        panelTengah.setLayout(new java.awt.BorderLayout());

        panel1.setPreferredSize(new java.awt.Dimension(533, 310));
        panel1.setLayout(null);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        panel1.add(TPasien);
        TPasien.setBounds(345, 10, 685, 30);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        panel1.add(TNoRM);
        TNoRM.setBounds(230, 10, 110, 30);

        NoKartu.setEditable(false);
        NoKartu.setHighlighter(null);
        panel1.add(NoKartu);
        NoKartu.setBounds(730, 100, 300, 30);

        jLabel20.setText("Tgl. SEP :");
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel20);
        jLabel20.setBounds(625, 190, 100, 30);

        TanggalSEP.setEditable(false);
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        TanggalSEP.setOpaque(false);
        TanggalSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        panel1.add(TanggalSEP);
        TanggalSEP.setBounds(730, 190, 170, 30);

        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel22);
        jLabel22.setBounds(625, 160, 100, 30);

        TanggalRujuk.setEditable(false);
        TanggalRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        TanggalRujuk.setOpaque(false);
        TanggalRujuk.setPreferredSize(new java.awt.Dimension(95, 23));
        panel1.add(TanggalRujuk);
        TanggalRujuk.setBounds(730, 160, 170, 30);

        jLabel23.setText("No. SKDP / Surat Kontrol :");
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel23);
        jLabel23.setBounds(75, 70, 150, 30);

        NoRujukan.setEditable(false);
        NoRujukan.setHighlighter(null);
        panel1.add(NoRujukan);
        NoRujukan.setBounds(230, 100, 340, 30);

        jLabel10.setText("PPK Rujukan :");
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel10);
        jLabel10.setBounds(75, 130, 150, 30);

        KdPpkRujukan.setEditable(false);
        KdPpkRujukan.setHighlighter(null);
        panel1.add(KdPpkRujukan);
        KdPpkRujukan.setBounds(230, 130, 75, 30);

        NmPpkRujukan.setEditable(false);
        NmPpkRujukan.setHighlighter(null);
        panel1.add(NmPpkRujukan);
        NmPpkRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel11);
        jLabel11.setBounds(75, 160, 150, 30);

        KdPenyakit.setEditable(false);
        KdPenyakit.setHighlighter(null);
        panel1.add(KdPenyakit);
        KdPenyakit.setBounds(230, 160, 75, 30);

        NmPenyakit.setEditable(false);
        NmPenyakit.setHighlighter(null);
        panel1.add(NmPenyakit);
        NmPenyakit.setBounds(310, 160, 260, 30);

        NmPoli.setEditable(false);
        NmPoli.setHighlighter(null);
        panel1.add(NmPoli);
        NmPoli.setBounds(310, 190, 260, 30);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        panel1.add(KdPoli);
        KdPoli.setBounds(230, 190, 75, 30);

        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(LabelPoli);
        LabelPoli.setBounds(75, 190, 150, 30);

        LabelKelas.setText("Kelas :");
        panel1.add(LabelKelas);
        LabelKelas.setBounds(75, 250, 150, 30);

        Kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        Kelas.setSelectedIndex(2);
        panel1.add(Kelas);
        Kelas.setBounds(230, 250, 150, 30);

        jLabel8.setText("Data Pasien :");
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel8);
        jLabel8.setBounds(75, 10, 150, 30);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        panel1.add(TglLahir);
        TglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setText("L / P :");
        panel1.add(jLabel18);
        jLabel18.setBounds(910, 40, 35, 30);

        JK.setEditable(false);
        JK.setHighlighter(null);
        panel1.add(JK);
        JK.setBounds(950, 40, 80, 30);

        jLabel24.setText("Jenis Peserta :");
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel24);
        jLabel24.setBounds(625, 40, 100, 30);

        JenisPeserta.setEditable(false);
        JenisPeserta.setHighlighter(null);
        panel1.add(JenisPeserta);
        JenisPeserta.setBounds(730, 40, 173, 30);

        jLabel25.setText("Status :");
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel25);
        jLabel25.setBounds(365, 40, 50, 30);

        Status.setEditable(false);
        Status.setHighlighter(null);
        panel1.add(Status);
        Status.setBounds(420, 40, 150, 30);

        jLabel27.setText("Asal Rujukan :");
        panel1.add(jLabel27);
        jLabel27.setBounds(625, 130, 100, 30);

        AsalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        panel1.add(AsalRujukan);
        AsalRujukan.setBounds(730, 130, 170, 30);

        NoTelp.setHighlighter(null);
        NoTelp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                NoTelpFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                NoTelpFocusLost(evt);
            }
        });
        NoTelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NoTelpMouseClicked(evt);
            }
        });
        panel1.add(NoTelp);
        NoTelp.setBounds(730, 250, 170, 30);

        Katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        panel1.add(Katarak);
        Katarak.setBounds(730, 220, 170, 30);

        jLabel37.setText("Katarak :");
        panel1.add(jLabel37);
        jLabel37.setBounds(625, 220, 100, 30);

        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(LabelPoli2);
        LabelPoli2.setBounds(75, 220, 150, 30);

        KdDPJP.setEditable(false);
        KdDPJP.setHighlighter(null);
        panel1.add(KdDPJP);
        KdDPJP.setBounds(230, 220, 75, 30);

        NmDPJP.setEditable(false);
        NmDPJP.setHighlighter(null);
        panel1.add(NmDPJP);
        NmDPJP.setBounds(310, 220, 260, 30);

        btnCariDokter.setBackground(new java.awt.Color(238, 238, 255));
        btnCariDokter.setBorder(null);
        btnCariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariDokter.setMnemonic('X');
        btnCariDokter.setToolTipText("Alt+X");
        btnCariDokter.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDokterActionPerformed(evt);
            }
        });
        panel1.add(btnCariDokter);
        btnCariDokter.setBounds(575, 220, 40, 30);

        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel12);
        jLabel12.setBounds(75, 40, 150, 30);

        jLabel6.setText("NIK :");
        panel1.add(jLabel6);
        jLabel6.setBounds(625, 70, 100, 30);

        NoSKDP.setEditable(false);
        NoSKDP.setHighlighter(null);
        panel1.add(NoSKDP);
        NoSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setText("No. Rujukan :");
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel26);
        jLabel26.setBounds(75, 100, 150, 30);

        NIK.setEditable(false);
        NIK.setHighlighter(null);
        panel1.add(NIK);
        NIK.setBounds(730, 70, 300, 30);

        jLabel7.setText("No. Peserta :");
        panel1.add(jLabel7);
        jLabel7.setBounds(625, 100, 100, 30);

        btnCariPoli.setBackground(new java.awt.Color(238, 238, 255));
        btnCariPoli.setBorder(null);
        btnCariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariPoli.setMnemonic('X');
        btnCariPoli.setToolTipText("Alt+X");
        btnCariPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPoliActionPerformed(evt);
            }
        });
        panel1.add(btnCariPoli);
        btnCariPoli.setBounds(575, 190, 40, 30);

        btnDiagnosaAwal.setBackground(new java.awt.Color(238, 238, 255));
        btnDiagnosaAwal.setBorder(null);
        btnDiagnosaAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal.setMnemonic('X');
        btnDiagnosaAwal.setToolTipText("Alt+X");
        btnDiagnosaAwal.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwalActionPerformed(evt);
            }
        });
        panel1.add(btnDiagnosaAwal);
        btnDiagnosaAwal.setBounds(575, 160, 40, 30);

        btnCariNoRujukan.setBackground(new java.awt.Color(238, 238, 255));
        btnCariNoRujukan.setBorder(null);
        btnCariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnCariNoRujukan.setMnemonic('X');
        btnCariNoRujukan.setToolTipText("Alt+X");
        btnCariNoRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnCariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariNoRujukanActionPerformed(evt);
            }
        });
        panel1.add(btnCariNoRujukan);
        btnCariNoRujukan.setBounds(575, 100, 40, 30);

        btnRiwayatPelayanan.setBackground(new java.awt.Color(255, 255, 255));
        btnRiwayatPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        btnRiwayatPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayanan.setMnemonic('X');
        btnRiwayatPelayanan.setText("Riwayat Layanan BPJS");
        btnRiwayatPelayanan.setToolTipText("Alt+X");
        btnRiwayatPelayanan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRiwayatPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananActionPerformed(evt);
            }
        });
        panel1.add(btnRiwayatPelayanan);
        btnRiwayatPelayanan.setBounds(980, 220, 220, 30);

        jLabel57.setText("No. Telp :");
        jLabel57.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel57);
        jLabel57.setBounds(625, 250, 100, 30);

        btnFingerprint.setBackground(new java.awt.Color(255, 255, 255));
        btnFingerprint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setMnemonic('X');
        btnFingerprint.setText("Fingerprint");
        btnFingerprint.setToolTipText("Alt+X");
        btnFingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFingerprint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerprintActionPerformed(evt);
            }
        });
        panel1.add(btnFingerprint);
        btnFingerprint.setBounds(1080, 100, 120, 80);

        btnFrista.setBackground(new java.awt.Color(255, 255, 255));
        btnFrista.setForeground(new java.awt.Color(0, 131, 62));
        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-scan.png"))); // NOI18N
        btnFrista.setMnemonic('X');
        btnFrista.setText("FRISTA");
        btnFrista.setToolTipText("Alt+X");
        btnFrista.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFrista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFrista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFristaActionPerformed(evt);
            }
        });
        panel1.add(btnFrista);
        btnFrista.setBounds(1080, 10, 120, 80);

        panelNumpad1.setFontSize(30);
        panelNumpad1.setTextBox(NoTelp);
        panel1.add(panelNumpad1);
        panelNumpad1.setBounds(730, 290, 210, 280);

        panelTengah.add(panel1, java.awt.BorderLayout.PAGE_START);

        panel2.setLayout(new java.awt.BorderLayout());

        form.setOpaque(false);
        form.setLayout(null);

        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel13);
        jLabel13.setBounds(75, 10, 150, 30);

        JenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        JenisPelayanan.setSelectedIndex(1);
        JenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JenisPelayananItemStateChanged(evt);
            }
        });
        form.add(JenisPelayanan);
        JenisPelayanan.setBounds(230, 10, 150, 30);

        jLabel55.setText("Laka Lantas :");
        form.add(jLabel55);
        jLabel55.setBounds(625, 10, 100, 30);

        LakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        LakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        LakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LakaLantasItemStateChanged(evt);
            }
        });
        form.add(LakaLantas);
        LakaLantas.setBounds(730, 10, 170, 30);

        TanggalKKL.setEditable(false);
        TanggalKKL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        TanggalKKL.setEnabled(false);
        TanggalKKL.setOpaque(false);
        TanggalKKL.setPreferredSize(new java.awt.Dimension(64, 25));
        form.add(TanggalKKL);
        TanggalKKL.setBounds(730, 40, 170, 30);

        jLabel38.setText("Tgl. KLL :");
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel38);
        jLabel38.setBounds(625, 40, 100, 30);

        TujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        TujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TujuanKunjunganItemStateChanged(evt);
            }
        });
        form.add(TujuanKunjungan);
        TujuanKunjungan.setBounds(230, 40, 340, 30);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel42);
        jLabel42.setBounds(75, 40, 150, 30);

        jLabel43.setText("Flag Prosedur :");
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel43);
        jLabel43.setBounds(75, 70, 150, 30);

        FlagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        FlagProsedur.setEnabled(false);
        form.add(FlagProsedur);
        FlagProsedur.setBounds(230, 70, 340, 30);

        jLabel36.setText("Keterangan :");
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel36);
        jLabel36.setBounds(625, 70, 100, 30);

        Keterangan.setEditable(false);
        Keterangan.setHighlighter(null);
        form.add(Keterangan);
        Keterangan.setBounds(730, 70, 300, 30);

        NoSEPSuplesi.setEditable(false);
        NoSEPSuplesi.setHighlighter(null);
        form.add(NoSEPSuplesi);
        NoSEPSuplesi.setBounds(890, 100, 140, 30);

        jLabel41.setText("No. SEP :");
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel41);
        jLabel41.setBounds(830, 100, 55, 30);

        Suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        Suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SuplesiItemStateChanged(evt);
            }
        });
        form.add(Suplesi);
        Suplesi.setBounds(730, 100, 95, 30);

        jLabel40.setText("Suplesi :");
        form.add(jLabel40);
        jLabel40.setBounds(625, 100, 100, 30);

        Penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        Penunjang.setEnabled(false);
        form.add(Penunjang);
        Penunjang.setBounds(230, 100, 340, 30);

        jLabel44.setText("Penunjang :");
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel44);
        jLabel44.setBounds(75, 100, 150, 30);

        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel45);
        jLabel45.setBounds(75, 130, 150, 30);

        AsesmenPoli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        form.add(AsesmenPoli);
        AsesmenPoli.setBounds(230, 130, 340, 30);

        LabelPoli3.setText("Propinsi KLL :");
        form.add(LabelPoli3);
        LabelPoli3.setBounds(625, 130, 100, 30);

        KdPropinsi.setEditable(false);
        KdPropinsi.setHighlighter(null);
        form.add(KdPropinsi);
        KdPropinsi.setBounds(730, 130, 75, 30);

        NmPropinsi.setEditable(false);
        NmPropinsi.setHighlighter(null);
        form.add(NmPropinsi);
        NmPropinsi.setBounds(810, 130, 220, 30);

        NmKabupaten.setEditable(false);
        NmKabupaten.setHighlighter(null);
        form.add(NmKabupaten);
        NmKabupaten.setBounds(810, 160, 220, 30);

        KdKabupaten.setEditable(false);
        KdKabupaten.setHighlighter(null);
        form.add(KdKabupaten);
        KdKabupaten.setBounds(730, 160, 75, 30);

        LabelPoli4.setText("Kabupaten KLL :");
        form.add(LabelPoli4);
        LabelPoli4.setBounds(625, 160, 100, 30);

        NmDPJPLayanan.setEditable(false);
        NmDPJPLayanan.setHighlighter(null);
        form.add(NmDPJPLayanan);
        NmDPJPLayanan.setBounds(310, 160, 260, 30);

        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(LabelPoli7);
        LabelPoli7.setBounds(75, 160, 150, 30);

        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel9);
        jLabel9.setBounds(75, 190, 150, 30);

        KdPPK.setEditable(false);
        KdPPK.setHighlighter(null);
        form.add(KdPPK);
        KdPPK.setBounds(230, 190, 75, 30);

        NmPPK.setEditable(false);
        NmPPK.setHighlighter(null);
        form.add(NmPPK);
        NmPPK.setBounds(310, 190, 260, 30);

        LabelPoli5.setText("Kecamatan KLL :");
        form.add(LabelPoli5);
        LabelPoli5.setBounds(625, 190, 100, 30);

        KdKecamatan.setEditable(false);
        KdKecamatan.setHighlighter(null);
        form.add(KdKecamatan);
        KdKecamatan.setBounds(730, 190, 75, 30);

        NmKecamatan.setEditable(false);
        NmKecamatan.setHighlighter(null);
        form.add(NmKecamatan);
        NmKecamatan.setBounds(810, 190, 220, 30);

        Catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        Catatan.setHighlighter(null);
        form.add(Catatan);
        Catatan.setBounds(730, 220, 300, 30);

        jLabel14.setText("Catatan :");
        form.add(jLabel14);
        jLabel14.setBounds(625, 220, 100, 30);

        JumlahBarcode.setText("3");
        JumlahBarcode.setHighlighter(null);
        form.add(JumlahBarcode);
        JumlahBarcode.setBounds(230, 220, 50, 30);

        jLabel15.setText("Jumlah Barcode :");
        form.add(jLabel15);
        jLabel15.setBounds(75, 220, 150, 30);

        KdDPJPLayanan.setEditable(false);
        KdDPJPLayanan.setHighlighter(null);
        form.add(KdDPJPLayanan);
        KdDPJPLayanan.setBounds(230, 160, 75, 30);

        btnApprovalFP.setBackground(new java.awt.Color(255, 255, 255));
        btnApprovalFP.setForeground(new java.awt.Color(0, 131, 62));
        btnApprovalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnApprovalFP.setMnemonic('X');
        btnApprovalFP.setText("Approval FP");
        btnApprovalFP.setToolTipText("Alt+X");
        btnApprovalFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnApprovalFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnApprovalFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApprovalFPActionPerformed(evt);
            }
        });
        form.add(btnApprovalFP);
        btnApprovalFP.setBounds(1080, 190, 120, 90);

        btnPengajuanFP.setBackground(new java.awt.Color(255, 255, 255));
        btnPengajuanFP.setForeground(new java.awt.Color(0, 131, 62));
        btnPengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnPengajuanFP.setMnemonic('X');
        btnPengajuanFP.setText("Pengajuan FP");
        btnPengajuanFP.setToolTipText("Alt+X");
        btnPengajuanFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPengajuanFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPengajuanFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPengajuanFPActionPerformed(evt);
            }
        });
        form.add(btnPengajuanFP);
        btnPengajuanFP.setBounds(1080, 80, 120, 90);

        lblNoRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRawat.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        lblNoRawat.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(lblNoRawat);
        lblNoRawat.setBounds(730, 250, 220, 30);

        panel2.add(form, java.awt.BorderLayout.CENTER);

        ChkInput.setForeground(new java.awt.Color(150, 155, 159));
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 30));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        panel2.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        panelTengah.add(panel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelTengah, java.awt.BorderLayout.CENTER);

        panelBawah.setMinimumSize(new java.awt.Dimension(533, 120));
        panelBawah.setPreferredSize(new java.awt.Dimension(533, 100));

        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("KONFIRMASI");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 60));
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

        btnKeluar.setBackground(new java.awt.Color(255, 255, 255));
        btnKeluar.setForeground(new java.awt.Color(255, 33, 32));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/exit.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText("Batal");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKeluar.setPreferredSize(new java.awt.Dimension(300, 60));
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

        getContentPane().add(panelBawah, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCariDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDokterActionPerformed
        dokter.setSize(getContentPane().getSize());
        dokter.setLocationRelativeTo(getContentPane());
        dokter.carinamadokter(KdPoli.getText(), NmPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_btnCariDokterActionPerformed

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
            btnCariDokter.setEnabled(false);
        } else if (JenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            KdPoli.setVisible(true);
            NmPoli.setVisible(true);

            btnCariDokter.setEnabled(true);
        }
    }//GEN-LAST:event_JenisPelayananItemStateChanged

    private void btnCariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPoliActionPerformed
        poli.setSize(getContentPane().getSize());
        poli.setLocationRelativeTo(getContentPane());
        poli.setVisible(true);
    }//GEN-LAST:event_btnCariPoliActionPerformed

    private void btnDiagnosaAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwalActionPerformed
        penyakit.setSize(getContentPane().getSize());
        penyakit.setLocationRelativeTo(getContentPane());
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaAwalActionPerformed

    private void btnCariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariNoRujukanActionPerformed
        if (NoKartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(getContentPane().getSize());
            rujukanterakhir.setLocationRelativeTo(getContentPane());
            rujukanterakhir.tampil(NoKartu.getText(), TPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnCariNoRujukanActionPerformed

    private void btnRiwayatPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        historiPelayanan.setSize(getContentPane().getSize());
        historiPelayanan.setLocationRelativeTo(getContentPane());
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPelayananActionPerformed

    private void btnDokterTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterTerapiActionPerformed
        doktermapping.setSize(getContentPane().getSize());
        doktermapping.setLocationRelativeTo(getContentPane());
        doktermapping.tampilDokterMapping(KdDPJPLayanan.getText());
        doktermapping.setVisible(true);
    }//GEN-LAST:event_btnDokterTerapiActionPerformed

    private void btnPoliTerapiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoliTerapiActionPerformed
        polimapping.setSize(getContentPane().getSize());
        polimapping.setLocationRelativeTo(getContentPane());
        polimapping.tampilPoliMapping(KdPoli.getText());
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
            JOptionPane.showMessageDialog(null, "Silahkan lakukan validasi biometrik dahulu..!!");
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
                        JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            SimpanAntrianOnSite();
                        }
                        insertSEP();
                    }
                } else if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", NoKartu.getText().trim(), JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.SetTgl(TanggalSEP.getSelectedItem().toString())) >= 1) {
                        JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
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
        if (!NoKartu.getText().isBlank()) {
            aksi = "Approval";
            WindowAksi.setSize(415, 250);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (!NoKartu.getText().isBlank()) {
            aksi = "Pengajuan";
            WindowAksi.setSize(415, 250);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
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

    private void userAksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userAksiKeyPressed
        Valid.pindah(evt, btnAksiBatal, passAksi);
    }//GEN-LAST:event_userAksiKeyPressed

    private void passAksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passAksiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAksiKonfirmasiActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            userAksi.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            btnAksiKonfirmasi.requestFocus();
        }
    }//GEN-LAST:event_passAksiKeyPressed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try {
                ps = koneksi.prepareStatement("select aes_decrypt(id_user, 'nur') from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1");
                try {
                    ps.setString(1, new String(userAksi.getPassword()));
                    ps.setString(2, new String(passAksi.getPassword()));
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
                                URL = koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                                requestJson = " {" +
                                    "\"request\": {" +
                                    "\"t_sep\": {" +
                                    "\"noKartu\": \"" + NoKartu.getText() + "\"," +
                                    "\"tglSep\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\"," +
                                    "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                    "\"jnsPengajuan\": \"2\"," +
                                    "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\"," +
                                    "\"user\": \"" + rs.getString(1) + "\"" +
                                    "}" +
                                    "}" +
                                    "}";
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
                            System.out.println("Aksi " + aksi);
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                utc = String.valueOf(api.GetUTCdatetimeAsString());
                                headers.add("X-Timestamp", utc);
                                headers.add("X-Signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                URL = koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                                requestJson = " {" +
                                    "\"request\": {" +
                                    "\"t_sep\": {" +
                                    "\"noKartu\": \"" + NoKartu.getText() + "\"," +
                                    "\"tglSep\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\"," +
                                    "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                    "\"jnsPengajuan\": \"2\"," +
                                    "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\"," +
                                    "\"user\": \"" + rs.getString(1) + "\"" +
                                    "}" +
                                    "}" +
                                    "}";
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
                            resetAksi();
                            JOptionPane.showMessageDialog(rootPane, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                        }
                    } else {
                        resetAksi();
                        JOptionPane.showMessageDialog(rootPane, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
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
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        userAksi.setText("");
        passAksi.setText("");
        aksi = "";
        WindowAksi.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void btnFingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerprintActionPerformed
        bukaAplikasiFingerprint();
    }//GEN-LAST:event_btnFingerprintActionPerformed

    private void btnFristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFristaActionPerformed
        bukaAplikasiFrista();
    }//GEN-LAST:event_btnFristaActionPerformed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void NoTelpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NoTelpFocusGained
        ChkInput.setSelected(false);
        panelNumpad1.setVisible(true);
        isForm();
    }//GEN-LAST:event_NoTelpFocusGained

    private void NoTelpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NoTelpFocusLost
        panelNumpad1.setVisible(false);
    }//GEN-LAST:event_NoTelpFocusLost

    private void NoTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NoTelpMouseClicked
        if (ChkInput.isSelected()) {
            ChkInput.setSelected(false);
            panelNumpad1.setVisible(true);
            isForm();
        }
    }//GEN-LAST:event_NoTelpMouseClicked

    private void SuplesiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SuplesiItemStateChanged
        if (Suplesi.getSelectedIndex() == 1) {
            NoSEPSuplesi.setEditable(true);
        } else {
            NoSEPSuplesi.setEditable(false);
            NoSEPSuplesi.setText("");
        }
    }//GEN-LAST:event_SuplesiItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.ComboBox AsalRujukan;
    private widget.ComboBox AsesmenPoli;
    private widget.TextField Biaya;
    private widget.TextField Catatan;
    private widget.PaneToggle ChkInput;
    private widget.ComboBox FlagProsedur;
    private widget.TextField JK;
    private widget.ComboBox JenisPelayanan;
    private widget.TextField JenisPeserta;
    private widget.TextField JumlahBarcode;
    private widget.ComboBox Katarak;
    private widget.TextField KdDPJP;
    private widget.TextField KdDPJPLayanan;
    private widget.TextField KdKabupaten;
    private widget.TextField KdKecamatan;
    private widget.TextField KdPPK;
    private widget.TextField KdPenyakit;
    private widget.TextField KdPoli;
    private widget.TextField KdPoliTerapi;
    private widget.TextField KdPpkRujukan;
    private widget.TextField KdPropinsi;
    private widget.TextField Kdpnj;
    private widget.ComboBox Kelas;
    private widget.TextField Keterangan;
    private widget.TextField KodeDokterTerapi;
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.ComboBox LakaLantas;
    private widget.Label LblKdDokter;
    private widget.Label LblKdPoli;
    private widget.TextField NIK;
    private widget.TextField NmDPJP;
    private widget.TextField NmDPJPLayanan;
    private widget.TextField NmDokterTerapi;
    private widget.TextField NmKabupaten;
    private widget.TextField NmKecamatan;
    private widget.TextField NmPPK;
    private widget.TextField NmPenyakit;
    private widget.TextField NmPoli;
    private widget.TextField NmPoliTerapi;
    private widget.TextField NmPpkRujukan;
    private widget.TextField NmPropinsi;
    private widget.TextField NoKartu;
    private widget.TextField NoRawat;
    private widget.TextField NoReg;
    private widget.TextField NoRujukMasuk;
    private widget.TextField NoRujukan;
    private widget.TextField NoSEPSuplesi;
    private widget.TextField NoSKDP;
    private widget.TextField NoTelp;
    private widget.Label NoTelpPasien;
    private widget.ComboBox Penunjang;
    private widget.TextField Status;
    private widget.ComboBox Suplesi;
    private widget.Label TAlmt;
    private widget.TextField TBiaya;
    private widget.Label THbngn;
    private widget.TextField TNoRM;
    private widget.TextField TNoRw;
    private widget.TextField TPasien;
    private widget.Label TPngJwb;
    private widget.Tanggal Tanggal;
    private widget.Tanggal TanggalKKL;
    private widget.Tanggal TanggalRujuk;
    private widget.Tanggal TanggalSEP;
    private widget.TextField TglLahir;
    private widget.ComboBox TujuanKunjungan;
    private widget.Dialog WindowAksi;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnApprovalFP;
    private widget.Button btnCariDokter;
    private widget.Button btnCariNoRujukan;
    private widget.Button btnCariPoli;
    private widget.Button btnDiagnosaAwal;
    private widget.Button btnDokterTerapi;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private widget.Button btnKeluar;
    private widget.Button btnPengajuanFP;
    private widget.Button btnPoliTerapi;
    private widget.Button btnRiwayatPelayanan;
    private widget.Button btnSimpan;
    private widget.Panel form;
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
    private widget.Label judulAksi;
    private widget.TextField kdpoli;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label4;
    private widget.Label lblNoRawat;
    private widget.Label lblTerapi;
    private widget.TextField nmpnj;
    private widget.Panel panel1;
    private widget.Panel panel2;
    private widget.Panel panelAtas;
    private widget.Panel panelBawah;
    private widget.Panel panelBawahAksi;
    private widget.Numpad panelNumpad1;
    private widget.Panel panelTengah;
    private widget.Panel panelTengahAksi;
    private widget.PasswordField passAksi;
    private widget.PasswordField userAksi;
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
            URL = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            requestJson = "{" +
                "\"request\":{" +
                "\"t_sep\":{" +
                "\"noKartu\":\"" + NoKartu.getText() + "\"," +
                "\"tglSep\":\"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\"," +
                "\"ppkPelayanan\":\"" + KdPPK.getText() + "\"," +
                "\"jnsPelayanan\":\"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"klsRawat\":{" +
                "\"klsRawatHak\":\"" + Kelas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"klsRawatNaik\":\"\"," +
                "\"pembiayaan\":\"\"," +
                "\"penanggungJawab\":\"\"" +
                "}," +
                "\"noMR\":\"" + TNoRM.getText() + "\"," +
                "\"rujukan\": {" +
                "\"asalRujukan\":\"" + AsalRujukan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"tglRujukan\":\"" + Valid.SetTgl(TanggalRujuk.getSelectedItem() + "") + "\"," +
                "\"noRujukan\":\"" + NoRujukan.getText() + "\"," +
                "\"ppkRujukan\":\"" + KdPpkRujukan.getText() + "\"" +
                "}," +
                "\"catatan\":\"" + Catatan.getText() + "\"," +
                "\"diagAwal\":\"" + KdPenyakit.getText() + "\"," +
                "\"poli\": {" +
                "\"tujuan\": \"" + KdPoli.getText() + "\"," +
                "\"eksekutif\": \"0\"" +
                "}," +
                "\"cob\": {" +
                "\"cob\": \"0\"" +
                "}," +
                "\"katarak\": {" +
                "\"katarak\": \"" + Katarak.getSelectedItem().toString().substring(0, 1) + "\"" +
                "}," +
                "\"jaminan\": {" +
                "\"lakaLantas\":\"" + LakaLantas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"penjamin\": {" +
                "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\"," +
                "\"keterangan\": \"" + Keterangan.getText() + "\"," +
                "\"suplesi\": {" +
                "\"suplesi\": \"" + Suplesi.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"noSepSuplesi\": \"" + NoSEPSuplesi.getText() + "\"," +
                "\"lokasiLaka\": {" +
                "\"kdPropinsi\": \"" + KdPropinsi.getText() + "\"," +
                "\"kdKabupaten\": \"" + KdKabupaten.getText() + "\"," +
                "\"kdKecamatan\": \"" + KdKecamatan.getText() + "\"" +
                "}" +
                "}" +
                "}" +
                "}," +
                "\"tujuanKunj\": \"" + TujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"flagProcedure\": \"" + (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"kdPenunjang\": \"" + (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : "") + "\"," +
                "\"assesmentPel\": \"" + (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"skdp\": {" +
                "\"noSurat\": \"" + NoSKDP.getText() + "\"," +
                "\"kodeDPJP\": \"" + KdDPJP.getText() + "\"" +
                "}," +
                "\"dpjpLayan\": \"" + (KdDPJPLayanan.getText().equals("") ? "" : KdDPJPLayanan.getText()) + "\"," +
                "\"noTelp\": \"" + NoTelp.getText() + "\"," +
                "\"user\":\"" + NoKartu.getText() + "\"" +
                "}" +
                "}" +
                "}";
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            JOptionPane.showMessageDialog(rootPane, "Respon BPJS : " + nameNode.path("message").asText());
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
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void cekFinger() {
        statusfinger = false;

        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. Kartu BPJS tidak ada..!!");

            return;
        }

        try {
            URL = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + NoKartu.getText() + "/TglPelayanan/" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString());
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
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
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
            "and not exists(select * from bridging_sep where bridging_sep.no_rawat = referensi_mobilejkn_bpjs.no_rawat)"
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
                        URL = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rsjkn.getString("nomorkartu") + "/tglSEP/" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString());
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
                                    URL = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rsjkn.getString("nomorreferensi");
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
                                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke Server BPJS terputus...!!!");
                                    }
                                    emptTeks();
                                }
                            } else if (jeniskunjungan.equals("4")) {
                                // RUJUKAN FKTL
                                AsalRujukan.setSelectedIndex(1);
                                try {
                                    URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rsjkn.getString("nomorreferensi");
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
                                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke Server BPJS terputus...!!!");
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
                                            if (!rskontrol.getString("tgl_rencana").equals(Valid.SetTgl(TanggalSEP.getSelectedItem().toString()))) {
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
                                                        URL = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                                        AsalRujukan.setSelectedIndex(0);
                                                    } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                                        URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
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
                                                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                                    }
                                                    emptTeks();
                                                }
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(rootPane, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                            emptTeks();
                                        }
                                    }
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(rootPane, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    emptTeks();
                                }
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "Maaf, antrian JKN tidak ditemukan!\nSilahkan hubungi administrasi.");
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
                            JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                        }
                        emptTeks();
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Maaf, pasien membatalkan antrian MobileJKN, atau telah menerima pelayanan!\nSilahkan hubungi administrasi.");
                    emptTeks();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(rootPane, "Maaf, terjadi kesalahan pada saat mencari rujukan di MobileJKN!\nSilahkan hubungi administrasi.");
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
                        requestJson = "{" +
                            "\"kodebooking\": \"" + rs.getString("nobooking") + "\"," +
                            "\"jenispasien\": \"JKN\"," +
                            "\"nomorkartu\": \"" + rs.getString("nomorkartu") + "\"," +
                            "\"nik\": \"" + rs.getString("nik") + "\"," +
                            "\"nohp\": \"" + NoTelp.getText().trim() + "\"," +
                            "\"kodepoli\": \"" + rs.getString("kodepoli") + "\"," +
                            "\"namapoli\": \"" + rs.getString("nm_poli") + "\"," +
                            "\"pasienbaru\": " + rs.getString("pasienbaru") + "," +
                            "\"norm\": \"" + rs.getString("no_rkm_medis") + "\"," +
                            "\"tanggalperiksa\": \"" + rs.getString("tanggalperiksa") + "\"," +
                            "\"kodedokter\": " + rs.getString("kodedokter") + "," +
                            "\"namadokter\": \"" + rs.getString("nm_dokter") + "\"," +
                            "\"jampraktek\": \"" + rs.getString("jampraktek") + "\"," +
                            "\"jeniskunjungan\": " + rs.getString("jeniskunjungan").substring(0, 1) + "," +
                            "\"nomorreferensi\": \"" + rs.getString("nomorreferensi") + "\"," +
                            "\"nomorantrean\": \"" + rs.getString("nomorantrean") + "\"," +
                            "\"angkaantrean\": " + rs.getInt("angkaantrean") + "," +
                            "\"estimasidilayani\": " + rs.getString("estimasidilayani") + "," +
                            "\"sisakuotajkn\": " + rs.getInt("sisakuotajkn") + "," +
                            "\"kuotajkn\": " + rs.getInt("kuotajkn") + "," +
                            "\"sisakuotanonjkn\": " + rs.getInt("sisakuotanonjkn") + "," +
                            "\"kuotanonjkn\": " + rs.getInt("kuotanonjkn") + "," +
                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                            "}";
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
        btnCariDokter.setEnabled(true);
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
            URL = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
            requestJson = "{" +
                "\"request\": {" +
                "\"noSuratKontrol\":\"" + noSKDP + "\"," +
                "\"noSEP\":\"" + noSEP + "\"," +
                "\"kodeDokter\":\"" + kodeDPJP + "\"," +
                "\"poliKontrol\":\"" + kodePoli + "\"," +
                "\"tglRencanaKontrol\":\"" + tanggalPeriksa + "\"," +
                "\"user\":\"" + noKartu + "\"" +
                "}" +
                "}";
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
        while (coba < maxCoba && !sukses) {
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
        userAksi.setText("");
        passAksi.setText("");
        aksi = "";
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            panelNumpad1.setVisible(false);
            panel1.setPreferredSize(new Dimension(WIDTH, 310));
            panel2.setPreferredSize(new Dimension(WIDTH, 290));
            form.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            panel1.setPreferredSize(new Dimension(WIDTH, 610));
            panel2.setPreferredSize(new Dimension(WIDTH, 30));
            form.setVisible(false);
            ChkInput.setVisible(true);
        }
    }
}
