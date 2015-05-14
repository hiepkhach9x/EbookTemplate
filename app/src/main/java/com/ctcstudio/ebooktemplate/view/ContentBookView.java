package com.ctcstudio.ebooktemplate.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ctcstudio.ebooktemplate.R;

/**
 * Created by HungHN on 5/10/2015.
 */
public class ContentBookView extends View {

    private String textContent = "";

    private int mWidth = 100;

    private int mHeight = 200;

    private int mTextSize = 18;

    private int mTextColor = 0xffA7573E;

    private int mBackground = 0xffFDF8A6;

    private int mPaddingTop = 10;

    private int mPaddingBottom = 10;

    private int mPaddingLeft = 15;

    private int mPaddingRight = 15;

    private float density = 0;

    private float scaledDensity = 0;

    private float spacingMult = 1.0f;

    private float spacingAdd = 10f;

    // name file font on Assets/fonts
    private String mTextFont = "UVNDaLat_R.TTF";

    private TextPaint mTextPaint;

    public ContentBookView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ContentBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ContentBookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        density = context.getResources().getDisplayMetrics().density;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

        mTextSize = (int) (mTextSize * scaledDensity);
        mWidth = (int) (mWidth * density);
        mHeight = (int) (mHeight * density);
        mPaddingTop = (int) (mPaddingTop * density);
        mPaddingBottom = (int) (mPaddingBottom * density);
        mPaddingLeft = (int) (mPaddingLeft * density);
        mPaddingRight = (int) (mPaddingRight * density);

        if (attrs != null) {

            final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ContentBookView, defStyle, 0);
            try {
                mBackground = typedArray.getColor(R.styleable.ContentBookView_bookBackground, mBackground);
                textContent = typedArray.getString(R.styleable.ContentBookView_textContent);
                mWidth = (int) typedArray.getDimension(R.styleable.ContentBookView_bookWidth, mWidth);
                mHeight = (int) typedArray.getDimension(R.styleable.ContentBookView_bookHeight, mHeight);
                mTextSize = (int) typedArray.getDimension(R.styleable.ContentBookView_textSize, mTextSize);
                mTextColor = typedArray.getColor(R.styleable.ContentBookView_textColor, mTextColor);
                mPaddingTop = (int) typedArray.getDimension(R.styleable.ContentBookView_paddingTop, mPaddingTop);
                mPaddingBottom = (int) typedArray.getDimension(R.styleable.ContentBookView_paddingBottom, mPaddingBottom);
                mPaddingLeft = (int) typedArray.getDimension(R.styleable.ContentBookView_paddingLeft, mPaddingLeft);
                mPaddingRight = (int) typedArray.getDimension(R.styleable.ContentBookView_paddingRight, mPaddingRight);
                mTextFont = typedArray.getString(R.styleable.ContentBookView_textFont);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mTextPaint.setAntiAlias(true);
        if (!mTextFont.isEmpty()) {
            try {
                Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + mTextFont);
                mTextPaint.setTypeface(tf);
            } catch (Exception ex) {
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackground);
        StaticLayout staticLayout = new StaticLayout(textContent, mTextPaint, mWidth - mPaddingLeft - mPaddingRight, Layout.Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
        canvas.save();
        canvas.translate(mPaddingLeft, mPaddingTop);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
        invalidate();
    }

    public int getBookWidth() {
        return mWidth;
    }


    public int getBookHeight() {
        return mHeight;
    }

    public int getBookTextSize() {
        return mTextSize;
    }

    public void setBookTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getBookTextColor() {
        return mTextColor;
    }

    public void setBookTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public int getBookBackground() {
        return mBackground;
    }

    public void setBookBackground(int mBackground) {
        this.mBackground = mBackground;
        invalidate();
    }

    public int getBookPaddingTop() {
        return mPaddingTop;
    }

    public void setBookPaddingTop(int mPaddingTop) {
        this.mPaddingTop = mPaddingTop;
        invalidate();
    }

    public int getBookPaddingBottom() {
        return mPaddingBottom;
    }

    public void setBookPaddingBottom(int mPaddingBottom) {
        this.mPaddingBottom = mPaddingBottom;
        invalidate();
    }

    public int getBookPaddingLeft() {
        return mPaddingLeft;
    }

    public void setBookPaddingLeft(int mPaddingLeft) {
        this.mPaddingLeft = mPaddingLeft;
        invalidate();
    }

    public int getBookPaddingRight() {
        return mPaddingRight;
    }

    public void setBookPaddingRight(int mPaddingRight) {
        this.mPaddingRight = mPaddingRight;
        invalidate();
    }

    public String getBookTextFont() {
        return mTextFont;
    }

    public void setBookTextFont(String mTextFont) {
        this.mTextFont = mTextFont;
        if (!mTextFont.isEmpty()) {
            try {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + mTextFont);
                mTextPaint.setTypeface(tf);
            } catch (Exception ex) {

            }
        }
        invalidate();
    }

    public float getSpacingMult() {
        return spacingMult;
    }

    public void setSpacingMult(float spacingMult) {
        this.spacingMult = spacingMult;
        invalidate();
    }

    public float getSpacingAdd() {
        return spacingAdd;
    }

    public void setSpacingAdd(float spacingAdd) {
        this.spacingAdd = spacingAdd;
        invalidate();
    }
}
