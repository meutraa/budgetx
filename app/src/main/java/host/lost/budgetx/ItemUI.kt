package host.lost.budgetx

import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip

class ItemUI : AnkoComponent<ViewGroup> {
    lateinit var view: TransactionView

    override fun createView(ui: AnkoContext<ViewGroup>): View {
        view = ui.transactionView {}
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ui.dip(48))
        return view
    }
}

