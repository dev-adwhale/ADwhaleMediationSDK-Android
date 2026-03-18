package kr.co.adwhale.sample.transition;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.ADWHALE_POPUP_AD_CLOSE_REASON;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationTransitionPopupAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationTransitionPopupAdListener;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.R;

public class TransitionTestMainActivity extends AppCompatActivity {

    private static final String LOG_TAG = TransitionTestMainActivity.class.getSimpleName();

    private AdWhaleMediationTransitionPopupAd adWhaleMediationTransitionPopupAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_test_main);
        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Error);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")");
                loadTransitionPopupAd();
            }
        });
    }

    private void loadTransitionPopupAd() {
        adWhaleMediationTransitionPopupAd = new AdWhaleMediationTransitionPopupAd(getResources().getString(R.string.transition_popup_placement_uid));
        adWhaleMediationTransitionPopupAd.setAdWhaleMediationTransitionPopupAdListener(new AdWhaleMediationTransitionPopupAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                showTransitionPopupAd();
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.d(LOG_TAG, "onAdLoadFailed(" + statusCode + ", " + message + ");");
            }

            @Override
            public void onAdShowed() {
                Log.d(LOG_TAG, "onAdShowed");
            }

            @Override
            public void onAdClicked() {
                Log.d(LOG_TAG, "onAdClicked");
            }

            @Override
            public void onAdShowFailed(int statusCode, String message) {
                Log.d(LOG_TAG, "onAdShowFailed(" + statusCode + ", " + message + ");");
            }

            @Override
            public void onAdClosed(ADWHALE_POPUP_AD_CLOSE_REASON adwhalePopupAdCloseReason) {
                Log.d(LOG_TAG, "onAdClosed(" + adwhalePopupAdCloseReason.getCloseReasonTypeString() + ");");
            }
        });
        adWhaleMediationTransitionPopupAd.loadAd();
    }


    @Override
    protected void onResume() {
        if (adWhaleMediationTransitionPopupAd != null)
            adWhaleMediationTransitionPopupAd.resume(this); // 필수 호출
        super.onResume();
    }

    private void showTransitionPopupAd() {
        if(adWhaleMediationTransitionPopupAd != null) {
            adWhaleMediationTransitionPopupAd.showAd(TransitionTestMainActivity.this, TransitionTestMainActivity.this.getSupportFragmentManager());
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, ".onDestroy()");
        if(adWhaleMediationTransitionPopupAd != null) {
            adWhaleMediationTransitionPopupAd.destroy();
        }
        super.onDestroy();
    }
}