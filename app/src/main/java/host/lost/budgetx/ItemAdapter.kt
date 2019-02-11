package host.lost.budgetx

import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ItemAdapter(
    @NonNull options: FirestoreRecyclerOptions<Transaction>
) :
    FirestoreRecyclerAdapter<Transaction, ItemAdapter.VH>(options) {

    init {
        startListening()
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Transaction) {
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        TransactionView(parent.context).apply {
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.context.dip(48))
        }
    )

    inner class VH(private val ui: TransactionView) :
        RecyclerView.ViewHolder(ui) {
        fun bind(item: Transaction) {
            ui.setTransaction(item, adapterPosition)
            ui.setOnClickListener {
                //                MainActivity.dialog(ui.context, item)
            }
        }
    }
}
