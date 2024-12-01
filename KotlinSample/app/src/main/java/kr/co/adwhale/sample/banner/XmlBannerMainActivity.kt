package kr.co.adwhale.sample.banner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener

class XmlBannerMainActivity : AppCompatActivity() {

    private lateinit var adWhaleMediationAdView : AdWhaleMediationAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml_banner_main)

        AdWhaleMediationAds.init(this, AdWhaleMediationOnInitCompleteListener { statusCode, message ->
            Log.i(XmlBannerMainActivity::class.simpleName, "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)")
        })

        adWhaleMediationAdView = findViewById(R.id.test)
        adWhaleMediationAdView.adWhaleMediationAdViewListener =
            object : AdWhaleMediationAdViewListener {
                override fun onAdLoaded() {
                    Log.i(XmlBannerMainActivity::class.simpleName, ".onAdLoaded()")
                }

                override fun onAdLoadFailed(statusCode : Int, message : String?) {
                    Log.i(XmlBannerMainActivity::class.simpleName, ".onAdLoadFailed($statusCode, $message)")
                }

                override fun onAdClicked() {
                    Log.i(XmlBannerMainActivity::class.simpleName, ".onAdClicked()")
                }
            }

        adWhaleMediationAdView.loadAd()
    }

    override fun onResume() {
        super.onResume()
        adWhaleMediationAdView.resume()
    }

    override fun onPause() {
        super.onPause()
        adWhaleMediationAdView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adWhaleMediationAdView.destroy()
    }
}