## APM Custom
Aplikasi anjungan pasien mandiri (APM) modifikasi dari [APM RS Indriati Boyolali](https://github.com/abdulrokhimrepo/anjunganmandiriSEP).

### Requirements
- [Apache Netbeans](https://netbeans.apache.org/front/main/download/index.html)
- Liberica JDK 17 [Download](https://github.com/bell-sw/Liberica/releases?q=17.0&expanded=true)
- Aplikasi Fingerprint BPJS v2.0+
- Library yang digunakan bisa di download [disini](https://drive.google.com/drive/folders/1bLKuw8l9k5ElC5dxxlrXijACPLtNmCTg?usp=sharing).  

### Konfigurasi
Berikut adalah konfigurasi yang disediakan dalam file `apm.xml.example`:
```xml
<entry key="PRINTER_REGISTRASI"></entry>
<entry key="PRINTER_BARCODE"></entry>
<entry key="PRINTER_ANTRIAN"></entry>
<entry key="PRINTERJUMLAHBARCODE">3</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS">D:\BPJS Kesehatan\Aplikasi Sidik Jari BPJS Kesehatan\After.exe</entry>
<entry key="URLAPLIKASIFRISTABPJS">D:\BPJS Kesehatan\FRISTA\frista.exe</entry>
<entry key="USERFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="PASSWORDFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="AUTOBUKAAPLIKASI">frista</entry>
<entry key="TOMBOLDIMATIKAN"></entry>
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

### `PRINTERJUMLAHBARCODE`
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

### `AUTOBUKAAPLIKASI`
Pengaturan ini digunakan untuk menentukan aplikasi validasi biometrik apa yang mau dibuka secara default.  

values: `frista|fingerprint`  
default: `"frista"`  

### `TOMBOLDIMATIKAN`
Pengaturan ini digunakan untuk mengatur menu apa yang dimatikan dari halaman depan APM.  

values: List menu, dipisah dengan koma, `antrian|cekin|daftarpoli|seppertama|sepkontrol|sepbedapoli|mobilejkn|satusehat`  
default: `"cekin,satusehat"`  
