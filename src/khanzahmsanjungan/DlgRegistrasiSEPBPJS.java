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

/**
 *
 * @author Kode
 */
public class DlgRegistrasiSEPBPJS extends widget.Dialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final BPJSCekReferensiDokterDPJP dokter;
    private final BPJSCekReferensiPenyakit penyakit;
    private final DlgCariPoliBPJS poli;
    private final BPJSCekRiwayatRujukanTerakhir rujukanterakhir;
    private final BPJSCekRiwayatPelayanan historiPelayanan;
    private final boolean ADDANTRIANAPIMOBILEJKN = koneksiDB.ADDANTRIANAPIMOBILEJKN();
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private String umur = "0",
        sttsumur = "Th",
        hari = "",
        kode_dokter = "",
        kode_poli = "",
        instansiNama,
        instansiAlamat,
        instansiKota,
        instansiPropinsi,
        instansiKontak,
        instansiEmail,
        kdkel = "",
        kdkec = "",
        kdkab = "",
        kdprop = "",
        nosisrute = "",
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
        jampraktek = "",
        kabupatenpj = "",
        hariawal = "",
        requestJson,
        URL = "",
        nosep = "",
        user = "",
        prb = "",
        peserta = "",
        noreg = "",
        norawat = "",
        kodedokterreg = "",
        kodepolireg = "",
        norujukmasuk = "",
        status = "Baru",
        utc = "",
        jeniskunjungan = "",
        aksi = "",
        nohppasien = "";
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

    public DlgRegistrasiSEPBPJS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        barcode.setDocument(new batasInput((byte) 3).getOnlyAngka(barcode));

        try (ResultSet rs = koneksi.createStatement().executeQuery("select kode_ppk, nama_instansi, alamat_instansi, kabupaten, propinsi, kontak, email from setting")) {
            if (rs.next()) {
                instansiNama = rs.getString("nama_instansi");
                instansiAlamat = rs.getString("alamat_instansi");
                instansiKota = rs.getString("kabupaten");
                instansiPropinsi = rs.getString("propinsi");
                instansiKontak = rs.getString("kontak");
                instansiEmail = rs.getString("email");
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

        rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(parent, modal);
        rujukanterakhir.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (rujukanterakhir.getTable().getSelectedRow() != -1) {
                    kodeDiagnosa.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 0).toString());
                    namaDiagnosa.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 1).toString());
                    noRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 2).toString());
                    kodePoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 3).toString());
                    namaPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 4).toString());
                    kodePPKRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 6).toString());
                    namaPPKRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 7).toString());
                    Valid.SetTgl(tglRujukan, rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 5).toString());
                    catatan.requestFocus();
                }
            }
        });

        historiPelayanan = new BPJSCekRiwayatPelayanan(parent, modal);
        historiPelayanan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (historiPelayanan.getTable().getSelectedRow() != -1) {
                    if ((historiPelayanan.getTable().getSelectedColumn() == 6) || (historiPelayanan.getTable().getSelectedColumn() == 7)) {
                        noRujukan.setText(historiPelayanan.getTable().getValueAt(historiPelayanan.getTable().getSelectedRow(), historiPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                noRujukan.requestFocus();
            }
        });

        emptTeks();
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

        NoReg = new widget.TextField();
        Biaya = new widget.TextField();
        TAlmt = new widget.Label();
        TPngJwb = new widget.Label();
        THbngn = new widget.Label();
        TBiaya = new widget.TextField();
        Kdpnj = new widget.TextField();
        nmpnj = new widget.TextField();
        TNoRw = new widget.TextField();
        NoRujukMasuk = new widget.TextField();
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
        panelNumpad1 = new widget.Numpad();
        panel2 = new widget.Panel();
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
        jLabel38 = new widget.Label();
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
        panelBawah = new widget.Panel();
        btnKonfirmasi = new widget.Button();
        btnBatal = new widget.Button();

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
        label1.setPreferredSize(new java.awt.Dimension(60, 30));
        panelTengahAksi.add(label1);
        label1.setBounds(0, 30, 110, 30);

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setFont(new java.awt.Font("Inter Medium", 0, 18)); // NOI18N
        label2.setPreferredSize(new java.awt.Dimension(60, 30));
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

        panelAtas.setMinimumSize(new java.awt.Dimension(390, 40));
        panelAtas.setPreferredSize(new java.awt.Dimension(400, 40));
        panelAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 10));

        label4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label4.setText("DATA ELIGIBILITAS PESERTA JKN");
        label4.setFocusable(false);
        label4.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        panelAtas.add(label4);

        getContentPane().add(panelAtas, java.awt.BorderLayout.PAGE_START);

        panelTengah.setPreferredSize(new java.awt.Dimension(390, 290));
        panelTengah.setLayout(new java.awt.BorderLayout());

        panel1.setMinimumSize(new java.awt.Dimension(533, 290));
        panel1.setPreferredSize(new java.awt.Dimension(533, 310));
        panel1.setLayout(null);

        namaPasien.setEditable(false);
        namaPasien.setFocusable(false);
        panel1.add(namaPasien);
        namaPasien.setBounds(345, 10, 685, 30);

        noRM.setEditable(false);
        noRM.setFocusable(false);
        noRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noRMActionPerformed(evt);
            }
        });
        panel1.add(noRM);
        noRM.setBounds(230, 10, 110, 30);

        noPeserta.setEditable(false);
        noPeserta.setFocusable(false);
        panel1.add(noPeserta);
        noPeserta.setBounds(730, 100, 300, 30);

        jLabel20.setText("Tgl. SEP :");
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel20);
        jLabel20.setBounds(625, 190, 100, 30);

        tglSEP.setEditable(false);
        tglSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglSEP.setFocusable(false);
        tglSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        tglSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglSEPKeyPressed(evt);
            }
        });
        panel1.add(tglSEP);
        tglSEP.setBounds(730, 190, 170, 30);

        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel22);
        jLabel22.setBounds(625, 160, 100, 30);

        tglRujukan.setEditable(false);
        tglRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglRujukan.setFocusable(false);
        tglRujukan.setPreferredSize(new java.awt.Dimension(95, 23));
        tglRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglRujukanKeyPressed(evt);
            }
        });
        panel1.add(tglRujukan);
        tglRujukan.setBounds(730, 160, 170, 30);

        jLabel23.setText("No. SKDP / Surat Kontrol :");
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel23);
        jLabel23.setBounds(75, 70, 150, 30);

        noRujukan.setEditable(false);
        noRujukan.setFocusable(false);
        noRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noRujukanKeyPressed(evt);
            }
        });
        panel1.add(noRujukan);
        noRujukan.setBounds(230, 100, 340, 30);

        jLabel10.setText("PPK Rujukan :");
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel10);
        jLabel10.setBounds(75, 130, 150, 30);

        kodePPKRujukan.setEditable(false);
        kodePPKRujukan.setFocusable(false);
        panel1.add(kodePPKRujukan);
        kodePPKRujukan.setBounds(230, 130, 75, 30);

        namaPPKRujukan.setEditable(false);
        namaPPKRujukan.setFocusable(false);
        panel1.add(namaPPKRujukan);
        namaPPKRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel11);
        jLabel11.setBounds(75, 160, 150, 30);

        kodeDiagnosa.setEditable(false);
        kodeDiagnosa.setFocusable(false);
        panel1.add(kodeDiagnosa);
        kodeDiagnosa.setBounds(230, 160, 75, 30);

        namaDiagnosa.setEditable(false);
        namaDiagnosa.setFocusable(false);
        panel1.add(namaDiagnosa);
        namaDiagnosa.setBounds(310, 160, 260, 30);

        namaPoli.setEditable(false);
        namaPoli.setFocusable(false);
        panel1.add(namaPoli);
        namaPoli.setBounds(310, 190, 260, 30);

        kodePoli.setEditable(false);
        kodePoli.setFocusable(false);
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
        kelas.setFocusable(false);
        kelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kelasKeyPressed(evt);
            }
        });
        panel1.add(kelas);
        kelas.setBounds(230, 250, 150, 30);

        jLabel8.setText("Data Pasien :");
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel8);
        jLabel8.setBounds(75, 10, 150, 30);

        tglLahir.setEditable(false);
        tglLahir.setFocusable(false);
        panel1.add(tglLahir);
        tglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setText("L / P :");
        panel1.add(jLabel18);
        jLabel18.setBounds(910, 40, 35, 30);

        jk.setEditable(false);
        jk.setFocusable(false);
        panel1.add(jk);
        jk.setBounds(950, 40, 80, 30);

        jLabel24.setText("Jenis Peserta :");
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel24);
        jLabel24.setBounds(625, 40, 100, 30);

        jenisPeserta.setEditable(false);
        jenisPeserta.setFocusable(false);
        panel1.add(jenisPeserta);
        jenisPeserta.setBounds(730, 40, 173, 30);

        jLabel25.setText("Status :");
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel25);
        jLabel25.setBounds(365, 40, 50, 30);

        statusPeserta.setEditable(false);
        statusPeserta.setFocusable(false);
        panel1.add(statusPeserta);
        statusPeserta.setBounds(420, 40, 150, 30);

        jLabel27.setText("Asal Rujukan :");
        panel1.add(jLabel27);
        jLabel27.setBounds(625, 130, 100, 30);

        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        asalRujukan.setFocusable(false);
        asalRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                asalRujukanKeyPressed(evt);
            }
        });
        panel1.add(asalRujukan);
        asalRujukan.setBounds(730, 130, 170, 30);

        noTelp.setFocusable(false);
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
        noTelp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noTelpKeyPressed(evt);
            }
        });
        panel1.add(noTelp);
        noTelp.setBounds(730, 250, 170, 30);

        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setFocusable(false);
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        katarak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                katarakKeyPressed(evt);
            }
        });
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
        kodeDPJP.setFocusable(false);
        panel1.add(kodeDPJP);
        kodeDPJP.setBounds(230, 220, 75, 30);

        namaDPJP.setEditable(false);
        namaDPJP.setFocusable(false);
        panel1.add(namaDPJP);
        namaDPJP.setBounds(310, 220, 260, 30);

        cariDPJP.setBackground(new java.awt.Color(240, 249, 255));
        cariDPJP.setBorder(null);
        cariDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDPJP.setFocusable(false);
        cariDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDPJPActionPerformed(evt);
            }
        });
        cariDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDPJPKeyPressed(evt);
            }
        });
        panel1.add(cariDPJP);
        cariDPJP.setBounds(575, 220, 40, 30);

        jLabel56.setText("No. Telp :");
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel56);
        jLabel56.setBounds(625, 250, 100, 30);

        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel12);
        jLabel12.setBounds(75, 40, 150, 30);

        jLabel6.setText("NIK :");
        panel1.add(jLabel6);
        jLabel6.setBounds(625, 70, 100, 30);

        noSKDP.setEditable(false);
        noSKDP.setFocusable(false);
        noSKDP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noSKDPKeyPressed(evt);
            }
        });
        panel1.add(noSKDP);
        noSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setText("No. Rujukan :");
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        panel1.add(jLabel26);
        jLabel26.setBounds(75, 100, 150, 30);

        nik.setEditable(false);
        nik.setFocusable(false);
        panel1.add(nik);
        nik.setBounds(730, 70, 300, 30);

        jLabel7.setText("No. Peserta :");
        panel1.add(jLabel7);
        jLabel7.setBounds(625, 100, 100, 30);

        cariPoli.setBackground(new java.awt.Color(240, 249, 255));
        cariPoli.setBorder(null);
        cariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariPoli.setFocusable(false);
        cariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariPoliActionPerformed(evt);
            }
        });
        cariPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariPoliKeyPressed(evt);
            }
        });
        panel1.add(cariPoli);
        cariPoli.setBounds(575, 190, 40, 30);

        cariDiagnosa.setBackground(new java.awt.Color(240, 249, 255));
        cariDiagnosa.setBorder(null);
        cariDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDiagnosa.setFocusable(false);
        cariDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDiagnosaActionPerformed(evt);
            }
        });
        cariDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDiagnosaKeyPressed(evt);
            }
        });
        panel1.add(cariDiagnosa);
        cariDiagnosa.setBounds(575, 160, 40, 30);

        cariNoRujukan.setBackground(new java.awt.Color(240, 249, 255));
        cariNoRujukan.setBorder(null);
        cariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariNoRujukan.setFocusable(false);
        cariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariNoRujukanActionPerformed(evt);
            }
        });
        cariNoRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariNoRujukanKeyPressed(evt);
            }
        });
        panel1.add(cariNoRujukan);
        cariNoRujukan.setBounds(575, 100, 40, 30);

        btnRiwayatPelayanan.setBackground(new java.awt.Color(255, 255, 255));
        btnRiwayatPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        btnRiwayatPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayanan.setText("Riwayat Layanan BPJS");
        btnRiwayatPelayanan.setFocusable(false);
        btnRiwayatPelayanan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRiwayatPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananActionPerformed(evt);
            }
        });
        btnRiwayatPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRiwayatPelayananKeyPressed(evt);
            }
        });
        panel1.add(btnRiwayatPelayanan);
        btnRiwayatPelayanan.setBounds(980, 220, 220, 30);

        btnFingerprint.setBackground(new java.awt.Color(255, 255, 255));
        btnFingerprint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setText("Fingerprint");
        btnFingerprint.setFocusable(false);
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
        panel1.add(btnFingerprint);
        btnFingerprint.setBounds(1080, 100, 120, 80);

        btnFrista.setBackground(new java.awt.Color(255, 255, 255));
        btnFrista.setForeground(new java.awt.Color(0, 131, 62));
        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-scan.png"))); // NOI18N
        btnFrista.setText("FRISTA");
        btnFrista.setFocusable(false);
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
        panel1.add(btnFrista);
        btnFrista.setBounds(1080, 10, 120, 80);

        panelNumpad1.setFontSize(30);
        panelNumpad1.setTextBox(noTelp);
        panel1.add(panelNumpad1);
        panelNumpad1.setBounds(730, 290, 210, 280);

        panelTengah.add(panel1, java.awt.BorderLayout.PAGE_START);

        panel2.setOpaque(false);
        panel2.setLayout(new java.awt.BorderLayout());

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
        panel2.add(toggleInfoTambahan, java.awt.BorderLayout.PAGE_END);

        form.setPreferredSize(new java.awt.Dimension(533, 120));
        form.setLayout(null);

        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel13);
        jLabel13.setBounds(75, 10, 150, 30);

        jenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        jenisPelayanan.setSelectedIndex(1);
        jenisPelayanan.setFocusable(false);
        jenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jenisPelayananItemStateChanged(evt);
            }
        });
        jenisPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jenisPelayananKeyPressed(evt);
            }
        });
        form.add(jenisPelayanan);
        jenisPelayanan.setBounds(230, 10, 150, 30);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel42);
        jLabel42.setBounds(75, 40, 150, 30);

        tujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        tujuanKunjungan.setFocusable(false);
        tujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tujuanKunjunganItemStateChanged(evt);
            }
        });
        tujuanKunjungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tujuanKunjunganKeyPressed(evt);
            }
        });
        form.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(230, 40, 340, 30);

        jLabel43.setText("Flag Prosedur :");
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel43);
        jLabel43.setBounds(75, 70, 150, 30);

        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        flagProsedur.setFocusable(false);
        flagProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                flagProsedurKeyPressed(evt);
            }
        });
        form.add(flagProsedur);
        flagProsedur.setBounds(230, 70, 340, 30);

        jLabel44.setText("Penunjang :");
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel44);
        jLabel44.setBounds(75, 100, 150, 30);

        penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        penunjang.setEnabled(false);
        penunjang.setFocusable(false);
        penunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                penunjangKeyPressed(evt);
            }
        });
        form.add(penunjang);
        penunjang.setBounds(230, 100, 340, 30);

        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel45);
        jLabel45.setBounds(75, 130, 150, 30);

        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        asesmenPelayanan.setFocusable(false);
        asesmenPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                asesmenPelayananKeyPressed(evt);
            }
        });
        form.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(230, 130, 340, 30);

        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(LabelPoli7);
        LabelPoli7.setBounds(75, 160, 150, 30);

        kodeDPJPLayanan.setEditable(false);
        kodeDPJPLayanan.setFocusable(false);
        form.add(kodeDPJPLayanan);
        kodeDPJPLayanan.setBounds(230, 160, 75, 30);

        namaDPJPLayanan.setEditable(false);
        namaDPJPLayanan.setFocusable(false);
        form.add(namaDPJPLayanan);
        namaDPJPLayanan.setBounds(310, 160, 260, 30);

        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel9);
        jLabel9.setBounds(75, 190, 150, 30);

        kodePPK.setEditable(false);
        kodePPK.setFocusable(false);
        form.add(kodePPK);
        kodePPK.setBounds(230, 190, 75, 30);

        namaPPK.setEditable(false);
        namaPPK.setFocusable(false);
        form.add(namaPPK);
        namaPPK.setBounds(310, 190, 260, 30);

        jLabel55.setText("Laka Lantas :");
        form.add(jLabel55);
        jLabel55.setBounds(625, 10, 100, 30);

        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setFocusable(false);
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        lakaLantas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lakaLantasKeyPressed(evt);
            }
        });
        form.add(lakaLantas);
        lakaLantas.setBounds(730, 10, 170, 30);

        jLabel38.setText("Tgl. KLL :");
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel38);
        jLabel38.setBounds(625, 40, 100, 30);

        tglKLL.setEditable(false);
        tglKLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-07-2025" }));
        tglKLL.setEnabled(false);
        tglKLL.setFocusable(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        tglKLL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglKLLKeyPressed(evt);
            }
        });
        form.add(tglKLL);
        tglKLL.setBounds(730, 40, 170, 30);

        jLabel36.setText("Keterangan :");
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel36);
        jLabel36.setBounds(625, 70, 100, 30);

        keterangan.setEditable(false);
        keterangan.setFocusable(false);
        keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keteranganKeyPressed(evt);
            }
        });
        form.add(keterangan);
        keterangan.setBounds(730, 70, 300, 30);

        jLabel40.setText("Suplesi :");
        form.add(jLabel40);
        jLabel40.setBounds(625, 100, 100, 30);

        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setFocusable(false);
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                suplesiItemStateChanged(evt);
            }
        });
        form.add(suplesi);
        suplesi.setBounds(730, 100, 95, 30);

        jLabel41.setText("No. SEP :");
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        form.add(jLabel41);
        jLabel41.setBounds(830, 100, 55, 30);

        noSEPSuplesi.setEditable(false);
        noSEPSuplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noSEPSuplesiKeyPressed(evt);
            }
        });
        form.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(890, 100, 140, 30);

        LabelPoli3.setText("Propinsi KLL :");
        form.add(LabelPoli3);
        LabelPoli3.setBounds(625, 130, 100, 30);

        kdPropKLL.setEditable(false);
        kdPropKLL.setFocusable(false);
        form.add(kdPropKLL);
        kdPropKLL.setBounds(730, 130, 75, 30);

        nmPropKLL.setEditable(false);
        nmPropKLL.setFocusable(false);
        form.add(nmPropKLL);
        nmPropKLL.setBounds(810, 130, 220, 30);

        LabelPoli4.setText("Kabupaten KLL :");
        form.add(LabelPoli4);
        LabelPoli4.setBounds(625, 160, 100, 30);

        kdKabKLL.setEditable(false);
        kdKabKLL.setFocusable(false);
        form.add(kdKabKLL);
        kdKabKLL.setBounds(730, 160, 75, 30);

        nmKabKLL.setEditable(false);
        nmKabKLL.setFocusable(false);
        form.add(nmKabKLL);
        nmKabKLL.setBounds(810, 160, 220, 30);

        LabelPoli5.setText("Kecamatan KLL :");
        form.add(LabelPoli5);
        LabelPoli5.setBounds(625, 190, 100, 30);

        kdKecKLL.setEditable(false);
        kdKecKLL.setFocusable(false);
        form.add(kdKecKLL);
        kdKecKLL.setBounds(730, 190, 75, 30);

        nmKecKLL.setEditable(false);
        nmKecKLL.setFocusable(false);
        form.add(nmKecKLL);
        nmKecKLL.setBounds(810, 190, 220, 30);

        jLabel14.setText("Catatan :");
        form.add(jLabel14);
        jLabel14.setBounds(625, 220, 100, 30);

        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        catatan.setFocusable(false);
        catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                catatanKeyPressed(evt);
            }
        });
        form.add(catatan);
        catatan.setBounds(730, 220, 300, 30);

        btnApprovalFP.setBackground(new java.awt.Color(255, 255, 255));
        btnApprovalFP.setForeground(new java.awt.Color(0, 131, 62));
        btnApprovalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnApprovalFP.setText("Approval FP");
        btnApprovalFP.setFocusable(false);
        btnApprovalFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnApprovalFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
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
        btnApprovalFP.setBounds(1080, 190, 120, 90);

        btnPengajuanFP.setBackground(new java.awt.Color(255, 255, 255));
        btnPengajuanFP.setForeground(new java.awt.Color(0, 131, 62));
        btnPengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnPengajuanFP.setText("Pengajuan FP");
        btnPengajuanFP.setFocusable(false);
        btnPengajuanFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPengajuanFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
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
        btnPengajuanFP.setBounds(1080, 80, 120, 90);

        jLabel15.setText("Jumlah Barcode :");
        form.add(jLabel15);
        jLabel15.setBounds(75, 220, 150, 30);

        barcode.setText("3");
        barcode.setFocusable(false);
        barcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                barcodeKeyPressed(evt);
            }
        });
        form.add(barcode);
        barcode.setBounds(230, 220, 50, 30);

        panel2.add(form, java.awt.BorderLayout.CENTER);

        panelTengah.add(panel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelTengah, java.awt.BorderLayout.CENTER);

        panelBawah.setMinimumSize(new java.awt.Dimension(533, 120));
        panelBawah.setPreferredSize(new java.awt.Dimension(533, 100));

        btnKonfirmasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnKonfirmasi.setMnemonic('S');
        btnKonfirmasi.setText("KONFIRMASI");
        btnKonfirmasi.setToolTipText("Alt+S");
        btnKonfirmasi.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        btnKonfirmasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKonfirmasi.setPreferredSize(new java.awt.Dimension(300, 60));
        btnKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKonfirmasiActionPerformed(evt);
            }
        });
        btnKonfirmasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnKonfirmasiKeyPressed(evt);
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
        btnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnBatalKeyPressed(evt);
            }
        });
        panelBawah.add(btnBatal);

        getContentPane().add(panelBawah, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NoRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegActionPerformed

    private void NoRegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRegKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegKeyPressed

    private void BiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaActionPerformed

    private void BiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BiayaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaKeyPressed

    private void btnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnBatalActionPerformed(null);
        }
    }//GEN-LAST:event_btnBatalKeyPressed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnKonfirmasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKonfirmasiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnKonfirmasiActionPerformed(null);
        }
    }//GEN-LAST:event_btnKonfirmasiKeyPressed

    private void btnKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(noPeserta.getText());
        if (TNoRw.getText().trim().equals("") || namaPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (noPeserta.getText().trim().equals("")) {
            Valid.textKosong(noPeserta, "Nomor Kartu");
        } else if (Sequel.cariIntegerSmc("select count(*) from pasien where no_rkm_medis = ?", noRM.getText()) < 1) {
            JOptionPane.showMessageDialog(null, "Maaf, no RM tidak sesuai");
        } else if (kodePPKRujukan.getText().trim().equals("") || namaPPKRujukan.getText().trim().equals("")) {
            Valid.textKosong(kodePPKRujukan, "PPK Rujukan");
        } else if (kodePPK.getText().trim().equals("") || namaPPK.getText().trim().equals("")) {
            Valid.textKosong(kodePPK, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().trim().equals("") || namaDiagnosa.getText().trim().equals("")) {
            Valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().trim().equals("")) {
            Valid.textKosong(catatan, "Catatan");
        } else if ((jenisPelayanan.getSelectedIndex() == 1) && (kodePoli.getText().trim().equals("") || namaPoli.getText().trim().equals(""))) {
            Valid.textKosong(kodePoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().equals("")) {
            Valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDPJP.getText().trim().equals("") || namaDPJP.getText().trim().equals("")) {
            Valid.textKosong(kodeDPJP, "DPJP");
        } else if (!statusfinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17 && jenisPelayanan.getSelectedIndex() != 0 && !kodePoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(null, "Silahkan lakukan validasi biometrik dahulu..!!");
        } else {
            kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
            kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());

            isPoli();
            isCekPasien();
            isNumber();

            // cek apabila pasien sudah pernah diregistrasikan sebelumnya
            if (Sequel.cariIntegerSmc("select count(*) from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", noRM.getText(), Valid.getTglSmc(tglSEP), kodepolireg, kodedokterreg, Kdpnj.getText()) > 0) {
                JOptionPane.showMessageDialog(null, "Maaf, Telah terdaftar pemeriksaan hari ini. Mohon konfirmasi ke Bagian Admisi");
                emptTeks();
            } else {
                if (!registerPasien()) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat pendaftaran pasien!");
                    this.setCursor(Cursor.getDefaultCursor());

                    return;
                }

                if (jenisPelayanan.getSelectedIndex() == 0) {
                    insertSEP();
                } else if (jenisPelayanan.getSelectedIndex() == 1) {
                    if (namaPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan like '%darurat%'", no_peserta, jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(tglSEP)) >= 3) {
                            JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                                if (kirimAntrianOnsite()) {
                                    insertSEP();
                                }
                            }
                        }
                    } else if (!namaPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = ? and tglsep = ? and nmpolitujuan not like '%darurat%'", no_peserta, jenisPelayanan.getSelectedItem().toString().substring(0, 1), Valid.getTglSmc(tglSEP)) >= 1) {
                            JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
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

    private void cariDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDPJPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariDPJPKeyPressed

    private void cariDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDPJPActionPerformed
        dokter.setSize(getContentPane().getSize());
        dokter.setLocationRelativeTo(getContentPane());
        dokter.carinamadokter(kodePoli.getText(), namaPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_cariDPJPActionPerformed

    private void asesmenPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_asesmenPelayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_asesmenPelayananKeyPressed

    private void penunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_penunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_penunjangKeyPressed

    private void flagProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_flagProsedurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_flagProsedurKeyPressed

    private void tujuanKunjunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tujuanKunjunganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tujuanKunjunganKeyPressed

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

    private void noSEPSuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noSEPSuplesiKeyPressed

    }//GEN-LAST:event_noSEPSuplesiKeyPressed

    private void keteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keteranganKeyPressed
        Valid.pindah(evt, tglKLL, suplesi);
    }//GEN-LAST:event_keteranganKeyPressed

    private void tglKLLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglKLLKeyPressed
        Valid.pindah(evt, lakaLantas, keterangan);
    }//GEN-LAST:event_tglKLLKeyPressed

    private void katarakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_katarakKeyPressed
        Valid.pindah(evt, catatan, noTelp);
    }//GEN-LAST:event_katarakKeyPressed

    private void noTelpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noTelpKeyPressed
        Valid.pindah(evt, katarak, lakaLantas);
    }//GEN-LAST:event_noTelpKeyPressed

    private void asalRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_asalRujukanKeyPressed

    }//GEN-LAST:event_asalRujukanKeyPressed

    private void lakaLantasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lakaLantasKeyPressed
        Valid.pindah(evt, noTelp, tglKLL);
    }//GEN-LAST:event_lakaLantasKeyPressed

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

    private void kelasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kelasKeyPressed

    }//GEN-LAST:event_kelasKeyPressed

    private void jenisPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jenisPelayananKeyPressed

    }//GEN-LAST:event_jenisPelayananKeyPressed

    private void jenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenisPelayananItemStateChanged
        if (jenisPelayanan.getSelectedIndex() == 0) {
            kodePoli.setText("");
            namaPoli.setText("");
            LabelPoli.setVisible(false);
            kodePoli.setVisible(false);
            namaPoli.setVisible(false);

            kodeDPJPLayanan.setText("");
            namaDPJPLayanan.setText("");
            cariPoli.setEnabled(false);
        } else if (jenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            kodePoli.setVisible(true);
            namaPoli.setVisible(true);

            cariPoli.setEnabled(true);
        }
    }//GEN-LAST:event_jenisPelayananItemStateChanged

    private void catatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_catatanKeyPressed

    }//GEN-LAST:event_catatanKeyPressed

    private void noRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noRujukanKeyPressed

    }//GEN-LAST:event_noRujukanKeyPressed

    private void tglRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglRujukanKeyPressed
        Valid.pindah(evt, noRujukan, tglSEP);
    }//GEN-LAST:event_tglRujukanKeyPressed

    private void tglSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglSEPKeyPressed
        Valid.pindah(evt, tglRujukan, asalRujukan);
    }//GEN-LAST:event_tglSEPKeyPressed

    private void noRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noRMActionPerformed

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

    private void noSKDPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noSKDPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_noSKDPKeyPressed

    private void cariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariPoliActionPerformed
        poli.setSize(getContentPane().getSize());
        poli.setLocationRelativeTo(getContentPane());
        poli.setVisible(true);
    }//GEN-LAST:event_cariPoliActionPerformed

    private void cariPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariPoliKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariPoliKeyPressed

    private void cariDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDiagnosaActionPerformed
        penyakit.setSize(getContentPane().getSize());
        penyakit.setLocationRelativeTo(getContentPane());
        penyakit.setVisible(true);
    }//GEN-LAST:event_cariDiagnosaActionPerformed

    private void cariDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDiagnosaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariDiagnosaKeyPressed

    private void cariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariNoRujukanActionPerformed
        if (noPeserta.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(getContentPane().getSize());
            rujukanterakhir.setLocationRelativeTo(getContentPane());
            rujukanterakhir.tampil(noPeserta.getText(), namaPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_cariNoRujukanActionPerformed

    private void cariNoRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariNoRujukanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariNoRujukanKeyPressed

    private void btnRiwayatPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        historiPelayanan.setSize(getContentPane().getSize());
        historiPelayanan.setLocationRelativeTo(getContentPane());
        historiPelayanan.setKartu(noPeserta.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPelayananActionPerformed

    private void btnRiwayatPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatPelayananKeyPressed

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

    private void btnApprovalFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnApprovalFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnApprovalFPActionPerformed(null);
        }
    }//GEN-LAST:event_btnApprovalFPKeyPressed

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

    private void btnPengajuanFPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPengajuanFPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnPengajuanFPActionPerformed(null);
        }
    }//GEN-LAST:event_btnPengajuanFPKeyPressed

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
                                    URL = koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                                    requestJson = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + Valid.SetTgl(tglSEP.getSelectedItem() + "") + "\"," +
                                        "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    nameNode = root.path("metaData");
                                    System.out.println("code : " + nameNode.path("code").asText());
                                    System.out.println("message : " + nameNode.path("message").asText());
                                    if (nameNode.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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
                                    URL = koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                                    requestJson = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + Valid.SetTgl(tglSEP.getSelectedItem() + "") + "\"," +
                                        "\"jnsPelayanan\": \"" + jenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    nameNode = root.path("metaData");
                                    System.out.println("code : " + nameNode.path("code").asText());
                                    System.out.println("message : " + nameNode.path("message").asText());
                                    if (nameNode.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Approval Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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
        WindowAksi.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void barcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_barcodeKeyPressed

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

    private void toggleInfoTambahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleInfoTambahanActionPerformed
        isForm();
    }//GEN-LAST:event_toggleInfoTambahanActionPerformed

    private void noTelpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusGained
        toggleInfoTambahan.setSelected(false);
        panelNumpad1.setVisible(true);
        isForm();
    }//GEN-LAST:event_noTelpFocusGained

    private void noTelpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusLost
        panelNumpad1.setVisible(false);
    }//GEN-LAST:event_noTelpFocusLost

    private void noTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noTelpMouseClicked
        if (toggleInfoTambahan.isSelected()) {
            toggleInfoTambahan.setSelected(false);
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
    private widget.TextField Biaya;
    private widget.TextField Kdpnj;
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.TextField NoReg;
    private widget.TextField NoRujukMasuk;
    private widget.Label TAlmt;
    private widget.TextField TBiaya;
    private widget.Label THbngn;
    private widget.TextField TNoRw;
    private widget.Label TPngJwb;
    private widget.Dialog WindowAksi;
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
    private widget.TextField nmpnj;
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
    private widget.PaneToggle toggleInfoTambahan;
    private widget.ComboBox tujuanKunjungan;
    private widget.PasswordField userAksi;
    // End of variables declaration//GEN-END:variables

    private void isNumber() {
        switch (koneksiDB.URUTNOREG()) {
            case "poli":
                NoReg.setText(Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?",
                        kodepolireg, Valid.getTglSmc(tglSEP)
                    )
                );
                break;
            case "dokter":
                NoReg.setText(Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?",
                        kodedokterreg, Valid.getTglSmc(tglSEP)
                    )
                );
                break;
            case "dokter + poli":
                NoReg.setText(Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, Valid.getTglSmc(tglSEP)
                    )
                );
                break;
            default:
                NoReg.setText(Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?",
                        kodepolireg, kodedokterreg, Valid.getTglSmc(tglSEP)
                    )
                );
                break;
        }

        TNoRw.setText(Sequel.cariIsiSmc("select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?",
                Valid.getTglSmc(tglSEP)
            )
        );
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
            ps.setString(1, Valid.SetTgl(tglSEP.getSelectedItem() + ""));
            ps.setString(2, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TAlmt.setText(rs.getString("asal"));
                    TPngJwb.setText(rs.getString("namakeluarga"));
                    THbngn.setText(rs.getString("keluarga"));
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
            System.out.println(e);
        }

        status = "Baru";
        if (Sequel.cariInteger("select count(*) from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodepolireg) > 0) {
            status = "Lama";
        }
    }

    private void cetakRegistrasi(String noSEP) {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", TNoRw.getText());
        param.put("parameter", noSEP);
        param.put("namars", Sequel.cariIsi("select setting.nama_instansi from setting limit 1"));
        param.put("kotars", Sequel.cariIsi("select setting.kabupaten from setting limit 1"));

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
        try {
            tglkkl = "0000-00-00";
            if (lakaLantas.getSelectedIndex() > 0) {
                tglkkl = Valid.SetTgl(tglKLL.getSelectedItem() + "");
            }
            utc = api.getUTCDateTimeAsString();

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());

            URL = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            requestJson = "{" +
                "\"request\":{" +
                "\"t_sep\":{" +
                "\"noKartu\":\"" + noPeserta.getText() + "\"," +
                "\"tglSep\":\"" + Valid.SetTgl(tglSEP.getSelectedItem() + "") + "\"," +
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
                "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\"," +
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
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");

            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            JOptionPane.showMessageDialog(null, "Respon BPJS : " + nameNode.path("message").asText());

            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
                System.out.println("SEP berhasil terbit!");
                System.out.println("No. SEP: " + response.asText());

                String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", Valid.getTglSmc(tglSEP), noRM.getText(), kodepolireg, kodedokterreg);

                if (isNoRawat == null || (!isNoRawat.equals(TNoRw.getText()))) {
                    System.out.println("======================================================");
                    System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                    System.out.println("No. Rawat: " + TNoRw.getText());
                    System.out.println("Tgl. Registrasi: " + Valid.getTglSmc(tglSEP));
                    System.out.println("No. Antrian: " + NoReg.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Dokter: " + kodedokterreg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("Kode Poli: " + kodepolireg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()) + ")");
                    System.out.println("======================================================");

                    return;
                }

                Sequel.menyimpanSmc("bridging_sep", null,
                    response.asText(),
                    TNoRw.getText(),
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
                    tglkkl,
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
                    Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?", Valid.getTglSmc(tglSEP), response.asText());
                }

                if (!prb.equals("")) {
                    Sequel.menyimpanSmc("bpjs_prb", null, response.asText(), prb);

                    prb = "";
                }

                if (Sequel.cariIntegerSmc("select count(*) from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'",
                    noRM.getText(), Valid.getTglSmc(tglSEP), kodedokterreg, kodepolireg
                ) == 1) {
                    Sequel.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", noRM.getText(), Valid.getTglSmc(tglSEP), kodedokterreg, kodepolireg);
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
        statusfinger = false;

        if (!noPeserta.getText().equals("")) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = api.getUTCDateTimeAsString();
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                URL = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + Valid.getTglSmc(tglSEP);
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
            URL = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTimeAsString();
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
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
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
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());
                isPoli();
                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                nohppasien = response.path("peserta").path("mr").path("noTelepon").asText();
                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(nohppasien);
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                JOptionPane.showMessageDialog(null, "Pesan Pencarian Rujukan FKTP : " + nameNode.path("message").asText());
                try {
                    URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTimeAsString();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                    nameNode = root.path("metaData");
                    peserta = "";
                    if (nameNode.path("code").asText().equals("200")) {
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
                        peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
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
                        kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", kodeDPJP.getText());
                        noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        nohppasien = response.path("peserta").path("mr").path("noTelepon").asText();
                        if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                            noTelp.setText(nohppasien);
                        }
                        kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                        asalRujukan.setSelectedIndex(1);
                        isNumber();
                        Kdpnj.setText("BPJ");
                        nmpnj.setText("BPJS");
                        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                    } else {
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Pesan Pencarian Rujukan FKRTL : " + nameNode.path("message").asText());
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
            ps.setString(1, kodepolireg);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
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
            URL = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            utc = api.getUTCDateTimeAsString();
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
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
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
                kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());
                isPoli();
                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                nohppasien = response.path("peserta").path("mr").path("noTelepon").asText();
                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(nohppasien);
                }
            } else {
                System.out.println("Pesan pencarian rujukan FKTP : " + nameNode.path("message").asText());
                try {
                    URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    utc = api.getUTCDateTimeAsString();
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    requestEntity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                    nameNode = root.path("metaData");
                    peserta = "";
                    if (nameNode.path("code").asText().equals("200")) {
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
                        peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
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
                        kodePoli.setText(response.path("poliRujukan").path("kode").asText());
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                        kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", kodeDPJP.getText());
                        noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        nohppasien = response.path("peserta").path("mr").path("noTelepon").asText();
                        if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                            noTelp.setText(nohppasien);
                        }
                        kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                        asalRujukan.setSelectedIndex(1);
                        isNumber();
                        Kdpnj.setText("BPJ");
                        nmpnj.setText("BPJS");
                        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                    } else {
                        emptTeks();
                        System.out.println("Pesan pencarian rujukan FKTL : " + nameNode.path("message").asText());
                        JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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
            ps.setString(1, kodepolireg);
            ps.setString(2, hari);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kodeDPJP.setText(rs.getString("kd_dokter_bpjs"));
                    namaDPJP.setText(rs.getString("nm_dokter_bpjs"));
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
                            URL = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rskontrol.getString("no_kartu") + "/tglSEP/" + Valid.getTglSmc(tglSEP);
                            utc = api.getUTCDateTimeAsString();
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
                                kodeDiagnosa.setText("Z09.8");
                                namaDiagnosa.setText("Z09.8 - Follow-up examination after other treatment for other conditions");
                                noRujukan.setText(rskontrol.getString("no_sep"));
                                tujuanKunjungan.setSelectedIndex(0);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(0);
                                asalRujukan.setSelectedIndex(1);
                                kodePoli.setText(rskontrol.getString("kd_poli_bpjs"));
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodeDPJP.setText(rskontrol.getString("kd_dokter_bpjs"));
                                namaDPJP.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDPJPLayanan.setText(kodeDPJP.getText());
                                namaDPJPLayanan.setText(namaDPJP.getText());
                                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
                                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());
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
                                peserta = response.path("jenisPeserta").path("keterangan").asText();
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
                                isNumber();
                                Kdpnj.setText("BPJ");
                                nmpnj.setText("BPJS");
                                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                nohppasien = response.path("mr").path("noTelepon").asText();
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(nohppasien);
                                }
                            } else {
                                emptTeks();
                                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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
                                URL = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                            } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                URL = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                            }
                            utc = api.getUTCDateTimeAsString();
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
                                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                noRujukan.setText(response.path("noKunjungan").asText());
                                noSKDP.setText(rskontrol.getString("no_surat"));
                                kodePoli.setText(rskontrol.getString("kd_poli_bpjs"));
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodeDPJP.setText(rskontrol.getString("kd_dokter_bpjs"));
                                namaDPJP.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDPJPLayanan.setText(kodeDPJP.getText());
                                namaDPJPLayanan.setText(namaDPJP.getText());
                                kodepolireg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli.getText());
                                kodedokterreg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDPJP.getText());
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
                                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
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
                                isPoli();
                                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                Valid.SetTgl(tglRujukan, response.path("tglKunjungan").asText());
                                isNumber();
                                Kdpnj.setText("BPJ");
                                nmpnj.setText("BPJS");
                                catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                nohppasien = response.path("peserta").path("mr").path("noTelepon").asText();
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(nohppasien);
                                }
                            } else {
                                emptTeks();
                                System.out.println("Pesan pencarian rujukan : " + nameNode.path("message").asText());
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

    private boolean kirimAntrianOnsite() {
        if (!ADDANTRIANAPIMOBILEJKN) {
            return true;
        }
        boolean sukses = true;
        int angkaantrean = Integer.parseInt(NoReg.getText());
        jeniskunjungan = "1";
        String nomorreferensi = noRujukan.getText();
        if ((!noRujukan.getText().equals("")) || (!noSKDP.getText().equals(""))) {
            if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().trim().equals("") && penunjang.getSelectedItem().toString().trim().equals("") && asesmenPelayanan.getSelectedItem().toString().trim().equals("")) {
                if (asalRujukan.getSelectedIndex() == 0) {
                    jeniskunjungan = "1";
                    nomorreferensi = noRujukan.getText();
                } else {
                    if (!noSKDP.getText().equals("")) {
                        jeniskunjungan = "3";
                        nomorreferensi = noSKDP.getText();
                    } else {
                        jeniskunjungan = "4";
                        nomorreferensi = noRujukan.getText();
                    }
                }
            } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().trim().equals("") && penunjang.getSelectedItem().toString().trim().equals("") && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                jeniskunjungan = "3";
                nomorreferensi = noSKDP.getText();
            } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().trim().equals("") && penunjang.getSelectedItem().toString().trim().equals("") && asesmenPelayanan.getSelectedItem().toString().trim().equals("4. Atas Instruksi RS")) {
                jeniskunjungan = "2";
                nomorreferensi = noRujukan.getText();
            } else {
                if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                    jeniskunjungan = "3";
                    nomorreferensi = noSKDP.getText();
                } else {
                    jeniskunjungan = "2";
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
                    ps.setString(2, kodepolireg);
                    ps.setString(3, kodedokterreg);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            jampraktek = rs.getString("jam_mulai").substring(0, 5) + "-" + rs.getString("jam_selesai").substring(0, 5);
                            jammulai = rs.getString("jam_mulai");
                            jamselesai = rs.getString("jam_selesai");
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
                    datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", Valid.getTglSmc(tglSEP), jammulai, String.valueOf(angkaantrean * 5));
                    parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datajam);
                    if (!jeniskunjungan.isBlank() && !nomorreferensi.isBlank()) {
                        requestJson = "{" +
                            "\"kodebooking\": \"" + TNoRw.getText() + "\"," +
                            "\"jenispasien\": \"JKN\"," +
                            "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                            "\"nik\": \"" + nik.getText() + "\"," +
                            "\"nohp\": \"" + noTelp.getText().trim() + "\"," +
                            "\"kodepoli\": \"" + kodePoli.getText() + "\"," +
                            "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                            "\"pasienbaru\": 0," +
                            "\"norm\": \"" + noRM.getText() + "\"," +
                            "\"tanggalperiksa\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                            "\"kodedokter\": " + kodeDPJP.getText() + "," +
                            "\"namadokter\": \"" + namaDPJP.getText() + "\"," +
                            "\"jampraktek\": \"" + jampraktek + "\"," +
                            "\"jeniskunjungan\": " + jeniskunjungan + "," +
                            "\"nomorreferensi\": \"" + nomorreferensi + "\"," +
                            "\"nomorantrean\": \"" + NoReg.getText() + "\"," +
                            "\"angkaantrean\": " + angkaantrean + "," +
                            "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                            "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                            "\"kuotajkn\": " + kuota + "," +
                            "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                            "\"kuotanonjkn\": " + kuota + "," +
                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                            "}";
                        System.out.println("JSON : " + requestJson);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        System.out.println("URL : " + URL);
                        System.out.print("addantrean " + TNoRw.getText() + " : ");
                        try {
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            requestEntity = new HttpEntity(requestJson, headers);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                            nameNode = root.path("metadata");
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                            System.out.println(nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                            if (!nameNode.path("code").asText().equals("200")) {
                                sukses = false;
                            }
                        } catch (HttpClientErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                        } catch (HttpServerErrorException e) {
                            sukses = false;
                            System.out.println("Notif : " + e.getMessage());
                            Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                        } catch (Exception e) {
                            sukses = false;
                            System.out.println("Notif : " + e);
                        }
                    }
                }
                if (!sukses) {
                    sukses = true;
                    requestJson = "{" +
                        "\"kodebooking\": \"" + TNoRw.getText() + "\"," +
                        "\"jenispasien\": \"JKN\"," +
                        "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                        "\"nik\": \"" + nik.getText() + "\"," +
                        "\"nohp\": \"" + nohppasien + "\"," +
                        "\"kodepoli\": \"" + kodePoli.getText() + "\"," +
                        "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                        "\"pasienbaru\": 0," +
                        "\"norm\": \"" + noRM.getText() + "\"," +
                        "\"tanggalperiksa\": \"" + Valid.getTglSmc(tglSEP) + "\"," +
                        "\"kodedokter\": " + kodeDPJP.getText() + "," +
                        "\"namadokter\": \"" + namaDPJP.getText() + "\"," +
                        "\"jampraktek\": \"" + jampraktek + "\"," +
                        "\"jeniskunjungan\": " + jeniskunjungan + "," +
                        "\"nomorreferensi\": \"" + nomorreferensi + "\"," +
                        "\"nomorantrean\": \"" + NoReg.getText() + "\"," +
                        "\"angkaantrean\": " + angkaantrean + "," +
                        "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                        "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                        "\"kuotajkn\": " + kuota + "," +
                        "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                        "\"kuotanonjkn\": " + kuota + "," +
                        "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                        "}";
                    System.out.println("JSON : " + requestJson);
                    URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    System.out.println("URL : " + URL);
                    System.out.print("addantrean " + TNoRw.getText() + " : ");
                    try {
                        utc = api.getUTCDateTimeAsString();
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, nameNode.path("code").asText(), nameNode.path("message").asText(), root.toString(), datajam);
                        System.out.println(nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
                        if (!nameNode.path("code").asText().equals("200")) {
                            JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
                            sukses = false;
                        }
                    } catch (HttpClientErrorException e) {
                        sukses = false;
                        System.out.println("Notif : " + e.getMessage());
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    } catch (HttpServerErrorException e) {
                        sukses = false;
                        System.out.println("Notif : " + e.getMessage());
                        Sequel.logTaskid(TNoRw.getText(), TNoRw.getText(), "Onsite", "addantrean", requestJson, e.getStatusCode().toString(), e.getMessage(), "", datajam);
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

        return sukses;
    }

    private void emptTeks() {
        namaPasien.setText("");
        tglSEP.setDate(new Date());
        tglRujukan.setDate(new Date());
        tglLahir.setText("");
        noPeserta.setText("");
        jenisPeserta.setText("");
        statusPeserta.setText("");
        jk.setText("");
        noRujukan.setText("");
        kodePPKRujukan.setText("");
        namaPPKRujukan.setText("");
        jenisPelayanan.setSelectedIndex(1);
        catatan.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        kodePoli.setText("");
        namaPoli.setText("");
        kelas.setSelectedIndex(2);
        lakaLantas.setSelectedIndex(0);
        noRM.setText("");
        kodeDPJP.setText("");
        namaDPJP.setText("");
        keterangan.setText("");
        noSEPSuplesi.setText("");
        kdPropKLL.setText("");
        nmPropKLL.setText("");
        kdKabKLL.setText("");
        nmKabKLL.setText("");
        kdKecKLL.setText("");
        nmKecKLL.setText("");
        katarak.setSelectedIndex(0);
        suplesi.setSelectedIndex(0);
        tglKLL.setDate(new Date());
        tglKLL.setEnabled(false);
        keterangan.setEditable(false);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        flagProsedur.setEnabled(false);
        penunjang.setSelectedIndex(0);
        penunjang.setEnabled(false);
        asesmenPelayanan.setSelectedIndex(0);
        asesmenPelayanan.setEnabled(true);
        kodeDPJPLayanan.setText("");
        namaDPJPLayanan.setText("");
        cariDPJP.setEnabled(true);
        noRujukan.requestFocus();
        kodepolireg = "";
        kodedokterreg = "";
        barcode.setText("3");
        resetAksi();
    }

    private void isPoli() {
        try (PreparedStatement ps = koneksi.prepareStatement("select registrasi, registrasilama from poliklinik where kd_poli = ? order by nm_poli")) {
            ps.setString(1, kodepolireg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (statuspasien.equals("Lama")) {
                        TBiaya.setText(rs.getString("registrasilama"));
                    } else {
                        TBiaya.setText(rs.getString("registrasi"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
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

            URL = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";

            requestJson = "{" +
                "\"request\": {" +
                "\"noSuratKontrol\":\"" + noSKDP + "\"," +
                "\"noSEP\":\"" + noSEP + "\"," +
                "\"kodeDokter\":\"" + kodeDokterKontrol + "\"," +
                "\"poliKontrol\":\"" + kodePoliKontrol + "\"," +
                "\"tglRencanaKontrol\":\"" + tglKontrol + "\"," +
                "\"user\":\"" + noKartuPeserta + "\"" +
                "}" +
                "}";

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
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
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

        do {
            isNumber();

            System.out.print("Mencoba mendaftarkan pasien dengan no. rawat [" + TNoRw.getText() + "]: ");

            sukses = Sequel.menyimpantfSmc("reg_periksa", null,
                NoReg.getText(), TNoRw.getText(), Valid.getTglSmc(tglSEP),
                Sequel.cariIsi("select current_time()"), kodedokterreg, noRM.getText(),
                kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(),
                TBiaya.getText(), "Belum", statuspasien, "Ralan", Kdpnj.getText(),
                umur, sttsumur, "Belum Bayar", status
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
            NoRujukMasuk.setText(Sequel.cariIsiSmc("select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')",
                    Valid.getTglSmc(tglSEP), Valid.getTglSmc(tglSEP)
                )
            );

            System.out.print("Mencoba memproses rujukan masuk pasien dengan no. surat [" + NoRujukMasuk.getText() + "]: ");

            sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
                TNoRw.getText(), namaPPKRujukan.getText(), "-", noRujukan.getText(),
                "0", namaPPKRujukan.getText(), kodeDiagnosa.getText(), "-", "-", NoRujukMasuk.getText()
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
            panelNumpad1.setVisible(false);
            panel1.setPreferredSize(new Dimension(WIDTH, 310));
            panel2.setPreferredSize(new Dimension(WIDTH, 290));
            form.setVisible(true);
            toggleInfoTambahan.setVisible(true);
        } else {
            toggleInfoTambahan.setVisible(false);
            panel1.setPreferredSize(new Dimension(WIDTH, 610));
            panel2.setPreferredSize(new Dimension(WIDTH, 30));
            form.setVisible(false);
            toggleInfoTambahan.setVisible(true);
        }
    }
}
