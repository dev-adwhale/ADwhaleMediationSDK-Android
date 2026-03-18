package kr.co.adwhale.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import net.adwhale.sdk.mediation.ads.ADWHALE_POPUP_AD_CLOSE_REASON;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAd;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAdListener;
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener;
import net.adwhale.sdk.utils.AdWhaleLog;

import kr.co.adwhale.sample.appopen.ProgrammaticAppOpenMainActivity;
import kr.co.adwhale.sample.banner.ProgrammaticBannerMainActivity;
import kr.co.adwhale.sample.banner.XmlBannerMainActivity;
import kr.co.adwhale.sample.interstitial.ProgrammaticInterstitialMainActivity;
import kr.co.adwhale.sample.nativead.ProgrammaticCustomBindingNativeMainActivity;
import kr.co.adwhale.sample.nativead.ProgrammaticTemplateBindingNativeMainActivity;
import kr.co.adwhale.sample.nativead.StyledTemplateBindingNativeMainActivity;
import kr.co.adwhale.sample.reward.ProgrammaticRewardAdMainActivity;
import kr.co.adwhale.sample.transition.TransitionTestMainActivity;

public class SampleMainActivity extends AppCompatActivity {


    private static final String LOG_TAG = SampleMainActivity.class.getSimpleName();

    private AdWhaleMediationExitPopupAd adWhaleMediationExitPopupAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_main);


        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Error);

        AdWhaleMediationAds.init(this, new AdWhaleMediationOnInitCompleteListener() {
            @Override
            public void onInitComplete(int statusCode, String message) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")");
                loadExitPopupAd();
            }
        });

        EditText etMediaUid = findViewById(R.id.etMediaUid);
        Button btnProgrammaticBanner = findViewById(R.id.btnProgrammaticBanner);
        Button btnTransitionAd = findViewById(R.id.btn_move_to_transition_ad);
        Button btnXmlBanner = findViewById(R.id.btnXmlBanner);
        Button btnInterstitial = findViewById(R.id.btnInterstitial);
        Button btnRewardAd = findViewById(R.id.btnRewardAd);
        Button btnAppOpenAd = findViewById(R.id.btnAppOpenAd);
        Button btnNativeAdCustomBinding = findViewById(R.id.btn_native_custom_binding);
        Button btnNativeAdTemplateBinding = findViewById(R.id.btn_native_template_binding);
        Button btnNativeAdTemplateBindingWithStyle = findViewById(R.id.btn_native_template_binding_with_style);

        etMediaUid.setText(getMetaData());
        btnProgrammaticBanner.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticBannerMainActivity.class);
            startActivity(intent);
        });

        btnTransitionAd.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, TransitionTestMainActivity.class);
            startActivity(intent);
        });

        btnXmlBanner.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, XmlBannerMainActivity.class);
            startActivity(intent);
        });

        btnInterstitial.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticInterstitialMainActivity.class);
            startActivity(intent);
        });

        btnRewardAd.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticRewardAdMainActivity.class);
            startActivity(intent);
        });

        btnAppOpenAd.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticAppOpenMainActivity.class);
            startActivity(intent);
        });

        btnNativeAdCustomBinding.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticCustomBindingNativeMainActivity.class);
            startActivity(intent);
        });

        btnNativeAdTemplateBinding.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticTemplateBindingNativeMainActivity.class);
            startActivity(intent);
        });

        btnNativeAdTemplateBindingWithStyle.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, StyledTemplateBindingNativeMainActivity.class);
            startActivity(intent);
        });

    }

    private void setMetaData(String value){
        if(value == null || value.isEmpty()){
            return;
        }

        try {
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 세팅.
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            bundle.putString("net.adwhale.sdk.mediation.PUBLISHER_UID", value);
        } catch (PackageManager.NameNotFoundException e) {
            throw new UnsupportedOperationException("Publisher Uid value is required. Please check <meta-data> in AndroidManifest.xml.");
        }
    }

    private String getMetaData() {
        try{
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 가져온다.
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            return String.valueOf(bundle.get("net.adwhale.sdk.mediation.PUBLISHER_UID"));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private void loadExitPopupAd() {
        adWhaleMediationExitPopupAd = new AdWhaleMediationExitPopupAd(getResources().getString(R.string.exit_popup_placement_uid));
//                adWhaleMediationExitPopupAd.disableOnExitPopupBackKey();
        adWhaleMediationExitPopupAd.setCustomizeButtonText("테스트 취소", "테스트 종료");
        adWhaleMediationExitPopupAd.setCustomDescription("테스트 문구");
        adWhaleMediationExitPopupAd.setAdWhaleMediationExitPopupAdListener(new AdWhaleMediationExitPopupAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
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
            public void onAdClosed(ADWHALE_POPUP_AD_CLOSE_REASON adwhaleExitPopupAdCloseReason) {
                Log.d(LOG_TAG, "onAdClosed(" + adwhaleExitPopupAdCloseReason.getCloseReasonTypeString() + ");");
            }
        });
        adWhaleMediationExitPopupAd.loadAd();
    }

    @Override
    protected void onResume() {
        if (adWhaleMediationExitPopupAd != null)
            adWhaleMediationExitPopupAd.resume(this); // 필수 호출
        super.onResume();
    }

    // Back Key가 눌러졌을 때, CloseAd 호출
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitPopupAd();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitPopupAd() {
        if(adWhaleMediationExitPopupAd != null) {
            adWhaleMediationExitPopupAd.showAd(SampleMainActivity.this, SampleMainActivity.this.getSupportFragmentManager());
        }
    }

    private void showDefaultClosePopup() {
        new AlertDialog.Builder(this).setTitle("").setMessage("종료 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, ".onDestroy()");
        if(adWhaleMediationExitPopupAd != null) {
            adWhaleMediationExitPopupAd.destroy();
        }
        super.onDestroy();
    }
}