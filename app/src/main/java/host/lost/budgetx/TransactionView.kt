package host.lost.budgetx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.jetbrains.anko.dip
import java.text.SimpleDateFormat

class TransactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        val paint = Paint().apply {
            color = Color.argb(255, 0, 0, 0)
            textSize = 24.0f
            isAntiAlias = true
        }
        val df = SimpleDateFormat.getDateInstance()
        val icons = mutableMapOf<String, VectorDrawable?>()
    }

    private var transaction: Transaction? = null

    fun setTransaction(item: Transaction) {
        transaction = item
        invalidate()
    }

    private fun getIcon(category: String) =
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
                    setBounds(0, 0, dip(32), dip(32))
                }
            icons[category]
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        transaction?.apply {
            val left = dip(48).toFloat()
            canvas.drawText(value.toString(), left, dip(12).toFloat(), paint)
            canvas.drawText(
                if (date != null) df.format(date?.toDate()) else "N/A",
                dip(220).toFloat(),
                dip(20).toFloat(),
                paint
            )
            canvas.drawText(comment, left, dip(28).toFloat(), paint)
            getIcon(category)?.draw(canvas)
        }
    }
}