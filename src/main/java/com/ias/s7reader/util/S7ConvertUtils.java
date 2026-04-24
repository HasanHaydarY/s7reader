package com.ias.s7reader.util;

import java.io.UnsupportedEncodingException;

public class S7ConvertUtils {

    private S7ConvertUtils() {}

    public static int getBoolean(byte[] data, int position, int bitNo) {
        if (data == null || position >= data.length) return 0;
        return ((data[position] >> bitNo) & 0x01);
    }

    public static long getByteAt(byte[] buffer, int pos) {
        long result = (long) (0x0000);
        result <<= 8;
        result += (long) (0x0000);
        result <<= 8;
        result += (long) (0x0000);
        result <<= 8;
        result += (long) (buffer[pos] & 0x00FF);
        return result;
    }

    public static char getCharAt(byte[] buffer, int pos) {
        long result = (long) (0x0000);
        result <<= 8;
        result += (long) (0x0000);
        result <<= 8;
        result += (long) (0x0000);
        result <<= 8;
        result += (long) (buffer[pos] & 0x00FF);
        return (char) result;
    }

    public static int getStringLengthAt(byte[] buffer) {
        return ((0x0000 << 8) + (buffer[1] & 0x00FF));
    }

    public static int getStringMaxLengthAt(byte[] buffer) {
        return ((0x0000 << 8) + (buffer[0] & 0x00FF));
    }

    public static String getStringAt(byte[] buffer) {
        try {
            return new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static void setByteAt(byte[] buffer, int pos, int value) {
        buffer[pos] = (byte) (value & 0x00FF);
    }

    public static void setCharAt(byte[] buffer, int pos, char value) {
        buffer[pos] = (byte) value;
    }

    public static void setStringAt(byte[] buffer, String value, int maxLength) {
        byte[] byteVals = new byte[maxLength];
        int strLen = (value.length() <= maxLength) ? value.length() : maxLength;
        try {
            byteVals = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            buffer[1] = (byte) 0;
        }
        buffer[0] = (byte) maxLength;
        buffer[1] = (byte) strLen;
        System.arraycopy(byteVals, 0, buffer, 2, strLen);
    }
}
