package com.karsu.viewpagertransition

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.karsu.viewpagertransition.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.detailImage.transitionName = TRANSITION_IMAGE

        val url = intent.getStringExtra(EXTRA_IMAGE_URL)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val subtitle = intent.getStringExtra(EXTRA_SUBTITLE)
        val rating = intent.getFloatExtra(EXTRA_RATING, 0f)

        binding.detailImage.load(url) { crossfade(true) }
        binding.detailTitle.text = title.orEmpty()
        binding.detailSubtitle.text = subtitle.orEmpty()
        binding.detailRating.rating = rating

        binding.detailClose.setOnClickListener { finishAfterTransition() }

        onBackPressedDispatcher.addCallback(this) {
            finishAfterTransition()
        }
    }

    companion object {
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SUBTITLE = "extra_subtitle"
        const val EXTRA_RATING = "extra_rating"
        const val TRANSITION_IMAGE = "transition_image"
    }
}
