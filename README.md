# S7 PLC Reader

Siemens S7 PLC'den veri okuyup JSON dosyasına yazan bağımsız Java uygulaması.

---

## Kurulum

1. `s7reader.jar` ve `config.json` dosyalarını aynı klasöre koy
2. `config.json` içindeki PLC bilgilerini düzenle
3. Çalıştır:
```
java -jar s7reader.jar
```

Durdurmak için: `Ctrl+C`

---

## config.json Yapısı

```json
{
  "plc": {
    "ip": "10.6.51.61",        // PLC IP adresi
    "rack": 0,                  // Rack numarası
    "slot": 1,                  // Slot numarası
    "readIntervalMs": 10000     // Okuma aralığı (ms)
  },
  "output": {
    "jsonFilePath": "output/plc_data.json",  // Çıktı dosyası
    "maxEntriesPerFile": 10000               // Dosya rotasyon limiti
  },
  "variables": [
    {
      "name": "KM17TOTALCOUNT",   // Değişken adı
      "areaType": "DB",           // DB / MB / IB / QB
      "dbNumber": 22,             // DB numarası
      "startIndex": 0,            // Byte indeksi
      "dataType": "INT"           // BOOL/BYTE/INT/DINT/REAL/STRING
    }
  ]
}
```

---

## Çıktı Formatı

Her okuma `output/plc_data.json` dosyasına satır satır yazılır:

```json
{"timestamp":"2026-04-24T10:00:00Z","deviceName":"Device1","variableName":"KM17TOTALCOUNT","dataType":"INT","value":1450,"readSuccess":true}
```

Hata durumunda:
```json
{"timestamp":"2026-04-24T10:00:00Z","variableName":"KM17TOTALCOUNT","readSuccess":false,"errorMessage":"..."}
```

---

## Loglar

`logs/s7reader.log` dosyasına yazılır, 30 gün saklanır.
