package com.giis.mobileappproto1.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.giis.mobileappproto1.ui.fragments.AppsFragment
import com.giis.mobileappproto1.ui.fragments.CalendarFragment
import com.giis.mobileappproto1.ui.fragments.HomeFragment
import com.giis.mobileappproto1.ui.fragments.MessagesFragment
import com.giis.mobileappproto1.ui.fragments.OnlyFeedFragment
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.databinding.ActivityMainBinding
import com.giis.mobileappproto1.utils.globalTag
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // @Inject
    lateinit var firstFragment: HomeFragment

    companion object MainAct {
        var capturedBitmap: Bitmap? = null
        var topViewWidth = 0
        var topViewHeight = 0
        var mainConstLayoutReference: ConstraintLayout? = null

        fun applyGaussianBlur(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createTyped(rs, input.type)

            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            script.setRadius(radius)
            script.setInput(input)
            script.forEach(output)

            output.copyTo(bitmap)

            input.destroy()
            output.destroy()
            script.destroy()
            rs.destroy()

            return bitmap
        }

        fun blurBitMapUsingRenderScript(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(
                rs,
                bitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            // Create an output allocation
            val output = Allocation.createTyped(rs, input.type)
            // Create a blur script
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            // Set the blur radius
            blurScript.setRadius(radius)
            blurScript.setInput(input)
            blurScript.forEach(output)
            // Copy the blurred output to a new bitmap
            val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            output.copyTo(blurredBitmap)
            // Release resources
            input.destroy()
            output.destroy()
            blurScript.destroy()
            rs.destroy()
            return blurredBitmap
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        setSupportActionBar(binding.mainGraphicToolBar)

        this.firstFragment = HomeFragment()

        // By default the first fragment which appears
        with(supportFragmentManager.beginTransaction()) {
            add(R.id.fragments_container_main, firstFragment)
        }.commit()

        binding.bottomBubbleTabBarMain.addBubbleListener { id ->
            Log.e(globalTag, "$id and ${R.id.homeBubble}")
            when (id) {
                R.id.homeBubble -> {
                    binding.mainToolbarContent.groupFeed.visibility = View.GONE
                    binding.mainToolbarContent.groupHome.visibility = View.VISIBLE
                    binding.mainAppBarLayout.outlineProvider = ViewOutlineProvider.BOUNDS
                    replaceFragment(firstFragment, 0)
                }

                R.id.calendarBubble -> replaceFragment(CalendarFragment(), 1)
                R.id.tasksBubble -> {
                    binding.mainToolbarContent.groupFeed.visibility = View.VISIBLE
                    binding.mainToolbarContent.groupHome.visibility = View.GONE
                    binding.mainAppBarLayout.outlineProvider = null
                    replaceFragment(OnlyFeedFragment(), 2)
                }

                R.id.messagesBubble -> replaceFragment(MessagesFragment(), 3)
                R.id.appsBubble -> replaceFragment(AppsFragment(), 4)
                else -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_container_main, Fragment()).commit()
            }
        }
        // Select Home Bubble onCreate() by default
        // binding.bottomBubbleTabBarMain.setSelected(position = 0, callListener = true)
        // binding.bottomBubbleTabBarMain.performClick()

        binding.mainConstLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                topViewWidth = binding.mainConstLayout.width
                topViewHeight = binding.mainConstLayout.height
                val rootView = window.decorView.rootView
                capturedBitmap =
                    Bitmap.createBitmap(topViewWidth, topViewHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(capturedBitmap!!)
                canvas.translate(
                    -binding.mainConstLayout.left.toFloat(),
                    -binding.mainConstLayout.top.toFloat()
                )
                rootView.draw(canvas)
                val bitmapDrawable = BitmapDrawable(
                    resources,
                    applyGaussianBlur(this@MainActivity, capturedBitmap!!, 22f)
                )
                binding.mainConstLayout.setBackgroundDrawable(bitmapDrawable)
                mainConstLayoutReference = binding.mainConstLayout
                binding.mainConstLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


    }

    private fun replaceFragment(fragment: Fragment, idPos: Int) {
        val transaction = with(supportFragmentManager.beginTransaction()) {
            replace(R.id.fragments_container_main, fragment)
        }
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.e(globalTag, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.home_screen_menus, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notificationsHome -> {
                startActivity(
                    Intent(
                        this,
                        NotificationsActivity::class.java
                    )
                )
            }

            R.id.searchHome -> {
                Toast.makeText(this, "Search functionality under development", Toast.LENGTH_SHORT)
                    .show()
                // startActivity(Intent(this, CreatePostActivity::class.java))
            }

            else -> Toast.makeText(this@MainActivity, "Functionality TODO", Toast.LENGTH_SHORT)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }



}