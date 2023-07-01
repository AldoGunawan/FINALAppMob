package com.app.uas_api_aldo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.app.uas_api_aldo.Model.AyatModel.Verses;
import com.app.uas_api_aldo.Model.AyatModel.VersesItem;
import com.app.uas_api_aldo.Model.Terjemahan.Indo;
import com.app.uas_api_aldo.Model.Terjemahan.TranslationsItem;
import com.app.uas_api_aldo.Retrofit.ApiBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailSurahActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AyatAdapter ayatAdapter;
    private final List<VersesItem> versesItemList = new ArrayList<>();
    private final List<TranslationsItem> translationsItemList = new ArrayList<>();
    List<VersesItem> ayat;
    List<TranslationsItem> arti;
    TextView tvNameComplexSurah, tvJumlahAyat, tvTempat, tvnoSurah, tvArabic;
    Button btAudio;
    private MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_surah);

        int id = getIntent().getIntExtra("id", 1);
        tvnoSurah = findViewById(R.id.textNomorSurah);
        tvnoSurah.setText(String.valueOf(id));

        String nameComplex = getIntent().getStringExtra("name");
        tvNameComplexSurah = findViewById(R.id.textJudul);
        tvNameComplexSurah.setText((nameComplex));

        String namaArab = getIntent().getStringExtra("arabic");
        tvArabic = findViewById(R.id.textNamaArab);
        tvArabic.setText("Nama Arab " + namaArab);

        String tempat = getIntent().getStringExtra("tempat");
        tvTempat = findViewById(R.id.textTempat);
        tvTempat.setText("Tempat Diturunkan " + tempat);

        int jumlahAyat = getIntent().getIntExtra("verses", 1);
        tvJumlahAyat = findViewById(R.id.textJumlahAyat);
        tvJumlahAyat.setText("Jumlah Ayat " + jumlahAyat);

        mediaPlayer = new MediaPlayer();
        String audioUrl = getIntent().getStringExtra("audio_url");
        btAudio = findViewById(R.id.btnAudio);
        btAudio.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                pauseAudio();
            } else {
                playAudio(audioUrl);
            }
        });

        setUpView();
        setUpRecyclerView();
        System.out.println(id);
        getDatafromApi(id);
    }
    private void pauseAudio() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
    private void playAudio(String audio) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audio);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void getDatafromApi(int id) {
        ApiBase.endpoint().getIndo(id).enqueue(new Callback<Indo>() {
            @Override
            public void onResponse(@NonNull Call<Indo> call, @NonNull Response<Indo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    DetailSurahActivity.this.arti = response.body().getTranslations();
                    getVersesData(getIntent().getIntExtra("id", 1));
                }
            }
            @Override
            public void onFailure(@NonNull Call<Indo> call, @NonNull Throwable t) {
                Log.d("error", t.toString());
            }
        });
    }

    private void getVersesData(int id) {
        ApiBase.endpoint().getAyat(id).enqueue(new Callback<Verses>() {
            @Override
            public void onResponse(@NonNull Call<Verses> call, @NonNull Response<Verses> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    DetailSurahActivity.this.ayat = response.body().getVerses();
                    ayatAdapter.setData(ayat, arti);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Verses> call, @NonNull Throwable t) {
                Log.d("error", t.toString());
            }
        });
    }
    private void setUpRecyclerView() {
        ayatAdapter = new AyatAdapter(versesItemList, translationsItemList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ayatAdapter);
    }
    private void setUpView() {
        recyclerView = findViewById(R.id.recviewAyat);
    }
}
