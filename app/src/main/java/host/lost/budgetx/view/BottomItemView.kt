package host.lost.budgetx.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import host.lost.budgetx.MainActivity
import host.lost.budgetx.R
import host.lost.budgetx.dip
import java.text.DecimalFormat
import kotlin.math.roundToInt

class BottomItemView(context: Context) : View(context) {

    private val items = mutableListOf(
        "Home", "Toka", "Paul", "Savings", "British"
    )
    private val nf = DecimalFormat("##.##")

    companion object {
        val icons = MutableList<VectorDrawable?>(5) {
            null
        }

        fun getIconByPosition(position: Int) = when (position) {
                0 -> R.drawable.menu_home
                1 -> R.drawable.menu_toka
                2 -> R.drawable.menu_paul
                3 -> R.drawable.menu_savings
                else -> R.drawable.menu_british
            }
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.argb(255, 0, 0, 0)
        textAlign = Paint.Align.CENTER
        textSize = 24.0f
    }

    private var itemWidth = 0
    private var textMargin = 0.0f
    private var itemWidthHalf = 0.0f
    private lateinit var itemOffsets: List<Int>
    private lateinit var itemOffsetsMiddle: List<Float>
    private var uncheckedColor = Color.GRAY

    var checkedItem: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    fun setText(position: Int, value: Double) {
        items[position] = nf.format(value)
        invalidate()
    }

    fun setOnItemClickListener(onClickListener: BottomItemView.(pos: Int) -> Unit) {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val position = when {
                    event.rawX < itemOffsets[1] -> 0
                    event.rawX < itemOffsets[2] -> 1
                    event.rawX < itemOffsets[3] -> 2
                    event.rawX < itemOffsets[4] -> 3
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
        return icons[position] ?: let {
            icons[position] =
                (ContextCompat.getDrawable(
                    context, getIconByPosition(position)
                ) as? VectorDrawable)?.apply {
                    val diff = ((itemWidth - dip(32)) / 2.0).roundToInt()
                    setBounds(
                        diff + itemOffsets[position],
                        dip(2),
                        dip(32) + diff + itemOffsets[position],
                        dip(34)
                    )
                }
            icons[position]
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (itemWidth == 0) {
            itemWidth = (width / 5.0).roundToInt()
            itemWidthHalf = (itemWidth / 2.0).roundToInt().toFloat()
            textMargin = dip(44).toFloat()
            itemOffsets = List(5) { it * itemWidth }
            itemOffsetsMiddle = List(5) { itemOffsets[it] + itemWidthHalf }
        }

        (0..4).forEach {
            paint.color = if (it == checkedItem) MainActivity.colors[it] else uncheckedColor
            canvas.drawText(items[it], itemOffsetsMiddle[it], textMargin, paint)
            getIcon(it)?.apply {
                setColorFilter(paint.color, PorterDuff.Mode.SRC_ATOP)
                draw(canvas)
            }
        }
    }
}