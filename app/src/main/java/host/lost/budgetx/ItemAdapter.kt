package host.lost.budgetx

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import java.util.concurrent.Executors

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
        val pool = Executors.newWorkStealingPool()
    }

    init {
        setHasStableIds(true)
        activity.db.collection("transactions")
            .whereEqualTo("account", account)
            .whereEqualTo("deleted", false)
            .orderBy("date")
            .addSnapshotListener(pool, EventListener { snapshot, ex ->
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
                        activity.homeItem.setValue(position, value)
                    }
                }
            })
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        TransactionView(parent.context).apply {
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.context.dip(48))
        }
    )

    inner class VH(private val ui: TransactionView) :
        RecyclerView.ViewHolder(ui) {
        fun bind(item: DocumentSnapshot) {
            ui.setTransaction(item, adapterPosition)
            ui.setOnClickListener {
                //                MainActivity.dialog(ui.context, item)
            }
        }
    }
}
