package com.bflgroup.bflshop.comm;

public class Company {

    public static Company instance;

    private static String TaxHead;
    private static String CompanyShort;
    private static String CompanyName;
    private static String Address;
    private static String AddressSub;
    private static String TelHead;
    private static String TelNo;
    private static String TelNoSub;
    private static String CompanyNameSub;
    private static String FaxNo;
    private static String Email;
    private static String SubTitle;
    private static String TaxAddressHead;
    private static String TaxAddress;
    private static String TaxAddress1;
    private static String TRNNoHead;
    private static String TRNNo;
    private static String VATEX;

    public static synchronized Company getInstance() {
        if (instance == null) {
            instance = new Company();
        }
        return instance;
    }

    public static String getCompanyShort() {
        return CompanyShort;
    }

    public static void setCompanyShort(String companyShort) {
        CompanyShort = companyShort;
    }

    public static String getCompanyName() {
        return CompanyName;
    }

    public static void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public static String getCompanyNameSub() {
        return CompanyNameSub;
    }

    public static void setCompanyNameSub(String companyNameSub) {
        CompanyNameSub = companyNameSub;
    }

    public static String getSubTitle() {
        return SubTitle;
    }

    public static void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public static String getTaxAddress() {
        return TaxAddress;
    }

    public static void setTaxAddress(String taxAddress) {
        TaxAddress = taxAddress;
    }

    public static String getTaxAddress1() {
        return TaxAddress1;
    }

    public static void setTaxAddress1(String taxAddress1) {
        TaxAddress1 = taxAddress1;
    }

    public static String getTRNNo() {
        return TRNNo;
    }

    public static void setTRNNo(String TRNNo) {
        Company.TRNNo = TRNNo;
    }

    public static String getVATEX() {
        return VATEX;
    }

    public static void setVATEX(String VATEX) {
        Company.VATEX = VATEX;
    }

    public static String getTaxHead() {
        return TaxHead;
    }

    public static void setTaxHead(String taxHead) {
        TaxHead = taxHead;
    }

    public static String getAddress() {
        return Address;
    }

    public static void setAddress(String address) {
        Address = address;
    }

    public static String getTelNo() {
        return TelNo;
    }

    public static void setTelNo(String telNo) {
        TelNo = telNo;
    }

    public static String getAddressSub() {
        return AddressSub;
    }

    public static void setAddressSub(String addressSub) {
        AddressSub = addressSub;
    }

    public static String getTelNoSub() {
        return TelNoSub;
    }

    public static void setTelNoSub(String telNoSub) {
        TelNoSub = telNoSub;
    }

    public static String getFaxNo() {
        return FaxNo;
    }

    public static void setFaxNo(String faxNo) {
        FaxNo = faxNo;
    }

    public static String getEmail() {
        return Email;
    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static String getTelHead() {
        return TelHead;
    }

    public static void setTelHead(String telHead) {
        TelHead = telHead;
    }

    public static String getTaxAddressHead() {
        return TaxAddressHead;
    }

    public static void setTaxAddressHead(String taxAddressHead) {
        TaxAddressHead = taxAddressHead;
    }

    public static String getTRNNoHead() {
        return TRNNoHead;
    }

    public static void setTRNNoHead(String TRNNoHead) {
        Company.TRNNoHead = TRNNoHead;
    }
}
