package host.lost.budgetx

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.design.themedTextInputEditText

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

    private val ui = MainActivityUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        ui.setContentView(this)
    }
}
