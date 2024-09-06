package dev.fian.dolananpuzzle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private int kosongX = 3;
    private int kosongY = 3;
    private RelativeLayout tombolGroup;
    private Button[][] tombolHuruf;
    private char[] huruf;
    private TextView rekor, langkah;
    private int hitungLangkah = 0;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rekor = findViewById(R.id.rekor);
        langkah = findViewById(R.id.langkah);

        SharedPrefManager SharedPrefManager;
        SharedPrefManager = new SharedPrefManager(this);

        SharedPrefManager.saveSPBoolean(dev.fian.dolananpuzzle.SharedPrefManager.SP_SUDAH_REKOR, true);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Rekor", MODE_PRIVATE);

        if (SharedPrefManager.getSPSudahRekor()){
            rekor.setText("Rekor Terbaikmu: " + pref.getString("rekor", "0") + " Langkah");
        }

        // Mendapatkan Layout dan tombolnya
        tombolGroup = findViewById(R.id.tombolGroup);
        tombolHuruf = new Button[4][4];

        for (int i = 0; i < tombolGroup.getChildCount(); i++) {
            tombolHuruf[i/4][i%4] = (Button) tombolGroup.getChildAt(i);
            tombolHuruf[i/4][i%4].setBackgroundResource(android.R.drawable.btn_default);
        } // End

        // Mendapatkan Hurufnya
        huruf = new char[16];
        for (int i = 0; i < tombolGroup.getChildCount() - 1; i++) {
            huruf[i] = (char) ('a' + i);
        } // End

        generateHuruf();
        loadDataToViews();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView bannerBawah = findViewById(R.id.bannerBawah);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerBawah.loadAd(adRequest);
        loadInterstitial();

    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                Toast.makeText(MainActivity.this, "Terima Kasih Sudah Bermain :)", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void loadDataToViews() {
        // loadDataToViews
        kosongX = 3;
        kosongY = 3;
        tombolHuruf[kosongX][kosongY].setText("");
        for (int i = 0; i < tombolGroup.getChildCount() - 1; i++) {
            tombolHuruf[i/4][i%4].setText(String.valueOf(huruf[i]));
            tombolHuruf[i/4][i%4].setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void generateHuruf() {
        int n = 15;
        Random acak = new Random();
        while (n > 1) {
            int acakHuruf = acak.nextInt(n--);
            char sementara = huruf[acakHuruf];
            huruf[acakHuruf] = huruf[n];
            huruf[n] = sementara;
        }
        if (!selesai()) {
            generateHuruf();
        }
    }

    private boolean selesai() {
        int hitungPerubahan = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < i; j++) {
                if (huruf[j] > huruf[i]) {
                    hitungPerubahan++;
                }
            }
        }

        return hitungPerubahan % 2 == 0;
    }

    public void klikTombol(View v) {
        Button tombol = (Button) v;
        int x = tombol.getTag().toString().charAt(0) - '0';
        int y = tombol.getTag().toString().charAt(1) - '0';

        if ((Math.abs(kosongX - x) == 1 && kosongY == y) || (Math.abs(kosongY - y) == 1 && kosongX == x)) {
            tombolHuruf[kosongX][kosongY].setText(tombol.getText().toString());
            tombolHuruf[kosongX][kosongY].setBackgroundResource(android.R.drawable.btn_default);
            tombol.setText("");
            kosongX = x;
            kosongY = y;
            hitungLangkah++;
            langkah.setText("Langkah: "+hitungLangkah);
        }

        // cekSelesai
        boolean jikaSelesai = false;
        if (kosongX == 3 && kosongY == 3) {
            for (int i = 0; i < tombolGroup.getChildCount() - 1; i++) {
                if (tombolHuruf[i/4][i%4].getText().toString().equals(String.valueOf((char) ('a' + i)))) {
                    jikaSelesai = true;
                } else {
                    jikaSelesai = false;
                    break;
                }
            }
        }

        if (jikaSelesai) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("Rekor", MODE_PRIVATE);
            int rekorSementara = Integer.parseInt(pref.getString("rekor", "0"));
            if(rekorSementara == 0) {
                if (hitungLangkah > rekorSementara) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("rekor", String.valueOf(hitungLangkah));
                    editor.apply();
                    rekor.setText("Rekor Terbaikmu: " + pref.getString("rekor", "0") + " Langkah");
                }
            } else {
                if (hitungLangkah < rekorSementara) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("rekor", String.valueOf(hitungLangkah));
                    editor.apply();
                    rekor.setText("Rekor Terbaikmu: " + pref.getString("rekor", "0") + " Langkah");
                }
            }
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            }
            for (int i = 0; i < tombolGroup.getChildCount(); i++) {
                tombolHuruf[i/4][i%4].setClickable(false);
            }
            Toast.makeText(this, "Yey Kamu Menang!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ulangi) {
            generateHuruf();
            loadDataToViews();
            for (int i = 0; i < tombolGroup.getChildCount(); i++) {
                tombolHuruf[i/4][i%4].setClickable(true);
            }
            langkah.setText("Langkah: 0");
            hitungLangkah = 0;
            Toast.makeText(MainActivity.this, item.getTitle().toString() + " Berhasil!", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.guideline) {
            CaraBermain();
        }
        if (item.getItemId() == R.id.keluar) {
            Toast.makeText(MainActivity.this, "Terima Kasih Sudah Bermain :)", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        }

        return true;
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, "ca-app-pub-6951529986432751/8409351750", new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitial) {
                        // Saat iklan berhasil dimuat
                        mInterstitialAd = interstitial;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Saat iklan gagal dimuat
                        mInterstitialAd = null;
                    }
                });
    }

    private void CaraBermain() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cara_bermain, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Cara Bermain");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}