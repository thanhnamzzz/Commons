package common.commons.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import common.commons.R

class ViewAdapter(private val context: Context) :
	RecyclerView.Adapter<ViewAdapter.ViewHolder>() {
	private val mListItem = mutableListOf(
		ViewObject("name1", "text1"),
		ViewObject("name2", "text2"),
		ViewObject("name3", "text3"),
		ViewObject("name4", "text4"),
		ViewObject("name5", "text5"),
		ViewObject("name6", "text6"),
		ViewObject("name7", "text7"),
		ViewObject("name8", "text8"),
		ViewObject("name9", "text9"),
		ViewObject("name10", "text10"),
		ViewObject("name11", "text11"),
		ViewObject("name12", "text12"),
		ViewObject("name13", "text13"),
		ViewObject("name14", "text14"),
		ViewObject("name15", "text15"),
		ViewObject("name16", "text16"),
		ViewObject("name17", "text17"),
		ViewObject("name18", "text18"),
		ViewObject("name19", "text19"),
		ViewObject("name20", "text20"),
		ViewObject("name21", "text21"),
		ViewObject("name22", "text22"),
		ViewObject("name23", "text23"),
		ViewObject("name24", "text24"),
		ViewObject("name25", "text25"),
		ViewObject("name26", "text26"),
		ViewObject("name27", "text27"),
		ViewObject("name28", "text28"),
		ViewObject("name29", "text29"),
		ViewObject("name30", "text30"),
	)

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val tvName: TextView = itemView.findViewById(R.id.tv_name)
		val tvName1: TextView = itemView.findViewById(R.id.tv_name_1)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount(): Int {
		return mListItem.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val viewObject = mListItem[position]
		viewObject.let {
			holder.tvName.text = it.textView
			holder.tvName1.text = it.textView1
		}
	}
}