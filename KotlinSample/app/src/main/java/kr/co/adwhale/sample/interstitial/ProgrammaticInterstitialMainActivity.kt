package kr.co.adwhale.sample.interstitial

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAdListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener

class ProgrammaticInterstitialMainActivity : AppCompatActivity(){

    private lateinit var etPlacementUid : EditText
    private lateinit var btnTest : Button
    private lateinit var btnShow : Button
    private lateinit var adWhaleMediationInterstitialAd : AdWhaleMediationInterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_interstitial_main)

        etPlacementUid = findViewById(R.id.etPlacementUid)
        btnTest = findViewById(R.id.btnTest)
        btnShow = findViewById(R.id.btnShow)

        AdWhaleMediationAds.init(this, AdWhaleMediationOnInitCompleteListener { statusCode, message ->
            Log.i(ProgrammaticInterstitialMainActivity::class.simpleName, "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)")
        })

        adWhaleMediationInterstitialAd = AdWhaleMediationInterstitialAd(etPlacementUid.text.toString())
        adWhaleMediationInterstitialAd.setAdWhaleMediationInterstitialAdListener(object :
            AdWhaleMediationInterstitialAdListener {
            override fun onAdLoaded() {
                Log.i(ProgrammaticInterstitialMainActivity::class.java.simpleName, ".onAdLoaded()")
                Toast.makeText(applicationContext, ".onAdLoaded()", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoadFailed(statusCode: Int, message: String) {
                Log.e(
                    ProgrammaticInterstitialMainActivity::class.java.simpleName,
                    ".onAdLoadFailed($statusCode, $message)"
                )
                Toast.makeText(
                    applicationContext,
                    ".onAdLoadFailed(statusCode:$statusCode, message:$message)", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdShowed() {
                Log.i(ProgrammaticInterstitialMainActivity::class.java.simpleName, ".onAdShowed()")
                Toast.makeText(applicationContext, ".onAdShowed()", Toast.LENGTH_SHORT).show()
            }

            override fun onAdShowFailed(statusCode: Int, message: String) {
                Log.e(
                    ProgrammaticInterstitialMainActivity::class.java.simpleName,
                    ".onAdShowFailed($statusCode, $message)"
                )
                Toast.makeText(
                    applicationContext,
                    ".onAdShowFailed(statusCode:$statusCode, message:$message)", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClosed() {
                Log.i(ProgrammaticInterstitialMainActivity::class.java.simpleName, ".onAdClosed()")
                Toast.makeText(applicationContext, ".onAdClosed()", Toast.LENGTH_SHORT).show()
            }
        })

        btnTest.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationInterstitialAd.loadAd()
        })
        btnShow.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationInterstitialAd.showAd()
        })
    }
}