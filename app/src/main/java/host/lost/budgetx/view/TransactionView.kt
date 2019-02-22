package host.lost.budgetx.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import host.lost.budgetx.MainActivity
import host.lost.budgetx.R
import host.lost.budgetx.dip
import host.lost.budgetx.sp
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class TransactionView(context: Context, private val compressed: Boolean = false) : View(context) {
    companion object {
        val df = SimpleDateFormat.getDateInstance()!!
        val nf = DecimalFormat("##.##")

        fun getIconResource(category: String) = when (category) {
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

        var backgroundColorOdd = 0
        var leftMargin = 0.0f
        var leftCompressedMargin = 0.0f
        var valueY = 0.0f
        var commentY = 0.0f
        var dateX = 0.0f
        var dateCompressedX = 0.0f
        var dateY = 0.0f
        var dateTextSize = 0.0f
        var textSize = 0.0f
        var iconLeft = 0.0f
        var iconCompressedLeft = 0.0f
        var iconTop = 0.0f
        var iconRight = 0.0f
        var iconCompressedRight = 0.0f
        var iconBottom = 0.0f

        lateinit var datePaint: TextPaint
        lateinit var paint: TextPaint
        val icons = mutableMapOf<String, Paint>()
        private val compressedIcons = mutableMapOf<String, Paint>()

        private var isPaintReady = false
        private var areIconsReady = false
        private var areCompressedIconsReady = false
    }

    private var color: Int = 0
    private var value: String = ""
    private var date: String = ""
    private var comment: String = ""
    private var category: String = ""

    fun setTransaction(item: DocumentSnapshot, position: Int) = MainActivity.pool.execute {
        val value = item["value"] as? Number ?: 0.0
        val date = item["date"] as? Timestamp ?: Timestamp.now()

        this@TransactionView.value = nf.format(value)
        this@TransactionView.date = if (date != null) df.format(date.toDate()) else "N/A"
        color = if (position and 1 == 1) Color.TRANSPARENT else backgroundColorOdd
        comment = item["comment"] as? String ?: "Err"
        category = item["category"] as? String ?: "Err"

        postInvalidate()
    }

    init {
        MainActivity.pool.execute {
            synchronized(leftCompressedMargin) {
                if (compressed && leftCompressedMargin == 0.0f) {
                    leftCompressedMargin = dip(40).toFloat()
                }
            }
            synchronized(backgroundColorOdd) {
                if (backgroundColorOdd == 0) {
                    backgroundColorOdd = Color.argb(255, 245, 245, 245)

                    leftMargin = dip(64).toFloat()
                    valueY = dip(20).toFloat()
                    dateY = valueY
                    commentY = dip(36).toFloat()
                    textSize = dip(12).toFloat()
                    dateTextSize = dip(10).toFloat()
                }
            }
        }

        MainActivity.pool.execute {
            loadIcons(false)
            if (compressed) {
                loadIcons(true)
            }
        }

        MainActivity.pool.execute {
            synchronized(isPaintReady) {
                if (!isPaintReady) {
                    datePaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                            color = Color.argb(255, 0, 0, 0)
                            textSize = sp(10).toFloat()
                            textAlign = Paint.Align.RIGHT
                        }
                    paint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                            color = Color.argb(255, 0, 0, 0)
                            textSize = sp(12).toFloat()
                        }
                    isPaintReady = true
                }
            }
        }
    }

    private fun loadIcons(compressed: Boolean) {
        synchronized(if (compressed) areCompressedIconsReady else areIconsReady) {
            if (!(if (compressed) areCompressedIconsReady else areIconsReady)) {
                if (compressed) iconCompressedLeft = dip(8).toFloat()
                else iconLeft = dip(20).toFloat()
                iconTop = dip(12).toFloat()
                if (compressed) iconCompressedRight = dip(32).toFloat()
                else iconRight = dip(48).toFloat()
                iconBottom = dip(40).toFloat()
                MainActivity.categories.forEach {
                    (if (compressed) compressedIcons else icons)[it] = (ContextCompat.getDrawable(
                        context, getIconResource(it)
                    ) as BitmapDrawable).let {
                        Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                            shader = BitmapShader(
                                it.bitmap,
                                Shader.TileMode.CLAMP,
                                Shader.TileMode.CLAMP
                            ).apply {
                                setLocalMatrix(Matrix().apply {
                                    postTranslate(
                                        if (compressed) iconCompressedLeft else iconLeft,
                                        iconTop
                                    )
                                })
                            }
                        }
                    }
                }
                if (compressed) areCompressedIconsReady = true
                else areIconsReady = true
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (backgroundColorOdd != 0) {
            canvas.drawColor(color)
        }
        if (isPaintReady) {
            if (compressed) {
                if (0.0f == dateCompressedX) {
                    dateCompressedX = width - dip(8).toFloat()
                }
            } else {
                if (0.0f == dateX) {
                    dateX = width - dip(16).toFloat()
                }
            }

            canvas.drawText(date, if (compressed) dateCompressedX else dateX, dateY, datePaint)
            canvas.drawText(
                value, if (compressed) leftCompressedMargin else leftMargin, valueY, paint
            )
            canvas.drawText(
                comment, if (compressed) leftCompressedMargin else leftMargin, commentY, paint
            )
        }
        (if (compressed) compressedIcons else icons)[category]?.let {
            canvas.drawRect(
                if (compressed) iconCompressedLeft else iconLeft, iconTop,
                if (compressed) iconCompressedRight else iconRight, iconBottom,
                it
            )
        }
    }
}