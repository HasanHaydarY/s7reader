package com.ias.s7reader.core;

import com.ias.s7reader.model.AppConfig;
import com.ias.s7reader.model.PlcConfig;
import com.ias.s7reader.model.PlcDataRecord;
import com.ias.s7reader.model.VariableConfig;
import com.ias.s7reader.util.S7AreaType;
import com.ias.s7reader.util.S7ConvertUtils;
import com.sourceforge.snap7.moka7.S7Client;
import com.sourceforge.snap7.moka7.S7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class S7Reader {

    private static final Logger log = LoggerFactory.getLogger(S7Reader.class);

    private final PlcConfig plcConfig;
    private final List<VariableConfig> variables;
    private S7Client client;
    private boolean connected = false;

    public S7Reader(AppConfig config) {
        this.plcConfig = config.getPlc();
        this.variables = config.getVariables();
    }

    public boolean connect() {
        try {
            if (client == null) {
                client = new S7Client();
            }

            if (connected && client.Connected) {
                return true;
            }

            log.info("PLC'ye baglaniliyor: {}  rack={} slot={}",
                    plcConfig.getIp(), plcConfig.getRack(), plcConfig.getSlot());

            int result = client.ConnectTo(
                    plcConfig.getIp(),
                    plcConfig.getRack(),
                    plcConfig.getSlot()
            );

            if (result == 0) {
                connected = true;
                log.info("PLC baglantisi basarili: {}", plcConfig.getIp());
                return true;
            } else {
                connected = false;
                log.error("PLC baglantisi basarisiz. Hata kodu: {}  Aciklama: {}",
                        result, S7Client.ErrorText(result));
                return false;
            }

        } catch (Exception e) {
            connected = false;
            log.error("PLC baglantisinda beklenmeyen hata: {}", e.getMessage(), e);
            return false;
        }
    }

    public void disconnect() {
        try {
            if (client != null) {
                client.Disconnect();
                connected = false;
                log.info("PLC baglantisi kapatildi: {}", plcConfig.getIp());
            }
        } catch (Exception e) {
            log.warn("Disconnect sirasinda hata: {}", e.getMessage());
        }
    }

    public boolean ensureConnected() {
        if (client == null || !client.Connected) {
            connected = false;
            return connect();
        }
        return true;
    }

    public boolean isConnected() {
        return connected && client != null && client.Connected;
    }

    public List<PlcDataRecord> readAll() {
        List<PlcDataRecord> records = new ArrayList<>();

        if (!ensureConnected()) {
            log.warn("PLC baglantisi yok, okuma atlanıyor.");
            for (VariableConfig var : variables) {
                records.add(PlcDataRecord.failure(
                        var.getDeviceName(),
                        var.getName(),
                        var.getDataType(),
                        "PLC baglantisi kurulamadi: " + plcConfig.getIp()
                ));
            }
            return records;
        }

        for (VariableConfig var : variables) {
            records.add(readVariable(var));
        }

        return records;
    }

    private PlcDataRecord readVariable(VariableConfig var) {
        try {
            Object value = readValue(var);
            return PlcDataRecord.success(
                    var.getDeviceName(),
                    var.getName(),
                    var.getDataType(),
                    value,
                    var.getDescription()
            );
        } catch (S7ReadException e) {
            log.error("Okuma hatasi [{}]: kod={} mesaj={}",
                    var.getName(), e.getErrorCode(), e.getMessage());
            if (isConnectionError(e.getErrorCode())) {
                log.warn("Baglanti hatasi, sifirlanıyor...");
                disconnect();
            }
            return PlcDataRecord.failure(
                    var.getDeviceName(),
                    var.getName(),
                    var.getDataType(),
                    e.getMessage() + " (kod: " + e.getErrorCode() + ")"
            );
        } catch (Exception e) {
            log.error("Beklenmeyen hata [{}]: {}", var.getName(), e.getMessage(), e);
            return PlcDataRecord.failure(
                    var.getDeviceName(),
                    var.getName(),
                    var.getDataType(),
                    "Beklenmeyen hata: " + e.getMessage()
            );
        }
    }

    private Object readValue(VariableConfig var) throws S7ReadException {

        S7AreaType areaType = S7AreaType.fromString(var.getAreaType());
        int area  = areaType.getSnap7Code();
        int db    = var.getDbNumber();
        int start = var.getStartIndex();
        String dtype = var.getDataType().toUpperCase();

        byte[] data;
        int errorCode;

        switch (dtype) {

            case "BOOL": {
                data = new byte[1];
                errorCode = client.ReadArea(area, db, start, 1, data);
                checkError(errorCode, var.getName());
                return S7ConvertUtils.getBoolean(data, 0, var.getBitNo());
            }

            case "BYTE": {
                data = new byte[1];
                errorCode = client.ReadArea(area, db, start, 1, data);
                checkError(errorCode, var.getName());
                return S7ConvertUtils.getByteAt(data, 0);
            }

            case "CHAR": {
                data = new byte[1];
                errorCode = client.ReadArea(area, db, start, 1, data);
                checkError(errorCode, var.getName());
                return S7ConvertUtils.getCharAt(data, 0);
            }

            case "WORD": {
                data = new byte[2];
                errorCode = client.ReadArea(area, db, start, 2, data);
                checkError(errorCode, var.getName());
                return S7.GetWordAt(data, 0);
            }

            case "INT": {
                data = new byte[2];
                errorCode = client.ReadArea(area, db, start, 2, data);
                checkError(errorCode, var.getName());
                return S7.GetShortAt(data, 0);
            }

            case "DWORD": {
                data = new byte[4];
                errorCode = client.ReadArea(area, db, start, 4, data);
                checkError(errorCode, var.getName());
                return S7.GetDWordAt(data, 0);
            }

            case "DINT": {
                data = new byte[4];
                errorCode = client.ReadArea(area, db, start, 4, data);
                checkError(errorCode, var.getName());
                return S7.GetDIntAt(data, 0);
            }

            case "REAL": {
                data = new byte[4];
                errorCode = client.ReadArea(area, db, start, 4, data);
                checkError(errorCode, var.getName());
                return S7.GetFloatAt(data, 0);
            }

            case "STRING": {
                data = new byte[2];
                errorCode = client.ReadArea(area, db, start, 2, data);
                checkError(errorCode, var.getName());

                int strLength = S7ConvertUtils.getStringLengthAt(data);
                if (strLength <= 0) return "";

                byte[] data2 = new byte[strLength];
                errorCode = client.ReadArea(area, db, start + 2, strLength, data2);
                checkError(errorCode, var.getName());
                return S7ConvertUtils.getStringAt(data2);
            }

            default:
                log.warn("Bilinmeyen veri tipi: {} (degisken: {})", dtype, var.getName());
                return null;
        }
    }

    private void checkError(int errorCode, String variableName) throws S7ReadException {
        if (errorCode != 0) {
            throw new S7ReadException(
                    "PLC okuma hatasi [" + variableName + "]: " + S7Client.ErrorText(errorCode),
                    errorCode
            );
        }
    }

    private boolean isConnectionError(int errorCode) {
        return errorCode == S7Client.errTCPConnectionFailed
                || errorCode == S7Client.errTCPConnectionReset
                || errorCode == S7Client.errTCPDataRecv
                || errorCode == S7Client.errTCPDataRecvTout
                || errorCode == S7Client.errTCPDataSend
                || errorCode == S7Client.errISOConnectionFailed
                || errorCode == S7Client.errISOInvalidPDU
                || errorCode == S7Client.errISONegotiatingPDU;
    }
}