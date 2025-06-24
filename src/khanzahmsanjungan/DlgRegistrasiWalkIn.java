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

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Kode
 */
public class DlgRegistrasiWalkIn extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private final String URUTNOREG = koneksiDB.URUTNOREG(),
                         PRINTERREGISTRASI = koneksiDB.PRINTER_REGISTRASI(),
                         PRINTERBARCODE = koneksiDB.PRINTER_BARCODE(),
                         KODEPOLIEKSEKUTIF = koneksiDB.KODEPOLIEKSEKUTIF();
    private String hari = "",
                   noRawat = "",
                   noReg = "",
                   kdDokter = "",
                   kdPoli = "",
                   biayaReg = "",
                   statusDaftar = "Lama",
                   statusPoli = "Baru",
                   umurDaftar = "0",
                   statusUmur = "Th",
                   namaPJ = "-",
                   hubunganPJ = "-",
                   alamatPJ = "-",
                   instansiNama = "",
                   instansiAlamat = "",
                   instansiKota = "",
                   instansiKontak = "",
                   poliBiaya = "",
                   poliBiayaLama = "",
                   umurPasien = "";
    
    private DlgCariPoli poli = new DlgCariPoli(null, true);
    private DlgCariDokter dokter = new DlgCariDokter(null, true);
    private Calendar cal = Calendar.getInstance();
    private int day = cal.get(Calendar.DAY_OF_WEEK);

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgRegistrasiWalkIn(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();

        try (ResultSet rs = koneksi.prepareStatement("select nama_instansi, alamat_instansi, kabupaten, kontak from setting").executeQuery()) {
            if (rs.next()) {
                instansiNama = rs.getString("nama_instansi");
                instansiAlamat = rs.getString("alamat_instansi");
                instansiKota = rs.getString("kabupaten");
                instansiKontak = rs.getString("kontak");
            }
        } catch (SQLException e) {
            System.out.println("Notif : " + e);
        }
        
        if (KODEPOLIEKSEKUTIF.isBlank()) {
            buttonCariPoli.setVisible(true);
            poli.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (poli.getTable().getSelectedRow() >= 0) {
                        namaDokter.setText("");
                        kdDokter = "";
                        kdPoli = poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 0).toString();
                        namaPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 1).toString());
                        poliBiaya = poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 2).toString();
                        poliBiayaLama = poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 3).toString();
                    }
                }
            });
        } else {
            buttonCariPoli.setVisible(false);
            kdPoli = KODEPOLIEKSEKUTIF;
            namaPoli.setText(Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik where poliklinik.kd_poli = ?", kdPoli));
            poliBiaya = Sequel.cariIsiSmc("select poliklinik.registrasi from poliklinik where poliklinik.kd_poli = ?", kdPoli);
            poliBiayaLama = Sequel.cariIsiSmc("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli = ?", kdPoli);
        }

        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() >= 0) {
                    namaDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    kdDokter = dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new widget.Label();
        jLabel29 = new widget.Label();
        jLabel31 = new widget.Label();
        noRM = new widget.Label();
        jLabel32 = new widget.Label();
        buttonCariPoli = new widget.Button();
        namaPoli = new widget.TextBox();
        jLabel36 = new widget.Label();
        buttonCariDokter = new widget.Button();
        namaDokter = new widget.TextBox();
        jLabel11 = new widget.Label();
        namaPasien = new widget.Label();
        jLabel19 = new widget.Label();
        tglLahir = new widget.Label();
        tanggalPeriksa = new widget.Label();
        jenisBayar = new widget.Label();
        jPanel3 = new javax.swing.JPanel();
        btnSimpan = new widget.Button();
        btnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 215, 255)), "PENDAFTARAN POLIKLINIK PRIBADI", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 70));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        jPanel2.setBackground(new java.awt.Color(238, 238, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(390, 120));
        jPanel2.setLayout(null);

        jLabel10.setForeground(new java.awt.Color(0, 131, 62));
        jLabel10.setText("No. Rekam Medis :");
        jLabel10.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel10);
        jLabel10.setBounds(60, 50, 220, 40);

        jLabel29.setForeground(new java.awt.Color(0, 131, 62));
        jLabel29.setText("Tanggal Periksa :");
        jLabel29.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel29.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel29);
        jLabel29.setBounds(60, 200, 220, 40);

        jLabel31.setForeground(new java.awt.Color(0, 131, 62));
        jLabel31.setText("Poli Tujuan :");
        jLabel31.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel31);
        jLabel31.setBounds(60, 250, 220, 40);

        noRM.setForeground(new java.awt.Color(0, 131, 62));
        noRM.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        noRM.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        noRM.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(noRM);
        noRM.setBounds(290, 50, 590, 40);

        jLabel32.setForeground(new java.awt.Color(0, 131, 62));
        jLabel32.setText("Dokter Tujuan :");
        jLabel32.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel32.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel32);
        jLabel32.setBounds(60, 300, 220, 40);

        buttonCariPoli.setBackground(new java.awt.Color(238, 238, 255));
        buttonCariPoli.setForeground(new java.awt.Color(0, 131, 62));
        buttonCariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        buttonCariPoli.setMnemonic('S');
        buttonCariPoli.setToolTipText("Alt+S");
        buttonCariPoli.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        buttonCariPoli.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        buttonCariPoli.setPreferredSize(new java.awt.Dimension(300, 45));
        buttonCariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCariPoliActionPerformed(evt);
            }
        });
        jPanel2.add(buttonCariPoli);
        buttonCariPoli.setBounds(820, 250, 50, 40);

        namaPoli.setEditable(false);
        namaPoli.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        namaPoli.setPreferredSize(new java.awt.Dimension(72, 28));
        jPanel2.add(namaPoli);
        namaPoli.setBounds(290, 250, 520, 40);

        jLabel36.setForeground(new java.awt.Color(0, 131, 62));
        jLabel36.setText("Cara Bayar :");
        jLabel36.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel36);
        jLabel36.setBounds(60, 350, 220, 40);

        buttonCariDokter.setBackground(new java.awt.Color(238, 238, 255));
        buttonCariDokter.setForeground(new java.awt.Color(0, 131, 62));
        buttonCariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        buttonCariDokter.setMnemonic('S');
        buttonCariDokter.setToolTipText("Alt+S");
        buttonCariDokter.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        buttonCariDokter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        buttonCariDokter.setPreferredSize(new java.awt.Dimension(300, 45));
        buttonCariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCariDokterActionPerformed(evt);
            }
        });
        jPanel2.add(buttonCariDokter);
        buttonCariDokter.setBounds(820, 300, 50, 40);

        namaDokter.setEditable(false);
        namaDokter.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        namaDokter.setPreferredSize(new java.awt.Dimension(72, 28));
        jPanel2.add(namaDokter);
        namaDokter.setBounds(290, 300, 520, 40);

        jLabel11.setForeground(new java.awt.Color(0, 131, 62));
        jLabel11.setText("Nama Pasien :");
        jLabel11.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel11);
        jLabel11.setBounds(60, 100, 220, 40);

        namaPasien.setForeground(new java.awt.Color(0, 131, 62));
        namaPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        namaPasien.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        namaPasien.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(namaPasien);
        namaPasien.setBounds(290, 100, 590, 40);

        jLabel19.setForeground(new java.awt.Color(0, 131, 62));
        jLabel19.setText("Tgl. Lahir :");
        jLabel19.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jLabel19);
        jLabel19.setBounds(60, 150, 220, 40);

        tglLahir.setForeground(new java.awt.Color(0, 131, 62));
        tglLahir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tglLahir.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        tglLahir.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(tglLahir);
        tglLahir.setBounds(290, 150, 590, 40);

        tanggalPeriksa.setForeground(new java.awt.Color(0, 131, 62));
        tanggalPeriksa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tanggalPeriksa.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
        tanggalPeriksa.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        tanggalPeriksa.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(tanggalPeriksa);
        tanggalPeriksa.setBounds(290, 200, 590, 40);

        jenisBayar.setForeground(new java.awt.Color(0, 131, 62));
        jenisBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jenisBayar.setText("UMUM / PERSONAL");
        jenisBayar.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        jenisBayar.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel2.add(jenisBayar);
        jenisBayar.setBounds(290, 350, 590, 40);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(238, 238, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(615, 200));

        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("Konfirmasi");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 60));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel3.add(btnSimpan);

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
        jPanel3.add(btnKeluar);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        if (noRM.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "No. RM Kosong..!!");
        } else if (kdPoli.isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Pilih poli terlebih dahulu..!!");
        } else if (kdDokter.isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Pilih Dokter terlebih dahulu..!!");
        } else if (Sequel.cariIntegerSmc("select count(*) from reg_periksa where kd_pj = 'A09' and no_rkm_medis = ? and tgl_registrasi = current_date() and kd_poli = ? and kd_dokter = ?", noRM.getText(), kdPoli, kdDokter) > 0) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, anda sudah terdaftar pada hari ini dengan dokter dan poli yang sama..!!");
        } else if (Sequel.cariIntegerSmc("select count(*) from reg_periksa join kamar_inap on reg_periksa.no_rawat = kamar_inap.no_rawat where kamar_inap.stts_pulang = '-' and reg_periksa.no_rkm_medis = ?", noRM.getText()) > 0) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, pasien sedang dalam masa perawatan di rawat inap..!!");
        } else {
            setNomorRegistrasi();
            updateUmurPasien();
            setStatusPasien();
            if (registerPasien()) {
                cetakRegistrasi();
                JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            }
            kosongkanInput();
            dispose();
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void buttonCariDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCariDokterActionPerformed
        dokter.tampil(hari, kdPoli);
        dokter.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        dokter.setLocationRelativeTo(jPanel2);
        dokter.setVisible(true);
    }//GEN-LAST:event_buttonCariDokterActionPerformed

    private void buttonCariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCariPoliActionPerformed
        poli.tampil(hari);
        poli.setSize(jPanel1.getWidth() - 50, jPanel1.getHeight() - 50);
        poli.setLocationRelativeTo(jPanel2);
        poli.setVisible(true);
    }//GEN-LAST:event_buttonCariPoliActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button btnKeluar;
    private widget.Button btnSimpan;
    private widget.Button buttonCariDokter;
    private widget.Button buttonCariPoli;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel19;
    private widget.Label jLabel29;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel36;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.Label jenisBayar;
    private widget.TextBox namaDokter;
    private widget.Label namaPasien;
    private widget.TextBox namaPoli;
    private widget.Label noRM;
    private widget.Label tanggalPeriksa;
    private widget.Label tglLahir;
    // End of variables declaration//GEN-END:variables

    public void setPasien(String noRM) {
        this.noRM.setText(noRM);
        tentukanHari();
        ambilDataPasien();
    }
    
    private void ambilDataPasien() {
        try {
            ps = koneksi.prepareStatement("select nm_pasien, tgl_lahir from pasien where no_rkm_medis = ?");
            try {
                ps.setString(1, noRM.getText());
                
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    namaPasien.setText(rs.getString("nm_pasien"));
                    tglLahir.setText(formatTanggal(rs.getString("tgl_lahir")));
                }
            } catch (SQLException e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private String formatTanggal(String tanggal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(tanggal, dtf);
        
        return ld.format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(new Locale("id", "ID")));
    }
    
    private void setStatusPasien() {
        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kdPoli)) {
            statusPoli = "Lama";
        }
        biayaReg = statusPoli.equals("Lama") ? poliBiayaLama : poliBiaya;
        
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pasien.nm_pasien, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, " +
            "pasien.tgl_lahir, pasien.namakeluarga, pasien.keluarga, pasien.kd_pj, if(pasien.tgl_daftar = current_date(), 'baru', 'lama') as daftar, " +
            "timestampdiff(year, pasien.tgl_lahir, current_date()) as tahun, timestampdiff(month, pasien.tgl_lahir, current_date()) - ((timestampdiff(month, " +
            "pasien.tgl_lahir, current_date()) div 12) * 12) as bulan, timestampdiff(day, date_add(date_add(pasien.tgl_lahir, interval timestampdiff(year, " +
            "pasien.tgl_lahir, current_date()) year), interval timestampdiff(month, pasien.tgl_lahir, current_date()) - ((timestampdiff(month, pasien.tgl_lahir, " +
            "current_date()) div 12) * 12) month), current_date()) as hari from pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on " +
            "pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab where pasien.no_rkm_medis = ?"
        )) {
            ps.setString(1, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    namaPasien.setText(rs.getString("nm_pasien"));
                    tglLahir.setText(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(new Locale("id", "ID")).format(rs.getDate("tgl_lahir").toLocalDate()));
                    namaPJ = rs.getString("namakeluarga");
                    hubunganPJ = rs.getString("keluarga");
                    alamatPJ = rs.getString("alamat");
                    statusDaftar = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umurDaftar = rs.getString("tahun");
                        statusUmur = "Th";
                    } else if ((rs.getInt("tahun") <= 0) && (rs.getInt("bulan") > 0)) {
                        umurDaftar = rs.getString("bulan");
                        statusUmur = "Bl";
                    } else if ((rs.getInt("tahun") <= 0) && (rs.getInt("bulan") <= 0) && (rs.getInt("hari") > 0)) {
                        umurDaftar = rs.getString("hari");
                        statusUmur = "Hr";
                    }
                    umurPasien = rs.getString("tahun") + " Th " + rs.getString("bulan") + " Bl " + rs.getString("hari") + " Hr";
                }
            }
        } catch (SQLException e) {
            System.out.println("Notif : " + e);
        }
    }

    private void updateUmurPasien() {
        Sequel.mengupdateSmc("pasien", "umur = ?", "no_rkm_medis = ?", umurPasien, noRM.getText());
    }

    private void setNomorRegistrasi() {
        switch (URUTNOREG) {
            case "poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = current_date()", kdPoli);
                break;
            case "dokter":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = current_date()", kdPoli);
                break;
            case "dokter + poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = current_date()", kdPoli, kdDokter);
                break;
            default:
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = current_date()", kdPoli, kdDokter);
                break;
        }
        
        noRawat = Sequel.cariIsiSmc("select concat(date_format(current_date(), '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = current_date()");
    }

    private void tentukanHari() {
        try {
            day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
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

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
    
    private boolean registerPasien() {
        int coba = 0, maxCoba = 5;
        
        System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + noRawat);
             
        while (coba < maxCoba && (
            ! Sequel.menyimpantfSmc("reg_periksa", null,
                noReg, noRawat, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()),
                Sequel.cariIsi("select current_time()"), kdDokter, noRM.getText(), kdPoli,
                namaPJ, alamatPJ, hubunganPJ, biayaReg, "Belum", statusDaftar, "Ralan", "A09",
                umurDaftar, statusUmur, "Belum Bayar", statusPoli)
        )) {
            setNomorRegistrasi();
            System.out.println("Mencoba mendaftarkan pasien dengan no. rawat: " + noRawat);
            
            coba++;
        }
        
        String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = current_date() and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", noRM.getText(), kdPoli, kdDokter);
                
        if (coba == maxCoba && (isNoRawat == null || ! isNoRawat.equals(noRawat))) {
            System.out.println("======================================================");
            System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
            System.out.println("No. Rawat: " + noRawat);
            System.out.println("Tgl. Registrasi: " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            System.out.println("No. Antrian: " + noReg + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", noRawat) + ")");
            System.out.println("No. RM: " + noRM + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", noRawat) + ")");
            System.out.println("Kode Dokter: " + kdDokter + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", noRawat) + ")");
            System.out.println("Kode Poli: " + kdPoli  + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", noRawat) + ")");
            System.out.println("======================================================");

            return false;
        }
        
        return true;
    }
    
    private void cetakRegistrasi() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = new HashMap<>();
        param.put("namars", instansiNama);
        param.put("alamatrs", instansiAlamat);
        param.put("kotars", instansiKota);
        param.put("kontakrs", instansiKontak);
        param.put("norawat", noRawat);
        // Valid.printReport("rptBuktiRegisterAPM.jasper", PRINTERREGISTRASI, "::[ Bukti Registrasi 1 ]::", 1, param);
        Valid.printReport("rptBarcodeRawatAPM.jasper", PRINTERBARCODE, "::[ Barcode Perawatan ]::", 3, param);
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    private void kosongkanInput() {
        noRM.setText("");
        namaPasien.setText("");
        tglLahir.setText("");
        tanggalPeriksa.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
        namaPoli.setText(Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik where poliklinik.kd_poli = ?", kdPoli));
        namaDokter.setText("");
        jenisBayar.setText("UMUM / PERSONAL");
        
        hari = "";
        noRawat = "";
        noReg = "";
        kdDokter = "";
        kdPoli = KODEPOLIEKSEKUTIF.isBlank() ? "" : KODEPOLIEKSEKUTIF;
        biayaReg = "";
        statusDaftar = "Lama";
        statusPoli = "Baru";
        umurDaftar = "0";
        statusUmur = "Th";
        namaPJ = "-";
        hubunganPJ = "-";
        alamatPJ = "-";
        instansiNama = "";
        instansiAlamat = "";
        instansiKota = "";
        instansiKontak = "";
    }
}
