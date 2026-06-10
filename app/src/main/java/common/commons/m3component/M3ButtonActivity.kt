package common.commons.m3component

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.R
import common.commons.databinding.ActivityM3ButtonBinding
import common.commons.databinding.LayoutItemSelectFavouriteBinding
import common.libs.SimpleActivity

class M3ButtonActivity : SimpleActivity<ActivityM3ButtonBinding>(ActivityM3ButtonBinding::inflate) {
	private val listGenre = mutableListOf<GenreObject>()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		listGenre.apply {
			add(GenreObject("\uD83C\uDFB5 Classical", keySelect = "classical"))
			add(GenreObject("\uD83C\uDFB9 Jazz", keySelect = "jazz"))
			add(GenreObject("\uD83C\uDFB6 Blues ", keySelect = "blues"))
			add(GenreObject("\uD83C\uDFBC Pop ", keySelect = "pop"))
			add(GenreObject("\uD83C\uDFB8 Rock", keySelect = "rock"))
			add(GenreObject("\uD83C\uDFB5 Ballad", keySelect = "ballad"))
			add(GenreObject("\uD83C\uDFA4 R&B / Soul", keySelect = "rb_soul"))
			add(GenreObject("\uD83C\uDFAE Game / Anime add(Music)", keySelect = "game_anime"))
			add(GenreObject("\uD83C\uDFAC Soundtrack ", keySelect = "sound_track"))
		}

		listGenre.forEach { musicGenre ->
			val bd = LayoutItemSelectFavouriteBinding.inflate(
				LayoutInflater.from(this),
				binding.layoutFavorite,
				false
			)
			bd.tvMusicGenre.text = musicGenre.nameGenre
			bd.tvMusicGenre.setOnClickListener {
				musicGenre.isSelect = !musicGenre.isSelect
				if (musicGenre.isSelect) {
					bd.tvMusicGenre.setTextColor(ContextCompat.getColor(this, R.color.error_color))
				} else {
					bd.tvMusicGenre.setTextColor(ContextCompat.getColor(this, R.color.black))
				}
			}
			binding.layoutFavorite.addView(bd.root)
		}
	}
}

data class GenreObject(
	val nameGenre: String,
	var isSelect: Boolean = false,
	val keySelect: String
)