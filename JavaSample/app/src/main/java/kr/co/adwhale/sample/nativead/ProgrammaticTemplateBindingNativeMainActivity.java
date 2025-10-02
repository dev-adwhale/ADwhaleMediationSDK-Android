package kr.co.adwhale.sample.nativead;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.adwhale.sample.R;

import android.app.Dialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.adwhale.sdk.mediation.ads.ADWHALE_NATIVE_TEMPLATE;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;

import net.adwhale.sdk.utils.AdWhaleLog;

/**
 * 고정 템플릿 바인딩 방식 네이티브 광고 샘플 액티비티
 * 기존의 고정 템플릿을 사용하여 네이티브 광고를 표시합니다.
 */
public class ProgrammaticTemplateBindingNativeMainActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProgrammaticTemplateBindingNativeMainActivity.class.getSimpleName();

    private EditText etPlacementUid;
    private Button btnAdRequest;
    private Button btnShowAd;
    private RelativeLayout adViewRoot;
    private SwitchMaterial switchLogger;
    private RadioButton radioSmall, radioMedium, radioFullscreenDialog;
    private AdWhaleMediationNativeAdView adWhaleMediationNativeAdView;
    private Dialog fullscreenDialog;

    private AdWhaleMediationNativeAdView fullscreenAdView;

    private boolean isAdLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_template_binding_native_main);

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
        adViewRoot = findViewById(R.id.adViewRoot);

        // 템플릿 선택 라디오 버튼들
        radioSmall = findViewById(R.id.radioSmall);
        radioMedium = findViewById(R.id.radioMedium);
        radioFullscreenDialog = findViewById(R.id.radioFullscreenDialog);

        // 초기 상태 설정
        btnShowAd.setEnabled(false);
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
            if(etPlacementUid.getText().toString() != null && !etPlacementUid.getText().toString().isEmpty()){
                adWhaleMediationNativeAdView.setPlacementUid(etPlacementUid.getText().toString());
            }

            // 선택된 템플릿에 따라 광고 로드 (표시하지 않음)
            if (radioSmall.isChecked()) {
                adWhaleMediationNativeAdView.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.SMALL);
            } else if (radioMedium.isChecked()) {
                adWhaleMediationNativeAdView.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.MEDIUM);
            } else if (radioFullscreenDialog.isChecked()) {
                // FULLSCREEN 템플릿을 다이얼로그에 로드
                loadFullscreenNativeAdDialog();
            }
        });

        btnShowAd.setOnClickListener(view -> {
            if (isAdLoaded) {
                if (radioFullscreenDialog.isChecked()) {
                    // FULLSCREEN 다이얼로그 표시
                    showFullscreenNativeAdDialog();
                } else {
                    // 일반 광고 표시
                    adWhaleMediationNativeAdView.show();
                    btnShowAd.setEnabled(false);
                }
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
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 로드 성공!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdFailedToLoad(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")");
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 로드 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdShowFailed(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdShowFailed(" + errorCode + ", " + errorMessage + ")");
                Toast.makeText(getApplicationContext(), ".onNativeAdShowFailed(statusCode:" + errorCode + ", message:" + errorMessage + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClicked() {
                Log.i(LOG_TAG, ".onNativeAdClicked()");
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 클릭!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed()");
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 닫힘", Toast.LENGTH_SHORT).show();
            }
        });

        //root 에 add
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adViewRoot.addView(adWhaleMediationNativeAdView, params);
    }

    /**
     * FULLSCREEN 템플릿을 다이얼로그에 로드
     */
    private void loadFullscreenNativeAdDialog() {
        // FULLSCREEN 네이티브 광고 뷰 생성
        fullscreenAdView = new AdWhaleMediationNativeAdView(this);

        // placementUid 설정
        String placementUid = etPlacementUid.getText().toString();
        if (placementUid != null && !placementUid.isEmpty()) {
            fullscreenAdView.setPlacementUid(placementUid);
        } else {
            // 기본값 설정
            fullscreenAdView.setPlacementUid(getResources().getString(R.string.native_placement_uid));
        }

        fullscreenAdView.setAdWhaleMediationNativeAdViewListener(new AdWhaleMediationNativeAdViewListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(LOG_TAG, ".onNativeAdLoaded() - Fullscreen Dialog");
                isAdLoaded = true;
                btnShowAd.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Fullscreen 네이티브 광고 로드 성공!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdFailedToLoad(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ") - Fullscreen Dialog");
                Toast.makeText(getApplicationContext(), "Fullscreen 네이티브 광고 로드 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdShowFailed(int errorCode, String errorMessage) {
                Log.e(LOG_TAG, ".onNativeAdShowFailed(" + errorCode + ", " + errorMessage + ")");
                Toast.makeText(getApplicationContext(), ".onNativeAdShowFailed(statusCode:" + errorCode + ", message:" + errorMessage + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClicked() {
                Log.i(LOG_TAG, ".onNativeAdClicked() - Fullscreen Dialog");
                Toast.makeText(getApplicationContext(), "Fullscreen 네이티브 광고 클릭!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed() - Fullscreen Dialog");
                Toast.makeText(getApplicationContext(), "Fullscreen 네이티브 광고 닫힘", Toast.LENGTH_SHORT).show();
                // 광고가 닫힐 때 다이얼로그도 닫기
                if (fullscreenDialog != null && fullscreenDialog.isShowing()) {
                    fullscreenDialog.dismiss();
                }
            }
        });

        // 광고 로드
        Log.d(LOG_TAG, "FULLSCREEN 광고 로드 시작");
        fullscreenAdView.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.FULLSCREEN);
    }

    /**
     * FULLSCREEN 템플릿을 다이얼로그로 표시
     */
    private void showFullscreenNativeAdDialog() {
        if (fullscreenAdView == null || !isAdLoaded) {
            Toast.makeText(this, "광고가 로드되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 간단한 다이얼로그 생성
        fullscreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        // 프로그래매틱으로 레이아웃 생성
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.setBackgroundColor(0xFFFFFFFF); // 배경색을 흰색으로 설정

        // 헤더 레이아웃
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        headerLayout.setPadding(16, 16, 16, 16);
        headerLayout.setBackgroundColor(0xFF000000);

        TextView titleText = new TextView(this);
        titleText.setText("Fullscreen Native Ad");
        titleText.setTextColor(0xFFFFFFFF);
        titleText.setTextSize(18);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        titleText.setLayoutParams(titleParams);

        Button closeButton = new Button(this);
        closeButton.setText("X");
        closeButton.setTextColor(0xFFFFFFFF);
        closeButton.setBackgroundColor(0xFF000000);
        closeButton.setOnClickListener(v -> {
            if (fullscreenDialog != null && fullscreenDialog.isShowing()) {
                fullscreenDialog.dismiss();
            }
        });

        headerLayout.addView(titleText);
        headerLayout.addView(closeButton);

        // 광고 영역 - MediaView 최소 크기 120x120dp 보장
        RelativeLayout adContainer = new RelativeLayout(this);
        adContainer.setPadding(16, 16, 16, 16);
        adContainer.setBackgroundColor(0xFFF0F0F0); // 회색 배경으로 광고 영역 표시

        // MediaView 최소 크기 보장을 위한 최소 높이 설정 (120dp + padding)
        int minHeightDp = 120 + 32; // 120dp + 16dp padding * 2
        int minHeightPx = (int) (minHeightDp * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        adParams.height = Math.max(minHeightPx, 0); // 최소 높이 보장
        adContainer.setLayoutParams(adParams);

        mainLayout.addView(headerLayout);
        mainLayout.addView(adContainer);

        fullscreenDialog.setContentView(mainLayout);

        fullscreenDialog.setOnDismissListener(dialogInterface -> {
            if(fullscreenAdView != null) {
                fullscreenAdView.destroy();
            }
        });

        // 다이얼로그가 표시된 후 광고 표시
        fullscreenDialog.setOnShowListener(dialog -> {
            // 이미 로드된 광고를 표시
            fullscreenAdView.show();
            adContainer.addView(fullscreenAdView);
        });

        // 다이얼로그 표시
        fullscreenDialog.show();
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
        if(fullscreenDialog != null && fullscreenDialog.isShowing()) {
            fullscreenDialog.dismiss();
        }
    }
}