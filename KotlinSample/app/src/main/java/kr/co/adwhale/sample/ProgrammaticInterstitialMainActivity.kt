package kr.co.adwhale.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAdListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener

class ProgrammaticInterstitialMainActivity : AppCompatActivity(){

    private lateinit var etPlacementUid : EditText
    private lateinit var btnTest : Button
    private lateinit var adWhaleMediationInterstitialAd : AdWhaleMediationInterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_interstitial_main)

        etPlacementUid = findViewById(R.id.etPlacementUid)
        btnTest = findViewById(R.id.btnTest)

        AdWhaleMediationAds.init(this, AdWhaleMediationOnInitCompleteListener { statusCode, message ->
            Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)")
        })

        adWhaleMediationInterstitialAd = AdWhaleMediationInterstitialAd(etPlacementUid.text.toString())
        adWhaleMediationInterstitialAd.adWhaleMediationInterstitialAdListener =
            object : AdWhaleMediationInterstitialAdListener {
                override fun onAdLoaded() {
                    Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, ".onAdLoaded()")
                    adWhaleMediationInterstitialAd.showAd()
                }

                override fun onAdLoadFailed(statusCode : Int, message : String?) {
                    Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, ".onAdLoadFailed($statusCode, $message)")
                }

                override fun onAdShowed() {
                    Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, ".onAdShowed()")
                }

                override fun onAdShowFailed(statusCode : Int, message : String?) {
                    Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, ".onAdShowFailed($statusCode, $message)")
                }

                override fun onAdClosed() {
                    Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, ".onAdClosed()")
                }
            }

        btnTest.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationInterstitialAd.loadAd()
        })
    }
}