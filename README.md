## APM Custom
Aplikasi anjungan pasien mandiri (APM) modifikasi dari [APM RS Indriati Boyolali](https://github.com/abdulrokhimrepo/anjunganmandiriSEP)

### Requirements
- [Apache Netbeans](https://netbeans.apache.org/front/main/download/index.html)
- Liberica JDK 15 [Download](https://github.com/bell-sw/Liberica/releases/tag/15.0.2%2B10)
- Aplikasi Fingerprint BPJS v2.0

### Konfigurasi
Tambahkan konfigurasi berikut dalam file `database.xml`:
```xml
<entry key="URLFINGERPRINTBPJS">https://fp.bpjs-kesehatan.go.id/finger-rest/</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS">C:\Program Files (x86)\Aplikasi Sidik Jari BPJS Kesehatan\After.exe</entry>
<entry key="USERFINGERPRINTBPJS"></entry>
<entry key="PASSWORDFINGERPRINTBPJS"></entry>
```

> [!NOTE]
> `USERFINGERPRINTBPJS` dan `PASSWORDFINGERPRINTBPJS` diisi dengan kredensial aplikasi fingerprint BPJS dan dienkripsi menggunakan enkripsi dari SIMRS Khanza.
