package kr.co.adwhale.sample.banner

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.adwhale.sdk.mediation.ads.ADWHALE_AD_SIZE
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdView
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAdViewListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeBannerMainActivity : ComponentActivity() {

    private val TAG = ComposeBannerMainActivity::class.simpleName

    // 실제 배너 광고 View
    private var adWhaleMediationAdView: AdWhaleMediationAdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, ".onCreate()")

        // AdWhale 초기화
        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                runOnUiThread {
                    AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
                    Log.i(TAG, ".onInitComplete($statusCode, $message)")
                }
            }
        })

        // placementUid 리소스 미리 가져오기
        val banner320x50Uid = getString(R.string.banner32050_placementUid)
        val banner320x100Uid = getString(R.string.banner320100_placementUid)
        val banner300x250Uid = getString(R.string.banner300250_placementUid)
        val banner250x250Uid = getString(R.string.banner250250_placementUid)

        setContent {
            MaterialTheme {
                var placementUid by remember { mutableStateOf("") }
                var selectedSizeOption by remember { mutableStateOf<BannerSizeOption?>(null) }

                BannerSampleScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
                    selectedSize = selectedSizeOption,
                    onSizeSelected = { option ->
                        selectedSizeOption = option
                        placementUid = when (option) {
                            BannerSizeOption.B320x50 -> banner320x50Uid
                            BannerSizeOption.B320x100 -> banner320x100Uid
                            BannerSizeOption.B300x250 -> banner300x250Uid
                            BannerSizeOption.B250x250 -> banner250x250Uid
                        }
                    },
                    onRequestBanner = {
                        requestBanner(placementUid, selectedSizeOption)
                    },
                    adViewContainer = {
                        BannerAdViewContainer()
                    }
                )
            }
        }
    }

    /**
     * 실제 배너 광고 요청 로직
     */
    private fun requestBanner(placementUid: String, sizeOption: BannerSizeOption?) {
        if (placementUid.isBlank() || sizeOption == null) {
            Toast.makeText(
                applicationContext,
                "배너 사이즈와 placementUid를 먼저 선택/입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val adView = adWhaleMediationAdView
        if (adView == null) {
            Toast.makeText(
                applicationContext,
                "배너 View가 아직 초기화되지 않았습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        adView.setPlacementUid(placementUid)
        adView.setAdwhaleAdSize(sizeOption.toSdkSize())
        adView.loadAd()
    }

    /**
     * Compose 안에서 배너 View를 노출하는 컨테이너
     */
    @Composable
    private fun BannerAdViewContainer() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    // 한 번만 생성해서 재사용
                    val view = adWhaleMediationAdView ?: AdWhaleMediationAdView(context).also {
                        setupAdViewListener(it)
                        adWhaleMediationAdView = it
                    }

                    // RelativeLayout.LayoutParams 설정 (CENTER_HORIZONTAL)
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
     * 배너 리스너 설정
     */
    private fun setupAdViewListener(adView: AdWhaleMediationAdView) {
        adView.setAdWhaleMediationAdViewListener(object : AdWhaleMediationAdViewListener {
            override fun onAdLoaded() {
                Log.i(TAG, ".onAdLoaded()")
                Toast.makeText(applicationContext, ".onAdLoaded()", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoadFailed(statusCode: Int, message: String) {
                Log.e(TAG, ".onAdLoadFailed($statusCode, $message)")
                Toast.makeText(
                    applicationContext,
                    ".onAdLoadFailed(statusCode:$statusCode, message:$message)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClicked() {
                Log.i(TAG, ".onAdClicked()")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, ".onResume()")
        adWhaleMediationAdView?.resume()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, ".onPause()")
        adWhaleMediationAdView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, ".onDestroy()")
        adWhaleMediationAdView?.destroy()
        adWhaleMediationAdView = null
    }
}

/**
 * UI에서 사용할 배너 사이즈 옵션
 */
enum class BannerSizeOption {
    B320x50,
    B320x100,
    B300x250,
    B250x250
}

/**
 * UI용 enum -> SDK enum 매핑
 */
private fun BannerSizeOption.toSdkSize(): ADWHALE_AD_SIZE {
    return when (this) {
        BannerSizeOption.B320x50 -> ADWHALE_AD_SIZE.BANNER320x50
        BannerSizeOption.B320x100 -> ADWHALE_AD_SIZE.BANNER320x100
        BannerSizeOption.B300x250 -> ADWHALE_AD_SIZE.BANNER300x250
        BannerSizeOption.B250x250 -> ADWHALE_AD_SIZE.BANNER250x250
    }
}

/**
 * 기존 activity_programmatic_banner_main.xml 을 Compose로 옮긴 UI
 */
@Composable
fun BannerSampleScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    selectedSize: BannerSizeOption?,
    onSizeSelected: (BannerSizeOption) -> Unit,
    onRequestBanner: () -> Unit,
    adViewContainer: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "1. 테스트 배너광고 사이즈 선택:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

        // 2x2 레이아웃으로 라디오 버튼 정렬
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BannerSizeRadio(
                    label = "320x50",
                    option = BannerSizeOption.B320x50,
                    selected = selectedSize,
                    onSelected = onSizeSelected,
                    modifier = Modifier.weight(1f)
                )
                BannerSizeRadio(
                    label = "320x100",
                    option = BannerSizeOption.B320x100,
                    selected = selectedSize,
                    onSelected = onSizeSelected,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BannerSizeRadio(
                    label = "300x250",
                    option = BannerSizeOption.B300x250,
                    selected = selectedSize,
                    onSelected = onSizeSelected,
                    modifier = Modifier.weight(1f)
                )
                BannerSizeRadio(
                    label = "250x250",
                    option = BannerSizeOption.B250x250,
                    selected = selectedSize,
                    onSelected = onSizeSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "2. 테스트 배너광고 placementUid 입력:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = placementUid,
            onValueChange = onPlacementUidChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("placementUid 입력") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRequestBanner,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("테스트 배너광고 요청")
        }

        Spacer(Modifier.height(24.dp))

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "배너 광고 영역",
            style = MaterialTheme.typography.titleMedium
        )

        adViewContainer()
    }
}

@Composable
private fun BannerSizeRadio(
    label: String,
    option: BannerSizeOption,
    selected: BannerSizeOption?,
    onSelected: (BannerSizeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected == option,
            onClick = { onSelected(option) }
        )
        Text(text = label)
    }
}
