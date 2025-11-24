package kr.co.adwhale.sample.appopen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.NonNull
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAdListener
import net.adwhale.sdk.utils.AdWhaleLog
import kr.co.adwhale.sample.R

class ComposeAppOpenMainActivity : ComponentActivity(), AdWhaleMediationAppOpenAdListener {

    private val TAG = "ComposeAppOpenMain"

    private var appLifecycleObserver: DefaultLifecycleObserver? = null
    private var isAppInBackground = false

    private var appOpenAd: AdWhaleMediationAppOpenAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로거 설정
        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)

        // 앱 전체 라이프사이클 옵저버 등록
        setupLifecycleObserver()

        // AdWhale 초기화
        AdWhaleMediationAds.init(this) { statusCode, message ->
            // 콜백 스레드 보장 X → 메인 스레드로 보냄
            runOnUiThread {
                Log.i(TAG, "onInitComplete($statusCode, $message)")

                // 기존 광고만 정리 (옵저버는 건드리지 않음)
                appOpenAd?.destroy()
                appOpenAd = null

                // 새 앱 오프닝 광고 생성
                appOpenAd = AdWhaleMediationAppOpenAd(
                    this,
                    getString(R.string.app_open_placement_uid)
                ).apply {
                    setAdWhaleMediationAppOpenAdListener(this@ComposeAppOpenMainActivity)
                    loadAd()
                }
            }
        }

        setContent {
            MaterialTheme {
                AppOpenMainScreen()
            }
        }
    }

    @Composable
    private fun AppOpenMainScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "App Open Ad 테스트 화면",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))
            Text("백그라운드 → 포어그라운드 진입 시 자동 표시됩니다.")
        }
    }

    private fun setupLifecycleObserver() {
        appLifecycleObserver = object : DefaultLifecycleObserver {
            override fun onStart(@NonNull owner: LifecycleOwner) {
                Log.d(TAG, "App moved to foreground")

                if (appOpenAd != null && isAppInBackground) {
                    appOpenAd?.showAd(this@ComposeAppOpenMainActivity)
                }
                isAppInBackground = false
            }

            override fun onStop(@NonNull owner: LifecycleOwner) {
                Log.d(TAG, "App moved to background")
                isAppInBackground = true
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver!!)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 옵저버 정리는 여기에서만
        appLifecycleObserver?.let {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(it)
            appLifecycleObserver = null
        }

        appOpenAd?.destroy()
        appOpenAd = null
    }

    // ---- 광고 콜백 ----
    override fun onAdLoaded() {
        Log.i(TAG, "onAdLoaded()")
    }

    override fun onAdFailedToLoad(statusCode: Int, message: String) {
        Log.e(TAG, "onAdFailedToLoad($statusCode, $message)")
    }

    override fun onAdFailedToShow(statusCode: Int, message: String) {
        Log.e(TAG, "onAdFailedToShow($statusCode, $message)")
    }

    override fun onAdClicked() {
        Log.i(TAG, "onAdClicked()")
    }

    override fun onAdDismissed() {
        Log.i(TAG, "onAdDismissed()")
    }

    override fun onAdShowed() {
        Log.i(TAG, "onAdShowed()")
    }
}
