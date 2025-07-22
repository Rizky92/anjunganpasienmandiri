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
import java.sql.SQLException;
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

public class DlgRegistrasiSEPBPJS extends widget.Dialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final BPJSCekReferensiDokterDPJP dokter;
    private final BPJSCekReferensiPenyakit diagnosa;
    private final DlgCariPoliBPJS poli;
    private final BPJSCekRiwayatRujukanTerakhir riwayatRujukan;
    private final BPJSCekRiwayatPelayanan riwayatPelayanan;
    private final boolean ADDANTRIANAPIMOBILEJKN = koneksiDB.ADDANTRIANAPIMOBILEJKN();
    private String hari = "",
        instansiNama = "",
        instansiKota = "",
        tglkll = "0000-00-00",
        datajam = "",
        json = "",
        url = "",
        noSEP = "",
        noBooking = "",
        jenisKunjungan = "",
        kodePoli = "",
        kodeDokter = "",
        prb = "",
        noReg = "",
        noRawat = "",
        kodeDokterReg = "",
        kodePoliReg = "",
        kdpjBPJS = "",
        namaPJ = "",
        alamatPJ = "",
        hubunganPJ = "",
        biayaReg = "",
        statusPoli = "Baru",
        statusDaftar = "",
        umurDaftar = "0",
        statusUmur = "Th",
        umurPasien = "",
        utc = "",
        aksi = "",
        noTelpBPJS = "";
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode root, response, metadata;
    private Calendar cal = Calendar.getInstance();
    private HttpHeaders headers;
    private HttpEntity entity;
    private boolean statusFinger = false, aplikasiAktif = false, fristaAktif = false, isMobileJKN = false;
    private int day = cal.get(Calendar.DAY_OF_WEEK);
    private Date parsedDate;

    public DlgRegistrasiSEPBPJS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        barcode.setDocument(new batasInput((byte) 3).getOnlyAngka(barcode));

        try (ResultSet rs = koneksi.createStatement().executeQuery("select kode_ppk, nama_instansi, kabupaten from setting")) {
            if (rs.next()) {
                instansiNama = rs.getString("nama_instansi");
                instansiKota = rs.getString("kabupaten");
                kodePPK.setText(rs.getString("kode_ppk"));
                namaPPK.setText(instansiNama);
                catatan.setText("Anjungan Pasien Mandiri " + instansiNama);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        dokter = new BPJSCekReferensiDokterDPJP(parent, modal);
        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    kodeDokter = dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString();
                    namaDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    if (jenisPelayanan.getSelectedIndex() == 1) {
                        kodeDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        namaDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    }
                }
                namaDokter.requestFocus();
            }
        });

        poli = new DlgCariPoliBPJS(parent, modal);
        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.hasSelectedRow()) {
                    kodePoli = poli.getSelectedRow(0).toString();
                    namaPoli.setText(poli.getSelectedRow(1).toString());
                    namaPoli.requestFocus();
                }
            }
        });

        diagnosa = new BPJSCekReferensiPenyakit(parent, modal);
        diagnosa.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (diagnosa.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 1).toString());
                    namaDiagnosa.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 2).toString());
                }
                kodeDiagnosa.requestFocus();
            }
        });

        riwayatRujukan = new BPJSCekRiwayatRujukanTerakhir(parent, modal);
        riwayatRujukan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (riwayatRujukan.getTable().getSelectedRow() != -1) {
                    noRujukan.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 2).toString());
                    kodePPKRujukan.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 6).toString());
                    namaPPKRujukan.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 7).toString());
                    kodeDiagnosa.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 0).toString());
                    namaDiagnosa.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 1).toString());
                    kodePoli = riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 3).toString();
                    namaPoli.setText(riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 4).toString());
                    Valid.SetTgl(tglRujukan, riwayatRujukan.getTable().getValueAt(riwayatRujukan.getTable().getSelectedRow(), 5).toString());
                }
                catatan.requestFocus();
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

        kdpjBPJS = Sequel.cariIsiSmc("select password_asuransi.kd_pj from password_asuransi");

        emptTeks();
        barcode.setText("3");
        isForm();
        panelNumpad.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgAksiFP = new widget.Dialog();
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
        emptyKiri = new widget.Panel();
        panelTengah = new widget.Panel();
        panelUtama = new widget.Panel();
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
        namaDokter = new widget.TextField();
        cariDokter = new widget.Button();
        jLabel56 = new widget.Label();
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
        btnFingerprint = new widget.Button();
        btnFrista = new widget.Button();
        panelNumpad = new widget.Numpad();
        panelTambahan = new widget.Panel();
        toggleInfoTambahan = new widget.PaneToggle();
        form = new widget.Panel();
        jLabel13 = new widget.Label();
        jenisPelayanan = new widget.ComboBox();
        jLabel42 = new widget.Label();
        tujuanKunjungan = new widget.ComboBox();
        jLabel43 = new widget.Label();
        flagProsedur = new widget.ComboBox();
        jLabel44 = new widget.Label();
        penunjang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        asesmenPelayanan = new widget.ComboBox();
        LabelPoli7 = new widget.Label();
        kodeDPJPLayanan = new widget.TextField();
        namaDPJPLayanan = new widget.TextField();
        jLabel9 = new widget.Label();
        kodePPK = new widget.TextField();
        namaPPK = new widget.TextField();
        jLabel55 = new widget.Label();
        lakaLantas = new widget.ComboBox();
        tglKLL = new widget.Tanggal();
        jLabel36 = new widget.Label();
        keterangan = new widget.TextField();
        jLabel40 = new widget.Label();
        suplesi = new widget.ComboBox();
        jLabel41 = new widget.Label();
        noSEPSuplesi = new widget.TextField();
        LabelPoli3 = new widget.Label();
        kdPropKLL = new widget.TextField();
        nmPropKLL = new widget.TextField();
        LabelPoli4 = new widget.Label();
        kdKabKLL = new widget.TextField();
        nmKabKLL = new widget.TextField();
        LabelPoli5 = new widget.Label();
        kdKecKLL = new widget.TextField();
        nmKecKLL = new widget.TextField();
        jLabel14 = new widget.Label();
        catatan = new widget.TextField();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        jLabel15 = new widget.Label();
        barcode = new widget.TextField();
        emptyKanan = new widget.Panel();
        panelBawah = new widget.Panel();
        btnKonfirmasi = new widget.Button();
        btnBatal = new widget.Button();

        dlgAksiFP.setUndecorated(false);

        judulAksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        judulAksi.setText("KONFIRMASI AKSI");
        judulAksi.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        judulAksi.setPreferredSize(new java.awt.Dimension(400, 30));
        dlgAksiFP.getContentPane().add(judulAksi, java.awt.BorderLayout.PAGE_START);

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
        label1.setPreferredSize(new java.awt.Dimension(60, 30));
        panelTengahAksi.add(label1);
        label1.setBounds(0, 30, 110, 30);

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setPreferredSize(new java.awt.Dimension(60, 30));
        panelTengahAksi.add(label2);
        label2.setBounds(0, 70, 110, 30);

        dlgAksiFP.getContentPane().add(panelTengahAksi, java.awt.BorderLayout.CENTER);

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

        dlgAksiFP.getContentPane().add(panelBawahAksi, java.awt.BorderLayout.PAGE_END);

        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelAtas.setMinimumSize(new java.awt.Dimension(1, 35));
        panelAtas.setPreferredSize(new java.awt.Dimension(1, 35));
        panelAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        label4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label4.setText("DATA ELIGIBILITAS PESERTA JKN");
        label4.setFocusable(false);
        label4.setPreferredSize(new java.awt.Dimension(340, 35));
        panelAtas.add(label4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(panelAtas, gridBagConstraints);

        emptyKiri.setMinimumSize(new java.awt.Dimension(0, 0));
        emptyKiri.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(emptyKiri, gridBagConstraints);

        panelTengah.setPreferredSize(new java.awt.Dimension(1280, 585));
        panelTengah.setLayout(new java.awt.GridBagLayout());

        panelUtama.setMinimumSize(new java.awt.Dimension(533, 290));
        panelUtama.setPreferredSize(new java.awt.Dimension(1280, 420));
        panelUtama.setLayout(null);

        namaPasien.setEditable(false);
        panelUtama.add(namaPasien);
        namaPasien.setBounds(375, 10, 870, 40);

        noRM.setEditable(false);
        panelUtama.add(noRM);
        noRM.setBounds(230, 10, 140, 40);

        noPeserta.setEditable(false);
        panelUtama.add(noPeserta);
        noPeserta.setBounds(875, 145, 210, 40);

        jLabel20.setText("Tgl. SEP :");
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel20);
        jLabel20.setBounds(740, 280, 130, 40);

        tglSEP.setEditable(false);
        tglSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        panelUtama.add(tglSEP);
        tglSEP.setBounds(875, 280, 210, 40);

        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel22);
        jLabel22.setBounds(740, 235, 130, 40);

        tglRujukan.setEditable(false);
        tglRujukan.setPreferredSize(new java.awt.Dimension(95, 23));
        panelUtama.add(tglRujukan);
        tglRujukan.setBounds(875, 235, 210, 40);

        jLabel23.setText("No. SKDP / Surat Kontrol :");
        panelUtama.add(jLabel23);
        jLabel23.setBounds(0, 100, 225, 40);

        noRujukan.setEditable(false);
        panelUtama.add(noRujukan);
        noRujukan.setBounds(230, 145, 455, 40);

        jLabel10.setText("PPK Rujukan :");
        panelUtama.add(jLabel10);
        jLabel10.setBounds(0, 190, 225, 40);

        kodePPKRujukan.setEditable(false);
        panelUtama.add(kodePPKRujukan);
        kodePPKRujukan.setBounds(230, 190, 115, 40);

        namaPPKRujukan.setEditable(false);
        panelUtama.add(namaPPKRujukan);
        namaPPKRujukan.setBounds(350, 190, 335, 40);

        jLabel11.setText("Diagnosa Awal :");
        panelUtama.add(jLabel11);
        jLabel11.setBounds(0, 235, 225, 40);

        kodeDiagnosa.setEditable(false);
        panelUtama.add(kodeDiagnosa);
        kodeDiagnosa.setBounds(230, 235, 115, 40);

        namaDiagnosa.setEditable(false);
        panelUtama.add(namaDiagnosa);
        namaDiagnosa.setBounds(350, 235, 335, 40);

        namaPoli.setEditable(false);
        panelUtama.add(namaPoli);
        namaPoli.setBounds(230, 280, 455, 40);

        LabelPoli.setText("Poli Tujuan :");
        panelUtama.add(LabelPoli);
        LabelPoli.setBounds(0, 280, 225, 40);

        LabelKelas.setText("Kelas :");
        panelUtama.add(LabelKelas);
        LabelKelas.setBounds(0, 370, 225, 40);

        kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        kelas.setSelectedIndex(2);
        panelUtama.add(kelas);
        kelas.setBounds(230, 370, 150, 40);

        jLabel8.setText("Data Pasien :");
        panelUtama.add(jLabel8);
        jLabel8.setBounds(0, 10, 225, 40);

        tglLahir.setEditable(false);
        panelUtama.add(tglLahir);
        tglLahir.setBounds(230, 55, 140, 40);

        jLabel18.setText("L / P :");
        panelUtama.add(jLabel18);
        jLabel18.setBounds(1145, 55, 55, 40);

        jk.setEditable(false);
        panelUtama.add(jk);
        jk.setBounds(1205, 55, 40, 40);

        jLabel24.setText("Jenis Peserta :");
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel24);
        jLabel24.setBounds(740, 55, 130, 40);

        jenisPeserta.setEditable(false);
        panelUtama.add(jenisPeserta);
        jenisPeserta.setBounds(875, 55, 270, 40);

        jLabel25.setText("Status :");
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel25);
        jLabel25.setBounds(380, 55, 70, 40);

        statusPeserta.setEditable(false);
        panelUtama.add(statusPeserta);
        statusPeserta.setBounds(455, 55, 230, 40);

        jLabel27.setText("Asal Rujukan :");
        panelUtama.add(jLabel27);
        jLabel27.setBounds(740, 190, 130, 40);

        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        panelUtama.add(asalRujukan);
        asalRujukan.setBounds(875, 190, 210, 40);

        noTelp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                noTelpFocusLost(evt);
            }
        });
        noTelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                noTelpMouseClicked(evt);
            }
        });
        panelUtama.add(noTelp);
        noTelp.setBounds(875, 370, 210, 40);

        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        panelUtama.add(katarak);
        katarak.setBounds(875, 325, 210, 40);

        jLabel37.setText("Katarak :");
        panelUtama.add(jLabel37);
        jLabel37.setBounds(740, 325, 130, 40);

        LabelPoli2.setText("Dokter Tujuan :");
        panelUtama.add(LabelPoli2);
        LabelPoli2.setBounds(0, 325, 225, 40);

        namaDokter.setEditable(false);
        panelUtama.add(namaDokter);
        namaDokter.setBounds(230, 325, 455, 40);

        cariDokter.setBackground(new java.awt.Color(240, 249, 255));
        cariDokter.setBorder(null);
        cariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDokter.setToolTipText("Referensi Dokter");
        cariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDokterActionPerformed(evt);
            }
        });
        panelUtama.add(cariDokter);
        cariDokter.setBounds(690, 325, 45, 40);

        jLabel56.setText("No. Telp :");
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel56);
        jLabel56.setBounds(740, 370, 130, 40);

        jLabel12.setText("Tgl. Lahir :");
        panelUtama.add(jLabel12);
        jLabel12.setBounds(0, 55, 225, 40);

        jLabel6.setText("NIK :");
        panelUtama.add(jLabel6);
        jLabel6.setBounds(740, 100, 130, 40);

        noSKDP.setEditable(false);
        panelUtama.add(noSKDP);
        noSKDP.setBounds(230, 100, 455, 40);

        jLabel26.setText("No. Rujukan :");
        panelUtama.add(jLabel26);
        jLabel26.setBounds(0, 145, 225, 40);

        nik.setEditable(false);
        panelUtama.add(nik);
        nik.setBounds(875, 100, 370, 40);

        jLabel7.setText("No. Peserta :");
        panelUtama.add(jLabel7);
        jLabel7.setBounds(740, 145, 130, 40);

        cariPoli.setBackground(new java.awt.Color(240, 249, 255));
        cariPoli.setBorder(null);
        cariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariPoli.setToolTipText("Referensi Poli BPJS");
        cariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariPoliActionPerformed(evt);
            }
        });
        panelUtama.add(cariPoli);
        cariPoli.setBounds(690, 280, 45, 40);

        cariDiagnosa.setBackground(new java.awt.Color(240, 249, 255));
        cariDiagnosa.setBorder(null);
        cariDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDiagnosa.setToolTipText("Referensi Diagnosa BPJS");
        cariDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDiagnosaActionPerformed(evt);
            }
        });
        panelUtama.add(cariDiagnosa);
        cariDiagnosa.setBounds(690, 235, 45, 40);

        cariNoRujukan.setBackground(new java.awt.Color(240, 249, 255));
        cariNoRujukan.setBorder(null);
        cariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariNoRujukan.setToolTipText("Daftar Rujukan Pasien");
        cariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariNoRujukanActionPerformed(evt);
            }
        });
        panelUtama.add(cariNoRujukan);
        cariNoRujukan.setBounds(690, 145, 45, 40);

        btnRiwayatPelayanan.setBackground(new java.awt.Color(240, 249, 255));
        btnRiwayatPelayanan.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnRiwayatPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananActionPerformed(evt);
            }
        });
        panelUtama.add(btnRiwayatPelayanan);
        btnRiwayatPelayanan.setBounds(1090, 145, 45, 40);

        btnFingerprint.setBackground(new java.awt.Color(255, 255, 255));
        btnFingerprint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setText("Fingerprint");
        btnFingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFingerprint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerprintActionPerformed(evt);
            }
        });
        panelUtama.add(btnFingerprint);
        btnFingerprint.setBounds(650, 420, 140, 100);

        btnFrista.setBackground(new java.awt.Color(255, 255, 255));
        btnFrista.setForeground(new java.awt.Color(0, 131, 62));
        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-scan.png"))); // NOI18N
        btnFrista.setText("FRISTA");
        btnFrista.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFrista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFrista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFristaActionPerformed(evt);
            }
        });
        panelUtama.add(btnFrista);
        btnFrista.setBounds(490, 420, 140, 100);

        panelNumpad.setFocusable(false);
        panelNumpad.setFontSize(36);
        panelNumpad.setTextBox(noTelp);
        panelUtama.add(panelNumpad);
        panelNumpad.setBounds(875, 420, 255, 340);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTengah.add(panelUtama, gridBagConstraints);

        panelTambahan.setOpaque(false);
        panelTambahan.setLayout(new java.awt.BorderLayout());

        toggleInfoTambahan.setForeground(new java.awt.Color(150, 155, 159));
        toggleInfoTambahan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        toggleInfoTambahan.setMnemonic('I');
        toggleInfoTambahan.setToolTipText("Alt+I");
        toggleInfoTambahan.setPreferredSize(new java.awt.Dimension(192, 30));
        toggleInfoTambahan.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        toggleInfoTambahan.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        toggleInfoTambahan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        toggleInfoTambahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleInfoTambahanActionPerformed(evt);
            }
        });
        panelTambahan.add(toggleInfoTambahan, java.awt.BorderLayout.PAGE_END);

        form.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0, 131, 62)));
        form.setPreferredSize(new java.awt.Dimension(1280, 540));
        form.setLayout(null);

        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel13);
        jLabel13.setBounds(0, 10, 225, 40);

        jenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        jenisPelayanan.setSelectedIndex(1);
        jenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jenisPelayananItemStateChanged(evt);
            }
        });
        form.add(jenisPelayanan);
        jenisPelayanan.setBounds(230, 10, 150, 40);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel42);
        jLabel42.setBounds(0, 55, 225, 40);

        tujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        tujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tujuanKunjunganItemStateChanged(evt);
            }
        });
        form.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(230, 55, 455, 40);

        jLabel43.setText("Flag Prosedur :");
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel43);
        jLabel43.setBounds(0, 100, 225, 40);

        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        form.add(flagProsedur);
        flagProsedur.setBounds(230, 100, 455, 40);

        jLabel44.setText("Penunjang :");
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel44);
        jLabel44.setBounds(0, 145, 225, 40);

        penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        penunjang.setEnabled(false);
        form.add(penunjang);
        penunjang.setBounds(230, 145, 455, 40);

        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel45);
        jLabel45.setBounds(0, 190, 225, 40);

        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        form.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(230, 190, 455, 40);

        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(LabelPoli7);
        LabelPoli7.setBounds(0, 235, 225, 40);

        kodeDPJPLayanan.setEditable(false);
        form.add(kodeDPJPLayanan);
        kodeDPJPLayanan.setBounds(230, 235, 115, 40);

        namaDPJPLayanan.setEditable(false);
        form.add(namaDPJPLayanan);
        namaDPJPLayanan.setBounds(350, 235, 335, 40);

        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel9);
        jLabel9.setBounds(0, 280, 225, 40);

        kodePPK.setEditable(false);
        form.add(kodePPK);
        kodePPK.setBounds(230, 280, 115, 40);

        namaPPK.setEditable(false);
        form.add(namaPPK);
        namaPPK.setBounds(350, 280, 335, 40);

        jLabel55.setText("Laka Lantas :");
        form.add(jLabel55);
        jLabel55.setBounds(720, 10, 150, 40);

        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        form.add(lakaLantas);
        lakaLantas.setBounds(875, 10, 225, 40);

        tglKLL.setEditable(false);
        tglKLL.setEnabled(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        form.add(tglKLL);
        tglKLL.setBounds(1105, 10, 140, 40);

        jLabel36.setText("Keterangan :");
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel36);
        jLabel36.setBounds(720, 55, 150, 40);

        keterangan.setEditable(false);
        form.add(keterangan);
        keterangan.setBounds(875, 55, 370, 40);

        jLabel40.setText("Suplesi :");
        form.add(jLabel40);
        jLabel40.setBounds(720, 100, 150, 40);

        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                suplesiItemStateChanged(evt);
            }
        });
        form.add(suplesi);
        suplesi.setBounds(875, 100, 140, 40);

        jLabel41.setText("No. SEP :");
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel41);
        jLabel41.setBounds(720, 145, 150, 40);

        noSEPSuplesi.setEditable(false);
        form.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(875, 145, 370, 40);

        LabelPoli3.setText("Propinsi KLL :");
        form.add(LabelPoli3);
        LabelPoli3.setBounds(720, 190, 150, 40);

        kdPropKLL.setEditable(false);
        form.add(kdPropKLL);
        kdPropKLL.setBounds(875, 190, 115, 40);

        nmPropKLL.setEditable(false);
        form.add(nmPropKLL);
        nmPropKLL.setBounds(995, 190, 250, 40);

        LabelPoli4.setText("Kabupaten KLL :");
        form.add(LabelPoli4);
        LabelPoli4.setBounds(720, 235, 150, 40);

        kdKabKLL.setEditable(false);
        form.add(kdKabKLL);
        kdKabKLL.setBounds(875, 235, 115, 40);

        nmKabKLL.setEditable(false);
        form.add(nmKabKLL);
        nmKabKLL.setBounds(995, 235, 250, 40);

        LabelPoli5.setText("Kecamatan KLL :");
        form.add(LabelPoli5);
        LabelPoli5.setBounds(720, 280, 150, 40);

        kdKecKLL.setEditable(false);
        form.add(kdKecKLL);
        kdKecKLL.setBounds(875, 280, 115, 40);

        nmKecKLL.setEditable(false);
        form.add(nmKecKLL);
        nmKecKLL.setBounds(995, 280, 250, 40);

        jLabel14.setText("Catatan :");
        form.add(jLabel14);
        jLabel14.setBounds(720, 325, 150, 40);

        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        form.add(catatan);
        catatan.setBounds(875, 325, 370, 40);

        btnApprovalFP.setBackground(new java.awt.Color(255, 255, 255));
        btnApprovalFP.setForeground(new java.awt.Color(0, 131, 62));
        btnApprovalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnApprovalFP.setText("Approval FP");
        btnApprovalFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnApprovalFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnApprovalFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApprovalFPActionPerformed(evt);
            }
        });
        form.add(btnApprovalFP);
        btnApprovalFP.setBounds(650, 400, 160, 120);

        btnPengajuanFP.setBackground(new java.awt.Color(255, 255, 255));
        btnPengajuanFP.setForeground(new java.awt.Color(0, 131, 62));
        btnPengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnPengajuanFP.setText("Pengajuan FP");
        btnPengajuanFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPengajuanFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPengajuanFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPengajuanFPActionPerformed(evt);
            }
        });
        form.add(btnPengajuanFP);
        btnPengajuanFP.setBounds(470, 400, 160, 120);

        jLabel15.setText("Jumlah Barcode :");
        form.add(jLabel15);
        jLabel15.setBounds(0, 325, 225, 40);

        barcode.setText("3");
        form.add(barcode);
        barcode.setBounds(230, 325, 50, 40);

        panelTambahan.add(form, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTengah.add(panelTambahan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panelTengah, gridBagConstraints);

        emptyKanan.setMinimumSize(new java.awt.Dimension(0, 0));
        emptyKanan.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(emptyKanan, gridBagConstraints);

        panelBawah.setMinimumSize(new java.awt.Dimension(533, 100));
        panelBawah.setPreferredSize(new java.awt.Dimension(1, 75));

        btnKonfirmasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnKonfirmasi.setMnemonic('S');
        btnKonfirmasi.setText("KONFIRMASI");
        btnKonfirmasi.setToolTipText("Alt+S");
        btnKonfirmasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKonfirmasi.setPreferredSize(new java.awt.Dimension(300, 60));
        btnKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKonfirmasiActionPerformed(evt);
            }
        });
        panelBawah.add(btnKonfirmasi);

        btnBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnBatal.setForeground(new java.awt.Color(255, 33, 32));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/exit.png"))); // NOI18N
        btnBatal.setMnemonic('K');
        btnBatal.setText("Batal");
        btnBatal.setToolTipText("Alt+K");
        btnBatal.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnBatal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBatal.setPreferredSize(new java.awt.Dimension(300, 60));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        panelBawah.add(btnBatal);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(panelBawah, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(noPeserta.getText());
        if (namaPasien.getText().isBlank()) {
            Valid.textKosong(namaPasien, "Pasien");
        } else if (noPeserta.getText().isBlank()) {
            Valid.textKosong(noPeserta, "Nomor Kartu");
        } else if (Sequel.cariIntegerSmc("select count(*) from pasien where no_rkm_medis = ?", noRM.getText()) < 1) {
            JOptionPane.showMessageDialog(null, "Maaf, no RM tidak sesuai");
        } else if (kodePPKRujukan.getText().isBlank() || namaPPKRujukan.getText().isBlank()) {
            Valid.textKosong(kodePPKRujukan, "PPK Rujukan");
        } else if (kodePPK.getText().isBlank() || namaPPK.getText().isBlank()) {
            Valid.textKosong(kodePPK, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().isBlank() || namaDiagnosa.getText().isBlank()) {
            Valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().isBlank()) {
            Valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.isBlank() || namaPoli.getText().isBlank())) {
            Valid.textKosong(namaPoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().isBlank()) {
            Valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDokter.isBlank() || namaDokter.getText().isBlank()) {
            Valid.textKosong(namaDokter, "DPJP");
        } else if (!statusFinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !namaPoli.getText().toLowerCase().contains("darurat")) {
            JOptionPane.showMessageDialog(null, "Silahkan lakukan validasi biometrik dahulu..!!");
        } else {
            kodePoliReg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
            kodeDokterReg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
            if (kodePoliReg.isBlank() || kodeDokterReg.isBlank()) {
                JOptionPane.showMessageDialog(null, "Mapping Poliklinik atau Dokter tidak ditemukan..!!");
            } else {
                if (!registerPasien()) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat pendaftaran pasien!");
                } else {
                    if (jenisPelayanan.getSelectedIndex() == 0) {
                        insertSEP();
                    } else if (jenisPelayanan.getSelectedIndex() == 1) {
                        if (namaPoli.getText().toLowerCase().contains("darurat")) {
                            if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", noPeserta.getText(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(tglSEP)) >= 3) {
                                JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                            } else {
                                if (kirimAntrianOnsite()) {
                                    insertSEP();
                                }
                            }
                        } else if (!namaPoli.getText().toLowerCase().contains("darurat")) {
                            if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", noPeserta.getText(), jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(tglSEP)) >= 1) {
                                JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                            } else {
                                if (kirimAntrianOnsite()) {
                                    insertSEP();
                                }
                            }
                        }
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnKonfirmasiActionPerformed

    private void cariDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDokterActionPerformed
        dokter.setSize(getContentPane().getSize());
        dokter.setLocationRelativeTo(getContentPane());
        dokter.carinamadokter(kodePoli, namaPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_cariDokterActionPerformed

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

    private void jenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenisPelayananItemStateChanged
        if (jenisPelayanan.getSelectedIndex() == 0) {
            kodePoli = "";
            namaPoli.setText("");
            LabelPoli.setVisible(false);
            namaPoli.setVisible(false);
            kodeDPJPLayanan.setText("");
            namaDPJPLayanan.setText("");
            cariPoli.setEnabled(false);
        } else if (jenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
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
        diagnosa.setSize(getContentPane().getSize());
        diagnosa.setLocationRelativeTo(getContentPane());
        diagnosa.setVisible(true);
    }//GEN-LAST:event_cariDiagnosaActionPerformed

    private void cariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariNoRujukanActionPerformed
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            riwayatRujukan.setSize(getContentPane().getSize());
            riwayatRujukan.setLocationRelativeTo(getContentPane());
            riwayatRujukan.tampil(noPeserta.getText(), namaPasien.getText());
            riwayatRujukan.setVisible(true);
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

    private void btnApprovalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApprovalFPActionPerformed
        resetAksi();
        if (!noPeserta.getText().isBlank()) {
            aksi = "Approval";
            dlgAksiFP.setSize(415, 250);
            dlgAksiFP.setLocationRelativeTo(null);
            dlgAksiFP.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (!noPeserta.getText().isBlank()) {
            aksi = "Pengajuan";
            dlgAksiFP.setSize(415, 250);
            dlgAksiFP.setLocationRelativeTo(null);
            dlgAksiFP.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!");
        }
    }//GEN-LAST:event_btnPengajuanFPActionPerformed

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
                                    utc = api.getUTCDateTimeAsString();
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    url = koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                                    json = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                                        "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    entity = new HttpEntity(json, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("code : " + metadata.path("code").asText());
                                    System.out.println("message : " + metadata.path("message").asText());
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
                                    utc = api.getUTCDateTimeAsString();
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    url = koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                                    json = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                                        "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    entity = new HttpEntity(json, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("code : " + metadata.path("code").asText());
                                    System.out.println("message : " + metadata.path("message").asText());
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

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        resetAksi();
        dlgAksiFP.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void btnFingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerprintActionPerformed
        bukaAplikasiFingerprint();
    }//GEN-LAST:event_btnFingerprintActionPerformed

    private void btnFristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFristaActionPerformed
        bukaAplikasiFrista();
    }//GEN-LAST:event_btnFristaActionPerformed

    private void toggleInfoTambahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleInfoTambahanActionPerformed
        isForm();
    }//GEN-LAST:event_toggleInfoTambahanActionPerformed

    private void noTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noTelpMouseClicked
        if (!toggleInfoTambahan.isSelected()) {
            panelNumpad.setVisible(true);
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

    private void noTelpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusLost
        panelNumpad.setVisible(false);
    }//GEN-LAST:event_noTelpFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.ComboBox asalRujukan;
    private widget.ComboBox asesmenPelayanan;
    private widget.TextField barcode;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnApprovalFP;
    private widget.Button btnBatal;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private widget.Button btnKonfirmasi;
    private widget.Button btnPengajuanFP;
    private widget.Button btnRiwayatPelayanan;
    private widget.Button cariDiagnosa;
    private widget.Button cariDokter;
    private widget.Button cariNoRujukan;
    private widget.Button cariPoli;
    private widget.TextField catatan;
    private widget.Dialog dlgAksiFP;
    private widget.Panel emptyKanan;
    private widget.Panel emptyKiri;
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
    private widget.TextField kodeDPJPLayanan;
    private widget.TextField kodeDiagnosa;
    private widget.TextField kodePPK;
    private widget.TextField kodePPKRujukan;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label4;
    private widget.ComboBox lakaLantas;
    private widget.TextField namaDPJPLayanan;
    private widget.TextField namaDiagnosa;
    private widget.TextField namaDokter;
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
    private widget.Panel panelAtas;
    private widget.Panel panelBawah;
    private widget.Panel panelBawahAksi;
    private widget.Numpad panelNumpad;
    private widget.Panel panelTambahan;
    private widget.Panel panelTengah;
    private widget.Panel panelTengahAksi;
    private widget.Panel panelUtama;
    private widget.PasswordField passAksi;
    private widget.ComboBox penunjang;
    private widget.TextField statusPeserta;
    private widget.ComboBox suplesi;
    private widget.Tanggal tglKLL;
    private widget.TextField tglLahir;
    private widget.Tanggal tglRujukan;
    private widget.Tanggal tglSEP;
    private widget.PaneToggle toggleInfoTambahan;
    private widget.ComboBox tujuanKunjungan;
    private widget.PasswordField userAksi;
    // End of variables declaration//GEN-END:variables

    private void setNomorRegistrasi() {
        switch (koneksiDB.URUTNOREG()) {
            case "poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?", kodePoliReg, Valid.getTglSmc(tglSEP));
                break;
            case "dokter":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?", kodeDokterReg, Valid.getTglSmc(tglSEP));
                break;
            case "dokter + poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?", kodePoliReg, kodeDokterReg, Valid.getTglSmc(tglSEP));
                break;
            default:
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?", kodePoliReg, kodeDokterReg, Valid.getTglSmc(tglSEP));
                break;
        }

        noRawat = Sequel.cariIsiSmc("select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?", Valid.getTglSmc(tglSEP));
    }

    private void tentukanHari() {
        try {
            java.sql.Date hariperiksa = java.sql.Date.valueOf(Valid.getTglSmc(tglSEP));
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
        try (PreparedStatement ps = koneksi.prepareStatement("select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal," +
            "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, " +
            "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun,pasien.no_peserta, " +
            "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, " +
            "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,pasien.no_ktp,pasien.no_tlp " +
            "from pasien inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel " +
            "inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec " +
            "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab " +
            "inner join penjab on pasien.kd_pj=penjab.kd_pj " +
            "where pasien.no_rkm_medis=?"
        )) {
            ps.setString(1, Valid.getTglSmc(tglSEP));
            ps.setString(2, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    namaPJ = rs.getString("namakeluarga");
                    hubunganPJ = rs.getString("keluarga");
                    alamatPJ = rs.getString("asal");
                    umurDaftar = "0";
                    statusUmur = "Th";
                    statusDaftar = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umurDaftar = rs.getString("tahun");
                        statusUmur = "Th";
                    } else if (rs.getInt("tahun") == 0) {
                        if (rs.getInt("bulan") > 0) {
                            umurDaftar = rs.getString("bulan");
                            statusUmur = "Bl";
                        } else if (rs.getInt("bulan") == 0) {
                            umurDaftar = rs.getString("hari");
                            statusUmur = "Hr";
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodePoliReg)) {
            statusPoli = "Lama";
        }
    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", noRawat);
        param.put("parameter", noSEP);
        param.put("namars", instansiNama);
        param.put("kotars", instansiKota);

        if (jenisPelayanan.getSelectedIndex() == 0) {
            Valid.printReport("rptBridgingSEPAPM1.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            // Valid.MyReport("rptBridgingSEPAPM1.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        } else {
            Valid.printReport("rptBridgingSEPAPM2.jasper", koneksiDB.PRINTER_REGISTRASI(), "::[ Cetak SEP Model 4 ]::", 1, param);
            // Valid.MyReport("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param);
        }

        Valid.printReport("rptBarcodeRawatAPM.jasper", koneksiDB.PRINTER_BARCODE(), "::[ Barcode Perawatan ]::", Integer.parseInt(barcode.getText().trim()), param);
        // Valid.MyReport("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param);
    }

    private void insertSEP() {
        try {
            tglkll = "0000-00-00";
            if (lakaLantas.getSelectedIndex() > 0) {
                tglkll = Valid.SetTgl(tglKLL.getSelectedItem() + "");
            }
            utc = api.getUTCDateTimeAsString();

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            url = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            json = "{" +
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
                "\"tglRujukan\":\"" + Valid.SetTgl(tglRujukan.getSelectedItem() + "") + "\"," +
                "\"noRujukan\":\"" + noRujukan.getText() + "\"," +
                "\"ppkRujukan\":\"" + kodePPKRujukan.getText() + "\"" +
                "}," +
                "\"catatan\":\"" + catatan.getText() + "\"," +
                "\"diagAwal\":\"" + kodeDiagnosa.getText() + "\"," +
                "\"poli\": {" +
                "\"tujuan\": \"" + kodePoli + "\"," +
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
                "\"kodeDPJP\": \"" + kodeDokter + "\"" +
                "}," +
                "\"dpjpLayan\": \"" + (kodeDPJPLayanan.getText().isBlank() ? "" : kodeDPJPLayanan.getText()) + "\"," +
                "\"noTelp\": \"" + noTelp.getText() + "\"," +
                "\"user\":\"" + noPeserta.getText() + "\"" +
                "}" +
                "}" +
                "}";

            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
            metadata = root.path("metaData");

            System.out.println("code : " + metadata.path("code").asText());
            System.out.println("message : " + metadata.path("message").asText());
            JOptionPane.showMessageDialog(null, "Respon BPJS : " + metadata.path("message").asText());

            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                System.out.println("SEP berhasil terbit!");
                System.out.println("No. SEP: " + response.asText());

                String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", Valid.getTglSmc(tglSEP), noRM.getText(), kodePoliReg, kodeDokterReg);

                if (isNoRawat == null || (!isNoRawat.equals(noRawat))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + noRawat);
                    System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(tglSEP));
                    System.out.println("No. Antrian: " + noReg + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("Kode Dokter: " + kodeDokterReg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("Kode Poli: " + kodePoliReg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", noRawat) + ")");
                    System.out.println("======================================================");

                    return;
                }

                Sequel.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    noRawat,
                    Valid.getTglSmc(tglSEP),
                    Valid.SetTgl(tglRujukan.getSelectedItem().toString()),
                    noRujukan.getText(),
                    kodePPKRujukan.getText(),
                    namaPPKRujukan.getText(),
                    kodePPK.getText(),
                    namaPPK.getText(),
                    jenisPelayanan.getSelectedItem().toString().substring(0, 1),
                    catatan.getText(),
                    kodeDiagnosa.getText(),
                    namaDiagnosa.getText(),
                    kodePoli,
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
                    kodeDokter,
                    namaDokter.getText(),
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
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.getTglSmc(tglSEP), response.asText());
                }

                if (!prb.isBlank()) {
                    Sequel.menyimpanSmc("bpjs_prb", null, response.asText(), prb);

                    prb = "";
                }

                if (Sequel.cariIntegerSmc("select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    noRM.getText(), Valid.getTglSmc(tglSEP), kodeDokterReg, kodePoliReg
                ) == 1) {
                    Sequel.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", noRM.getText(), Valid.getTglSmc(tglSEP), kodeDokterReg, kodePoliReg);
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
    }

    private void cekFinger(String noka) {
        statusFinger = false;

        if (!noPeserta.getText().isBlank()) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = api.getUTCDateTimeAsString();
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                url = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + Valid.getTglSmc(tglSEP);
                entity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                metadata = root.path("metaData");
                System.out.println("kodecekstatus : " + metadata.path("code").asText());
                // System.out.println("message : "+metadata.path("message").asText());
                if (metadata.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    if (response.path("kode").asText().equals("1")) {
                        if (response.path("status").asText().contains(Sequel.cariIsi("select current_date()"))) {
                            statusFinger = true;
                        } else {
                            statusFinger = false;
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
        toggleInfoTambahan.setSelected(false);
        isForm();
        emptTeks();
        try {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("URL : " + url);
            if (metadata.path("code").asText().equals("200")) {
                asalRujukan.setSelectedIndex(0);
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
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
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                namaPasien.setText(response.path("peserta").path("nama").asText());
                noPeserta.setText(response.path("peserta").path("noKartu").asText());
                noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                nik.setText(response.path("peserta").path("nik").asText());
                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                jk.setText(response.path("peserta").path("sex").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                kodePoli = response.path("poliRujukan").path("kode").asText();
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                setNomorRegistrasi();
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(noTelpBPJS);
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + metadata.path("message").asText());
                JOptionPane.showMessageDialog(null, "Pesan Pencarian Rujukan FKTP : " + metadata.path("message").asText());
                try {
                    url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTimeAsString();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metadata = root.path("metaData");
                    if (metadata.path("code").asText().equals("200")) {
                        asalRujukan.setSelectedIndex(1);
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                        namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                        noRujukan.setText(response.path("noKunjungan").asText());
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
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
                        prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        namaPasien.setText(response.path("peserta").path("nama").asText());
                        noPeserta.setText(response.path("peserta").path("noKartu").asText());
                        noRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noPeserta.getText()));
                        nik.setText(response.path("peserta").path("nik").asText());
                        if (nik.getText().contains("null") || nik.getText().isBlank()) {
                            nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        jk.setText(response.path("peserta").path("sex").asText());
                        statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        kodePoli = response.path("poliRujukan").path("kode").asText();
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kodePoliReg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodeDokterReg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", kodeDokter);
                        noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                        if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                            noTelp.setText(noTelpBPJS);
                        }
                        kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                        asalRujukan.setSelectedIndex(1);
                        setNomorRegistrasi();
                        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
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
        try (PreparedStatement ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?")) {
            tentukanHari();
            ps.setString(1, kodePoliReg);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDokter = rs.getString("kd_dokter_bpjs");
                    namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilKunjunganBedaPoli(String noKartu) {
        toggleInfoTambahan.setSelected(false);
        isForm();
        emptTeks();
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        penunjang.setSelectedIndex(0);
        asesmenPelayanan.setSelectedIndex(1);
        try {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("URL : " + url);
            if (metadata.path("code").asText().equals("200")) {
                asalRujukan.setSelectedIndex(0);
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
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
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                namaPasien.setText(response.path("peserta").path("nama").asText());
                noPeserta.setText(response.path("peserta").path("noKartu").asText());
                noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                nik.setText(response.path("peserta").path("nik").asText());
                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                jk.setText(response.path("peserta").path("sex").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                kodePoli = response.path("poliRujukan").path("kode").asText();
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                setNomorRegistrasi();
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(noTelpBPJS);
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + metadata.path("message").asText());
                try {
                    url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTimeAsString();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metadata = root.path("metaData");
                    if (metadata.path("code").asText().equals("200")) {
                        asalRujukan.setSelectedIndex(1);
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                        namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                        noRujukan.setText(response.path("noKunjungan").asText());
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
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
                        prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        namaPasien.setText(response.path("peserta").path("nama").asText());
                        noPeserta.setText(response.path("peserta").path("noKartu").asText());
                        noRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noPeserta.getText()));
                        nik.setText(response.path("peserta").path("nik").asText());
                        if (nik.getText().contains("null") || nik.getText().isBlank()) {
                            nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        jk.setText(response.path("peserta").path("sex").asText());
                        statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        kodePoli = response.path("poliRujukan").path("kode").asText();
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", kodeDokter);
                        noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                        if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                            noTelp.setText(noTelpBPJS);
                        }
                        kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                        asalRujukan.setSelectedIndex(1);
                        setNomorRegistrasi();
                        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
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
        try (PreparedStatement ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?")) {
            tentukanHari();
            ps.setString(1, kodePoliReg);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDokter = rs.getString("kd_dokter_bpjs");
                    namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilKontrol(String noSurat) {
        toggleInfoTambahan.setSelected(false);
        isForm();
        emptTeks();
        try (PreparedStatement pskontrol = koneksi.prepareStatement(
            "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_kartu, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat " +
            "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
        )) {
            pskontrol.setString(1, noSurat);
            try (ResultSet rskontrol = pskontrol.executeQuery()) {
                if (rskontrol.next()) {
                    if (!rskontrol.getString("tgl_rencana").equals(Valid.getTglSmc(tglSEP))) {
                        updateSuratKontrol(rskontrol.getString("no_surat"), rskontrol.getString("no_sep"), rskontrol.getString("no_kartu"), Valid.getTglSmc(tglSEP),
                            rskontrol.getString("kd_dokter_bpjs"), rskontrol.getString("nm_dokter_bpjs"), rskontrol.getString("kd_poli_bpjs"), rskontrol.getString("nm_poli_bpjs")
                        );
                    }
                    if (rskontrol.getString("jnspelayanan").equals("1")) {
                        try {
                            url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rskontrol.getString("no_kartu") + "/tglSEP/" + Valid.getTglSmc(tglSEP);
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println("URL : " + url);
                            if (metadata.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                                kodeDiagnosa.setText("Z09.8");
                                namaDiagnosa.setText("Follow-up examination after other treatment for other conditions");
                                noRujukan.setText(rskontrol.getString("no_sep"));
                                tujuanKunjungan.setSelectedIndex(0);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(0);
                                asalRujukan.setSelectedIndex(1);
                                kodePoli = rskontrol.getString("kd_poli_bpjs");
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodeDokter = rskontrol.getString("kd_dokter_bpjs");
                                namaDokter.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDPJPLayanan.setText(kodeDokter);
                                namaDPJPLayanan.setText(namaDokter.getText());
                                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
                                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                                noSKDP.setText(rskontrol.getString("no_surat"));
                                switch (rskontrol.getString("klsrawat")) {
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
                                noRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noPeserta.getText()));
                                nik.setText(response.path("nik").asText());
                                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                }
                                jk.setText(response.path("sex").asText());
                                statusPeserta.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                                tglLahir.setText(response.path("tglLahir").asText());
                                jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                                kodePPKRujukan.setText(Sequel.cariIsiSmc("select kode_ppk from setting"));
                                namaPPKRujukan.setText(Sequel.cariIsiSmc("select nama_instansi from setting"));
                                setNomorRegistrasi();
                                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                noTelpBPJS = response.path("mr").path("noTelepon").asText();
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(noTelpBPJS);
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
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                            } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                            }
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println("URL : " + url);
                            if (metadata.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                noRujukan.setText(response.path("noKunjungan").asText());
                                noSKDP.setText(rskontrol.getString("no_surat"));
                                kodePoli = rskontrol.getString("kd_poli_bpjs");
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodeDokter = rskontrol.getString("kd_dokter_bpjs");
                                namaDokter.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDPJPLayanan.setText(kodeDokter);
                                namaDPJPLayanan.setText(namaDokter.getText());
                                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
                                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                                tujuanKunjungan.setSelectedIndex(2);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(5);
                                if (rskontrol.getString("asal_rujukan").equals("2")) {
                                    asalRujukan.setSelectedIndex(1);
                                } else {
                                    asalRujukan.setSelectedIndex(0);
                                }
                                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
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
                                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText();
                                if (prb.contains("null")) {
                                    prb = "";
                                }
                                namaPasien.setText(response.path("peserta").path("nama").asText());
                                noPeserta.setText(response.path("peserta").path("noKartu").asText());
                                noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                                nik.setText(response.path("peserta").path("nik").asText());
                                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                }
                                jk.setText(response.path("peserta").path("sex").asText());
                                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                                setNomorRegistrasi();
                                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(noTelpBPJS);
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

    public void tampilMobileJKN(String noKartu) {
        toggleInfoTambahan.setSelected(false);
        isForm();
        emptTeks();
        isMobileJKN = true;
        try (PreparedStatement psjkn = koneksi.prepareStatement(
            "select referensi_mobilejkn_bpjs.*, maping_poli_bpjs.nm_poli_bpjs, maping_poli_bpjs.kd_poli_rs, maping_dokter_dpjpvclaim.nm_dokter_bpjs, maping_dokter_dpjpvclaim.kd_dokter from referensi_mobilejkn_bpjs " +
            "join maping_poli_bpjs on referensi_mobilejkn_bpjs.kodepoli = maping_poli_bpjs.kd_poli_bpjs join maping_dokter_dpjpvclaim on referensi_mobilejkn_bpjs.kodedokter = maping_dokter_dpjpvclaim.kd_dokter_bpjs " +
            "where referensi_mobilejkn_bpjs.nomorkartu = ? and referensi_mobilejkn_bpjs.tanggalperiksa = current_date() and referensi_mobilejkn_bpjs.status in ('Belum', 'Checkin') and tanggalperiksa = current_date() " +
            "and not exists(select * from bridging_sep where bridging_sep.no_rawat = referensi_mobilejkn_bpjs.no_rawat)"
        )) {
            psjkn.setString(1, noKartu);
            try (ResultSet rsjkn = psjkn.executeQuery()) {
                if (rsjkn.next()) {
                    noBooking = rsjkn.getString("nobooking");
                    jenisKunjungan = rsjkn.getString("jeniskunjungan").substring(0, 1);
                    noRawat = rsjkn.getString("no_rawat");
                    kodePoli = rsjkn.getString("kodepoli");
                    namaPoli.setText(rsjkn.getString("nm_poli_bpjs"));
                    kodePoliReg = rsjkn.getString("kd_poli_rs");
                    kodeDokter = rsjkn.getString("kodedokter");
                    namaDokter.setText(rsjkn.getString("nm_dokter_bpjs"));
                    kodeDokterReg = rsjkn.getString("kd_dokter");
                    kodeDPJPLayanan.setText(kodeDokter);
                    namaDPJPLayanan.setText(namaDokter.getText());
                    noPeserta.setText(rsjkn.getString("nomorkartu"));
                    noRM.setText(rsjkn.getString("norm"));
                    nik.setText(rsjkn.getString("nik"));
                    noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                        noTelp.setText(rsjkn.getString("nohp"));
                    }
                    // CEK STATUS PASIEN
                    try {
                        url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rsjkn.getString("nomorkartu") + "/tglSEP/" + Valid.getTglSmc(tglSEP);
                        utc = api.getUTCDateTimeAsString();
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                        headers.add("X-Timestamp", utc);
                        headers.add("X-Signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                        entity = new HttpEntity(headers);
                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("URL : " + url);
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
                            switch (jenisKunjungan) {
                                case "1":
                                    // RUJUKAN FKTP
                                    asalRujukan.setSelectedIndex(0);
                                    try {
                                        url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rsjkn.getString("nomorreferensi");
                                        System.out.println("URL : " + url);
                                        utc = api.getUTCDateTimeAsString();
                                        headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                        headers.add("X-Timestamp", utc);
                                        headers.add("X-Signature", api.getHmac(utc));
                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                        entity = new HttpEntity(headers);
                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
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
                                            JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
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
                                        url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rsjkn.getString("nomorreferensi");
                                        System.out.println("URL : " + url);
                                        utc = api.getUTCDateTimeAsString();
                                        headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                        headers.add("X-Timestamp", utc);
                                        headers.add("X-Signature", api.getHmac(utc));
                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                        entity = new HttpEntity(headers);
                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
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
                                            JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
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
                                                    updateSuratKontrol(
                                                        rskontrol.getString("no_surat"), rskontrol.getString("no_sep"), rsjkn.getString("nomorkartu"), Valid.getTglSmc(tglSEP),
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
                                                    kodePPKRujukan.setText(kodePPK.getText());
                                                    namaPPKRujukan.setText(namaPPK.getText());
                                                } else {
                                                    // KONTROL POLI
                                                    try {
                                                        if (rskontrol.getString("asal_rujukan").equals("1")) {
                                                            url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                                            asalRujukan.setSelectedIndex(0);
                                                        } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                                            url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                                                            asalRujukan.setSelectedIndex(1);
                                                        }
                                                        utc = api.getUTCDateTimeAsString();
                                                        headers = new HttpHeaders();
                                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                                        headers.add("X-Timestamp", utc);
                                                        headers.add("X-Signature", api.getHmac(utc));
                                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                                        entity = new HttpEntity(headers);
                                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                                        metadata = root.path("metaData");
                                                        System.out.println("URL : " + url);
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
                                                            JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
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
                                                emptTeks();
                                                JOptionPane.showMessageDialog(null, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                            }
                                        }
                                    } catch (Exception e) {
                                        emptTeks();
                                        JOptionPane.showMessageDialog(null, "Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    }
                                    break;
                                default:
                                    emptTeks();
                                    JOptionPane.showMessageDialog(null, "Maaf, antrian JKN tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    break;
                            }
                        } else {
                            emptTeks();
                            System.out.println("Notif : " + metadata.asText());
                            JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
                        }
                    } catch (Exception e) {
                        emptTeks();
                        System.out.println("Notif : " + e);
                        if (e.toString().contains("UnknownHostException")) {
                            JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                        }
                    }
                } else {
                    emptTeks();
                    JOptionPane.showMessageDialog(null, "Maaf, pasien membatalkan antrian MobileJKN, atau telah menerima pelayanan!\nSilahkan hubungi administrasi.");
                }
            }
        } catch (Exception e) {
            emptTeks();
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Maaf, terjadi kesalahan pada saat mencari rujukan di MobileJKN!\nSilahkan hubungi administrasi.");
        }
    }

    private boolean kirimAntrianOnsite() {
        boolean sukses = true;

        if (!ADDANTRIANAPIMOBILEJKN) {
            return sukses;
        }

        if (isMobileJKN) {
            if (Sequel.cariExistsSmc("select * from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", noBooking)) {
                Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "referensi_mobilejkn_bpjs.validasi = now(), referensi_mobilejkn_bpjs.status = 'Checkin'", "referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", noBooking);
                Sequel.mengupdateSmc("reg_periksa", "reg_periksa.jam_reg = current_time()", "reg_periksa.no_rawat = ? and stts != 'Batal'", noRawat);
            }
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select referensi_mobilejkn_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, poliklinik.nm_poli, dokter.nm_dokter from referensi_mobilejkn_bpjs " +
                "join reg_periksa on referensi_mobilejkn_bpjs.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli join dokter on reg_periksa.kd_dokter = dokter.kd_dokter " +
                "where referensi_mobilejkn_bpjs.statuskirim = 'Belum' and referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn.status = 'Checkin'"
            )) {
                ps.setString(1, noBooking);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            utc = String.valueOf(api.getUTCDateTime());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            json = "{" +
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
                            System.out.println("JSON : " + json);
                            entity = new HttpEntity(json, headers);
                            url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL : " + url);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                            metadata = root.path("metadata");
                            Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", json, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                            if (metadata.path("code").asText().equals("200") || metadata.path("code").asText().equals("208") || metadata.path("message").asText().equals("Ok")) {
                                Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "statuskirim = 'Sudah'", "nobooking = ?", rs.getString("nobooking"));
                            } else {
                                sukses = false;
                            }
                            System.out.println("respon WS BPJS : " + metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                        } catch (HttpClientErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        } catch (HttpServerErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        } catch (Exception e) {
                            sukses = false;
                            System.out.println("Notif : " + e);
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif Ketersediaan : " + e);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } else {
            int angkaantrean = Integer.parseInt(noReg), kuota = 0;
            jenisKunjungan = "1";
            String nomorreferensi = noRujukan.getText();
            String jamMulai = "", jamPraktek = "";
            if ((!noRujukan.getText().isBlank()) || (!noSKDP.getText().isBlank())) {
                if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().isBlank()) {
                    if (asalRujukan.getSelectedIndex() == 0) {
                        jenisKunjungan = "1";
                        nomorreferensi = noRujukan.getText();
                    } else {
                        if (!noSKDP.getText().isBlank()) {
                            jenisKunjungan = "3";
                            nomorreferensi = noSKDP.getText();
                        } else {
                            jenisKunjungan = "4";
                            nomorreferensi = noRujukan.getText();
                        }
                    }
                } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                    jenisKunjungan = "3";
                    nomorreferensi = noSKDP.getText();
                } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().trim().equals("4. Atas Instruksi RS")) {
                    jenisKunjungan = "2";
                    nomorreferensi = noRujukan.getText();
                } else {
                    if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                        jenisKunjungan = "3";
                        nomorreferensi = noSKDP.getText();
                    } else {
                        jenisKunjungan = "2";
                        nomorreferensi = noRujukan.getText();
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
                        ps.setString(2, kodePoliReg);
                        ps.setString(3, kodeDokterReg);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                jamPraktek = rs.getString("jam_mulai").substring(0, 5) + "-" + rs.getString("jam_selesai").substring(0, 5);
                                jamMulai = rs.getString("jam_mulai");
                                kuota = rs.getInt("kuota");
                            } else {
                                sukses = false;
                                System.out.println("Jadwal praktek tidak ditemukan...!!!");
                                JOptionPane.showMessageDialog(null, "Jadwal praktek tidak ditemukan...!!!");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                        sukses = false;
                    }

                    if (sukses) {
                        datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", Valid.getTglSmc(tglSEP), jamMulai, String.valueOf(angkaantrean * 5));
                        parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datajam);
                        if (!jenisKunjungan.isBlank() && !nomorreferensi.isBlank()) {
                            json = "{" +
                                "\"kodebooking\": \"" + noRawat + "\"," +
                                "\"jenispasien\": \"JKN\"," +
                                "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                                "\"nik\": \"" + nik.getText() + "\"," +
                                "\"nohp\": \"" + noTelp.getText().trim() + "\"," +
                                "\"kodepoli\": \"" + kodePoli + "\"," +
                                "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                                "\"pasienbaru\": 0," +
                                "\"norm\": \"" + noRM.getText() + "\"," +
                                "\"tanggalperiksa\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                                "\"kodedokter\": " + kodeDokter + "," +
                                "\"namadokter\": \"" + namaDokter.getText() + "\"," +
                                "\"jampraktek\": \"" + jamPraktek + "\"," +
                                "\"jeniskunjungan\": " + jenisKunjungan + "," +
                                "\"nomorreferensi\": \"" + nomorreferensi + "\"," +
                                "\"nomorantrean\": \"" + noReg + "\"," +
                                "\"angkaantrean\": " + angkaantrean + "," +
                                "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                                "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                                "\"kuotajkn\": " + kuota + "," +
                                "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                                "\"kuotanonjkn\": " + kuota + "," +
                                "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                "}";
                            System.out.println("JSON : " + json);
                            url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL : " + url);
                            System.out.print("addantrean " + noRawat + " : ");
                            try {
                                utc = api.getUTCDateTimeAsString();
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                                headers.add("x-timestamp", utc);
                                headers.add("x-signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                                entity = new HttpEntity(json, headers);
                                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                metadata = root.path("metadata");
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                                System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                                if (!metadata.path("code").asText().equals("200")) {
                                    sukses = false;
                                }
                            } catch (HttpClientErrorException e) {
                                sukses = false;
                                System.out.println("Notif : " + e.getMessage());
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                            } catch (HttpServerErrorException e) {
                                sukses = false;
                                System.out.println("Notif : " + e.getMessage());
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                            } catch (Exception e) {
                                sukses = false;
                                System.out.println("Notif : " + e);
                            }
                        }
                    }
                    if (!sukses) {
                        sukses = true;
                        json = "{" +
                            "\"kodebooking\": \"" + noRawat + "\"," +
                            "\"jenispasien\": \"JKN\"," +
                            "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                            "\"nik\": \"" + nik.getText() + "\"," +
                            "\"nohp\": \"" + noTelpBPJS + "\"," +
                            "\"kodepoli\": \"" + kodePoli + "\"," +
                            "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                            "\"pasienbaru\": 0," +
                            "\"norm\": \"" + noRM.getText() + "\"," +
                            "\"tanggalperiksa\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                            "\"kodedokter\": " + kodeDokter + "," +
                            "\"namadokter\": \"" + namaDokter.getText() + "\"," +
                            "\"jampraktek\": \"" + jamPraktek + "\"," +
                            "\"jeniskunjungan\": " + jenisKunjungan + "," +
                            "\"nomorreferensi\": \"" + nomorreferensi + "\"," +
                            "\"nomorantrean\": \"" + noReg + "\"," +
                            "\"angkaantrean\": " + angkaantrean + "," +
                            "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                            "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                            "\"kuotajkn\": " + kuota + "," +
                            "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                            "\"kuotanonjkn\": " + kuota + "," +
                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                            "}";
                        System.out.println("JSON : " + json);
                        url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL : " + url);
                        System.out.print("addantrean " + noRawat + " : ");
                        try {
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            entity = new HttpEntity(json, headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                            metadata = root.path("metadata");
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText() + "\n");
                            if (!metadata.path("code").asText().equals("200")) {
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                sukses = false;
                            }
                        } catch (HttpClientErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        } catch (HttpServerErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", json, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        } catch (Exception e) {
                            sukses = false;
                            System.out.println("Notif : " + e);
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas");
                        }
                    }
                } catch (Exception e) {
                    sukses = false;
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas");
                }
            }
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
        kodePoli = "";
        namaPoli.setText("");
        kodeDokter = "";
        namaDokter.setText("");
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
        jenisPelayananItemStateChanged(null);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        flagProsedur.setEnabled(false);
        penunjang.setSelectedIndex(0);
        penunjang.setEnabled(false);
        asesmenPelayanan.setSelectedIndex(0);
        asesmenPelayanan.setEnabled(true);
        kodeDPJPLayanan.setText("");
        namaDPJPLayanan.setText("");
        barcode.setText(String.valueOf(koneksiDB.PRINTJUMLAHBARCODE()));
        lakaLantas.setSelectedIndex(0);
        lakaLantasItemStateChanged(null);
        suplesi.setSelectedIndex(0);
        suplesiItemStateChanged(null);
        kdPropKLL.setText("");
        nmPropKLL.setText("");
        kdKabKLL.setText("");
        nmKabKLL.setText("");
        kdKecKLL.setText("");
        nmKecKLL.setText("");
        catatan.setText("Anjungan Pasien Mandiri " + namaPPK.getText());

        json = "";
        noReg = "";
        noRawat = "";
        kodeDokterReg = "";
        kodePoliReg = "";
        namaPJ = "";
        alamatPJ = "";
        hubunganPJ = "";
        biayaReg = "";
        statusDaftar = "Baru";
        umurDaftar = "0";
        statusUmur = "Hr";
        statusPoli = "Baru";
        noTelpBPJS = "";
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
                    DlgRegistrasiSEPBPJS.this.aplikasiAktif = true;
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
                    DlgRegistrasiSEPBPJS.this.fristaAktif = true;
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

    private void updateSuratKontrol(String noSKDP, String noSEP, String tglKontrol, String noKartuPeserta) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");

            return;
        }

        String kodePoliKontrol = Sequel.cariIsiSmc("select kd_poli_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
            namaPoliKontrol = Sequel.cariIsiSmc("select nm_poli_bpjs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoliKontrol),
            kodeDokterKontrol = Sequel.cariIsiSmc("select kd_dokter_bpjs from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP),
            namaDokterKontrol = Sequel.cariIsiSmc("select nm_dokter_bpjs from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokterKontrol);

        try {
            utc = api.getUTCDateTimeAsString();

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";

            json = "{" +
                "\"request\": {" +
                "\"noSuratKontrol\":\"" + noSKDP + "\"," +
                "\"noSEP\":\"" + noSEP + "\"," +
                "\"kodeDokter\":\"" + kodeDokterKontrol + "\"," +
                "\"poliKontrol\":\"" + kodePoliKontrol + "\"," +
                "\"tglRencanaKontrol\":\"" + tglKontrol + "\"," +
                "\"user\":\"" + noKartuPeserta + "\"" +
                "}" +
                "}";

            System.out.println("JSON : " + json);

            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("code : " + metadata.path("code").asText());
            System.out.println("message : " + metadata.path("message").asText());

            if (metadata.path("code").asText().equals("200")) {
                System.out.println("Respon BPJS : " + metadata.path("message").asText());

                Sequel.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?",
                    "no_surat = ?",
                    tglKontrol, kodeDokterKontrol, namaDokterKontrol, kodePoliKontrol, namaPoliKontrol,
                    noSKDP
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

    private void updateSuratKontrol(String noSKDP, String noSEP, String noKartu, String tanggalPeriksa, String kodeDPJP, String namaDPJP, String kodePoli, String namaPoli) {
        if (noSKDP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Maaf, data surat kontrol tidak ditemukan...!!\nSilahkan hubungi administrasi...!!");
            return;
        }
        try {
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
            json = "{" +
                "\"request\": {" +
                "\"noSuratKontrol\":\"" + noSKDP + "\"," +
                "\"noSEP\":\"" + noSEP + "\"," +
                "\"kodeDokter\":\"" + kodeDPJP + "\"," +
                "\"poliKontrol\":\"" + kodePoli + "\"," +
                "\"tglRencanaKontrol\":\"" + tanggalPeriksa + "\"," +
                "\"user\":\"" + noKartu + "\"" +
                "}" +
                "}";
            System.out.println("JSON : " + json);
            entity = new HttpEntity(json, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, entity, String.class).getBody());
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
        int next = 0, retries = 5;
        boolean sukses = false;

        isCekPasien();

        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", noRM.getText(), Valid.getTglSmc(tglSEP), kodePoliReg, kodeDokterReg, kdpjBPJS)) {
            JOptionPane.showMessageDialog(null, "Maaf, Telah terdaftar pemeriksaan hari ini\nSilahkan hubungi bagian pendaftaran..!!");
            return false;
        }

        biayaReg = Sequel.cariIsiSmc("select if(? = 'Lama', poliklinik.registrasi, poliklinik.registrasilama) from poliklinik where poliklinik.kd_poli = ?", statusPoli, kodePoliReg);

        do {
            setNomorRegistrasi();

            System.out.print("Mencoba mendaftarkan pasien dengan no. rawat [" + noRawat + "]: ");

            sukses = Sequel.menyimpantfSmc("reg_periksa", null,
                noReg, noRawat, Valid.getTglSmc(tglSEP), Sequel.cariIsiSmc("select current_time()"),
                kodeDokterReg, noRM.getText(), kodePoliReg, namaPJ, alamatPJ, hubunganPJ, biayaReg, "Belum",
                statusDaftar, "Ralan", kdpjBPJS, umurDaftar, statusUmur, "Belum Bayar", statusPoli
            );

            System.out.println(sukses ? "Sukses!" : "Gagal!");
        } while (next++ < retries && !sukses);

        if (sukses) {
            updateUmurPasien();
        }

        return sukses;
    }

    private boolean simpanRujukan() {
        int next = 0, retries = 5;
        boolean sukses = false;

        do {
            String noRujukMasuk = Sequel.cariIsiSmc("select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')", Valid.getTglSmc(tglSEP), Valid.getTglSmc(tglSEP));

            System.out.print("Mencoba memproses rujukan masuk pasien dengan no. surat [" + noRujukMasuk + "]: ");

            sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
                noRawat, namaPPKRujukan.getText(), "-", noRujukan.getText(), "0",
                namaPPKRujukan.getText(), kodeDiagnosa.getText(), "-", "-", noRujukMasuk
            );

            System.out.println(sukses ? "Sukses!" : "Gagal!");
        } while (next++ < retries && !sukses);

        return sukses;
    }

    private void updateUmurPasien() {
        Sequel.mengupdateSmc("pasien",
            "no_tlp = ?, no_ktp = ?, umur = concat(concat(concat(timestampdiff(year, tgl_lahir, curdate()), ' Th '), concat(timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12), ' Bl ')), concat(timestampdiff(day, date_add(date_add(tgl_lahir, interval timestampdiff(year, tgl_lahir, curdate()) year), interval timestampdiff(month, tgl_lahir, curdate()) - ((timestampdiff(month, tgl_lahir, curdate()) div 12) * 12) month), curdate()), ' Hr'))",
            "no_rkm_medis = ?",
            noTelp.getText(), nik.getText(), noRM.getText()
        );
    }

    private void resetAksi() {
        userAksi.setText("");
        passAksi.setText("");
        aksi = "";
    }

    private void isForm() {
        if (toggleInfoTambahan.isSelected()) {
            toggleInfoTambahan.setVisible(false);
            panelNumpad.setVisible(false);
            panelUtama.setPreferredSize(new Dimension(WIDTH, 70));
            panelTambahan.setPreferredSize(new Dimension(WIDTH, 540));
            form.setVisible(true);
            toggleInfoTambahan.setVisible(true);
        } else {
            toggleInfoTambahan.setVisible(false);
            panelUtama.setPreferredSize(new Dimension(WIDTH, 780));
            panelTambahan.setPreferredSize(new Dimension(WIDTH, 30));
            form.setVisible(false);
            toggleInfoTambahan.setVisible(true);
        }
    }
}
