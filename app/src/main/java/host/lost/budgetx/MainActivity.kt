package host.lost.budgetx

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//the same for the views
inline fun View.dip(value: Int): Int = context.dip(value)

inline fun View.dip(value: Float): Int = context.dip(value)
inline fun View.sp(value: Int): Int = context.sp(value)
inline fun View.sp(value: Float): Int = context.sp(value)

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

//        fun dialog(context: Context, item: Transaction) = context.alert {
//            customView {
//                constraintLayout {
//                    id = View.generateViewId()
//                    imageView {
//                        id = iconId
//                        setImageDrawable(TransactionView.icons[item.category])
//                    }.lparams(width = dip(48), height = dip(48)) {
//                        startToStart = PARENT_ID
//                        topToTop = PARENT_ID
//                        bottomToBottom = PARENT_ID
//                    }
//                    textInputLayout {
//                        id = valueTilId
//                        hint = "Value"
//                        themedTextInputEditText(R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox) {
//                            id = valueId
//                            inputType =
//                                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
//                            setText(item.value.toString())
//                        }
//                    }.lparams(width = matchConstraint, height = wrapContent) {
//                        topToTop = PARENT_ID
//                        startToEnd = iconId
//                        endToStart = dateId
//                    }
//                    textInputLayout {
//                        id = commentTilId
//                        hint = "Comment"
//                        textInputEditText {
//                            id = commentId
//                            inputType =
//                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
//                            setText(item.comment)
//                        }
//                    }.lparams(width = matchConstraint, height = wrapContent) {
//                        topToBottom = valueTilId
//                        startToEnd = iconId
//                        endToStart = dateId
//                        bottomToBottom = PARENT_ID
//                    }
//                }
//                cancelButton {
//                    it.dismiss()
//                }
//                okButton {
//                    it.dismiss()
//                }
//            }
//        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setBackgroundDrawable(null)

        lateinit var pages: ViewPager2
        lateinit var homeItem: BottomItemView

        pages = ViewPager2(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 0, 0, dip(52))
            adapter = PageAdapter()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    homeItem.checkedItem = position
                }
            })
        }
        homeItem = BottomItemView(this).apply {
            layoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(52)).apply {
                    gravity = Gravity.BOTTOM
                }
            setOnItemClickListener { position: Int ->
                pages.post {
                    pages.setCurrentItem(position, true)
                }
            }
        }
        setContentView(FrameLayout(this).apply {
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            addView(pages)
            addView(homeItem)
        })
    }
}
