package kr.co.adwhale.sample.appopen

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kr.co.adwhale.sample.R
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAppOpenAdListener
import net.adwhale.sdk.mediation.ads.AdWhaleMediationOnInitCompleteListener
import net.adwhale.sdk.utils.AdWhaleLog

class ProgrammaticAppOpenMainActivity : AppCompatActivity(), AdWhaleMediationAppOpenAdListener {
    // 라이프사이클 관리 (사용자가 직접 처리)
    private var appLifecycleObserver: DefaultLifecycleObserver? = null

    // 백그라운드/포어그라운드 상태체크 플래그값 (사용자가 직접 처리)
    private var isAppInBackground = false

    private var adWhaleMediationAppOpenAd: AdWhaleMediationAppOpenAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_app_open_main)

        // 앱 라이프사이클 관리 Observer 등록: 백그라운드/포어그라운드 감지를 위해 등록 필요
        appLifecycleObserver = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                Log.d(TAG, "App moved to foreground")

                // 사용자가 직접 처리: 포어그라운드로 전환 시 광고 표시
                // 필요에 따라 조건 체크 (예: 백그라운드 시간, 광고 간격 등)를 추가할 수 있음
                if (adWhaleMediationAppOpenAd != null && isAppInBackground) {
                    if (adWhaleMediationAppOpenAd != null) {
                        adWhaleMediationAppOpenAd!!.showAd(this@ProgrammaticAppOpenMainActivity)
                    }
                }
                // 사용자가 직접 처리: 백그라운드/포어그라운드 상태체크 플래그값 업데이트
                isAppInBackground = false
            }

            override fun onStop(owner: LifecycleOwner) {
                Log.d(TAG, "App moved to background")

                // 사용자가 직접 처리: 백그라운드/포어그라운드 상태체크 플래그값 업데이트
                isAppInBackground = true
            }
        }

        // ProcessLifecycleOwner에 Observer 등록
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver!!)

        // 로거 설정
        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.None)

        // 초기화 코드
        AdWhaleMediationAds.init(this, object : AdWhaleMediationOnInitCompleteListener {
            override fun onInitComplete(statusCode: Int, message: String?) {
                Log.i(TAG, ".onInitComplete(" + statusCode + ", " + message + ");")

                // 앱 오프닝 광고 null 체크: 이미 load하거나 생성된 앱 오프닝 광고가 있다면 onDestroy()가 있는 release() 호출
                if (adWhaleMediationAppOpenAd != null) {
                    release()
                }

                // 앱 오프닝 광고 생성
                adWhaleMediationAppOpenAd = AdWhaleMediationAppOpenAd(
                    this@ProgrammaticAppOpenMainActivity,
                    getString(R.string.app_open_placement_uid)
                )

                // 앱 오프닝 광고 콜백 리스너 등록
                adWhaleMediationAppOpenAd!!.setAdWhaleMediationAppOpenAdListener(this@ProgrammaticAppOpenMainActivity)

                // 앱 오프닝 광고 로드
                adWhaleMediationAppOpenAd!!.loadAd()
            }
        })
    }


    // 라이프사이클 onDestroy 콜백 시 반드시 onDestroy()가 있는 release() 호출 필요
    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() {
        // ProcessLifecycleOwner Observer 해제
        if (appLifecycleObserver != null) {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver!!)
            appLifecycleObserver = null
        }

        if (adWhaleMediationAppOpenAd != null) {
            // 앱 종료 시 또는 더 이상 앱오프닝 광고를 사용하지 않을 때 호출
            adWhaleMediationAppOpenAd!!.destroy()
            adWhaleMediationAppOpenAd = null
        }
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 로드 성공 시 콜백됨
    override fun onAdLoaded() {
        Log.i(TAG, ".onAdLoaded();")
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 로드 실패 시 콜백됨
    override fun onAdFailedToLoad(statusCode: Int, message: String?) {
        Log.e(TAG, ".onAdFailedToLoad(" + statusCode + ", " + message + ");")
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 표시 실패 시 콜백됨
    override fun onAdFailedToShow(statusCode: Int, message: String?) {
        Log.e(TAG, ".onAdFailedToShow(" + statusCode + ", " + message + ");")
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 클릭 시 콜백됨
    override fun onAdClicked() {
        Log.i(TAG, ".onAdClicked();")
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 닫기 시 콜백됨
    override fun onAdDismissed() {
        Log.i(TAG, ".onAdDismissed();")
    }

    // 앱 오프낭 광고 콜백 리스너: 광고 표시 성공 시 콜백됨
    override fun onAdShowed() {
        Log.i(TAG, ".onAdShowed();")
    }

    companion object {
        private const val TAG = "AppOpenLifecycleTest"
    }
}