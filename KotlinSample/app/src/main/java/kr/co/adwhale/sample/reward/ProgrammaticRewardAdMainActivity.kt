package kr.co.adwhale.sample.reward

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds

class ProgrammaticRewardAdMainActivity : AppCompatActivity() {
    private lateinit var btnTest: Button

    private lateinit var btnShow: Button

    private lateinit var etPlacementUid: EditText

    private lateinit var adWhaleMediationRewardAd: AdWhaleMediationRewardAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_reward_ad_main)
        btnTest = findViewById(R.id.btnTest)
        btnShow = findViewById<Button>(R.id.btnShow)
        etPlacementUid = findViewById(R.id.etPlacementUid)

        AdWhaleMediationAds.init(
            this
        ) { statusCode, message ->
            Log.i(
                ProgrammaticRewardAdMainActivity::class.java.simpleName,
                ".onInitComplete($statusCode, $message)"
            )
        }

        adWhaleMediationRewardAd = AdWhaleMediationRewardAd(etPlacementUid.getText().toString())

        adWhaleMediationRewardAd.setAdWhaleMediationFullScreenContentCallback(object :
            AdWhaleMediationFullScreenContentCallback() {
            fun onAdClicked() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdClicked()")
            }

            fun onAdDismissed() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdDismissed()")
                Toast.makeText(applicationContext, ".onAdDismissed()", Toast.LENGTH_SHORT).show()
            }

            fun onFailedToShow(statusCode: Int, message: String) {
                Log.i(
                    ProgrammaticRewardAdMainActivity::class.java.simpleName,
                    ".onFailedToShow($statusCode, $message)"
                )
                Toast.makeText(
                    applicationContext,
                    ".onFailedToShow(statusCode:$statusCode, message:$message)", Toast.LENGTH_SHORT
                ).show()
            }

            fun onAdShowed() {
                Log.i(ProgrammaticRewardAdMainActivity::class.java.simpleName, ".onAdShowed()")
                Toast.makeText(applicationContext, ".onAdShowed()", Toast.LENGTH_SHORT).show()
            }
        })

        btnTest.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationRewardAd.loadAd(object : AdWhaleMediationRewardedAdLoadCallback() {
                fun onAdLoaded(
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

                fun onAdFailedToLoad(statusCode: Int, message: String) {
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
        })

        btnShow.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationRewardAd.showAd { adWhaleMediationRewardItem ->
                Log.i(
                    ProgrammaticRewardAdMainActivity::class.java.simpleName,
                    ".onUserRewarded(" + adWhaleMediationRewardItem.toString() + ")"
                )
                Toast.makeText(
                    applicationContext,
                    ".onUserRewarded(" + adWhaleMediationRewardItem.toString() + ")",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}