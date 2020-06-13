package com.example.coroutines.main

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.coroutines.R
import com.example.coroutines.main.data.Dog
import com.example.coroutines.util.GeneralResult
import com.example.coroutines.util.ImageLoader
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_dog_of_the_day.view.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy(NONE) {
        ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
    }
    private val adapter by lazy(NONE) { RecyclerAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler.adapter = adapter
        subscribeObservers()
        initListeners()
    }

    private fun initListeners() {
        toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonAsync -> fetchImagesAsynchronously()
                    R.id.buttonSync -> fetchImagesSynchronously()
                }
            }
        }
    }

    private fun fetchImagesSynchronously() {
        viewModel.loadDogListSynchronously()
    }

    private fun fetchImagesAsynchronously() {
        viewModel.loadDogListAsynchronously()
    }

    private fun subscribeObservers() {
        viewModel.spinner.observe(this, Observer { show ->
            spinner.visibility = if (show) VISIBLE else GONE
        })

        viewModel.status.observe(this, Observer { it ->
            time.text = it
        })


        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(root_layout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        })

        viewModel.topDogsAsync.observe(this, Observer {
            it?.let {
                it[0].let {
                    dog_one.breed_name.text = it.breed
                    it.imageUsl?.let { it1 -> ImageLoader.loadImage(this, it1, dog_one.episode_item_image) }
                }

                it[1].let {
                    dog_two.breed_name.text = it.breed
                    it.imageUsl?.let { it1 -> ImageLoader.loadImage(this, it1, dog_two.episode_item_image) }
                }
            }
        })

        viewModel.dogList.observe(this, Observer {
            val list = it as List<Dog>
            adapter.submitList(list)
        })

//        viewModel.topTwoDogs.observe(this, Observer {
//            when (it) {
//                is GeneralResult.Progress -> {
//                }
//                is GeneralResult.SuccessGeneric<*> -> {
//                    updateTopTwoDogs(it.data as List<Dog>)
//                }
//                is GeneralResult.Error -> {
//                }
//            }
//        })


        viewModel.liveDataResult.observe(this, Observer {
            Log.e("liveDataResult", it.toString())
            when (it) {
                is GeneralResult.Progress -> { showLoadingStatus(it.loading)}
                is GeneralResult.SuccessGeneric<*> -> {
                    showLoadingStatus(false)
                    updateTopTwoDogs(it.data as List<Dog>)
                }
                is GeneralResult.Error -> {
                }
            }
        })

    }

    private fun showLoadingStatus(loading: Boolean) {
        statusTop.text = if (loading) "Loading..." else "Finished"
    }

    private fun updateTopTwoDogs(it: List<Dog>) {
        it.let {
            it[0].let {
                dog_one.breed_name.text = it.breed
                it.imageUsl?.let { it1 -> ImageLoader.loadImage(this, it1, dog_one.episode_item_image) }
            }

            it[1].let {
                dog_two.breed_name.text = it.breed
                it.imageUsl?.let { it1 -> ImageLoader.loadImage(this, it1, dog_two.episode_item_image) }
            }
        }
    }
}