package kr.co.adwhale.sample.nativead

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import com.google.android.material.switchmaterial.SwitchMaterial
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.ADWHALE_NATIVE_TEMPLATE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.utils.AdWhaleLog
import kotlin.math.max

/**
 * 고정 템플릿 바인딩 방식 네이티브 광고 샘플 액티비티
 * 기존의 고정 템플릿을 사용하여 네이티브 광고를 표시합니다.
 */
class ProgrammaticTemplateBindingNativeMainActivity : AppCompatActivity() {
    private var etPlacementUid: EditText? = null
    private var btnAdRequest: Button? = null
    private var btnShowAd: Button? = null
    private var adViewRoot: RelativeLayout? = null
    private var switchLogger: SwitchMaterial? = null
    private var radioSmall: RadioButton? = null
    private var radioMedium: RadioButton? = null
    private var radioFullscreenDialog: RadioButton? = null
    private var adWhaleMediationNativeAdView: AdWhaleMediationNativeAdView? = null
    private var fullscreenDialog: Dialog? = null

    private var fullscreenAdView: AdWhaleMediationNativeAdView? = null

    private var isAdLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_template_binding_native_main)

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
        adViewRoot = findViewById<RelativeLayout>(R.id.adViewRoot)

        // 템플릿 선택 라디오 버튼들
        radioSmall = findViewById<RadioButton>(R.id.radioSmall)
        radioMedium = findViewById<RadioButton>(R.id.radioMedium)
        radioFullscreenDialog = findViewById<RadioButton>(R.id.radioFullscreenDialog)

        // 초기 상태 설정
        btnShowAd!!.setEnabled(false)
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
            if (etPlacementUid!!.getText().toString() != null && !etPlacementUid!!.getText()
                    .toString().isEmpty()
            ) {
                adWhaleMediationNativeAdView!!.setPlacementUid(
                    etPlacementUid!!.getText().toString()
                )
            }
            adWhaleMediationNativeAdView!!.setDebugEnabled(switchLogger!!.isChecked())

            // 선택된 템플릿에 따라 광고 로드 (표시하지 않음)
            if (radioSmall!!.isChecked()) {
                adWhaleMediationNativeAdView!!.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.SMALL)
            } else if (radioMedium!!.isChecked()) {
                adWhaleMediationNativeAdView!!.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.MEDIUM)
            } else if (radioFullscreenDialog!!.isChecked()) {
                // FULLSCREEN 템플릿을 다이얼로그에 로드
                loadFullscreenNativeAdDialog()
            }
        })

        btnShowAd!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (isAdLoaded) {
                if (radioFullscreenDialog!!.isChecked()) {
                    // FULLSCREEN 다이얼로그 표시
                    showFullscreenNativeAdDialog()
                } else {
                    // 일반 광고 표시
                    adWhaleMediationNativeAdView!!.show()
                    btnShowAd!!.setEnabled(false)
                }
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
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 로드 성공!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String?) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")")
                Toast.makeText(
                    getApplicationContext(),
                    "고정 템플릿 네이티브 광고 로드 실패: " + errorMessage,
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
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 클릭!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed()")
                Toast.makeText(getApplicationContext(), "고정 템플릿 네이티브 광고 닫힘", Toast.LENGTH_SHORT)
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

    /**
     * FULLSCREEN 템플릿을 다이얼로그에 로드
     */
    private fun loadFullscreenNativeAdDialog() {
        // FULLSCREEN 네이티브 광고 뷰 생성
        fullscreenAdView = AdWhaleMediationNativeAdView(this)

        // placementUid 설정
        val placementUid = etPlacementUid!!.getText().toString()
        if (placementUid != null && !placementUid.isEmpty()) {
            fullscreenAdView!!.setPlacementUid(placementUid)
        } else {
            // 기본값 설정
            fullscreenAdView!!.setPlacementUid(getResources().getString(R.string.native_placement_uid))
        }

        fullscreenAdView!!.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {
            override fun onNativeAdLoaded() {
                Log.i(LOG_TAG, ".onNativeAdLoaded() - Fullscreen Dialog")
                isAdLoaded = true
                btnShowAd!!.setEnabled(true)
                Toast.makeText(
                    getApplicationContext(),
                    "Fullscreen 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String?) {
                Log.e(
                    LOG_TAG,
                    ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ") - Fullscreen Dialog"
                )
                Toast.makeText(
                    getApplicationContext(),
                    "Fullscreen 네이티브 광고 로드 실패: " + errorMessage,
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
                Log.i(LOG_TAG, ".onNativeAdClicked() - Fullscreen Dialog")
                Toast.makeText(
                    getApplicationContext(),
                    "Fullscreen 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed() - Fullscreen Dialog")
                Toast.makeText(getApplicationContext(), "Fullscreen 네이티브 광고 닫힘", Toast.LENGTH_SHORT)
                    .show()
                // 광고가 닫힐 때 다이얼로그도 닫기
                if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
                    fullscreenDialog!!.dismiss()
                }
            }
        })

        // 광고 로드
        Log.d(LOG_TAG, "FULLSCREEN 광고 로드 시작")
        fullscreenAdView!!.setDebugEnabled(switchLogger!!.isChecked())
        fullscreenAdView!!.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.FULLSCREEN)
    }

    /**
     * FULLSCREEN 템플릿을 다이얼로그로 표시
     */
    private fun showFullscreenNativeAdDialog() {
        if (fullscreenAdView == null || !isAdLoaded) {
            Toast.makeText(this, "광고가 로드되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 간단한 다이얼로그 생성
        fullscreenDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        // 프로그래매틱으로 레이아웃 생성
        val mainLayout = LinearLayout(this)
        mainLayout.setOrientation(LinearLayout.VERTICAL)
        mainLayout.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        mainLayout.setBackgroundColor(-0x1) // 배경색을 흰색으로 설정

        // 헤더 레이아웃
        val headerLayout = LinearLayout(this)
        headerLayout.setOrientation(LinearLayout.HORIZONTAL)
        headerLayout.setGravity(Gravity.CENTER_VERTICAL)
        headerLayout.setPadding(16, 16, 16, 16)
        headerLayout.setBackgroundColor(-0x1000000)

        val titleText = TextView(this)
        titleText.setText("Fullscreen Native Ad")
        titleText.setTextColor(-0x1)
        titleText.setTextSize(18f)
        titleText.setTypeface(null, Typeface.BOLD)
        val titleParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        titleText.setLayoutParams(titleParams)

        val closeButton = Button(this)
        closeButton.setText("X")
        closeButton.setTextColor(-0x1)
        closeButton.setBackgroundColor(-0x1000000)
        closeButton.setOnClickListener(View.OnClickListener { v: View? ->
            if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
                fullscreenDialog!!.dismiss()
            }
        })

        headerLayout.addView(titleText)
        headerLayout.addView(closeButton)

        // 광고 영역 - MediaView 최소 크기 120x120dp 보장
        val adContainer = RelativeLayout(this)
        adContainer.setPadding(16, 16, 16, 16)
        adContainer.setBackgroundColor(-0xf0f10) // 회색 배경으로 광고 영역 표시

        // MediaView 최소 크기 보장을 위한 최소 높이 설정 (120dp + padding)
        val minHeightDp = 120 + 32 // 120dp + 16dp padding * 2
        val minHeightPx = (minHeightDp * getResources().getDisplayMetrics().density).toInt()

        val adParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        )
        adParams.height = max(minHeightPx, 0) // 최소 높이 보장
        adContainer.setLayoutParams(adParams)

        mainLayout.addView(headerLayout)
        mainLayout.addView(adContainer)

        fullscreenDialog!!.setContentView(mainLayout)

        fullscreenDialog!!.setOnDismissListener(DialogInterface.OnDismissListener { dialogInterface: DialogInterface? ->
            if (fullscreenAdView != null) {
                fullscreenAdView!!.destroy()
            }
        })

        // 다이얼로그가 표시된 후 광고 표시
        fullscreenDialog!!.setOnShowListener(OnShowListener { dialog: DialogInterface? ->
            // 이미 로드된 광고를 표시
            fullscreenAdView!!.show()
            adContainer.addView(fullscreenAdView)
        })

        // 다이얼로그 표시
        fullscreenDialog!!.show()
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
        if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
            fullscreenDialog!!.dismiss()
        }
    }

    companion object {
        private val LOG_TAG: String =
            ProgrammaticTemplateBindingNativeMainActivity::class.java.getSimpleName()
    }
}