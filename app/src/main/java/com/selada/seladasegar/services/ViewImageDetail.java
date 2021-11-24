package com.selada.seladasegar.services;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.selada.seladasegar.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewImageDetail extends Activity {

    private Context context;
    private String image;

    @BindView(R.id.imgDetail)
    ImageView imgDetail;

    @OnClick(R.id.imgClose)
    void onClickimgClose(){
        onBackPressed();
    }

    @OnClick(R.id.imgUnduh)
    void onClickImgUnduh(){
        downloadImage();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_detail);
        ButterKnife.bind(this);

        context = this;

        if (getIntent()!=null){

            image = getIntent().getStringExtra("source_image");

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.resto_default);
            requestOptions.fitCenter().dontAnimate();
            Glide.with(context).load(image)
                    .apply(requestOptions)
                    .into(imgDetail);
        }

        Log.d("Path", image);
    }

    private void downloadImage() {
        Toast.makeText(context, "Mengunduh gambar", Toast.LENGTH_SHORT).show();
        String filename = "item.jpg";
        String DIR_NAME = "SeladaSegar";
        String downloadUrlOfImage = image;
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + DIR_NAME + "/");


        if (!direct.exists()) {
            direct.mkdir();
            Log.d("DownloadManager", "dir created for first time");
        }

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrlOfImage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + DIR_NAME + File.separator + filename);

        dm.enqueue(request);
    }
}