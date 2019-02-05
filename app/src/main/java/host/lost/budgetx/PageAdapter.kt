package host.lost.budgetx

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.AnkoContext

class PageAdapter(private val db: FirebaseFirestore) : RecyclerView.Adapter<PageAdapter.VH>() {
    private val itemPool = RecyclerView.RecycledViewPool()
    private val pages = listOf("jar", "toka", "paul", "savings", "british")

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
            ui.itemsList.adapter = ItemAdapter(
                FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(
                        db.collection("transactions")
                            .whereEqualTo("account", account)
                            .orderBy("date"),
                        Transaction::class.java
                    ).build())
        }
    }
}
