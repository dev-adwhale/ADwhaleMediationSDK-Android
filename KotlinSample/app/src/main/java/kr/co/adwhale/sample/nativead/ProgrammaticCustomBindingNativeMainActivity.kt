package kr.co.adwhale.sample.nativead

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import com.google.android.material.switchmaterial.SwitchMaterial
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.mediation.ads.AdWhaleNativeAdBinder
import net.adwhale.sdk.utils.AdWhaleLog

/**
 * 커스텀 바인딩 방식 네이티브 광고 샘플 액티비티
 * DARO 스타일의 바인딩 API를 사용하여 네이티브 광고를 표시합니다.
 */
class ProgrammaticCustomBindingNativeMainActivity : AppCompatActivity() {
    private var etPlacementUid: EditText? = null
    private var btnAdRequest: Button? = null
    private var btnShowAd: Button? = null
    private var adViewRoot: RelativeLayout? = null
    private var switchLogger: SwitchMaterial? = null
    private var adWhaleMediationNativeAdView: AdWhaleMediationNativeAdView? = null
    private var isAdLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_custom_binding_native_main)

        bindView()
        setListener()

        // 1. Init
        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")")
            }
        })

        // 2. nativead
        createNativeAd()
    }

    /**
     * 뷰 바인딩
     */
    private fun bindView() {
        switchLogger = findViewById<SwitchMaterial>(R.id.switch_logger)
        etPlacementUid = findViewById<EditText>(R.id.etPlacementUid)
        etPlacementUid!!.setText(getResources().getString(R.string.native_placement_uid))
        btnAdRequest = findViewById<Button>(R.id.btnAdRequest)
        btnShowAd = findViewById<Button>(R.id.btnShowAd)
        btnShowAd!!.setEnabled(false)
        adViewRoot = findViewById<RelativeLayout>(R.id.adViewRoot)
    }

    /**
     * 리스너 이벤트 등록
     */
    private fun setListener() {
        switchLogger!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                Log.d(MotionEffect.TAG, "로거가 활성화되었습니다.")
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
            } else {
                Log.d(MotionEffect.TAG, "로거가 비활성화되었습니다.")
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)
            }
        })

        btnAdRequest!!.setOnClickListener(View.OnClickListener { view: View? ->
            adWhaleMediationNativeAdView!!.setPlacementUid(etPlacementUid!!.getText().toString())
            // custom native AdBinder 생성
            val adWhaleNativeAdBinder =
                AdWhaleNativeAdBinder.Builder(this, R.layout.custom_native_ad_layout)
                    .setIconViewId(R.id.view_icon)
                    .setTitleViewId(R.id.view_title)
                    .setBodyTextViewId(R.id.view_body)
                    .setCallToActionViewId(R.id.button_cta)
                    .setMediaViewGroupId(R.id.view_media)
                    .build()

            // 커스텀 바인딩으로 광고 로드
            adWhaleMediationNativeAdView!!.setDebugEnabled(true)
            adWhaleMediationNativeAdView!!.loadAdWithBinder(adWhaleNativeAdBinder)
        })

        btnShowAd!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (isAdLoaded) {
                adWhaleMediationNativeAdView!!.show()
                btnShowAd!!.setEnabled(false)
            }
        })
    }

    private fun createNativeAd() {
        adWhaleMediationNativeAdView = AdWhaleMediationNativeAdView(this)
        adWhaleMediationNativeAdView!!.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {
            override fun onNativeAdLoaded() {
                Log.i(LOG_TAG, ".onNativeAdLoaded()")
                isAdLoaded = true
                btnShowAd!!.setEnabled(true)
                Toast.makeText(
                    getApplicationContext(),
                    "커스텀 바인딩 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String?) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")")
                Toast.makeText(
                    getApplicationContext(),
                    "커스텀 바인딩 네이티브 광고 로드 실패: " + errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdShowFailed(errorCode: Int, errorMessage: String?) {
                Log.e(LOG_TAG, ".onNativeAdShowFailed(" + errorCode + ", " + errorMessage + ")")
                Toast.makeText(
                    getApplicationContext(),
                    ".onNativeAdShowFailed(statusCode:" + errorCode + ", message:" + errorMessage + ")",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClicked() {
                Log.i(LOG_TAG, ".onNativeAdClicked()")
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 클릭!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed()")
                Toast.makeText(getApplicationContext(), "커스텀 바인딩 네이티브 광고 닫힘", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        //root 에 add
        val params = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        adViewRoot!!.addView(adWhaleMediationNativeAdView, params)
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOG_TAG, ".onResume()")
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(LOG_TAG, ".onPause()")
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, ".onDestroy()")
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.destroy()
        }
    }

    companion object {
        private val LOG_TAG: String =
            ProgrammaticCustomBindingNativeMainActivity::class.java.getSimpleName()
    }
}