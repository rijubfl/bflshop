package com.bflgroup.bflshop;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bflgroup.bflshop.comm.BarcodePrinting;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SlashingQrPrintTest {
    private static final String COMPANY = "BRANDS FOR LESS";
    private static final String INFO = "414-345-1126";
    private static final String QR = "190035463717/80.00/U65";
    private static final String ITEM = "190035463717";
    private static final String SHOP = "U65";
    private static final String SUBCLASS = "CHILDREN BOTTOMS";
    private static final String ARABIC = "ملابس أطفال";

    @Test
    public void allSixVariantsUseOneQrAndOnePrintCommand() {
        BarcodePrinting printer = new BarcodePrinting();
        byte[][] labels = {
                printer.getSlashingQrCode(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 80.00"),
                printer.getSlashingQrCodeArabic(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 80.00", ARABIC),
                printer.getSlashingQrCodeWasNow(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 100.00", "QAR 80.00", "Was", "Now"),
                printer.getSlashingQrCodeWasNowArabic(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 100.00", "QAR 80.00", "Was", "Now", ARABIC),
                printer.getSlashingQrCodeWasNowPerc(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 100.00", "QAR 80.00", "20.00", "Was", "Now"),
                printer.getSlashingQrCodeWasNowPercArabic(COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 100.00", "QAR 80.00", "20.00", "Was", "Now", ARABIC)
        };
        for (int i = 0; i < labels.length; i++) {
            String commands = new String(labels[i], StandardCharsets.ISO_8859_1);
            assertEquals(1, count(commands, "1W1d4400000160006" + QR));
            assertEquals(1, count(commands, "Q0001\r\nE\r\n"));
            assertTrue(commands.contains(COMPANY));
            assertTrue(commands.contains(INFO));
            assertTrue(commands.contains(ITEM));
            assertTrue(commands.contains(SHOP));
            assertFalse(commands.contains("1W1j"));
            assertEquals(i >= 2, commands.contains("Was QAR 100.00"));
            assertEquals(i >= 2, commands.contains("Now QAR 80.00"));
            assertEquals(i >= 4, commands.contains("Save 20.00 %"));
            assertEquals(i % 2 == 1, commands.contains("1911U50"));
            if (i >= 2) assertTrue(commands.contains("1X11001"));
        }
    }

    @Test
    public void longSubclassStopsAtTwoLinesWithoutShrinking() {
        BarcodePrinting printer = new BarcodePrinting();
        String commands = new String(printer.getSlashingQrCode(
                COMPANY, INFO, QR, ITEM, SHOP,
                "CHILDRENJKHKLHLGKKJ BOTTOMSTUFJ " +
                        "EXTREMELY LONG SUBCLASS THAT MUST NOT CREATE A THIRD LINE " +
                        "TRUNCATED_SUFFIX",
                "QAR 80.00"), StandardCharsets.ISO_8859_1);
        java.util.regex.Matcher details = java.util.regex.Pattern
                .compile("(?m)^1911S01\\d{4}0052P007P007").matcher(commands);
        int detailCount = 0;
        while (details.find()) detailCount++;
        assertEquals(2, detailCount);
        assertFalse(commands.contains("TRUNCATED_SUFFIX"));
        assertTrue(commands.contains("QAR 80.00"));
        assertTrue(commands.endsWith("FB-\r\nQ0001\r\nE\r\n"));
    }

    @Test
    public void arabicFieldsRetainBinaryUnicodeBytes() {
        BarcodePrinting printer = new BarcodePrinting();
        byte[] label = printer.getSlashingQrCodeWasNowPercArabic(
                COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, "QAR 100.00", "QAR 80.00",
                "20", "Was", "Now", ARABIC);
        String commands = new String(label, StandardCharsets.ISO_8859_1);
        String descriptionPrefix = "1911U5000620007P006P006";
        assertTrue(commands.contains(descriptionPrefix));
        assertTrue(commands.contains("1911U5000120052P006P006"));
        assertTrue(commands.contains("1911U5000120124P006P006"));
        int start = commands.indexOf(descriptionPrefix) + descriptionPrefix.length();
        int end = commands.indexOf("\r\n", start);
        String descriptionBytes = commands.substring(start, end);
        assertFalse(descriptionBytes.isEmpty());
        assertEquals(0, descriptionBytes.length() % 2);
        assertFalse(descriptionBytes.contains("?"));
        boolean unicode = false;
        for (int i = 0; i < descriptionBytes.length(); i += 2) {
            if (descriptionBytes.charAt(i) >= 6) unicode = true;
        }
        assertTrue(unicode);
    }

    @Test
    public void nullArabicDescriptionAndEmptyHeadersAreHandled() {
        BarcodePrinting printer = new BarcodePrinting();
        String commands = new String(printer.getSlashingQrCodeWasNowArabic(
                COMPANY, INFO, QR, ITEM, SHOP, null, "QAR 100.00", "QAR 80.00",
                null, null, null), StandardCharsets.ISO_8859_1);
        assertTrue(commands.contains("Was QAR 100.00"));
        assertTrue(commands.contains("Now QAR 80.00"));
        assertFalse(commands.contains("null"));
    }

    @Test
    public void normalQrUsesOnlySupportedBarcodeFontSizes() {
        BarcodePrinting printer = new BarcodePrinting();
        String[] prices = {"AED 19", "AED 1234567.89", "AED 123456789012.34"};
        for (String price : prices) {
            String commands = new String(printer.getSlashingQrCode(
                    COMPANY, INFO, QR, ITEM, SHOP, SUBCLASS, price),
                    StandardCharsets.ISO_8859_1);
            assertTrue(commands.contains("1911A1000820019" + COMPANY));
            assertTrue(commands.contains("1911A0800640007" + INFO));
            assertFalse(commands.contains("1911S00"));
            assertTrue(commands.contains("P007P007"));
            java.util.regex.Matcher fonts = java.util.regex.Pattern
                    .compile("(?m)^1911A(\\d{2})\\d{8}").matcher(commands);
            int fontCount = 0;
            while (fonts.find()) {
                int size = Integer.parseInt(fonts.group(1));
                assertTrue("Unsupported A font size: " + size,
                        size == 6 || size == 8 || size == 10 || size == 12);
                fontCount++;
            }
            assertTrue(fontCount >= 3);
        }
    }

    private static int count(String text, String value) {
        int count = 0;
        int position = 0;
        while ((position = text.indexOf(value, position)) >= 0) {
            count++;
            position += value.length();
        }
        return count;
    }
}
