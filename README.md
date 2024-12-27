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
<entry key="PRINTER_REGISTRASI"></entry>
<entry key="PRINTER_BARCODE"></entry>
<entry key="PRINTERJUMLAHBARCODE">3</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS">D:\BPJS Kesehatan\Aplikasi Sidik Jari BPJS Kesehatan\After.exe</entry>
<entry key="URLAPLIKASIFRISTABPJS">D:\BPJS Kesehatan\Aplikasi FRISTA BPJS Kesehatan\frista.exe</entry>
<entry key="USERFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="PASSWORDFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
```

#### key "PRINTER_BARCODE"
Digunakan untuk mengetahui nama printer untuk mencetak barcode.

#### key "PRINTER_REGISTRASI"
Digunakan untuk mengetahui nama printer untuk mencetak lembar registrasi pasien dan SEP pasien.

#### key "PRINTERJUMLAHBARCODE"
Digunakan untuk mengatur nilai default jumlah barcode yang mau dicetak.

#### key "URLAPLIKASIFINGERPRINTBPJS"
Berisi path ke aplikasi fingerprint BPJS.

#### key "URLAPLIKASIFRISTABPJS"
Berisi path ke aplikasi FRISTA BPJS.

#### key "USERFINGERPRINTBPJS" dan "PASSFINGERPRINTBPJS"
Berisi kredensial login username dan password aplikasi fingerprint BPJS, dengan kredensial dienkripsi menggunakan enkripsi dari SIMRS Khanza.
