package com.bflgroup.bflshop.comm;

import java.sql.Connection;
import java.util.List;

public class Global {

    public static Global instance;

    private static Connection connection;
    private static Connection cloudCon;

    private static String sqUserName;
    private static String sqPassword;
    private static String errorMessage;
    private static String serverIP;
    private static String dbName;
    private static String cloudDbName;
    private static int userId;
    private static String userName;
    private static String empCode;
    private static String fcCode;
    private static float fcRate;
    private static String userRepCode;
    private static boolean enterQty;
    private static boolean hideKeyPad;
    private static String toteTrfNo;
    private static String deviceName;
    private static String serverDate;
    private static String officeType;
    private static String countryCode;
    private static String pdaversion;

    private List<String> bluetoothDevicesYellow;
    private List<String> bluetoothDevicesRed;
    private List<String> bluetoothDevicesWhite;

    public static String getServerDate() {
        return serverDate;
    }

    public static void setServerDate(String serverDate) {
        Global.serverDate = serverDate;
    }

    public static String getServerTime() {
        return serverTime;
    }

    public static void setServerTime(String serverTime) {
        Global.serverTime = serverTime;
    }

    private static String serverTime;

    public static void setInstance(Global instance) {
        Global.instance = instance;
    }

    public boolean getEnterQty() {
        return enterQty;
    }

    public void setEnterQty(boolean enterQty) {
        this.enterQty = enterQty;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getCloudCon() {
        return cloudCon;
    }

    public void setCloudCon(Connection cloudCon) {
        Global.cloudCon = cloudCon;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCloudDbName() {
        return cloudDbName;
    }

    public void setCloudDbName(String cloudDbName) {
        Global.cloudDbName = cloudDbName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getHideKeyPad() {
        return hideKeyPad;
    }

    public void setHideKeyPad(boolean hideKeyPad) {
        this.hideKeyPad = hideKeyPad;
    }

    public String getToteTrfNo() {
        return toteTrfNo;
    }

    public void setToteTrfNo(String toteTrfNo) {
        Global.toteTrfNo = toteTrfNo;
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        Global.deviceName = deviceName;
    }

    public static String getUserRepCode() {
        return userRepCode;
    }

    public static void setUserRepCode(String userRepCode) {
        Global.userRepCode = userRepCode;
    }

    public static String getFcCode() {
        return fcCode;
    }

    public static void setFcCode(String fcCode) {
        Global.fcCode = fcCode;
    }

    public static float getFcRate() {
        return fcRate;
    }

    public static void setFcRate(float fcRate) {
        Global.fcRate = fcRate;
    }

    public static String getEmpCode() {
        return empCode;
    }

    public static void setEmpCode(String empCode) {
        Global.empCode = empCode;
    }

    public static String getOfficeType() {
        return officeType;
    }

    public static void setOfficeType(String officeType) {
        Global.officeType = officeType;
    }

    public List<String> getBluetoothDevicesYellow() {
        return bluetoothDevicesYellow;
    }

    public void setBluetoothDevicesYellow(List<String> bluetoothDevicesYellow) {
        this.bluetoothDevicesYellow = bluetoothDevicesYellow;
    }

    public static String getPdaversion() {
        return pdaversion;
    }

    public static void setPdaversion(String pdaversion) {
        Global.pdaversion = pdaversion;
    }

    public List<String> getBluetoothDevicesRed() {
        return bluetoothDevicesRed;
    }

    public void setBluetoothDevicesRed(List<String> bluetoothDevicesRed) {
        this.bluetoothDevicesRed = bluetoothDevicesRed;
    }

    public List<String> getBluetoothDevicesWhite() {
        return bluetoothDevicesWhite;
    }

    public void setBluetoothDevicesWhite(List<String> bluetoothDevicesWhite) {
        this.bluetoothDevicesWhite = bluetoothDevicesWhite;
    }

    public static String getCountryCode() {
        return countryCode;
    }

    public static void setCountryCode(String countryCode) {
        Global.countryCode = countryCode;
    }

    public static String getSqUserName() {
        return sqUserName;
    }

    public static void setSqUserName(String sqUserName) {
        Global.sqUserName = sqUserName;
    }

    public static String getSqPassword() {
        return sqPassword;
    }

    public static void setSqPassword(String sqPassword) {
        Global.sqPassword = sqPassword;
    }

    public static synchronized Global getInstance() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }
}