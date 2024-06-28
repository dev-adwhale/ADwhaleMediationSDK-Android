package kr.co.adwhale.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SampleMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_main)

        var btnProgrammaticBanner = findViewById<Button>(R.id.btnProgrammaticBanner)
        var btnXmlBanner = findViewById<Button>(R.id.btnXmlBanner)
        var btnInterstitial = findViewById<Button>(R.id.btnInterstitial)

        btnProgrammaticBanner.setOnClickListener(View.OnClickListener { view: View? ->
            startActivity(Intent(this, ProgrammaticBannerMainActivity::class.java))
        })

        btnXmlBanner.setOnClickListener(View.OnClickListener { view: View? ->
            startActivity(Intent(this, XmlBannerMainActivity::class.java))
        })

        btnInterstitial.setOnClickListener(View.OnClickListener { view: View? ->
            startActivity(Intent(this, ProgrammaticInterstitialMainActivity::class.java))
        })
    }
}