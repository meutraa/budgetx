package host.lost.budgetx

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jetbrains.anko.AnkoContext

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemUI(), parent)

    inner class VH(private val ui: ItemUI, parent: ViewGroup) :
        RecyclerView.ViewHolder(ui.createView(AnkoContext.create(parent.context, parent))) {
        fun bind(item: Transaction) {
//            ui.card.setCardBackgroundColor(
//                if (adapterPosition and 1 == 1) Color.WHITE else Color.argb(
//                    255,
//                    245,
//                    245,
//                    245
//                )
//            )
            ui.view.setTransaction(item, adapterPosition)
            ui.view.setOnClickListener {
                MainActivity.dialog(ui.view.context, item)
            }
        }
    }
}
