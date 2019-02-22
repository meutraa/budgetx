package host.lost.budgetx.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import host.lost.budgetx.MainActivity
import host.lost.budgetx.dip
import host.lost.budgetx.recyclerview.widget.ItemAdapter
import host.lost.budgetx.recyclerview.widget.ItemAdapter.Companion.params
import host.lost.budgetx.view.EditItemView
import host.lost.budgetx.view.TransactionView

class CommentAdapter(context: Context, private val editItemView: EditItemView) :
    ArrayAdapter<CommentAdapter.DropdownItem>(
        context,
        android.R.layout.simple_dropdown_item_1line, mutableListOf()
    ) {

    class DropdownItem(val item: DocumentSnapshot) {
        override fun toString() = item["comment"] as? String ?: ""
    }

    init {
        setNotifyOnChange(false)
        synchronized(MainActivity.isFirestoreReady) {
            (context as MainActivity).db.collection("transactions")
                .whereEqualTo("deleted", false)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (null != exception) {
                        exception.printStackTrace()
                        return@addSnapshotListener
                    }

                    clear()
                    snapshot?.documents?.forEach {
                        add(DropdownItem(it))
                    }
                    editItemView.post {
                        notifyDataSetChanged()
                    }
                }
        }
    }

    override fun hasStableIds() = true

    override fun getItemId(position: Int) = getItem(position)?.item?.id?.hashCode()?.toLong() ?: 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView ?: TransactionView(context, true).apply {
            if (null == ItemAdapter.params) {
                ItemAdapter.params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    parent.context.dip(48)
                )
            }
            layoutParams = ItemAdapter.params
        }) as TransactionView
        view.setTransaction(getItem(position)!!.item, position)
        view.setOnClickListener {
            editItemView.copyTransactionDetails(getItem(position)!!.item)
        }
        return view
    }
}