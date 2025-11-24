package kr.co.adwhale.sample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kr.co.adwhale.sample.appopen.ComposeAppOpenMainActivity
import kr.co.adwhale.sample.banner.ComposeBannerMainActivity
import kr.co.adwhale.sample.interstitial.ComposeInterstitialMainActivity
import kr.co.adwhale.sample.nativead.ComposeCustomBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.ComposeStyledTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.nativead.ComposeTemplateBindingNativeMainActivity
import kr.co.adwhale.sample.reward.ComposeRewardAdMainActivity

class SampleMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentMetaData = getMetaData()

        setContent {
            MaterialTheme {
                SampleMainScreen(
                    initialMediaUid = currentMetaData,
                    onMediaUidChanged = { setMetaData(it) },
                    onBanner = {
                        startActivity(Intent(this, ComposeBannerMainActivity::class.java))
                    },
                    onInterstitial = {
                        startActivity(Intent(this, ComposeInterstitialMainActivity::class.java))
                    },
                    onRewardAd = {
                        startActivity(Intent(this, ComposeRewardAdMainActivity::class.java))
                    },
                    onAppOpenAd = {
                        startActivity(Intent(this, ComposeAppOpenMainActivity::class.java))
                    },
                    onNativeCustomBinding = {
                        startActivity(Intent(this, ComposeCustomBindingNativeMainActivity::class.java))
                    },
                    onNativeTemplateBinding = {
                        startActivity(Intent(this, ComposeTemplateBindingNativeMainActivity::class.java))
                    },
                    onNativeTemplateBindingWithStyle = {
                        startActivity(Intent(this, ComposeStyledTemplateBindingNativeMainActivity::class.java))
                    }
                )
            }
        }
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

    @Composable
    fun SampleMainScreen(
        initialMediaUid: String,
        onMediaUidChanged: (String) -> Unit,
        onBanner: () -> Unit,
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
