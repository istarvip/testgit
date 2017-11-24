package com.walnutin.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.walnutin.Jinterface.OnWheelChangedListener;
import com.walnutin.activity.PersonalInfoActivity;
import com.walnutin.hard.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by assa on 2016/5/19.
 */
public class BirthDateDialog extends PopupWindow implements View.OnClickListener {

    public static final int FROM_YEAR = 1930;
    public static final int DEFAULT_YEAR = 1990;
    private CallBack back;
    private PriorityListener lis;
    private Context context = null;
    private static int theme = R.style.AnimBottom; // 主题

    private int curYear = 0;
    private int curMonth = 0;
    private int curDay = 0;
    private boolean scrolling = false;

    private String title;
    private int flag = 0;
    private int width; // 对话框宽度
    private int height; // 对话框高度
    private LinearLayout dateLayout;
    private WheelView monthview = null;
    private WheelView yearview = null;
    private WheelView dayview = null;
    private View mMenuView;
    private boolean isSure;

    private NumericWheelAdapter yearAdapter = null;
    private NumericWheelAdapter monthAdapter = null;
    private NumericWheelAdapter dayAdapter = null;

    private Button btnSure = null;
    private Button btnCancel = null;


    public interface PriorityListener {
        public void refreshPriorityUI(String year, String month, String day,
                                      CallBack back);
    }

    public interface CallBack {
        public void execute();
    }


    public BirthDateDialog(Context context, String birthDate) {
        super(context);
        this.context = context;
    }

    public BirthDateDialog(Context context, PriorityListener listener, CallBack callBack) {
        super(context);
        this.context = context;
    }


    public BirthDateDialog(final Context context, final PriorityListener listener,
                           CallBack callback, int currentyear, int currentmonth,
                           int currentday, int width, int height, String title, int flag) {
        super(context);
        this.context = context;
        lis = listener;
        back = callback;
        this.curYear = currentyear;
        this.curMonth = currentmonth;
        this.curDay = currentday;
        this.width = width;
        this.title = title;
        this.height = height;
        this.flag = 0;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.birthdate_pop, null);

        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        materialActivity = (PersonalInfoActivity) context;
        init();
    }

    private void init() {
        btnSure = (Button) mMenuView.findViewById(R.id.date_btnsure);
        btnSure.setOnClickListener(this);
        btnCancel = (Button) mMenuView.findViewById(R.id.date_btncancel);
        btnCancel.setOnClickListener(this);

        yearview = (WheelView) mMenuView.findViewById(R.id.wheelView_year);
        monthview = (WheelView) mMenuView.findViewById(R.id.wheelView_month);
        dayview = (WheelView)mMenuView.findViewById(R.id.wheelView_day);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateDays(yearview, monthview, dayview);
                }
            }
        };

        monthview.addChangingListener(listener);
        yearview.addChangingListener(listener);


        Calendar calendar = Calendar.getInstance();
        if (this.curYear == 0 || this.curMonth == 0) {
            curYear = calendar.get(Calendar.YEAR);
            curMonth = calendar.get(Calendar.MONTH) + 1;
            curDay = calendar.get(Calendar.DAY_OF_MONTH);
        }
//        if(curYear >= 2016){
//            curYear -= 20;
//        }
        yearAdapter = new NumericWheelAdapter(FROM_YEAR, calendar.get(Calendar.YEAR));
        yearview.setAdapter(yearAdapter);
        int cc = curYear - FROM_YEAR;
        yearview.setCurrentItem(cc);
        yearview.setVisibleItems(5);
        monthAdapter = new NumericWheelAdapter(1, 12, "%02d");
        monthview.setAdapter(monthAdapter);
        monthview.setCurrentItem(curMonth - 1);
        monthview.setCyclic(false);
        monthview.setVisibleItems(5);
        updateDays(yearview, monthview, dayview);
        dayview.setCyclic(false);
        dayview.setVisibleItems(5);


    }




    /**
     * 根据年份和月份来更新日期
     */
    private void updateDays(WheelView year, WheelView month, WheelView day) {
        String[] monthsBig = {"1", "3", "5", "7", "8", "10", "12"};
        String[] monthsLittle = {"4", "6", "9", "11"};

        final List<String> listBig = Arrays.asList(monthsBig);
        final List<String> listLittle = Arrays.asList(monthsLittle);
        int yearNum = year.getCurrentItem() + FROM_YEAR;
        if (listBig.contains(String.valueOf(month.getCurrentItem() + 1))) {
            dayAdapter = new NumericWheelAdapter(1, 31, "%02d");
        } else if (listLittle
                .contains(String.valueOf(month.getCurrentItem() + 1))) {
            dayAdapter = new NumericWheelAdapter(1, 30, "%02d");
        } else {
            if ((yearNum % 4 == 0 && yearNum % 100 != 0)
                    || yearNum % 400 == 0) {
                dayAdapter = new NumericWheelAdapter(1, 29, "%02d");
            } else {
                dayAdapter = new NumericWheelAdapter(1, 28, "%02d");
            }
        }
        dayview.setAdapter(dayAdapter);
        dayview.setCurrentItem(curDay - 1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_btnsure:
                lis.refreshPriorityUI(yearAdapter.getValues(),
                        monthAdapter.getValues(), dayAdapter.getValues(), back);
                this.dismiss();
                changeButton();
                break;

            case R.id.date_btncancel:
                this.dismiss();
                break;

            default:
                break;
        }
    }

    PersonalInfoActivity materialActivity = null;
    private void changeButton() {
        materialActivity.changeBirth();
    }
}
