package com.suan.weclient.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.suan.weclient.R;

/**
 * Created by lhk on 2/28/14.
 */
public class IndecatorView extends RelativeLayout {


    private LinearLayout backgroundLayout;
    private LinearLayout dotLayout;
    private int dotAmount;
    private int dotWidth;
    private int dotColor;


    private int wholeWidth;

    private boolean indexPlaceSet = false;

    public IndecatorView(Context context) {
        this(context, null);
    }

    public IndecatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndecatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        /*
        get attrs
         */

        /*
        about indecator
         */

        int[] attrsArray = new int[]{
                android.R.attr.id, // 0
                android.R.attr.background, // 1
                android.R.attr.layout_width, // 2
                android.R.attr.layout_height // 3
        };

        TypedArray indecatorAttributes = context.obtainStyledAttributes(attrs,
                attrsArray);

        wholeWidth = indecatorAttributes.getDimensionPixelSize(2, ViewGroup.LayoutParams.MATCH_PARENT);
        Log.e("whole wodth", "" + wholeWidth);

        indecatorAttributes.recycle();




        /*
        about dot
         */

        TypedArray dotAttributes = context.obtainStyledAttributes(attrs,
                R.styleable.Indecator);

        final int indexCount = dotAttributes.getIndexCount();
        for (int i = 0; i < indexCount; ++i) {
            int index = dotAttributes.getIndex(i);
            switch (index) {
                case R.styleable.Indecator_dot_amount:

                    dotAmount = dotAttributes.getInteger(index, 0);

                    break;
                case R.styleable.Indecator_dot_width:

                    dotWidth = (int) dotAttributes.getDimension(index, 0);

                    break;
                case R.styleable.Indecator_dot_color:

                    dotColor = dotAttributes.getColor(index, Color.parseColor("#ffffff"));



                    break;
            }
        }
        dotAttributes.recycle();

        initChild();

    }


    private void initChild() {

        backgroundLayout = new LinearLayout(getContext());

        /*
        init background dot
         */
        for (int i = 0; i < dotAmount; i++) {
            RelativeLayout circleParentLayout = new RelativeLayout(getContext());

            /*
            init dots
             */

            GradientDrawable bgDotDrawable = new GradientDrawable();
            bgDotDrawable.setShape(GradientDrawable.RECTANGLE);
            bgDotDrawable.setColor(dotColor);
            bgDotDrawable.setCornerRadius(dotWidth / 2);

            RelativeLayout dotLayout = new RelativeLayout(getContext());
            dotLayout.setBackground(bgDotDrawable);
            LayoutParams dotParam = new LayoutParams(dotWidth, dotWidth);
            dotParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            /*
            init circle
             */

            circleParentLayout.addView(dotLayout, dotParam);

            TableLayout.LayoutParams circleParentParam = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            backgroundLayout.addView(circleParentLayout, circleParentParam);

        }


        LayoutParams backgroundParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(backgroundLayout, backgroundParam);


        dotLayout = new LinearLayout(getContext());

        GradientDrawable dotDrawable = new GradientDrawable();
        dotDrawable.setShape(GradientDrawable.RECTANGLE);
        dotDrawable.setColor(Color.RED);
        dotDrawable.setCornerRadius(dotWidth / 2);

        dotLayout.setBackground(dotDrawable);


    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!indexPlaceSet) {


            RelativeLayout.LayoutParams dotParam = new RelativeLayout.LayoutParams(dotWidth, dotWidth);

            dotParam.leftMargin = wholeWidth / dotAmount / 2 - dotWidth / 2;

            dotLayout.setLayoutParams(dotParam);

            this.removeView(dotLayout);
            this.addView(dotLayout,dotParam);

        }

    }

    int position = 0;

    public void setIndexScroll(int index, float indexPercent) {
        float percent = (indexPercent + index) / dotAmount;


        RelativeLayout.LayoutParams dotParam = new RelativeLayout.LayoutParams(dotWidth, dotWidth);

        dotParam.leftMargin = position;
        dotParam.topMargin = position;

//        dotLayout.setLayoutParams(dotParam);

/*
        LayoutParams dotParam = new LayoutParams(dotWidth, dotWidth);
        dotParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        dotParam.leftMargin = (wholeWidth / dotAmount / 2 - dotWidth / 2 + (int) (percent * wholeWidth));

        dotLayout.setLayoutParams(dotParam);
*/

//        dotLayout.layout((wholeWidth / dotAmount / 2 - dotWidth / 2 + (int) (percent * wholeWidth)),0,wholeWidth-dotWidth-(wholeWidth / dotAmount / 2 - dotWidth / 2 + (int) (percent * wholeWidth)),0);

        this.removeView(dotLayout);
        this.addView(dotLayout,dotParam);
        position++;


/*

        dotLayout.setLeft((wholeWidth / dotAmount / 2 - dotWidth / 2 + (int) (percent * wholeWidth)));
*/




    }








/*

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        LayoutParams dotParam = new LayoutParams(dotWidth, dotWidth);
        dotParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        dotParam.leftMargin = (int) (event.getX());

        dotParam.topMargin = (int) (event.getY());


        dotLayout.setLayoutParams(dotParam);

        this.removeView(dotLayout);
        this.addView(dotLayout);

        return super.onTouchEvent(event);
    }
*/


    public float dipToPx(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                getResources().getDisplayMetrics());
    }


}
