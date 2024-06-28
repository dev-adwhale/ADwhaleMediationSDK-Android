package kr.co.adwhale.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SampleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_main);

        Button btnProgrammaticBanner = findViewById(R.id.btnProgrammaticBanner);
        Button btnXmlBanner = findViewById(R.id.btnXmlBanner);
        Button btnInterstitial = findViewById(R.id.btnInterstitial);

        btnProgrammaticBanner.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProgrammaticBannerMainActivity.class);
            startActivity(intent);
        });

        btnXmlBanner.setOnClickListener(view -> {
            Intent intent = new Intent(this, XmlBannerMainActivity.class);
            startActivity(intent);
        });

        btnInterstitial.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProgrammaticInterstitialMainActivity.class);
            startActivity(intent);
        });

    }
}