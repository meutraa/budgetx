package host.lost.budgetx.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.R
import com.google.android.material.textfield.TextInputLayout

class TextInputAutoCompleteTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout?
        get() {
            var parent = parent
            while (parent is View) {
                if (parent is TextInputLayout) {
                    return parent
                }
                parent = parent.getParent()
            }
            return null
        }

    private val hintFromLayout: CharSequence?
        get() {
            val layout = textInputLayout
            return layout?.hint
        }

    override fun getHint(): CharSequence? {
        // Certain test frameworks expect the actionable element to expose its hint as a label. When
        // TextInputLayout is providing our hint, retrieve it from the parent layout.
        val layout = textInputLayout
        return if (layout != null) {
            layout.hint
        } else super.getHint()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(outAttrs)
        if (ic != null && outAttrs.hintText == null) {
            // If we don't have a hint and our parent is a TextInputLayout, use its hint for the
            // EditorInfo. This allows us to display a hint in 'extract mode'.
            outAttrs.hintText = hintFromLayout
        }
        return ic
    }
}