package com.bflgroup.bflshop.ui.ageingslashing.model;

public class AgeingSlashingScanDetailsGlobal {

    public static AgeingSlashingScanDetailsGlobal instance;
    private static String itemName;
    private static String department;
    private static String division;
    private static String groupCode;
    private static String groupName;
    private static float currPrice;
    private static float newPrice;
    private static String labelType;
    private static String slashingType;
    private static float WasPrice;
    private static float savePercentage;
    private static String trfNo;
    private static String trfDate;
    private static int dateDiff;
    private static String newTrfNo;
    private static String newBarcode;
    private static String oldBarcode;
    private static int eligibleQty;
    private static int scanQty;
    private static int slashQty;
    private static String addInfo;

    private static String rfidScanBarcode;

    public static String getItemName() {
        return itemName;
    }

    public static void setItemName(String itemName) {
        AgeingSlashingScanDetailsGlobal.itemName = itemName;
    }

    public static String getDepartment() {
        return department;
    }

    public static void setDepartment(String department) {
        AgeingSlashingScanDetailsGlobal.department = department;
    }

    public static String getDivision() {
        return division;
    }

    public static void setDivision(String division) {
        AgeingSlashingScanDetailsGlobal.division = division;
    }

    public static String getGroupCode() {
        return groupCode;
    }

    public static void setGroupCode(String groupCode) {
        AgeingSlashingScanDetailsGlobal.groupCode = groupCode;
    }

    public static String getGroupName() {
        return groupName;
    }

    public static void setGroupName(String groupName) {
        AgeingSlashingScanDetailsGlobal.groupName = groupName;
    }

    public static float getCurrPrice() {
        return currPrice;
    }

    public static void setCurrPrice(float currPrice) {
        AgeingSlashingScanDetailsGlobal.currPrice = currPrice;
    }

    public static float getNewPrice() {
        return newPrice;
    }

    public static void setNewPrice(float newPrice) {
        AgeingSlashingScanDetailsGlobal.newPrice = newPrice;
    }

    public static String getLabelType() {
        return labelType;
    }

    public static void setLabelType(String labelType) {
        AgeingSlashingScanDetailsGlobal.labelType = labelType;
    }

    public static String getSlashingType() {
        return slashingType;
    }

    public static void setSlashingType(String slashingType) {
        AgeingSlashingScanDetailsGlobal.slashingType = slashingType;
    }

    public static float getWasPrice() {
        return WasPrice;
    }

    public static void setWasPrice(float wasPrice) {
        WasPrice = wasPrice;
    }

    public static String getNewTrfNo() {
        return newTrfNo;
    }

    public static void setNewTrfNo(String newTrfNo) {
        AgeingSlashingScanDetailsGlobal.newTrfNo = newTrfNo;
    }

    public static String getNewBarcode() {
        return newBarcode;
    }

    public static void setNewBarcode(String newBarcode) {
        AgeingSlashingScanDetailsGlobal.newBarcode = newBarcode;
    }

    public static int getDateDiff() {
        return dateDiff;
    }

    public static void setDateDiff(int dateDiff) {
        AgeingSlashingScanDetailsGlobal.dateDiff = dateDiff;
    }

    public static String getTrfDate() {
        return trfDate;
    }

    public static void setTrfDate(String trfDate) {
        AgeingSlashingScanDetailsGlobal.trfDate = trfDate;
    }

    public static String getTrfNo() {
        return trfNo;
    }

    public static void setTrfNo(String trfNo) {
        AgeingSlashingScanDetailsGlobal.trfNo = trfNo;
    }

    public static String getOldBarcode() {
        return oldBarcode;
    }

    public static void setOldBarcode(String oldBarcode) {
        AgeingSlashingScanDetailsGlobal.oldBarcode = oldBarcode;
    }

    public static int getEligibleQty() {
        return eligibleQty;
    }

    public static void setEligibleQty(int eligibleQty) {
        AgeingSlashingScanDetailsGlobal.eligibleQty = eligibleQty;
    }

    public static int getScanQty() {
        return scanQty;
    }

    public static void setScanQty(int scanQty) {
        AgeingSlashingScanDetailsGlobal.scanQty = scanQty;
    }

    public static int getSlashQty() {
        return slashQty;
    }

    public static void setSlashQty(int slashQty) {
        AgeingSlashingScanDetailsGlobal.slashQty = slashQty;
    }

    public static float getSavePercentage() {
        return savePercentage;
    }

    public static void setSavePercentage(float savePercentage) {
        AgeingSlashingScanDetailsGlobal.savePercentage = savePercentage;
    }

    public static String getRfidScanBarcode() {
        return rfidScanBarcode;
    }

    public static void setRfidScanBarcode(String rfidScanBarcode) {
        AgeingSlashingScanDetailsGlobal.rfidScanBarcode = rfidScanBarcode;
    }

    public static String getAddInfo() {
        return addInfo;
    }

    public static void setAddInfo(String addInfo) {
        AgeingSlashingScanDetailsGlobal.addInfo = addInfo;
    }



    public static synchronized AgeingSlashingScanDetailsGlobal getInstance() {
        if (instance == null) {
            instance = new AgeingSlashingScanDetailsGlobal();
        }
        return instance;
    }
}
