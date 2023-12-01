package jp.ac.it_college.std.s22025.fragmentbasicaltest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //MainActivityには特別なことは記載してないけどfragment_select_city.xmlの中身が写った
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}