package com.karsu.viewpagertransition

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.karsu.viewpagertransition.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var basePrice: Int = 0
    private var quantity: Int = 1

    // Seçilen ekstraların ek fiyatları
    private val extras = mutableMapOf(
        "ginger" to 0,
        "mayo" to 0,
        "tempura" to 0,
    )

    private var productTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen: statusbar + navigationbar gizle, hero image edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.detailImage.transitionName = TRANSITION_IMAGE

        val url = intent.getStringExtra(EXTRA_IMAGE_URL)
        productTitle = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val subtitle = intent.getStringExtra(EXTRA_SUBTITLE).orEmpty()
        val rating = intent.getFloatExtra(EXTRA_RATING, 0f)
        basePrice = intent.getIntExtra(EXTRA_PRICE, 0)

        binding.detailImage.load(url) { crossfade(true) }
        binding.detailTitle.text = productTitle
        binding.detailSubtitle.text = subtitle
        binding.detailRating.rating = rating
        binding.detailBasePrice.text = formatPrice(basePrice)

        setupQuantity()
        setupExtras()
        setupCloseAndBack()
        updateAddToCartButton()
    }

    private fun setupQuantity() {
        binding.tvQuantity.text = quantity.toString()
        binding.btnPlus.setOnClickListener {
            quantity = (quantity + 1).coerceAtMost(20)
            binding.tvQuantity.text = quantity.toString()
            updateAddToCartButton()
        }
        binding.btnMinus.setOnClickListener {
            quantity = (quantity - 1).coerceAtLeast(1)
            binding.tvQuantity.text = quantity.toString()
            updateAddToCartButton()
        }
    }

    private fun setupExtras() {
        binding.chipExtraGinger.setOnCheckedChangeListener { _, isChecked ->
            extras["ginger"] = if (isChecked) PRICE_GINGER else 0
            updateAddToCartButton()
        }
        binding.chipExtraMayo.setOnCheckedChangeListener { _, isChecked ->
            extras["mayo"] = if (isChecked) PRICE_MAYO else 0
            updateAddToCartButton()
        }
        binding.chipExtraTempura.setOnCheckedChangeListener { _, isChecked ->
            extras["tempura"] = if (isChecked) PRICE_TEMPURA else 0
            updateAddToCartButton()
        }

        binding.btnAddToCart.setOnClickListener {
            val total = calculateTotal()
            Snackbar.make(
                binding.root,
                getString(R.string.added_to_cart_format, quantity, productTitle, total),
                Snackbar.LENGTH_SHORT,
            ).show()
        }
    }

    private fun setupCloseAndBack() {
        binding.detailClose.setOnClickListener { finishAfterTransition() }
        onBackPressedDispatcher.addCallback(this) { finishAfterTransition() }
    }

    private fun calculateTotal(): Int {
        val extrasTotal = extras.values.sum()
        return (basePrice + extrasTotal) * quantity
    }

    private fun updateAddToCartButton() {
        binding.btnAddToCart.text = getString(R.string.add_to_cart_format, calculateTotal())
    }

    private fun formatPrice(amount: Int): String = "₺$amount"

    companion object {
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SUBTITLE = "extra_subtitle"
        const val EXTRA_RATING = "extra_rating"
        const val EXTRA_PRICE = "extra_price"
        const val TRANSITION_IMAGE = "transition_image"

        private const val PRICE_GINGER = 5
        private const val PRICE_MAYO = 8
        private const val PRICE_TEMPURA = 10
    }
}
