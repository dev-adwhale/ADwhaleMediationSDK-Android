package kr.co.adwhale.sample;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.adwhale.sample.banner.ProgrammaticBannerMainActivity;
import kr.co.adwhale.sample.banner.XmlBannerMainActivity;
import kr.co.adwhale.sample.interstitial.ProgrammaticInterstitialMainActivity;
import kr.co.adwhale.sample.reward.ProgrammaticRewardAdMainActivity;

public class SampleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_main);

        EditText etMediaUid = findViewById(R.id.etMediaUid);
        Button btnProgrammaticBanner = findViewById(R.id.btnProgrammaticBanner);
        Button btnXmlBanner = findViewById(R.id.btnXmlBanner);
        Button btnInterstitial = findViewById(R.id.btnInterstitial);
        Button btnRewardAd = findViewById(R.id.btnRewardAd);

        etMediaUid.setText(getMetaData());
        btnProgrammaticBanner.setOnClickListener(view -> {
            setMetaData(etMediaUid.getText().toString());
            Intent intent = new Intent(this, ProgrammaticBannerMainActivity.class);
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
}