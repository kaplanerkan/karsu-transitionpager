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

        val items = listOf(
            TransitionItem(
                image = "https://foodish-api.com/images/sushi/sushi1.jpg",
                title = "Salmon Nigiri",
                subtitle = "Tsukiji, Tokyo",
                rating = 4.9f,
            ),
            TransitionItem(
                image = "https://foodish-api.com/images/sushi/sushi2.jpg",
                title = "Dragon Roll",
                subtitle = "Dotonbori, Osaka",
                rating = 4.7f,
            ),
            TransitionItem(
                image = "https://foodish-api.com/images/sushi/sushi3.jpg",
                title = "Spicy Tuna Maki",
                subtitle = "Gion, Kyoto",
                rating = 4.5f,
            ),
            TransitionItem(
                image = "https://foodish-api.com/images/sushi/sushi4.jpg",
                title = "Uni Gunkan",
                subtitle = "Sapporo, Hokkaido",
                rating = 4.8f,
            ),
            TransitionItem(
                image = "https://foodish-api.com/images/sushi/sushi5.jpg",
                title = "California Roll",
                subtitle = "Hakata, Fukuoka",
                rating = 4.3f,
            ),
        )

        binding.transitionPager.setItems(items)
        binding.transitionPager.onItemClick = { item, _, shared ->
            shared.image.transitionName = DetailActivity.TRANSITION_IMAGE

            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_IMAGE_URL, item.image.toString())
                putExtra(DetailActivity.EXTRA_TITLE, item.title)
                putExtra(DetailActivity.EXTRA_SUBTITLE, item.subtitle)
                putExtra(DetailActivity.EXTRA_RATING, item.rating)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(shared.image, DetailActivity.TRANSITION_IMAGE),
            )
            startActivity(intent, options.toBundle())
        }
    }
}
