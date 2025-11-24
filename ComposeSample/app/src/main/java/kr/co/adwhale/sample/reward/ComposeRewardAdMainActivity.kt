package kr.co.adwhale.sample.reward

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationFullScreenContentCallback
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationRewardedAdLoadCallback
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeRewardAdMainActivity : ComponentActivity() {

    private val TAG = ComposeRewardAdMainActivity::class.simpleName

    private var rewardAd: AdWhaleMediationRewardAd? = null
    private lateinit var isAdLoadedState: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdWhale 초기화
        AdWhaleMediationAds.init(this) { statusCode, message ->
            AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
            Log.i(TAG, ".onInitComplete($statusCode, $message)")
        }

        val defaultPlacementUid = getString(R.string.reward_placementUid)

        setContent {
            MaterialTheme {
                var placementUid by remember { mutableStateOf(defaultPlacementUid) }
                val isAdLoaded = remember { mutableStateOf(false) }
                isAdLoadedState = isAdLoaded

                RewardAdScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
                    isAdLoaded = isAdLoaded.value,
                    onLoadClick = {
                        loadRewardAd(placementUid)
                    },
                    onShowClick = {
                        showRewardAd()
                    }
                )
            }
        }
    }

    /**
     * 보상형 광고 로드
     */
    private fun loadRewardAd(placementUid: String) {
        if (placementUid.isBlank()) {
            Toast.makeText(
                applicationContext,
                "placementUid 를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 새로운 RewardAd 인스턴스 생성 (placementUid 변경 반영)
        val ad = AdWhaleMediationRewardAd(placementUid)
        rewardAd = ad

        ad.setAdWhaleMediationFullScreenContentCallback(object :
            AdWhaleMediationFullScreenContentCallback {

            override fun onAdClicked() {
                Log.i(TAG, ".onAdClicked()")
            }

            override fun onAdDismissed() {
                Log.i(TAG, ".onAdDismissed()")
                Toast.makeText(
                    applicationContext,
                    ".onAdDismissed()",
                    Toast.LENGTH_SHORT
                ).show()
                isAdLoadedState.value = false
            }

            override fun onFailedToShow(statusCode: Int, message: String) {
                Log.i(TAG, ".onFailedToShow($statusCode, $message)")
                Toast.makeText(
                    applicationContext,
                    ".onFailedToShow(statusCode:$statusCode, message:$message)",
                    Toast.LENGTH_SHORT
                ).show()
                isAdLoadedState.value = false
            }

            override fun onAdShowed() {
                Log.i(TAG, ".onAdShowed()")
                Toast.makeText(
                    applicationContext,
                    ".onAdShowed()",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        ad.loadAd(object : AdWhaleMediationRewardedAdLoadCallback {
            override fun onAdLoaded(rewardAd: AdWhaleMediationRewardAd, message: String) {
                Log.i(TAG, ".onAdLoaded($message)")
                Toast.makeText(
                    applicationContext,
                    ".onAdLoaded($message)",
                    Toast.LENGTH_SHORT
                ).show()
                isAdLoadedState.value = true
            }

            override fun onAdFailedToLoad(statusCode: Int, message: String) {
                Log.i(TAG, ".onAdFailedToLoad($statusCode, $message)")
                Toast.makeText(
                    applicationContext,
                    ".onAdFailedToLoad(statusCode:$statusCode, message:$message)",
                    Toast.LENGTH_SHORT
                ).show()
                isAdLoadedState.value = false
            }
        })
    }

    /**
     * 보상형 광고 표시
     */
    private fun showRewardAd() {
        val ad = rewardAd
        if (ad == null || !isAdLoadedState.value) {
            Toast.makeText(
                applicationContext,
                "광고가 로드되지 않았습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        ad.showAd { rewardItem ->
            Log.i(TAG, ".onUserRewarded(${rewardItem})")
            Toast.makeText(
                applicationContext,
                ".onUserRewarded(${rewardItem})",
                Toast.LENGTH_SHORT
            ).show()
            // 리워드 지급 후 isAdLoaded 는 false 상태 유지
            isAdLoadedState.value = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 특별히 destroy 메서드는 없지만, 참조 제거
        rewardAd = null
    }
}

/**
 * 기존 activity_programmatic_reward_ad_main.xml 을 Compose 로 옮긴 UI
 */
@Composable
fun RewardAdScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    isAdLoaded: Boolean,
    onLoadClick: () -> Unit,
    onShowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "1. 테스트 보상형광고 Placement Uid 입력:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = placementUid,
            onValueChange = onPlacementUidChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("placementUid 입력") }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onLoadClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("테스트 보상형광고 요청")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onShowClick,
            enabled = isAdLoaded,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("테스트 보상형광고 표시")
        }
    }
}
