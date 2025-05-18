package com.codingstudio.mutualtransfer.ui.recently_viewed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewedNew
import com.google.android.material.chip.Chip

class AdapterForRecentlyViewedPersonNew : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForRecentlyViewedPerson(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewSearchResultUserName = itemView.findViewById<TextView>(R.id.textViewSearchResultUserName)
        private val textViewSearchResultEmployeeCurrentDesignationAndDepartment = itemView.findViewById<TextView>(R.id.textViewSearchResultEmployeeCurrentDesignationAndDepartment)
        private val textViewSearchResultEmployeeOrganization = itemView.findViewById<TextView>(R.id.textViewSearchResultEmployeeOrganization)
        private val textViewSearchResultUserAddress = itemView.findViewById<TextView>(R.id.textViewSearchResultUserAddress)

        private val textViewSearchResultViewDetailsButton = itemView.findViewById<TextView>(R.id.textViewSearchResultViewDetailsButton)
        private val textViewSearchResultMessagePerson = itemView.findViewById<TextView>(R.id.textViewSearchResultMessagePerson)

        private val chipDistrictPreference1 = itemView.findViewById<Chip>(R.id.chipDistrictPreference1)
        private val chipDistrictPreference2 = itemView.findViewById<Chip>(R.id.chipDistrictPreference2)
        private val chipDistrictPreference3 = itemView.findViewById<Chip>(R.id.chipDistrictPreference3)

        private val textViewSearchResultPreferredDistrictsText = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferredDistrictsText)
        private val textViewSearchResultPreferenceNotMatch = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferenceNotMatch)

        fun bind(modelRecentlyViewed: ModelRecentlyViewedNew) {

            textViewSearchResultUserName.text = modelRecentlyViewed.name
            textViewSearchResultEmployeeCurrentDesignationAndDepartment.text = "${modelRecentlyViewed.current_role} - ${modelRecentlyViewed.department} - ${modelRecentlyViewed.service_type}"
            textViewSearchResultEmployeeOrganization.text = "${modelRecentlyViewed.current_organisation_name} (${modelRecentlyViewed.zone_division})"

            textViewSearchResultUserAddress.text = "${modelRecentlyViewed.job_address_village},${modelRecentlyViewed.job_address_block},${modelRecentlyViewed.job_address_district},${modelRecentlyViewed.job_address_state},${modelRecentlyViewed.job_address_pin}"

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

            textViewSearchResultPreferenceNotMatch.visibility = View.GONE

            textViewSearchResultViewDetailsButton.setOnClickListener {
                onPersonViewDetailsClickListener?.let {
                    it(modelRecentlyViewed)
                }
            }

            textViewSearchResultMessagePerson.setOnClickListener {
                onMessagePersonClickedListener?.let {
                    it(modelRecentlyViewed)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelRecentlyViewedNew>() {
        override fun areItemsTheSame(
            oldItem: ModelRecentlyViewedNew,
            newItem: ModelRecentlyViewedNew
        ): Boolean {
           return oldItem.user_details_new_id == newItem.user_details_new_id
        }

        override fun areContentsTheSame(
            oldItem: ModelRecentlyViewedNew,
            newItem: ModelRecentlyViewedNew
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForRecentlyViewedPerson(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_search_result_of_person_new, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForRecentlyViewedPerson).bind(searchedHistory)
    }

    private var onMessagePersonClickedListener : ((ModelRecentlyViewedNew) -> Unit) ?= null
    fun setOnMessagePersonClickedListener(listener: ((ModelRecentlyViewedNew) -> Unit)) {
        onMessagePersonClickedListener = listener
    }

    private var onPersonViewDetailsClickListener : ((ModelRecentlyViewedNew) -> Unit) ?= null
    fun setOnPersonViewDetailsClickListener(listener : ((ModelRecentlyViewedNew) -> Unit)) {
        onPersonViewDetailsClickListener = listener
    }

}