package com.perqin.gandear.floatingwindow.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author   : perqin
 * Date     : 17-4-28
 */

public class DraggableToggleImageButton extends AppCompatImageView {
    private OnDraggableToggleButtonTouchEvent mListener;
    private boolean mDraggable;
    private float mLastX;
    private float mLastY;

    public DraggableToggleImageButton(Context context) {
        super(context);

        init();
    }

    public DraggableToggleImageButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setOnDraggableButtonTouchEventListener(OnDraggableToggleButtonTouchEvent listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_MOVE == event.getAction()) {
            final float x = event.getRawX();
            final float y = event.getRawY();
            // Send drag info
            if (mDraggable && mListener != null) {
                mListener.onDrag(x - mLastX, y - mLastY);
            }
            mLastX = x;
            mLastY = y;
        } else if (MotionEvent.ACTION_DOWN == event.getAction()) {
            mLastX = event.getRawX();
            mLastY = event.getRawY();
        } else if (MotionEvent.ACTION_UP == event.getAction()) {
            mListener.onRelease();
            mDraggable = false;
        } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
            mDraggable = false;
        }
        return super.onTouchEvent(event);
    }

    private void init() {
        mDraggable = false;
        setOnLongClickListener(new OnLongClickListenerInternal());
    }

    public interface OnDraggableToggleButtonTouchEvent {
        void onDrag(float dx, float dy);
        void onRelease();
    }

    private class OnLongClickListenerInternal implements OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            mDraggable = true;
            return true;
        }
    }
}
