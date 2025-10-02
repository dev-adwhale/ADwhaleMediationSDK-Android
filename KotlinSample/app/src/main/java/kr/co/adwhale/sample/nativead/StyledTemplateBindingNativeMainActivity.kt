package kr.co.adwhale.sample.nativead

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.ADWHALE_NATIVE_TEMPLATE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeTemplateStyle
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener

class StyledTemplateBindingNativeMainActivity : AppCompatActivity() {
    private var etPlacementUid: EditText? = null
    private var btnAdRequest: Button? = null
    private var btnShowAd: Button? = null
    private var adViewRoot: RelativeLayout? = null
    private var switchLogger: Switch? = null
    private var radioSmall: RadioButton? = null
    private var radioMedium: RadioButton? = null
    private var radioFullscreen: RadioButton? = null
    private var radioDefault: RadioButton? = null
    private var radioDark: RadioButton? = null
    private var radioBrand: RadioButton? = null
    private var radioMinimal: RadioButton? = null
    private var radioFullStyle: RadioButton? = null
    private var radioCustomFont: RadioButton? = null
    private var adWhaleMediationNativeAdView: AdWhaleMediationNativeAdView? = null
    private var fullscreenDialog: Dialog? = null

    private var fullscreenAdView: AdWhaleMediationNativeAdView? = null

    private var isAdLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_styled_template_binding_native_main)

        // 1. Init
        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                Log.i(LOG_TAG, ".onInitComplete(" + statusCode + ", " + message + ")")
            }
        })

        initViews()
        createNativeAd()
        setListener()
    }

    /**
     * 뷰 초기화
     */
    private fun initViews() {
        etPlacementUid = findViewById<EditText>(R.id.etPlacementUid)
        etPlacementUid!!.setText(getResources().getString(R.string.native_placement_uid))
        btnAdRequest = findViewById<Button>(R.id.btnAdRequest)
        btnShowAd = findViewById<Button>(R.id.btnShowAd)
        adViewRoot = findViewById<RelativeLayout>(R.id.adViewRoot)
        switchLogger = findViewById<Switch>(R.id.switchLogger)

        // 템플릿 크기 선택 라디오 버튼들
        radioSmall = findViewById<RadioButton>(R.id.radioSmall)
        radioMedium = findViewById<RadioButton>(R.id.radioMedium)
        radioFullscreen = findViewById<RadioButton>(R.id.radioFullscreen)

        // 스타일 테마 선택 라디오 버튼들
        radioDefault = findViewById<RadioButton>(R.id.radioDefault)
        radioDark = findViewById<RadioButton>(R.id.radioDark)
        radioBrand = findViewById<RadioButton>(R.id.radioBrand)
        radioMinimal = findViewById<RadioButton>(R.id.radioMinimal)
        radioFullStyle = findViewById<RadioButton>(R.id.radioFullStyle)
        radioCustomFont = findViewById<RadioButton>(R.id.radioCustomFont)

        // 초기 상태 설정
        btnShowAd!!.setEnabled(false)
    }

    /**
     * 리스너 이벤트 등록
     */
    private fun setListener() {
        btnAdRequest!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (etPlacementUid!!.getText().toString() != null && !etPlacementUid!!.getText()
                    .toString().isEmpty()
            ) {
                adWhaleMediationNativeAdView!!.setPlacementUid(
                    etPlacementUid!!.getText().toString()
                )
            }
            adWhaleMediationNativeAdView!!.setDebugEnabled(switchLogger!!.isChecked())

            // 선택된 스타일 적용
            val style = this.selectedStyle
            adWhaleMediationNativeAdView!!.setTemplateStyle(style)

            // 선택된 템플릿에 따라 광고 로드 (표시하지 않음)
            if (radioSmall!!.isChecked()) {
                adWhaleMediationNativeAdView!!.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.SMALL)
            } else if (radioMedium!!.isChecked()) {
                adWhaleMediationNativeAdView!!.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.MEDIUM)
            } else if (radioFullscreen!!.isChecked()) {
                loadFullscreenNativeAdDialog()
            }
        })

        btnShowAd!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (isAdLoaded) {
                if (radioFullscreen!!.isChecked()) {
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

    private val selectedStyle: AdWhaleMediationNativeTemplateStyle?
        /**
         * 선택된 스타일 반환
         */
        get() {
            if (radioDefault!!.isChecked()) {
                return createDefaultStyle()
            } else if (radioDark!!.isChecked()) {
                return createDarkStyle()
            } else if (radioBrand!!.isChecked()) {
                return createBrandStyle()
            } else if (radioMinimal!!.isChecked()) {
                return createMinimalStyle()
            } else if (radioFullStyle!!.isChecked()) {
                return createFullStyle()
            } else if (radioCustomFont!!.isChecked()) {
                return createCustomFontStyle()
            }
            return null // 기본 스타일
        }

    /**
     * 기본 스타일 (스타일 없음)
     */
    private fun createDefaultStyle(): AdWhaleMediationNativeTemplateStyle? {
        return null // 기본 스타일 사용
    }

    /**
     * 다크 테마 스타일
     */
    private fun createDarkStyle(): AdWhaleMediationNativeTemplateStyle? {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#2C2C2C")))
            .withPrimaryTextTypefaceColor(Color.WHITE)
            .withSecondaryTextTypefaceColor(Color.parseColor("#CCCCCC"))
            .withTertiaryTextTypefaceColor(Color.parseColor("#AAAAAA"))
            .withCallToActionTypefaceColor(Color.WHITE)
            .withCallToActionBackgroundColor(ColorDrawable(Color.parseColor("#4CAF50")))
            .build()
    }

    /**
     * 브랜드 테마 스타일
     */
    private fun createBrandStyle(): AdWhaleMediationNativeTemplateStyle? {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#E3F2FD")))
            .withPrimaryTextTypefaceColor(Color.parseColor("#1976D2"))
            .withSecondaryTextTypefaceColor(Color.parseColor("#1565C0"))
            .withTertiaryTextTypefaceColor(Color.parseColor("#424242"))
            .withCallToActionTypefaceColor(Color.WHITE)
            .withCallToActionBackgroundColor(ColorDrawable(Color.parseColor("#FF5722")))
            .withPrimaryTextSize(18f)
            .withCallToActionTextSize(16f)
            .build()
    }

    /**
     * 미니멀 스타일
     */
    private fun createMinimalStyle(): AdWhaleMediationNativeTemplateStyle? {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.WHITE))
            .withPrimaryTextTypefaceColor(Color.parseColor("#333333"))
            .withSecondaryTextTypefaceColor(Color.parseColor("#666666"))
            .withTertiaryTextTypefaceColor(Color.parseColor("#999999"))
            .withCallToActionTypefaceColor(Color.parseColor("#2196F3"))
            .withCallToActionBackgroundColor(ColorDrawable(Color.TRANSPARENT))
            .build()
    }

    /**
     * 풀 스타일 (모든 요소 스타일링)
     */
    private fun createFullStyle(): AdWhaleMediationNativeTemplateStyle? {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#F5F5F5")))
            .withPrimaryTextTypefaceColor(Color.parseColor("#1A1A1A"))
            .withPrimaryTextSize(20f)
            .withPrimaryTextBackgroundColor(ColorDrawable(Color.parseColor("#E8F5E8")))
            .withSecondaryTextTypefaceColor(Color.parseColor("#2E7D32"))
            .withSecondaryTextSize(16f)
            .withSecondaryTextBackgroundColor(ColorDrawable(Color.parseColor("#F1F8E9")))
            .withTertiaryTextTypefaceColor(Color.parseColor("#424242"))
            .withTertiaryTextSize(14f)
            .withTertiaryTextBackgroundColor(ColorDrawable(Color.parseColor("#FAFAFA")))
            .withCallToActionTypefaceColor(Color.WHITE)
            .withCallToActionTextSize(18f)
            .withCallToActionBackgroundColor(ColorDrawable(Color.parseColor("#FF6B35")))
            .build()
    }

    /**
     * 커스텀 폰트 스타일
     */
    private fun createCustomFontStyle(): AdWhaleMediationNativeTemplateStyle? {
        val boldTypeface = Typeface.DEFAULT_BOLD
        val italicTypeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#FFF8E1")))
            .withPrimaryTextTypeface(boldTypeface)
            .withPrimaryTextTypefaceColor(Color.parseColor("#E65100"))
            .withPrimaryTextSize(22f)
            .withSecondaryTextTypeface(italicTypeface)
            .withSecondaryTextTypefaceColor(Color.parseColor("#F57C00"))
            .withSecondaryTextSize(16f)
            .withTertiaryTextTypefaceColor(Color.parseColor("#FF8F00"))
            .withTertiaryTextSize(14f)
            .withCallToActionTextTypeface(boldTypeface)
            .withCallToActionTypefaceColor(Color.WHITE)
            .withCallToActionTextSize(16f)
            .withCallToActionBackgroundColor(ColorDrawable(Color.parseColor("#FF9800")))
            .build()
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
                    "스타일이 적용된 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String?) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")")
                Toast.makeText(
                    getApplicationContext(),
                    "네이티브 광고 로드 실패: " + errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdShowFailed(errorCode: Int, errorMessage: String?) {
                Log.e(LOG_TAG, ".onNativeAdFailedToLoad(" + errorCode + ", " + errorMessage + ")")
                Toast.makeText(
                    getApplicationContext(),
                    ".onNativeAdFailedToLoad(statusCode:" + errorCode + ", message:" + errorMessage + ")",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClicked() {
                Log.i(LOG_TAG, ".onNativeAdClicked()")
                Toast.makeText(getApplicationContext(), "네이티브 광고 클릭!", Toast.LENGTH_SHORT).show()
            }

            override fun onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed()")
                Toast.makeText(getApplicationContext(), "네이티브 광고 닫힘", Toast.LENGTH_SHORT).show()
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

        val placementUid = etPlacementUid!!.getText().toString()
        if (placementUid != null && !placementUid.isEmpty()) {
            fullscreenAdView!!.setPlacementUid(placementUid)
        } else {
            fullscreenAdView!!.setPlacementUid(getResources().getString(R.string.native_placement_uid))
        }

        // 선택된 스타일 적용
        val style = this.selectedStyle
        fullscreenAdView!!.setTemplateStyle(style)

        fullscreenAdView!!.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {
            override fun onNativeAdLoaded() {
                Log.i(LOG_TAG, ".onNativeAdLoaded() - Fullscreen Dialog")
                isAdLoaded = true
                btnShowAd!!.setEnabled(true)
                Toast.makeText(
                    getApplicationContext(),
                    "FULLSCREEN 스타일 네이티브 광고 로드 성공!",
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
                    "FULLSCREEN 네이티브 광고 로드 실패: " + errorMessage,
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
                    "FULLSCREEN 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(LOG_TAG, ".onNativeAdClosed() - Fullscreen Dialog")
                Toast.makeText(getApplicationContext(), "FULLSCREEN 네이티브 광고 닫힘", Toast.LENGTH_SHORT)
                    .show()
                if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
                    fullscreenDialog!!.dismiss()
                }
            }
        })

        // 광고 로드
        Log.d(LOG_TAG, "FULLSCREEN 스타일 광고 로드 시작")
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

        fullscreenDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val mainLayout = LinearLayout(this)
        mainLayout.setOrientation(LinearLayout.VERTICAL)
        mainLayout.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        mainLayout.setBackgroundColor(-0x1)

        // 헤더 레이아웃
        val headerLayout = LinearLayout(this)
        headerLayout.setOrientation(LinearLayout.HORIZONTAL)
        headerLayout.setPadding(16, 16, 16, 16)
        headerLayout.setBackgroundColor(-0xde690d)

        val titleText = TextView(this)
        titleText.setText("FULLSCREEN 네이티브 광고 (스타일 적용)")
        titleText.setTextColor(Color.WHITE)
        titleText.setTextSize(18f)
        titleText.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val closeButton = Button(this)
        closeButton.setText("닫기")
        closeButton.setTextColor(Color.WHITE)
        closeButton.setBackgroundColor(-0xe6892e)
        closeButton.setOnClickListener(View.OnClickListener { v: View? ->
            if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
                fullscreenDialog!!.dismiss()
            }
        })

        headerLayout.addView(titleText)
        headerLayout.addView(closeButton)

        // 광고 컨테이너
        val adContainer = RelativeLayout(this)
        val adParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        )
        adContainer.setLayoutParams(adParams)
        adContainer.setPadding(16, 16, 16, 16)
        adContainer.setBackgroundColor(-0xf0f10)

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
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adWhaleMediationNativeAdView != null) {
            adWhaleMediationNativeAdView!!.destroy()
        }
        if (fullscreenDialog != null && fullscreenDialog!!.isShowing()) {
            fullscreenDialog!!.dismiss()
        }
    }

    companion object {
        private val LOG_TAG: String =
            StyledTemplateBindingNativeMainActivity::class.java.getSimpleName()
    }
}
