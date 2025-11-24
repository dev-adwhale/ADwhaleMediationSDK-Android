package kr.co.adwhale.sample.nativead

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationNativeAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.mediation.ads.AdWhaleNativeAdBinder
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeCustomBindingNativeMainActivity : ComponentActivity() {

    private val TAG = ComposeCustomBindingNativeMainActivity::class.simpleName

    private var nativeAdView: AdWhaleMediationNativeAdView? = null
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
                val context = LocalContext.current

                var placementUid by remember { mutableStateOf(defaultPlacementUid) }
                val isAdLoaded = remember { mutableStateOf(false) }
                isAdLoadedState = isAdLoaded

                CustomBindingNativeScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
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
                    onAdRequest = {
                        requestNativeAd(placementUid, context)
                    },
                    onShowAd = {
                        if (isAdLoaded.value) {
                            nativeAdView?.show()
                            isAdLoaded.value = false
                        }
                    },
                    adViewContainer = {
                        CustomBindingNativeAdContainer()
                    }
                )
            }
        }
    }

    /**
     * 광고 요청
     */
    private fun requestNativeAd(placementUid: String, context: android.content.Context) {
        if (placementUid.isBlank()) {
            Toast.makeText(
                context,
                "placementUid 를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val adView = nativeAdView
        if (adView == null) {
            Toast.makeText(
                context,
                "네이티브 광고 View가 아직 초기화되지 않았습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        adView.setPlacementUid(placementUid)

        // custom native AdBinder 생성 (기존 XML 그대로 사용)
        val binder = AdWhaleNativeAdBinder.Builder(this, R.layout.custom_native_ad_layout)
            .setIconViewId(R.id.view_icon)
            .setTitleViewId(R.id.view_title)
            .setBodyTextViewId(R.id.view_body)
            .setCallToActionViewId(R.id.button_cta)
            .setMediaViewGroupId(R.id.view_media)
            .build()

        // 커스텀 바인딩으로 광고 로드
        adView.loadAdWithBinder(binder)
    }

    /**
     * Compose 안에 네이티브 광고 View를 표시하는 컨테이너
     */
    @Composable
    private fun CustomBindingNativeAdContainer() {
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
                        setupNativeListener(it)
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
     * 네이티브 광고 리스너 설정
     */
    private fun setupNativeListener(adView: AdWhaleMediationNativeAdView) {
        adView.setAdWhaleMediationNativeAdViewListener(object :
            AdWhaleMediationNativeAdViewListener {

            override fun onNativeAdLoaded() {
                Log.i(TAG, ".onNativeAdLoaded()")
                isAdLoadedState.value = true
                Toast.makeText(
                    applicationContext,
                    "커스텀 바인딩 네이티브 광고 로드 성공!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdFailedToLoad(errorCode: Int, errorMessage: String) {
                Log.e(TAG, ".onNativeAdFailedToLoad($errorCode, $errorMessage)")
                isAdLoadedState.value = false
                Toast.makeText(
                    applicationContext,
                    "커스텀 바인딩 네이티브 광고 로드 실패: $errorMessage",
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
                    "커스텀 바인딩 네이티브 광고 클릭!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNativeAdClosed() {
                Log.i(TAG, ".onNativeAdClosed()")
                Toast.makeText(
                    applicationContext,
                    "커스텀 바인딩 네이티브 광고 닫힘",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
    }
}

/**
 * 기존 activity_programmatic_custom_binding_native_main.xml 을 Compose 로 옮긴 UI
 */
@Composable
fun CustomBindingNativeScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    isLoggerEnabledChange: (Boolean) -> Unit,
    isAdLoaded: Boolean,
    onAdRequest: () -> Unit,
    onShowAd: () -> Unit,
    adViewContainer: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "커스텀 바인딩 네이티브 광고 테스트",
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

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onAdRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("커스텀 바인딩 광고 로드")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onShowAd,
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
                .weight(1f),
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
