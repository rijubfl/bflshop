package com.bflgroup.bflshop.ui.pricecheck;

public class PriceCheckScanDetail {

    public static PriceCheckScanDetail instance;
    private static String itemcode;
    private static String description;
    private static String group;
    private static float price;
    private static int stock;
    private static String department;
    private static String division;
    private static String message;
    private static String brand;
    private static String iClass;
    private static String subclass;
    private static String season;

    public static String getItemcode() {
        return itemcode;
    }

    public static void setItemcode(String itemcode) {
        PriceCheckScanDetail.itemcode = itemcode;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        PriceCheckScanDetail.description = description;
    }

    public static String getGroup() {
        return group;
    }

    public static void setGroup(String group) {
        PriceCheckScanDetail.group = group;
    }

    public static float getPrice() {
        return price;
    }

    public static void setPrice(float price) {
        PriceCheckScanDetail.price = price;
    }

    public static int getStock() {
        return stock;
    }

    public static void setStock(int stock) {
        PriceCheckScanDetail.stock = stock;
    }

    public static String getDepartment() {
        return department;
    }

    public static void setDepartment(String department) {
        PriceCheckScanDetail.department = department;
    }

    public static String getDivision() {
        return division;
    }

    public static void setDivision(String division) {
        PriceCheckScanDetail.division = division;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        PriceCheckScanDetail.message = message;
    }

    public static String getBrand() {
        return brand;
    }

    public static void setBrand(String brand) {
        PriceCheckScanDetail.brand = brand;
    }

    public static String getiClass() {
        return iClass;
    }

    public static void setiClass(String iClass) {
        PriceCheckScanDetail.iClass = iClass;
    }

    public static String getSubclass() {
        return subclass;
    }

    public static void setSubclass(String subclass) {
        PriceCheckScanDetail.subclass = subclass;
    }

    public static String getSeason() {
        return season;
    }

    public static void setSeason(String season) {
        PriceCheckScanDetail.season = season;
    }

    public static synchronized PriceCheckScanDetail getInstance() {
        if (instance == null) {
            instance = new PriceCheckScanDetail();
        }
        return instance;
    }
}