package com.jobcoin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jobcoin.view.SignInFragment

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initTimelineFragment()
    }

    private fun initTimelineFragment() {
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.container)
        if (fragment == null) {
            fragment = SignInFragment.newInstance()
            fm.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()

        }
    }
}
