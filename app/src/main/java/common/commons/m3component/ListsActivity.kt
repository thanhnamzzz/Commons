package common.commons.m3component

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.listitem.ListItemCardView
import com.google.android.material.listitem.RevealableListItem
import com.google.android.material.listitem.SwipeableListItem
import common.commons.databinding.ActivityListsBinding
import common.commons.databinding.CatListItemSwipeableViewholderBinding
import common.libs.SimpleActivity

class ListsActivity : SimpleActivity<ActivityListsBinding>(ActivityListsBinding::inflate) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		val adapter = ListsAdapter(this)
		adapter.getData()
		binding.rvList.adapter = adapter
		binding.rvList.addItemDecoration(MarginItemDecoration(15))
	}
}

private class ListsAdapter(private val context: Context) :
	RecyclerView.Adapter<ListsAdapter.ViewHolder>() {
	private var indexSelect = -1
	private val diffCallback = object : DiffUtil.ItemCallback<ItemData>() {
		override fun areItemsTheSame(
			p0: ItemData,
			p1: ItemData
		): Boolean {
			return (p0.text == p1.text)
		}

		override fun areContentsTheSame(
			p0: ItemData,
			p1: ItemData
		): Boolean {
			return p0 == p1
		}
	}

	private val differ = AsyncListDiffer(this, diffCallback)

	fun getData() {
		val listData = mutableListOf<ItemData>()
		for (i in 0..30) {
			listData.add(ItemData("List item $i", i))
		}
		differ.submitList(listData)
	}

	inner class ViewHolder(private val binding: CatListItemSwipeableViewholderBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(position: Int) {
			val item = differ.currentList[position]
			// Reset trạng thái swipe khi bind lại dữ liệu để tránh lỗi reuse ViewHolder
			binding.root.setSwipeState(SwipeableListItem.STATE_CLOSED, Gravity.START, false)
			binding.root.setSwipeState(SwipeableListItem.STATE_CLOSED, Gravity.END, false)

			changeSelect(position == indexSelect)
			binding.catListItemText.text = item.text
			binding.catListItemText2.text = item.text
			binding.catListActionAddButton.setOnClickListener {
				Toast.makeText(context, "Add | $position", Toast.LENGTH_SHORT).show()
			}
			binding.catListActionStarButton.setOnClickListener {
				Toast.makeText(context, "Start | $position", Toast.LENGTH_SHORT).show()
			}
			binding.catListActionDeleteButton.setOnClickListener {
				Toast.makeText(context, "Delete | $position", Toast.LENGTH_SHORT).show()
			}

			binding.catListItemStartIcon.setOnClickListener {
				if (binding.root.swipeState == SwipeableListItem.STATE_CLOSED) {
					binding.root.setSwipeState(
						SwipeableListItem.STATE_SWIPE_PRIMARY_ACTION,
						Gravity.START
					)
				} else {
					binding.root.setSwipeState(SwipeableListItem.STATE_CLOSED, Gravity.START)
				}
			}

			binding.catListItemEndIcon.setOnClickListener {
				if (binding.root.swipeState == SwipeableListItem.STATE_CLOSED) {
					binding.root.setSwipeState(
						SwipeableListItem.STATE_OPEN,
						Gravity.END
					)
				} else {
					binding.root.setSwipeState(SwipeableListItem.STATE_CLOSED, Gravity.END)
				}
			}

			binding.catListItemCardView.setOnClickListener {
				if (position == indexSelect) {
					val previous = indexSelect
					indexSelect = -1
					notifyItemChanged(previous, false)
				} else {
					val previous = indexSelect
					indexSelect = position
					notifyItemChanged(indexSelect, true)
					notifyItemChanged(previous, false)
				}
			}

			binding.catListItemCardView.addSwipeCallback(object : ListItemCardView.SwipeCallback(){
				override fun onSwipe(p0: Int) {}

				override fun <T> onSwipeStateChanged(
					p0: Int,
					p1: T & Any,
					p2: Int
				) where T : View?, T : RevealableListItem? {
					if (p0 == SwipeableListItem.STATE_SWIPE_PRIMARY_ACTION) {
						if (p2 == Gravity.START) {
							val position = bindingAdapterPosition
							if (position != RecyclerView.NO_POSITION) {
								val newList = differ.currentList.toMutableList()
								newList.removeAt(position)
								differ.submitList(newList)
							}
						}
					}
				}
			})
		}

		fun changeSelect(bool: Boolean) {
			binding.catListItemCardView.isChecked = bool
		}
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any?>) {
		if (payloads.isEmpty())
			super.onBindViewHolder(holder, position, payloads)
		else {
			for (payload in payloads) {
				if (payload is Boolean) {
					holder.changeSelect(payload)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		p0: ViewGroup,
		p1: Int
	): ViewHolder {
		val binding =
			CatListItemSwipeableViewholderBinding.inflate(LayoutInflater.from(context), p0, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
		p0.bind(p1)
	}

	override fun getItemCount(): Int = differ.currentList.size
}

class MarginItemDecoration(private val itemMargin: Int = 5) : ItemDecoration() {
	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val position = parent.getChildAdapterPosition(view)
		if (position != state.itemCount - 1) {
			outRect.bottom = itemMargin
		}
	}
}