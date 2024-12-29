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
import fungsi.BatasInput;
import fungsi.KoneksiDB;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author Kode
 */
public class DlgRegistrasiSEPPertama extends javax.swing.JDialog {

    private final Connection koneksi = KoneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final ApiBPJS api = new ApiBPJS();
    private final validasi Valid = new validasi();
    private final BPJSCekReferensiDokterDPJP dokter = new BPJSCekReferensiDokterDPJP(null, true);
    private final BPJSCekReferensiPenyakit penyakit = new BPJSCekReferensiPenyakit(null, true);
    private final DlgCariPoliBPJS poli = new DlgCariPoliBPJS(null, true);
    private final DlgCariPoli polimapping = new DlgCariPoli(null, true);
    private final DlgCariDokter doktermapping = new DlgCariDokter(null, true);
    private final BPJSCekRiwayatRujukanTerakhir rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private final BPJSCekRiwayatPelayanan historiPelayanan = new BPJSCekRiwayatPelayanan(null, true);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String kodepj = Sequel.cariIsiSmc("select password_asuransi.kd_pj from password_asuransi");
    private String norawat = "",
        noreg = "",
        alamatpasien = "",
        pjpasien = "",
        hubungankeluargapasien = "",
        biayareg = "",
        norujukmasuk = "",
        umur = "0",
        sttsumur = "Th",
        hari = "",
        statuspasien = "",
        tglkll = "0000-00-00",
        datajam = "",
        jammulai = "",
        jamselesai = "",
        requestJson = "",
        url = "",
        nosep = "",
        prb = "",
        kodedokterreg = "",
        kodepolireg = "",
        statuspoli = "Baru",
        utc = "",
        aksi = "";
    private JsonNode root, metadata, response;
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private int kuota = 0;
    private Date parsedDate;
    private boolean statusfinger = false, fingerprintAktif = false, fristaAktif = false;

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgRegistrasiSEPPertama(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();
        JumlahBarcode.setDocument(new BatasInput((byte) 3).getOnlyAngka(JumlahBarcode));

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

        KdPPK.setText(Sequel.cariIsiSmc("select setting.kode_ppk from setting"));
        NmPPK.setText(Sequel.cariIsiSmc("select setting.nama_instansi from setting"));
        JumlahBarcode.setText("3");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        WindowAksi = new javax.swing.JDialog();
        internalFrame1 = new widget.InternalFrame();
        pwUserId = new widget.PasswordBox();
        pwPass = new widget.PasswordBox();
        btnAksiKonfirmasi = new widget.Button();
        btnAksiBatal = new widget.Button();
        label1 = new widget.Label();
        label2 = new widget.Label();
        label3 = new widget.Label();
        WindowPilihValidasi = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        panelisi1 = new widget.panelisi();
        btnFrista = new widget.Button();
        btnFingerprint = new widget.Button();
        BtnBatalValidasi = new widget.Button();
        jPanel1 = new widget.Panel();
        jPanel2 = new widget.Panel();
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
        jLabel56 = new widget.Label();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        NoSKDP = new widget.TextBox();
        jLabel26 = new widget.Label();
        NIK = new widget.TextBox();
        jLabel7 = new widget.Label();
        btnDPJPLayanan1 = new widget.Button();
        btnDiagnosaAwal = new widget.Button();
        btnDiagnosaAwal1 = new widget.Button();
        btnDiagnosaAwal2 = new widget.Button();
        KodeDokterTerapi = new widget.TextBox();
        KdPoliTerapi = new widget.TextBox();
        NmPoliTerapi = new widget.TextBox();
        NmDokterTerapi = new widget.TextBox();
        btnDokterTerapi = new widget.Button();
        btnPoliTerapi = new widget.Button();
        LabelPoli7 = new widget.Label();
        btnDiagnosaAwal3 = new widget.Button();
        btnDiagnosaAwal4 = new widget.Button();
        jLabel15 = new widget.Label();
        JumlahBarcode = new widget.TextBox();
        jPanel3 = new javax.swing.JPanel();
        btnSimpan = new widget.Button();
        btnValidasi = new widget.Button();
        btnKeluar = new widget.Button();

        WindowAksi.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowAksi.setModal(true);
        WindowAksi.setUndecorated(true);
        WindowAksi.setResizable(false);

        internalFrame1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pwUserId.setForeground(new java.awt.Color(40, 40, 40));
        pwUserId.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        pwUserId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwUserIdKeyPressed(evt);
            }
        });
        internalFrame1.add(pwUserId, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 230, 23));

        pwPass.setForeground(new java.awt.Color(40, 40, 40));
        pwPass.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        pwPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwPassKeyPressed(evt);
            }
        });
        internalFrame1.add(pwPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 230, 23));

        btnAksiKonfirmasi.setText("Konfirmasi");
        btnAksiKonfirmasi.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        btnAksiKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiKonfirmasiActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiKonfirmasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, -1, -1));

        btnAksiBatal.setText("Batal");
        btnAksiBatal.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        btnAksiBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiBatalActionPerformed(evt);
            }
        });
        internalFrame1.add(btnAksiBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        label1.setText("User ID :");
        label1.setFocusable(false);
        label1.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        internalFrame1.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 120, 23));

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        internalFrame1.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 120, 23));

        label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label3.setText("Konfirmasi Aksi");
        label3.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        internalFrame1.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 400, -1));

        WindowAksi.getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        WindowPilihValidasi.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        internalFrame2.setBackground(new java.awt.Color(238, 238, 255));
        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "PILIH METODE", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        internalFrame2.setForeground(new java.awt.Color(0, 131, 62));
        internalFrame2.setLayout(new java.awt.BorderLayout());

        java.awt.GridBagLayout panelisi1Layout = new java.awt.GridBagLayout();
        panelisi1Layout.rowWeights = new double[] {3.0};
        panelisi1.setLayout(panelisi1Layout);

        btnFrista.setForeground(new java.awt.Color(0, 131, 62));
        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-recognition-48px.png"))); // NOI18N
        btnFrista.setMnemonic('S');
        btnFrista.setText(" FRISTA (PENGENALAN WAJAH)");
        btnFrista.setToolTipText("Alt+S");
        btnFrista.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnFrista.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFrista.setPreferredSize(new java.awt.Dimension(360, 100));
        btnFrista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFristaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panelisi1.add(btnFrista, gridBagConstraints);

        btnFingerprint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setMnemonic('S');
        btnFingerprint.setText(" FINGERPRINT (SIDIK JARI)");
        btnFingerprint.setToolTipText("Alt+S");
        btnFingerprint.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnFingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFingerprint.setPreferredSize(new java.awt.Dimension(360, 100));
        btnFingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerprintActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 2.0;
        panelisi1.add(btnFingerprint, gridBagConstraints);

        BtnBatalValidasi.setForeground(new java.awt.Color(0, 131, 62));
        BtnBatalValidasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/exit.png"))); // NOI18N
        BtnBatalValidasi.setMnemonic('S');
        BtnBatalValidasi.setText("BATAL");
        BtnBatalValidasi.setToolTipText("Alt+S");
        BtnBatalValidasi.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        BtnBatalValidasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnBatalValidasi.setPreferredSize(new java.awt.Dimension(300, 40));
        BtnBatalValidasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalValidasiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 9.0;
        panelisi1.add(BtnBatalValidasi, gridBagConstraints);

        internalFrame2.add(panelisi1, java.awt.BorderLayout.CENTER);

        WindowPilihValidasi.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "DATA ELIGIBILITAS PESERTA JKN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(543, 106));
        jPanel1.setPreferredSize(new java.awt.Dimension(543, 106));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

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
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-12-2024" }));
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
        TanggalRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-12-2024" }));
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
        KdPPK.setBounds(230, 250, 75, 30);

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
        KdPpkRujukan.setBounds(230, 130, 75, 30);

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
        KdPenyakit.setBounds(230, 160, 75, 30);

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
        KdPoli.setBounds(230, 190, 75, 30);

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
        jLabel18.setText("J.K :");
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
        TanggalKKL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-12-2024" }));
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
        KdDPJP.setBounds(230, 220, 75, 30);

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
        KdPropinsi.setBounds(730, 370, 55, 30);

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
        KdKabupaten.setBounds(730, 400, 55, 30);

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
        KdKecamatan.setBounds(730, 430, 55, 30);

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

        jLabel56.setForeground(new java.awt.Color(0, 131, 62));
        jLabel56.setText("No. Telp :");
        jLabel56.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        jPanel2.add(jLabel56);
        jLabel56.setBounds(625, 190, 100, 30);

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
        jLabel7.setText("No. Peserta :");
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

        btnDiagnosaAwal1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal1.setMnemonic('X');
        btnDiagnosaAwal1.setToolTipText("Alt+X");
        btnDiagnosaAwal1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDiagnosaAwal1);
        btnDiagnosaAwal1.setBounds(570, 100, 40, 30);

        btnDiagnosaAwal2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal2.setMnemonic('X');
        btnDiagnosaAwal2.setText("Riwayat Layanan BPJS");
        btnDiagnosaAwal2.setToolTipText("Alt+X");
        btnDiagnosaAwal2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDiagnosaAwal2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal2ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDiagnosaAwal2);
        btnDiagnosaAwal2.setBounds(1040, 150, 220, 30);

        KodeDokterTerapi.setEditable(false);
        KodeDokterTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KodeDokterTerapi.setHighlighter(null);
        jPanel2.add(KodeDokterTerapi);
        KodeDokterTerapi.setBounds(230, 520, 75, 30);

        KdPoliTerapi.setEditable(false);
        KdPoliTerapi.setBackground(new java.awt.Color(255, 255, 153));
        KdPoliTerapi.setHighlighter(null);
        jPanel2.add(KdPoliTerapi);
        KdPoliTerapi.setBounds(230, 490, 75, 30);

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

        btnDiagnosaAwal3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnDiagnosaAwal3.setMnemonic('X');
        btnDiagnosaAwal3.setText("Approval FP BPJS");
        btnDiagnosaAwal3.setToolTipText("Alt+X");
        btnDiagnosaAwal3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDiagnosaAwal3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDiagnosaAwal3);
        btnDiagnosaAwal3.setBounds(1040, 260, 190, 50);

        btnDiagnosaAwal4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnDiagnosaAwal4.setMnemonic('X');
        btnDiagnosaAwal4.setText("Pengajuan FP BPJS");
        btnDiagnosaAwal4.setToolTipText("Alt+X");
        btnDiagnosaAwal4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnDiagnosaAwal4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDiagnosaAwal4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal4ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDiagnosaAwal4);
        btnDiagnosaAwal4.setBounds(1040, 200, 190, 50);

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
        btnSimpan.setText(" KONFIRMASI");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 45));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel3.add(btnSimpan);

        btnValidasi.setForeground(new java.awt.Color(0, 131, 62));
        btnValidasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnValidasi.setMnemonic('K');
        btnValidasi.setText(" VALIDASI");
        btnValidasi.setToolTipText("Alt+K");
        btnValidasi.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnValidasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnValidasi.setPreferredSize(new java.awt.Dimension(300, 45));
        btnValidasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidasiActionPerformed(evt);
            }
        });
        jPanel3.add(btnValidasi);

        btnKeluar.setForeground(new java.awt.Color(0, 131, 62));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText(" BATAL");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKeluar.setPreferredSize(new java.awt.Dimension(300, 45));
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        jPanel3.add(btnKeluar);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(NoKartu.getText());
        if (norawat.isBlank() || TPasien.getText().isBlank()) {
            Valid.textKosong(TPasien, "Pasien");
        } else if (NoKartu.getText().isBlank()) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
        } else if (Sequel.cariExistsSmc("select * from pasien where no_rkm_medis = ?", TNoRM.getText())) {
            JOptionPane.showMessageDialog(null, "Maaf, no RM tidak sesuai");
        } else if (KdPpkRujukan.getText().isBlank() || NmPpkRujukan.getText().isBlank()) {
            Valid.textKosong(KdPpkRujukan, "PPK Rujukan");
        } else if (KdPPK.getText().isBlank() || NmPPK.getText().isBlank()) {
            Valid.textKosong(KdPPK, "PPK Pelayanan");
        } else if (KdPenyakit.getText().isBlank() || NmPenyakit.getText().isBlank()) {
            Valid.textKosong(KdPenyakit, "Diagnosa");
        } else if (Catatan.getText().isBlank()) {
            Valid.textKosong(Catatan, "Catatan");
        } else if ((JenisPelayanan.getSelectedIndex() == 1) && (KdPoli.getText().isBlank() || NmPoli.getText().isBlank())) {
            Valid.textKosong(KdPoli, "Poli Tujuan");
        } else if ((LakaLantas.getSelectedIndex() == 1) && Keterangan.getText().equals("")) {
            Valid.textKosong(Keterangan, "Keterangan");
        } else if (KdDPJP.getText().isBlank() || NmDPJP.getText().isBlank()) {
            Valid.textKosong(KdDPJP, "DPJP");
        } else if (!statusfinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, current_date())", TglLahir.getText()) >= 17 && JenisPelayanan.getSelectedIndex() != 0 && !KdPoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(null, "Maaf, Pasien belum melakukan Fingerprint");
            btnValidasiActionPerformed(null);
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
            if (Sequel.cariIntegerSmc("select count(*) from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodepolireg, kodedokterreg, kodepj) > 0) {
                JOptionPane.showMessageDialog(null, "Maaf, Telah terdaftar pemeriksaan hari ini. Mohon konfirmasi ke Bagian Admisi");
                emptTeks();
            } else {
                if (!registerPasien()) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat pendaftaran pasien!");
                    this.setCursor(Cursor.getDefaultCursor());
                    return;
                }

                if (JenisPelayanan.getSelectedIndex() == 0) {
                    insertSEP();
                } else if (JenisPelayanan.getSelectedIndex() == 1) {
                    if (NmPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", NoKartu.getText(), JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(TanggalSEP)) >= 3) {
                            JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                SimpanAntrianOnSite();
                            }
                            insertSEP();
                        }
                    } else if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", NoKartu.getText(), JenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(TanggalSEP)) >= 1) {
                            JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
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

    private void btnValidasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidasiActionPerformed
        if (NoKartu.getText().isBlank() || NIK.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, NIK / No. Peserta masih kosong...!!!");
        } else {
            WindowPilihValidasi.setSize(600, 500);
            WindowPilihValidasi.setLocationRelativeTo(null);
            WindowPilihValidasi.setVisible(true);
        }
    }//GEN-LAST:event_btnValidasiActionPerformed

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

    private void btnDiagnosaAwal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal1ActionPerformed
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
            rujukanterakhir.setLocationRelativeTo(jPanel1);
            rujukanterakhir.tampil(NoKartu.getText(), TPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnDiagnosaAwal1ActionPerformed

    private void btnDiagnosaAwal2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal2ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        historiPelayanan.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        historiPelayanan.setLocationRelativeTo(jPanel1);
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnDiagnosaAwal2ActionPerformed

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

    private void btnDiagnosaAwal3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal3ActionPerformed
        resetAksi();
        if (!NoKartu.getText().isBlank()) {
            aksi = "Approval";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnDiagnosaAwal3ActionPerformed

    private void btnDiagnosaAwal4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal4ActionPerformed
        resetAksi();
        if (!NoKartu.getText().isBlank()) {
            aksi = "Pengajuan";
            WindowAksi.setSize(400, 300);
            WindowAksi.setLocationRelativeTo(null);
            WindowAksi.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnDiagnosaAwal4ActionPerformed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        } else {
            try (PreparedStatement ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1")) {
                ps.setString(1, new String(pwUserId.getPassword()));
                ps.setString(2, new String(pwPass.getPassword()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        switch (aksi) {
                            case "Pengajuan":
                                url = KoneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                                requestJson = "{"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + NoKartu.getText() + "\","
                                    + "\"tglSep\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
                                    + "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri " + Sequel.cariIsiSmc("select nama_instansi from setting") + "\","
                                    + "\"user\": \"NoRM:" + TNoRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                System.out.println("URL : " + url);
                                System.out.println("JSON : " + requestJson);
                                try {
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                                    utc = api.getUTCDateTime();
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("code : " + metadata.path("code").asText());
                                    System.out.println("message : " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notifikasi Bridging : " + e);
                                    if (e.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                                break;
                            case "Approval":
                                url = KoneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                                requestJson = "{"
                                    + "\"request\": {"
                                    + "\"t_sep\": {"
                                    + "\"noKartu\": \"" + NoKartu.getText() + "\","
                                    + "\"tglSep\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
                                    + "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"jnsPengajuan\": \"2\","
                                    + "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri " + Sequel.cariIsiSmc("select nama_instansi from setting") + "\","
                                    + "\"user\": \"NoRM:" + TNoRM.getText() + "\""
                                    + "}"
                                    + "}"
                                    + "}";
                                System.out.println("URL : " + url);
                                System.out.println("JSON : " + requestJson);
                                try {
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                                    utc = api.getUTCDateTime();
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("code : " + metadata.path("code").asText());
                                    System.out.println("message : " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notifikasi Bridging : " + e);
                                    if (e.toString().contains("UnknownHostException")) {
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

    private void btnFingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerprintActionPerformed
        bukaAplikasiFingerprint();
        WindowPilihValidasi.setVisible(false);
    }//GEN-LAST:event_btnFingerprintActionPerformed

    private void btnFristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFristaActionPerformed
        bukaAplikasiFrista();
        WindowPilihValidasi.setVisible(false);
    }//GEN-LAST:event_btnFristaActionPerformed

    private void BtnBatalValidasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalValidasiActionPerformed
        WindowPilihValidasi.setVisible(false);
    }//GEN-LAST:event_BtnBatalValidasiActionPerformed

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
    private widget.Button BtnBatalValidasi;
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
    private widget.TextBox NoRujukan;
    private widget.TextBox NoSEPSuplesi;
    private widget.TextBox NoSKDP;
    private widget.TextBox NoTelp;
    private widget.ComboBox Penunjang;
    private widget.TextBox Status;
    private widget.ComboBox Suplesi;
    private widget.TextBox TNoRM;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalKKL;
    private widget.Tanggal TanggalRujuk;
    private widget.Tanggal TanggalSEP;
    private widget.TextBox TglLahir;
    private widget.ComboBox TujuanKunjungan;
    private javax.swing.JDialog WindowAksi;
    private javax.swing.JDialog WindowPilihValidasi;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnDPJPLayanan;
    private widget.Button btnDPJPLayanan1;
    private widget.Button btnDiagnosaAwal;
    private widget.Button btnDiagnosaAwal1;
    private widget.Button btnDiagnosaAwal2;
    private widget.Button btnDiagnosaAwal3;
    private widget.Button btnDiagnosaAwal4;
    private widget.Button btnDokterTerapi;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private widget.Button btnKeluar;
    private widget.Button btnPoliTerapi;
    private widget.Button btnSimpan;
    private widget.Button btnValidasi;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
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
    private widget.Panel jPanel1;
    private widget.Panel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label lblTerapi;
    private widget.panelisi panelisi1;
    private widget.PasswordBox pwPass;
    private widget.PasswordBox pwUserId;
    // End of variables declaration//GEN-END:variables

    private void isNumber() {
        switch (KoneksiDB.URUTNOREG()) {
            case "poli":
                noreg = Sequel.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                    kodepolireg, Valid.getTglSmc(TanggalSEP));
                break;
            case "dokter":
                noreg = Sequel.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                    kodedokterreg, Valid.getTglSmc(TanggalSEP));
                break;
            case "dokter + poli":
                noreg = Sequel.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                    kodepolireg, kodedokterreg, Valid.getTglSmc(TanggalSEP));
                break;
            default:
                noreg = Sequel.cariIsiSmc(
                    "select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                    kodepolireg, kodedokterreg, Valid.getTglSmc(TanggalSEP));
                break;
        }

        norawat = Sequel.autonomorSmc("", "/", "reg_periksa", "no_rawat", 6, "0", Valid.getTglSmc(TanggalSEP));
    }

    private void isCekPasien() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pasien.nm_pasien, pasien.namakeluarga, pasien.keluarga, pasien.kd_pj, penjab.png_jawab, pasien.no_peserta, "
            + "concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as asal, if(pasien.tgl_daftar = ?, "
            + "'Baru', 'Lama') as daftar, timestampdiff(year, pasien.tgl_lahir, curdate()) as tahun, (timestampdiff(month, pasien.tgl_lahir, "
            + "curdate()) - ((timestampdiff(month, pasien.tgl_lahir, curdate()) div 12) * 12)) as bulan, timestampdiff(day, date_add(date_add( "
            + "pasien.tgl_lahir, interval timestampdiff(year, pasien.tgl_lahir, curdate()) year), interval timestampdiff(month, pasien.tgl_lahir, "
            + "curdate()) - ((timestampdiff(month, pasien.tgl_lahir, curdate()) div 12) * 12) month), curdate()) as hari, pasien.no_ktp "
            + "from pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join "
            + "kabupaten on pasien.kd_kab = kabupaten.kd_kab join penjab on pasien.kd_pj = penjab.kd_pj where pasien.no_rkm_medis = ?"
        )) {
            ps.setString(1, Valid.getTglSmc(TanggalSEP));
            ps.setString(2, TNoRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    alamatpasien = rs.getString("asal");
                    pjpasien = rs.getString("namakeluarga");
                    hubungankeluargapasien = rs.getString("keluarga");
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
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        statuspoli = "Baru";
        if (Sequel.cariExistsSmc("select * from reg_periksa where reg_periksa.no_rkm_medis = ? and reg_periksa.kd_poli = ?", TNoRM.getText(), kodepolireg)) {
            statuspoli = "Lama";
        }
    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", norawat);
        param.put("parameter", noSEP);
        param.put("namars", Sequel.cariIsi("select setting.nama_instansi from setting limit 1"));
        param.put("kotars", Sequel.cariIsi("select setting.kabupaten from setting limit 1"));

        if (JenisPelayanan.getSelectedIndex() == 0) {
            Valid.printReportSmc("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param, 1, KoneksiDB.PRINTER_REGISTRASI());
            Valid.reportSmc("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            Valid.printReportSmc("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param, 1, KoneksiDB.PRINTER_REGISTRASI());
            Valid.reportSmc("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }
        Valid.printReportSmc("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param, Integer.parseInt(JumlahBarcode.getText().trim()), KoneksiDB.PRINTER_BARCODE());
        Valid.reportSmc("rptBarcodeRawatAPM.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
    }

    private void insertSEP() {
        url = KoneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
        requestJson = "{"
            + "\"request\": {"
            + "\"t_sep\": {"
            + "\"noKartu\": \"" + NoKartu.getText() + "\","
            + "\"tglSep\": \"" + Valid.getTglSmc(TanggalSEP) + "\","
            + "\"ppkPelayanan\": \"" + KdPPK.getText() + "\","
            + "\"jnsPelayanan\": \"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
            + "\"klsRawat\": {"
            + "\"klsRawatHak\": \"" + Kelas.getSelectedItem().toString().substring(0, 1) + "\","
            + "\"klsRawatNaik\": \"\","
            + "\"pembiayaan\": \"\","
            + "\"penanggungJawab\": \"\""
            + "},"
            + "\"noMR\": \"" + TNoRM.getText() + "\","
            + "\"rujukan\": {"
            + "\"asalRujukan\": \"" + AsalRujukan.getSelectedItem().toString().substring(0, 1) + "\","
            + "\"tglRujukan\": \"" + Valid.getTglSmc(TanggalRujuk) + "\","
            + "\"noRujukan\": \"" + NoRujukan.getText() + "\","
            + "\"ppkRujukan\": \"" + KdPpkRujukan.getText() + "\""
            + "},"
            + "\"catatan\": \"" + Catatan.getText() + "\","
            + "\"diagAwal\": \"" + KdPenyakit.getText() + "\","
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
            + "\"lakaLantas\": \"" + LakaLantas.getSelectedItem().toString().substring(0, 1) + "\","
            + "\"penjamin\": {"
            + "\"tglKejadian\": \"" + tglkll.replaceAll("0000-00-00", "") + "\","
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
            + "\"user\": \"" + NoKartu.getText() + "\""
            + "}"
            + "}"
            + "}";
        try {
            tglkll = "0000-00-00";
            if (LakaLantas.getSelectedIndex() > 0) {
                tglkll = Valid.getTglSmc(TanggalKKL);
            }
            utc = api.getUTCDateTime();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("code : " + metadata.path("code").asText());
            System.out.println("message : " + metadata.path("message").asText());
            JOptionPane.showMessageDialog(null, "Respon BPJS : " + metadata.path("message").asText());
            if (metadata.path("code").asText().equals("200")) {
                nosep = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep").asText();
                System.out.println("SEP berhasil terbit!");
                System.out.println("No. SEP: " + nosep);
                String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ? and kd_pj = 'BPJ'", Valid.getTglSmc(TanggalSEP), TNoRM.getText(), kodepolireg, kodedokterreg);
                if (isNoRawat == null || (!isNoRawat.equals(norawat))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + norawat);
                    System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(TanggalSEP));
                    System.out.println("No. Antrian: " + noreg + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", norawat) + ")");
                    System.out.println("No. RM: " + TNoRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", norawat) + ")");
                    System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", norawat) + ")");
                    System.out.println("Kode Poli: " + kodepolireg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", norawat) + ")");
                    System.out.println("======================================================");
                    return;
                }

                Sequel.menyimpanSmc("bridging_sep", null,
                    nosep,
                    norawat,
                    Valid.getTglSmc(TanggalSEP),
                    Valid.getTglSmc(TanggalRujuk),
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
                    tglkll,
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
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.getTglSmc(TanggalSEP), nosep);
                }

                if (!prb.equals("")) {
                    Sequel.menyimpanSmc("bpjs_prb", null, nosep, prb);
                    prb = "";
                }

                if (Sequel.cariIntegerSmc(
                    "select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodedokterreg, kodepolireg
                ) == 1) {
                    Sequel.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", TNoRM.getText(), Valid.getTglSmc(TanggalSEP), kodedokterreg, kodepolireg);
                }
                cetakRegistrasi(nosep);
                emptTeks();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void cekFinger(String noka) {
        statusfinger = false;
        if (!NoKartu.getText().equals("")) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                utc = api.getUTCDateTime();
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                url = KoneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + Valid.getTglSmc(TanggalSEP);
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
                metadata = root.path("metaData");
                System.out.println("kodecekstatus : " + metadata.path("code").asText());
                if (metadata.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    if (response.path("kode").asText().equals("1")) {
                        if (response.path("status").asText().contains(Sequel.cariIsi("select current_date()"))) {
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
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data peserta!");
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
            url = KoneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTime();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("URL : " + url);
            if (metadata.path("code").asText().equals("200")) {
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
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                isPoli();
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                    NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + metadata.path("message").asText());
                JOptionPane.showMessageDialog(null, "Pesan Pencarian Rujukan FKTP : " + metadata.path("message").asText());
                try {
                    url = KoneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTime();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
                    metadata = root.path("metaData");
                    if (metadata.path("code").asText().equals("200")) {
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
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                        NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                            NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        }
                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                        AsalRujukan.setSelectedIndex(1);
                        isNumber();
                        Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                    } else {
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Pesan Pencarian Rujukan FKRTL : " + metadata.path("message").asText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi Peserta : " + ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
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
            url = KoneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTime();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("URL : " + url);
            if (metadata.path("code").asText().equals("200")) {
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
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", KdDPJP.getText());
                isPoli();
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                    NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + metadata.path("message").asText());
                try {
                    url = KoneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTime();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
                    metadata = root.path("metaData");
                    if (metadata.path("code").asText().equals("200")) {
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

                        kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                        NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                        if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                            NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                        }
                        KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                        AsalRujukan.setSelectedIndex(1);
                        isNumber();
                        Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                    } else {
                        emptTeks();
                        System.out.println("Pesan pencarian rujukan FKTL : " + metadata.path("message").asText());
                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi Peserta : " + ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, "
            + "maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal "
            + "on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?"
        )) {
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
            ps.setString(1, kodepolireg);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KdDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    NmDPJP.setText(rs.getString("nm_dokter_bpjs"));
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
            "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_kartu, left(bridging_sep.asal_rujukan, 1) "
            + "as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat "
            + "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = "
            + "bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
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
                            url = KoneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rskontrol.getString("no_kartu") + "/tglSEP/" + Valid.getTglSmc(TanggalSEP);
                            utc = api.getUTCDateTime();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                            requestEntity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println("URL : " + url);
                            if (metadata.path("code").asText().equals("200")) {
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
                                Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                                NoTelp.setText(response.path("mr").path("noTelepon").asText());
                                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                                    NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                }
                            } else {
                                emptTeks();
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi Peserta : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                            }
                        }
                    } else {
                        try {
                            if (rskontrol.getString("asal_rujukan").equals("1")) {
                                url = KoneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                            } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                url = KoneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                            }
                            utc = api.getUTCDateTime();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
                            requestEntity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println("URL : " + url);
                            if (metadata.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                                NoRujukan.setText(response.path("noKunjungan").asText());
                                NoSKDP.setText(rskontrol.getString("no_surat"));
                                KdPoli.setText(rskontrol.getString("kd_poli_bpjs"));
                                NmPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                KdDPJP.setText(rskontrol.getString("kd_dokter_bpjs"));
                                NmDPJP.setText(rskontrol.getString("nm_dokter_bpjs"));

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
                                Catatan.setText("Anjungan Pasien Mandiri " + NmPPK.getText());
                                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                                if (NoTelp.getText().contains("null") || NoTelp.getText().isBlank()) {
                                    NoTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", TNoRM.getText()));
                                }
                            } else {
                                emptTeks();
                                System.out.println("Pesan pencarian rujukan : " + metadata.path("message").asText());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi Peserta : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Maaf, Data surat kontrol tidak ditemukan...!!!");
        }
    }

    private void SimpanAntrianOnSite() {
        int angkaantrean = Integer.parseInt(noreg);
        String jeniskunjungan = "1";
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
                            datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", Valid.getTglSmc(TanggalSEP), jammulai, String.valueOf(Integer.parseInt(noreg) * 5));
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
                        + "\"kodebooking\": \"" + norawat + "\","
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
                        + "\"nomorantrean\": \"" + noreg + "\","
                        + "\"angkaantrean\": " + angkaantrean + ","
                        + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                        + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                        + "\"kuotajkn\": " + kuota + ","
                        + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                        + "\"kuotanonjkn\": " + kuota + ","
                        + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                        + "}";
                    System.out.println("JSON : " + requestJson);
                    url = KoneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    System.out.println("URL : " + url);
                    System.out.print("addantrean " + norawat + " : ");
                    try {
                        utc = api.getUTCDateTime();
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", KoneksiDB.CONSIDAPIMOBILEJKN());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", KoneksiDB.USERKEYAPIMOBILEJKN());
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
                        metadata = root.path("metadata");
                        responsecode = metadata.path("code").asText();
                        Sequel.logTaskid(norawat, norawat, "Onsite", "addantrean", requestJson, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                        System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                    } catch (HttpClientErrorException e) {
                        responsecode = e.getStatusCode().toString();
                        System.out.println(e.getMessage());
                        Sequel.logTaskid(norawat, norawat, "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                    } catch (HttpServerErrorException e) {
                        responsecode = e.getStatusCode().toString();
                        System.out.println(e.getMessage());
                        Sequel.logTaskid(norawat, norawat, "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                if (!responsecode.equals("200")) {
                    if (!NoSKDP.getText().equals("")) {
                        try {
                            utc = api.getUTCDateTime();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", KoneksiDB.CONSIDAPIMOBILEJKN());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", KoneksiDB.USERKEYAPIMOBILEJKN());
                            requestJson = "{"
                                + "\"kodebooking\": \"" + norawat + "\","
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
                                + "\"nomorantrean\": \"" + noreg + "\","
                                + "\"angkaantrean\": " + angkaantrean + ","
                                + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                                + "\"sisakuotajkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotajkn\": " + kuota + ","
                                + "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + ","
                                + "\"kuotanonjkn\": " + kuota + ","
                                + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
                                + "}";
                            requestEntity = new HttpEntity(requestJson, headers);
                            url = KoneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL : " + url);
                            System.out.println(requestEntity);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
                            metadata = root.path("metadata");
                            Sequel.logTaskid(norawat, norawat, "Onsite", "addantrean", requestJson, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                            System.out.println("respon WS BPJS Kirim Pakai SKDP : " + metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                        } catch (Exception e) {
                            System.out.println("Notif SKDP : " + e);
                        }
                    }

                    if (!NoRujukan.getText().equals("")) {
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", KoneksiDB.CONSIDAPIMOBILEJKN());
                            utc = api.getUTCDateTime();
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", KoneksiDB.USERKEYAPIMOBILEJKN());
                            requestJson = "{"
                                + "\"kodebooking\": \"" + norawat + "\","
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
                                + "\"nomorantrean\": \"" + noreg + "\","
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
                            url = KoneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL Kirim Pakai No.Rujuk : " + url);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
                            metadata = root.path("metadata");
                            Sequel.logTaskid(norawat, norawat, "Onsite", "addantrean", requestJson, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                            System.out.println("respon WS BPJS : " + metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
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
        resetAksi();
    }

    private void isPoli() {
        try (PreparedStatement ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ? order by nm_poli")) {
            ps.setString(1, kodepolireg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (statuspasien.equals("Lama")) {
                        biayareg = rs.getString("registrasilama");
                    } else {
                        biayareg = rs.getString("registrasi");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void bukaAplikasiFingerprint() {
        if (NoKartu.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No. kartu peserta tidak ada..!!");
            return;
        }
        try {
            fingerprintAktif = false;
            User32 u32 = User32.INSTANCE;

            u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                char[] windowText = new char[512];
                u32.GetWindowText(hwnd, windowText, 512);
                String wText = Native.toString(windowText);

                if (wText.isEmpty()) {
                    return true;
                }

                if (wText.toLowerCase().contains("registrasi sidik jari")) {
                    DlgRegistrasiSEPPertama.this.fingerprintAktif = true;
                    u32.SetForegroundWindow(hwnd);
                }

                return true;
            }, Pointer.NULL);

            Robot r = new Robot();
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;

            if (fingerprintAktif) {
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
                Runtime.getRuntime().exec(KoneksiDB.URLAPLIKASIFINGERPRINTBPJS());
                Thread.sleep(2000);
                ss = new StringSelection(KoneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);

                ss = new StringSelection(KoneksiDB.PASSFINGERPRINTBPJS());
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
                Runtime.getRuntime().exec(KoneksiDB.URLAPLIKASIFRISTABPJS());
                Thread.sleep(5000);

                ss = new StringSelection(KoneksiDB.USERFINGERPRINTBPJS());
                c.setContents(ss, ss);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_V);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(1000);

                ss = new StringSelection(KoneksiDB.PASSFINGERPRINTBPJS());
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
            utc = api.getUTCDateTime();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", KoneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", KoneksiDB.USERKEYAPIBPJS());
            url = KoneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
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
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, requestEntity, String.class).getBody());
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

    private boolean registerPasien() {
        int coba = 0, maxCoba = 5;
        System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + norawat);
        while (coba < maxCoba && (!Sequel.menyimpantfSmc("reg_periksa", null,
            noreg, norawat, Valid.getTglSmc(TanggalSEP),
            Sequel.cariIsi("select current_time()"), kodedokterreg, TNoRM.getText(), kodepolireg,
            pjpasien, alamatpasien, hubungankeluargapasien, biayareg, "Belum",
            statuspasien, "Ralan", kodepj, umur, sttsumur, "Belum Bayar", statuspoli))) {
            isNumber();
            System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + norawat);
            coba++;
        }

        String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", Valid.getTglSmc(TanggalSEP), TNoRM.getText(), kodepolireg, kodedokterreg, kodepj);
        if (coba == maxCoba && (isNoRawat == null || !isNoRawat.equals(norawat))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
            System.out.println("No. Rawat: " + norawat);
            System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(TanggalSEP));
            System.out.println("No. Antrian: " + noreg + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", norawat) + ")");
            System.out.println("No. RM: " + TNoRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", norawat) + ")");
            System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", norawat) + ")");
            System.out.println("Kode Poli: " + kodepolireg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", norawat) + ")");
            System.out.println("======================================================");
            return false;
        }
        updateUmurPasien();
        return true;
    }

    private boolean simpanRujukan() {
        int coba = 0, maxCoba = 5;
        norujukmasuk = Sequel.autonomorSmc("BR", "/", "rujuk_masuk", "no_balasan", 4, "0", Valid.getTglSmc(TanggalSEP));
        System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat: " + norujukmasuk);
        while (coba < maxCoba && (!Sequel.menyimpantfSmc("rujuk_masuk", null,
            norawat, NmPpkRujukan.getText(), "-", NoRujukan.getText(),
            "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-", "-", norujukmasuk
        ))) {
            norujukmasuk = Sequel.autonomorSmc("BR", "/", "rujuk_masuk", "no_balasan", 4, "0", Valid.getTglSmc(TanggalSEP));
            System.out.println("Mencoba memproses rujukan masuk pasien dengan no. surat balasan: " + norujukmasuk);
            coba++;
        }

        String isNoRujukMasuk = Sequel.cariIsiSmc("select rujuk_masuk.no_balasan from rujuk_masuk where rujuk_masuk.no_rawat = ?", norawat);
        if (coba == maxCoba && (isNoRujukMasuk == null || (!isNoRujukMasuk.equals(norujukmasuk)))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat memproses rujukan masuk dengan detail berikut:");
            System.out.println("No. Surat: " + norujukmasuk);
            System.out.println("No. Rawat: " + norawat);
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
}
