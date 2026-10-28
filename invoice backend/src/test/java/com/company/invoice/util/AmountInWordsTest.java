package com.company.invoice.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AmountInWordsTest {

    @Test
    void zero() {
        assertEquals("Rupees Zero Only", AmountInWords.convert(BigDecimal.ZERO));
    }

    @Test
    void singleRupee() {
        assertEquals("Rupees One Only", AmountInWords.convert(new BigDecimal("1.00")));
    }

    @Test
    void paiseOnly() {
        assertEquals("Rupees Paise Fifty Only", AmountInWords.convert(new BigDecimal("0.50")));
    }

    @Test
    void rupeesAndPaise() {
        assertEquals(
            "Rupees One Thousand Two Hundred Thirty Four and Paise Fifty Only",
            AmountInWords.convert(new BigDecimal("1234.50")));
    }

    @Test
    void lakh() {
        assertEquals("Rupees One Lakh Only", AmountInWords.convert(new BigDecimal("100000")));
    }

    @Test
    void tenLakh() {
        assertEquals("Rupees Ten Lakh Only", AmountInWords.convert(new BigDecimal("1000000")));
    }

    @Test
    void crore() {
        assertEquals("Rupees One Crore Only", AmountInWords.convert(new BigDecimal("10000000")));
    }

    @Test
    void complexAmount() {
        // 1,23,45,678.99
        assertEquals(
            "Rupees One Crore Twenty Three Lakh Forty Five Thousand Six Hundred Seventy Eight and Paise Ninety Nine Only",
            AmountInWords.convert(new BigDecimal("12345678.99")));
    }

    @Test
    void workedExampleIntraState() {
        // Item 1: qty=2, rate=5000, disc=10% → taxable=9000, GST18% → CGST=810, SGST=810, line=10620
        // Item 2: qty=1, rate=1200, disc=0%  → taxable=1200, GST12% → CGST=72,  SGST=72,  line=1344
        // subtotal=10200, taxTotal=1764, gross=11964, roundOff=0.00, total=11964
        assertEquals(
            "Rupees Eleven Thousand Nine Hundred Sixty Four Only",
            AmountInWords.convert(new BigDecimal("11964.00")));
    }

    @Test
    void negativeThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> AmountInWords.convert(new BigDecimal("-1")));
    }

    @Test
    void nullThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> AmountInWords.convert(null));
    }

    @Test
    void nineteen() {
        assertEquals("Rupees Nineteen Only", AmountInWords.convert(new BigDecimal("19")));
    }

    @Test
    void hundred() {
        assertEquals("Rupees One Hundred Only", AmountInWords.convert(new BigDecimal("100")));
    }

    @Test
    void ninetyNineThousandNineHundredNinetyNine() {
        assertEquals(
            "Rupees Ninety Nine Thousand Nine Hundred Ninety Nine Only",
            AmountInWords.convert(new BigDecimal("99999")));
    }
}
