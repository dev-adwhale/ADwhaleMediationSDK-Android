package kr.co.adwhale.sample.reward

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.databinding.ActivityProgrammaticRewardAdMainBinding
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationFullScreenContentCallback
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardedAdLoadCallback
import net.adwhale.sdk.utils.AdWhaleLog

class ProgrammaticRewardAdMainActivity : AppCompatActivity() {
    private var mBinding: ActivityProgrammaticRewardAdMainBinding? = null
    private val binding get() = mBinding!!

    private lateinit var btnTest: Button

    private lateinit var btnShow: Button

    private lateinit var etPlacementUid: EditText

    private lateinit var adWhaleMediationRewardAd: AdWhaleMediationRewardAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgrammaticRewardAdMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnTest = binding.btnTest
        btnShow = binding.btnShow
        etPlacementUid = binding.etPlacementUid

        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)

        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                ProgrammaticRewardAdMainActivity::class.java.simpleName,
                ".onInitComplete($statusCode, $message)"
            )
        }

        adWhaleMediationRewardAd = AdWhaleMediationRewardAd(etPlacementUid.text.toString())

        adWhaleMediationRewardAd.setAdWhaleMediationFullScreenContentCallback(object :
            AdWhaleMediationFullScreenContentCallback {
            override fun onAdClicked() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdClicked()")
            }

            override fun onAdDismissed() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdDismissed()")
                Toast.makeText(applicationContext, ".onAdDismissed()", Toast.LENGTH_SHORT).show()
            }

            override fun onFailedToShow(statusCode: Int, message: String) {
                Log.i(
                    ProgrammaticRewardAdMainActivity::class.java.simpleName,
                    ".onFailedToShow($statusCode, $message)"
                )
                Toast.makeText(
                    applicationContext,
                    ".onFailedToShow(statusCode:$statusCode, message:$message)", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdShowed() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdShowed()")
                Toast.makeText(applicationContext, ".onAdShowed()", Toast.LENGTH_SHORT).show()
            }
        })

        btnTest.setOnClickListener {
            adWhaleMediationRewardAd.loadAd(object : AdWhaleMediationRewardedAdLoadCallback {
                override fun onAdLoaded(
                    adWhaleMediationRewardAd: AdWhaleMediationRewardAd?,
                    message: String
                ) {
                    Log.i(
                        ProgrammaticRewardAdMainActivity::class.java.simpleName,
                        ".onAdLoaded($message)"
                    )
                    Toast.makeText(
                        applicationContext,
                        ".onAdLoaded($message)",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdFailedToLoad(statusCode: Int, message: String) {
                    Log.i(
                        ProgrammaticRewardAdMainActivity::class.java.simpleName,
                        ".onAdFailedToLoad($statusCode, $message)"
                    )
                    Toast.makeText(
                        applicationContext,
                        ".onAdFailedToLoad(statusCode:$statusCode, message:$message)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        btnShow.setOnClickListener {
            adWhaleMediationRewardAd.showAd { adWhaleMediationRewardItem ->
                Log.i(
                    ProgrammaticRewardAdMainActivity::class.java.simpleName,
                    ".onUserRewarded($adWhaleMediationRewardItem)"
                )
                Toast.makeText(
                    applicationContext,
                    ".onUserRewarded($adWhaleMediationRewardItem)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}