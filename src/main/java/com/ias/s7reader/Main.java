package com.ias.s7reader;

import com.ias.s7reader.config.ConfigLoader;
import com.ias.s7reader.core.ReadLoop;
import com.ias.s7reader.model.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * S7 PLC Reader - Ana giris noktasi.
 *
 * Kullanim:
 *   java -jar s7reader.jar
 *   java -jar s7reader.jar --config /path/to/config.json
 *   java -jar s7reader.jar --config config.json --ask
 *
 * Parametreler:
 *   --config <yol>   Konfigurasyon dosyasinin yolu (varsayilan: config.json)
 *   --ask            PLC IP/rack/slot degerlerini konsoldan sor
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("  S7 PLC Reader  v1.0.0");
        System.out.println("  Snap7/Moka7 tabanli, bagimsiz okuyucu");
        System.out.println("=========================================");

        String configPath = null;
        boolean askInteractive = false;

        // CLI argumanlari parse et
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config":
                    if (i + 1 < args.length) {
                        configPath = args[++i];
                    } else {
                        System.err.println("HATA: --config parametresi bir dosya yolu bekliyor.");
                        System.exit(1);
                    }
                    break;
                case "--ask":
                    askInteractive = true;
                    break;
                case "--help":
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Bilinmeyen parametre: " + args[i]);
                    printHelp();
                    System.exit(1);
            }
        }

        try {
            AppConfig config = ConfigLoader.load(configPath, askInteractive);

            log.info("PLC: {} | Rack: {} | Slot: {}",
                    config.getPlc().getIp(),
                    config.getPlc().getRack(),
                    config.getPlc().getSlot());
            log.info("Degisken sayisi: {}", config.getVariables().size());
            log.info("Okuma araligi: {} ms", config.getPlc().getReadIntervalMs());

            ReadLoop loop = new ReadLoop(config);
            loop.start();

        } catch (IllegalArgumentException e) {
            System.err.println("Konfigurasyon hatasi: " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            log.error("Uygulama baslatma hatasi: {}", e.getMessage(), e);
            System.exit(3);
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Kullanim:");
        System.out.println("  java -jar s7reader.jar [parametreler]");
        System.out.println();
        System.out.println("Parametreler:");
        System.out.println("  --config <dosya>   Konfigurasyon dosyasi yolu");
        System.out.println("                     (varsayilan: calisma dizinindeki config.json)");
        System.out.println("  --ask              PLC baglanti bilgilerini konsoldan sor");
        System.out.println("  --help             Bu yardim mesajini goster");
        System.out.println();
        System.out.println("Ornekler:");
        System.out.println("  java -jar s7reader.jar");
        System.out.println("  java -jar s7reader.jar --config /etc/plc/config.json");
        System.out.println("  java -jar s7reader.jar --ask");
        System.out.println();
    }
}
