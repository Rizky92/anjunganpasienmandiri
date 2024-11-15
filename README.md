## APM Custom
Aplikasi anjungan pasien mandiri (APM) modifikasi dari [APM RS Indriati Boyolali](https://github.com/abdulrokhimrepo/anjunganmandiriSEP).

### Requirements
- [Apache Netbeans](https://netbeans.apache.org/front/main/download/index.html)
- Liberica JDK 17 [Download](https://github.com/bell-sw/Liberica/releases?q=17.0&expanded=true)
- Aplikasi Fingerprint BPJS v2.0+
- Library yang digunakan bisa di download [disini](https://drive.google.com/drive/folders/1bLKuw8l9k5ElC5dxxlrXijACPLtNmCTg?usp=sharing).  

### Konfigurasi
Berikut adalah konfigurasi yang disediakan dalam file `apm.xml`:
```xml
<entry key="URLFINGERPRINTBPJS">https://fp.bpjs-kesehatan.go.id/finger-rest/</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS">C:\Program Files (x86)\Aplikasi Sidik Jari BPJS Kesehatan\After.exe</entry>
<entry key="USERFINGERPRINTBPJS"></entry>
<entry key="PASSFINGERPRINTBPJS"></entry>
<entry key="PRINTER_BARCODE"></entry>
<entry key="PRINTER_REGISTRASI"></entry>
```

#### key "URLFINGERPRINTBPJS"
Digunakan untuk mengakses URL API Fingerprint BPJS.

#### key "URLAPLIKASIFINGERPRINTBPJS"
Berisi path ke aplikasi fingerprint BPJS

#### key "USERFINGERPRINTBPJS" dan "PASSFINGERPRINTBPJS"
Berisi kredensial login username dan password aplikasi fingerprint BPJS, dengan kredensial dienkripsi menggunakan enkripsi dari SIMRS Khanza.

#### key "PRINTER_BARCODE"
Digunakan untuk mengetahui nama printer service untuk mencetak barcode.

#### key "PRINTER_REGISTRASI"
Digunakan untuk mengetahui nama printer service untuk mencetak lembar registrasi pasien dan SEP pasien.
