package com.codingstudio.mutualtransfer.ui.search.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPersonNew
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.chip.Chip

class AdapterForSearchResultOfPersonNew : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PERSON = 0
        private const val VIEW_TYPE_AD = 1
        private const val AD_FREQUENCY = 15 // Show an ad after every 15 items
    }

    inner class ViewHolderForSearchHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {

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

        fun bind(modelSearchResultOfPerson: ModelSearchResultOfPersonNew) {

            textViewSearchResultUserName.text = modelSearchResultOfPerson.name
            textViewSearchResultEmployeeCurrentDesignationAndDepartment.text = "${modelSearchResultOfPerson.current_role} - ${modelSearchResultOfPerson.department} - ${modelSearchResultOfPerson.service_type}"
            textViewSearchResultEmployeeOrganization.text = "${modelSearchResultOfPerson.current_organisation_name} (${modelSearchResultOfPerson.zone_division})"

            textViewSearchResultUserAddress.text = "${modelSearchResultOfPerson.job_address_village},${modelSearchResultOfPerson.job_address_block},${modelSearchResultOfPerson.job_address_district},${modelSearchResultOfPerson.job_address_state},${modelSearchResultOfPerson.job_address_pin}"

            var hasPreferredDistricts = false

            modelSearchResultOfPerson.preferred_district_1?.let {
                chipDistrictPreference1.text = it
                chipDistrictPreference1.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference1.visibility = View.GONE
            }

            modelSearchResultOfPerson.preferred_district_2?.let {
                chipDistrictPreference2.text = it
                chipDistrictPreference2.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference2.visibility = View.GONE
            }

            modelSearchResultOfPerson.preferred_district_3?.let {
                chipDistrictPreference3.text = it
                chipDistrictPreference3.visibility = View.VISIBLE
                hasPreferredDistricts = true
            } ?: run {
                chipDistrictPreference3.visibility = View.GONE
            }

            if (hasPreferredDistricts) {
                textViewSearchResultPreferredDistrictsText.text = "Preferred Districts (Want To Go)"
            }
            else {
                textViewSearchResultPreferredDistrictsText.text = "No Preferred District Available"
            }


            if (modelSearchResultOfPerson.district_match_flag == 0) {
                textViewSearchResultPreferenceNotMatch.visibility = View.VISIBLE
            }
            else {
                textViewSearchResultPreferenceNotMatch.visibility = View.GONE
            }


            textViewSearchResultViewDetailsButton.setOnClickListener {
                onPersonViewDetailsClickListener?.let {
                    it(modelSearchResultOfPerson)
                }
            }
            textViewSearchResultMessagePerson.setOnClickListener {
                onMessagePersonClickedListener?.let {
                    it(modelSearchResultOfPerson)
                }
            }

        }

    }

    inner class ViewHolderForAd(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val adContainer: FrameLayout = itemView.findViewById(R.id.native_ad_container)

        fun bindNativeAd() {
            // Code to load a Native Ad and populate the ad view
            val adLoader = AdLoader.Builder(itemView.context, BuildConfig.ADMOB_NATIVE_ADVANCED_UNIT_ID)
                .forNativeAd { nativeAd: NativeAd ->
                    val adView = LayoutInflater.from(itemView.context).inflate(R.layout.native_ad_layout, null) as NativeAdView
                    populateNativeAdView(nativeAd, adView)
                    adContainer.removeAllViews()
                    adContainer.addView(adView)
                }
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Populate ad data into the view
            adView.headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            adView.bodyView = adView.findViewById<TextView>(R.id.ad_body)
            adView.iconView = adView.findViewById<ImageView>(R.id.ad_icon)
            adView.callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)

            (adView.headlineView as TextView).text = nativeAd.headline
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                adView.iconView?.visibility = View.VISIBLE
                (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            }

            adView.setNativeAd(nativeAd)
        }
    }



    private val differCallback = object : DiffUtil.ItemCallback<ModelSearchResultOfPersonNew>() {
        override fun areItemsTheSame(
            oldItem: ModelSearchResultOfPersonNew,
            newItem: ModelSearchResultOfPersonNew
        ): Boolean {
           return oldItem.user_details_new_id == newItem.user_details_new_id
        }

        override fun areContentsTheSame(
            oldItem: ModelSearchResultOfPersonNew,
            newItem: ModelSearchResultOfPersonNew
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PERSON -> {
                ViewHolderForSearchHistory(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_view_layout_for_search_result_of_person_new, parent, false)
                )
            }
            VIEW_TYPE_AD -> {
                ViewHolderForAd(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.native_ad_layout, parent, false)
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size + differ.currentList.size / AD_FREQUENCY
    }

    override fun getItemViewType(position: Int): Int {
        // Return view type based on the position. If it's an ad position, return VIEW_TYPE_AD
        return if ((position + 1) % (AD_FREQUENCY + 1) == 0) {
            VIEW_TYPE_AD
        } else {
            VIEW_TYPE_PERSON
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_PERSON) {
            val actualPosition = position - position / (AD_FREQUENCY + 1) // Calculate actual item position in list
            val searchedHistory = differ.currentList[actualPosition]
            (holder as ViewHolderForSearchHistory).bind(searchedHistory)
        } else if (getItemViewType(position) == VIEW_TYPE_AD) {
            (holder as ViewHolderForAd).bindNativeAd()
        }
    }

    private var onMessagePersonClickedListener : ((ModelSearchResultOfPersonNew) -> Unit) ?= null
    fun setOnMessagePersonClickedListener(listener: ((ModelSearchResultOfPersonNew) -> Unit)) {
        onMessagePersonClickedListener = listener
    }

    private var onPersonViewDetailsClickListener : ((ModelSearchResultOfPersonNew) -> Unit) ?= null
    fun setOnPersonViewDetailsClickListener(listener : ((ModelSearchResultOfPersonNew) -> Unit)) {
        onPersonViewDetailsClickListener = listener
    }


}