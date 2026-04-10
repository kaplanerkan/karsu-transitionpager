package com.karsu.transitionpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

internal class TransitionPageAdapter(
    private val items: List<TransitionItem>,
    private val onPageClick: (position: Int, holder: PageViewHolder) -> Unit,
) : RecyclerView.Adapter<TransitionPageAdapter.PageViewHolder>() {

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dragLayout: DragLayout = view.findViewById(R.id.tp_drag_layout)
        val imageView: ImageView = view.findViewById(R.id.tp_image)
        val titleView: TextView = view.findViewById(R.id.tp_title)
        val subtitleView: TextView = view.findViewById(R.id.tp_subtitle)
        val ratingBar: RatingBar = view.findViewById(R.id.tp_rating)
        val bottomTitleView: TextView = view.findViewById(R.id.tp_bottom_title)
        val bottomSubtitleView: TextView = view.findViewById(R.id.tp_bottom_subtitle)
        val bottomRatingBar: RatingBar = view.findViewById(R.id.tp_bottom_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tp_item_transition_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.load(item.image) {
            crossfade(true)
        }
        holder.titleView.apply {
            text = item.title.orEmpty()
            visibility = if (item.title.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        holder.subtitleView.apply {
            text = item.subtitle.orEmpty()
            visibility = if (item.subtitle.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        holder.ratingBar.rating = item.rating

        holder.bottomTitleView.text = item.title.orEmpty()
        holder.bottomSubtitleView.text = item.subtitle.orEmpty()
        holder.bottomRatingBar.rating = item.rating

        holder.dragLayout.gotoDetailListener = {
            onPageClick(holder.bindingAdapterPosition, holder)
        }
    }

    override fun getItemCount(): Int = items.size
}
