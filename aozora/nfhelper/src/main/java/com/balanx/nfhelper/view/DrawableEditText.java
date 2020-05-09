package com.balanx.nfhelper.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author：ZXC
 * date: 2017/10/19 09:51
 */

public class DrawableEditText extends AppCompatEditText {

    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private OnDrawableClick mRightDrawableClickListener, mLeftDrawableClickListener;



    public DrawableEditText(Context context) {
        this(context, null);
    }

    public DrawableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
//        this(context, attrs, 0);
    }

    public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
//        setBackground(null);
    }

    protected void setLeftDrawableVisible(boolean visible) {
        Drawable left = visible ? mLeftDrawable : null;
        setCompoundDrawables(left,
                getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
    }

    protected void setRightDrawableVisible(boolean visible) {
        Drawable right = visible ? mRightDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (getCompoundDrawables()[2] != null) {
                    boolean touchRight = event.getX() > (getWidth()
                            - getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth())
                            && (event.getX() < ((getWidth() - getPaddingRight())));
                    if (touchRight)
                        clickRight();
                }

                if (getCompoundDrawables()[0] != null){
                    boolean touchLeft = event.getX() > getPaddingLeft()
                            && (event.getX() < ((getPaddingLeft() + getCompoundDrawables()[0].getIntrinsicWidth())));
                    if (touchLeft)
                        clickLeft();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    protected void clickRight(){
        if (mRightDrawableClickListener != null)
            mRightDrawableClickListener.onClick();
    }

    protected void clickLeft(){
        if (mLeftDrawableClickListener != null)
            mLeftDrawableClickListener.onClick();
    }

    public void setRightDrawableClickListener(OnDrawableClick listener) {
        this.mRightDrawableClickListener = listener;
    }

    public void setLeftDrawableClickListener(OnDrawableClick listener) {
        this.mLeftDrawableClickListener = listener;
    }

    public Drawable getRightDrawable() {
        return mRightDrawable;
    }

    public void setRightDrawable(Drawable drawable) {
        this.mRightDrawable = drawable;
        setRightDrawableVisible(true);
    }

    public Drawable getLeftDrawable() {
        return mLeftDrawable;
    }

    public void setLeftDrawable(Drawable drawable) {
        this.mLeftDrawable = drawable;
        setLeftDrawableVisible(true);
    }

    public interface OnDrawableClick{
        void onClick();
    }
}
