package com.ias.s7reader.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ias.s7reader.model.OutputConfig;
import com.ias.s7reader.model.PlcDataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonWriter {

    private static final Logger log = LoggerFactory.getLogger(JsonWriter.class);
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final OutputConfig config;
    private final ObjectMapper mapper;

    private File currentFile;
    private int entryCount = 0;

    public JsonWriter(OutputConfig config) {
        this.config = config;
        this.mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        initOutputDir();
        initCurrentFile();
    }

    public void write(List<PlcDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        for (PlcDataRecord record : records) {
            writeRecord(record);
        }
    }

    private void writeRecord(PlcDataRecord record) {
        try {
            if (config.isRotateLogs() && entryCount >= config.getMaxEntriesPerFile()) {
                rotatFile();
            }

            String line = mapper.writeValueAsString(record);

            try (PrintWriter pw = new PrintWriter(new FileWriter(currentFile, true))) {
                pw.println(line);
            }

            entryCount++;

            if (entryCount % 100 == 0) {
                log.debug("JSON dosyasina {} kayit yazildi: {}", entryCount, currentFile.getName());
            }

        } catch (IOException e) {
            log.error("JSON yazma hatasi: {}", e.getMessage(), e);
        }
    }

    private void initOutputDir() {
        File outputFile = new File(config.getJsonFilePath());
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                log.info("Cikti dizini olusturuldu: {}", parentDir.getAbsolutePath());
            }
        }
    }

    private void initCurrentFile() {
        currentFile = new File(config.getJsonFilePath());
        entryCount = 0;
        log.info("JSON cikti dosyasi: {}", currentFile.getAbsolutePath());
    }

    private void rotatFile() {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        File base = new File(config.getJsonFilePath());
        String name = base.getName().replace(".json", "");
        File rotated = new File(base.getParent(), name + "_" + timestamp + ".json");

        if (currentFile.renameTo(rotated)) {
            log.info("Dosya rotasyonu: {} -> {}", currentFile.getName(), rotated.getName());
        }

        currentFile = base;
        entryCount = 0;
    }

    public File getCurrentFile() {
        return currentFile;
    }
}
