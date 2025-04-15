package kr.co.adwhale.sample.banner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.co.adwhale.sample.databinding.ActivityXmlBannerMainBinding
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.utils.AdWhaleLog

class XmlBannerMainActivity : AppCompatActivity() {
    private var mBinding: ActivityXmlBannerMainBinding? = null
    private val binding get() = mBinding!!

    private lateinit var adWhaleMediationAdView : AdWhaleMediationAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityXmlBannerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)

        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                XmlBannerMainActivity::class.simpleName,
                "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)"
            )
        }

        adWhaleMediationAdView = binding.test
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
        adWhaleMediationAdView.resume()
        super.onResume()
    }

    override fun onPause() {
        adWhaleMediationAdView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adWhaleMediationAdView.destroy()
        mBinding = null
        super.onDestroy()
    }
}