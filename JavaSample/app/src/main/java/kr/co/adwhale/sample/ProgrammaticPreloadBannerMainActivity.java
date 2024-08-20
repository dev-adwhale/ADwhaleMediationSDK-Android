package kr.co.adwhale.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

public class ProgrammaticPreloadBannerMainActivity extends AppCompatActivity {

    private RelativeLayout root;
    private Button btnTest;
    private RadioGroup rgBannerAdSize;
    private EditText etPlacementUid;
    private ADWHALE_AD_SIZE selectedAdWhaleAdSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_preload_banner_main);
        btnTest = findViewById(R.id.btnTest);
        rgBannerAdSize = findViewById(R.id.rgBannerAdSize);
        etPlacementUid = findViewById(R.id.etPlacementUid);
        root = (RelativeLayout) findViewById(R.id.root);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");
            }
        });

        AdWhaleMediationAdBannerView adWhaleMediationAdBannerView = new AdWhaleMediationAdBannerView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        root.addView(adWhaleMediationAdBannerView, params);

        adWhaleMediationAdBannerView.setAdWhaleMediationAdBannerViewListener(new AdWhaleMediationAdBannerViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onAdLoaded()");
                if(adWhaleMediationAdBannerView != null) {
                    adWhaleMediationAdBannerView.show();
                }
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
            }

            @Override
            public void onShowLandingScreen() {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onShowLandingScreen()");
            }

            @Override
            public void onCloseLandingScreen() {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onCloseLandingScreen()");
            }

            @Override
            public void onTimeout(String errorMessage) {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onTimeout()");
            }

            @Override
            public void onClickAd() {
                Log.i(ProgrammaticPreloadBannerMainActivity.class.getSimpleName(), ".onClickAd()");
            }
        });

        rgBannerAdSize.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.rdBanner320x50:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x50;
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조");
                    break;
                case R.id.rdBanner320x100:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x100;
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조");
                    break;
                case R.id.rdBanner300x250:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER300x250;
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조");
                    break;
                case R.id.rdBanner250x250:
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER250x250;
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조");
                    break;
                default:
                    selectedAdWhaleAdSize = null;
                    etPlacementUid.setText("");
                    break;
            }
        });
        btnTest.setOnClickListener(view -> {
            adWhaleMediationAdBannerView.setPlacementUid(etPlacementUid.getText().toString());
            adWhaleMediationAdBannerView.setAdwhaleAdSize(selectedAdWhaleAdSize);
            adWhaleMediationAdBannerView.loadAd();
        });
    }
}