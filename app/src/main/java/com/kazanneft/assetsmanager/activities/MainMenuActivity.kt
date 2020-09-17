package com.kazanneft.assetsmanager.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.appbar.AppBarLayout
import com.kazanneft.assetsmanager.R
import com.kazanneft.assetsmanager.animations.BackdropRevealAnimator
import com.kazanneft.assetsmanager.databinding.ActivityMainMenuBinding
import java.lang.IllegalArgumentException

class MainMenuActivity : AppCompatActivity() {

    private lateinit var backdropAnimator: BackdropRevealAnimator
    private lateinit var toolbar: Toolbar
    private lateinit var filterItem: MenuItem
    private var closeToFilter: AnimatedVectorDrawableCompat? = null

    private var filterToClose: AnimatedVectorDrawableCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init toolbar only iny portrait orientation
        if(resources.configuration.orientation
            == Configuration.ORIENTATION_PORTRAIT) {

            toolbar = findViewById(R.id.toolbar_main)

            setSupportActionBar(toolbar)

            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

            filterToClose = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.filter_to_close_animation
            )

            closeToFilter = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.close_to_filter_animation
            )

            binding.root.viewTreeObserver.addOnGlobalLayoutListener {

                if (this::backdropAnimator.isInitialized)
                    return@addOnGlobalLayoutListener

                val fragment = findViewById<View>(R.id.fragment_assets_front)

                backdropAnimator = BackdropRevealAnimator(
                    fragment, binding.root.height
                )
            }
        }
        //Hide toolbar in landscape orientation
        else {
            val appBarLayout: AppBarLayout = findViewById(R.id.app_bar_layout_main)
            appBarLayout.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(menu == null)
            throw IllegalArgumentException()

        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        filterItem = menu.findItem(R.id.menu_asset_filter)
        filterItem.icon = filterToClose
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.menu_asset_filter -> {

                if(backdropAnimator.isRevealed)
                    showFrontLayer()
                else
                    showBackLayer()
            }


            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

        return true
    }

    private fun showBackLayer(){
        backdropAnimator.reveal()
        filterItem.icon = filterToClose
        filterToClose?.start()
        toolbar.title = "Filters"
    }

    private fun showFrontLayer(){
        backdropAnimator.unreveal()
        filterItem.icon = closeToFilter
        closeToFilter?.start()
        toolbar.title = "Assets Manager"
    }
}