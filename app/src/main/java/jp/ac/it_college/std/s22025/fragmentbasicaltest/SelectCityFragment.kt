package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
            connectTimeout = 7000
            requestTimeout = 7000
            socketTimeout = 7000
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

    //位置情報を取得するためのライブラリ↓
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //位置情報の更新に関する設定情報が格納されている↓
    private lateinit var locationRequest: LocationRequest

    //位置情報が取得できた・変わった等位置情報に関するイベントが発生したときのリスナメソッドがで意義されている↓
    private lateinit var locationCallback: LocationCallback

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGrantedFineLocation =
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val isGrantedCoarseLocation =
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        //どちらかの権限が許可もらえたという場合
        if(isGrantedFineLocation || isGrantedCoarseLocation){
            requestLocationUpdates()
            return@registerForActivityResult
        }
        //結局権限の許可をもらえなかったとき とりあえずログだけだしとく
        Log.w("(;_;)", "許可がもらえませんでした")
    }

    private var latitude = 0.0
    private var longitude = 0.0

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
        binding.btSearch.setOnClickListener(::onMapSearchButtonClick)

        //位置情報取得関連
        //位置情報取得クライアントを作成
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                //lastLocation: 最新の位置情報
                result.lastLocation?.also { location ->
                    //緯度経度取る
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        }

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

    //取得開始のリクエストを投げる↓
    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
    }

    //リソースの開放
    override fun onPause() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    @UiThread
    private fun onMapSearchButtonClick(view: View) {
        lifecycleScope.launch {

            val locationUrl = "$WEATHER_INFO_URL&lat=$latitude&lon=$longitude&appid=$APP_ID"

            val result = ktorClient.get {
                url(locationUrl)
            }.body<WeatherInfo>()

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

    private fun requestLocationUpdates(){
        //true or false
        val isGrantedFineLocation = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        //true or false
        val isGrantedCoarseLocation = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        //位置情報取得の権限↑のうち、どちらか一方でも権限あれば(=trueなら)OKなので位置情報取得開始
        if (isGrantedFineLocation || isGrantedCoarseLocation){
            //権限チェック処理入れないと赤線が消えない
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                requireActivity().mainLooper
            )
            return
        }
        //ここまで来たらどの権限も無いのでリクエストm
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}