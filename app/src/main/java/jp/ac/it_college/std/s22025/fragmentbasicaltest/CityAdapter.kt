package jp.ac.it_college.std.s22025.fragmentbasicaltest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.ac.it_college.std.s22025.fragmentbasicaltest.databinding.CityRowBinding

//つまりこのCityAdapterはrvCityListにcity_row.xmlのcityNameを表示させるためのアダプターってこと
//ていうかもしかしたらcity_row.xmlとCityDataを紐付けているやつかも
class CityAdapter(private val data: List<City>) :
    //RecyclerView.Adapter :リスト形式のデータを効率に表示するためのアダプタ
    RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    //city_row.xmlにバインドしますよ的な?
    class ViewHolder(val binding: CityRowBinding) : RecyclerView.ViewHolder(binding.root)

    //onCreateViewHolder -> ViewHolderオブジェクト(各要素)を作成して返すメソッド
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        //ViewHolder(...):CityRowBindingから生成されたViewオブジェクトを引数に取り、ViewHolderオブジェクトを作成

        //CityRowBinding.inflate(...):CityRowBindingクラスを生成
        // そのクラス内で定義されたビュー要素を含むViewオブジェクトを作成
        // 第一引数にはLayoutInflater
        // 第二引数にはレイアウトの親であるViewGroup
        // 第三引数にはViewがアタッチされるかどうかを示すboolean値が渡される
        // これにより、CityRowBindingが生成されたときに、その中のrootプロパティがアタッチされていないViewオブジェクトを得ることができます。

        //LayoutInflater.from(parent.context) -> .xmlファイルからViewオブジェクトを作成するためのLayoutInflater を親のContextから取得
        ViewHolder(CityRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    //Listの要素数をカウント
    //インデックスの確認みたいな
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //city_row.xml内のcityNameというtextビューに、指定されたpositionに対応するデータの名前をバインド
        holder.binding.cityName.text = data[position].name
    }
}