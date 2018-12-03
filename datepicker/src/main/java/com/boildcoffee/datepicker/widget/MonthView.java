package com.boildcoffee.datepicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.boildcoffee.datepicker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author zjh
 *         2018/12/3
 */

public class MonthView extends View{
    private final int ROW_CELL_COUNT = 7; //一行7个格子
    private int mCellColumn; //列数

    private int mCellHorizontalSpace; //水平间距
    private int mCellVerticalSpace; //垂直间距

    private int mHeaderSpaceHeight; //头部间隙
    private int mHeaderSpaceBgColor; //头部间隙背景颜色

    private int mHeaderYMHeight; //头部年月高度
    private int mHeaderYMFontSize; //头部年月文字大小
    private int mHeaderYMFontColor; //头部年月文字颜色

    private int mGridLineWidth; //网格线宽度
    private int mGridLineColor;//网格线颜色
    private int mDayFontSize; //普通日期字体大小
    private int mDayFontColor; //普通日期字体颜色
    private int mSelectedDayFontColor; //选中后普通日期字体颜色
    private int mHolidayFontColor; //节假日字体颜色
    private int mDescFontSize; //底部描述字体大小
    private int mDescFontColor; //底部描述字体颜色
    private int mSelectedDescFontColor; //选中后底部描述字体颜色
    private int mSelectedCellBgColor; //选中后格子背景颜色

    private int mYear; //年
    private int mMonth; //月
    private int mDay; //日
    private Calendar mCalendar;

    private int mWidth;
    private int mHeight;

    private int mCellWidth; //每一个网格的宽度
    private int mCellHeight; //每一个网格的高度

    private float mDownX;
    private float mDownY;

    private OnSingleSelectedListener mOnSingleSelectedListener;
    private OnMultiSelectedListener mOnMultiSelectedListener;

    private List<DayCell> mSelectedList = new ArrayList<>(); //选中的集合
    private List<DayCell> mDayCells = new ArrayList<>();
    private Map<String,String> mDescTextMap;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);

    public MonthView(Context context) {
        super(context);
        setAttrDefaultValue(context);
        init();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,R.styleable.YJMonthView_month_view_style);
        init();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context,attrs,defStyleAttr);
        init();
    }

    /**
     * 设置年月后重新生成日期数据
     * @param year 年
     * @param month 月
     */
    public void setYearAndMonth(int year, int month){
        mYear = year;
        mMonth = month;
        mCalendar.set(Calendar.YEAR,mYear);
        mCalendar.set(Calendar.MONTH,mMonth - 1);
        mDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        generateData();
        postInvalidate();
    }

    /**
     * 设置描述集合
     * @param descTextMap
     */
    public void setDescTextMap(Map<String,String> descTextMap){
        mDescTextMap = descTextMap;
    }

    /**
     * 设置单选监听事件
     * @param listener {@see OnSingleSelectedListener}
     */
    public void setOnSingleSelectedListener(OnSingleSelectedListener listener){
        mOnSingleSelectedListener = listener;
    }

    /**
     * 设置多选监听事件
     * @param listener {@see OnMultiSelectedListener}
     */
    public void setOnMultiSelectedListener(OnMultiSelectedListener listener){
        mOnMultiSelectedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawHeaderSpace(canvas);
        drawHeaderYM(canvas);
        drawCell(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        mCellWidth = (mWidth - (ROW_CELL_COUNT + 1) * mCellHorizontalSpace) / ROW_CELL_COUNT;

        generateData();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float enY = event.getY();
                int distance = (int) Math.sqrt(Math.pow(mDownX - endX,2) + Math.pow(mDownY - enY,2));
                if (distance <= 15){
                    //点击事件
                    DayCell collisionDayCell = collisionDayCell(endX,enY);
                    if (collisionDayCell != null && collisionDayCell.day > 0){
                        if (mOnSingleSelectedListener != null){
                            mSelectedList.clear();
                            mOnSingleSelectedListener.onSingleSelected(collisionDayCell);
                            mSelectedList.add(collisionDayCell);
                        }else if (mOnMultiSelectedListener != null){
                            if (mSelectedList.contains(collisionDayCell)){
                                mSelectedList.remove(collisionDayCell);
                            }else {
                                mSelectedList.add(collisionDayCell);
                            }
                            mOnMultiSelectedListener.onMultiSelected(mSelectedList);
                        }
                        postInvalidate();
                        performClick();
                    }
                }
                break;
        }
        return true;
    }

    private DayCell collisionDayCell(float x,float y){
        for (DayCell dayCell : mDayCells){
            if (x >= dayCell.left && x <= dayCell.right && y >= dayCell.top && y <= dayCell.bottom){
                return dayCell;
            }
        }
        return null;
    }

    /**
     * 绘制顶部间隙
     * @param canvas
     */
    private void drawHeaderSpace(Canvas canvas) {
        mPaint.setColor(mHeaderSpaceBgColor);
        canvas.drawRect(0,0,mWidth,mHeaderSpaceHeight,mPaint);
    }

    private void drawHeaderYM(Canvas canvas) {
        mPaint.setColor(mHeaderYMFontColor);
        mPaint.setTextSize(mHeaderYMFontSize);

        String text = mYear + "年" + mMonth + "月";
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseLine = mHeaderSpaceHeight + (mHeaderYMHeight - (Math.abs(fontMetrics.bottom) - Math.abs(fontMetrics.top))) / 2;
        float x = (mWidth - mPaint.measureText(text)) / 2;
        canvas.drawText(text,x,baseLine,mPaint);

        mPaint.setColor(mGridLineColor);
        mPaint.setStrokeWidth(mGridLineWidth);

        int lineY = mHeaderSpaceHeight + mHeaderYMHeight;
        canvas.drawLine(0,lineY,mWidth,lineY,mPaint);
    }

    private void drawCell(Canvas canvas) {
        for (DayCell dayCell : mDayCells){
            if (dayCell.day <= 0){
                continue;
            }
            drawCellBg(dayCell,canvas);
            drawDayFont(dayCell,canvas);
            drawDescFont(dayCell,canvas);
        }
    }

    private void drawCellBg(DayCell dayCell, Canvas canvas) {
        if (!mSelectedList.contains(dayCell)) return;
        mPaint.setColor(mSelectedCellBgColor);
        int radius = getContext().getResources().getDimensionPixelSize(R.dimen.dp_5);
        RectF rect = new RectF(dayCell.left,dayCell.top,dayCell.right,dayCell.bottom);
        canvas.drawRoundRect(rect,radius,radius,mPaint);
    }

    private void drawDayFont(DayCell dayCell, Canvas canvas) {
        if (dayCell.day <= 0) return;

        String dayText = String.valueOf(dayCell.day);
        mPaint.setColor(dayCell.isHoliday ? mHolidayFontColor : mDayFontColor);
        if (mSelectedList.contains(dayCell)){
            mPaint.setColor(mSelectedDayFontColor);
        }
        mPaint.setTextSize(mDayFontSize);

        float x = dayCell.left + (mCellWidth - mPaint.measureText(dayText)) / 2;
        float baseLine = dayCell.top + mCellHeight * 0.4f;
        canvas.drawText(String.valueOf(dayCell.day),x,baseLine,mPaint);
    }

    private void drawDescFont(DayCell dayCell, Canvas canvas) {
        final String descText = dayCell.descText;
        if (TextUtils.isEmpty(descText)) return;

        mPaint.setColor(mSelectedList.contains(dayCell) ? mSelectedDescFontColor : mDescFontColor);
        mPaint.setTextSize(mDescFontSize);

        float x = dayCell.left  + (mCellWidth - mPaint.measureText(descText)) / 2;
        float baseLine = dayCell.top + mCellHeight * 0.8f;
        canvas.drawText(dayCell.descText,x,baseLine,mPaint);
    }

    private void init() {
        mCalendar = Calendar.getInstance();
        if (mYear == 0 || mMonth == 0){ //不设置年月,默认获取当前年月
            mYear = mCalendar.get(Calendar.YEAR);
            mMonth = mCalendar.get(Calendar.MONTH) + 1;
        }
        mDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void setAttrDefaultValue(Context context) {
        mHeaderSpaceHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_13);
        mHeaderSpaceBgColor = context.getResources().getColor(R.color.bg_ebebeb);

        mHeaderYMHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_45);
        mHeaderYMFontSize = context.getResources().getDimensionPixelSize(R.dimen.text_36);
        mHeaderYMFontColor = context.getResources().getColor(R.color.text_999);

        mGridLineWidth = 1;
        mGridLineColor = context.getResources().getColor(R.color.text_cccccc);
        mDayFontSize = context.getResources().getDimensionPixelSize(R.dimen.text_48);
        mDayFontColor = context.getResources().getColor(R.color.text_333);
        mHolidayFontColor = context.getResources().getColor(R.color.text_999);
        mDescFontSize = context.getResources().getDimensionPixelSize(R.dimen.text_30);
        mDescFontColor = context.getResources().getColor(R.color.text_39bbfb);

        mSelectedDayFontColor = Color.WHITE;
        mSelectedDescFontColor = Color.WHITE;
        mSelectedCellBgColor = context.getResources().getColor(R.color.text_39bbfb);

        mCellHorizontalSpace = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
        mCellVerticalSpace = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
    }

    private void setViewAttributes(Context context, @Nullable AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YJMonthView,defStyle,R.style.Widget_MonthView);

        mHeaderYMHeight = a.getDimensionPixelSize(R.styleable.YJMonthView_header_YM_height,context.getResources().getDimensionPixelSize(R.dimen.dp_45));
        mHeaderYMFontSize = a.getDimensionPixelSize(R.styleable.YJMonthView_header_YM_font_size,context.getResources().getDimensionPixelSize(R.dimen.text_36));
        mHeaderYMFontColor = a.getColor(R.styleable.YJMonthView_header_YM_font_color,context.getResources().getColor(R.color.text_999));

        mHeaderSpaceHeight = a.getDimensionPixelSize(R.styleable.YJMonthView_header_space_height,context.getResources().getDimensionPixelSize(R.dimen.dp_13));
        mHeaderSpaceBgColor = a.getColor(R.styleable.YJMonthView_header_space_bg_color,context.getResources().getColor(R.color.bg_ebebeb));

        mGridLineWidth = a.getDimensionPixelSize(R.styleable.YJMonthView_grid_line_width,1);
        mGridLineColor = a.getColor(R.styleable.YJMonthView_grid_line_color,context.getResources().getColor(R.color.text_cccccc));
        mDayFontSize = a.getDimensionPixelSize(R.styleable.YJMonthView_day_font_size,context.getResources().getDimensionPixelSize(R.dimen.text_48));
        mDayFontColor = a.getColor(R.styleable.YJMonthView_day_font_color, context.getResources().getColor(R.color.text_333));
        mSelectedDayFontColor = a.getColor(R.styleable.YJMonthView_selected_day_font_color, Color.WHITE);
        mHolidayFontColor = a.getColor(R.styleable.YJMonthView_holiday_font_color, context.getResources().getColor(R.color.text_999));
        mDescFontSize = a.getDimensionPixelSize(R.styleable.YJMonthView_desc_font_size,context.getResources().getDimensionPixelSize(R.dimen.text_30));
        mDescFontColor = a.getColor(R.styleable.YJMonthView_desc_font_color, context.getResources().getColor(R.color.text_39bbfb));
        mSelectedDescFontColor = a.getColor(R.styleable.YJMonthView_selected_desc_font_color, Color.WHITE);
        mSelectedCellBgColor = a.getColor(R.styleable.YJMonthView_selected_cell_bg_color,context.getResources().getColor(R.color.text_39bbfb));

        mCellHorizontalSpace = a.getDimensionPixelSize(R.styleable.YJMonthView_cell_horizontal_space,context.getResources().getDimensionPixelSize(R.dimen.dp_3));
        mCellVerticalSpace = a.getDimensionPixelSize(R.styleable.YJMonthView_cell_horizontal_space,context.getResources().getDimensionPixelSize(R.dimen.dp_3));

        a.recycle();
    }


    /**
     * 生成数据提供canvas绘制
     */
    private void generateData() {
        mDayCells.clear();
        mCalendar.set(Calendar.DAY_OF_MONTH,1);
        int skipCell = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (skipCell == 7){
            skipCell = 0;
        }

        int totalCell = mDay + skipCell;
        mCellColumn = totalCell / ROW_CELL_COUNT + (totalCell % ROW_CELL_COUNT == 0 ? 0 : 1);
        mCellHeight = (mHeight - mHeaderYMHeight - mHeaderSpaceHeight - mCellVerticalSpace * (mCellColumn + 1)) / mCellColumn;

        for (int i=skipCell; i<mDay + skipCell; i++){
            final int top =  mHeaderSpaceHeight + mHeaderYMHeight + mCellHeight * (i / ROW_CELL_COUNT) + mCellVerticalSpace * ((i / ROW_CELL_COUNT) + 1);
            final int left = (i % ROW_CELL_COUNT) * mCellWidth + mCellHorizontalSpace * ((i % ROW_CELL_COUNT) + 1);
            final int right = left + mCellWidth;
            final int bottom = top + mCellHeight;

            final int day = i - skipCell + 1;
            DayCell dayCell = new DayCell();
            dayCell.left = left;
            dayCell.top = top;
            dayCell.right = right;
            dayCell.bottom = bottom;
            dayCell.day = day;
            dayCell.descText = getDescText(day);
            dayCell.isHoliday = isHoliday(i);
            dayCell.year = mYear;
            dayCell.month = mMonth;
            mDayCells.add(dayCell);
        }
    }

    private boolean isHoliday(int cellPosition) {
        return (cellPosition+1) % ROW_CELL_COUNT == 1 || (cellPosition+1) % ROW_CELL_COUNT == 0;
    }

    private String getDescText(int day) {
        String key = mYear + "-" + mMonth + "-" + day;
        if (mDescTextMap != null){
            return mDescTextMap.get(key);
        }
        return null;
    }

    public final static class DayCell {
        private int left;
        private int right;
        private int top;
        private int bottom;
        private String descText;
        private boolean isHoliday;
        private int day;
        private int year;
        private int month;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DayCell dayCell = (DayCell) o;

            if (left != dayCell.left) return false;
            if (right != dayCell.right) return false;
            if (top != dayCell.top) return false;
            if (bottom != dayCell.bottom) return false;
            if (isHoliday != dayCell.isHoliday) return false;
            if (day != dayCell.day) return false;
            if (year != dayCell.year) return false;
            if (month != dayCell.month) return false;
            return descText != null ? descText.equals(dayCell.descText) : dayCell.descText == null;
        }

        @Override
        public int hashCode() {
            int result = left;
            result = 31 * result + right;
            result = 31 * result + top;
            result = 31 * result + bottom;
            result = 31 * result + (descText != null ? descText.hashCode() : 0);
            result = 31 * result + (isHoliday ? 1 : 0);
            result = 31 * result + day;
            result = 31 * result + year;
            result = 31 * result + month;
            return result;
        }

        @Override
        public String toString() {
            return "year=" + year + "month=" + month + "day=" + day;
        }

        public int getDay() {
            return day;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }
    }

    public interface OnSingleSelectedListener{
        void onSingleSelected(DayCell dayCell);
    }

    public interface OnMultiSelectedListener {
        void onMultiSelected(List<DayCell> dayCells);
    }
}
