package host.lost.budgetx.recyclerview.widget

import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import host.lost.budgetx.MainActivity

class PageAdapter(private val activity: MainActivity) : RecyclerView.Adapter<PageAdapter.VH>() {
    private val itemPool = RecyclerView.RecycledViewPool()

    companion object {
        val accounts = listOf("jar", "toka", "paul", "savings", "british")
        val accountsForDisplay = listOf("Jar", "Toka", "Paul", "Savings", "British")
    }

    private val itemAdapters = List(5) {
        lazy { ItemAdapter(activity, accounts[it], it) }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = accounts.size

    override fun getItemId(position: Int) = accounts[position].hashCode().toLong()

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
                initialPrefetchItemCount = 13
            }
        })

    override fun onBindViewHolder(holder: VH, position: Int) {
        val index = accounts.indexOf(accounts[position])

        holder.rv.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply {
                    color = MainActivity.colors[index]
                }
            }
        }
        holder.rv.swapAdapter(itemAdapters[index].value, true)
    }

    inner class VH(val rv: RecyclerView) : RecyclerView.ViewHolder(rv)
}
