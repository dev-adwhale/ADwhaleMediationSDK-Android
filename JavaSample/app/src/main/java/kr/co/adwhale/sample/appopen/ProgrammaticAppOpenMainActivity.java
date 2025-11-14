package kr.co.adwhale.sample.appopen;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAdListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.R;

public class ProgrammaticAppOpenMainActivity extends AppCompatActivity implements AdWhaleMediationAppOpenAdListener {

    private static final String TAG = "AppOpenLifecycleTest";

    // 라이프사이클 관리 (사용자가 직접 처리)
    private DefaultLifecycleObserver appLifecycleObserver;

    // 백그라운드/포어그라운드 상태체크 플래그값 (사용자가 직접 처리)
    private boolean isAppInBackground = false;

    private AdWhaleMediationAppOpenAd adWhaleMediationAppOpenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_app_open_main);

        // 앱 라이프사이클 관리 Observer 등록: 백그라운드/포어그라운드 감지를 위해 등록 필요
        appLifecycleObserver = new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                Log.d(TAG, "App moved to foreground");

                // 사용자가 직접 처리: 포어그라운드로 전환 시 광고 표시
                // 필요에 따라 조건 체크 (예: 백그라운드 시간, 광고 간격 등)를 추가할 수 있음
                if (adWhaleMediationAppOpenAd != null && isAppInBackground) {
                    if (adWhaleMediationAppOpenAd != null) {
                        adWhaleMediationAppOpenAd.showAd(ProgrammaticAppOpenMainActivity.this);
                    }
                }
                // 사용자가 직접 처리: 백그라운드/포어그라운드 상태체크 플래그값 업데이트
                isAppInBackground = false;
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                Log.d(TAG, "App moved to background");

                // 사용자가 직접 처리: 백그라운드/포어그라운드 상태체크 플래그값 업데이트
                isAppInBackground = true;
            }
        };

        // ProcessLifecycleOwner에 Observer 등록
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

        // 로거 설정
        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None);

        // 초기화 코드
        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(TAG, ".onInitComplete(" + statusCode + ", " + message + ");");

                // 앱 오프닝 광고 null 체크: 이미 load하거나 생성된 앱 오프닝 광고가 있다면 onDestroy()가 있는 release() 호출
                if (adWhaleMediationAppOpenAd != null) {
                    release();
                }

                // 앱 오프닝 광고 생성
                adWhaleMediationAppOpenAd = new AdWhaleMediationAppOpenAd(ProgrammaticAppOpenMainActivity.this, getString(R.string.app_open_placement_uid));

                // 앱 오프닝 광고 콜백 리스너 등록
                adWhaleMediationAppOpenAd.setAdWhaleMediationAppOpenAdListener(ProgrammaticAppOpenMainActivity.this);

                // 앱 오프닝 광고 로드
                adWhaleMediationAppOpenAd.loadAd();
            }
        });
    }


    // 라이프사이클 onDestroy 콜백 시 반드시 onDestroy()가 있는 release() 호출 필요
    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        // ProcessLifecycleOwner Observer 해제
        if (appLifecycleObserver != null) {
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(appLifecycleObserver);
            appLifecycleObserver = null;
        }

        if (adWhaleMediationAppOpenAd != null) {
            // 앱 종료 시 또는 더 이상 앱오프닝 광고를 사용하지 않을 때 호출
            adWhaleMediationAppOpenAd.destroy();
            adWhaleMediationAppOpenAd = null;
        }
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 로드 성공 시 콜백됨
    @Override
    public void onAdLoaded() {
        Log.i(TAG, ".onAdLoaded();");
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 로드 실패 시 콜백됨
    @Override
    public void onAdFailedToLoad(int statusCode, String message) {
        Log.e(TAG, ".onAdFailedToLoad(" + statusCode + ", " + message + ");");
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 표시 실패 시 콜백됨
    @Override
    public void onAdFailedToShow(int statusCode, String message) {
        Log.e(TAG, ".onAdFailedToShow(" + statusCode + ", " + message + ");");
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 클릭 시 콜백됨
    @Override
    public void onAdClicked() {
        Log.i(TAG, ".onAdClicked();");
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 닫기 시 콜백됨
    @Override
    public void onAdDismissed() {
        Log.i(TAG, ".onAdDismissed();");
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 표시 성공 시 콜백됨
    @Override
    public void onAdShowed() {
        Log.i(TAG, ".onAdShowed();");
    }
}