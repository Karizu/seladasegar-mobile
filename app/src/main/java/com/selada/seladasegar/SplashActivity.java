package com.selada.seladasegar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashActivity extends Activity {

    private static final int UPDATE_FROM_PLAY_STORE_REQUEST = 111;
    private String playStoreVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        downTimer();
    }

    private void downTimer() {
        long futureMillis = TimeUnit.SECONDS.toMillis(2);
        new CountDownTimer(futureMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                if (seconds == 0) {
                    cancel();
                    onFinish();
                }
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }.start();
    }

//    public void getUpdate() {
//        final String appPackageName = getPackageName();
//        Document doc = null;
//        try {
//            doc = Jsoup.connect("https://apk-dl.com/" + appPackageName).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assert doc != null;
//        Elements data = doc.select(".file-list .mdl-menu__item");
//        double playStoreVersion = 0;
//        double currentVersion = getVersionName();
//        if (data.size() > 0) {
//            System.out.println("full text : " + data.get(0).text());
//            Pattern pattern = Pattern.compile("(.*)\\s+\\((\\d+)\\)");
//            Matcher matcher = pattern.matcher(data.get(0).text());
//            if (matcher.find()) {
//                System.out.println("version name : " + matcher.group(1));
//                System.out.println("version code : " + matcher.group(2));
//                playStoreVersion = Double.parseDouble(Objects.requireNonNull(matcher.group(1)));
//            }
//        }
//
//        if (playStoreVersion > currentVersion) {
//            try {
//                new AlertDialog.Builder(SplashActivity.this)
//                        .setTitle("Pemberitahuan")
//                        .setMessage("Pembaruan tersedia, silahkan perbarui aplikasi anda")
//                        .setIcon(R.mipmap.ic_launcher)
//                        .setCancelable(false)
//                        .setPositiveButton("Perbarui",
//                                (dialog, id) -> {
//                                    try {
//                                        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
//                                    } catch (Exception e) {
//                                        try {
//                                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
//                                        } catch (Exception e1) {
//
//                                        }
//                                    }
//                                    dialog.dismiss();
//                                }).setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
////                                Handler();
//
//                    }
//                }).show();
//                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                try {
//                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
//                } catch (Exception e) {
//                }
//            }
//        }
//    }

    private class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String title ="";
            final String appPackageName = getPackageName();
            Document doc = null;
            try {
                doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.selada.seladasegar&hl=en&gl=US").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert doc != null;
            Elements data = doc.select("#fcxH9b > div.WpDbMd > c-wiz > div > div.ZfcPIb > div > main > c-wiz:nth-child(4) > div.W4P4ne > div.JHTxhe.IQ1z0d > div > div:nth-child(4) > span > div > span");
            playStoreVersion = "0";
            if (data.size() > 0) {
                System.out.println("full text : " + data.get(0).text());
                Pattern pattern = Pattern.compile("(.*)\\s+\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(data.get(0).text());
                if (matcher.find()) {
                    System.out.println("version name : " + matcher.group(1));
                    System.out.println("version code : " + matcher.group(2));
                    playStoreVersion = matcher.group(1);
                }
            }
            return playStoreVersion;
        }


        @Override
        protected void onPostExecute(String result) {
            //if you had a ui element, you could display the title

        }
    }


    public String getVersionName() {
        double VersionName = 0.0;
//        try {
//            String vName = SplashActivity.this.getPackageManager().getPackageInfo(SplashActivity.this.getPackageName(), 0).versionName;
//            VersionName = Double.parseDouble(vName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return BuildConfig.VERSION_NAME;
    }
}