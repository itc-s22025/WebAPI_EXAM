package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.FragmentSelectCityBinding


//多分ここの内部でrvCityListにcity_row.xml内のcityNameをアプラいしてる
class SelectCityFragment : Fragment() {
    // Bindingクラスのインスタンスを入れておくプロパティ(Nullable)
    // Bindingクラス->レイアウトファイルの要素にアクセスするための自動生成されたクラス
    private var _binding: FragmentSelectCityBinding? = null

    //Activityのときと同じようにbindingを使うための工夫
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //FragmentSelectCityBindingクラスのinflateメソッドを使用して、このフラグメント(SelectCityFragment)のレイアウトをインフレート
        // Inflateメソッド->レイアウトのXMLファイルからビューバインディングオブジェクトを生成
        _binding =  FragmentSelectCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvCityList.apply {
            //CityAdapterクラスの新しいインスタンスを作成、その際に都市データのリスト(CityクラスのcityList)を渡している
            adapter = CityAdapter(cityList)
            //リストの要素を垂直方向に配置するための設定
            val manager = LinearLayoutManager(context)
            //リストの要素が垂直方向に配置される
            layoutManager = manager
            //区切り線が追加されて見やすくなるだけ
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}