package dora.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import kotlin.properties.Delegates

class ClearableEditText @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatEditText(context, attrs, defStyle), OnFocusChangeListener, TextWatcher {

    private var iconVerticalMargin: Int = 0
    private var iconMarginLeft: Int = 0
    private var iconMarginRight: Int = 0
    private var clearDrawable: Drawable? = null
    private var clearView: ImageView
    private var iconSize by Delegates.notNull<Int>()
    private var isClearIconVisible = false

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text!!.isNotEmpty())
        } else {
            setClearIconVisible(false)
        }
    }

    fun setClearIconVisible(visible: Boolean) {
        this.isClearIconVisible = visible
        invalidate()
    }

    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        setClearIconVisible(s.isNotEmpty())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {}

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText)
        clearDrawable = a.getDrawable(R.styleable.ClearableEditText_dora_clearIcon)
        iconVerticalMargin = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_dora_clearIconVerticalMargin, iconVerticalMargin)
        iconMarginLeft = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_dora_clearIconMarginLeft, iconMarginLeft)
        iconMarginRight = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_dora_clearIconMarginRight, iconMarginRight)
        a.recycle()
        if (clearDrawable == null) {
            clearDrawable = resources.getDrawable(R.drawable.ic_clear)
        }
        setClearIconVisible(false)
        onFocusChangeListener = this
        addTextChangedListener(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (clearDrawable!!.bounds.contains(event.x.toInt() + scrollX, event.y.toInt())) {
                    setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, clearDrawable!!.intrinsicHeight
                    + iconVerticalMargin * 2)
        }
        clearView.measure(widthMeasureSpec, heightMeasureSpec)
        iconSize = measuredHeight - iconVerticalMargin * 2
        setPadding(iconMarginRight, 0, iconMarginLeft + iconSize + iconMarginRight, 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isClearIconVisible) {
            clearView.draw(canvas)
        }
        clearDrawable!!.setBounds((measuredWidth - iconSize - iconMarginRight) + scrollX,
                iconVerticalMargin, (measuredWidth - iconMarginRight) + scrollX,
                measuredHeight - iconVerticalMargin)
    }

    init {
        initAttrs(context!!, attrs!!)
        clearView = ImageView(context)
        clearView.setImageDrawable(clearDrawable)
        gravity = Gravity.CENTER_VERTICAL
        isFocusableInTouchMode = true
        requestFocus()
        setSingleLine()
        setLines(1)
    }
}