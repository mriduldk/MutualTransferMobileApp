package com.codingstudio.mutualtransfer.ui.search.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class AdapterForSearchResultOfPerson : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PERSON = 0
        private const val VIEW_TYPE_AD = 1
        private const val AD_FREQUENCY = 6 // Show an ad after every 3 items
    }

    inner class ViewHolderForSearchHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
        private val chipAmalgamated = itemView.findViewById<Chip>(R.id.chipAmalgamated)
        private val textViewSearchResultPreferredDistrictsText = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferredDistrictsText)
        private val textViewSearchResultPreferenceNotMatch = itemView.findViewById<TextView>(R.id.textViewSearchResultPreferenceNotMatch)

        fun bind(modelSearchResultOfPerson: ModelSearchResultOfPerson) {

            textViewSearchResultUserName.text = modelSearchResultOfPerson.name
            textViewSearchResultUserPhoneNo.text = modelSearchResultOfPerson.phone
            textViewSearchResultUserEmail.text = modelSearchResultOfPerson.email
            textViewSearchResultUserSchoolName.text = modelSearchResultOfPerson.school_name
            textViewSearchResultUserAddress.text = "${modelSearchResultOfPerson.school_address_vill},${modelSearchResultOfPerson.school_address_block},${modelSearchResultOfPerson.school_address_district},${modelSearchResultOfPerson.school_address_state},${modelSearchResultOfPerson.school_address_pin}"

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
                textViewSearchResultPreferredDistrictsText.text = "Preferred Districts"
            }
            else {
                textViewSearchResultPreferredDistrictsText.text = "No Preferred District Available"
            }

            if (modelSearchResultOfPerson.is_paid == 1){
                textViewSearchResultViewDetailsButton.visibility = View.VISIBLE
                constraintLayoutSearchResultPayCoins.visibility = View.GONE
            }
            else{
                textViewSearchResultViewDetailsButton.visibility = View.VISIBLE
                constraintLayoutSearchResultPayCoins.visibility = View.VISIBLE
            }

            if (modelSearchResultOfPerson.district_match_flag == 0) {
                textViewSearchResultPreferenceNotMatch.visibility = View.VISIBLE
            }
            else {
                textViewSearchResultPreferenceNotMatch.visibility = View.GONE
            }

            if (modelSearchResultOfPerson.amalgamation == 0) {
                chipAmalgamated.visibility = View.GONE
            }
            else {
                chipAmalgamated.visibility = View.VISIBLE
            }

            textViewSearchResultPayButton.text = "Pay ${modelSearchResultOfPerson.pay_to_view_amount} Coins"

            textViewSearchResultViewDetailsButton.setOnClickListener {
                onPersonViewDetailsClickListener?.let {
                    it(modelSearchResultOfPerson)
                }
            }

            constraintLayoutSearchResultPayCoins.setOnClickListener {
                onPayPersonClickedListener?.let {
                    it(modelSearchResultOfPerson)
                }
            }

            imageViewPhoneNoCopy.setOnClickListener {
                onPhoneNoCopyClickListener?.let {
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



    private val differCallback = object : DiffUtil.ItemCallback<ModelSearchResultOfPerson>() {
        override fun areItemsTheSame(
            oldItem: ModelSearchResultOfPerson,
            newItem: ModelSearchResultOfPerson
        ): Boolean {
           return oldItem.user_details_id == newItem.user_details_id
        }

        override fun areContentsTheSame(
            oldItem: ModelSearchResultOfPerson,
            newItem: ModelSearchResultOfPerson
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
                        .inflate(R.layout.recycler_view_layout_for_search_result_of_person, parent, false)
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


        /*return ViewHolderForSearchHistory(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_search_result_of_person, parent, false)
        )*/
    }

    override fun getItemCount(): Int {
        //return differ.currentList.size
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

        /*val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForSearchHistory).bind(searchedHistory)*/
    }

    private var onPayPersonClickedListener : ((ModelSearchResultOfPerson) -> Unit) ?= null
    fun setOnPayPersonClickedListener(listener: ((ModelSearchResultOfPerson) -> Unit)) {
        onPayPersonClickedListener = listener
    }

    private var onPersonViewDetailsClickListener : ((ModelSearchResultOfPerson) -> Unit) ?= null
    fun setOnPersonViewDetailsClickListener(listener : ((ModelSearchResultOfPerson) -> Unit)) {
        onPersonViewDetailsClickListener = listener
    }

    private var onPhoneNoCopyClickListener : ((ModelSearchResultOfPerson) -> Unit) ?= null
    fun setOnPhoneNoCopyClickListener(listener : ((ModelSearchResultOfPerson) -> Unit)) {
        onPhoneNoCopyClickListener = listener
    }

}