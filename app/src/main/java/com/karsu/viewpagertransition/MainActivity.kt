package com.karsu.viewpagertransition

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.karsu.transitionpager.TransitionItem
import com.karsu.viewpagertransition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sushis = listOf(
            Sushi(
                item = TransitionItem(
                    image = "https://loremflickr.com/900/1200/sushi?lock=1",
                    title = "Salmon Nigiri",
                    subtitle = "Tsukiji, Tokyo",
                    rating = 4.9f,
                ),
                price = 85,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://loremflickr.com/900/1200/sushi?lock=2",
                    title = "Dragon Roll",
                    subtitle = "Dotonbori, Osaka",
                    rating = 4.7f,
                ),
                price = 120,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://loremflickr.com/900/1200/sushi?lock=3",
                    title = "Spicy Tuna Maki",
                    subtitle = "Gion, Kyoto",
                    rating = 4.5f,
                ),
                price = 95,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://loremflickr.com/900/1200/sushi?lock=4",
                    title = "Uni Gunkan",
                    subtitle = "Sapporo, Hokkaido",
                    rating = 4.8f,
                ),
                price = 140,
            ),
            Sushi(
                item = TransitionItem(
                    image = "https://loremflickr.com/900/1200/sushi?lock=5",
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
