package com.dave.fish.ui.fweather

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.ui.GlideApp
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_weather.*

/**
 * Created by soul on 2018. 2. 17..
 */
class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.RepositoryHolder>() {

    private var items: MutableList<WeatherRepo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = RepositoryHolder(parent)

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {

        items[position].let {repo ->

            with(holder){
                GlideApp.with(holder.itemView.context)
                        .load(repo.imageUrl)
                        .into(weather_item_iv)

                weather_item_title.text = repo.title
            }
        }
    }

    override fun getItemCount() = items.size

    fun setItems(items: List<WeatherRepo>){
        this.items = items.toMutableList()
    }

    fun clearItems(){
        this.items.clear()
    }

    class RepositoryHolder(parant: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parant.context)
                    .inflate(R.layout.item_weather, parant, false))

    abstract class AndroidExtensionsViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer
}