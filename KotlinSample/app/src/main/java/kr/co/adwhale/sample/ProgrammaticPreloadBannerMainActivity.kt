package kr.co.adwhale.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdBannerViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener

class ProgrammaticPreloadBannerMainActivity : AppCompatActivity() {

    private lateinit var root: RelativeLayout
    private lateinit var btnTest: Button
    private lateinit var rgBannerAdSize: RadioGroup
    private lateinit var etPlacementUid: EditText
    private lateinit var selectedAdWhaleAdSize: ADWHALE_AD_SIZE
    private lateinit var adWhaleMediationAdBannerView : AdWhaleMediationAdBannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_preload_banner_main)
        btnTest = findViewById(R.id.btnTest)
        rgBannerAdSize = findViewById(R.id.rgBannerAdSize)
        etPlacementUid = findViewById(R.id.etPlacementUid)
        root = findViewById(R.id.root)

        AdWhaleMediationAds.init(this, AdWhaleMediationOnInitCompleteListener { statusCode, message ->
            Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)");
        })

        adWhaleMediationAdBannerView = AdWhaleMediationAdBannerView(this)
        val params = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        root.addView(adWhaleMediationAdBannerView, params)

        adWhaleMediationAdBannerView.adWhaleMediationAdBannerViewListener =
            object : AdWhaleMediationAdBannerViewListener {
                override fun onAdLoaded() {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onAdLoaded()")
                    if (adWhaleMediationAdBannerView != null) {
                        adWhaleMediationAdBannerView.show()
                    }
                }

                override fun onAdLoadFailed(statusCode: Int, message: String?) {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onAdLoadFailed($statusCode, $message)")
                }

                override fun onShowLandingScreen() {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onShowLandingScreen()")
                }

                override fun onCloseLandingScreen() {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onCloseLandingScreen()")
                }

                override fun onTimeout(errorMessage: String?) {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onTimeout($errorMessage)")
                }

                override fun onClickAd() {
                    Log.i(ProgrammaticPreloadBannerMainActivity::class.simpleName, ".onClickAd()")
                }
            }

        rgBannerAdSize.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.rdBanner320x50 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x50
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조")
                }

                R.id.rdBanner320x100 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x100
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조")
                }

                R.id.rdBanner300x250 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER300x250
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조")
                }

                R.id.rdBanner250x250 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER250x250
                    etPlacementUid.setText("ADwhale Mediation SDK 가이드 내 키값 참조")
                }

                else -> {
                    etPlacementUid.setText("")
                }
            }
        }

        btnTest.setOnClickListener(View.OnClickListener { view : View? ->
            adWhaleMediationAdBannerView.placementUid = etPlacementUid.text.toString()
            adWhaleMediationAdBannerView.adwhaleAdSize = selectedAdWhaleAdSize
            adWhaleMediationAdBannerView.loadAd()
        })
    }
}