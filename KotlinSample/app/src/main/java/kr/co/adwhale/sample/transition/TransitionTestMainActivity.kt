package kr.co.adwhale.sample.transition

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.ADWHALE_POPUP_AD_CLOSE_REASON
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationTransitionPopupAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationTransitionPopupAdListener
import net.adwhale.sdk.utils.AdWhaleLog

class TransitionTestMainActivity : AppCompatActivity() {
    private var adWhaleMediationTransitionPopupAd: AdWhaleMediationTransitionPopupAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_test_main)
        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Error)

        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")")
                loadTransitionPopupAd()
            }
        })
    }

    private fun loadTransitionPopupAd() {
        adWhaleMediationTransitionPopupAd =
            AdWhaleMediationTransitionPopupAd(getResources().getString(R.string.transition_popup_placement_uid))
        adWhaleMediationTransitionPopupAd!!.setAdWhaleMediationTransitionPopupAdListener(object :
            AdWhaleMediationTransitionPopupAdListener {
            override fun onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded")
                showTransitionPopupAd()
            }

            override fun onAdLoadFailed(statusCode: Int, message: String?) {
                Log.d(LOG_TAG, "onAdLoadFailed(" + statusCode + ", " + message + ");")
            }

            override fun onAdShowed() {
                Log.d(LOG_TAG, "onAdShowed")
            }

            override fun onAdClicked() {
                Log.d(LOG_TAG, "onAdClicked")
            }

            override fun onAdShowFailed(statusCode: Int, message: String?) {
                Log.d(LOG_TAG, "onAdShowFailed(" + statusCode + ", " + message + ");")
            }

            override fun onAdClosed(adwhalePopupAdCloseReason: ADWHALE_POPUP_AD_CLOSE_REASON) {
                Log.d(
                    LOG_TAG,
                    "onAdClosed(" + adwhalePopupAdCloseReason.getCloseReasonTypeString() + ");"
                )
            }
        })
        adWhaleMediationTransitionPopupAd!!.loadAd()
    }


    override fun onResume() {
        if (adWhaleMediationTransitionPopupAd != null) adWhaleMediationTransitionPopupAd!!.resume(
            this
        ) // 필수 호출

        super.onResume()
    }

    private fun showTransitionPopupAd() {
        if (adWhaleMediationTransitionPopupAd != null) {
            adWhaleMediationTransitionPopupAd!!.showAd(
                this@TransitionTestMainActivity,
                this@TransitionTestMainActivity.getSupportFragmentManager()
            )
        }
    }

    override fun onDestroy() {
        Log.i(LOG_TAG, ".onDestroy()")
        if (adWhaleMediationTransitionPopupAd != null) {
            adWhaleMediationTransitionPopupAd!!.destroy()
        }
        super.onDestroy()
    }

    companion object {
        private val LOG_TAG: String = TransitionTestMainActivity::class.java.getSimpleName()
    }
}