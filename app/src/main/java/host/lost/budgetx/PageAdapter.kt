package host.lost.budgetx

import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PageAdapter(private val activity: MainActivity) : RecyclerView.Adapter<PageAdapter.VH>() {
    private val itemPool = RecyclerView.RecycledViewPool()
    private val pages = listOf("jar", "toka", "paul", "savings", "british")

    private val itemAdapters = List(5) {
        lazy { ItemAdapter(activity, pages[it], it) }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = pages.size

    override fun getItemId(position: Int) = pages[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RecyclerView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setHasFixedSize(true)
            setRecycledViewPool(itemPool)
            layoutManager = object : LinearLayoutManager(context) {
                override fun isLayoutRTL(): Boolean {
                    return false
                }
            }.apply {
                stackFromEnd = true
            }
        })

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(pages[position])
    }

    inner class VH(private val rv: RecyclerView) :
        RecyclerView.ViewHolder(rv) {
        fun bind(account: String) {
            val index = pages.indexOf(account)

            rv.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply {
                        color = MainActivity.colors[index]
                    }
                }
            }
            rv.swapAdapter(itemAdapters[index].value, true)
        }
    }
}
