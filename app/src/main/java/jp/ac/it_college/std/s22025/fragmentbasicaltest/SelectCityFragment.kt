package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.FragmentSelectCityBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import jp.ac.it_college.std.s22025.fragmentbasicaltest.model.WeatherInfo

//KtorClient
private val ktorClient = HttpClient(CIO) {
    engine {
        endpoint {
            connectTimeout = 5000
            requestTimeout = 5000
            socketTimeout = 5000
        }
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }
        )
    }
}

/** Fragment Result API を通じて Activity へデータを返すためのイベント名 */
internal const val REQUEST_SELECTED_CITY = "selectedCity"

/** Fragment Result API を通じて Activity へメニュー名を返すための識別子 */
internal const val RESULT_NAME = "cityName"

/** Fragment Result API を通じて Activity へデータを返すためのイベント名 */
/* タブレットで用いる*/
internal const val REQUEST_BACK_CITY = "backCity"


internal const val GAINED_CITY_NAME = "gainedCityName"
internal const val GAINED_CITY_DATETEXT = "gainedCityDateText"
internal const val GAINED_CITY_WEATHER_DESC = "gainedCityWeatherDesc"
internal const val GAINED_TEMP = "gainedTemp"
internal const val GAINED_FEELS_LIKE = "gainedFeelsLike"
internal const val GAINED_PRESS = "pressure"
internal const val GAINED_HUMIDITY = "humidity"
internal const val GAINED_POP = "pop"
internal const val GAINED_WIND_SPEED = "windSpeed"
internal const val GAINED_WIND_MAX = "windMax"
internal const val GAINED_WIND_DEG = "windDeg"


//多分ここの内部でrvCityListにcity_row.xml内のcityNameをアプラいしてる
class SelectCityFragment : Fragment() {

    //インスタンスを生成しないオブジェクト
    companion object {
        private const val WEATHER_INFO_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lang=ja"
        private const val IMAGE_URL = "https://openweathermap.org/img/wn/"
        private const val IMAGE_FORMAT = ".png"
        private const val APP_ID = BuildConfig.APP_ID
    }

    // Bindingクラスのインスタンスを入れておくプロパティ(Nullable)
    // Bindingクラス->レイアウトファイルの要素にアクセスするための自動生成されたクラス
    private var _binding: FragmentSelectCityBinding? = null


    //Activityのときと同じようにbindingを使うための工夫
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //FragmentSelectCityBindingクラスのinflateメソッドを使用して、このフラグメント(SelectCityFragment)のレイアウト(fragment_select_city)をインフレート
        // Inflateメソッド->レイアウトのXMLファイルからビューバインディングオブジェクトを生成
        _binding =  FragmentSelectCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvCityList.apply {
            //CityAdapterクラスの新しいインスタンスを作成、その際に都市データのリスト(CityクラスのcityList)を渡している
            adapter = CityAdapter(cityList){
                getWeatherInfo(it.q)
            }

            //リストの要素を垂直方向に配置するための設定
            val manager = LinearLayoutManager(context)
            //リストの要素が垂直方向に配置される
            layoutManager = manager
            //区切り線が追加されて見やすくなるだけ
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }
    }

    @UiThread
    private fun getWeatherInfo(q: String){
        lifecycleScope.launch {
            //データ取得
            val siteUrl = "$WEATHER_INFO_URL&q=$q&appid=$APP_ID"

            //ktorClient.get{} ->GETリクエストを行うことを定義
            val result = ktorClient.get {
                //url() ->URLを設定　してリクエスト
                url(siteUrl)
                //.body ->取得にトライ
            }.body<WeatherInfo>()

            //parentFragmentManager -> 親フラグメントのフラグメントマネージャーを通じて、フラグメント間でデータをやりとりするためのメソッド
            //Fragment Result Api内のメソッド
            //.setFragmentResult() -> 特に、子フラグメントが親フラグメントに対して何らかの結果を通知する必要がある場合に用いる
            parentFragmentManager.setFragmentResult(
                //bundleOf() -> キーがRESULT_NAME, 値がit.nameのBundleオブジェクトを作成
                REQUEST_SELECTED_CITY, bundleOf(
                    GAINED_CITY_NAME to result.cityInfo.cityName,
                    GAINED_CITY_DATETEXT to result.hourlyList[0].dateText,
                    GAINED_CITY_WEATHER_DESC to result.hourlyList[0].weather[0].description,
                    GAINED_TEMP to result.hourlyList[0].mainContents.temperature -273,
                    GAINED_FEELS_LIKE to result.hourlyList[0].mainContents.feelsLike -273,
                    GAINED_PRESS to result.hourlyList[0].mainContents.pressure,
                    GAINED_HUMIDITY to result.hourlyList[0].mainContents.humidity,
                    GAINED_POP to result.hourlyList[0].rainPer*100,
                    GAINED_WIND_SPEED to result.hourlyList[0].wind.speed,
                    GAINED_WIND_MAX to result.hourlyList[0].wind.gust,
                    GAINED_WIND_DEG to result.hourlyList[0].wind.windDegrees
                    )
            )
            Log.d("TAG", "DateText: ${result.hourlyList[0].dateText}")




            // 取得したデータを UI に反映
//            result.run {
//                binding.tvWeatherTelop.text = getString(R.string.tv_telop, cityInfo.cityName)
//                binding.tvWeatherDesc.text = getString(R.string.tv_desc,hourlyList[0].dateText ,hourlyList[0].weather[0].description)
//                binding.tvWetherMore.text = getString(
//                    R.string.tv_more,
//                    hourlyList[0].mainContents.temperature -273,
//                    hourlyList[0].mainContents.feelsLike -273,
//                    hourlyList[0].mainContents.pressure,
//                    hourlyList[0].mainContents.humidity,
//                    hourlyList[0].rainPer,
//                    hourlyList[0].wind.speed,
//                    hourlyList[0].wind.gust,
//                    hourlyList[0].wind.windDegrees
//                )
//
//                // 天気アイコンのURL
//                val iconUrl = "$IMAGE_URL${hourlyList[0].weather[0].icon}$IMAGE_FORMAT"
//
//                // Glideを使って画像をロードしてImageViewに表示
//                Glide.with(this@MainActivity)
//                    .load(iconUrl)
//                    .into(binding.ivWeatherIcon)
//            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}