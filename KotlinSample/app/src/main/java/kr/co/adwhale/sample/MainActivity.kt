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
import androidx.constraintlayout.widget.ConstraintLayout
import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds

class MainActivity : AppCompatActivity() {

    private lateinit var root: ConstraintLayout
    private lateinit var btnTest: Button
    private lateinit var rgBannerAdSize: RadioGroup
    private lateinit var etPlacementUid: EditText
    private lateinit var selectedAdWhaleAdSize: ADWHALE_AD_SIZE
    private lateinit var adWhaleMediationAdView: AdWhaleMediationAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnTest = findViewById(R.id.btnTest)
        rgBannerAdSize = findViewById(R.id.rgBannerAdSize)
        etPlacementUid = findViewById(R.id.etPlacementUid)
        root = findViewById<View>(R.id.root) as ConstraintLayout
        adWhaleMediationAdView = AdWhaleMediationAdView(this)
        val params = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        root!!.addView(adWhaleMediationAdView, params)
        adWhaleMediationAdView!!.adWhaleMediationAdViewListener =
            object : AdWhaleMediationAdViewListener {
                override fun onAdLoaded() {
                    Log.i(MainActivity::class.java.simpleName, "[QA] onAdLoaded()")
                }

                override fun onAdLoadFailed(statusCode: Int, message: String) {
                    Log.i(
                        MainActivity::class.java.simpleName,
                        "[QA] statusCode:$statusCode, message:$message"
                    )
                }
            }
        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                MainActivity::class.java.simpleName,
                "[QA] statusCode:$statusCode, message:$message"
            )
        }
        rgBannerAdSize.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup: RadioGroup?, checkedId: Int ->
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
        })
        btnTest.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationAdView!!.placementUid = etPlacementUid.getText().toString()
            adWhaleMediationAdView!!.adwhaleAdSize = selectedAdWhaleAdSize
            adWhaleMediationAdView!!.loadAd()
        })
    }

    override fun onResume() {
        super.onResume()
        adWhaleMediationAdView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        adWhaleMediationAdView!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adWhaleMediationAdView!!.destroy()
    }
}