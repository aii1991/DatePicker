package com.boildcoffee.datepicker.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boildcoffee.datepicker.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zjh
 *         2018/12/3
 */

public class DatePicker extends LinearLayout {
    private static final String[] HEADER_WEEK_TEXT = new String[]{"日","一","二","三","四","五","六"};
    private static final int RANGE = 30; //前后30年

    private boolean showPastDate = true; //是否显示以前的日期
    private int mCurrentYear;
    private int mCurrentMonth;
    private List<String> mYM = new ArrayList<>();
    private MonthView.OnSingleSelectedListener mOnSingleSelectedListener;
    private MonthView.OnMultiSelectedListener mOnMultiSelectedListener;
    private Map<String,String> mDescTextMap = new HashMap<>();

    public DatePicker(Context context) {
        super(context);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 添加对应日期的描述文字
     * @param date 日期格式 YY-MM-DD
     * @param descText 描述文字
     */
    public void putDescText(String date,String descText){
        mDescTextMap.put(date,descText);
    }

    private void init(Context context) {
        initData();
        initView(context);
    }

    private void initView(Context context) {
        this.setOrientation(VERTICAL);

        LinearLayout llHeader = new LinearLayout(context);
        llHeader.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,context.getResources().getDimensionPixelSize(R.dimen.dp_25));
        llHeader.setLayoutParams(layoutParams);
        for (String text : HEADER_WEEK_TEXT){
            TextView tv = new TextView(context);
            tv.setTextSize(15);
            tv.setTextColor(context.getResources().getColor(R.color.text_999));
            tv.setText(text);
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams tvLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            tvLayoutParams.weight = 1;
            tv.setLayoutParams(tvLayoutParams);
            llHeader.addView(tv);
        }
        addView(llHeader);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter<String,BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_date_pick,mYM) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.addOnClickListener(R.id.month_view);
                MonthView monthView = helper.getView(R.id.month_view);
                String[] ym = item.split("-");
                monthView.setYearAndMonth(Integer.valueOf(ym[0]),Integer.valueOf(ym[1]));
                monthView.setOnSingleSelectedListener(mOnSingleSelectedListener);
                monthView.setOnMultiSelectedListener(mOnMultiSelectedListener);
                monthView.setDescTextMap(mDescTextMap);
            }
        };
        recyclerView.setAdapter(adapter);
        int toPosition = mYM.indexOf(mCurrentYear + "-" + mCurrentMonth);
        recyclerView.scrollToPosition(toPosition);
        addView(recyclerView);
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1; //月是从0开始需加1

        addYM(calendar, mCurrentYear,true);
        addYM(calendar, mCurrentYear,false);
    }

    private void addYM(Calendar calendar, int currentYear,boolean isBefore) {
        if (isBefore && showPastDate){
            for (int y=currentYear - RANGE; y<currentYear; y++){
                calendar.set(Calendar.YEAR,y);
                int month = calendar.get(Calendar.MONTH) + 1;
                for (int m=1; m<=month; m++){
                    String yearMonth = y + "-" + m;
                    mYM.add(yearMonth);
                }
            }
        }else {
            for (int y=currentYear; y<currentYear + RANGE; y++){
                calendar.set(Calendar.YEAR,y);
                int month = calendar.get(Calendar.MONTH) + 1;
                for (int m=1; m<=month; m++){
                    String yearMonth = y + "-" + m;
                    mYM.add(yearMonth);
                }
            }
        }
    }

    public void setOnSingleSelectedListener(MonthView.OnSingleSelectedListener onSingleSelectedListener) {
        this.mOnSingleSelectedListener = onSingleSelectedListener;
    }

    public void setOnMultiSelectedListener(MonthView.OnMultiSelectedListener onMultiSelectedListener){
        this.mOnMultiSelectedListener = onMultiSelectedListener;
    }
}
