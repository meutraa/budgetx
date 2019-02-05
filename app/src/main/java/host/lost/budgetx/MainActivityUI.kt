package host.lost.budgetx

import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.*
import org.jetbrains.anko.design.bottomNavigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivityUI(private val db: FirebaseFirestore) : AnkoComponent<MainActivity> {
    lateinit var pages: RecyclerView
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var homeItem: MenuItem
    lateinit var tokaItem: MenuItem
    lateinit var paulItem: MenuItem
    lateinit var savingsItem: MenuItem
    lateinit var britishItem: MenuItem

    private fun Menu.menuItem(title: String, @DrawableRes iconRes: Int, position: Int): MenuItem =
        add(
            Menu.NONE,
            View.generateViewId(),
            Menu.NONE,
            title
        ).setIcon(iconRes).setOnMenuItemClickListener {
            pages.post {
                pages.smoothScrollToPosition(position)
            }
            false
        }

    override fun createView(ui: AnkoContext<MainActivity>) = ui.frameLayout {
        recyclerView {
            pages = this
            PagerSnapHelper().attachToRecyclerView(this)
            layoutManager = object : LinearLayoutManager(ui.ctx, RecyclerView.HORIZONTAL, false) {
                override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
                    return super.getExtraLayoutSpace(state) * 6
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.findChildViewUnder(0.0f, 0.0f)?.let {
                            val pos = layoutManager?.getPosition(it)
                            when (pos) {
                                0 -> homeItem.isChecked = true
                                1 -> tokaItem.isChecked = true
                                2 -> paulItem.isChecked = true
                                3 -> savingsItem.isChecked = true
                                4 -> britishItem.isChecked = true
                            }
                        }
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
            adapter = PageAdapter(db)
            setPadding(0, 0, 0, dip(48))
        }.lparams(
            width = matchParent,
            height = matchParent
        )
        bottomNavigationView {
            bottomNavigation = this
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            menu.apply {
                homeItem = menuItem("Home", R.drawable.menu_home, 0)
                tokaItem = menuItem("Toka", R.drawable.menu_toka, 1)
                paulItem = menuItem("Paul", R.drawable.menu_paul, 2)
                savingsItem = menuItem("Savings", R.drawable.menu_savings, 3)
                britishItem = menuItem("British", R.drawable.menu_british, 4)
            }
        }.lparams(
            width = matchParent,
            height = wrapContent,
            gravity = Gravity.BOTTOM
        )
    }
}