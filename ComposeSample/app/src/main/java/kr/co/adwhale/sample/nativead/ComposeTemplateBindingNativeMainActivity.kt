package kr.co.adwhale.sample.nativead

import android.app.Dialog
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
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeTemplateBindingNativeMainActivity : ComponentActivity() {

    private val TAG = ComposeTemplateBindingNativeMainActivity::class.simpleName

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
                var selectedTemplate by remember { mutableStateOf(TemplateOption.SMALL) }
                val isAdLoaded = remember { mutableStateOf(false) }
                isAdLoadedState = isAdLoaded

                TemplateBindingNativeScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
                    selectedTemplate = selectedTemplate,
                    onTemplateChange = { selectedTemplate = it },
                    isLoggerEnabledChange = { enabled ->
                        if (enabled) {
                            Log.d(TAG, "로거가 활성화되었습니다.")
                            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
                        } else {
                            Log.d(TAG, "로거가 비활성화되었습니다.")
                            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)
                        }
                    },
                    isAdLoaded = isAdLoaded.value,
                    onAdRequest = { template ->
                        requestNativeTemplate(placementUid, template)
                    },
                    onShowAd = { template ->
                        showNativeTemplate(template)
                    },
                    adViewContainer = {
                        TemplateNativeAdContainer()
                    }
                )
            }
        }
    }

    /**
     * 템플릿 네이티브 광고 로드
     */
    private fun requestNativeTemplate(placementUid: String, template: TemplateOption) {
        if (placementUid.isBlank()) {
            Toast.makeText(
                applicationContext,
                "placementUid 를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (template) {
            TemplateOption.SMALL, TemplateOption.MEDIUM -> {
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
                val t = when (template) {
                    TemplateOption.SMALL -> ADWHALE_NATIVE_TEMPLATE.SMALL
                    TemplateOption.MEDIUM -> ADWHALE_NATIVE_TEMPLATE.MEDIUM
                    else -> ADWHALE_NATIVE_TEMPLATE.SMALL // not used
                }
                adView.loadAdWithTemplate(t)
            }

            TemplateOption.FULLSCREEN_DIALOG -> {
                loadFullscreenNativeAdDialog(placementUid)
            }
        }
    }

    /**
     * 템플릿 네이티브 광고 표시
     */
    private fun showNativeTemplate(template: TemplateOption) {
        when (template) {
            TemplateOption.SMALL, TemplateOption.MEDIUM -> {
                nativeAdView?.show()
                isAdLoadedState.value = false
            }

            TemplateOption.FULLSCREEN_DIALOG -> {
                showFullscreenNativeAdDialog()
            }
        }
    }

    /**
     * Compose 안에 인라인 템플릿 네이티브 광고 View를 표시하는 컨테이너
     */
    @Composable
    private fun TemplateNativeAdContainer() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            AndroidView(
                factory = { context ->
                    // 한 번만 생성해서 재사용
                    val view = nativeAdView ?: AdWhaleMediationNativeAdView(context).also {
                        setupInlineNativeListener(it)
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
     * 인라인 네이티브 광고 리스너
     */
    private fun setupInlineNativeListener(adView: AdWhaleMediationNativeAdView) {
        adView.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {

            override fun onNativeAdLoaded() {
                Log.i(TAG, ".onNativeAdLoaded()")
                isAdLoadedState.value = true
                Toast.makeText(
                    applicationContext,
                    "고정 템플릿 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdFailedToLoad($errorCode, $errorMessage)")
                isAdLoadedState.value = false
                Toast.makeText(
                    applicationContext,
                    "고정 템플릿 네이티브 광고 로드 실패: $errorMessage",
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
                    "고정 템플릿 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(TAG, ".onNativeAdClosed()")
                Toast.makeText(
                    applicationContext,
                    "고정 템플릿 네이티브 광고 닫힘",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * FULLSCREEN 템플릿 로드 (다이얼로그용)
     */
    private fun loadFullscreenNativeAdDialog(placementUid: String) {
        // FULLSCREEN 네이티브 광고 뷰 생성
        val view = AdWhaleMediationNativeAdView(this)
        fullscreenAdView = view

        val uid = if (placementUid.isNotBlank()) {
            placementUid
        } else {
            getString(R.string.native_placement_uid)
        }
        view.setPlacementUid(uid)

        view.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {

            override fun onNativeAdLoaded() {
                Log.i(TAG, ".onNativeAdLoaded() - Fullscreen Dialog")
                isAdLoadedState.value = true
                Toast.makeText(
                    applicationContext,
                    "Fullscreen 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdFailedToLoad($errorCode, $errorMessage) - Fullscreen Dialog")
                isAdLoadedState.value = false
                Toast.makeText(
                    applicationContext,
                    "Fullscreen 네이티브 광고 로드 실패: $errorMessage",
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
                    "Fullscreen 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(TAG, ".onNativeAdClosed() - Fullscreen Dialog")
                Toast.makeText(
                    applicationContext,
                    "Fullscreen 네이티브 광고 닫힘",
                    Toast.LENGTH_SHORT
                ).show()
                if (fullscreenDialog != null && fullscreenDialog!!.isShowing) {
                    fullscreenDialog!!.dismiss()
                }
            }
        })

        Log.d(TAG, "FULLSCREEN 광고 로드 시작")
        view.loadAdWithTemplate(ADWHALE_NATIVE_TEMPLATE.FULLSCREEN)
    }

    /**
     * FULLSCREEN 템플릿을 다이얼로그로 표시
     */
    private fun showFullscreenNativeAdDialog() {
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

        // 헤더 레이아웃
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFF000000.toInt())
        }

        val titleText = TextView(this).apply {
            text = "Fullscreen Native Ad"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val closeButton = Button(this).apply {
            text = "X"
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF000000.toInt())
            setOnClickListener {
                fullscreenDialog?.dismiss()
            }
        }

        headerLayout.addView(titleText)
        headerLayout.addView(closeButton)

        // 광고 영역
        val adContainer = RelativeLayout(this).apply {
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFF0F0F0.toInt())

            val minHeightDp = 120 + 32 // 120dp + padding
            val minHeightPx = (minHeightDp * resources.displayMetrics.density).toInt()
            val adParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            adParams.height = minHeightPx
            layoutParams = adParams
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

    override fun onResume() {
        super.onResume()
        Log.i(TAG, ".onResume()")
        nativeAdView?.resume()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, ".onPause()")
        nativeAdView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, ".onDestroy()")
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
 * 템플릿 옵션 (UI + 로직 공용)
 */
enum class TemplateOption {
    SMALL,
    MEDIUM,
    FULLSCREEN_DIALOG
}

/**
 * 기존 activity_programmatic_template_binding_native_main.xml 을 Compose 로 옮긴 UI
 */
@Composable
fun TemplateBindingNativeScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    selectedTemplate: TemplateOption,
    onTemplateChange: (TemplateOption) -> Unit,
    isLoggerEnabledChange: (Boolean) -> Unit,
    isAdLoaded: Boolean,
    onAdRequest: (TemplateOption) -> Unit,
    onShowAd: (TemplateOption) -> Unit,
    adViewContainer: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "고정 템플릿 네이티브 광고 테스트",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        // 설정 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "설정",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = placementUid,
                    onValueChange = onPlacementUidChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Placement UID") }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "로거 활성화")
                    Spacer(Modifier.width(8.dp))
                    var checked by remember { mutableStateOf(false) }
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            isLoggerEnabledChange(it)
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "템플릿 크기 선택",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTemplate == TemplateOption.SMALL,
                            onClick = { onTemplateChange(TemplateOption.SMALL) }
                        )
                        Text("Small")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTemplate == TemplateOption.MEDIUM,
                            onClick = { onTemplateChange(TemplateOption.MEDIUM) }
                        )
                        Text("Medium")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTemplate == TemplateOption.FULLSCREEN_DIALOG,
                            onClick = { onTemplateChange(TemplateOption.FULLSCREEN_DIALOG) }
                        )
                        Text("Fullscreen (Dialog)")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onAdRequest(selectedTemplate) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("고정 템플릿 광고 로드")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { onShowAd(selectedTemplate) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAdLoaded
                ) {
                    Text("광고 표시")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 광고 영역 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp), // 적당한 높이
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "광고 영역",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    adViewContainer()
                }
            }
        }
    }
}
