package kr.co.adwhale.sample.banner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import kr.co.adwhale.sample.R
import kr.co.adwhale.sample.databinding.ActivityProgrammaticBannerMainBinding
import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds

class ProgrammaticBannerMainActivity : AppCompatActivity() {
    private var mBinding: ActivityProgrammaticBannerMainBinding? = null
    private val binding get() = mBinding!!

    private lateinit var bannerRoot : RelativeLayout
    private lateinit var btnTest : Button
    private lateinit var rgBannerAdSize : RadioGroup
    private lateinit var etPlacementUid : EditText
    private lateinit var selectedAdWhaleAdSize : ADWHALE_AD_SIZE
    private lateinit var adWhaleMediationAdView : AdWhaleMediationAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgrammaticBannerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnTest = binding.btnTest
        rgBannerAdSize = binding.rgBannerAdSize
        etPlacementUid = binding.etPlacementUid
        bannerRoot = binding.bannerRoot

        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                ProgrammaticBannerMainActivity::class.simpleName,
                "AdWhaleMediationOnInitCompleteListener.onInitComplete($statusCode, $message)"
            )
        }

        adWhaleMediationAdView = AdWhaleMediationAdView(this)
        val params = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        bannerRoot.addView(adWhaleMediationAdView, params)

        adWhaleMediationAdView.adWhaleMediationAdViewListener =
            object : AdWhaleMediationAdViewListener {

                override fun onAdLoaded() {
                    Log.i(ProgrammaticBannerMainActivity::class.simpleName, ".onAdLoaded()")
                }

                override fun onAdLoadFailed(statusCode: Int, message: String?) {
                    Log.i(ProgrammaticBannerMainActivity::class.simpleName, ".onAdLoadFailed($statusCode, $message)")
                }

                override fun onAdClicked() {
                    Log.i(ProgrammaticBannerMainActivity::class.simpleName, ".onAdClicked()")
                }
            }

        rgBannerAdSize.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdBanner320x50 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x50
                    etPlacementUid.setText(getString(R.string.banner32050_placementUid))
                }

                R.id.rdBanner320x100 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER320x100
                    etPlacementUid.setText(getString(R.string.banner320100_placementUid))
                }

                R.id.rdBanner300x250 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER300x250
                    etPlacementUid.setText(getString(R.string.banner300250_placementUid))
                }

                R.id.rdBanner250x250 -> {
                    selectedAdWhaleAdSize = ADWHALE_AD_SIZE.BANNER250x250
                    etPlacementUid.setText(getString(R.string.banner250250_placementUid))
                }

                else -> {
                    etPlacementUid.setText("")
                }
            }
        }

        btnTest.setOnClickListener {
            adWhaleMediationAdView.placementUid = etPlacementUid.text.toString()
            adWhaleMediationAdView.adwhaleAdSize = selectedAdWhaleAdSize
            adWhaleMediationAdView.loadAd()
        }
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