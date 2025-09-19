package kr.co.adwhale.sample.banner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationResponseInfo;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.R;

public class ProgrammaticBannerMainActivity extends AppCompatActivity {

    private RelativeLayout bannerRoot;
    private Button btnTest;
    private RadioGroup rgBannerAdSize;
    private EditText etPlacementUid;
    private ADWHALE_AD_SIZE selectedAdWhaleAdSize;

    private AdWhaleMediationAdView adWhaleMediationAdView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onCreate()");
        setContentView(R.layout.activity_programmatic_banner_main);
        btnTest = findViewById(R.id.btnTest);
        rgBannerAdSize = findViewById(R.id.rgBannerAdSize);
        etPlacementUid = findViewById(R.id.etPlacementUid);
        bannerRoot = (RelativeLayout) findViewById(R.id.bannerRoot);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose);
                Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");
            }
        });

        adWhaleMediationAdView = new AdWhaleMediationAdView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bannerRoot.addView(adWhaleMediationAdView, params);

        adWhaleMediationAdView.setAdWhaleMediationAdViewListener(new AdWhaleMediationAdViewListener() {
            @Override
            public void onAdLoaded(AdWhaleMediationResponseInfo adWhaleMediationResponseInfo) {
                Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onAdLoaded()");
                Toast.makeText(getApplicationContext(), ".onAdLoaded()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdLoadFailed(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onAdClicked()");
            }
        });

        rgBannerAdSize.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.rdBanner320x50:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x50;
                    etPlacementUid.setText(getString(R.string.banner32050_placementUid));
                    break;
                case R.id.rdBanner320x100:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x100;
                    etPlacementUid.setText(getString(R.string.banner320100_placementUid));
                    break;
                case R.id.rdBanner300x250:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER300x250;
                    etPlacementUid.setText(getString(R.string.banner300250_placementUid));
                    break;
                case R.id.rdBanner250x250:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER250x250;
                    etPlacementUid.setText(getString(R.string.banner250250_placementUid));
                    break;
                default:
                    selectedAdWhaleAdSize = null;
                    etPlacementUid.setText("");
                    break;
            }
        });
        btnTest.setOnClickListener(view -> {
            adWhaleMediationAdView.setPlacementUid(etPlacementUid.getText().toString());
            adWhaleMediationAdView.setAdwhaleAdSize(selectedAdWhaleAdSize);
            adWhaleMediationAdView.loadAd();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onResume()");
        adWhaleMediationAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onPause()");
        adWhaleMediationAdView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ProgrammaticBannerMainActivity.class.getSimpleName(), ".onDestroy()");
        adWhaleMediationAdView.destroy();
    }
}