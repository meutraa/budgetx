package host.lost.budgetx

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var pages: RecyclerView
    lateinit var homeItem: BottomItemView

    override fun createView(ui: AnkoContext<MainActivity>) = ui.frameLayout {
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        pages = recyclerView {
            lparams()
            setHasFixedSize(true)
            PagerSnapHelper().attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(ui.ctx, RecyclerView.HORIZONTAL, false)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.findChildViewUnder(0.0f, 0.0f)?.let {
                            val pos = layoutManager?.getPosition(it)
                            homeItem.checkedItem = pos ?: 0
                        }
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
            adapter = PageAdapter()
            setPadding(0, 0, 0, dip(52))
        }.lparams(width = matchParent, height = matchParent)
        homeItem = bottomItemView {
            setOnItemClickListener { position: Int ->
                pages.post {
                    pages.smoothScrollToPosition(position)
                }
            }
        }.lparams(width = matchParent, height = dip(52), gravity = Gravity.BOTTOM)
    }
}