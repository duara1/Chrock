package com.example.chrock.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Imagem {

    private static final int LADO_MAXIMO = 1600;
    public static byte[] prepararJpeg(ContentResolver leitor, Uri uri) throws IOException {
        BitmapFactory.Options medidas = new BitmapFactory.Options();
        medidas.inJustDecodeBounds = true;
        try (InputStream entrada = abrir(leitor, uri)) {
            BitmapFactory.decodeStream(entrada, null, medidas);
        }

        int maiorLado = Math.max(medidas.outWidth, medidas.outHeight);
        if (maiorLado <= 0) throw new IOException("Não consegui ler essa imagem.");

        int reducao = 1;
        while (maiorLado / (reducao * 2) >= LADO_MAXIMO) reducao *= 2;

        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inSampleSize = reducao;
        Bitmap foto;
        try (InputStream entrada = abrir(leitor, uri)) {
            foto = BitmapFactory.decodeStream(entrada, null, opcoes);
        }
        if (foto == null) throw new IOException("Não consegui ler essa imagem.");

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 80, saida);
        foto.recycle();
        return saida.toByteArray();
    }

    private static InputStream abrir(ContentResolver leitor, Uri uri) throws IOException {
        InputStream entrada = leitor.openInputStream(uri);
        if (entrada == null) throw new IOException("Não consegui abrir essa imagem.");
        return entrada;
    }
}