package kr.co.adwhale.sample.reward;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationFullScreenContentCallback;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardedAdLoadCallback;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.R;

public class ProgrammaticRewardAdMainActivity extends AppCompatActivity {

    private AdWhaleMediationRewardAd adWhaleMediationRewardAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_reward_ad_main);
        Button btnTest = findViewById(R.id.btnTest);
        Button btnShow = findViewById(R.id.btnShow);
        EditText etPlacementUid = findViewById(R.id.etPlacementUid);

        AdWhaleMediationAds.init(this, (statusCode, message) -> {
            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose);
            Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");
        });

        adWhaleMediationRewardAd = new AdWhaleMediationRewardAd(etPlacementUid.getText().toString());

        adWhaleMediationRewardAd.setAdWhaleMediationFullScreenContentCallback(new AdWhaleMediationFullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onAdClicked()");
            }

            @Override
            public void onAdDismissed() {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onAdDismissed()");
                Toast.makeText(getApplicationContext(), ".onAdDismissed()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedToShow(int statusCode, String message) {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onFailedToShow(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onFailedToShow(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowed() {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onAdShowed()");
                Toast.makeText(getApplicationContext(), ".onAdShowed()", Toast.LENGTH_SHORT).show();
            }
        });

        btnTest.setOnClickListener(view -> adWhaleMediationRewardAd.loadAd(new AdWhaleMediationRewardedAdLoadCallback() {

            @Override
            public void onAdLoaded(AdWhaleMediationRewardAd adWhaleMediationRewardAd, String message) {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onAdLoaded(" + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdLoaded(" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int statusCode, String message) {
                Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onAdFailedToLoad(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdFailedToLoad(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }
        }));

        btnShow.setOnClickListener(view -> adWhaleMediationRewardAd.showAd(adWhaleMediationRewardItem -> {
            Log.i(ProgrammaticRewardAdMainActivity.class.getSimpleName(), ".onUserRewarded(" + adWhaleMediationRewardItem.toString() + ")");
            Toast.makeText(getApplicationContext(), ".onUserRewarded(" + adWhaleMediationRewardItem.toString() + ")", Toast.LENGTH_SHORT).show();
        }));
    }
}