package kr.co.adwhale.sample.nativead

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.adwhale.sdk.mediation.ads.ADWHALE_NATIVE_TEMPLATE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeTemplateStyle
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeStyledTemplateBindingNativeMainActivity : ComponentActivity() {

    private val TAG = ComposeStyledTemplateBindingNativeMainActivity::class.simpleName

    private var nativeAdView: AdWhaleMediationNativeAdView? = null
    private var fullscreenAdView: AdWhaleMediationNativeAdView? = null
    private var fullscreenDialog: Dialog? = null

    private lateinit var isAdLoadedState: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdWhale 초기화
        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                Log.i(TAG, ".onInitComplete($statusCode, $message)")
            }
        })

        val defaultPlacementUid = getString(R.string.native_placement_uid)

        setContent {
            MaterialTheme {
                var placementUid by remember { mutableStateOf(defaultPlacementUid) }
                var selectedTemplate by remember { mutableStateOf(StyledTemplateOption.SMALL) }
                var selectedStyle by remember { mutableStateOf(StyledThemeOption.DEFAULT) }
                val isAdLoaded = remember { mutableStateOf(false) }
                isAdLoadedState = isAdLoaded

                StyledTemplateNativeScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
                    selectedTemplate = selectedTemplate,
                    onTemplateChange = { selectedTemplate = it },
                    selectedStyle = selectedStyle,
                    onStyleChange = { selectedStyle = it },
                    onLoggerChange = { enabled ->
                        if (enabled) {
                            Log.d(TAG, "로거가 활성화되었습니다.")
                            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
                        } else {
                            Log.d(TAG, "로거가 비활성화되었습니다.")
                            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)
                        }
                    },
                    isAdLoaded = isAdLoaded.value,
                    onAdRequest = { template, style ->
                        requestStyledNative(placementUid, template, style)
                    },
                    onShowAd = { template ->
                        showStyledNative(template)
                    },
                    adViewContainer = {
                        StyledTemplateAdContainer()
                    }
                )
            }
        }
    }

    /**
     * 스타일 + 템플릿 네이티브 광고 로드
     */
    private fun requestStyledNative(
        placementUid: String,
        template: StyledTemplateOption,
        styleOption: StyledThemeOption
    ) {
        if (placementUid.isBlank()) {
            Toast.makeText(
                applicationContext,
                "placementUid 를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (template) {
            StyledTemplateOption.SMALL,
            StyledTemplateOption.MEDIUM -> {
                val adView = nativeAdView
                if (adView == null) {
                    Toast.makeText(
                        applicationContext,
                        "네이티브 광고 View가 아직 초기화되지 않았습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                adView.setPlacementUid(placementUid)
                val style = getSelectedStyle(styleOption)
                adView.setTemplateStyle(style)

                val tpl = when (template) {
                    StyledTemplateOption.SMALL -> ADWHALE_NATIVE_TEMPLATE.SMALL
                    StyledTemplateOption.MEDIUM -> ADWHALE_NATIVE_TEMPLATE.MEDIUM
                    else -> ADWHALE_NATIVE_TEMPLATE.SMALL
                }
                adView.loadAdWithTemplate(tpl)
            }

            StyledTemplateOption.FULLSCREEN -> {
                loadFullscreenStyledNativeDialog(placementUid, styleOption)
            }
        }
    }

    /**
     * 스타일 + 템플릿 네이티브 광고 표시
     */
    private fun showStyledNative(template: StyledTemplateOption) {
        when (template) {
            StyledTemplateOption.SMALL,
            StyledTemplateOption.MEDIUM -> {
                val adView = nativeAdView
                if (adView == null || !isAdLoadedState.value) {
                    Toast.makeText(
                        applicationContext,
                        "광고가 로드되지 않았습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                adView.show()
                isAdLoadedState.value = false
            }

            StyledTemplateOption.FULLSCREEN -> {
                showFullscreenStyledNativeDialog()
            }
        }
    }

    /**
     * Compose 안에 인라인 Styled 템플릿 네이티브 광고 View를 표시하는 컨테이너
     */
    @Composable
    private fun StyledTemplateAdContainer() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            AndroidView(
                factory = { context ->
                    val view = nativeAdView ?: AdWhaleMediationNativeAdView(context).also {
                        setupInlineStyledListener(it)
                        nativeAdView = it
                    }

                    val params = RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        addRule(RelativeLayout.CENTER_HORIZONTAL)
                    }
                    view.layoutParams = params
                    view
                }
            )
        }
    }

    /**
     * 인라인 스타일 템플릿 네이티브 광고 리스너
     */
    private fun setupInlineStyledListener(adView: AdWhaleMediationNativeAdView) {
        adView.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {

            override fun onNativeAdLoaded() {
                Log.i(TAG, ".onNativeAdLoaded()")
                isAdLoadedState.value = true
                Toast.makeText(
                    applicationContext,
                    "스타일이 적용된 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdFailedToLoad($errorCode, $errorMessage)")
                isAdLoadedState.value = false
                Toast.makeText(
                    applicationContext,
                    "네이티브 광고 로드 실패: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdShowFailed(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdShowFailed($errorCode, $errorMessage)")
                Toast.makeText(
                    applicationContext,
                    ".onNativeAdShowFailed(statusCode:$errorCode, message:$errorMessage)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClicked() {
                Log.i(TAG, ".onNativeAdClicked()")
                Toast.makeText(
                    applicationContext,
                    "네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(TAG, ".onNativeAdClosed()")
                Toast.makeText(
                    applicationContext,
                    "네이티브 광고 닫힘",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * FULLSCREEN 스타일 템플릿 로드 (다이얼로그용)
     */
    private fun loadFullscreenStyledNativeDialog(
        placementUid: String,
        styleOption: StyledThemeOption
    ) {
        val view = AdWhaleMediationNativeAdView(this)
        fullscreenAdView = view

        val uid = if (placementUid.isNotBlank()) {
            placementUid
        } else {
            getString(R.string.native_placement_uid)
        }
        view.setPlacementUid(uid)

        val style = getSelectedStyle(styleOption)
        view.setTemplateStyle(style)

        view.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {

            override fun onNativeAdLoaded() {
                Log.i(TAG, ".onNativeAdLoaded() - Fullscreen Dialog")
                isAdLoadedState.value = true
                Toast.makeText(
                    applicationContext,
                    "FULLSCREEN 스타일 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdFailedToLoad($errorCode, $errorMessage) - Fullscreen Dialog")
                isAdLoadedState.value = false
                Toast.makeText(
                    applicationContext,
                    "FULLSCREEN 네이티브 광고 로드 실패: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdShowFailed(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdShowFailed($errorCode, $errorMessage)")
                Toast.makeText(
                    applicationContext,
                    ".onNativeAdShowFailed(statusCode:$errorCode, message:$errorMessage)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClicked() {
                Log.i(TAG, ".onNativeAdClicked() - Fullscreen Dialog")
                Toast.makeText(
                    applicationContext,
                    "FULLSCREEN 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(TAG, ".onNativeAdClosed() - Fullscreen Dialog")
                Toast.makeText(
                    applicationContext,
                    "FULLSCREEN 네이티브 광고 닫힘",
                    Toast.LENGTH_SHORT
                ).show()
                if (fullscreenDialog != null && fullscreenDialog!!.isShowing) {
                    fullscreenDialog!!.dismiss()
                }
            }
        })

        Log.d(TAG, "FULLSCREEN 스타일 광고 로드 시작")
        view.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.FULLSCREEN)
    }

    /**
     * FULLSCREEN 스타일 템플릿을 다이얼로그로 표시
     */
    private fun showFullscreenStyledNativeDialog() {
        val view = fullscreenAdView
        if (view == null || !isAdLoadedState.value) {
            Toast.makeText(this, "광고가 로드되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        fullscreenDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFFFFFFFF.toInt())
        }

        // 헤더
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFF2196F3.toInt())
        }

        val titleText = TextView(this).apply {
            text = "FULLSCREEN 네이티브 광고 (스타일 적용)"
            setTextColor(Color.WHITE)
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val closeButton = Button(this).apply {
            text = "닫기"
            setTextColor(Color.WHITE)
            setBackgroundColor(0xFF1976D2.toInt())
            setOnClickListener {
                fullscreenDialog?.dismiss()
            }
        }

        headerLayout.addView(titleText)
        headerLayout.addView(closeButton)

        // 광고 컨테이너
        val adContainer = RelativeLayout(this).apply {
            val adParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutParams = adParams
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFF0F0F0.toInt())
        }

        mainLayout.addView(headerLayout)
        mainLayout.addView(adContainer)

        fullscreenDialog?.setContentView(mainLayout)

        fullscreenDialog?.setOnDismissListener {
            fullscreenAdView?.destroy()
            fullscreenAdView = null
        }

        fullscreenDialog?.setOnShowListener {
            fullscreenAdView?.show()
            adContainer.addView(view)
            isAdLoadedState.value = false
        }

        fullscreenDialog?.show()
    }

    /**
     * 선택된 스타일 생성
     */
    private fun getSelectedStyle(styleOption: StyledThemeOption): AdWhaleMediationNativeTemplateStyle? {
        return when (styleOption) {
            StyledThemeOption.DEFAULT -> createDefaultStyle()
            StyledThemeOption.DARK -> createDarkStyle()
            StyledThemeOption.BRAND -> createBrandStyle()
            StyledThemeOption.MINIMAL -> createMinimalStyle()
            StyledThemeOption.FULL_STYLE -> createFullStyle()
            StyledThemeOption.CUSTOM_FONT -> createCustomFontStyle()
        }
    }

    private fun createDefaultStyle(): AdWhaleMediationNativeTemplateStyle? {
        // 기본 스타일 없음 (SDK 기본값 사용)
        return null
    }

    private fun createDarkStyle(): AdWhaleMediationNativeTemplateStyle {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#2C2C2C")))
            .withPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
            .withPrimaryTextTypefaceColor(Color.WHITE)
            .withSecondaryTextTypefaceColor(Color.parseColor("#CCCCCC"))
            .withTertiaryTextTypefaceColor(Color.parseColor("#AAAAAA"))
            .withCallToActionTypefaceColor(Color.WHITE)
            .withCallToActionBackgroundColor(ColorDrawable(Color.parseColor("#4CAF50")))
            .build()
    }

    private fun createBrandStyle(): AdWhaleMediationNativeTemplateStyle {
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

    private fun createMinimalStyle(): AdWhaleMediationNativeTemplateStyle {
        return AdWhaleMediationNativeTemplateStyle.Builder()
            .withMainBackgroundColor(ColorDrawable(Color.WHITE))
            .withPrimaryTextTypefaceColor(Color.parseColor("#333333"))
            .withSecondaryTextTypefaceColor(Color.parseColor("#666666"))
            .withTertiaryTextTypefaceColor(Color.parseColor("#999999"))
            .withCallToActionTypefaceColor(Color.parseColor("#2196F3"))
            .withCallToActionBackgroundColor(ColorDrawable(Color.TRANSPARENT))
            .build()
    }

    private fun createFullStyle(): AdWhaleMediationNativeTemplateStyle {
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

    private fun createCustomFontStyle(): AdWhaleMediationNativeTemplateStyle {
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

    override fun onResume() {
        super.onResume()
        nativeAdView?.resume()
    }

    override fun onPause() {
        super.onPause()
        nativeAdView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAdView?.destroy()
        nativeAdView = null
        fullscreenAdView?.destroy()
        fullscreenAdView = null
        if (fullscreenDialog != null && fullscreenDialog!!.isShowing) {
            fullscreenDialog!!.dismiss()
        }
    }
}

/**
 * 템플릿 옵션
 */
enum class StyledTemplateOption {
    SMALL,
    MEDIUM,
    FULLSCREEN
}

/**
 * 스타일 테마 옵션
 */
enum class StyledThemeOption {
    DEFAULT,
    DARK,
    BRAND,
    MINIMAL,
    FULL_STYLE,
    CUSTOM_FONT
}

/**
 * 기존 activity_styled_template_binding_native_main.xml 을 Compose 로 옮긴 UI
 */
@Composable
fun StyledTemplateNativeScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    selectedTemplate: StyledTemplateOption,
    onTemplateChange: (StyledTemplateOption) -> Unit,
    selectedStyle: StyledThemeOption,
    onStyleChange: (StyledThemeOption) -> Unit,
    onLoggerChange: (Boolean) -> Unit,
    isAdLoaded: Boolean,
    onAdRequest: (StyledTemplateOption, StyledThemeOption) -> Unit,
    onShowAd: (StyledTemplateOption) -> Unit,
    adViewContainer: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 제목
        Text(
            text = "스타일이 적용된 네이티브 템플릿 테스트",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // Placement UID
        Text(
            text = "Placement UID",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = placementUid,
            onValueChange = onPlacementUidChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Placement UID를 입력하세요") }
        )

        Spacer(Modifier.height(16.dp))

        // 로거 스위치
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "디버그 로그",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            var loggerChecked by remember { mutableStateOf(true) }
            Switch(
                checked = loggerChecked,
                onCheckedChange = {
                    loggerChecked = it
                    onLoggerChange(it)
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 템플릿 크기 선택
        Text(
            text = "템플릿 크기 선택",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TemplateRadio(
                text = "Small",
                selected = selectedTemplate == StyledTemplateOption.SMALL,
                onClick = { onTemplateChange(StyledTemplateOption.SMALL) },
                modifier = Modifier.weight(1f)
            )
            TemplateRadio(
                text = "Medium",
                selected = selectedTemplate == StyledTemplateOption.MEDIUM,
                onClick = { onTemplateChange(StyledTemplateOption.MEDIUM) },
                modifier = Modifier.weight(1f)
            )
            TemplateRadio(
                text = "Fullscreen",
                selected = selectedTemplate == StyledTemplateOption.FULLSCREEN,
                onClick = { onTemplateChange(StyledTemplateOption.FULLSCREEN) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 스타일 테마 선택
        Text(
            text = "스타일 테마 선택",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            StyleRadio(
                text = "기본 테마 (Default)",
                selected = selectedStyle == StyledThemeOption.DEFAULT,
                onClick = { onStyleChange(StyledThemeOption.DEFAULT) }
            )
            StyleRadio(
                text = "다크 테마 (Dark)",
                selected = selectedStyle == StyledThemeOption.DARK,
                onClick = { onStyleChange(StyledThemeOption.DARK) }
            )
            StyleRadio(
                text = "브랜드 테마 (Brand)",
                selected = selectedStyle == StyledThemeOption.BRAND,
                onClick = { onStyleChange(StyledThemeOption.BRAND) }
            )
            StyleRadio(
                text = "미니멀 스타일 (Minimal)",
                selected = selectedStyle == StyledThemeOption.MINIMAL,
                onClick = { onStyleChange(StyledThemeOption.MINIMAL) }
            )
            StyleRadio(
                text = "풀 스타일 (Full Style)",
                selected = selectedStyle == StyledThemeOption.FULL_STYLE,
                onClick = { onStyleChange(StyledThemeOption.FULL_STYLE) }
            )
            StyleRadio(
                text = "커스텀 폰트 (Custom Font)",
                selected = selectedStyle == StyledThemeOption.CUSTOM_FONT,
                onClick = { onStyleChange(StyledThemeOption.CUSTOM_FONT) }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 광고 요청 버튼
        Button(
            onClick = { onAdRequest(selectedTemplate, selectedStyle) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("스타일이 적용된 네이티브 광고 로드")
        }

        Spacer(Modifier.height(8.dp))

        // 광고 표시 버튼
        Button(
            onClick = { onShowAd(selectedTemplate) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isAdLoaded
        ) {
            Text("광고 표시")
        }

        Spacer(Modifier.height(24.dp))

        // 광고 표시 영역
        Text(
            text = "광고 표시 영역",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                adViewContainer()
            }
        }
    }
}

@Composable
private fun TemplateRadio(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = text)
    }
}

@Composable
private fun StyleRadio(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = text)
    }
}
