package kr.co.adwhale.sample.nativead;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.adwhale.sample.R;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;
import net.adwhale.sdk.mediation.ads.AdWhaleNativeAdBinder;
import net.adwhale.sdk.utils.AdWhaleLog;

/**
 * 커스텀 바인딩 방식 네이티브 광고 샘플 액티비티
 * DARO 스타일의 바인딩 API를 사용하여 네이티브 광고를 표시합니다.
 */
public class ProgrammaticCustomBindingNativeMainActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProgrammaticCustomBindingNativeMainActivity.class.getSimpleName();

    private EditText etPlacementUid;
    private Button btnAdRequest;
    private Button btnShowAd;
    private RelativeLayout adViewRoot;
    private SwitchMaterial switchLogger;
    private AdWhaleMediationNativeAdView adWhaleMediationNativeAdView;
    private boolean isAdLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_custom_binding_native_main);

        bindView();
        setListener();

        // 1. Init
        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")");
            }
        });

        // 2. nativead
        createNativeAd();
    }

    /**
     * 뷰 바인딩
     */
    private void bindView() {
        switchLogger = findViewById(R.id.switch_logger);
        etPlacementUid = findViewById(R.id.etPlacementUid);
        etPlacementUid.setText(getResources().getString(R.string.native_placement_uid));
        btnAdRequest = findViewById(R.id.btnAdRequest);
        btnShowAd = findViewById(R.id.btnShowAd);
        btnShowAd.setEnabled(false);
        adViewRoot = findViewById(R.id.adViewRoot);
    }

    /**
     * 리스너 이벤트 등록
     */
    private void setListener() {
        switchLogger.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "로거가 활성화되었습니다.");
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose);
            } else {
                Log.d(TAG, "로거가 비활성화되었습니다.");
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None);
            }
        });

        btnAdRequest.setOnClickListener(view -> {
            adWhaleMediationNativeAdView.setPlacementUid(etPlacementUid.getText().toString());
            // custom native AdBinder 생성
            AdWhaleNativeAdBinder adWhaleNativeAdBinder = new AdWhaleNativeAdBinder.Builder(this, R.layout.custom_native_ad_layout)
                    .setIconViewId(R.id.view_icon)
                    .setTitleViewId(R.id.view_title)
                    .setBodyTextViewId(R.id.view_body)
                    .setCallToActionViewId(R.id.button_cta)
                    .setMediaViewGroupId(R.id.view_media)
                    .build();

            // 커스텀 바인딩으로 광고 로드
            adWhaleMediationNativeAdView.loadAdWithBinder(adWhaleNativeAdBinder);
        });

        btnShowAd.setOnClickListener(view -> {
            if (isAdLoaded) {
                adWhaleMediationNativeAdView.show();
                btnShowAd.setEnabled(false);
            }
        });
    }

    private void createNativeAd() {
        adWhaleMediationNativeAdView = new AdWhaleMediationNativeAdView(this);
        adWhaleMediationNativeAdView.setAdWhaleMediationNativeAdViewListener(new AdWhaleMediationNativeAdViewListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(LOG_TAG, ".onNativeAdLoaded()");
                isAdLoaded = true;
                btnShowAd.setEnabled(true);
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 로드 성공!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdFailedToLoad(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")");
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 로드 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdShowFailed(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdShowFailed(" + errorCode + ", " + errorMessage + ")");
                Toast.makeText(getApplicationContext(), ".onNativeAdShowFailed(statusCode:" + errorCode + ", message:" + errorMessage + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClicked() {
                Log.i(LOG_TAG, ".onNativeAdClicked()");
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 클릭!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed()");
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 닫힘", Toast.LENGTH_SHORT).show();
            }
        });

        //root 에 add
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adViewRoot.addView(adWhaleMediationNativeAdView, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, ".onResume()");
        if(adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, ".onPause()");
        if(adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, ".onDestroy()");
        if(adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView.destroy();
        }
    }
}