package com.example.chrock.util;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImagemSala {

    public static boolean ehEnderecoDaInternet(Uri uri) {
        String endereco = uri == null ? "" : uri.toString();
        return endereco.startsWith("http://") || endereco.startsWith("https://");
    }

    public static void mostrarOuEsconder(ImageView imagem, Uri uri) {
        if (!ehEnderecoDaInternet(uri)) {
            Glide.with(imagem).clear(imagem);
            imagem.setImageDrawable(null);
            imagem.setVisibility(View.GONE);
            return;
        }

        imagem.setVisibility(View.VISIBLE);
        Glide.with(imagem)
                .load(uri.toString())
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        imagem.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imagem);
    }

    public static void mostrar(ImageView imagem, Uri uri) {
        if (!ehEnderecoDaInternet(uri)) return;
        Glide.with(imagem).load(uri.toString()).centerCrop().into(imagem);
    }
}
