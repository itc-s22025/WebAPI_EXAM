package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.FragmentWeatherResultBinding

//internal -> 同じモジュール内からのみアクセス可能ですよ
internal const val ARG_NAME = "cityName"
class WeatherResultFragment : Fragment() {
    private var _binding: FragmentWeatherResultBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //fragment_weather_result.xmlと紐付け？
        _binding = FragmentWeatherResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // arguments プロパティ経由で外部からパラメータを受け取る
        //つまりこのcityNameには他Fragmentから受け取ったデータが入っている
        val cityName = arguments?.getString(ARG_NAME) ?: ""

        //ビューに値をセット
        binding.tvWeatherTelop.text = cityName

        // ボタンのイベントリスナを設定
        binding.btBack.setOnClickListener(::onBackButtonClick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 戻るボタンが押されたときの処理。フラグメントの履歴を遡る処理。
     */
    private fun onBackButtonClick(v: View) {
        parentFragmentManager.popBackStack()
    }
}