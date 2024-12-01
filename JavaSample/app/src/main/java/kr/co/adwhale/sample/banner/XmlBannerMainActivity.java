package kr.co.adwhale.sample.banner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

import kr.co.adwhale.sample.R;

public class XmlBannerMainActivity extends AppCompatActivity {

    private AdWhaleMediationAdView adWhaleMediationAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_banner_main);
        adWhaleMediationAdView = findViewById(R.id.test);
        adWhaleMediationAdView.setAdWhaleMediationAdViewListener(new AdWhaleMediationAdViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onAdLoaded()");
                Toast.makeText(getApplicationContext(), ".onAdLoaded()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(XmlBannerMainActivity.class.getSimpleName(), ".onAdLoadFailed(" + statusCode + ", " + message + ")");
                Toast.makeText(getApplicationContext(), ".onAdLoadFailed(statusCode:" + statusCode + ", message:" + message + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onAdClicked()");
            }
        });

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onInitComplete(" + statusCode + ", " + message + ")");
                if(adWhaleMediationAdView != null){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        adWhaleMediationAdView.loadAd();
                    },0);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onResume()");
        if(adWhaleMediationAdView != null) {
            adWhaleMediationAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onPause()");
        if(adWhaleMediationAdView != null) {
            adWhaleMediationAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(XmlBannerMainActivity.class.getSimpleName(), ".onDestroy()");
        if(adWhaleMediationAdView != null) {
            adWhaleMediationAdView.destroy();
        }
    }
}