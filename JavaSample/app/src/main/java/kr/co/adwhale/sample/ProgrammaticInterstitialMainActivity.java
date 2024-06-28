package kr.co.adwhale.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAdListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

public class ProgrammaticInterstitialMainActivity extends AppCompatActivity {

    private Button btnTest;

    private EditText etPlacementUid;

    private AdWhaleMediationInterstitialAd adWhaleMediationInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_interstitial_main);
        btnTest = findViewById(R.id.btnTest);
        etPlacementUid = findViewById(R.id.etPlacementUid);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");
            }
        });

        adWhaleMediationInterstitialAd = new AdWhaleMediationInterstitialAd(etPlacementUid.getText().toString());
        adWhaleMediationInterstitialAd.setAdWhaleMediationInterstitialAdListener(new AdWhaleMediationInterstitialAdListener() {
            @Override
            public void onAdLoaded() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdLoaded()");
                adWhaleMediationInterstitialAd.showAd();
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
            }

            @Override
            public void onAdShowed() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdShowed()");
            }

            @Override
            public void onAdShowFailed(int statusCode, String message) {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdShowFailed(" + statusCode + ", " + message + ")");
            }

            @Override
            public void onAdClosed() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdClosed()");
            }
        });
        btnTest.setOnClickListener(view -> {
            adWhaleMediationInterstitialAd.loadAd();
        });
    }
}