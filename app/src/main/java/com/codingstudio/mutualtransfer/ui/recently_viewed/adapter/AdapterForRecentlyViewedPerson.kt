package com.codingstudio.mutualtransfer.ui.recently_viewed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson
import com.google.android.material.chip.Chip

class AdapterForRecentlyViewedPerson : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForRecentlyViewedPerson(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewSearchResultUserName = itemView.findViewById<TextView>(R.id.textViewSearchResultUserName)
        private val textViewSearchResultUserPhoneNo = itemView.findViewById<TextView>(R.id.textViewSearchResultUserPhoneNo)
        private val textViewSearchResultUserEmail = itemView.findViewById<TextView>(R.id.textViewSearchResultUserEmail)
        private val textViewSearchResultUserSchoolName = itemView.findViewById<TextView>(R.id.textViewSearchResultUserSchoolName)
        private val textViewSearchResultUserAddress = itemView.findViewById<TextView>(R.id.textViewSearchResultUserAddress)

        private val imageViewPhoneNoCopy = itemView.findViewById<ImageView>(R.id.imageViewPhoneNoCopy)

        private val textViewSearchResultPayButton = itemView.findViewById<TextView>(R.id.textViewSearchResultPayButton)
        private val textViewSearchResultViewDetailsButton = itemView.findViewById<TextView>(R.id.textViewSearchResultViewDetailsButton)
        private val constraintLayoutSearchResultPayCoins = itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutSearchResultPayCoins)

        private val chipDistrictPreference1 = itemView.findViewById<Chip>(R.id.chipDistrictPreference1)
        private val chipDistrictPreference2 = itemView.findViewById<Chip>(R.id.chipDistrictPreference2)
        private val chipDistrictPreference3 = itemView.findViewById<Chip>(R.id.chipDistrictPreference3)
        private val textViewSearchResultPreferredDistrictsText = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferredDistrictsText)
        private val textViewSearchResultPreferenceNotMatch = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferenceNotMatch)

        fun bind(modelRecentlyViewed: ModelRecentlyViewed) {

            textViewSearchResultUserName.text = modelRecentlyViewed.name
            textViewSearchResultUserPhoneNo.text = modelRecentlyViewed.phone
            textViewSearchResultUserEmail.text = modelRecentlyViewed.email
            textViewSearchResultUserSchoolName.text = modelRecentlyViewed.school_name
            textViewSearchResultUserAddress.text = "${modelRecentlyViewed.school_address_vill},${modelRecentlyViewed.school_address_block},${modelRecentlyViewed.school_address_district},${modelRecentlyViewed.school_address_state},${modelRecentlyViewed.school_address_pin}"

            var hasPreferredDistricts = false

            modelRecentlyViewed.preferred_district_1?.let {
                chipDistrictPreference1.text = it
                chipDistrictPreference1.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference1.visibility = View.GONE
            }

            modelRecentlyViewed.preferred_district_2?.let {
                chipDistrictPreference2.text = it
                chipDistrictPreference2.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference2.visibility = View.GONE
            }

            modelRecentlyViewed.preferred_district_3?.let {
                chipDistrictPreference3.text = it
                chipDistrictPreference3.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference3.visibility = View.GONE
            }

            if (hasPreferredDistricts) {
                textViewSearchResultPreferredDistrictsText.text = "Preferred Districts"
            }
            else {
                textViewSearchResultPreferredDistrictsText.text = "No Preferred District Available"
            }

            if (modelRecentlyViewed.is_paid == 1){
                textViewSearchResultViewDetailsButton.visibility = View.VISIBLE
                constraintLayoutSearchResultPayCoins.visibility = View.GONE
            }
            else{
                textViewSearchResultViewDetailsButton.visibility = View.VISIBLE
                constraintLayoutSearchResultPayCoins.visibility = View.VISIBLE
            }

            textViewSearchResultPayButton.text = "Pay ${modelRecentlyViewed.pay_to_view_amount} Coins"


            textViewSearchResultViewDetailsButton.setOnClickListener {
                onPersonViewDetailsClickListener?.let {
                    it(modelRecentlyViewed)
                }
            }

            constraintLayoutSearchResultPayCoins.setOnClickListener {
                onPayPersonClickedListener?.let {
                    it(modelRecentlyViewed)
                }
            }

            imageViewPhoneNoCopy.setOnClickListener {
                onPhoneNoCopyClickListener?.let {
                    it(modelRecentlyViewed)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelRecentlyViewed>() {
        override fun areItemsTheSame(
            oldItem: ModelRecentlyViewed,
            newItem: ModelRecentlyViewed
        ): Boolean {
           return oldItem.user_details_id == newItem.user_details_id
        }

        override fun areContentsTheSame(
            oldItem: ModelRecentlyViewed,
            newItem: ModelRecentlyViewed
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForRecentlyViewedPerson(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_search_result_of_person, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForRecentlyViewedPerson).bind(searchedHistory)
    }

    private var onPayPersonClickedListener : ((ModelRecentlyViewed) -> Unit) ?= null
    fun setOnPayPersonClickedListener(listener: ((ModelRecentlyViewed) -> Unit)) {
        onPayPersonClickedListener = listener
    }

    private var onPersonViewDetailsClickListener : ((ModelRecentlyViewed) -> Unit) ?= null
    fun setOnPersonViewDetailsClickListener(listener : ((ModelRecentlyViewed) -> Unit)) {
        onPersonViewDetailsClickListener = listener
    }

    private var onPhoneNoCopyClickListener : ((ModelRecentlyViewed) -> Unit) ?= null
    fun setOnPhoneNoCopyClickListener(listener : ((ModelRecentlyViewed) -> Unit)) {
        onPhoneNoCopyClickListener = listener
    }

}