package host.lost.budgetx.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import host.lost.budgetx.MainActivity
import host.lost.budgetx.R
import host.lost.budgetx.dip
import host.lost.budgetx.recyclerview.widget.PageAdapter
import host.lost.budgetx.view.TransactionView.Companion.df
import host.lost.budgetx.view.TransactionView.Companion.getIconResource
import host.lost.budgetx.view.TransactionView.Companion.nf
import host.lost.budgetx.widget.CommentAdapter
import java.util.*

class EditItemView(context: Context) : FrameLayout(context) {

    private val previousComments = CommentAdapter(context, this)

    private val etValue = EditText(context).apply {
        maxLines = 1
        focusable = View.FOCUSABLE
        hint = "-100"
        isFocusableInTouchMode = true
        imeOptions = EditorInfo.IME_ACTION_NEXT
        inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
    }

    private val etComment: AutoCompleteTextView = AutoCompleteTextView(context).apply {
        maxLines = 1
        hint = "Cat food"
        imeOptions = EditorInfo.IME_ACTION_DONE
        textSize = 13.0f
        inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        post {
            setAdapter(previousComments)
        }
    }

    private val tvDate = TextView(context).apply {
        setOnClickListener {
            val cal = Calendar.getInstance().apply {
                time = date.toDate()
            }
            DatePickerDialog(it.context, { _, year, month, day ->
                cal[Calendar.YEAR] = year
                cal[Calendar.MONTH] = month
                cal[Calendar.DAY_OF_MONTH] = day
                date = Timestamp(cal.time)
                dateStr = df.format(date.toDate())
                text = dateStr
            }, cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
    }

    private val ivCategory = ImageView(context).apply {
        setPadding(dip(12), dip(12), dip(12), dip(12))
        setOnClickListener {
            lateinit var dialog: DialogInterface
            dialog = AlertDialog.Builder(context)
                .setAdapter(object : ArrayAdapter<String>(
                    context,
                    android.R.layout.select_dialog_singlechoice,
                    MainActivity.categories
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        return ((convertView ?: TextView(context).apply {
                            setPadding(dip(16), 0, dip(16), 0)
                            layoutParams =
                                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(42))
                        }) as TextView).apply {
                            val category = getItem(position) ?: defaultCategory
                            setBackgroundColor(if (position and 1 == 1) Color.WHITE else TransactionView.backgroundColorOdd)
                            gravity = Gravity.CENTER_VERTICAL
                            text = category
                            setCompoundDrawablesWithIntrinsicBounds(
                                getIconResource(category), 0, 0, 0
                            )
                            compoundDrawablePadding = dip(16)
                            setOnClickListener {
                                dialog.dismiss()
                                this@EditItemView.category = category
                                refreshCategory()
                            }
                        }
                    }
                }) { _, _ ->
                }
                .show()
        }
    }

    private val ivAccount = ImageView(context).apply {
        setPadding(dip(12), dip(12), dip(12), dip(12))
        setOnClickListener {
            lateinit var dialog: DialogInterface
            dialog = AlertDialog.Builder(context)
                .setAdapter(object : ArrayAdapter<String>(
                    context,
                    android.R.layout.select_dialog_singlechoice,
                    PageAdapter.accountsForDisplay
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        return ((convertView ?: TextView(context).apply {
                            setPadding(dip(16), 0, dip(16), 0)
                            layoutParams =
                                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(48))
                        }) as TextView).apply {
                            val account = getItem(position) ?: defaultAccount
                            setBackgroundColor(if (position and 1 == 1) Color.WHITE else TransactionView.backgroundColorOdd)
                            gravity = Gravity.CENTER_VERTICAL
                            text = account
                            setCompoundDrawablesWithIntrinsicBounds(
                                BottomItemView.getIconByPosition(position), 0, 0, 0
                            )
                            compoundDrawablePadding = dip(16)
                            setOnClickListener {
                                dialog.dismiss()
                                this@EditItemView.account = account.toLowerCase()
                                refreshAccount()
                            }
                        }
                    }
                }) { _, _ ->
                }
                .show()
        }
    }

    companion object {
        const val defaultAccount = "jar"
        const val defaultCategory = "Food & Drink"
    }

    private var documentSnapshot: DocumentSnapshot? = null
    private var dateStr = ""
    private var value = ""
    private var comment = ""
    private var account = defaultAccount
    private var category = defaultCategory
    private var date: Timestamp = Timestamp.now()

    init {
        addView(
            etValue,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dip(60), dip(4), dip(128), 0)
            })
        addView(
            etComment,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dip(60), dip(56), dip(16), 0)
            })
        addView(
            tvDate,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                setMargins(0, dip(20), dip(16), 0)
            })
        addView(ivCategory, FrameLayout.LayoutParams(dip(48), dip(48)).apply {
            setMargins(dip(8), dip(4), 0, 0)
        })
        addView(ivAccount, FrameLayout.LayoutParams(dip(48), dip(48)).apply {
            setMargins(dip(8), dip(52), 0, 0)
        })
    }

    fun save() {
        val db = (context as? MainActivity)?.db ?: return

        val item = mapOf(
            "comment" to etComment.text.toString(),
            "value" to (etValue.text.toString().toDoubleOrNull() ?: 0.0),
            "category" to category,
            "account" to account,
            "date" to date,
            "deleted" to false
        )
        if (null == documentSnapshot) {
            db.collection("transactions").add(item)
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Add failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            return
        }

        documentSnapshot?.apply {
            reference.update(item)
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Update failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    fun delete() {
        documentSnapshot?.apply {
            reference.update("deleted", true)
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Delete failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    fun clear(account: String) {
        documentSnapshot = null
        dateStr = ""
        value = ""
        comment = ""
        category = defaultCategory
        this@EditItemView.account = account
        date = Timestamp.now()
        dateStr = df.format(date.toDate())
        refreshView()
    }

    fun copyTransactionDetails(item: DocumentSnapshot) {
        MainActivity.pool.execute {
            documentSnapshot = null

            val value = item["value"] as? Number ?: 0.0
            this@EditItemView.value = nf.format(value)
            date = Timestamp.now()
            dateStr = df.format(date.toDate())
            comment = item["comment"] as? String ?: ""
            category = item["category"] as? String ?: defaultCategory
            account = item["account"] as? String ?: defaultAccount

            refreshView()
        }
    }

    fun setTransaction(item: DocumentSnapshot) =
        MainActivity.pool.execute {
            documentSnapshot = item

            val value = item["value"] as? Number ?: 0.0

            this@EditItemView.value = nf.format(value)
            date = (item["date"] as? Timestamp) ?: Timestamp.now()
            dateStr = df.format(date.toDate())
            comment = item["comment"] as? String ?: ""
            category = item["category"] as? String ?: defaultCategory
            account = item["account"] as? String ?: defaultAccount

            refreshView()
        }

    private fun refreshCategory() {
        ivCategory.setImageResource(TransactionView.getIconResource(category))
    }

    private fun refreshAccount() {
        ivAccount.setImageResource(
            when (account) {
                "jar" -> R.drawable.menu_home
                "toka" -> R.drawable.menu_toka
                "paul" -> R.drawable.menu_paul
                "savings" -> R.drawable.menu_savings
                else -> R.drawable.menu_british
            }
        )
    }

    private fun refreshView() {
        post {
            etValue.setText(this@EditItemView.value)
            etValue.setSelection(this@EditItemView.value.length)
            etValue.requestFocus()
            etComment.setText(comment)
            tvDate.text = dateStr
            refreshCategory()
            refreshAccount()
        }
    }
}