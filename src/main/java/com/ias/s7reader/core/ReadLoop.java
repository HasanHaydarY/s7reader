package com.ias.s7reader.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ias.s7reader.model.AppConfig;
import com.ias.s7reader.model.PlcDataRecord;
import com.ias.s7reader.output.JsonWriter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadLoop {

    private static final Logger log = LoggerFactory.getLogger(ReadLoop.class);

    private final AppConfig config;
    private final S7Reader reader;
    private final JsonWriter writer;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public ReadLoop(AppConfig config) {
        this.config = config;
        this.reader = new S7Reader(config);
        this.writer = new JsonWriter(config.getOutput());
    }

    public void start() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Kapatma sinyali alindi. Durdurluyor...");
            running.set(false);
            reader.disconnect();
        }));

        int intervalMs = config.getPlc().getReadIntervalMs();
        log.info("Okuma dongusu baslatiliyor. Aralik: {} ms", intervalMs);
        log.info("Cikti dosyasi: {}", config.getOutput().getJsonFilePath());
        log.info("Durdurmak icin Ctrl+C");

        long cycleCount = 0;

        while (running.get()) {
            long cycleStart = System.currentTimeMillis();

            try {
                List<PlcDataRecord> records = reader.readAll();
                writer.write(records);

                cycleCount++;

                if (cycleCount % 10 == 0) {
                    long successCount = records.stream().filter(PlcDataRecord::isReadSuccess).count();
                    long failCount = records.size() - successCount;
                    log.info("Dongü #{}: {} basarili / {} hatali okuma", cycleCount, successCount, failCount);
                }

            } catch (Exception e) {
                log.error("Okuma dongusunde beklenmeyen hata: {}", e.getMessage(), e);
            }

            long elapsed = System.currentTimeMillis() - cycleStart;
            long sleepTime = intervalMs - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Dongu uyku kesildi, cikiliyor.");
                    break;
                }
            } else {
                log.warn("Okuma suresi araliktan uzun: {}ms > {}ms aralik", elapsed, intervalMs);
            }
        }

        log.info("Okuma dongusu tamamlandi.");
    }
}
