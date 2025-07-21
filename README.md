## APM Custom
Aplikasi anjungan pasien mandiri (APM) modifikasi dari [APM RS Indriati Boyolali](https://github.com/abdulrokhimrepo/anjunganmandiriSEP).

### Requirements
- [Apache Netbeans](https://netbeans.apache.org/front/main/download/index.html)  
- [Liberica JDK 17](https://github.com/bell-sw/Liberica/releases?q=17.0&expanded=true)  
- [SIMRS Khanza SMC](https://github.com/Rizky92/simrs-khanza)  
- Library yang digunakan bisa di [download disini](https://drive.google.com/drive/folders/1bLKuw8l9k5ElC5dxxlrXijACPLtNmCTg?usp=sharing)  

### Konfigurasi
Terdapat dua jenis konfigurasi. Konfigurasi bawaan [SIMRS Khanza SMC](https://github.com/Rizky92/simrs-khanza), dan konfigurasi khusus untuk APM.  
Berikut adalah konfigurasi yang disediakan dalam file `apm.xml.example`:
```xml
<entry key="PRINTER_REGISTRASI"></entry>
<entry key="PRINTER_BARCODE"></entry>
<entry key="PRINTER_ANTRIAN"></entry>
<entry key="PRINTJUMLAHBARCODE">3</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS"></entry>
<entry key="URLAPLIKASIFRISTABPJS"></entry>
<entry key="USERFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="PASSWORDFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="TOMBOLDIMATIKAN">satusehat</entry>
<entry key="KODEPOLIEKSEKUTIF"></entry>
```

### `PRINTER_REGISTRASI`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak bukti registrasi dan lembar SEP.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

values: nama printer yang muncul di terminal  
default: `""`  

### `PRINTER_BARCODE`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak lembar barcode.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

values: nama printer yang muncul di terminal  
default: `""`  

### `PRINTER_ANTRIAN`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak nomor antrian dari APM.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

values: nama printer yang muncul di terminal  
default: `""`  

### `PRINTJUMLAHBARCODE`
Pengaturan ini digunakan untuk menentukan jumlah barcode yang mau dicetak.  

values: integer  
default: `3`  

### `URLAPLIKASIFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mencari lokasi file exe aplikasi fingerprint BPJS Kesehatan untuk proses validasi biometrik fingerprint dari APM.  

values: Path file exe  
default: `""`  

### `URLAPLIKASIFRISTABPJS`
Pengaturan ini digunakan untuk mencari lokasi file exe aplikasi FRISTA BPJS Kesehatan untuk proses validasi biometrik pengenalan wajah dari APM.  

values: Path file exe  
default: `""`  

### `USERFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mengisi kolom username pada saat pertama kali membuka aplikasi validasi biometrik BPJS Kesehatan. Harus dilakukan enkripsi dahulu.  

values: encrypted  
default: `""`  

### `PASSWORDFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mengisi kolom password pada saat pertama kali membuka aplikasi validasi biometrik BPJS Kesehatan. Harus dilakukan enkripsi dahulu.  

values: encrypted  
default: `""`  

### `TOMBOLDIMATIKAN`
Pengaturan ini digunakan untuk mengatur menu apa yang dimatikan dari halaman depan APM.  

values: List menu, dipisah dengan koma, `antrian,cekin,daftarpoli,seppertama,sepkontrol,sepbedapoli,mobilejkn,satusehat`  
default: `"satusehat"`  

### `KODEPOLIEKSEKUTIF`
Pengaturan ini digunakan untuk mengatur kode poli yang digunakan sebagai default untuk pendaftaran registrasi eksekutif.  

values: kode poli eksekutif dari SIMRS  
default: `""`  
