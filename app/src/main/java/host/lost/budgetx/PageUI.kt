package host.lost.budgetx

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class PageUI(
    private val itemPool: RecyclerView.RecycledViewPool
) : AnkoComponent<ViewGroup> {
    lateinit var itemsList: RecyclerView

    override fun createView(ui: AnkoContext<ViewGroup>) = ui.frameLayout {
        lparams(
            width = matchParent,
            height = matchParent
        )
        recyclerView {
            itemsList = this
            setRecycledViewPool(itemPool)
            setPadding(0, dip(16), 0, dip(16))
            clipToPadding = false
            layoutManager = object : LinearLayoutManager(ui.ctx) {
                override fun isLayoutRTL(): Boolean {
                    return false
                }
            }.apply {
                stackFromEnd = true
            }
        }.lparams(
            width = matchParent,
            height = matchParent
        )
    }
}