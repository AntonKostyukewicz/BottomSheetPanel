package com.akostyukewicz.bottomsheet;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class BottomSheetOnSwipeListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;
    private BottomSheet bottomSheet;

    private float dY;
    private float movingY;

    private float startYraw;

    public BottomSheetOnSwipeListener(BottomSheet bottomSheet) {
        this.bottomSheet = bottomSheet;
        gestureDetector = new GestureDetector(bottomSheet.getContext(), new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            return (swipe(startYraw - event.getRawY()));
        }
        return false;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            dY = bottomSheet.getY() - motionEvent.getRawY();
            startYraw = motionEvent.getRawY();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            movingY = e2.getRawY() + dY;
            if (!((movingY) < bottomSheet.getMaxYLimit())) {
                bottomSheet.setY(movingY);
                dY = bottomSheet.getY() - e2.getRawY();
                return true;
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return swipe(e1.getRawY() - e2.getRawY());
        }

    }

    private boolean swipe(float diff) {
        if (diff < 0) {
            if (diff <= bottomSheet.getMaxHalfYDiffToSwipeClose()) {
                bottomSheet.scrollToBottom(true);
            } else {
                if (!bottomSheet.isHalfStatusDisabled())
                    bottomSheet.scrollToHalf();
                else {
                    bottomSheet.scrollToTop();

                }
            }
            return true;
        } else if (diff > 0) {
            if (diff >= bottomSheet.getSwipeTreshold()) {
                bottomSheet.scrollToTop();
            } else {
                if (!bottomSheet.isHalfStatusDisabled())
                    bottomSheet.scrollToHalf();
            }
            return true;
        }
        return false;
    }
}