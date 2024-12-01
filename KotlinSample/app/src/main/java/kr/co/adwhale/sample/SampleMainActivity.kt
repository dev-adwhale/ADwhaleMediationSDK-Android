package kr.co.adwhale.sample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.banner.ProgrammaticBannerMainActivity
import kr.co.adwhale.sample.banner.XmlBannerMainActivity
import kr.co.adwhale.sample.interstitial.ProgrammaticInterstitialMainActivity
import kr.co.adwhale.sample.reward.ProgrammaticRewardAdMainActivity

class SampleMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_main)

        val etMediaUid = findViewById<EditText>(R.id.etMediaUid)
        val btnProgrammaticBanner = findViewById<Button>(R.id.btnProgrammaticBanner)
        val btnXmlBanner = findViewById<Button>(R.id.btnXmlBanner)
        val btnInterstitial = findViewById<Button>(R.id.btnInterstitial)
        val btnRewardAd = findViewById<Button>(R.id.btnRewardAd)

        btnProgrammaticBanner.setOnClickListener { view: View? ->
            setMetaData(etMediaUid.text.toString())
            Log.e("meta:", getMetaData()!!)
            val intent = Intent(this, ProgrammaticBannerMainActivity::class.java)
            startActivity(intent)
        }

        btnXmlBanner.setOnClickListener { view: View? ->
            setMetaData(etMediaUid.text.toString())
            Log.e("meta:", getMetaData()!!)
            val intent = Intent(this, XmlBannerMainActivity::class.java)
            startActivity(intent)
        }

        btnInterstitial.setOnClickListener { view: View? ->
            setMetaData(etMediaUid.text.toString())
            Log.e("meta:", getMetaData()!!)
            val intent = Intent(this, ProgrammaticInterstitialMainActivity::class.java)
            startActivity(intent)
        }

        btnRewardAd.setOnClickListener { view: View? ->
            setMetaData(etMediaUid.text.toString())
            Log.e("meta:", getMetaData()!!)
            val intent = Intent(this, ProgrammaticRewardAdMainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setMetaData(value: String?) {
        if (value == null || value.isEmpty()) {
            return
        }
        try {
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 세팅.
            val applicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
            bundle.putString("net.adwhale.sdk.mediation.PUBLISHER_UID", value)
        } catch (e: PackageManager.NameNotFoundException) {
            throw UnsupportedOperationException("Publisher Uid value is required. Please check <meta-data> in AndroidManifest.xml.")
        }
    }

    private fun getMetaData(): String? {
        return try {
            // net.adwhale.sdk.mediation.PUBLISHER_UID를 name 속성값으로 갖는 <meta-data> value를 가져온다.
            val applicationInfo = applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
            bundle["net.adwhale.sdk.mediation.PUBLISHER_UID"].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
}