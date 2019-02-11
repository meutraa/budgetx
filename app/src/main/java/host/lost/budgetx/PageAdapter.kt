package host.lost.budgetx

import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jetbrains.anko.AnkoContext

class PageAdapter : RecyclerView.Adapter<PageAdapter.VH>() {
    private val itemPool = RecyclerView.RecycledViewPool()
    private val pages = listOf("jar", "toka", "paul", "savings", "british")

    private val pageIndexes = mapOf(
        "jar" to 0,
        "toka" to 1,
        "paul" to 2,
        "savings" to 3,
        "british" to 4
    )

    private fun getOptions(account: String) = FirestoreRecyclerOptions.Builder<Transaction>()
        .setQuery(
            MainActivity.db.collection("transactions")
                .whereEqualTo("account", account)
                .orderBy("date"),
            Transaction::class.java
        ).build()

    private val itemAdapters = List(5) {
        lazy { ItemAdapter(getOptions(pages[it])) }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = pages.size

    override fun getItemId(position: Int) = pages[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(PageUI(itemPool), parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(pages[position])
    }

    inner class VH(private val ui: PageUI, parent: ViewGroup) :
        RecyclerView.ViewHolder(ui.createView(AnkoContext.create(parent.context, parent))) {
        fun bind(account: String) {
            val index = pageIndexes[account] ?: 0

            ui.itemsList.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply {
                        color = MainActivity.colors[index]
                    }
                }
            }
            ui.itemsList.swapAdapter(itemAdapters[index].value, true)
        }
    }
}
