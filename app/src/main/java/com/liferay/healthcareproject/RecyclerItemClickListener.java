package com.liferay.healthcareproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    GestureDetector gestureDetector;

    private OnItemClickListener listener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {

        this.listener = listener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

        if (childView != null && listener != null && gestureDetector.onTouchEvent(motionEvent)) {

            childView.setPressed(true);

            listener.onItemClick(childView, recyclerView.getChildLayoutPosition(childView));
        }

        return false;

    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean status) {

    }

    public interface OnItemClickListener {

        public void onItemClick(View view, int position);

    }

}