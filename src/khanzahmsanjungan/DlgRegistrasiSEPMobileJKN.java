package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSCekReferensiDokterDPJP;
import bridging.BPJSCekReferensiPenyakit;
import bridging.BPJSCekRiwayatPelayanan;
import bridging.BPJSCekRiwayatRujukanTerakhir;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class DlgRegistrasiSEPMobileJKN extends widget.Dialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final BPJSCekReferensiDokterDPJP dokter;
    private final BPJSCekReferensiPenyakit penyakit;
    private final DlgCariPoliBPJS poli;
    private final BPJSCekRiwayatRujukanTerakhir cariRujukan;
    private final BPJSCekRiwayatPelayanan riwayatPelayanan;
    private final boolean ADDANTRIANAPIMOBILEJKN = koneksiDB.ADDANTRIANAPIMOBILEJKN();
    private String aksi = "",
        tglkll = "0000-00-00",
        datajam = "",
        requestJson,
        URL = "",
        prb = "",
        nobooking = "",
        norawat = "",
        kodedokterreg = "",
        kodepolireg = "",
        utc = "",
        jeniskunjungan = "";

    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root, metadata, response;
    private boolean statusfinger = false, aplikasiAktif = false, fristaAktif = false;
    private HttpHeaders headers;
    private HttpEntity requestEntity;

    public DlgRegistrasiSEPMobileJKN(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        barcode.setDocument(new batasInput((byte) 3).getOnlyAngka(barcode));
        
        dokter = new BPJSCekReferensiDokterDPJP(parent, modal);
        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    kodeDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    namaDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    if (jenisPelayanan.getSelectedIndex() == 1) {
                        kodeDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        namaDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    }
                    kodeDPJP.requestFocus();

                }
            }
        });

        poli = new DlgCariPoliBPJS(parent, modal);
        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.hasSelectedRow()) {
                    kodePoli.setText(poli.getSelectedRow(0).toString());
                    namaPoli.setText(poli.getSelectedRow(1).toString());
                    kodeDPJP.requestFocus();
                }
            }
        });

        penyakit = new BPJSCekReferensiPenyakit(parent, modal);
        penyakit.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (penyakit.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString());
                    namaDiagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 2).toString());
                    kodeDiagnosa.requestFocus();
                }
            }
        });

        cariRujukan = new BPJSCekRiwayatRujukanTerakhir(parent, modal);
        cariRujukan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (cariRujukan.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 0).toString());
                    namaDiagnosa.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 1).toString());
                    noRujukan.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 2).toString());
                    kodePoli.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 3).toString());
                    namaPoli.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 4).toString());
                    kodePPKRujukan.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 6).toString());
                    namaPPKRujukan.setText(cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 7).toString());
                    Valid.SetTgl(tglRujukan, cariRujukan.getTable().getValueAt(cariRujukan.getTable().getSelectedRow(), 5).toString());
                    catatan.requestFocus();
                }
            }
        });

        riwayatPelayanan = new BPJSCekRiwayatPelayanan(parent, modal);
        riwayatPelayanan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (riwayatPelayanan.getTable().getSelectedRow() != -1) {
                    if ((riwayatPelayanan.getTable().getSelectedColumn() == 6) || (riwayatPelayanan.getTable().getSelectedColumn() == 7)) {
                        noRujukan.setText(riwayatPelayanan.getTable().getValueAt(riwayatPelayanan.getTable().getSelectedRow(), riwayatPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                noRujukan.requestFocus();
            }
        });

        kodePPK.setText(Sequel.cariIsiSmc("select setting.kode_ppk from setting"));
        namaPPK.setText(Sequel.cariIsiSmc("select setting.nama_instansi from setting"));
        barcode.setText("3");
        isForm();
        panelNumpad1.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        panelAtas = new widget.Panel();
        label4 = new widget.Label();
        panelTengah = new widget.Panel();
        panel1 = new widget.Panel();
        namaPasien = new widget.TextField();
        noRM = new widget.TextField();
        noPeserta = new widget.TextField();
        jLabel20 = new widget.Label();
        tglSEP = new widget.Tanggal();
        jLabel22 = new widget.Label();
        tglRujukan = new widget.Tanggal();
        jLabel23 = new widget.Label();
        noRujukan = new widget.TextField();
        jLabel10 = new widget.Label();
        kodePPKRujukan = new widget.TextField();
        namaPPKRujukan = new widget.TextField();
        jLabel11 = new widget.Label();
        kodeDiagnosa = new widget.TextField();
        namaDiagnosa = new widget.TextField();
        namaPoli = new widget.TextField();
        kodePoli = new widget.TextField();
        LabelPoli = new widget.Label();
        LabelKelas = new widget.Label();
        kelas = new widget.ComboBox();
        jLabel8 = new widget.Label();
        tglLahir = new widget.TextField();
        jLabel18 = new widget.Label();
        jk = new widget.TextField();
        jLabel24 = new widget.Label();
        jenisPeserta = new widget.TextField();
        jLabel25 = new widget.Label();
        statusPeserta = new widget.TextField();
        jLabel27 = new widget.Label();
        asalRujukan = new widget.ComboBox();
        noTelp = new widget.TextField();
        katarak = new widget.ComboBox();
        jLabel37 = new widget.Label();
        LabelPoli2 = new widget.Label();
        kodeDPJP = new widget.TextField();
        namaDPJP = new widget.TextField();
        cariDPJP = new widget.Button();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        noSKDP = new widget.TextField();
        jLabel26 = new widget.Label();
        nik = new widget.TextField();
        jLabel7 = new widget.Label();
        cariPoli = new widget.Button();
        cariDiagnosa = new widget.Button();
        cariNoRujukan = new widget.Button();
        btnRiwayatPelayanan = new widget.Button();
        jLabel57 = new widget.Label();
        btnFingerprint = new widget.Button();
        btnFrista = new widget.Button();
        panelNumpad1 = new widget.Numpad();
        panel2 = new widget.Panel();
        form = new widget.Panel();
        jLabel13 = new widget.Label();
        jenisPelayanan = new widget.ComboBox();
        jLabel55 = new widget.Label();
        lakaLantas = new widget.ComboBox();
        tglKLL = new widget.Tanggal();
        jLabel38 = new widget.Label();
        tujuanKunjungan = new widget.ComboBox();
        jLabel42 = new widget.Label();
        jLabel43 = new widget.Label();
        flagProsedur = new widget.ComboBox();
        jLabel36 = new widget.Label();
        keterangan = new widget.TextField();
        noSEPSuplesi = new widget.TextField();
        jLabel41 = new widget.Label();
        suplesi = new widget.ComboBox();
        jLabel40 = new widget.Label();
        penunjang = new widget.ComboBox();
        jLabel44 = new widget.Label();
        jLabel45 = new widget.Label();
        asesmenPelayanan = new widget.ComboBox();
        LabelPoli3 = new widget.Label();
        kdPropKLL = new widget.TextField();
        nmPropKLL = new widget.TextField();
        nmKabKLL = new widget.TextField();
        kdKabKLL = new widget.TextField();
        LabelPoli4 = new widget.Label();
        namaDPJPLayanan = new widget.TextField();
        LabelPoli7 = new widget.Label();
        jLabel9 = new widget.Label();
        kodePPK = new widget.TextField();
        namaPPK = new widget.TextField();
        LabelPoli5 = new widget.Label();
        kdKecKLL = new widget.TextField();
        nmKecKLL = new widget.TextField();
        catatan = new widget.TextField();
        jLabel14 = new widget.Label();
        barcode = new widget.TextField();
        jLabel15 = new widget.Label();
        kodeDPJPLayanan = new widget.TextField();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        ChkInput = new widget.PaneToggle();
        panelBawah = new widget.Panel();
        btnSimpan = new widget.Button();
        btnKeluar = new widget.Button();

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

        namaPasien.setEditable(false);
        namaPasien.setHighlighter(null);
        panel1.add(namaPasien);
        namaPasien.setBounds(345, 10, 685, 30);

        noRM.setEditable(false);
        noRM.setHighlighter(null);
        panel1.add(noRM);
        noRM.setBounds(230, 10, 110, 30);

        noPeserta.setEditable(false);
        noPeserta.setHighlighter(null);
        panel1.add(noPeserta);
        noPeserta.setBounds(730, 100, 300, 30);

        jLabel20.setText("Tgl. SEP :");
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel20);
        jLabel20.setBounds(625, 190, 100, 30);

        tglSEP.setEditable(false);
        tglSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglSEP.setOpaque(false);
        tglSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        panel1.add(tglSEP);
        tglSEP.setBounds(730, 190, 170, 30);

        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel22);
        jLabel22.setBounds(625, 160, 100, 30);

        tglRujukan.setEditable(false);
        tglRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglRujukan.setOpaque(false);
        tglRujukan.setPreferredSize(new java.awt.Dimension(95, 23));
        panel1.add(tglRujukan);
        tglRujukan.setBounds(730, 160, 170, 30);

        jLabel23.setText("No. SKDP / Surat Kontrol :");
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel23);
        jLabel23.setBounds(75, 70, 150, 30);

        noRujukan.setEditable(false);
        noRujukan.setHighlighter(null);
        panel1.add(noRujukan);
        noRujukan.setBounds(230, 100, 340, 30);

        jLabel10.setText("PPK Rujukan :");
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel10);
        jLabel10.setBounds(75, 130, 150, 30);

        kodePPKRujukan.setEditable(false);
        kodePPKRujukan.setHighlighter(null);
        panel1.add(kodePPKRujukan);
        kodePPKRujukan.setBounds(230, 130, 75, 30);

        namaPPKRujukan.setEditable(false);
        namaPPKRujukan.setHighlighter(null);
        panel1.add(namaPPKRujukan);
        namaPPKRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel11);
        jLabel11.setBounds(75, 160, 150, 30);

        kodeDiagnosa.setEditable(false);
        kodeDiagnosa.setHighlighter(null);
        panel1.add(kodeDiagnosa);
        kodeDiagnosa.setBounds(230, 160, 75, 30);

        namaDiagnosa.setEditable(false);
        namaDiagnosa.setHighlighter(null);
        panel1.add(namaDiagnosa);
        namaDiagnosa.setBounds(310, 160, 260, 30);

        namaPoli.setEditable(false);
        namaPoli.setHighlighter(null);
        panel1.add(namaPoli);
        namaPoli.setBounds(310, 190, 260, 30);

        kodePoli.setEditable(false);
        kodePoli.setHighlighter(null);
        panel1.add(kodePoli);
        kodePoli.setBounds(230, 190, 75, 30);

        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(LabelPoli);
        LabelPoli.setBounds(75, 190, 150, 30);

        LabelKelas.setText("Kelas :");
        panel1.add(LabelKelas);
        LabelKelas.setBounds(75, 250, 150, 30);

        kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        kelas.setSelectedIndex(2);
        panel1.add(kelas);
        kelas.setBounds(230, 250, 150, 30);

        jLabel8.setText("Data Pasien :");
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel8);
        jLabel8.setBounds(75, 10, 150, 30);

        tglLahir.setEditable(false);
        tglLahir.setHighlighter(null);
        panel1.add(tglLahir);
        tglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setText("L / P :");
        panel1.add(jLabel18);
        jLabel18.setBounds(910, 40, 35, 30);

        jk.setEditable(false);
        jk.setHighlighter(null);
        panel1.add(jk);
        jk.setBounds(950, 40, 80, 30);

        jLabel24.setText("Jenis Peserta :");
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel24);
        jLabel24.setBounds(625, 40, 100, 30);

        jenisPeserta.setEditable(false);
        jenisPeserta.setHighlighter(null);
        panel1.add(jenisPeserta);
        jenisPeserta.setBounds(730, 40, 173, 30);

        jLabel25.setText("Status :");
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel25);
        jLabel25.setBounds(365, 40, 50, 30);

        statusPeserta.setEditable(false);
        statusPeserta.setHighlighter(null);
        panel1.add(statusPeserta);
        statusPeserta.setBounds(420, 40, 150, 30);

        jLabel27.setText("Asal Rujukan :");
        panel1.add(jLabel27);
        jLabel27.setBounds(625, 130, 100, 30);

        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        panel1.add(asalRujukan);
        asalRujukan.setBounds(730, 130, 170, 30);

        noTelp.setHighlighter(null);
        noTelp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                noTelpFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                noTelpFocusLost(evt);
            }
        });
        noTelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                noTelpMouseClicked(evt);
            }
        });
        panel1.add(noTelp);
        noTelp.setBounds(730, 250, 170, 30);

        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        panel1.add(katarak);
        katarak.setBounds(730, 220, 170, 30);

        jLabel37.setText("Katarak :");
        panel1.add(jLabel37);
        jLabel37.setBounds(625, 220, 100, 30);

        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(LabelPoli2);
        LabelPoli2.setBounds(75, 220, 150, 30);

        kodeDPJP.setEditable(false);
        kodeDPJP.setHighlighter(null);
        panel1.add(kodeDPJP);
        kodeDPJP.setBounds(230, 220, 75, 30);

        namaDPJP.setEditable(false);
        namaDPJP.setHighlighter(null);
        panel1.add(namaDPJP);
        namaDPJP.setBounds(310, 220, 260, 30);

        cariDPJP.setBackground(new java.awt.Color(238, 238, 255));
        cariDPJP.setBorder(null);
        cariDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDPJP.setMnemonic('X');
        cariDPJP.setToolTipText("Alt+X");
        cariDPJP.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        cariDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDPJPActionPerformed(evt);
            }
        });
        panel1.add(cariDPJP);
        cariDPJP.setBounds(575, 220, 40, 30);

        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel12);
        jLabel12.setBounds(75, 40, 150, 30);

        jLabel6.setText("NIK :");
        panel1.add(jLabel6);
        jLabel6.setBounds(625, 70, 100, 30);

        noSKDP.setEditable(false);
        noSKDP.setHighlighter(null);
        panel1.add(noSKDP);
        noSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setText("No. Rujukan :");
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel26);
        jLabel26.setBounds(75, 100, 150, 30);

        nik.setEditable(false);
        nik.setHighlighter(null);
        panel1.add(nik);
        nik.setBounds(730, 70, 300, 30);

        jLabel7.setText("No. Peserta :");
        panel1.add(jLabel7);
        jLabel7.setBounds(625, 100, 100, 30);

        cariPoli.setBackground(new java.awt.Color(238, 238, 255));
        cariPoli.setBorder(null);
        cariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariPoli.setMnemonic('X');
        cariPoli.setToolTipText("Alt+X");
        cariPoli.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        cariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariPoliActionPerformed(evt);
            }
        });
        panel1.add(cariPoli);
        cariPoli.setBounds(575, 190, 40, 30);

        cariDiagnosa.setBackground(new java.awt.Color(238, 238, 255));
        cariDiagnosa.setBorder(null);
        cariDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDiagnosa.setMnemonic('X');
        cariDiagnosa.setToolTipText("Alt+X");
        cariDiagnosa.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        cariDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDiagnosaActionPerformed(evt);
            }
        });
        panel1.add(cariDiagnosa);
        cariDiagnosa.setBounds(575, 160, 40, 30);

        cariNoRujukan.setBackground(new java.awt.Color(238, 238, 255));
        cariNoRujukan.setBorder(null);
        cariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariNoRujukan.setMnemonic('X');
        cariNoRujukan.setToolTipText("Alt+X");
        cariNoRujukan.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        cariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariNoRujukanActionPerformed(evt);
            }
        });
        panel1.add(cariNoRujukan);
        cariNoRujukan.setBounds(575, 100, 40, 30);

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
        panelNumpad1.setTextBox(noTelp);
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

        jenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        jenisPelayanan.setSelectedIndex(1);
        jenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jenisPelayananItemStateChanged(evt);
            }
        });
        form.add(jenisPelayanan);
        jenisPelayanan.setBounds(230, 10, 150, 30);

        jLabel55.setText("Laka Lantas :");
        form.add(jLabel55);
        jLabel55.setBounds(625, 10, 100, 30);

        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        form.add(lakaLantas);
        lakaLantas.setBounds(730, 10, 170, 30);

        tglKLL.setEditable(false);
        tglKLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglKLL.setEnabled(false);
        tglKLL.setOpaque(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        form.add(tglKLL);
        tglKLL.setBounds(730, 40, 170, 30);

        jLabel38.setText("Tgl. KLL :");
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel38);
        jLabel38.setBounds(625, 40, 100, 30);

        tujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        tujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tujuanKunjunganItemStateChanged(evt);
            }
        });
        form.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(230, 40, 340, 30);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel42);
        jLabel42.setBounds(75, 40, 150, 30);

        jLabel43.setText("Flag Prosedur :");
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel43);
        jLabel43.setBounds(75, 70, 150, 30);

        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        form.add(flagProsedur);
        flagProsedur.setBounds(230, 70, 340, 30);

        jLabel36.setText("Keterangan :");
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel36);
        jLabel36.setBounds(625, 70, 100, 30);

        keterangan.setEditable(false);
        keterangan.setHighlighter(null);
        form.add(keterangan);
        keterangan.setBounds(730, 70, 300, 30);

        noSEPSuplesi.setEditable(false);
        noSEPSuplesi.setHighlighter(null);
        form.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(890, 100, 140, 30);

        jLabel41.setText("No. SEP :");
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel41);
        jLabel41.setBounds(830, 100, 55, 30);

        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                suplesiItemStateChanged(evt);
            }
        });
        form.add(suplesi);
        suplesi.setBounds(730, 100, 95, 30);

        jLabel40.setText("Suplesi :");
        form.add(jLabel40);
        jLabel40.setBounds(625, 100, 100, 30);

        penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        penunjang.setEnabled(false);
        form.add(penunjang);
        penunjang.setBounds(230, 100, 340, 30);

        jLabel44.setText("Penunjang :");
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel44);
        jLabel44.setBounds(75, 100, 150, 30);

        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel45);
        jLabel45.setBounds(75, 130, 150, 30);

        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        form.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(230, 130, 340, 30);

        LabelPoli3.setText("Propinsi KLL :");
        form.add(LabelPoli3);
        LabelPoli3.setBounds(625, 130, 100, 30);

        kdPropKLL.setEditable(false);
        kdPropKLL.setHighlighter(null);
        form.add(kdPropKLL);
        kdPropKLL.setBounds(730, 130, 75, 30);

        nmPropKLL.setEditable(false);
        nmPropKLL.setHighlighter(null);
        form.add(nmPropKLL);
        nmPropKLL.setBounds(810, 130, 220, 30);

        nmKabKLL.setEditable(false);
        nmKabKLL.setHighlighter(null);
        form.add(nmKabKLL);
        nmKabKLL.setBounds(810, 160, 220, 30);

        kdKabKLL.setEditable(false);
        kdKabKLL.setHighlighter(null);
        form.add(kdKabKLL);
        kdKabKLL.setBounds(730, 160, 75, 30);

        LabelPoli4.setText("Kabupaten KLL :");
        form.add(LabelPoli4);
        LabelPoli4.setBounds(625, 160, 100, 30);

        namaDPJPLayanan.setEditable(false);
        namaDPJPLayanan.setHighlighter(null);
        form.add(namaDPJPLayanan);
        namaDPJPLayanan.setBounds(310, 160, 260, 30);

        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(LabelPoli7);
        LabelPoli7.setBounds(75, 160, 150, 30);

        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel9);
        jLabel9.setBounds(75, 190, 150, 30);

        kodePPK.setEditable(false);
        kodePPK.setHighlighter(null);
        form.add(kodePPK);
        kodePPK.setBounds(230, 190, 75, 30);

        namaPPK.setEditable(false);
        namaPPK.setHighlighter(null);
        form.add(namaPPK);
        namaPPK.setBounds(310, 190, 260, 30);

        LabelPoli5.setText("Kecamatan KLL :");
        form.add(LabelPoli5);
        LabelPoli5.setBounds(625, 190, 100, 30);

        kdKecKLL.setEditable(false);
        kdKecKLL.setHighlighter(null);
        form.add(kdKecKLL);
        kdKecKLL.setBounds(730, 190, 75, 30);

        nmKecKLL.setEditable(false);
        nmKecKLL.setHighlighter(null);
        form.add(nmKecKLL);
        nmKecKLL.setBounds(810, 190, 220, 30);

        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        catatan.setHighlighter(null);
        form.add(catatan);
        catatan.setBounds(730, 220, 300, 30);

        jLabel14.setText("Catatan :");
        form.add(jLabel14);
        jLabel14.setBounds(625, 220, 100, 30);

        barcode.setText("3");
        barcode.setHighlighter(null);
        form.add(barcode);
        barcode.setBounds(230, 220, 50, 30);

        jLabel15.setText("Jumlah Barcode :");
        form.add(jLabel15);
        jLabel15.setBounds(75, 220, 150, 30);

        kodeDPJPLayanan.setEditable(false);
        kodeDPJPLayanan.setHighlighter(null);
        form.add(kodeDPJPLayanan);
        kodeDPJPLayanan.setBounds(230, 160, 75, 30);

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

        panel2.add(form, java.awt.BorderLayout.CENTER);

        ChkInput.setForeground(new java.awt.Color(150, 155, 159));
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 30));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
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

    private void cariDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDPJPActionPerformed
        dokter.setSize(getContentPane().getSize());
        dokter.setLocationRelativeTo(getContentPane());
        dokter.carinamadokter(kodePoli.getText(), namaPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_cariDPJPActionPerformed

    private void tujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tujuanKunjunganItemStateChanged
        if (tujuanKunjungan.getSelectedIndex() == 0) {
            flagProsedur.setEnabled(false);
            flagProsedur.setSelectedIndex(0);
            penunjang.setEnabled(false);
            penunjang.setSelectedIndex(0);
            asesmenPelayanan.setEnabled(true);
        } else {
            if (tujuanKunjungan.getSelectedIndex() == 1) {
                asesmenPelayanan.setSelectedIndex(0);
                asesmenPelayanan.setEnabled(false);
            } else {
                asesmenPelayanan.setEnabled(true);
            }
            if (flagProsedur.getSelectedIndex() == 0) {
                flagProsedur.setSelectedIndex(2);
            }
            flagProsedur.setEnabled(true);
            if (penunjang.getSelectedIndex() == 0) {
                penunjang.setSelectedIndex(10);
            }
            penunjang.setEnabled(true);
        }
    }//GEN-LAST:event_tujuanKunjunganItemStateChanged

    private void jenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenisPelayananItemStateChanged
        if (jenisPelayanan.getSelectedIndex() == 0) {
            kodePoli.setText("");
            namaPoli.setText("");
            LabelPoli.setVisible(false);
            kodePoli.setVisible(false);
            namaPoli.setVisible(false);
            cariPoli.setEnabled(false);
            kodeDPJPLayanan.setText("");
            namaDPJPLayanan.setText("");
        } else if (jenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            kodePoli.setVisible(true);
            namaPoli.setVisible(true);
            cariPoli.setEnabled(true);
        }
    }//GEN-LAST:event_jenisPelayananItemStateChanged

    private void cariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariPoliActionPerformed
        poli.setSize(getContentPane().getSize());
        poli.setLocationRelativeTo(getContentPane());
        poli.setVisible(true);
    }//GEN-LAST:event_cariPoliActionPerformed

    private void cariDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDiagnosaActionPerformed
        penyakit.setSize(getContentPane().getSize());
        penyakit.setLocationRelativeTo(getContentPane());
        penyakit.setVisible(true);
    }//GEN-LAST:event_cariDiagnosaActionPerformed

    private void cariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariNoRujukanActionPerformed
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            cariRujukan.setSize(getContentPane().getSize());
            cariRujukan.setLocationRelativeTo(getContentPane());
            cariRujukan.tampil(noPeserta.getText(), namaPasien.getText());
            cariRujukan.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_cariNoRujukanActionPerformed

    private void btnRiwayatPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        riwayatPelayanan.setSize(getContentPane().getSize());
        riwayatPelayanan.setLocationRelativeTo(getContentPane());
        riwayatPelayanan.setKartu(noPeserta.getText());
        riwayatPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPelayananActionPerformed

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
        if (namaPasien.getText().isBlank()) {
            Valid.textKosong(namaPasien, "Pasien");
        } else if (noPeserta.getText().isBlank()) {
            Valid.textKosong(noPeserta, "Nomor Kartu");
        } else if (kodePPKRujukan.getText().isBlank() || namaPPKRujukan.getText().isBlank()) {
            Valid.textKosong(kodePPKRujukan, "PPK Rujukan");
        } else if (kodePPK.getText().isBlank() || namaPPK.getText().isBlank()) {
            Valid.textKosong(kodePPK, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().isBlank() || namaDiagnosa.getText().isBlank()) {
            Valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().isBlank()) {
            Valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.getText().isBlank() || namaPoli.getText().isBlank())) {
            Valid.textKosong(kodePoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().isBlank()) {
            Valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDPJP.getText().isBlank() || namaDPJP.getText().isBlank()) {
            Valid.textKosong(kodeDPJP, "DPJP");
        } else if (!statusfinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, current_date())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(null, "Silahkan lakukan validasi biometrik dahulu..!!");
        } else {
            kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
            kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());

            if (jenisPelayanan.getSelectedIndex() == 0) {
                insertSEP();
            } else if (jenisPelayanan.getSelectedIndex() == 1) {
                if (namaPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", noPeserta.getText().trim(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.SetTgl(tglSEP.getSelectedItem().toString())) >= 3) {
                        JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            kirimAntrolOnsite();
                        }
                        insertSEP();
                    }
                } else if (!namaPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", noPeserta.getText().trim(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.SetTgl(tglSEP.getSelectedItem().toString())) >= 1) {
                        JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            kirimAntrolOnsite();
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
        if (!noPeserta.getText().isBlank()) {
            aksi = "Approval";
            WindowAksi.setSize(415, 250);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (!noPeserta.getText().isBlank()) {
            aksi = "Pengajuan";
            WindowAksi.setSize(415, 250);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnPengajuanFPActionPerformed

    private void lakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lakaLantasItemStateChanged
        if (lakaLantas.getSelectedIndex() == 0) {
            tglKLL.setEnabled(false);
            keterangan.setEditable(false);
            keterangan.setText("");
        } else {
            tglKLL.setEnabled(true);
            keterangan.setEditable(true);
        }
    }//GEN-LAST:event_lakaLantasItemStateChanged

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
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try (PreparedStatement ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1")) {
                ps.setString(1, new String(userAksi.getPassword()));
                ps.setString(2, new String(passAksi.getPassword()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        switch (aksi) {
                            case "Pengajuan":
                                try {
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    System.out.print("Pengajuan FP : ");
                                    
                                    ObjectNode t_sep = mapper.createObjectNode();
                                    t_sep.put("noKartu", noPeserta.getText());
                                    t_sep.put("tglSep", Valid.getTglSmc(tglSEP));
                                    t_sep.put("jnsPelayanan", jenisPelayanan.getSelectedItem().toString().substring(0, 1));
                                    t_sep.put("jnsPengajuan", "2");
                                    t_sep.put("keterangan", "Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                    t_sep.put("user", "NoRM:" + noRM.getText());
                                    
                                    ObjectNode request = mapper.createObjectNode();
                                    request.set("t_sep", t_sep);
                                    
                                    ObjectNode root = mapper.createObjectNode();
                                    root.set("request", request);

                                    requestEntity = new HttpEntity(mapper.writeValueAsString(root), headers);

                                    JsonNode metadata = mapper.readTree(api.getRest().exchange(koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP", HttpMethod.POST, requestEntity, String.class).getBody()).path("metaData");
                                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                    
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notifikasi Bridging : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                                break;
                            case "Approval":
                                try {
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    System.out.print("Approval FP : ");
                                    
                                    ObjectNode t_sep = mapper.createObjectNode();
                                    t_sep.put("noKartu", noPeserta.getText());
                                    t_sep.put("tglSep", Valid.getTglSmc(tglSEP));
                                    t_sep.put("jnsPelayanan", jenisPelayanan.getSelectedItem().toString().substring(0, 1));
                                    t_sep.put("jnsPengajuan", "2");
                                    t_sep.put("keterangan", "Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri");
                                    t_sep.put("user", "NoRM:" + noRM.getText());

                                    ObjectNode request = mapper.createObjectNode();
                                    request.set("t_sep", t_sep);

                                    ObjectNode root = mapper.createObjectNode();
                                    root.set("request", request);

                                    requestEntity = new HttpEntity(mapper.writeValueAsString(root), headers);
                                    JsonNode metadata = mapper.readTree(api.getRest().exchange(koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP", HttpMethod.POST, requestEntity, String.class).getBody()).path("metadata");
                                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                    
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Approval Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notifikasi Bridging : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        resetAksi();
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

    private void noTelpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusGained
        ChkInput.setSelected(false);
        panelNumpad1.setVisible(true);
        isForm();
    }//GEN-LAST:event_noTelpFocusGained

    private void noTelpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusLost
        panelNumpad1.setVisible(false);
    }//GEN-LAST:event_noTelpFocusLost

    private void noTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noTelpMouseClicked
        if (ChkInput.isSelected()) {
            ChkInput.setSelected(false);
            panelNumpad1.setVisible(true);
            isForm();
        }
    }//GEN-LAST:event_noTelpMouseClicked

    private void suplesiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_suplesiItemStateChanged
        if (suplesi.getSelectedIndex() == 1) {
            noSEPSuplesi.setEditable(true);
        } else {
            noSEPSuplesi.setEditable(false);
            noSEPSuplesi.setText("");
        }
    }//GEN-LAST:event_suplesiItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.PaneToggle ChkInput;
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.Dialog WindowAksi;
    private widget.ComboBox asalRujukan;
    private widget.ComboBox asesmenPelayanan;
    private widget.TextField barcode;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnApprovalFP;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private widget.Button btnKeluar;
    private widget.Button btnPengajuanFP;
    private widget.Button btnRiwayatPelayanan;
    private widget.Button btnSimpan;
    private widget.Button cariDPJP;
    private widget.Button cariDiagnosa;
    private widget.Button cariNoRujukan;
    private widget.Button cariPoli;
    private widget.TextField catatan;
    private widget.ComboBox flagProsedur;
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
    private widget.ComboBox jenisPelayanan;
    private widget.TextField jenisPeserta;
    private widget.TextField jk;
    private widget.Label judulAksi;
    private widget.ComboBox katarak;
    private widget.TextField kdKabKLL;
    private widget.TextField kdKecKLL;
    private widget.TextField kdPropKLL;
    private widget.ComboBox kelas;
    private widget.TextField keterangan;
    private widget.TextField kodeDPJP;
    private widget.TextField kodeDPJPLayanan;
    private widget.TextField kodeDiagnosa;
    private widget.TextField kodePPK;
    private widget.TextField kodePPKRujukan;
    private widget.TextField kodePoli;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label4;
    private widget.ComboBox lakaLantas;
    private widget.TextField namaDPJP;
    private widget.TextField namaDPJPLayanan;
    private widget.TextField namaDiagnosa;
    private widget.TextField namaPPK;
    private widget.TextField namaPPKRujukan;
    private widget.TextField namaPasien;
    private widget.TextField namaPoli;
    private widget.TextField nik;
    private widget.TextField nmKabKLL;
    private widget.TextField nmKecKLL;
    private widget.TextField nmPropKLL;
    private widget.TextField noPeserta;
    private widget.TextField noRM;
    private widget.TextField noRujukan;
    private widget.TextField noSEPSuplesi;
    private widget.TextField noSKDP;
    private widget.TextField noTelp;
    private widget.Panel panel1;
    private widget.Panel panel2;
    private widget.Panel panelAtas;
    private widget.Panel panelBawah;
    private widget.Panel panelBawahAksi;
    private widget.Numpad panelNumpad1;
    private widget.Panel panelTengah;
    private widget.Panel panelTengahAksi;
    private widget.PasswordField passAksi;
    private widget.ComboBox penunjang;
    private widget.TextField statusPeserta;
    private widget.ComboBox suplesi;
    private widget.Tanggal tglKLL;
    private widget.TextField tglLahir;
    private widget.Tanggal tglRujukan;
    private widget.Tanggal tglSEP;
    private widget.ComboBox tujuanKunjungan;
    private widget.PasswordField userAksi;
    // End of variables declaration//GEN-END:variables

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", norawat);
        param.put("parameter", noSEP);
        param.put("namars", kodePPK.getText());
        param.put("kotars", Sequel.cariIsiSmc("select setting.kabupaten from setting limit 1"));

        if (jenisPelayanan.getSelectedIndex() == 0) {
            Valid.printReport("rptBridgingSEPAPM1.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            Valid.MyReport("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            Valid.printReport("rptBridgingSEPAPM2.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            Valid.MyReport("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }

        Valid.printReport("rptBarcodeRawatAPM.jasper", koneksiDB.PRINTER_BARCODE(), "::[ Barcode Perawatan ]::", Integer.parseInt(barcode.getText().trim()), param);
        Valid.MyReport("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param);
    }

    private void insertSEP() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            tglkll = "0000-00-00";
            if (lakaLantas.getSelectedIndex() > 0) {
                tglkll = Valid.getTglSmc(tglKLL);
            }

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            
            requestJson = "{" +
                "\"request\":{" +
                "\"t_sep\":{" +
                "\"noKartu\":\"" + noPeserta.getText() + "\"," +
                "\"tglSep\":\"" + Valid.getTglSmc(tglSEP) + "\"," +
                "\"ppkPelayanan\":\"" + kodePPK.getText() + "\"," +
                "\"jnsPelayanan\":\"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"klsRawat\":{" +
                "\"klsRawatHak\":\"" + kelas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"klsRawatNaik\":\"\"," +
                "\"pembiayaan\":\"\"," +
                "\"penanggungJawab\":\"\"" +
                "}," +
                "\"noMR\":\"" + noRM.getText() + "\"," +
                "\"rujukan\": {" +
                "\"asalRujukan\":\"" + asalRujukan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"tglRujukan\":\"" + Valid.getTglSmc(tglRujukan) + "\"," +
                "\"noRujukan\":\"" + noRujukan.getText() + "\"," +
                "\"ppkRujukan\":\"" + kodePPKRujukan.getText() + "\"" +
                "}," +
                "\"catatan\":\"" + catatan.getText() + "\"," +
                "\"diagAwal\":\"" + kodeDiagnosa.getText() + "\"," +
                "\"poli\": {" +
                "\"tujuan\": \"" + kodePoli.getText() + "\"," +
                "\"eksekutif\": \"0\"" +
                "}," +
                "\"cob\": {" +
                "\"cob\": \"0\"" +
                "}," +
                "\"katarak\": {" +
                "\"katarak\": \"" + katarak.getSelectedItem().toString().substring(0, 1) + "\"" +
                "}," +
                "\"jaminan\": {" +
                "\"lakaLantas\":\"" + lakaLantas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"penjamin\": {" +
                "\"tglKejadian\": \"" + tglkll.replaceAll("0000-00-00", "") + "\"," +
                "\"keterangan\": \"" + keterangan.getText() + "\"," +
                "\"suplesi\": {" +
                "\"suplesi\": \"" + suplesi.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"noSepSuplesi\": \"" + noSEPSuplesi.getText() + "\"," +
                "\"lokasiLaka\": {" +
                "\"kdPropinsi\": \"" + kdPropKLL.getText() + "\"," +
                "\"kdKabupaten\": \"" + kdKabKLL.getText() + "\"," +
                "\"kdKecamatan\": \"" + kdKecKLL.getText() + "\"" +
                "}" +
                "}" +
                "}" +
                "}," +
                "\"tujuanKunj\": \"" + tujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"flagProcedure\": \"" + (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"kdPenunjang\": \"" + (penunjang.getSelectedIndex() > 0 ? penunjang.getSelectedIndex() + "" : "") + "\"," +
                "\"assesmentPel\": \"" + (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"skdp\": {" +
                "\"noSurat\": \"" + noSKDP.getText() + "\"," +
                "\"kodeDPJP\": \"" + kodeDPJP.getText() + "\"" +
                "}," +
                "\"dpjpLayan\": \"" + (kodeDPJPLayanan.getText().equals("") ? "" : kodeDPJPLayanan.getText()) + "\"," +
                "\"noTelp\": \"" + noTelp.getText() + "\"," +
                "\"user\":\"" + noPeserta.getText() + "\"" +
                "}" +
                "}" +
                "}";
            
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert", HttpMethod.POST, requestEntity, String.class).getBody());
            metadata = root.path("metaData");
            
            System.out.println("Insert SEP : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
            
            if (metadata.path("code").asText().equals("200")) {
                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                String noSEP = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep").asText();
                Sequel.mengupdateSmc("pasien",
                    "no_tlp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
                    "no_rkm_medis = ?",
                    noTelp.getText(), noRM.getText()
                );
                Sequel.menyimpanSmc("bridging_sep", null,
                    noSEP,
                    norawat,
                    Valid.getTglSmc(tglSEP),
                    Valid.getTglSmc(tglRujukan),
                    noRujukan.getText(),
                    kodePPKRujukan.getText(),
                    namaPPKRujukan.getText(),
                    kodePPK.getText(),
                    namaPPK.getText(),
                    jenisPelayanan.getSelectedItem().toString().substring(0, 1),
                    catatan.getText(),
                    kodeDiagnosa.getText(),
                    namaDiagnosa.getText(),
                    kodePoli.getText(),
                    namaPoli.getText(),
                    kelas.getSelectedItem().toString().substring(0, 1),
                    "",
                    "",
                    "",
                    lakaLantas.getSelectedItem().toString().substring(0, 1),
                    noRM.getText(),
                    noRM.getText(),
                    namaPasien.getText(),
                    tglLahir.getText(),
                    jenisPeserta.getText(),
                    jk.getText(),
                    noPeserta.getText(),
                    "0000-00-00 00:00:00",
                    asalRujukan.getSelectedItem().toString(),
                    "0. Tidak",
                    "0. Tidak",
                    noTelp.getText(),
                    katarak.getSelectedItem().toString(),
                    tglkll,
                    keterangan.getText(),
                    suplesi.getSelectedItem().toString(),
                    noSEPSuplesi.getText(),
                    kdPropKLL.getText(),
                    nmPropKLL.getText(),
                    kdKabKLL.getText(),
                    nmKabKLL.getText(),
                    kdKecKLL.getText(),
                    nmKecKLL.getText(),
                    noSKDP.getText(),
                    kodeDPJP.getText(),
                    namaDPJP.getText(),
                    tujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                    (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                    (penunjang.getSelectedIndex() > 0 ? String.valueOf(penunjang.getSelectedIndex()) : ""),
                    (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : ""),
                    kodeDPJPLayanan.getText(),
                    namaDPJPLayanan.getText()
                );

                if (!simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }

                if (jenisPelayanan.getSelectedIndex() == 1) {
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.getTglSmc(tglSEP), noSEP);
                }

                if (!prb.equals("")) {
                    Sequel.menyimpanSmc("bpjs_prb", null, noSEP, prb);
                    prb = "";
                }

                cetakRegistrasi(noSEP);
                emptTeks();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Gagal", JOptionPane.ERROR_MESSAGE);
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

        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No. Kartu BPJS tidak ada..!!");

            return;
        }

        try {
            URL = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noPeserta.getText() + "/TglPelayanan/" + Valid.getTglSmc(tglSEP);
            utc = String.valueOf(api.GetUTCdatetimeAsString());

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            metadata = root.path("metaData");

            System.out.println("code : " + metadata.path("code").asText());
            System.out.println("message : " + metadata.path("message").asText());

            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                if (response.path("kode").asText().equals("1")) {
                    if (response.path("status").asText().contains(Sequel.cariIsiSmc("select current_date()"))) {
                        statusfinger = true;
                    } else {
                        statusfinger = false;
                        JOptionPane.showMessageDialog(null, response.path("status").asText());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, response.path("status").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    public void tampil(String noKartu) {
        ChkInput.setSelected(false);
        isForm();
        emptTeks();
        try (PreparedStatement psjkn = koneksi.prepareStatement(
            "select referensi_mobilejkn_bpjs.*, maping_poli_bpjs.nm_poli_bpjs, maping_poli_bpjs.kd_poli_rs, maping_dokter_dpjpvclaim.nm_dokter_bpjs, " +
            "maping_dokter_dpjpvclaim.kd_dokter from referensi_mobilejkn_bpjs join maping_poli_bpjs on referensi_mobilejkn_bpjs.kodepoli = maping_poli_bpjs.kd_poli_bpjs join " +
            "maping_dokter_dpjpvclaim on referensi_mobilejkn_bpjs.kodedokter = maping_dokter_dpjpvclaim.kd_dokter_bpjs where referensi_mobilejkn_bpjs.nomorkartu = ? and " +
            "referensi_mobilejkn_bpjs.tanggalperiksa = current_date() and referensi_mobilejkn_bpjs.status in ('Belum', 'Checkin') and tanggalperiksa = current_date() " +
            "and not exists(select * from bridging_sep where bridging_sep.no_rawat = referensi_mobilejkn_bpjs.no_rawat)"
        )) {
            psjkn.setString(1, noKartu);
            try (ResultSet rsjkn = psjkn.executeQuery()) {
                if (rsjkn.next()) {
                    norawat = rsjkn.getString("no_rawat");
                    nobooking = rsjkn.getString("nobooking");
                    jeniskunjungan = rsjkn.getString("jeniskunjungan").substring(0, 1);
                    kodePoli.setText(rsjkn.getString("kodepoli"));
                    namaPoli.setText(rsjkn.getString("nm_poli_bpjs"));
                    kodepolireg = rsjkn.getString("kd_poli_rs");
                    kodeDPJP.setText(rsjkn.getString("kodedokter"));
                    namaDPJP.setText(rsjkn.getString("nm_dokter_bpjs"));
                    kodedokterreg = rsjkn.getString("kd_dokter");
                    kodeDPJPLayanan.setText(kodeDPJP.getText());
                    namaDPJPLayanan.setText(namaDPJP.getText());
                    noPeserta.setText(rsjkn.getString("nomorkartu"));
                    noRM.setText(rsjkn.getString("norm"));
                    nik.setText(rsjkn.getString("nik"));
                    noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                        noTelp.setText(rsjkn.getString("nohp"));
                    }
                    // CEK STATUS PASIEN
                    try {
                        URL = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rsjkn.getString("nomorkartu") + "/tglSEP/" + Valid.getTglSmc(tglSEP);
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
                        metadata = root.path("metaData");
                        if (metadata.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                            switch (response.path("hakKelas").path("kode").asText()) {
                                case "1":
                                    kelas.setSelectedIndex(0);
                                    break;
                                case "2":
                                    kelas.setSelectedIndex(1);
                                    break;
                                case "3":
                                    kelas.setSelectedIndex(2);
                                    break;
                                default:
                                    break;
                            }
                            prb = response.path("informasi").path("prolanisPRB").asText();
                            if (prb.contains("null")) {
                                prb = "";
                            }
                            namaPasien.setText(response.path("nama").asText());
                            noPeserta.setText(response.path("noKartu").asText());
                            jk.setText(response.path("sex").asText());
                            statusPeserta.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                            tglLahir.setText(response.path("tglLahir").asText());
                            jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                            switch (jeniskunjungan) {
                                case "1":
                                    // RUJUKAN FKTP
                                    asalRujukan.setSelectedIndex(0);
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
                                        metadata = root.path("metaData");
                                        if (metadata.path("code").asText().equals("200")) {
                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                            noRujukan.setText(response.path("noKunjungan").asText());
                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                            Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                                        } else {
                                            System.out.println("Notif : " + metadata.asText());
                                            JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                            emptTeks();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Notif : " + e);
                                        if (e.toString().contains("UnknownHostException")) {
                                            JOptionPane.showMessageDialog(null, "Koneksi ke Server BPJS terputus...!!!");
                                        }
                                        emptTeks();
                                    }
                                    break;
                                case "4":
                                    // RUJUKAN FKTL
                                    asalRujukan.setSelectedIndex(1);
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
                                        metadata = root.path("metaData");
                                        if (metadata.path("code").asText().equals("200")) {
                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                            noRujukan.setText(response.path("noKunjungan").asText());
                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                            Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                                        } else {
                                            System.out.println("Notif : " + metadata.asText());
                                            JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                            emptTeks();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Notif : " + e);
                                        if (e.toString().contains("UnknownHostException")) {
                                            JOptionPane.showMessageDialog(null, "Koneksi ke Server BPJS terputus...!!!");
                                        }
                                        emptTeks();
                                    }
                                    break;
                                case "3":
                                    // CEK JENIS KONTROL DULU
                                    try (PreparedStatement pskontrol = koneksi.prepareStatement(
                                        "select bridging_surat_kontrol_bpjs.*, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat " +
                                            "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
                                    )) {
                                        pskontrol.setString(1, rsjkn.getString("nomorreferensi"));
                                        try (ResultSet rskontrol = pskontrol.executeQuery()) {
                                            if (rskontrol.next()) {
                                                if (!rskontrol.getString("tgl_rencana").equals(Valid.getTglSmc(tglSEP))) {
                                                    updateSuratKontrol(rskontrol.getString("no_surat"), rskontrol.getString("no_sep"), rsjkn.getString("nomorkartu"), Valid.getTglSmc(tglSEP),
                                                        rsjkn.getString("kodedokter"), rsjkn.getString("nm_dokter_bpjs"), rsjkn.getString("kodepoli"), rsjkn.getString("nm_poli_bpjs")
                                                    );
                                                }
                                                if (rskontrol.getString("jnspelayanan").equals("1")) {
                                                    // KONTROL POST RANAP
                                                    kodeDiagnosa.setText("Z09.8");
                                                    namaDiagnosa.setText("Follow-up examination after other treatment for other conditions");
                                                    noRujukan.setText(rskontrol.getString("no_sep"));
                                                    tujuanKunjungan.setSelectedIndex(0);
                                                    flagProsedur.setSelectedIndex(0);
                                                    penunjang.setSelectedIndex(0);
                                                    asesmenPelayanan.setSelectedIndex(0);
                                                    asalRujukan.setSelectedIndex(1);
                                                    noSKDP.setText(rskontrol.getString("no_surat"));
                                                    kodePPKRujukan.setText(Sequel.cariIsiSmc("select kode_ppk from setting"));
                                                    namaPPKRujukan.setText(Sequel.cariIsiSmc("select nama_instansi from setting"));
                                                } else {
                                                    // KONTROL POLI
                                                    try {
                                                        if (rskontrol.getString("asal_rujukan").equals("1")) {
                                                            URL = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                                            asalRujukan.setSelectedIndex(0);
                                                        } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                                            URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                                                            asalRujukan.setSelectedIndex(1);
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
                                                        metadata = root.path("metaData");
                                                        System.out.println("URL : " + URL);
                                                        if (metadata.path("code").asText().equals("200")) {
                                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                                            noRujukan.setText(response.path("noKunjungan").asText());
                                                            noSKDP.setText(rskontrol.getString("no_surat"));
                                                            tujuanKunjungan.setSelectedIndex(2);
                                                            flagProsedur.setSelectedIndex(0);
                                                            penunjang.setSelectedIndex(0);
                                                            asesmenPelayanan.setSelectedIndex(5);
                                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                                            Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                                                        } else {
                                                            System.out.println("Notif : " + metadata.asText());
                                                            JOptionPane.showMessageDialog(null, metadata.path("message").asText());
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
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "Maaf, antrian JKN tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    emptTeks();
                                    break;
                            }
                        } else {
                            System.out.println("Notif : " + metadata.asText());
                            JOptionPane.showMessageDialog(null, metadata.path("message").asText());
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

    public boolean kirimAntrolOnsite() {
        if (!ADDANTRIANAPIMOBILEJKN) {
            return true;
        }
        boolean sukses = true;
        if (Sequel.cariExistsSmc("select * from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", nobooking)) {
            Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "referensi_mobilejkn_bpjs.validasi = now(), referensi_mobilejkn_bpjs.status = 'Checkin'", "referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", nobooking);
            Sequel.mengupdateSmc("reg_periksa", "reg_periksa.jam_reg = current_time()", "reg_periksa.no_rawat = ? and stts != 'Batal'", norawat);
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select referensi_mobilejkn_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, poliklinik.nm_poli, dokter.nm_dokter from referensi_mobilejkn_bpjs " +
            "join reg_periksa on referensi_mobilejkn_bpjs.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
            "join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli join dokter on reg_periksa.kd_dokter = dokter.kd_dokter " +
            "where referensi_mobilejkn_bpjs.statuskirim = 'Belum' and referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn.status = 'Checkin'"
        )) {
            ps.setString(1, nobooking);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                            "\"nohp\": \"" + noTelp.getText().trim() + "\"," +
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
                        System.out.print("addantrean " + rs.getString("nobooking") + " : ");
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(koneksiDB.URLAPIMOBILEJKN() + "/antrean/add", HttpMethod.POST, requestEntity, String.class).getBody());
                        metadata = root.path("metadata");
                        Sequel.logTaskid(norawat, rs.getString("nobooking"), "Onsite", "addantrean", requestJson, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                        if (metadata.path("code").asText().equals("200") || metadata.path("code").asText().equals("208") || metadata.path("message").asText().equals("Ok")) {
                            Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "statuskirim = 'Sudah'", "nobooking = ?", rs.getString("nobooking"));
                        } else {
                            sukses = false;
                            JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                        }
                        System.out.println("respon WS BPJS : " + metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                    } catch (HttpClientErrorException e) {
                        sukses = false;
                        System.out.println("Notif : " + e.getMessage());
                        Sequel.logTaskid(norawat, rs.getString("nobooking"), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    } catch (HttpServerErrorException e) {
                        sukses = false;
                        System.out.println("Notif : " + e.getMessage());
                        Sequel.logTaskid(norawat, rs.getString("nobooking"), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    } catch (Exception e) {
                        sukses = false;
                        System.out.println("Notif : " + e);
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas");
                    }
                }
            }
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas");
        }
        return sukses;
    }

    private void emptTeks() {
        noRM.setText("");
        namaPasien.setText("");
        tglLahir.setText("");
        statusPeserta.setText("");
        noSKDP.setText("");
        noRujukan.setText("");
        kodePPKRujukan.setText("");
        namaPPKRujukan.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        kodePoli.setText("");
        namaPoli.setText("");
        kodeDPJP.setText("");
        namaDPJP.setText("");
        kelas.setSelectedIndex(2);
        jenisPeserta.setText("");
        jk.setText("");
        nik.setText("");
        noPeserta.setText("");
        asalRujukan.setSelectedIndex(0);
        tglRujukan.setDate(new Date());
        tglSEP.setDate(new Date());
        katarak.setSelectedIndex(0);
        noTelp.setText("");
        jenisPelayanan.setSelectedIndex(1);
        tujuanKunjungan.setSelectedIndex(0);
        penunjang.setSelectedIndex(0);
        asesmenPelayanan.setSelectedIndex(0);
        kodeDPJPLayanan.setText("");
        namaDPJPLayanan.setText("");
        barcode.setText(String.valueOf(koneksiDB.PRINTJUMLAHBARCODE()));
        lakaLantas.setSelectedIndex(0);
        tglKLL.setDate(new Date());
        keterangan.setText("");
        suplesi.setSelectedIndex(0);
        noSEPSuplesi.setText("");
        kdPropKLL.setText("");
        nmPropKLL.setText("");
        kdKabKLL.setText("");
        nmKabKLL.setText("");
        kdKecKLL.setText("");
        nmKecKLL.setText("");
        catatan.setText("Anjungan Pasien Mandiri " + namaPPK.getText());
        resetAksi();
    }

    private void bukaAplikasiFingerprint() {
        if (noPeserta.getText().isBlank()) {
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

                ss = new StringSelection(noPeserta.getText().trim());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            } else {
                Runtime.getRuntime().exec("\"" + koneksiDB.URLAPLIKASIFINGERPRINTBPJS() + "\"");
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

                ss = new StringSelection(koneksiDB.PASSWORDFINGERPRINTBPJS());
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(1000);

                ss = new StringSelection(noPeserta.getText().trim());
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
        if (nik.getText().isBlank()) {
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

                ss = new StringSelection(nik.getText());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
            } else {
                Runtime.getRuntime().exec("\"" + koneksiDB.URLAPLIKASIFRISTABPJS() + "\"");
                Thread.sleep(7000);

                ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1500);

                ss = new StringSelection(koneksiDB.PASSWORDFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                r.keyPress(KeyEvent.VK_SPACE);
                r.keyRelease(KeyEvent.VK_SPACE);
                Thread.sleep(5000);

                r.mouseMove(d.width / 2, d.height / 2);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                ss = new StringSelection(nik.getText());
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
            metadata = root.path("metaData");
            System.out.println("code : " + metadata.path("code").asText());
            System.out.println("message : " + metadata.path("message").asText());
            if (metadata.path("code").asText().equals("200")) {
                Sequel.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?", "no_surat = ?",
                    tanggalPeriksa, kodeDPJP, namaDPJP, kodePoli, namaPoli, noSKDP
                );
            } else {
                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private boolean simpanRujukan() {
        int next = 0, retries = 5;
        boolean sukses = false;

        do {
            String noRujukMasuk = Sequel.cariIsiSmc(
                "select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(" +
                "right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where " +
                "rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                Valid.getTglSmc(tglSEP), Valid.getTglSmc(tglSEP)
            );

            System.out.print("Mencoba memproses rujukan masuk pasien dengan no. surat [" + noRujukMasuk + "]: ");

            sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
                norawat, namaPPKRujukan.getText(), "-", noRujukan.getText(),
                "0", namaPPKRujukan.getText(), kodeDiagnosa.getText(), "-", "-", noRujukMasuk
            );

            System.out.println(sukses ? "Sukses!" : "Gagal!");
        } while (next++ < retries && !sukses);

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
