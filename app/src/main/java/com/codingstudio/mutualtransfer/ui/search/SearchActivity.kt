package com.codingstudio.mutualtransfer.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.mutualtransfer.MainApplication
import com.codingstudio.mutualtransfer.databinding.ActivitySearchBinding
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType
import com.codingstudio.mutualtransfer.ui.search.adpter.AdapterForSearchHistory
import com.codingstudio.mutualtransfer.ui.search.adpter.AdapterForSearchResult
import com.codingstudio.mutualtransfer.ui.search.viewmodel.block.BlockViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.district.DistrictViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.searchHistory.SearchedHistoryViewModel
import com.codingstudio.mutualtransfer.ui.search.viewmodel.searchHistory.SearchedHistoryViewModelFactory
import com.codingstudio.mutualtransfer.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private val TAG = "SearchActivity"
    private var searchType : String ?= null
    private var searchDistrictId : String ?= null
    private var _binding : ActivitySearchBinding ?= null
    private val binding get() = _binding!!

    private lateinit var adapterForSearchHistory: AdapterForSearchHistory
    private lateinit var adapterForSearchResult: AdapterForSearchResult

    private val handler = Handler()
    private var runnable : Runnable ?= null
    private val delay : Long = 500


    /*private val districtViewModel : DistrictViewModel by viewModels {
        DistrictViewModelFactory(application, (application as MainApplication).districtRepository, (application as MainApplication).historyRepository)
    }*/
    private val districtViewModel : DistrictViewModel by viewModels()


    /*private val blockViewModel : BlockViewModel by viewModels {
        BlockViewModelFactory(application, (application as MainApplication).blockRepository)
    }*/
    private val blockViewModel : BlockViewModel by viewModels ()

    private val searchedHistoryViewModel : SearchedHistoryViewModel by viewModels {
        SearchedHistoryViewModelFactory((application as MainApplication).historyRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchType = intent.getStringExtra(SEARCH_TYPE)
        searchDistrictId = intent.getStringExtra(SEARCH_DISTRICT_ID)

        onEditTextTextChangeListener()

        setOnclickListeners()
        setRecyclerViewOfSearchHistory()
        setRecyclerViewOfSearchResult()

        observeSearchedHistory()
        observeDistrictListForSearchedText()
        observeBlockListForSearchedText()

        searchType?.let {
            clickedSearchedText(it)
            //showHistoryRecyclerView()

            binding.editTextSearchText.setText("")
        }
    }

    private fun setOnclickListeners(){

        binding.imageViewBackSearch.setOnClickListener {
            onBackPressed()
        }

        binding.imageViewClearSearch.setOnClickListener {
            binding.editTextSearchText.setText("")
        }

    }

    private fun setRecyclerViewOfSearchHistory() {

        adapterForSearchHistory = AdapterForSearchHistory()
        binding.recyclerViewSearchHistory.apply {
            adapter = adapterForSearchHistory
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        adapterForSearchHistory.setOnSearchedHistoryPickClickListener { searchHistory ->

            binding.editTextSearchText.setText(searchHistory.searchedText)

        }

        adapterForSearchHistory.setOnSearchedHistoryClickedListener { searchHistory ->

            val resultIntent = Intent()
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_TYPE, searchHistory.searchedType.toString())
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_TEXT, searchHistory.searchedText)
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_ID, searchHistory.searchHistoryId)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }
    }
    private fun setRecyclerViewOfSearchResult() {

        adapterForSearchResult = AdapterForSearchResult()
        binding.recyclerViewSearchResult.apply {
            adapter = adapterForSearchResult
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        adapterForSearchResult.setOnSearchedResultClickedListener { searchResult ->

            searchedHistoryViewModel.insertHistoryFun(
                ModelSearchHistory(
                    searchHistoryId = searchResult.searchResultId ?: "",
                    searchedText = searchResult.searchResultText ?: "",
                    searchedType = searchResult.searchedType
                )
            )

            val resultIntent = Intent()
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_TYPE, searchResult.searchedType.toString())
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_TEXT, searchResult.searchResultText)
            resultIntent.putExtra(UserHomeActivity.SEARCH_RESULT_ID, searchResult.searchResultId)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }
    }

    private fun onEditTextTextChangeListener() {

        binding.editTextSearchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                runnable?.let {  handler.removeCallbacks(it) }

                runnable = Runnable {

                    if (searchType == SEARCH_TYPE_DISTRICT) {
                        getDistrictListFromSearchedText(s.toString())
                    }
                    else if(searchType == SEARCH_TYPE_BLOCK){
                        getBlockListFromSearchedText(s.toString())
                    }
                }

                runnable?.let { handler.postDelayed(it, delay) }
            }

        })

    }


    private fun clickedSearchedText(searchedType: String) {

        when (searchedType)
        {
            SEARCH_TYPE_DISTRICT -> {
                getSearchedHistoryByType(searchedType = SearchedType.DISTRICT)
            }
            SEARCH_TYPE_BLOCK -> {
                getSearchedHistoryByType(searchedType = SearchedType.BLOCK)
            }
            SEARCH_TYPE_SCHOOL -> {
                getSearchedHistoryByType(searchedType = SearchedType.SCHOOL)
            }
        }

    }

    /**
     * Get Searched History And Observe
     */
    private fun getSearchedHistoryByType(searchedType: SearchedType){
        searchedHistoryViewModel.getSearchByTypeHistoryFun(searchedType)
    }
    private fun observeSearchedHistory() {

        searchedHistoryViewModel.getSearchByTypeHistoryObserver.observe(this, Observer { result ->

            adapterForSearchHistory.differ.submitList(result)

        })

    }


    /**
     *  Get District List From Search And Observe
     * */
    private fun getDistrictListFromSearchedText(searchedText: String) {

        districtViewModel.getDistrictByNameFun(searchedText)

    }
    private fun observeDistrictListForSearchedText() {

        districtViewModel.getDistrictByNameObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseDistrict ->

                            if (responseDistrict.status == 200){
                                adapterForSearchResult.differ.submitList(responseDistrict.toSearchResults())
                                showSearchResultRecyclerView()
                            }
                            else{
                                showSnackBarMessage(responseDistrict.message)
                            }
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()

                        response.message?.let { errorMessage ->
                            when (errorMessage) {
                                Constants.NO_INTERNET -> {
                                    showSnackBarMessage("No internet connection")
                                }
                                else -> {
                                    showSnackBarMessage(errorMessage)
                                }
                            }
                        }

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }

            }


        })

    }


    /**
     *  Get Block List From Search And Observe
     * */
    private fun getBlockListFromSearchedText(searchedText: String) {

        blockViewModel.getBlocksByDistrictAndBlockNameFun(searchedText, searchDistrictId ?: "")

    }
    private fun observeBlockListForSearchedText() {

        blockViewModel.getBlocksByDistrictAndBlockNameObserver.observe(this, Observer { res ->

            res.getContentIfNotHandled()?.let { response ->

                when(response)
                {
                    is Resource.Success -> {
                        hideProgressBar()

                        response.data?.let { responseBlock ->

                            if (responseBlock.status == 200){
                                adapterForSearchResult.differ.submitList(responseBlock.toSearchResults())
                                showSearchResultRecyclerView()
                            }
                            else{
                                showSnackBarMessage(responseBlock.message)
                            }
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()

                        response.message?.let { errorMessage ->
                            when (errorMessage) {
                                Constants.NO_INTERNET -> {
                                    showSnackBarMessage("No internet connection")
                                }
                                else -> {
                                    showSnackBarMessage(errorMessage)
                                }
                            }
                        }

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }

            }


        })

    }




    private fun showProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.relativeLayoutProgressBar.visibility = View.GONE
    }

    private fun showHistoryRecyclerView(){
        binding.constraintLayoutHistory.visibility = View.VISIBLE
        binding.constraintLayoutSearchResult.visibility = View.GONE
    }
    private fun showSearchResultRecyclerView(){
        binding.constraintLayoutHistory.visibility = View.GONE
        binding.constraintLayoutSearchResult.visibility = View.VISIBLE
    }


    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.relativeLayoutParent, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {

        const val SEARCH_TYPE = "SEARCH_TYPE"
        const val SEARCH_TYPE_DISTRICT = "DISTRICT"
        const val SEARCH_TYPE_BLOCK = "BLOCK"
        const val SEARCH_TYPE_SCHOOL = "SCHOOL"
        const val SEARCH_DISTRICT_ID = "DISTRICT_ID"

    }



}