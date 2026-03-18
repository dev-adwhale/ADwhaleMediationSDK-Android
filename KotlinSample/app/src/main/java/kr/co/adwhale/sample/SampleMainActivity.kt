package kr.co.adwhale.sample

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.appopen.ProgrammaticAppOpenMainActivity
import kr.co.adwhale.sample.banner.ProgrammaticBannerMainActivity
import kr.co.adwhale.sample.banner.XmlBannerMainActivity
import kr.co.adwhale.sample.databinding.ActivitySampleMainBinding
import kr.co.adwhale.sample.interstitial.ProgrammaticInterstitialMainActivity
import kr.co.adwhale.sample.nativead.ProgrammaticCustomBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.ProgrammaticTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.StyledTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.reward.ProgrammaticRewardAdMainActivity
import kr.co.adwhale.sample.transition.TransitionTestMainActivity
import net.adwhale.sdk.mediation.ads.ADWHALE_POPUP_AD_CLOSE_REASON
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAdListener
import net.adwhale.sdk.utils.AdWhaleLog


class SampleMainActivity : AppCompatActivity() {
    private var mBinding: ActivitySampleMainBinding? = null
    private val binding get() = mBinding!!

    private val LOG_TAG: String = SampleMainActivity::class.java.getSimpleName()

    private var adWhaleMediationExitPopupAd: AdWhaleMediationExitPopupAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySampleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Error)

        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                LOG_TAG,
                ".onInitComplete(" + statusCode + ", " + message + ")"
            )
            loadExitPopupAd()
        }

        val etMediaUid = binding.etMediaUid
        val btnProgrammaticBanner = binding.btnProgrammaticBanner
        val btnTransitionAd = binding.btnMoveToTransitionAd
        val btnXmlBanner = binding.btnXmlBanner
        val btnInterstitial = binding.btnInterstitial
        val btnRewardAd = binding.btnRewardAd
        val btnAppOpenAd = binding.btnAppOpenAd
        val btnNativeAdCustomBinding = binding.btnNativeCustomBinding
        val btnNativeAdTemplateBinding = binding.btnNativeTemplateBinding
        val btnNativeAdTemplateBindingWithStyle = binding.btnNativeTemplateBindingWithStyle

        etMediaUid.setText(getMetaData())
        btnProgrammaticBanner.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticBannerMainActivity::class.java))
        }

        btnTransitionAd.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, TransitionTestMainActivity::class.java))
        }
        btnXmlBanner.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, XmlBannerMainActivity::class.java))
        }

        btnInterstitial.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticInterstitialMainActivity::class.java))
        }

        btnRewardAd.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticRewardAdMainActivity::class.java))
        }

        btnAppOpenAd.setOnClickListener({ view ->
            setMetaData(etMediaUid.getText().toString())
            val intent = Intent(this, ProgrammaticAppOpenMainActivity::class.java)
            startActivity(intent)
        })

        btnNativeAdCustomBinding.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticCustomBindingNativeMainActivity::class.java))
        }

        btnNativeAdTemplateBinding.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticTemplateBindingNativeMainActivity::class.java))
        }

        btnNativeAdTemplateBindingWithStyle.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, StyledTemplateBindingNativeMainActivity::class.java))
        }
    }


    private fun setMetaData(value: String?) {
        if (value.isNullOrEmpty()) {
            return
        }
        try {
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 세팅.
            val applicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
            bundle.putString("net.adwhale.sdk.mediation.PUBLISHER_UID", value)
        } catch (e: PackageManager.NameNotFoundException) {
            throw UnsupportedOperationException("Publisher Uid value is required. Please check <meta-data> in AndroidManifest.xml.")
        }
    }

    private fun getMetaData(): String? {
        return try {
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 가져온다.
            val applicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
            bundle["net.adwhale.sdk.mediation.PUBLISHER_UID"].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    private fun loadExitPopupAd() {
        adWhaleMediationExitPopupAd =
            AdWhaleMediationExitPopupAd(getResources().getString(kr.co.adwhale.sample.R.string.exit_popup_placement_uid))
        adWhaleMediationExitPopupAd?.setCustomizeButtonText("테스트 취소", "테스트 종료")
        adWhaleMediationExitPopupAd?.setCustomDescription("테스트 문구")
        adWhaleMediationExitPopupAd?.setAdWhaleMediationExitPopupAdListener(object :
            AdWhaleMediationExitPopupAdListener {
            override fun onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded")
            }

            override fun onAdLoadFailed(statusCode: Int, message: String?) {
                Log.d(
                    LOG_TAG,
                    "onAdLoadFailed(" + statusCode + ", " + message + ");"
                )
            }

            override fun onAdShowed() {
                Log.d(LOG_TAG, "onAdShowed")
            }

            override fun onAdClicked() {
                Log.d(LOG_TAG, "onAdClicked")
            }

            override fun onAdShowFailed(statusCode: Int, message: String?) {
                Log.d(
                    LOG_TAG,
                    "onAdShowFailed(" + statusCode + ", " + message + ");"
                )
            }

            override fun onAdClosed(adwhaleExitPopupAdCloseReason: ADWHALE_POPUP_AD_CLOSE_REASON) {
                Log.d(
                    LOG_TAG,
                    "onAdClosed(" + adwhaleExitPopupAdCloseReason.getCloseReasonTypeString() + ");"
                )
            }
        })
        adWhaleMediationExitPopupAd?.loadAd()
    }

    override fun onResume() {
        adWhaleMediationExitPopupAd?.resume(this) // 필수 호출

        super.onResume()
    }

    // Back Key가 눌러졌을 때, CloseAd 호출
    public override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitPopupAd()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showExitPopupAd() {
        adWhaleMediationExitPopupAd?.showAd(
            this@SampleMainActivity,
            this@SampleMainActivity.getSupportFragmentManager()
        )
    }

    private fun showDefaultClosePopup() {
        AlertDialog.Builder(this).setTitle("").setMessage("종료 하시겠습니까?")
            .setPositiveButton("예", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
            .setNegativeButton("아니요", null)
            .show()
    }

    override fun onDestroy() {
        mBinding = null
        Log.i(LOG_TAG, ".onDestroy()")
        adWhaleMediationExitPopupAd?.destroy()
        super.onDestroy()
    }
}