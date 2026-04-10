package com.karsu.viewpagertransition

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
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
                image = "https://picsum.photos/id/1018/900/1200",
                title = "Mountain Lake",
                subtitle = "Bergen, Norway",
                rating = 4.5f,
            ),
            TransitionItem(
                image = "https://picsum.photos/id/1015/900/1200",
                title = "River Valley",
                subtitle = "Colorado, USA",
                rating = 4.8f,
            ),
            TransitionItem(
                image = "https://picsum.photos/id/1019/900/1200",
                title = "Forest Path",
                subtitle = "Bavaria, Germany",
                rating = 4.2f,
            ),
            TransitionItem(
                image = "https://picsum.photos/id/1039/900/1200",
                title = "Starry Sky",
                subtitle = "Reykjavik, Iceland",
                rating = 4.9f,
            ),
            TransitionItem(
                image = "https://picsum.photos/id/1043/900/1200",
                title = "Coastal View",
                subtitle = "Amalfi, Italy",
                rating = 4.6f,
            ),
        )

        binding.transitionPager.setItems(items)
        binding.transitionPager.onItemClick = { item, _, shared ->
            ViewCompat.setTransitionName(shared.image, DetailActivity.TRANSITION_IMAGE)

            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_IMAGE_URL, item.image.toString())
                putExtra(DetailActivity.EXTRA_TITLE, item.title)
                putExtra(DetailActivity.EXTRA_SUBTITLE, item.subtitle)
                putExtra(DetailActivity.EXTRA_RATING, item.rating)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair.create(shared.image as android.view.View, DetailActivity.TRANSITION_IMAGE),
            )
            startActivity(intent, options.toBundle())
        }
    }
}
