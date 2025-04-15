package kr.co.adwhale.sample.interstitial;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAdListener;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.R;

public class ProgrammaticInterstitialMainActivity extends AppCompatActivity {

    private AdWhaleMediationInterstitialAd adWhaleMediationInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_interstitial_main);
        Button btnTest = findViewById(R.id.btnTest);
        Button btnShow = findViewById(R.id.btnShow);
        EditText etPlacementUid = findViewById(R.id.etPlacementUid);

        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None);

        AdWhaleMediationAds.init(this, (statusCode, message) -> Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")"));

        adWhaleMediationInterstitialAd = new AdWhaleMediationInterstitialAd(etPlacementUid.getText().toString());
        adWhaleMediationInterstitialAd.setAdWhaleMediationInterstitialAdListener(new AdWhaleMediationInterstitialAdListener() {
            @Override
            public void onAdLoaded() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdLoaded()");
                Toast.makeText(getApplicationContext(), ".onAdLoaded()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdLoadFailed(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowed() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdShowed()");
                Toast.makeText(getApplicationContext(), ".onAdShowed()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowFailed(int statusCode, String message) {
                Log.e(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdShowFailed(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdShowFailed(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdClosed()");
                Toast.makeText(getApplicationContext(), ".onAdClosed()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.i(ProgrammaticInterstitialMainActivity.class.getSimpleName(), ".onAdClicked()");
                Toast.makeText(getApplicationContext(), ".onAdClicked()", Toast.LENGTH_SHORT).show();
            }
        });
        btnTest.setOnClickListener(view -> adWhaleMediationInterstitialAd.loadAd());

        btnShow.setOnClickListener(view -> adWhaleMediationInterstitialAd.showAd());
    }
}