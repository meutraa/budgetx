package host.lost.budgetx

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import host.lost.budgetx.recyclerview.widget.PageAdapter
import host.lost.budgetx.view.BottomItemView
import host.lost.budgetx.view.EditItemView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
inline fun View.dip(value: Int): Int = context.dip(value)
inline fun View.sp(value: Int): Int = context.sp(value)

class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var homeItem: BottomItemView
    lateinit var editItemView: EditItemView
    var dialog: AlertDialog? = null

    companion object {
        val pool: ExecutorService = Executors.newWorkStealingPool()
        var layoutThread: ExecutorService = Executors.newFixedThreadPool(1)
        var isFirestoreReady = false
        val categories = arrayOf(
            "Food & Drink",
            "Bills",
            "Personal",
            "Holiday",
            "Medical",
            "Travel",
            "Pets",
            "House",
            "Income",
            "Other"
        )

        val colors = List(5) {
            Color.parseColor(
                when (it) {
                    0 -> "#9575CD"
                    1 -> "#E57373"
                    2 -> "#4FC3F7"
                    3 -> "#FF7043"
                    else -> "#4DB6AC"
                }
            )
        }
    }

    fun showDialog(item: DocumentSnapshot?, account: String = "") {
        item?.let {
            editItemView.setTransaction(it)
        }
        if (null == item) {
            editItemView.clear(account)
        }
        dialog?.show()
        dialog?.findViewById<View>(android.R.id.button3)?.visibility =
            if (null == item) View.GONE else View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pool.execute {
            synchronized(isFirestoreReady) {
                db = FirebaseFirestore.getInstance().apply {
                    firestoreSettings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                }
                isFirestoreReady = true
            }
        }
        pool.execute {
            editItemView = EditItemView(this@MainActivity).apply {
                runOnUiThread {
                    this@MainActivity.dialog = AlertDialog.Builder(this@MainActivity)
                        .setView(this)
                        .setCancelable(false)
                        .setPositiveButton("Confirm") { _, _ ->
                            save()
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                        }
                        .setNeutralButton("Delete") { _, _ ->
                            delete()
                        }
                        .create()
                }
            }
        }
        layoutThread.execute {
            lateinit var pages: ViewPager2

            pages = ViewPager2(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setPadding(0, 0, 0, dip(52))
                adapter = PageAdapter(this@MainActivity)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        homeItem.checkedItem = position
                    }
                })
            }
            homeItem = BottomItemView(this).apply {
                layoutParams =
                    FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(52)).apply {
                        gravity = Gravity.BOTTOM
                    }
                setOnItemClickListener { pages.setCurrentItem(it, true) }
            }

            (findViewById<ContentFrameLayout>(android.R.id.content).parent.parent as FrameLayout).apply {
                runOnUiThread {
                    removeAllViews()
                    importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    addView(pages)
                    addView(homeItem)
                    layoutThread.shutdown()
                }
            }
        }
    }
}
