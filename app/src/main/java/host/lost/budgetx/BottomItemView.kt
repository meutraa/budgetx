package host.lost.budgetx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class BottomItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val items = listOf(
        "Home", "Toka", "Paul", "Savings", "British"
    )
    private val icons = MutableList<VectorDrawable?>(5) {
        null
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.argb(255, 0, 0, 0)
        textAlign = Paint.Align.CENTER
        textSize = 24.0f
    }

    private var uncheckedColor = Color.GRAY
    var checkedItem: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    fun setOnItemClickListener(onClickListener: BottomItemView.(pos: Int) -> Unit) {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val off = width / 5.0
                val position = when {
                    event.rawX < off -> 0
                    event.rawX < off * 2 -> 1
                    event.rawX < off * 3 -> 2
                    event.rawX < off * 4 -> 3
                    else -> 4
                }
                onClickListener(this, position)
                checkedItem = position
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun getIcon(position: Int): VectorDrawable? {
        val xoffset = (position * (width / 5.0)).roundToInt()
        return icons[position] ?: let {
            icons[position] =
                (ContextCompat.getDrawable(
                    context,
                    when (position) {
                        0 -> R.drawable.menu_home
                        1 -> R.drawable.menu_toka
                        2 -> R.drawable.menu_paul
                        3 -> R.drawable.menu_savings
                        else -> R.drawable.menu_british
                    }
                ) as? VectorDrawable)?.apply {
                    val diff = (((width / 5.0) - dip(32)) / 2.0).roundToInt()
                    setBounds(diff + xoffset, dip(2), dip(32) + diff + xoffset, dip(34))
                }
            icons[position]
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Icon is 32 wide
        val offset = (width / 5.0).roundToInt()

        (0..4).forEach {
            val itoffset = offset * it
            val color = if (it == checkedItem) MainActivity.colors[it] else uncheckedColor
            paint.color = color
            canvas.drawText(
                items[it],
                (offset / 2.0).roundToInt().toFloat() + itoffset,
                dip(44).toFloat(),
                paint
            )
            getIcon(it)?.apply {
                setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }?.apply {
                draw(canvas)
            }
        }
    }
}