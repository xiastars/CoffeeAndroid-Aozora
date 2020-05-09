package com.balanx.nfhelper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.balanx.nfhelper.R;

import java.util.Calendar;

public class SDatePicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mDayPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mYearPicker;
	private Calendar mCalendar;
	
	private OnDateChangedListener listener;

	private String[] mMonthDisplay;

	public SDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mCalendar = Calendar.getInstance();
		initMonthDisplay();
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.view_date_picker, this, true);
		mDayPicker = (NumberPicker) findViewById(R.id.np_date_day);
		mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
		mYearPicker = (NumberPicker) findViewById(R.id.date_year);

		mDayPicker.setMinValue(1);
		mDayPicker.setMaxValue(31);
		mDayPicker.setValue(20);
		mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

		mMonthPicker.setMinValue(0);
		mMonthPicker.setMaxValue(11);
		mMonthPicker.setDisplayedValues(mMonthDisplay);
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

		mYearPicker.setMinValue(1950);
		mYearPicker.setMaxValue(2100);
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

		mMonthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.MONTH, newVal);
				updateDate();
				
			}
		});
		mDayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {

				mCalendar.set(Calendar.DATE, newVal);
				updateDate();
			}
		});
		mYearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.YEAR, newVal);
				updateDate();

			}
		});

		updateDate();

	}
	
	public void receiveListener(OnDateChangedListener listener){
		this.listener = listener;
	}

	private void initMonthDisplay() {
		mMonthDisplay = new String[12];
		mMonthDisplay[0] = "01";
		mMonthDisplay[1] = "02";
		mMonthDisplay[2] = "03";
		mMonthDisplay[3] = "04";
		mMonthDisplay[4] = "05";
		mMonthDisplay[5] = "06";
		mMonthDisplay[6] = "07";
		mMonthDisplay[7] = "08";
		mMonthDisplay[8] = "09";
		mMonthDisplay[9] = "10";
		mMonthDisplay[10] = "11";
		mMonthDisplay[11] = "12";
	}

	private void updateDate() {
		if(listener!=null){
			listener.getTime();
		}


		mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
		mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		mDayPicker.setValue(mCalendar.get(Calendar.DATE));
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));
	}

	public SDatePicker(Context context) {
		this(context, null);
	}

	public String getDate() {
		String date = mYearPicker.getValue() + "-"
				+ (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();
		return date;

	}

	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonth() {
		return mCalendar.get(Calendar.MONTH);
	}

	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}

	public void setCalendar(Calendar calendar) {
		mCalendar = calendar;
		updateDate();
	}
	
	public interface OnDateChangedListener{
		void getTime();
	}

}
