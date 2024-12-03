package kr.co.adwhale.sample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.adwhale.sample.banner.ProgrammaticBannerMainActivity
import kr.co.adwhale.sample.banner.XmlBannerMainActivity
import kr.co.adwhale.sample.databinding.ActivitySampleMainBinding
import kr.co.adwhale.sample.interstitial.ProgrammaticInterstitialMainActivity
import kr.co.adwhale.sample.reward.ProgrammaticRewardAdMainActivity

class SampleMainActivity : AppCompatActivity() {
    private var mBinding: ActivitySampleMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySampleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etMediaUid = binding.etMediaUid
        val btnProgrammaticBanner = binding.btnProgrammaticBanner
        val btnXmlBanner = binding.btnXmlBanner
        val btnInterstitial = binding.btnInterstitial
        val btnRewardAd = binding.btnRewardAd

        etMediaUid.setText(getMetaData())
        btnProgrammaticBanner.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticBannerMainActivity::class.java))
        }

        btnXmlBanner.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, XmlBannerMainActivity::class.java))
        }

        btnInterstitial.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticInterstitialMainActivity::class.java))
        }

        btnRewardAd.setOnClickListener {
            setMetaData(etMediaUid.text.toString())
            startActivity(Intent(this, ProgrammaticRewardAdMainActivity::class.java))
        }
    }


    private fun setMetaData(value: String?) {
        if (value.isNullOrEmpty()) {
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

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}