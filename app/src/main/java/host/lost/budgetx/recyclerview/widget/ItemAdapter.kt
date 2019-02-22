package host.lost.budgetx.recyclerview.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import host.lost.budgetx.MainActivity
import host.lost.budgetx.dip
import host.lost.budgetx.view.TransactionView

class ItemAdapter(
    activity: MainActivity,
    account: String,
    private val position: Int
) : ListAdapter<DocumentSnapshot, ItemAdapter.VH>(
    object : DiffUtil.ItemCallback<DocumentSnapshot>() {
        override fun areItemsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(a: DocumentSnapshot, b: DocumentSnapshot) = a == b
    }
) {

    companion object {
        var params: ViewGroup.LayoutParams? = null
    }

    init {
        setHasStableIds(true)
        synchronized(MainActivity.isFirestoreReady) {
            activity.db.collection("transactions")
                .whereEqualTo("account", account)
                .whereEqualTo("deleted", false)
                .orderBy("date")
                .addSnapshotListener(MainActivity.pool, EventListener { snapshot, ex ->
                    if (null != ex) {
                        ex.printStackTrace()
                        return@EventListener
                    }
                    snapshot?.apply {
                        submitList(documents)
                        var value = 0.0
                        documents.forEach {
                            value += ((it["value"] as Number).toDouble())
                        }
                        activity.homeItem.post {
                            activity.homeItem.setText(position, value)
                        }
                    }
                })
        }
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong()

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.ui.setTransaction(item, position)
        holder.ui.setOnClickListener {
            (it.context as? MainActivity)?.showDialog(item)
        }
        holder.ui.setOnLongClickListener {
            (it.context as? MainActivity)?.showDialog(null, item["account"] as String? ?: "")
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        TransactionView(parent.context).apply {
            if (null == params) {
                params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    parent.context.dip(48)
                )
            }
            layoutParams = params
        }
    )

    inner class VH(val ui: TransactionView) : RecyclerView.ViewHolder(ui)
}
