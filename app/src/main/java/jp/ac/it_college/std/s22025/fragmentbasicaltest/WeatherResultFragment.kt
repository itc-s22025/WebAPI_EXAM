package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.FragmentWeatherResultBinding

//internal -> 同じモジュール内からのみアクセス可能ですよ
internal const val ARG_NAME = "cityName"
internal const val ARG_CITY_DATETEXT = "cityDateText"
internal const val ARG_CITY_WEATHER_DESC = "cityWeatherDesc"
internal const val ARG_TEMP = "temp"
internal const val ARG_FEELS_LIKE = "feelsLike"
internal const val ARG_PRESS = "pressure"
internal const val ARG_HUMIDITY = "humidity"
internal const val ARG_POP = "pop"
internal const val ARG_WIND_SPEED = "windSpeed"
internal const val ARG_WIND_MAX = "windMax"
internal const val ARG_WIND_DEG = "windDeg"
internal const val ARG_ICON = "icon"


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
        val cityDateText = arguments?.getString(ARG_CITY_DATETEXT) ?: "datetext"
        val cityWeatherDesc = arguments?.getString(ARG_CITY_WEATHER_DESC) ?: "citydesc"
        val temp = arguments?.getDouble(ARG_TEMP) ?: 0
        val feelsLike = arguments?.getDouble(ARG_FEELS_LIKE) ?: 0
        val pressure = arguments?.getDouble(ARG_PRESS) ?: 0
        val humidity = arguments?.getDouble(ARG_HUMIDITY) ?: 0
        val pop = arguments?.getDouble(ARG_POP) ?: 0
        val windSpeed = arguments?.getDouble(ARG_WIND_SPEED) ?: 0
        val windMaxSpeed = arguments?.getDouble(ARG_WIND_MAX) ?: 0
        val windDeg = arguments?.getDouble(ARG_WIND_DEG) ?: 0
        val icon = arguments?.getString(ARG_ICON) ?: ""


        //ビューに値をセット
        binding.tvWeatherTelop.text = cityName
        binding.tvWeatherDateText.text = cityDateText
        binding.tvWether.text = cityWeatherDesc
        binding.tvTemp.text = "%,f".format(temp)
        binding.tvFeelsLike.text = "%,f".format(feelsLike)
        binding.tvPressure.text = "%,f".format(pressure)
        binding.tvHumidity.text = "%,f".format(humidity)
        binding.tvPop.text = "%,f".format(pop)
        binding.tvWindSpeed.text = "%,f".format(windSpeed)
        binding.tvMaxWindSpeed.text = "%,f".format(windMaxSpeed)
        binding.tvWindDeg.text = "%,f".format(windDeg)

        val imageURL = "https://openweathermap.org/img/wn/${icon}.png"
        // Glideを使って画像をロードしてImageViewに表示
                Glide.with(this)
                    .load(imageURL)
                    .into(binding.ivWeatherIcon)



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