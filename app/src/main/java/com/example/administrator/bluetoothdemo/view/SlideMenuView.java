package com.example.administrator.bluetoothdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.bluetoothdemo.R;

/**
 * Created by Administrator on 2015/9/9.
 */
public class SlideMenuView extends HorizontalScrollView {

    private LinearLayout mLinearLayout;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    private int mScreenWidth;
    private int mMenuWidth;
    private int mMenuPaddingRigth;

    private boolean once = false;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideMenuView, defStyleAttr, 0);

        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.SlideMenuView_PaddingRight:
                    mMenuPaddingRigth = ta.getDimensionPixelOffset(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
                    break;
            }
        }

        ta.recycle();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        /*dp转化为px*/
//        mMenuPaddingRigth = );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!once) {
            mLinearLayout = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mLinearLayout.getChildAt(0);
            mContent = (ViewGroup) mLinearLayout.getChildAt(1);


            TextView textView = (TextView) mMenu.getChildAt(0);
            textView.setMovementMethod(ScrollingMovementMethod.getInstance());



            //mMenu.getLayoutParams().width =
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuPaddingRigth;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;


        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置scroll的偏移量，隐藏menu
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed)
            this.scrollTo(mMenuWidth, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int scrollX = getScrollX();
        int action = ev.getAction();

        //Log.v("onTouch", ev.getX() + " " + ev.getY());

//        if(scrollX == 0 && ev.getX() <= mMenuWidth)
//            return false;

        switch (action) {
            case MotionEvent.ACTION_UP :
                if (scrollX >= mMenuWidth / 2) {
                    TextView textView = (TextView) mMenu.getChildAt(0);
                    textView.append("\nscrollX >= mMenuWidth / 2");
                    this.smoothScrollTo(mMenuWidth, 0);
                } else {
                    TextView textView = (TextView) mMenu.getChildAt(0);
                    textView.append("\nscrollX < mMenuWidth / 2");
                    this.smoothScrollTo(0, 0);
                }

                return true;
            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.v("onInterceptTouchEvent", "onIntercept");
       // return super.onInterceptTouchEvent(ev);
        int scrollx = getScrollX();

        if(scrollx == 0 && ev.getX() <= mMenuWidth) {
            return false;
        } else {
            //Log.v("onInterceptTouchEvent", scrollx + " "+ ev.getX() +" " +mMenuWidth);
            return super.onInterceptTouchEvent(ev);
        }

        //return false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        Log.v("onScrollChanged", l + " " + "oldl=" + oldl);
        mMenu.setTranslationX(l);

//        mContent.setPivotX(0);
//        mContent.setPivotY(mContent.getHeight()/2);
//        mContent.setScaleX(0.7f + 0.3f * l / mMenuWidth);
//        mContent.setScaleY(0.7f + 0.3f * l / mMenuWidth);

        super.onScrollChanged(l, t, oldl, oldt);
    }


}
