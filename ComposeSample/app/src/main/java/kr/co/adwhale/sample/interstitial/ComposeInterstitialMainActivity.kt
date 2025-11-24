package kr.co.adwhale.sample.interstitial

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
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationInterstitialAdListener
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeInterstitialMainActivity : ComponentActivity() {

    private val TAG = ComposeInterstitialMainActivity::class.simpleName

    private var interstitialAd: AdWhaleMediationInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdWhale 초기화
        AdWhaleMediationAds.init(this) { statusCode, message ->
            runOnUiThread {
                AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Verbose)
                Log.i(TAG, ".onInitComplete($statusCode, $message)")
            }
        }

        val defaultPlacementUid = getString(R.string.interstitial_placementUid)

        setContent {
            MaterialTheme {
                var placementUid by remember { mutableStateOf(defaultPlacementUid) }

                InterstitialSampleScreen(
                    placementUid = placementUid,
                    onPlacementUidChange = { placementUid = it },
                    onRequestAd = {
                        requestInterstitial(placementUid)
                    },
                    onShowAd = {
                        showInterstitial()
                    }
                )
            }
        }
    }

    /**
     * 전면 광고 로드 요청
     */
    private fun requestInterstitial(placementUid: String) {
        if (placementUid.isBlank()) {
            Toast.makeText(
                applicationContext,
                "placementUid 를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 이전 광고 객체 정리
        interstitialAd = null

        // 새 Interstitial 생성
        val ad = AdWhaleMediationInterstitialAd(placementUid).also {
            setupInterstitialListener(it)
        }
        interstitialAd = ad

        ad.loadAd()
    }

    /**
     * 전면 광고 표시
     */
    private fun showInterstitial() {
        val ad = interstitialAd
        if (ad == null) {
            Toast.makeText(
                applicationContext,
                "먼저 전면 광고를 로드하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        ad.showAd()
    }

    /**
     * 리스너 세팅
     */
    private fun setupInterstitialListener(ad: AdWhaleMediationInterstitialAd) {
        ad.setAdWhaleMediationInterstitialAdListener(object :
            AdWhaleMediationInterstitialAdListener {

            override fun onAdLoaded() {
                Log.i(TAG, ".onAdLoaded()")
                Toast.makeText(
                    applicationContext,
                    ".onAdLoaded()",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdLoadFailed(statusCode: Int, message: String) {
                Log.e(TAG, ".onAdLoadFailed($statusCode, $message)")
                Toast.makeText(
                    applicationContext,
                    ".onAdLoadFailed(statusCode:$statusCode, message:$message)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdShowed() {
                Log.i(TAG, ".onAdShowed()")
                Toast.makeText(
                    applicationContext,
                    ".onAdShowed()",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdShowFailed(statusCode: Int, message: String) {
                Log.e(TAG, ".onAdShowFailed($statusCode, $message)")
                Toast.makeText(
                    applicationContext,
                    ".onAdShowFailed(statusCode:$statusCode, message:$message)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClosed() {
                Log.i(TAG, ".onAdClosed()")
                Toast.makeText(
                    applicationContext,
                    ".onAdClosed()",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClicked() {
                Log.i(TAG, ".onAdClicked()")
                Toast.makeText(
                    applicationContext,
                    ".onAdClicked()",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

/**
 * 기존 activity_programmatic_interstitial_main.xml 을 Compose 로 옮긴 UI
 */
@Composable
fun InterstitialSampleScreen(
    placementUid: String,
    onPlacementUidChange: (String) -> Unit,
    onRequestAd: () -> Unit,
    onShowAd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "1. 테스트 전면광고 Placement Uid 입력:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = placementUid,
            onValueChange = onPlacementUidChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("placementUid 입력") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRequestAd,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("테스트 전면광고 요청")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onShowAd,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("테스트 전면광고 표시")
        }
    }
}
