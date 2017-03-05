package com.akostyukewicz.bottomsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class BottomSheet extends LinearLayout {

    public static final String TAG = "BottomSheet";

    /* Work screen height (screen height without toolbar & status bar height) */
    private int workScreenHeight;

    /* Full screen height */
    private int fullScreenHeight;

    /* Toolbar & status bar height */
    private int topBarsHeight;

    /* Bottomsheet height */
    private int height;

    /* Bottomsheet half height */
    private int halfHeight;

    /* Bottomsheet static half height */
    private int staticHalfHeight;

    /* Maximum Y limit for bottomsheet */
    private int maxYlimit;

    /* Maximum Y limit for bottomsheet in half status */
    private int halfYLimit;

    /* Swipe treshold */
    private int swipeTreshold;

    private int maxYDiffToSwipeClose;

    private int maxHalfYDiffToSwipeClose;

    private boolean halfStatusDisabled;

    private int lastViewHeight;

    private Status status;

    private BottomSheetOnSwipeListener touchListener;

    private BottomSheetBehavior behavior;

    private ViewGroup.LayoutParams layoutParams;

    {
        //Second
        calculateScreenHeights();
        setY(getWorkScreenHeight());
        status = Status.CLOSED;
        setVisibility(INVISIBLE);
        touchListener = new BottomSheetOnSwipeListener(this);
        setOnTouchListener(touchListener);
        setOrientation(VERTICAL);
        setGravity(Gravity.BOTTOM);
    }

    public BottomSheet(Context context) {
        super(context);
    }

    public BottomSheet(Context context, AttributeSet attrs) {
        //First
        super(context, attrs);
    }

    public BottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchListener.onTouch(this, ev))
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        onStateChange();
    }

    private void onStateChange() {
        if(height != getHeight() && getChildCount() > 0) {
            calculateHeightsLimits();
            if (status == Status.HALF_OPENED)
                setY(halfYLimit);
            else if (status == Status.FULL_OPENED)
                setY(maxYlimit);
        }
    }

    public void setStaticHalfHeight(int staticHalfHeight) {
        if (!isHalfStatusDisabled()) {
            if (staticHalfHeight <= 0) {
                this.staticHalfHeight = getHeight();
            } else {
                this.staticHalfHeight = staticHalfHeight;
            }
            calculateHeightsLimits();
        }
    }

    public boolean isOpened() {
        return (status != Status.CLOSED);
    }

    public void setSwipeTreshold(int swipeTreshold) {
        if (swipeTreshold == 0) {
            this.swipeTreshold = getHeightByPercent(20);
        } else {
            this.swipeTreshold = swipeTreshold;
        }
    }

    public boolean isHalfStatusDisabled() {
        return halfStatusDisabled;
    }

    public void setHalfStatusDisabled(boolean halfStatusDisabled) {
        if (!this.halfStatusDisabled && status == Status.HALF_OPENED) {
            scrollToTop();
        }
        this.halfStatusDisabled = halfStatusDisabled;
    }

    private void setHalfYLimit() {
        //Calculate half height from top inner element
        if (staticHalfHeight == 0) {
            ViewGroup innerView = (ViewGroup) getChildAt(0);
            if (innerView != null) {
                halfHeight = innerView.getChildAt(0).getHeight();
                lastViewHeight = height - halfHeight;
                halfYLimit = workScreenHeight - halfHeight;
            }
        }
        //Use static half height (if it's set)
        else {
            this.halfHeight = staticHalfHeight;
            this.halfYLimit = workScreenHeight - halfHeight;
        }
    }

    public void openPanel(boolean open) {
        if (getChildCount() > 0) {
            setVisibility(VISIBLE);
            if (status == Status.CLOSED && open) {
                setY(workScreenHeight);
                if (!halfStatusDisabled) {
                    scrollToHalf();
                }
                else
                    scrollToTop();
            } else if (isOpened() && !open) {
                scrollToBottom(true);
            }
        }
    }

    public void setBehavior(BottomSheetBehavior behavior) {
        this.behavior = behavior;
    }

    public Status getStatus() {
        return status;
    }

    protected int getSwipeTreshold() {
        return swipeTreshold;
    }

    protected int getMaxYDiffToSwipeClose() {
        return maxYDiffToSwipeClose;
    }

    protected int getMaxHalfYDiffToSwipeClose() {
        return maxHalfYDiffToSwipeClose;
    }

    protected void setStatus(Status status) {
        this.status = status;
    }

    protected int getWorkScreenHeight() {
        return workScreenHeight;
    }

    protected int getHeightByPercent(int percent) {
        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;
        return Math.round(((height / 100f) * percent));
    }

    protected int getMaxYLimit() {
        return maxYlimit;
    }

    protected int getHalfYLimit() {
        return halfYLimit;
    }

    /*
    Scroll bottomsheet to top
     */
    public void scrollToTop() {
        clearAnimation();
        animate()
                .y(maxYlimit)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (behavior != null)
                            behavior.onFullOpened();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setStatus(BottomSheet.Status.FULL_OPENED);
                    }
                })
                .start();
    }

    /*
    fullDown == true - scroll to bottom for 1 time
    fullDown == false - scroll to halfheight if bottomsheet was fully opened,
                        scroll to bottom if bottomsheet was half opened
     */
    public void scrollToBottom(boolean fullDown) {
        float toY;
        if (fullDown) {
            toY = getWorkScreenHeight();
            setStatus(BottomSheet.Status.CLOSED);
        } else if (getStatus() == BottomSheet.Status.FULL_OPENED && !halfStatusDisabled) {
            toY = getHalfYLimit();
            setStatus(BottomSheet.Status.HALF_OPENED);
            if (behavior != null)
                behavior.onHalfOpened();
        } else {
            toY = getWorkScreenHeight();
            setStatus(BottomSheet.Status.CLOSED);
        }

        clearAnimation();
        ViewPropertyAnimator animation = animate()
                .y(toY)
                .setInterpolator(new DecelerateInterpolator());
        animation.setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (behavior != null)
                    behavior.onClosed();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (getStatus() == BottomSheet.Status.CLOSED) {
                    setVisibility(View.INVISIBLE);
                }
            }
        });
        animation.start();
    }

    /*
    Scroll bottomsheet to half status
     */
    public void scrollToHalf() {
        clearAnimation();
        animate()
                .y(halfYLimit)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (behavior != null)
                            behavior.onHalfOpened();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        status = Status.HALF_OPENED;
                    }
                })
                .start();
        if (behavior != null)
            behavior.onHalfOpened();
    }

    private void calculateScreenHeights() {
        fullScreenHeight = ScreenTools.getScreenHeight(getContext());
        if (topBarsHeight == 0) {
            topBarsHeight = ScreenTools.getToolbarHeight(getContext()) + ScreenTools.getStatusBarHeight(getContext());
        }
        workScreenHeight = fullScreenHeight - topBarsHeight;
    }

    private void calculateHeightsLimits() {
        layoutParams = getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        height = getHeight();
        maxYlimit = workScreenHeight - height;
        maxYDiffToSwipeClose = -getHeightByPercent(90);
        setHalfYLimit();
        maxHalfYDiffToSwipeClose = -Math.round((halfHeight / 100f) * 50);
        if (swipeTreshold == 0)
            swipeTreshold = getHeightByPercent(20);
    }

    public enum Status {
        FULL_OPENED,
        HALF_OPENED,
        CLOSED
    }

    public interface BottomSheetBehavior {

        void onFullOpened();

        void onHalfOpened();

        void onClosed();

    }
}
