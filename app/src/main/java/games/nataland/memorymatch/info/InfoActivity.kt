package games.nataland.memorymatch.info

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import games.nataland.memorymatch.R
import kotlinx.android.synthetic.main.activity_info.*


class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.how_to_play)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}