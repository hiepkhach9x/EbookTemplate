package com.ctcstudio.ebooktemplate.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by HungHN on 5/14/2015.
 */
public class EBookViewPagerView extends ViewPager {
    private float mStartDragX;

    private float mEndDragX;

    OnSwipeOutListener mListener;

    public EBookViewPagerView(Context context) {
        super(context);
    }

    public EBookViewPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                mEndDragX = x;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mStartDragX < mEndDragX && getCurrentItem() == 0) {
                mListener.onSwipeOutAtStart();
            } else if (mStartDragX > mEndDragX && getCurrentItem() == getAdapter().getCount() - 1) {
                mListener.onSwipeOutAtEnd();
            }
        }
        return super.onTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtStart();

        public void onSwipeOutAtEnd();
    }
}
