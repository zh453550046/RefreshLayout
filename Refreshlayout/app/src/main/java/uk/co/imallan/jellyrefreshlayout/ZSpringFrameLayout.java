package uk.co.imallan.jellyrefreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class ZSpringFrameLayout extends FrameLayout {

    private Paint paint;
    private Path path;
    private float actionY;
    private float contentHeight, springHeight;
    private int springColor;

    public ZSpringFrameLayout(Context context) {
        super(context);
        init();
    }

    public ZSpringFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZSpringFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZSpringFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(springColor == 0 ? ContextCompat.getColor(getContext(), android.R.color.holo_orange_light) : springColor);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                actionY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                distanceY = event.getY() - actionY;
//                if (distanceY < 0)
//                    distanceY = 0;
//                path.reset();
//                path.lineTo(0.0F, contentHeight);
//                path.quadTo(getMeasuredWidth() / 2, distanceY + contentHeight, getMeasuredWidth(), 0 + contentHeight);
//                path.lineTo(getMeasuredWidth(), 0);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                if (distanceY != 0) {
//                    autoUp();
//                }
//                break;
//            default:
//                return super.onTouchEvent(event);
//        }
//        return true;
//    }

//    private void autoUp() {
//        ValueAnimator animator = ValueAnimator.ofFloat(distanceY, 0);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float y = (float) animation.getAnimatedValue();
//                path.reset();
//                path.lineTo(0.0F, contentHeight);
//                path.quadTo(getMeasuredWidth() / 2, y + contentHeight, getMeasuredWidth(), 0 + contentHeight);
//                path.lineTo(getMeasuredWidth(), 0);
//                invalidate();
//            }
//        });
//        animator.setDuration(300);
//        animator.setInterpolator(new OvershootInterpolator(3.0F));
//        animator.start();
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        path.reset();
        if (contentHeight > 0) {
            path.lineTo(0.0F, contentHeight);
            path.quadTo(getMeasuredWidth() / 2, springHeight * 2 + contentHeight, getMeasuredWidth(), 0 + contentHeight);
            path.lineTo(getMeasuredWidth(), 0);
        }
        canvas.drawPath(path, paint);
    }


    public void setcontentHeight(float contentHeight) {
        this.contentHeight = contentHeight;
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public void setSpringHeight(float springHeight) {
        this.springHeight = springHeight;
    }

    public float getSpringHeight() {
        return springHeight;
    }
}