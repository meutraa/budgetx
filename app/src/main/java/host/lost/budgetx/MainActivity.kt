package host.lost.budgetx

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.design.themedTextInputEditText
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivity : AppCompatActivity() {

    companion object {
        private val valueId = View.generateViewId()
        private val valueTilId = View.generateViewId()
        private val commentId = View.generateViewId()
        private val commentTilId = View.generateViewId()
        private val iconId = View.generateViewId()
        private val dateId = View.generateViewId()

        val db: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance()
        }

        val colors = List(5) {
            Color.parseColor(
                when (it) {
                    0 -> "#aa00ff"
                    1 -> "#ff1744"
                    2 -> "#2979ff"
                    3 -> "#ff3d00"
                    else -> "#00e676"
                }
            )
        }

        fun dialog(context: Context, item: Transaction) = context.alert {
            customView {
                constraintLayout {
                    id = View.generateViewId()
                    imageView {
                        id = iconId
                        setImageDrawable(TransactionView.icons[item.category])
                    }.lparams(width = dip(48), height = dip(48)) {
                        startToStart = PARENT_ID
                        topToTop = PARENT_ID
                        bottomToBottom = PARENT_ID
                    }
                    textInputLayout {
                        id = valueTilId
                        hint = "Value"
                        themedTextInputEditText(R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox) {
                            id = valueId
                            inputType =
                                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
                            setText(item.value.toString())
                        }
                    }.lparams(width = matchConstraint, height = wrapContent) {
                        topToTop = PARENT_ID
                        startToEnd = iconId
                        endToStart = dateId
                    }
                    textInputLayout {
                        id = commentTilId
                        hint = "Comment"
                        textInputEditText {
                            id = commentId
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                            setText(item.comment)
                        }
                    }.lparams(width = matchConstraint, height = wrapContent) {
                        topToBottom = valueTilId
                        startToEnd = iconId
                        endToStart = dateId
                        bottomToBottom = PARENT_ID
                    }
                }
                cancelButton {
                    it.dismiss()
                }
                okButton {
                    it.dismiss()
                }
            }
        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var pages: RecyclerView?
        var homeItem: BottomItemView? = null

        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        setContentView(
            frameLayout {
                importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
                pages = recyclerView {
                    lparams()
                    setHasFixedSize(true)
                    PagerSnapHelper().attachToRecyclerView(this)
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                recyclerView.findChildViewUnder(0.0f, 0.0f)?.let {
                                    val pos = layoutManager?.getPosition(it)
                                    homeItem?.checkedItem = pos ?: 0
                                }
                            }
                            super.onScrollStateChanged(recyclerView, newState)
                        }
                    })
                    adapter = PageAdapter()
                    setPadding(0, 0, 0, dip(52))
                }.lparams(width = matchParent, height = matchParent)
                homeItem = bottomItemView {
                    setOnItemClickListener { position: Int ->
                        pages?.post {
                            pages?.smoothScrollToPosition(position)
                        }
                    }
                }.lparams(width = matchParent, height = dip(52), gravity = Gravity.BOTTOM)
            })
    }
}
