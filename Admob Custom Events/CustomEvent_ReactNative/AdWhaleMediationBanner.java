package com.example.rnadmob;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.ThemedReactContext;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;

import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

public class AdWhaleMediationBanner implements MediationBannerAd {
    final static String TAG = AdWhaleMediationBanner.class.getSimpleName();
    private final MediationBannerAdConfiguration mediationBannerAdConfiguration;
    private final MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mediationAdLoadCallback;
    private MediationBannerAdCallback bannerAdCallback;

    private AdWhaleMediationAdBannerView adWhaleMediationAdView = null;

    /** Error raised when the custom event adapter cannot obtain the ad unit id. */
    public static final int ERROR_NO_AD_UNIT_ID = 101;
    /** Error raised when the custom event adapter cannot obtain the activity context. */
    public static final int ERROR_NO_ACTIVITY_CONTEXT = 103;
    public static final String SAMPLE_SDK_DOMAIN = "com.example.rnadmob";
    public static final String CUSTOM_EVENT_ERROR_DOMAIN = "com.google.ads.mediation.sample.customevent";

    private Activity currentActivity;

    private boolean load = false;

    public AdWhaleMediationBanner(MediationBannerAdConfiguration mediationBannerAdConfiguration, MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mediationAdLoadCallback) {
        this.mediationBannerAdConfiguration = mediationBannerAdConfiguration;
        this.mediationAdLoadCallback = mediationAdLoadCallback;
    }

    /** Loads a banner ad from the third party ad network. */
    public void loadAd() {
        Log.d(TAG, "loadAd()");
        load = true;

        /** 미디에이션 서버로부터 파라미터 설정 값 획득 **/
        String serverParameter = mediationBannerAdConfiguration.getServerParameters().getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        if (TextUtils.isEmpty(serverParameter)) {
            mediationAdLoadCallback.onFailure(new AdError(ERROR_NO_AD_UNIT_ID, "Ad unit id is empty", CUSTOM_EVENT_ERROR_DOMAIN));
            return;
        }
        /** 미디에이션 서버로부터 파라미터 설정 값 획득 **/

        /** 현재 화면의 Activity Context 획득 **/
        Context context = mediationBannerAdConfiguration.getContext();

        if (context instanceof Activity) {
            currentActivity = (Activity) context;
        } else if (context instanceof ThemedReactContext) {
            currentActivity = ((ThemedReactContext) context).getCurrentActivity();
        }

        if (currentActivity == null) {
            Log.e(TAG, "Activity context is empty");
            mediationAdLoadCallback.onFailure(new AdError(ERROR_NO_ACTIVITY_CONTEXT, "Activity context is empty", CUSTOM_EVENT_ERROR_DOMAIN));
            return;

        }
        /** 현재 화면의 Activity Context 획득 **/

        /** Admob ad_size를 Adwhale ad_size로 변환 **/
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
        /** Admob ad_size를 Adwhale ad_size로 변환 **/

        // Adwhale SDK 초기화 코드
        AdWhaleMediationAds.init(currentActivity, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(TAG, ".onInitComplete(" + statusCode + ", " + message + ")");
            }
        });

        // 배너 뷰 생성
        adWhaleMediationAdView = new AdWhaleMediationAdBannerView(currentActivity);
        adWhaleMediationAdView.setPlacementUid(serverParameter);
        adWhaleMediationAdView.setAdwhaleAdSize(adwhale_ad_size);

        // 배너 뷰 콜백 리스너 등록
        adWhaleMediationAdView.setAdWhaleMediationAdBannerViewListener(new AdWhaleMediationAdBannerViewListener() {
            @Override
            public void onAdLoaded() {
                Log.i(TAG, ".onAdLoaded()");
                if(adWhaleMediationAdView != null) {
                    if (adWhaleMediationAdView.getDisplay() == null && !load) {
                        adWhaleMediationAdView.destroy();
                        adWhaleMediationAdView = null;
                        return;
                    }

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 광고 표시는 반드시 Main UI Thread에서 진행
                            adWhaleMediationAdView.show();

                            bannerAdCallback = mediationAdLoadCallback.onSuccess(AdWhaleMediationBanner.this);
                            bannerAdCallback.onAdOpened();
                            bannerAdCallback.reportAdImpression();

                            load = false;
                        }
                    });
                }
            }

            @Override
            public void onAdLoadFailed(int statusCode, String message) {
                Log.e(TAG, ".onAdLoadFailed(" + statusCode + ", " + message + ")");
                mediationAdLoadCallback.onFailure(new AdError(statusCode, message, SAMPLE_SDK_DOMAIN));
            }

            @Override
            public void onShowLandingScreen() {
                Log.i(TAG, ".onShowLandingScreen()");
            }

            @Override
            public void onCloseLandingScreen() {
                Log.i(TAG, ".onCloseLandingScreen()");
            }

            @Override
            public void onTimeout(String errorMessage) {
                Log.i(TAG, ".onTimeout()");
            }

            @Override
            public void onClickAd() {
                Log.i(TAG, ".onClickAd()");
                bannerAdCallback.reportAdClicked();
            }
        });


        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // 광고 요청시 반드시 Main UI Thread에서 진행
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