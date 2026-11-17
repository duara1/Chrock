package com.example.chrock.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Formatar {

    public static String reais(int dinheiro){
        return String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", dinheiro / 100.0);
    }

    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.forLanguageTag("pt-BR")).withResolverStyle(ResolverStyle.STRICT);

    public static int diasAte(String data){
        LocalDate fim = LocalDate.parse(data, formato);
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), fim);
    }
}
