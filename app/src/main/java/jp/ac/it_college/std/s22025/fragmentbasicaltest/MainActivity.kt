package jp.ac.it_college.std.s22025.fragmentbasicaltest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //MainActivityには特別なことは記載してないけどfragment_select_city.xmlの中身が写った
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // イベント識別名、LifecycleOwner,イベントリスナ を渡して待ち受ける
        supportFragmentManager
            .setFragmentResultListener(REQUEST_SELECTED_CITY, this, ::onSelectedCity)
    }

    //ユーザーが都市を選択したときに呼び出される関数
    private fun onSelectedCity(requestKey: String, bundle: Bundle){
        Log.i("SELECTED_CITYY", "requestKey: ${requestKey}, bundle: ${bundle}.")
        // SelectCityFragment から受け取ったデータを詰め直して
        // WeatherResultFragment を表示させる。
        //ragment のトランザクションを開始 ... Fragment トランザクションは、Fragment の追加、削除、置換などの変更を行うために使用される
        supportFragmentManager.commit {
            //setReorderingAllowed -> Fragment の再配置が許可されるように設定 -> Fragment の移動や置換がよりスムーズに行われる
            setReorderingAllowed(true)
            //ユーザーが "バック" ボタンを押したときに前の状態に戻ることができるようになる
            addToBackStack("only List")
            //指定されたコンテナ内の特定の Fragment を新しい Fragment で置き換える
            //WeatherResultFragment に対して bundleOf を使用してデータを渡している -> WRFragmentにSelectCityFragment内のRESULT_NAMEを渡している
            replace(
                R.id.fragmentMainContainer,WeatherResultFragment::class.java,
                bundleOf(
                    ARG_NAME to bundle.getString(RESULT_NAME, "")
                )
            )
        }
    }


}