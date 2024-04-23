package kr.co.adwhale.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout root;
    private Button btnTest;
    private RadioGroup rgBannerAdSize;
    private EditText etPlacementUid;
    private ADWHALE_AD_SIZE selectedAdWhaleAdSize;

    private AdWhaleMediationAdView adWhaleMediationAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTest = findViewById(R.id.btnTest);
        rgBannerAdSize = findViewById(R.id.rgBannerAdSize);
        etPlacementUid = findViewById(R.id.etPlacementUid);
        root = (ConstraintLayout) findViewById(R.id.root);
        adWhaleMediationAdView = new AdWhaleMediationAdView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        root.addView(adWhaleMediationAdView, params);

        adWhaleMediationAdView.setAdWhaleMediationAdViewListener(new AdWhaleMediationAdViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(MainActivity.class.getSimpleName()+"hh", "onAdLoaded()");
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.i(MainActivity.class.getSimpleName()+"hh", "statusCode:" + statusCode + ", message:" + message);
            }
        });

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(MainActivity.class.getSimpleName()+"hh", "statusCode:" + statusCode + ", message:" + message);
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
            adWhaleMediationAdView.setPlacementUid(etPlacementUid.getText().toString());
            adWhaleMediationAdView.setAdwhaleAdSize(selectedAdWhaleAdSize);
            adWhaleMediationAdView.loadAd();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adWhaleMediationAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adWhaleMediationAdView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adWhaleMediationAdView.destroy();
    }
}