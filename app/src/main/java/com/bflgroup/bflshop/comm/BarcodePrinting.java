package com.bflgroup.bflshop.comm;

import android.graphics.Bitmap;

import com.sewoo.jpos.printer.CPCLPrinter;
import com.sewoo.jpos.printer.ZPLPrinter;

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
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002V0\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002SG\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
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
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002V0\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002SG\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
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
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002V0\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002SG\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
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
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200\r\n;".getBytes());
        printData = addToDataVault(printData, "\u0002O0220\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, print.getBytes());
        printData = addToDataVault(printData, "1e6303400190039C12340678\r\n".getBytes());
        printData = addToDataVault(printData, "Q0001\r\n".getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
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
}