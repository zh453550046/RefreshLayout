package uk.co.imallan.jellyrefreshlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/3.
 */
public class ZRefreshLayout extends FrameLayout {

    private View child, headView;
    private float headHeihgt, springHeight;
    private float downY, distanceY;
    private ZSpringFrameLayout zSpringFrameLayout;
    private boolean onRefreshing, intercept;

    public ZRefreshLayout(Context context) {
        super(context);
        init();
    }

    public ZRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        headHeihgt = headHeihgt == 0 ? TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()) : headHeihgt;
        springHeight = springHeight == 0 ? headHeihgt / 2 : springHeight;
        if (headView == null) {
            headView = LayoutInflater.from(getContext()).inflate(R.layout.z_head_layout, null);
            zSpringFrameLayout = (ZSpringFrameLayout) headView.findViewById(R.id.zly);
        }
        addView(headView);
        ViewGroup.LayoutParams layoutParams = headView.getLayoutParams();
        layoutParams.height = (int) (headHeihgt + springHeight);
        headView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 2) {
            throw new RuntimeException("ZRefreshLayout only support one child");
        } else {
            if (getChildCount() > 1) {
                child = getChildAt(1);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (ev.getY() - downY > 0) {
                if (child != null) {
                    if (!ViewCompat.canScrollVertically(child, -1)) {
                        if (onRefreshing) {
                            return super.dispatchTouchEvent(ev);
                        }
                        if (!intercept) {
                            downY = ev.getY();
                        }
                        intercept = true;
                        requestDisallowInterceptTouchEvent(false);
                    }
                } else {
                    intercept = true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        if (child == null) {
//            intercept = true;
//        }
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            downY = event.getY();
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            if (event.getY() - downY > 0) {
//                if (!ViewCompat.canScrollVertically(child, -1)) {
//                    return true;
//                }
//            }
//            downY = event.getY();
//        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);
        if (onRefreshing) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                distanceY += event.getY() - downY;
                if (distanceY < 0) {
                    distanceY = 0;
                    zSpringFrameLayout.setcontentHeight(distanceY);
                    zSpringFrameLayout.invalidate();
                    downY = event.getY();
                    if (child != null) {
                        child.setTranslationY(distanceY);
                    }
                    return super.onTouchEvent(event);
                }
                if (distanceY > headHeihgt + springHeight) {
                    distanceY = headHeihgt + springHeight;
                }
                if (child != null) {
                    child.setTranslationY(distanceY);
                }
                if (distanceY <= headHeihgt) {
                    zSpringFrameLayout.setcontentHeight(distanceY);
                    zSpringFrameLayout.setSpringHeight(0);
                } else {
                    zSpringFrameLayout.setcontentHeight(headHeihgt);
                    zSpringFrameLayout.setSpringHeight(distanceY - headHeihgt);
                    if (onPullingListenner != null) {
                        onPullingListenner.onPulling(headView, (distanceY - headHeihgt) / springHeight);
                    }
                }
                downY = event.getY();
                zSpringFrameLayout.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                readyReresh();
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    private void readyReresh() {
        if (shouldRefresh()) {
            if (onReleaseListenner != null) {
                onReleaseListenner.onRelease(headView);
            }
            if (onRefreshListener != null) {
                onRefreshListener.onRefresh();
            }
            ValueAnimator animator = ValueAnimator.ofFloat(distanceY, headHeihgt);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float height = (float) animation.getAnimatedValue();
                    if (child != null) {
                        child.setTranslationY(height);
                    }
                    zSpringFrameLayout.setSpringHeight(height - height);
                    zSpringFrameLayout.invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator(3f));
            animator.setDuration(200);
            animator.start();

        } else if (zSpringFrameLayout.getContentHeight() > 0) {
            ValueAnimator animator = ValueAnimator.ofFloat(distanceY, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float contentHeight = (float) animation.getAnimatedValue();
                    zSpringFrameLayout.setcontentHeight(contentHeight);
                    zSpringFrameLayout.invalidate();
                    if (child != null) {
                        child.setTranslationY(contentHeight);
                    }
                }
            });
            animator.setDuration(400);
            animator.start();
        }
        distanceY = 0;
    }

    public void finishRefresh() {
        if (zSpringFrameLayout.getContentHeight() > 0) {
            if (onReleaseListenner != null) {
                onReleaseListenner.onFinishRefresh(headView);
            }
            zSpringFrameLayout.setSpringHeight(0);
            ValueAnimator animator = ValueAnimator.ofFloat(zSpringFrameLayout.getContentHeight(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float contentHeight = (float) animation.getAnimatedValue();
                    zSpringFrameLayout.setcontentHeight(contentHeight);
                    zSpringFrameLayout.invalidate();
                    if (child != null) {
                        child.setTranslationY(contentHeight);
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onRefreshing = false;
                }
            });
            animator.setDuration(200);
            animator.start();
        }
    }

    private boolean shouldRefresh() {
        intercept = false;
        if (child.getTranslationY() > headHeihgt) {
            onRefreshing = true;
            return true;
        }
        return false;
    }

    public void StartAutoRefresh() {
        if (!onRefreshing) {
            onRefreshing = true;
            ValueAnimator animator = ValueAnimator.ofFloat(0, headHeihgt + springHeight, 2 * headHeihgt + 2 * springHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    distanceY = (float) animation.getAnimatedValue();
                    if (distanceY > headHeihgt + springHeight) {
                        distanceY = headHeihgt + springHeight;
                    }
                    if (distanceY < headHeihgt) {
                        zSpringFrameLayout.setcontentHeight(distanceY);
                    } else {
                        zSpringFrameLayout.setcontentHeight(headHeihgt);
                        zSpringFrameLayout.setSpringHeight(distanceY - headHeihgt);
                    }
                    if (child != null) {
                        child.setTranslationY(distanceY);
                    }
                    zSpringFrameLayout.invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    readyReresh();
                }
            });
            animator.setDuration(2000);
            animator.start();
        }
    }

    public void setHeadHeihgt(float headHeihgt) {
        this.headHeihgt = headHeihgt;
    }

    public void setSpringHeight(float springHeight) {
        this.springHeight = springHeight;
    }

    public void setHeadView(View headView) {
        this.headView = headView;
    }

    private OnPullingListenner onPullingListenner;

    public void setOnPullingListenner(OnPullingListenner onPullingListenner) {
        this.onPullingListenner = onPullingListenner;
    }

    private OnReleaseListenner onReleaseListenner;

    public void setOnReleaseListenner(OnReleaseListenner onReleaseListenner) {
        this.onReleaseListenner = onReleaseListenner;
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    interface OnRefreshListener {
        void onRefresh();
    }
}
