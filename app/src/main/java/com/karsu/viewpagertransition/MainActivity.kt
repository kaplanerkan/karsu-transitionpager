package com.karsu.viewpagertransition

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.LocaleListCompat
import androidx.core.util.Pair
import com.karsu.transitionpager.TransitionItem
import com.karsu.viewpagertransition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLanguageChips()
        setupPager()
    }

    private fun setupLanguageChips() {
        val currentTag = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .substringBefore('-')
            .ifEmpty { "en" }

        val checkedId = when (currentTag) {
            "tr" -> R.id.chip_lang_tr
            "de" -> R.id.chip_lang_de
            else -> R.id.chip_lang_en
        }
        binding.languageChips.check(checkedId)

        binding.chipLangEn.setOnClickListener { switchLocale("en") }
        binding.chipLangTr.setOnClickListener { switchLocale("tr") }
        binding.chipLangDe.setOnClickListener { switchLocale("de") }
    }

    private fun switchLocale(languageTag: String) {
        val current = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .substringBefore('-')
            .ifEmpty { "en" }
        if (current == languageTag) return
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag),
        )
    }

    private fun setupPager() {
        // Wikimedia Commons'tan doğrulanmış sushi fotoğrafları (Category:Sushi).
        // KarSuApp.kt'deki özel OkHttp User-Agent sayesinde Coil bu URL'leri
        // Wikimedia'nın UA politikasını bozmadan çekebiliyor.
        val sushis = listOf(
            Sushi(
                item = TransitionItem(
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Another_sushi.jpg/900px-Another_sushi.jpg",
                    title = "Salmon Nigiri",
                    subtitle = "Tsukiji, Tokyo",
                    rating = 4.9f,
                ),
                price = 85,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Colorful_sushi_lunch.jpg/900px-Colorful_sushi_lunch.jpg",
                    title = "Dragon Roll",
                    subtitle = "Dotonbori, Osaka",
                    rating = 4.7f,
                ),
                price = 120,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Eight_Different_Types_of_Sushi.jpg/900px-Eight_Different_Types_of_Sushi.jpg",
                    title = "Spicy Tuna Maki",
                    subtitle = "Gion, Kyoto",
                    rating = 4.5f,
                ),
                price = 95,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Hyousei_sasamaki.jpg/900px-Hyousei_sasamaki.jpg",
                    title = "Uni Gunkan",
                    subtitle = "Sapporo, Hokkaido",
                    rating = 4.8f,
                ),
                price = 140,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Meet-_Sushi.jpg/900px-Meet-_Sushi.jpg",
                    title = "California Roll",
                    subtitle = "Hakata, Fukuoka",
                    rating = 4.3f,
                ),
                price = 75,
            ),
        )

        binding.transitionPager.setItems(sushis.map { it.item })
        binding.transitionPager.onItemClick = { item, position, shared ->
            shared.image.transitionName = DetailActivity.TRANSITION_IMAGE

            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_IMAGE_URL, item.image.toString())
                putExtra(DetailActivity.EXTRA_TITLE, item.title)
                putExtra(DetailActivity.EXTRA_SUBTITLE, item.subtitle)
                putExtra(DetailActivity.EXTRA_RATING, item.rating)
                putExtra(DetailActivity.EXTRA_PRICE, sushis[position].price)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(shared.image, DetailActivity.TRANSITION_IMAGE),
            )
            startActivity(intent, options.toBundle())
        }
    }

    private data class Sushi(
        val item: TransitionItem,
        val price: Int,
    )
}
