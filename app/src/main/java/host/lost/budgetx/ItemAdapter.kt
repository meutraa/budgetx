package host.lost.budgetx

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jetbrains.anko.AnkoContext

class ItemAdapter(@NonNull options: FirestoreRecyclerOptions<Transaction>) :
    ListAdapter<Transaction, ItemAdapter.VH>(
        object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }), LifecycleObserver {

    /**
     * Returns the backing [ObservableSnapshotArray] used to populate this adapter.
     *
     * @return the backing snapshot array
     */
    @get:NonNull
    val snapshots: ObservableSnapshotArray<Transaction> = options.snapshots

    override fun getItemCount() = snapshots.size

    init {
        if (options.owner != null) {
            options.owner!!.lifecycle.addObserver(this)
        }
        snapshots.addChangeEventListener(object : ChangeEventListener {
            override fun onChildChanged(
                @NonNull type: ChangeEventType,
                @NonNull snapshot: DocumentSnapshot,
                newIndex: Int,
                oldIndex: Int
            ) {
                submitList(snapshots)
            }

            override fun onDataChanged() {}

            override fun onError(@NonNull e: FirebaseFirestoreException) {
                Log.w("FirebaseAdapter", "onError", e)
            }
        })
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemUI(), parent)

    inner class VH(private val ui: ItemUI, parent: ViewGroup) :
        RecyclerView.ViewHolder(ui.createView(AnkoContext.create(parent.context, parent))) {
        fun bind(item: Transaction) {
            ui.view.setTransaction(item)
        }
    }
}
