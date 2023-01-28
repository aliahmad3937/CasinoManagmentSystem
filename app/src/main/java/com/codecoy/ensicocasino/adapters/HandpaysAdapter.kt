package com.codecoy.ensicocasino.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.ensicocasino.callbacks.HandpayInterface
import com.codecoy.ensicocasino.databinding.ItemHanpaysBinding
import com.codecoy.ensicocasino.models.Handpays
import java.text.DecimalFormat
import java.text.NumberFormat


class HandpaysAdapter(
    var list: ArrayList<Handpays>, val context:Context, val handpayInterface: HandpayInterface, val sp: SharedPreferences,
    var format: DecimalFormat = DecimalFormat()
) : RecyclerView.Adapter<HandpaysAdapter.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemHanpaysBinding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHanpaysBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    init {
        format.isDecimalSeparatorAlwaysShown = false
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                val amont:Double = this.amount?.toDouble()?.div(100.0)?.toDouble() ?: 1.0

                binding.textView4.setText("${this.gm} ${format.format(amont)}${sp.getString("currency", "EUR").toString()}")

                binding.root.setOnClickListener(View.OnClickListener {
                    handpayInterface.handpayClick(this,format.format(amont))
                })
//                binding.tvLangName.text = this.name
//                binding.tvExp.text = this.exp
            }
        }
    }

    fun updateList(languageList: ArrayList<Handpays>){
//        this.list.clear()
//        this.list.addAll(languageList)
//        notifyDataSetChanged()
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return list.size
    }
}
