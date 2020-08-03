package com.hcs.familytree.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by wanggang on 2017/4/20.
 * 家族树
 */

public class FamilyTreeView extends ViewGroup {

    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;

    /**
     * 界面可滚动的上边界
     */
    private int topBorder;

    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    /**
     * 界面可滚动的下边界
     */
    private int bottomBorder;

    FamilyTreeAdapter familyTreeAdapter;
    private Paint mPaint;

    int colSpace;
    int lineSpace;
    int radius; // 弧形的半径
    int itemWidth;
    int itemHeight;

    List<Ponit> ponits; // 记录所有有轨迹的点
    ValueAnimator mAnimator;
    float mPercent;

    Stack<PersonView> personViews; // 保存PersonView

    boolean canLayout; // 是否正在添加View
    boolean canMeasure; // 是否正在添加View

    int layoutIndex;

    public FamilyTreeAdapter getFamilyTreeAdapter() {
        return familyTreeAdapter;
    }

    public void setFamilyTreeAdapter(FamilyTreeAdapter familyTreeAdapter) {
        this.familyTreeAdapter = familyTreeAdapter;
    }

    public FamilyTreeView(Context context) {
        super(context);
        init();
    }

    public FamilyTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FamilyTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        personViews = new Stack<>();

        colSpace = MeasureUtils.dip2px(getContext(), 24);
        lineSpace = MeasureUtils.dip2px(getContext(), 32);
        radius = MeasureUtils.dip2px(getContext(), 6);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(MeasureUtils.dip2px(getContext(), 1));
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setAntiAlias(true);
        setWillNotDraw(false);

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(1000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (canMeasure) {
            canMeasure = false;
            for (int i = 0; i < getChildCount(); i++) {
                PersonView childView = (PersonView) getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                measureView(childView);
            }
//            setMeasuredDimension(rightBorder - leftBorder, bottomBorder - topBorder);
        }
    }



    private void measureView(PersonView personView) {
        PersonEntity personEntity = personView.getPersonEntity();
        PersonData personData = personEntity;
        itemWidth = personView.getMeasuredWidth();
        itemHeight = personView.getMeasuredHeight();
        changeMeasurePoint(personData.getCenterPoint());
        int centerX = personData.getCenterPoint().x;
        int centerY = personData.getCenterPoint().y;
        if (centerX - itemWidth < leftBorder) {
            leftBorder = centerX - itemWidth;
        }
        if (centerX + itemWidth > rightBorder) {
            rightBorder = centerX + itemWidth;
        }
        if (centerY - itemHeight < topBorder) {
            topBorder = centerY - itemHeight;
        }
        if (centerY + itemHeight > bottomBorder) {
            bottomBorder = centerY + itemHeight;
        }
    }

    public void changeMeasurePoint(Ponit ponit) {
        ponit.x = ponit.coordinateX * (itemWidth + colSpace);
        ponit.y = - ponit.coordinateY * (itemHeight + lineSpace);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (canLayout) {
            canLayout = false;

            for (int i = 0; i < getChildCount(); i++) {
                PersonView personView = (PersonView) getChildAt(i);
                layoutView(personView);

//                if (i == 0) {
//                    personView.setSelected(true);
//                    personView.setTitleColor(R.color.white);
//                } else {
//                    personView.setSelected(false);
//                    personView.setTitleColor(R.color.black);
//                }
            }
            mAnimator.start();
        }
    }

    /**
     * 通过中心点位置放置view
     */
    private void layoutView(PersonView personView) {
        PersonEntity personEntity = personView.getPersonEntity();
        PersonData personData = personEntity;
        itemWidth = personView.getMeasuredWidth();
        itemHeight = personView.getMeasuredHeight();
        changePoint(personData.getCenterPoint());
        addDrawPoint(personData.getCenterPoint());
        int centerX = personData.getCenterPoint().x;
        int centerY = personData.getCenterPoint().y;
        personView.layout(centerX - itemWidth / 2, centerY - itemHeight / 2,
                centerX + itemWidth / 2, centerY + itemHeight / 2);
        personView.setImage(personData.gender, personData.avatar);
        personView.setTitle(personData.name);
    }

    // 坐标系坐标转换成屏幕坐标
    public void changePoint(Ponit ponit) {
        ponit.x = ponit.x - leftBorder;
        ponit.y = ponit.y - topBorder;
    }

    private void addDrawPoint(Ponit ponit) {
        if (ponits == null) {
            ponits = new LinkedList<>();
        }
        ponits.add(ponit);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ponits != null) {
            for (int i = 0; i < ponits.size(); i++) {
                Ponit ponit = ponits.get(i);
                if (ponit.connecPonit != null) {
                    ponit.drawConnection(itemWidth, itemHeight, lineSpace, colSpace, radius);
                    canvas.drawPath(ponit.getSegment(mPercent), mPaint);
                }
            }
        }
    }

    public void refreshUI() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof PersonView)
                personViews.add((PersonView) getChildAt(i));
        }
        removeAllViews();
        if (ponits != null) {
            ponits.clear();
        }
        int childSize = familyTreeAdapter.dataList.size();

        for (int i = 0; i < childSize; i++) {
            PersonView personView = familyTreeAdapter.getPersonView(getPersonView(), this, familyTreeAdapter.dataList.get(i));
//            addViewInLayout(personView, i, new LayoutParams(DensityUtil.dip2px(getContext(), 60), DensityUtil.dip2px(getContext(), 80)));
            addView(personView);
        }
        canLayout = true;
        canMeasure = true;
        requestLayout();
    }

    private PersonView getPersonView() {
        if (personViews != null && personViews.size() > 0)
            return personViews.pop();
        return null;
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    public int getRealWidth() {
        return rightBorder - leftBorder;
    }

    public int getRealHeight() {
        return bottomBorder - topBorder;
    }

    public int getLeftBorder() {
        return leftBorder;
    }

    public int getTopBorder() {
        return topBorder;
    }

    public int getRightBorder() {
        return rightBorder;
    }

    public int getBottomBorder() {
        return bottomBorder;
    }
}
