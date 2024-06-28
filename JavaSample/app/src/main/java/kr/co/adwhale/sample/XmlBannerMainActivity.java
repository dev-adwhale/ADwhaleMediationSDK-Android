package kr.co.adwhale.sample;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

public class XmlBannerMainActivity extends AppCompatActivity {

    private AdWhaleMediationAdView adWhaleMediationAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_banner_main);
        adWhaleMediationAdView = findViewById(R.id.test);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");

            }
        });

        adWhaleMediationAdView.setAdWhaleMediationAdViewListener(new AdWhaleMediationAdViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onAdLoaded()");
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(XmlBannerMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
            }

            @Override
            public void onAdClicked() {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onAdClicked()");
            }
        });
        adWhaleMediationAdView.loadAd();
    }

    protected void onResume() {
        super.onResume();
        Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onResume()");
        adWhaleMediationAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onPause()");
        adWhaleMediationAdView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        adWhaleMediationAdView.destroy();
    }
}