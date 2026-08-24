package com.bflgroup.bflshop.comm;

import android.graphics.Bitmap;
import android.util.Log;

import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;


public class BarcodePrinting {
    private CPCLPrinter cpclPrinter;

    private Global objGlobal = Global.getInstance();

    public BarcodePrinting() {
        //cpclPrinter = new CPCLPrinter();    //Default = English.
        //cpclPrinter = new CPCLPrinter("EUC-KR"); // Korean.
        cpclPrinter = new CPCLPrinter("GB2312"); //Chinese.
        //WPC1256
    }

    public boolean PrintBarcodeImage(int count, int paper_type, Bitmap label) throws UnsupportedEncodingException {
        try {
            cpclPrinter.setForm(0, 200, 200, 406, 384, count);
            cpclPrinter.setMedia(paper_type);
            cpclPrinter.printBitmap(label, 0, 0);
            cpclPrinter.printForm();
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PrintBarcodeImage : " + e);
            return false;
        }
    }

    public boolean PrintBarcodeByte(byte[] printData) throws UnsupportedEncodingException {
        try {
            cpclPrinter.sendByte(printData);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PrintBarcodeByte : " + e);
            return false;
        }
    }

    /*
    1911 A 060 002 0074 Saving XX %
     │   │   │   └─ Y Position
     │   │   └──── X Position
     │   └────── Font/Style
     └────────── Text Command Prefix
     */
    private static final String ARABIC_PRICE_FONT_ID = "U50";
    public byte[] getSlashingBarcodeArabic(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String currency,String price, String mark,
                                           String usid, String pQty, String addinfo, String arabicDesc) {
        String mainHead1 = "1911A1000780019" + mainHead + "\r\n";
        String itemName1 = "1911A0600690007" + itemName + "\r\n";
        String addinfo1 = "1911A0600500007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600220007" + itemCode + "\r\n";
        //String barcode1 = "1e2102100450006C" + barcode + "\r\n";
        String barcode2 = "1W1j2101700320006" + barcode + "\r\n";
        String shopcode = "1911A0600280126" + subHead + "\r\n";
        String usid1 = "1911A0600100160" + mark + "  " + usid + "\r\n";
        String price1 = "1911A1200040007" + currency + " "+ price + "\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";
        byte[] arabicField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00630010" + "P007P007").getBytes(),
                arabicToUtf16BeBytes(arabicDesc));
        arabicField = addToDataVault(arabicField, "\r\n".getBytes());

        byte[] arabicPriceField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00120085" + "P012P012").getBytes(),
                arabicToUtf16BeBytes("ر.ق "+arabicizePrice(price)));
        arabicPriceField = addToDataVault(arabicPriceField, "\r\n".getBytes());

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());
        // FB+/FB- is a state-change command (own line, not part of the field string)
        // that bolds everything printed after it until turned off - only applies to
        // scalable fonts, which arabicField now is (U51 via arabicToUtf16BeBytes).
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, arabicField);
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, shopcode.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, price1.getBytes());
        printData = addToDataVault(printData, arabicPriceField);
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }

    public byte[] getSlashingBarcode(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String now, String mark,
                                     String usid, String pQty, String addinfo) {
        String mainHead1 = "1911A1000780019" + mainHead + "\r\n";
        String itemName1 = "1911A0600690007" + itemName + "\r\n";
        String addinfo1 = "1911A0600600007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600280007" + itemCode + "\r\n";
        //String barcode1 = "1e2102100450006C" + barcode + "\r\n";
        String barcode2 = "1W1j2102100380006" + barcode + "\r\n";
        String shopcode = "1911A0600280126" + subHead + "\r\n";
        String usid1 = "1911A0600100126" + mark + "     " + usid + "\r\n";
        String price1 = "1911A1400050007" + now + "\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, shopcode.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, price1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }
    public byte[] getSlashingBarcodeWasNowArabic(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String now, String mark,
                                           String usid, String pQty, String wasHead, String nowHead, String addinfo,String arabicDesc) {
        String mainHead1 = "1911A1000780019" + mainHead + "\r\n";
        String itemName1 = "1911A0600690007" + itemName + "\r\n";
        String addinfo1 = "1911A0600530007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600260007" + itemCode + "\r\n";
        //String barcode1 = "1e2102100450006C" + barcode + "\r\n";
        String barcode2 = "1W1j2101700350006" + barcode + "\r\n";
        String shopcode = "1911A0600450130" + subHead + "\r\n";
        String usid1 = "1911A0600350160" + mark + "  " + usid + "\r\n";
        String wasHead1 = "1911A0600190007" + wasHead + "\r\n";
        String was1 = "1911A0800090007" + was + "\r\n";
        String nowHead1 = "1911A0800240120" + nowHead + "\r\n";
        String now1 = "1911A0800140120" + now + "\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";
        byte[] arabicField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00630010" + "P007P007").getBytes(),
                arabicToUtf16BeBytes(arabicDesc));
        arabicField = addToDataVault(arabicField, "\r\n".getBytes());
        String wasNum = was.contains(" ") ? was.substring(was.lastIndexOf(' ') + 1) : was;
        String nowNum = now.contains(" ") ? now.substring(now.lastIndexOf(' ') + 1) : now;
        String arabicWasPriceStr = "ر.ق " + arabicizePrice(wasNum);
        byte[] arabicWasPriceField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00060007" + "P007P007").getBytes(),
                arabicToUtf16BeBytes(arabicWasPriceStr));
        arabicWasPriceField = addToDataVault(arabicWasPriceField, "\r\n".getBytes());
        byte[] arabicNowPriceField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00050120" + "P008P008").getBytes(),
                arabicToUtf16BeBytes("ر.ق " + arabicizePrice(nowNum)));
        arabicNowPriceField = addToDataVault(arabicNowPriceField, "\r\n".getBytes());
        String wasStrike = strikeLine(10, 8, 19, 8 + was.length() * 8);
        String arabicWasStrike = strikeLine(6, 8, 9, 8 + arabicWasPriceStr.length() * 6);

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());

        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, arabicField);
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, shopcode.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, wasHead1.getBytes());
        printData = addToDataVault(printData, was1.getBytes());
        printData = addToDataVault(printData, wasStrike.getBytes());
        printData = addToDataVault(printData, nowHead1.getBytes());
        printData = addToDataVault(printData, now1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, arabicWasPriceField);
        printData = addToDataVault(printData, arabicWasStrike.getBytes());
        printData = addToDataVault(printData, arabicNowPriceField);
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }
    public byte[] getSlashingBarcodeWasNow(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String now, String mark,
                                           String usid, String pQty, String wasHead, String nowHead, String addinfo) {
        String mainHead1 = "1911A1000800020" + mainHead + "\r\n";
        String trfNo1 = "1911A0600350126" + trfno + "\r\n";
        String itemName1 = "1911A0600730007" + itemName + "\r\n";
        String addinfo1 = "1911A0600640007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600350007" + itemCode + "\r\n";
        //String wasHead1 = "1911A0800240024" + wasHead + "\r\n";
        String wasHead1 = "1911A0800240007" + wasHead + "\r\n";
        String was1 = "1911A1000100007" + was + "\r\n";
        String nowHead1 = "1911A1000200104" + nowHead + "\r\n";  // was 0270 → changed to 0200
        String now1 = "1911A1200000100" + now + "\r\n";      // was 0090 → changed to 0020
        // String nowHead1 = "1911A1000220110" + nowHead + "\r\n";
        //String now1 = "1911A1200060092" + now + "\r\n";
        //   String now1 = "1911A1200060110" + now + "\r\n";
        String barcode1 = "1e2101900450006C" + barcode + "\r\n";
        String barcode2 = "1W1j2101900450006" + barcode + "\r\n";
        String mark1 = "1911A0600260172" + mark + "\r\n";
        String usid1 = "1911A0600180172" + usid + "\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());
        printData = addToDataVault(printData, was1.getBytes());
        printData = addToDataVault(printData, wasHead1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, nowHead1.getBytes());
        printData = addToDataVault(printData, now1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, "1X1100100210071P0010001002100710012000600120006\r\n".getBytes());
        printData = addToDataVault(printData, trfNo1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, mark1.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }
    public byte[] getSlashingBarcodeWasNowPercArabic(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String now, String mark,
                                               String usid, String pQty, String savePerc, String wasHead, String nowHead, String addinfo,String arabicDesc) {
        String mainHead1 = "1911A1000780019" + mainHead + "\r\n";
        String itemName1 = "1911A0600690007" + itemName + "\r\n";
        String addinfo1 = "1911A0600530007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600260007" + itemCode + "\r\n";
        //String barcode1 = "1e2102100450006C" + barcode + "\r\n";
        String barcode2 = "1W1j2101700350006" + barcode + "\r\n";
        String shopcode = "1911A0600450130" + subHead + "\r\n";
        String usid1 = "1911A0600350160" + mark + "  " + usid + "\r\n";
        String wasHead1 = "1911A0600190007" + wasHead + "\r\n";
        String was1 = "1911A0800090007" + was + "\r\n";
        String nowHead1 = "1911A0800240120" + nowHead + "\r\n";
        String now1 = "1911A0800140120" + now + "\r\n";
        String saveperc = "1911A0600010060Save " + savePerc + " %\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";
        byte[] arabicField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00630010" + "P007P007").getBytes(),
                arabicToUtf16BeBytes(arabicDesc));
        arabicField = addToDataVault(arabicField, "\r\n".getBytes());
        String wasNum = was.contains(" ") ? was.substring(was.lastIndexOf(' ') + 1) : was;
        String nowNum = now.contains(" ") ? now.substring(now.lastIndexOf(' ') + 1) : now;
        String arabicWasPriceStr = "ر.ق " + arabicizePrice(wasNum);
        byte[] arabicWasPriceField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00060007" + "P007P007").getBytes(),
                arabicToUtf16BeBytes(arabicWasPriceStr));
        arabicWasPriceField = addToDataVault(arabicWasPriceField, "\r\n".getBytes());
        byte[] arabicNowPriceField = addToDataVault(
                ("1911" + ARABIC_PRICE_FONT_ID + "00050120" + "P008P008").getBytes(),
                arabicToUtf16BeBytes("ر.ق " + arabicizePrice(nowNum)));
        arabicNowPriceField = addToDataVault(arabicNowPriceField, "\r\n".getBytes());
        String wasStrike = strikeLine(10, 8, 19, 8 + was.length() * 8);
        String arabicWasStrike = strikeLine(6, 8, 9, 8 + arabicWasPriceStr.length() * 6);

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());

        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, arabicField);
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, shopcode.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, wasHead1.getBytes());
        printData = addToDataVault(printData, was1.getBytes());
        printData = addToDataVault(printData, wasStrike.getBytes());
        printData = addToDataVault(printData, nowHead1.getBytes());
        printData = addToDataVault(printData, now1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, arabicWasPriceField);
        printData = addToDataVault(printData, arabicWasStrike.getBytes());
        printData = addToDataVault(printData, arabicNowPriceField);
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, saveperc.getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }
    public byte[] getSlashingBarcodeWasNowPerc(String mainHead, String subHead, String itemCode, String itemName, String barcode, String trfno, String was, String now, String mark,
                                               String usid, String pQty, String savePerc, String wasHead, String nowHead, String addinfo) {
        String mainHead1 = "1911A1000810019" + mainHead + "\r\n";
        String trfNo1 = "1911A0600360126" + trfno + "\r\n";
        String itemName1 = "1911A0600730007" + itemName + "\r\n";
        String addinfo1 = "1911A0600660007" + addinfo + "\r\n";
        String itemCode1 = "1911A0600360007" + itemCode + "\r\n";
        //String wasHead1 = "1911A0800280029" + wasHead + "\r\n";
        String wasHead1 = "1911A0800250007" + wasHead + "\r\n";
        String was1 = "1911A1000120007" + was + "\r\n";
        String nowHead1 = "1911A1000230104" + nowHead + "\r\n";  // was 0270 → changed to 0200
        String now1 = "1911A1000110104" + now + "\r\n";      // was 0090 → changed to 0020
        // String nowHead1 = "1911A1000270104" + nowHead + "\r\n";
        //String now1 = "1911A1200090090" + now + "\r\n";
        // String now1 = "1911A1200090104" + now + "\r\n";
        //String barcode1 = "1e2102100530006C" + barcode + "\r\n";
        String barcode2 = "1W1j2102100450006" + barcode + "\r\n";
        String mark1 = "1911A0600260178" + mark + "\r\n";
        String usid1 = "1911A0600150178" + usid + "\r\n";
        //  String saveperc = "1911A0600020074Saving " + savePerc + " %\r\n";
        String saveperc = "1911A0600040060Save " + savePerc + " %\r\n";
        String pQty1 = "Q000" + pQty + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "V0\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, addinfo1.getBytes());
        printData = addToDataVault(printData, was1.getBytes());
        printData = addToDataVault(printData, wasHead1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, nowHead1.getBytes());
        printData = addToDataVault(printData, now1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, "1X1100100140008P0010001001400080025007500250075\r\n".getBytes());
        printData = addToDataVault(printData, trfNo1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, mark1.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, saveperc.getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }

    public byte[] getLabelWasNowHoneyWellTestPrint(String color) {
        String print = "1911A1400560045TEST " + color + "\r\n";
        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "n\r\n".getBytes());
        printData = addToDataVault(printData, "M0500\r\n".getBytes());
        printData = addToDataVault(printData, "KcLW0200\r\n;".getBytes());
        printData = addToDataVault(printData, "O0220\r\n".getBytes());
        printData = addToDataVault(printData, "d\r\n".getBytes());
        printData = addToDataVault(printData, "L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, print.getBytes());
        printData = addToDataVault(printData, "1e6303400190039C12340678\r\n".getBytes());
        printData = addToDataVault(printData, "Q0001\r\n".getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }

    private String strikeLine(int rowStart, int colStart, int rowEnd, int colEnd) {
        String r1 = String.format("%04d", rowStart);
        String c1 = String.format("%04d", colStart);
        String r2 = String.format("%04d", rowEnd);
        String c2 = String.format("%04d", colEnd);
        return "1X11001" + r1 + c1 + "P0010001" + r1 + c1 + r2 + c2 + r2 + c2 + "\r\n";
    }

    private byte[] addToDataVault(byte[] src, byte[] data) {
        byte[] now;
        if ((src.length == 1) && (src[0] == 0)) {
            now = new byte[data.length];
            System.arraycopy(data, 0, now, 0, data.length);
        } else {
            now = new byte[src.length + data.length];
            System.arraycopy(src, 0, now, 0, src.length);
            System.arraycopy(data, 0, now, src.length, data.length);
        }
        return now;
    }

    private static final int JOIN_DUAL = 0;
    private static final int JOIN_RIGHT = 1;
    private static final int JOIN_NONE = 2;
    private static final int[][] ARABIC_LETTER_FORMS = {
            {0x0621, JOIN_NONE,  0xFE80, 0,      0,      0     }, // HAMZA
            {0x0622, JOIN_RIGHT, 0xFE81, 0,      0,      0xFE82}, // ALEF MADDA
            {0x0623, JOIN_RIGHT, 0xFE83, 0,      0,      0xFE84}, // ALEF HAMZA ABOVE
            {0x0624, JOIN_RIGHT, 0xFE85, 0,      0,      0     }, // WAW HAMZA ABOVE
            {0x0626, JOIN_DUAL,  0,      0xFE8B, 0,      0     }, // YEH HAMZA ABOVE
            {0x0627, JOIN_RIGHT, 0xFE8D, 0,      0,      0xFE8E}, // ALEF
            {0x0628, JOIN_DUAL,  0xFE8F, 0xFE91, 0,      0     }, // BEH
            {0x0629, JOIN_RIGHT, 0xFE93, 0,      0,      0     }, // TEH MARBUTA
            {0x062A, JOIN_DUAL,  0xFE95, 0xFE97, 0,      0     }, // TEH
            {0x062B, JOIN_DUAL,  0xFE99, 0xFE9B, 0,      0     }, // THEH
            {0x062C, JOIN_DUAL,  0xFE9D, 0xFE9F, 0,      0     }, // JEEM
            {0x062D, JOIN_DUAL,  0xFEA1, 0xFEA3, 0,      0     }, // HAH
            {0x062E, JOIN_DUAL,  0xFEA5, 0xFEA7, 0,      0     }, // KHAH
            {0x062F, JOIN_RIGHT, 0xFEA9, 0,      0,      0     }, // DAL
            {0x0630, JOIN_RIGHT, 0xFEAB, 0,      0,      0     }, // THAL
            {0x0631, JOIN_RIGHT, 0xFEAD, 0,      0,      0     }, // REH
            {0x0632, JOIN_RIGHT, 0xFEAF, 0,      0,      0     }, // ZAIN
            {0x0633, JOIN_DUAL,  0xFEB1, 0xFEB3, 0,      0     }, // SEEN
            {0x0634, JOIN_DUAL,  0xFEB5, 0xFEB7, 0,      0     }, // SHEEN
            {0x0635, JOIN_DUAL,  0xFEB9, 0xFEBB, 0,      0     }, // SAD
            {0x0636, JOIN_DUAL,  0xFEBD, 0xFEBF, 0,      0     }, // DAD
            {0x0637, JOIN_DUAL,  0xFEC1, 0,      0,      0     }, // TAH
            {0x0638, JOIN_DUAL,  0xFEC5, 0,      0,      0     }, // ZAH
            {0x0639, JOIN_DUAL,  0xFEC9, 0xFECB, 0xFECC, 0xFECA}, // AIN
            {0x063A, JOIN_DUAL,  0xFECD, 0xFECF, 0,      0xFECE}, // GHAIN
            {0x0640, JOIN_DUAL,  0x0640, 0,      0,      0     }, // TATWEEL
            {0x0641, JOIN_DUAL,  0xFED1, 0xFED3, 0,      0     }, // FEH
            {0x0642, JOIN_DUAL,  0xFED5, 0xFED7, 0,      0     }, // QAF
            {0x0643, JOIN_DUAL,  0xFED9, 0xFEDB, 0,      0     }, // KAF
            {0x0644, JOIN_DUAL,  0xFEDD, 0xFEDF, 0,      0     }, // LAM
            {0x0645, JOIN_DUAL,  0xFEE1, 0xFEE3, 0,      0     }, // MEEM
            {0x0646, JOIN_DUAL,  0xFEE5, 0xFEE7, 0,      0     }, // NOON
            {0x0647, JOIN_DUAL,  0xFEE9, 0xFEEB, 0xFEEC, 0     }, // HEH
            {0x0648, JOIN_RIGHT, 0xFEED, 0,      0,      0     }, // WAW
            {0x0649, JOIN_RIGHT, 0xFEEF, 0,      0,      0xFEF0}, // ALEF MAKSURA
            {0x064A, JOIN_DUAL,  0xFEF1, 0xFEF3, 0,      0xFEF2}, // YEH
    };

    private static final int[][] CP864_BYTES = {
            {0xB0,0x0660},{0xB1,0x0661},{0xB2,0x0662},{0xB3,0x0663},{0xB4,0x0664}, // Arabic-Indic digits 0-4
            {0xB5,0x0665},{0xB6,0x0666},{0xB7,0x0667},{0xB8,0x0668},{0xB9,0x0669}, // Arabic-Indic digits 5-9
            {0x99,0xFEF7},{0x9A,0xFEF8},{0x9D,0xFEFB},{0x9E,0xFEFC},
            {0xA2,0xFE82},{0xA5,0xFE84},{0xA8,0xFE8E},{0xA9,0xFE8F},{0xAA,0xFE95},{0xAB,0xFE99},
            {0xAC,0x060C},{0xAD,0xFE9D},{0xAE,0xFEA1},{0xAF,0xFEA5},
            {0xBA,0xFED1},{0xBB,0x061B},{0xBC,0xFEB1},{0xBD,0xFEB5},{0xBE,0xFEB9},{0xBF,0x061F},
            {0xC1,0xFE80},{0xC2,0xFE81},{0xC3,0xFE83},{0xC4,0xFE85},
            {0xC5,0xFECA},{0xC6,0xFE8B},{0xC7,0xFE8D},{0xC8,0xFE91},{0xC9,0xFE93},
            {0xCA,0xFE97},{0xCB,0xFE9B},{0xCC,0xFE9F},{0xCD,0xFEA3},{0xCE,0xFEA7},{0xCF,0xFEA9},
            {0xD0,0xFEAB},{0xD1,0xFEAD},{0xD2,0xFEAF},{0xD3,0xFEB3},{0xD4,0xFEB7},
            {0xD5,0xFEBB},{0xD6,0xFEBF},{0xD7,0xFEC1},{0xD8,0xFEC5},{0xD9,0xFECB},
            {0xDA,0xFECF},{0xDF,0xFEC9},
            {0xE0,0x0640},{0xE1,0xFED3},{0xE2,0xFED7},{0xE3,0xFEDB},{0xE4,0xFEDF},
            {0xE5,0xFEE3},{0xE6,0xFEE7},{0xE7,0xFEEB},{0xE8,0xFEED},{0xE9,0xFEEF},
            {0xEA,0xFEF3},{0xEB,0xFEBD},{0xEC,0xFECC},{0xED,0xFECE},{0xEE,0xFECD},{0xEF,0xFEE1},
            {0xF0,0xFE7D},{0xF1,0x0651},{0xF2,0xFEE5},{0xF3,0xFEE9},{0xF4,0xFEEC},
            {0xF5,0xFEF0},{0xF6,0xFEF2},{0xF7,0xFED0},{0xF8,0xFED5},{0xF9,0xFEF5},
            {0xFA,0xFEF6},{0xFB,0xFEDD},{0xFC,0xFED9},{0xFD,0xFEF1},
    };

    private static final java.util.Map<Integer, int[]> ARABIC_FORMS = new java.util.HashMap<>();
    private static final java.util.Map<Integer, Integer> CP864_ENCODE = new java.util.HashMap<>();
    static {
        for (int[] entry : ARABIC_LETTER_FORMS) ARABIC_FORMS.put(entry[0], entry);
        for (int[] entry : CP864_BYTES) CP864_ENCODE.put(entry[1], entry[0]);
    }
    private byte[] arabicToUtf16BeBytes(String logicalText) {
        String shaped;
        try {
            com.ibm.icu.text.ArabicShaping shaping =
                    new com.ibm.icu.text.ArabicShaping(com.ibm.icu.text.ArabicShaping.LETTERS_SHAPE);
            shaped = shaping.shape(logicalText);
        } catch (Exception e) {
            Log.e("PRINT", "Arabic shaping failed for UTF-16BE path, using unshaped text", e);
            shaped = logicalText;
        }
        String visual = reorderVisual(shaped);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < visual.length(); i++) {
            char c = visual.charAt(i);
            out.write((c >> 8) & 0xFF); // high byte first (big-endian)
            out.write(c & 0xFF);
        }
        return out.toByteArray();
    }

    private String arabicizePrice(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            result.append((c >= '0' && c <= '9') ? (char) (0x0660 + (c - '0')) : c);
        }
        return result.toString();
    }

    private String reorderVisual(String shapedLogical) {
        try {
            com.ibm.icu.text.Bidi bidi = new com.ibm.icu.text.Bidi();
            bidi.setPara(shapedLogical, com.ibm.icu.text.Bidi.RTL, null);
            return bidi.writeReordered(com.ibm.icu.text.Bidi.DO_MIRRORING | com.ibm.icu.text.Bidi.KEEP_BASE_COMBINING);
        } catch (Exception e) {
            Log.e("PRINT", "Bidi reorder failed, using logical order", e);
            return shapedLogical;
        }
    }
}
