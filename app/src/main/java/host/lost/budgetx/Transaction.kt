package host.lost.budgetx

import com.google.firebase.Timestamp

class Transaction {
    var account: String = ""
    var category: String = ""
    var comment: String = ""
    var date: Timestamp? = null
    var deleted: Boolean = false
    var id: Int = 0
    var isExpanded: Boolean = false
    var value: Float = 0.0f

    fun setIsExpanded(isExpanded: Boolean) {
        this.isExpanded = isExpanded
    }

    fun getIsExpanded() = isExpanded
}
