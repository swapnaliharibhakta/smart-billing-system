package com.company.invoice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converts a rupee amount to words using the Indian numbering system.
 * Examples:
 *   1234.50  → "Rupees One Thousand Two Hundred Thirty Four and Paise Fifty Only"
 *   100000   → "Rupees One Lakh Only"
 *   10000000 → "Rupees One Crore Only"
 */
public final class AmountInWords {

    private AmountInWords() {
    }

    private static final String[] ONES = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convert(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        long rupees = scaled.longValue();
        int paise = scaled.remainder(BigDecimal.ONE)
                          .multiply(new BigDecimal("100"))
                          .setScale(0, RoundingMode.HALF_UP)
                          .intValue();

        if (rupees == 0 && paise == 0) {
            return "Rupees Zero Only";
        }

        StringBuilder sb = new StringBuilder("Rupees ");
        if (rupees > 0) {
            sb.append(inWords(rupees));
        }
        if (paise > 0) {
            if (rupees > 0) sb.append(" and ");
            sb.append("Paise ").append(inWords(paise));
        }
        sb.append(" Only");
        return sb.toString();
    }

    /** Converts a non-negative long to Indian-system words. */
    static String inWords(long n) {
        if (n == 0) return "Zero";
        StringBuilder sb = new StringBuilder();

        long crore = n / 10_000_000L;
        n %= 10_000_000L;
        long lakh = n / 100_000L;
        n %= 100_000L;
        long thousand = n / 1_000L;
        n %= 1_000L;
        long hundred = n / 100L;
        long remainder = n % 100L;

        if (crore > 0) {
            sb.append(inWords(crore)).append(" Crore ");
        }
        if (lakh > 0) {
            sb.append(belowThousand((int) lakh)).append(" Lakh ");
        }
        if (thousand > 0) {
            sb.append(belowThousand((int) thousand)).append(" Thousand ");
        }
        if (hundred > 0) {
            sb.append(ONES[(int) hundred]).append(" Hundred ");
        }
        if (remainder > 0) {
            sb.append(belowHundred((int) remainder));
        }
        return sb.toString().trim();
    }

    private static String belowThousand(int n) {
        int h = n / 100;
        int rem = n % 100;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(ONES[h]).append(" Hundred ");
        if (rem > 0) sb.append(belowHundred(rem));
        return sb.toString().trim();
    }

    private static String belowHundred(int n) {
        if (n < 20) return ONES[n];
        return TENS[n / 10] + (n % 10 != 0 ? " " + ONES[n % 10] : "");
    }
}
