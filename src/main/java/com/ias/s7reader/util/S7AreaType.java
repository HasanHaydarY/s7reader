package com.ias.s7reader.util;

import com.sourceforge.snap7.moka7.S7;

public enum S7AreaType {

    DB(S7.S7AreaDB),  
    MB(S7.S7AreaMK),   
    IB(S7.S7AreaPE),
    QB(S7.S7AreaPA);

    private final int snap7Code;

    S7AreaType(int snap7Code) {
        this.snap7Code = snap7Code;
    }

    public int getSnap7Code() {
        return snap7Code;
    }

    public static S7AreaType fromString(String value) {
        for (S7AreaType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Bilinmeyen area tipi: " + value +
                " (Gecerli degerler: DB, MB, IB, QB)");
    }
}
