package kr.co.adwhalejavasampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;

import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;


public class AdWhaleMediationBanner implements MediationBannerAd {
    final static String TAG = AdWhaleMediationBanner.class.getSimpleName();
    private final MediationBannerAdConfiguration mediationBannerAdConfiguration;
    private final MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mediationAdLoadCallback;
    private MediationBannerAdCallback bannerAdCallback;

    private AdWhaleMediationAdView adWhaleMediationAdView;
    private ViewGroup layout = null;

    /** Error raised when the custom event adapter cannot obtain the ad unit id. */
    public static final int ERROR_NO_AD_UNIT_ID = 101;
    /** Error raised when the custom event adapter cannot obtain the activity context. */
    public static final int ERROR_NO_ACTIVITY_CONTEXT = 103;
    public static final String SAMPLE_SDK_DOMAIN = "net.adwhale.sdk.mediation.ads";
    public static final String CUSTOM_EVENT_ERROR_DOMAIN = "com.google.ads.mediation.sample.customevent";

    public AdWhaleMediationBanner(MediationBannerAdConfiguration mediationBannerAdConfiguration, MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mediationAdLoadCallback) {
        this.mediationBannerAdConfiguration = mediationBannerAdConfiguration;
        this.mediationAdLoadCallback = mediationAdLoadCallback;
    }

    /** Loads a banner ad from the third party ad network. */
    public void loadAd() {
        //아래가 미디에이션 서버로부터 파라미터 설정한 값 받아오는 부분
        String serverParameter = mediationBannerAdConfiguration.getServerParameters().getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        if (TextUtils.isEmpty(serverParameter)) {
            mediationAdLoadCallback.onFailure(new AdError(ERROR_NO_AD_UNIT_ID, "Ad unit id is empty", CUSTOM_EVENT_ERROR_DOMAIN));
            return;
        }

        Context context = mediationBannerAdConfiguration.getContext();

        // AdwhaleMediation SDK 초기화
        AdWhaleMediationAds.init(context, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(AdWhaleMediationBanner.class.getSimpleName(), "statusCode:" + statusCode + ", message:" + message);
            }
        });

        // ADWHALE_AD_SIZE 변환
        AdSize size = mediationBannerAdConfiguration.getAdSize();
        int widthInPixels = size.getWidthInPixels(context);
        int heightInPixels = size.getHeightInPixels(context);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int widthInDp = Math.round(widthInPixels / displayMetrics.density);
        int heightInDp = Math.round(heightInPixels / displayMetrics.density);
        String adSizeString = widthInDp + "x" + heightInDp;

        ADWHALE_AD_SIZE adwhale_ad_size = ADWHALE_AD_SIZE.BANNER320x50;

        switch (adSizeString) {
            case "320x50":
                adwhale_ad_size = ADWHALE_AD_SIZE.BANNER320x50;
                break;

            case "320x100":
                adwhale_ad_size = ADWHALE_AD_SIZE.BANNER320x100;
                break;

            case "300x250":
                adwhale_ad_size = ADWHALE_AD_SIZE.BANNER300x250;
                break;

            case "250x250":
                adwhale_ad_size = ADWHALE_AD_SIZE.BANNER250x250;
                break;
        }

        if (context instanceof Activity) {
            Activity bannerActivity = (Activity) context;
            layout = bannerActivity.findViewById(kr.co.adwhale.sdk.core.R.id.adView);
        }

        // AdWhale Mediation View 생성
        adWhaleMediationAdView = new AdWhaleMediationAdView(context);
        adWhaleMediationAdView.setPlacementUid(serverParameter);
        adWhaleMediationAdView.setAdwhaleAdSize(adwhale_ad_size);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adWhaleMediationAdView, params);

        // AdWhale Mediation View 리스너 등록
        adWhaleMediationAdView.setAdWhaleMediationAdViewListener(new AdWhaleMediationAdViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(TAG, "onAdLoaded()");
                layout.removeView(adWhaleMediationAdView);

                if (adWhaleMediationAdView != null) {
                    bannerAdCallback = mediationAdLoadCallback.onSuccess(AdWhaleMediationBanner.this);
                    bannerAdCallback.onAdOpened();
                    bannerAdCallback.reportAdImpression();
                }
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.i(TAG, "statusCode:" + statusCode + ", message:" + message);
                mediationAdLoadCallback.onFailure(new AdError(statusCode, message, SAMPLE_SDK_DOMAIN));
                layout.removeView(adWhaleMediationAdView);
            }
        });

        // Main UI Thread에서 광고 load 필수
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                adWhaleMediationAdView.loadAd();
            }
        });
    }

    @NonNull
    @Override
    public View getView() {
        return adWhaleMediationAdView;
    }
}
