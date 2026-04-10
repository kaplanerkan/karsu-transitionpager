package com.karsu.transitionpager

/**
 * Tek bir sayfada gösterilecek içerik.
 *
 * @param image Görsel kaynağı: String (URL), Uri, @DrawableRes Int, File vb. Coil'in
 *              desteklediği herhangi bir tür olabilir.
 * @param title Üst kartta gösterilecek başlık.
 * @param subtitle Açılır panelde gösterilecek alt metin.
 * @param rating 0..5 arası yıldız değeri.
 */
data class TransitionItem(
    val image: Any,
    val title: String? = null,
    val subtitle: String? = null,
    val rating: Float = 0f,
)
