package com.ias.s7reader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ias.s7reader.model.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Uygulama konfigurasyonunu yukler.
 *
 * Oncelik sirasi:
 *   1. CLI argumani ile belirtilen dosya yolu
 *   2. Calisma dizinindeki config.json
 *   3. JAR icindeki varsayilan config.json (resources)
 *
 * Eger PLC IP'si config'de "ASK" ise veya bos ise,
 * konsoldan kullanicidan deger istenir.
 */
public class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Konfigurasyonu yukler.
     *
     * @param configPath null ise otomatik arama yapar
     * @param askInteractive true ise eksik degerler icin kullanicidan sorar
     */
    public static AppConfig load(String configPath, boolean askInteractive) throws Exception {

        AppConfig config = loadFromFile(configPath);

        if (askInteractive) {
            promptMissingValues(config);
        }

        validate(config);
        return config;
    }

    private static AppConfig loadFromFile(String configPath) throws Exception {

        // 1. CLI argümani
        if (configPath != null) {
            File f = new File(configPath);
            if (f.exists()) {
                log.info("Konfigurasyon yukleniyor: {}", f.getAbsolutePath());
                return mapper.readValue(f, AppConfig.class);
            } else {
                throw new IllegalArgumentException("Konfigurasyon dosyasi bulunamadi: " + configPath);
            }
        }

        // 2. Calısma dizini
        File local = new File("config.json");
        if (local.exists()) {
            log.info("Konfigurasyon yukleniyor: {}", local.getAbsolutePath());
            return mapper.readValue(local, AppConfig.class);
        }

        // 3. JAR icindeki varsayilan
        InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.json");
        if (is != null) {
            log.info("Varsayilan konfigurasyon (JAR ici) yukleniyor.");
            return mapper.readValue(is, AppConfig.class);
        }

        throw new IllegalStateException("config.json bulunamadi! " +
                "JAR ile ayni dizine koyun veya --config parametresi kullanin.");
    }

    /**
     * Bos veya "ASK" olan PLC degerlerini konsoldan sorar.
     */
    private static void promptMissingValues(AppConfig config) {
        Scanner scanner = new Scanner(System.in);

        String ip = config.getPlc().getIp();
        if (ip == null || ip.trim().isEmpty() || "ASK".equalsIgnoreCase(ip.trim())) {
            System.out.print("PLC IP adresi: ");
            config.getPlc().setIp(scanner.nextLine().trim());
        }

        if (config.getPlc().getRack() < 0) {
            System.out.print("Rack numarasi [varsayilan 0]: ");
            String rack = scanner.nextLine().trim();
            config.getPlc().setRack(rack.isEmpty() ? 0 : Integer.parseInt(rack));
        }

        if (config.getPlc().getSlot() < 0) {
            System.out.print("Slot numarasi [varsayilan 1]: ");
            String slot = scanner.nextLine().trim();
            config.getPlc().setSlot(slot.isEmpty() ? 1 : Integer.parseInt(slot));
        }
    }

    private static void validate(AppConfig config) {
        if (config.getPlc() == null) {
            throw new IllegalArgumentException("config.json icinde 'plc' blogu eksik!");
        }
        String ip = config.getPlc().getIp();
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("PLC IP adresi bos olamaz!");
        }
        if (config.getVariables() == null || config.getVariables().isEmpty()) {
            throw new IllegalArgumentException("config.json icinde hic 'variables' tanimlanmamis!");
        }
        if (config.getOutput() == null) {
            throw new IllegalArgumentException("config.json icinde 'output' blogu eksik!");
        }
        log.info("Konfigurasyon gecerli. {} degisken tanimli.", config.getVariables().size());
    }
}
