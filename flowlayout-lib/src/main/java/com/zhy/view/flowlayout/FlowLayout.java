package com.zhy.view.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.text.TextUtilsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FlowLayout extends ViewGroup {
    public static final String TAG = "FlowLayout";
    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;

    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    protected List<Integer> mLineWidth = new ArrayList<Integer>();
    private int mGravity;
    private List<View> lineViews = new ArrayList<>();

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mGravity = ta.getInt(R.styleable.TagFlowLayout_tag_gravity, LEFT);
        int layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
        if (layoutDirection == LayoutDirection.RTL) {
            if (mGravity == LEFT) {
                mGravity = RIGHT;
            } else {
                mGravity = LEFT;
            }
        }
        ta.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        Log.i(TAG, "onMeasure ...mShowAll:" + mShowAll + ", cCount:"+cCount);
        boolean isFirstLine = true;
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;


            if (isFirstLine) {
                if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight() - moreWidth) {
                    width = Math.max(width, lineWidth);
                    lineWidth = childWidth;
                    height += lineHeight;
                    lineHeight = childHeight;
                    isFirstLine = false;
                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
            } else {
                if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                    width = Math.max(width, lineWidth);
                    lineWidth = childWidth;
                    height += lineHeight;
                    lineHeight = childHeight;
                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
            }


            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        Log.i(TAG, "height:"+height);
        int cwidth = (modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight());
        int cHeight = (modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
        Log.i(TAG, "cwidth:"+cwidth + ", cHeight:"+cHeight);
        setMeasuredDimension(cwidth, cHeight);

    }

    public static int moreWidth = 100;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        Log.i(TAG, "onLayout ... cCount:"+cCount + ", mShowAll:"+mShowAll);
        boolean isFirstLine = true;


        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            Log.i(TAG, i + " : child.getVisibility() == View.GONE ? "+ (child.getVisibility() == View.GONE));
            if (child.getVisibility() == View.GONE) continue;
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (isFirstLine) {
                if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight() - moreWidth ) {
                    mLineHeight.add(lineHeight);
                    mAllViews.add(lineViews);
                    mLineWidth.add(lineWidth);

                    lineWidth = 0;
                    lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                    lineViews = new ArrayList<View>();
                    isFirstLine = false;
                }
            } else {
                if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                    mLineHeight.add(lineHeight);
                    mAllViews.add(lineViews);
                    mLineWidth.add(lineWidth);

                    lineWidth = 0;
                    lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                    lineViews = new ArrayList<View>();
                }
            }


            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);
        }

        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);



        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();
        Log.i(TAG, "mAllViews lineNum:"+lineNum + ", mLineHeight size:"+mLineHeight.size());
        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);

            lineHeight = mLineHeight.get(i);
            Log.i(TAG, "第"+i+"行view的个数为:"+lineViews.size() + ", lineHeight:"+lineHeight);
            // set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    //  适配了rtl，需要补偿一个padding值
                    left = width - (currentLineWidth + getPaddingLeft()) - getPaddingRight();
                    //  适配了rtl，需要把lineViews里面的数组倒序排
                    Collections.reverse(lineViews);
                    break;
            }

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                Log.i(TAG, "      "+j + "   子view是否显示: (child.getVisibility() == View.GONE)"
                        + (child.getVisibility() == View.GONE));
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                Log.i(TAG, "      lc:"+lc+", tc:"+tc+", rc:"+rc+", bc:"+bc);
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            top += lineHeight;
        }
        mIsInitDataFinished = false;
    }

    public boolean mIsInitDataFinished = false;

    public boolean mShowAll = false;

    public void switchShow() {
        mShowAll = !mShowAll;

        int cCount = getChildCount();
        Log.i(TAG, "switchShow mShowAll:"+mShowAll + ", cCount:"+cCount);
        for (int i = 0; i < cCount; i++) {
            if (mShowAll) {
                getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                getChildAt(i).setVisibility(View.GONE);
            }
        }
        int lineNum = mAllViews.size();
        if (lineNum > 0) {// 处理第一行
            List<View> lineViews = mAllViews.get(0);
            int lineViewSize = lineViews.size();
            for (int j=0; j<lineViewSize; j++) {
                lineViews.get(j).setVisibility(View.VISIBLE);
            }
        }

        requestLayout();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
