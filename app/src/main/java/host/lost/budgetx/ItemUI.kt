package host.lost.budgetx

import android.view.ViewGroup
import android.view.ViewManager
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.transactionView(init: (@AnkoViewDslMarker TransactionView).() -> Unit) =
    ankoView({ TransactionView(it) }, theme = 0, init = { init() })

class ItemUI : AnkoComponent<ViewGroup> {
    lateinit var view: TransactionView

    override fun createView(ui: AnkoContext<ViewGroup>) = ui.cardView {
        //        useCompatPadding = true
        lparams(width = matchParent, height = dip(48), init = {
            topMargin = dip(4)
            bottomMargin = dip(4)
            leftMargin = dip(16)
            rightMargin = dip(16)
        })
        transactionView {
            view = this
        }.lparams(
            width = matchParent,
            height = matchParent,
            init = {
                leftMargin = dip(16)
                rightMargin = dip(16)
                topMargin = dip(8)
                bottomMargin = dip(8)
            }
        )
    }
}