package com.controlfinanceiro.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * Formata um valor BigDecimal para a moeda brasileira (R$)
     */
    public static String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }
        return CURRENCY_FORMAT.format(valor);
    }

    /**
     * Formata um valor double para a moeda brasileira (R$)
     */
    public static String formatarValor(double valor) {
        return formatarValor(BigDecimal.valueOf(valor));
    }

    /**
     * Formata um valor BigDecimal como decimal simples
     */
    public static String formatarDecimal(BigDecimal valor) {
        if (valor == null) {
            return "0,00";
        }
        return DECIMAL_FORMAT.format(valor);
    }
}
