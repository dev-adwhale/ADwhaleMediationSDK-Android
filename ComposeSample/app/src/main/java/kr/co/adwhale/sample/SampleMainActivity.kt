package kr.co.adwhale.sample

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kr.co.adwhale.sample.appopen.ComposeAppOpenMainActivity
import kr.co.adwhale.sample.banner.ComposeBannerMainActivity
import kr.co.adwhale.sample.interstitial.ComposeInterstitialMainActivity
import kr.co.adwhale.sample.nativead.ComposeCustomBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.ComposeStyledTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.ComposeTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.reward.ComposeRewardAdMainActivity
import kr.co.adwhale.sample.transition.TransitionTestMainActivity
import net.adwhale.sdk.mediation.ads.ADWHALE_POPUP_AD_CLOSE_REASON
import net.adwhale.sdk.mediation.ads.AdWhaleMediationAds
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAd
import net.adwhale.sdk.mediation.ads.AdWhaleMediationExitPopupAdListener
import net.adwhale.sdk.utils.AdWhaleLog

class SampleMainActivity : AppCompatActivity() {
    private val LOG_TAG: String = SampleMainActivity::class.java.getSimpleName()

    private var adWhaleMediationExitPopupAd: AdWhaleMediationExitPopupAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentMetaData = getMetaData()

        AdWhaleLog.setLogLevel(AdWhaleLog.LogLevel.Error)

        AdWhaleMediationAds.init(this) { statusCode, message ->
            Log.i(
                LOG_TAG,
                ".onInitComplete(" + statusCode + ", " + message + ")"
            )
            loadExitPopupAd()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // SDK init 이전에는 ad 객체가 없을 수 있으므로, 이 경우엔 기본 종료 팝업을 띄웁니다.
                    if (adWhaleMediationExitPopupAd == null) {
                        showDefaultClosePopup()
                    } else {
                        showExitPopupAd()
                    }
                }
            }
        )

        // AppCompatActivity에서는 ComponentActivity 전용 setContent() 확장을 바로 쓰기 어렵기 때문에,
        // ComposeView를 통해 화면을 구성합니다.
        setContentView(
            ComposeView(this).apply {
                setContent {
                    MaterialTheme {
                        SampleMainScreen(
                            initialMediaUid = currentMetaData,
                            onMediaUidChanged = { setMetaData(it) },
                            onBanner = {
                                startActivity(Intent(this@SampleMainActivity, ComposeBannerMainActivity::class.java))
                            },
                            onMoveToTransitionAd = {
                                startActivity(Intent(this@SampleMainActivity, TransitionTestMainActivity::class.java))
                            },
                            onInterstitial = {
                                startActivity(Intent(this@SampleMainActivity, ComposeInterstitialMainActivity::class.java))
                            },
                            onRewardAd = {
                                startActivity(Intent(this@SampleMainActivity, ComposeRewardAdMainActivity::class.java))
                            },
                            onAppOpenAd = {
                                startActivity(Intent(this@SampleMainActivity, ComposeAppOpenMainActivity::class.java))
                            },
                            onNativeCustomBinding = {
                                startActivity(Intent(this@SampleMainActivity, ComposeCustomBindingNativeMainActivity::class.java))
                            },
                            onNativeTemplateBinding = {
                                startActivity(Intent(this@SampleMainActivity, ComposeTemplateBindingNativeMainActivity::class.java))
                            },
                            onNativeTemplateBindingWithStyle = {
                                startActivity(Intent(this@SampleMainActivity, ComposeStyledTemplateBindingNativeMainActivity::class.java))
                            }
                        )
                    }
                }
            }
        )
    }

    private fun setMetaData(value: String) {
        if (value.isEmpty()) return
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            appInfo.metaData.putString("net.adwhale.sdk.mediation.PUBLISHER_UID", value)
        } catch (e: Exception) {
            throw UnsupportedOperationException(
                "Publisher Uid value is required. Please check <meta-data> in AndroidManifest.xml."
            )
        }
    }

    private fun getMetaData(): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            appInfo.metaData.getString("net.adwhale.sdk.mediation.PUBLISHER_UID", "")
        } catch (e: Exception) {
            ""
        }
    }

    private fun loadExitPopupAd() {
        adWhaleMediationExitPopupAd =
            AdWhaleMediationExitPopupAd(
                resources.getString(kr.co.adwhale.sample.R.string.exit_popup_placement_uid)
            )

        adWhaleMediationExitPopupAd?.setCustomizeButtonText("테스트 취소", "테스트 종료")
        adWhaleMediationExitPopupAd?.setCustomDescription("테스트 문구")
        adWhaleMediationExitPopupAd?.setAdWhaleMediationExitPopupAdListener(object :
            AdWhaleMediationExitPopupAdListener {
            override fun onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded")
            }

            override fun onAdLoadFailed(statusCode: Int, message: String?) {
                Log.d(
                    LOG_TAG,
                    "onAdLoadFailed(" + statusCode + ", " + message + ");"
                )
            }

            override fun onAdShowed() {
                Log.d(LOG_TAG, "onAdShowed")
            }

            override fun onAdClicked() {
                Log.d(LOG_TAG, "onAdClicked")
            }

            override fun onAdShowFailed(statusCode: Int, message: String?) {
                Log.d(
                    LOG_TAG,
                    "onAdShowFailed(" + statusCode + ", " + message + ");"
                )
            }

            override fun onAdClosed(adwhaleExitPopupAdCloseReason: ADWHALE_POPUP_AD_CLOSE_REASON) {
                Log.d(
                    LOG_TAG,
                    "onAdClosed(" + adwhaleExitPopupAdCloseReason.getCloseReasonTypeString() + ");"
                )
            }
        })
        adWhaleMediationExitPopupAd?.loadAd()
    }

    override fun onResume() {
        adWhaleMediationExitPopupAd?.resume(this) // 필수 호출
        super.onResume()
    }

    private fun showExitPopupAd() {
        adWhaleMediationExitPopupAd?.showAd(
            this@SampleMainActivity,
            supportFragmentManager
        )
    }

    private fun showDefaultClosePopup() {
        AlertDialog.Builder(this).setTitle("").setMessage("종료 하시겠습니까?")
            .setPositiveButton("예", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
            .setNegativeButton("아니요", null)
            .show()
    }

    override fun onDestroy() {
        Log.i(LOG_TAG, ".onDestroy()")
        adWhaleMediationExitPopupAd?.destroy()
        super.onDestroy()
    }

    @Composable
    fun SampleMainScreen(
        initialMediaUid: String,
        onMediaUidChanged: (String) -> Unit,
        onBanner: () -> Unit,
        onMoveToTransitionAd: () -> Unit,
        onInterstitial: () -> Unit,
        onRewardAd: () -> Unit,
        onAppOpenAd: () -> Unit,
        onNativeCustomBinding: () -> Unit,
        onNativeTemplateBinding: () -> Unit,
        onNativeTemplateBindingWithStyle: () -> Unit
    ) {
        var mediaUid by remember { mutableStateOf(TextFieldValue(initialMediaUid)) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(text = "1. Media Uid 값 입력:", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(6.dp))

            TextField(
                value = mediaUid,
                onValueChange = {
                    mediaUid = it
                    onMediaUidChanged(it.text)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("media uid 를 입력하세요.") }
            )

            Spacer(Modifier.height(20.dp))

            Button(onClick = onBanner, modifier = Modifier.fillMaxWidth()) {
                Text("banner")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onMoveToTransitionAd, modifier = Modifier.fillMaxWidth()) {
                Text("앱 전환 광고 테스트")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onInterstitial, modifier = Modifier.fillMaxWidth()) {
                Text("interstitial")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onRewardAd, modifier = Modifier.fillMaxWidth()) {
                Text("Reward Ad")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onAppOpenAd, modifier = Modifier.fillMaxWidth()) {
                Text("App Open Ad")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onNativeCustomBinding, modifier = Modifier.fillMaxWidth()) {
                Text("Native Ad (for Custom Binding)")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onNativeTemplateBinding, modifier = Modifier.fillMaxWidth()) {
                Text("Native Ad (for Template Binding)")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = onNativeTemplateBindingWithStyle, modifier = Modifier.fillMaxWidth()) {
                Text("Native Template Binding Ad (with Style)")
            }
        }
    }

}
