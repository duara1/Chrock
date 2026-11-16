package com.example.chrock.util;

import java.util.Locale;

public class Formatar {

    public static String reais(int dinheiro){
        return String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", dinheiro / 100.0);
    }

}
