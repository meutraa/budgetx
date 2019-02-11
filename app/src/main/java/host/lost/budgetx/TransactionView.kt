package host.lost.budgetx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class TransactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        val df = SimpleDateFormat.getDateInstance()!!
        val nf = DecimalFormat("##.##")
        private val icons = mutableMapOf<String, VectorDrawable?>()

        fun getIcon(context: Context, category: String) =
            icons[category] ?: let {
                icons[category] =
                    (ContextCompat.getDrawable(
                        context,
                        when (category) {
                            "Food & Drink" -> R.drawable.ic_restaurant_menu
                            "Bills" -> R.drawable.ic_money_off
                            "Personal" -> R.drawable.ic_face
                            "Holiday" -> R.drawable.ic_beach_access
                            "Medical" -> R.drawable.ic_local_pharmacy
                            "Travel" -> R.drawable.ic_airport_shuttle
                            "Pets" -> R.drawable.ic_pets
                            "House" -> R.drawable.ic_home
                            "Income" -> R.drawable.ic_credit_card
                            else -> R.drawable.ic_all_inclusive
                        }
                    ) as? VectorDrawable)?.apply {
                        setBounds(context.dip(16), context.dip(8), context.dip(48), context.dip(40))
                    }
                icons[category]
            }
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.argb(255, 0, 0, 0)
        textSize = sp(12).toFloat()
    }

    private var transaction: Transaction? = null
    private var position: Int = 0

    fun setTransaction(item: Transaction, position: Int) {
        transaction = item
        this.position = position
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val leftMarg = dip(16)

        transaction?.apply {
            canvas.drawColor(
                if (position and 1 == 1) Color.WHITE else Color.argb(
                    255,
                    245,
                    245,
                    245
                )
            )
            val left = dip(48).toFloat()
            canvas.drawText(nf.format(value), left + leftMarg, dip(20).toFloat(), paint)
            paint.textSize = sp(10).toFloat()
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                if (date != null) df.format(date?.toDate()) else "N/A",
                width - dip(16).toFloat(),
                dip(20).toFloat(),
                paint
            )
            paint.textAlign = Paint.Align.LEFT
            paint.textSize = sp(12).toFloat()
            canvas.drawText(comment, left + leftMarg, dip(36).toFloat(), paint)
            getIcon(context, category)?.draw(canvas)
        }
    }
}