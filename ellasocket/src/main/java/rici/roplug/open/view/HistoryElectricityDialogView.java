package rici.roplug.open.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.bll.bean.SocketElectricityData;
import rici.roplug.open.bll.bean.SocketInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class HistoryElectricityDialogView extends AlertDialog {
	private final static int GET_HISTORY_DATA_DAY = 0;
	private final static int GET_HISTORY_DATA_MONTH = 1;
	private final static int GET_HISTORY_DATA_YEAR = 2;
	private Context mContext = null;
	private Activity mActivity = null;
	private LinearLayout tabWidget = null;
	private LinearLayout waitLayout = null;
	private ProgressBar progresBar = null;
	private Button waitButton = null;
	private CustomerViewPager mViewPager = null;
	private PagerAdapter mPagerAdapter = null;
	public ArrayList<View> tabFragments = new ArrayList<View>();
	private Button historyElectricityDay = null;
	private Button historyElectricityMonth = null;
	private Button historyElectricityYear = null;	
	private SocketHistoryElectricityHandler socketHistoryElectricityHandler = null;
	public HistoryElectricityDialogView(Context context, Activity activity) {
		super(context);
		this.mContext = context;
		this.mActivity = activity;
	}

	private Handler waitProgressHandler = new Handler();
	
	private Runnable dismissRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(dismissRunnable);
			if(waitLayout!=null){
				waitLayout.setVisibility(View.GONE);
			}
		}
	};
	
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			if(progresBar!=null){
				progresBar.setVisibility(View.GONE);
			}
			if(waitButton!=null){
				waitButton.setText(R.string.get_data_failure);
				waitButton.setClickable(true);
			}
			waitProgressHandler.postDelayed(dismissRunnable, BLLUtil.WIFI_CONNECTED_TIMEOUT/2);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_electricity_layout);
		waitLayout = (LinearLayout)findViewById(R.id.ll_history_electricity_wait);
		progresBar = (ProgressBar)findViewById(R.id.pb_history_electricity);
		waitButton = (Button)findViewById(R.id.tv_history_electricity_wait);
		waitButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mViewPager!=null){
					getHistoryData(mViewPager.getCurrentItem());		
				}
			}
		});
		socketHistoryElectricityHandler = new SocketHistoryElectricityHandler();
		//setCanceledOnTouchOutside(false);
		SocketBLL.getInstance().restoreActivityHandler();
		SocketBLL.getInstance().initDanausBLL(mContext,
				socketHistoryElectricityHandler);
		tabWidget = (LinearLayout) findViewById(R.id.tw_history_electricity);
		historyElectricityDay = (Button) findViewById(R.id.ib_history_electricity_day);
		historyElectricityMonth = (Button) findViewById(R.id.ib_history_electricity_month);
		historyElectricityYear = (Button) findViewById(R.id.ib_history_electricity_year);

		historyElectricityDay.setOnClickListener(onClickListener);
		historyElectricityMonth.setOnClickListener(onClickListener);
		historyElectricityYear.setOnClickListener(onClickListener);

		ImageButton closeButton = (ImageButton) findViewById(R.id.ib_popupwindow_close);
		closeButton.setOnClickListener(onClickListener);
		Calendar calendar = Calendar.getInstance();

		// 日耗电量
		int days = calendar.getActualMaximum(Calendar.DATE);
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Random random = new Random();
		ArrayList<String> dayXArrayList = new ArrayList<String>();
		for (int i = 0; i < days; i++) {
			dayXArrayList.add(String.valueOf(i + 1));
		}
		HistoryElectricityView daySocketElectricView = new HistoryElectricityView(
				mContext);
		daySocketElectricView.setxArrayList(dayXArrayList);
		daySocketElectricView.setxCount(12);
		daySocketElectricView.setDrawLineSize(mContext.getResources().getDimensionPixelSize(R.dimen.history_electricity_draw_line_size));
		daySocketElectricView.setLabelStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_label_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		daySocketElectricView.setShowStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_show_text_size),
				mContext.getResources().getColor(
						R.color.history_electric_draw_color));
		daySocketElectricView.setPopStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_pop_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		daySocketElectricView.setUnitString(mContext.getResources().getString(
				R.string.history_electricity_unit_kw));
		daySocketElectricView.setShowItemValue(String.valueOf(Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH) - 1));

		daySocketElectricView.initDrawLocation(
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_top),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_bottom));
		tabFragments.add(daySocketElectricView);

		// 月耗电量
		HistoryElectricityView monthSocketElectricView = new HistoryElectricityView(
				mContext);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		ArrayList<String> monthXArrayList = new ArrayList<String>();
		for (int i = 0; i <= 12; i++) {
			monthXArrayList.add(String.valueOf(i + 1));
		}
		monthSocketElectricView.setxArrayList(monthXArrayList);
		monthSocketElectricView.setxCount(12);
		monthSocketElectricView.setDrawLineSize(mContext.getResources().getDimensionPixelSize(R.dimen.history_electricity_draw_line_size));
		monthSocketElectricView.setLabelStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_label_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		monthSocketElectricView.setShowStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_show_text_size),
				mContext.getResources().getColor(
						R.color.history_electric_draw_color));
		monthSocketElectricView.setPopStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_pop_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		monthSocketElectricView.setUnitString(mContext.getResources()
				.getString(R.string.history_electricity_unit_kw));
		monthSocketElectricView.setShowItemValue(String.valueOf(Calendar
				.getInstance().get(Calendar.MONTH) + 1));
		monthSocketElectricView.initDrawLocation(
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_top),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_bottom));

		tabFragments.add(monthSocketElectricView);
		// 年耗电量
		HistoryElectricityView yearSocketElectricView = new HistoryElectricityView(
				mContext);
		yearSocketElectricView.setShowItemValue(String.valueOf(Calendar
				.getInstance().get(Calendar.YEAR)));

		yearSocketElectricView.setShowY(false);
		yearSocketElectricView
				.setViewStyle(HistoryElectricityView.VIEW_STYLE_BAR);
		yearSocketElectricView.setLabelStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_label_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		yearSocketElectricView.setShowStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_show_text_size),
				mContext.getResources().getColor(
						R.color.history_electric_draw_color));
		yearSocketElectricView.setPopStyle(
				mContext.getResources().getDimensionPixelSize(
						R.dimen.history_electricity_pop_text_size), mContext
						.getResources().getColor(R.color.system_color_white));
		yearSocketElectricView.setUnitString(mContext.getResources().getString(
				R.string.history_electricity_unit_kw));
		yearSocketElectricView.initDrawLocation(
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_padding_h),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_top),
				mContext.getResources().getDimensionPixelOffset(
						R.dimen.history_electricity_point_bottom));
		tabFragments.add(yearSocketElectricView);

		mViewPager = (CustomerViewPager) findViewById(R.id.vp_history_electricity);
		mPagerAdapter = new MyPagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);

		mViewPager.setCurrentItem(0);
		/*
		 * ArrayList<SocketElectricityData> dayElectricityList = new
		 * ArrayList<SocketElectricityData>(); dayElectricityList.add(new
		 * SocketElectricityData("01", 50f)); dayElectricityList.add(new
		 * SocketElectricityData("02", 60f)); dayElectricityList.add(new
		 * SocketElectricityData("03", 50f)); dayElectricityList.add(new
		 * SocketElectricityData("04", 60f)); dayElectricityList.add(new
		 * SocketElectricityData("05", 70f)); dayElectricityList.add(new
		 * SocketElectricityData("06", 80f)); dayElectricityList.add(new
		 * SocketElectricityData("07", 100f)); dayElectricityList.add(new
		 * SocketElectricityData("08", 0.04f));
		 * handleSocketYearElectricity(0,dayElectricityList);
		 */
		showProgressDialog();
		SocketBLL
				.getInstance()
				.getSocketDayElectricity(
						((HistoryElectricityView) tabFragments.get(2))
								.getShowItemValue(),
						((HistoryElectricityView) tabFragments.get(1))
								.getShowItemValue());
	}

	public void showProgressDialog() {
		if (waitLayout != null) {
			waitLayout.setVisibility(View.VISIBLE);

			if(progresBar!=null){
				progresBar.setVisibility(View.VISIBLE);
			}
			if(waitButton!=null){
				waitButton.setText(R.string.get_data_from_server);
				waitButton.setClickable(false);
			}
		}
		waitProgressHandler.postDelayed(waitProgressRunnable, BLLUtil.WIFI_CONNECTED_TIMEOUT);
	}

	public void dissmissProgressDialog() {
		if (waitLayout != null) {
			waitLayout.setVisibility(View.GONE);
		}
		waitProgressHandler.removeCallbacks(waitProgressRunnable);
	}
	
	private void getHistoryData(int getType){
		showProgressDialog();
		switch(getType){
		case GET_HISTORY_DATA_DAY:
			int year = Integer
					.valueOf(((HistoryElectricityView) tabFragments.get(2))
							.getShowItemValue());
			int month = Integer
					.valueOf(((HistoryElectricityView) tabFragments.get(1))
							.getShowItemValue());
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			day = BLLUtil.getDaysByYearMonth(year, month);
			ArrayList<String> dayXArrayList = new ArrayList<String>();
			for (int i = 0; i < day; i++) {
				dayXArrayList.add(String.valueOf(i + 1));
			}
			((HistoryElectricityView)tabFragments.get(0)).setxArrayList(dayXArrayList);

			SocketBLL
					.getInstance()
					.getSocketDayElectricity(String.valueOf(year),
							String.valueOf(month));
			break;
		case GET_HISTORY_DATA_MONTH:
			SocketBLL.getInstance().getSocketMonthElectricity(
					((HistoryElectricityView) tabFragments.get(2))
							.getShowItemValue());
			break;
		case GET_HISTORY_DATA_YEAR:
			SocketBLL.getInstance().getSocketYearElectricity();
			break;
		}
	}
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			SocketBLL.getInstance().initDanausBLL(mContext,
					socketHistoryElectricityHandler);
			switch (v.getId()) {
			case R.id.ib_popupwindow_close:
				HistoryElectricityDialogView.this.dismiss();
				SocketBLL.getInstance().recoveActivityHandler();
				break;
			case R.id.ib_history_electricity_day:
				tabWidget
						.setBackgroundResource(R.drawable.y335_history_electricity_day);
				historyElectricityDay
						.setTextColor(mContext.getResources().getColor(
								R.color.history_electricity_item_select_color));
				historyElectricityMonth.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				historyElectricityYear.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				mViewPager.setCurrentItem(0);

				/*
				 * ArrayList<SocketElectricityData> dayElectricityList = new
				 * ArrayList<SocketElectricityData>();
				 * dayElectricityList.add(new SocketElectricityData("01", 50f));
				 * dayElectricityList.add(new SocketElectricityData("02", 60f));
				 * dayElectricityList.add(new SocketElectricityData("03", 50f));
				 * dayElectricityList.add(new SocketElectricityData("04", 60f));
				 * dayElectricityList.add(new SocketElectricityData("05", 70f));
				 * dayElectricityList.add(new SocketElectricityData("06", 80f));
				 * dayElectricityList.add(new SocketElectricityData("07",
				 * 100f)); dayElectricityList.add(new
				 * SocketElectricityData("08", 0.04f));
				 * handleSocketYearElectricity(0,dayElectricityList);
				 */
				getHistoryData(GET_HISTORY_DATA_DAY);
				break;
			case R.id.ib_history_electricity_month:
				tabWidget
						.setBackgroundResource(R.drawable.y335_history_electricity_month);
				historyElectricityDay.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				historyElectricityMonth
						.setTextColor(mContext.getResources().getColor(
								R.color.history_electricity_item_select_color));
				historyElectricityYear.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				mViewPager.setCurrentItem(1);
				getHistoryData(GET_HISTORY_DATA_MONTH);
				/*
				 * ArrayList<SocketElectricityData> monthElectricityList = new
				 * ArrayList<SocketElectricityData>();
				 * monthElectricityList.add(new SocketElectricityData("01",
				 * 50f)); monthElectricityList.add(new
				 * SocketElectricityData("02", 60f));
				 * monthElectricityList.add(new SocketElectricityData("03",
				 * 50f)); monthElectricityList.add(new
				 * SocketElectricityData("04", 60f));
				 * monthElectricityList.add(new SocketElectricityData("05",
				 * 70f)); monthElectricityList.add(new
				 * SocketElectricityData("06", 80f));
				 * monthElectricityList.add(new SocketElectricityData("07",
				 * 100f)); monthElectricityList.add(new
				 * SocketElectricityData("08", 0.04f));
				 * handleSocketYearElectricity(1,monthElectricityList);
				 */
				break;
			case R.id.ib_history_electricity_year:
				tabWidget
						.setBackgroundResource(R.drawable.y335_history_electricity_year);
				historyElectricityDay.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				historyElectricityMonth.setTextColor(mContext.getResources()
						.getColor(R.color.system_color_white));
				historyElectricityYear
						.setTextColor(mContext.getResources().getColor(
								R.color.history_electricity_item_select_color));
				mViewPager.setCurrentItem(2);

				getHistoryData(GET_HISTORY_DATA_YEAR);
				/*ArrayList<SocketElectricityData> yearElectricityList = new
				  ArrayList<SocketElectricityData>();
				  for(int i=0;i<10;i++){
				  yearElectricityList.add(new SocketElectricityData(String.valueOf(2009+i),
				  888.8f));
				  }
				  handleSocketHistoryElectricity(2,yearElectricityList);*/
				 
				break;
			}
		}
	};

	private class MyPagerAdapter extends PagerAdapter {
		public MyPagerAdapter() {
		}

		@Override
		public int getCount() {
			return tabFragments.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
			view.removeView(tabFragments.get(position));
		}

		// 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			view.addView(tabFragments.get(position));
			return tabFragments.get(position);
		}

	}

	private void handleSocketHistoryElectricity(int viewIndex,
			ArrayList<SocketElectricityData> historyElectricityList) {
		HistoryElectricityView historyElectricView = (HistoryElectricityView) tabFragments
				.get(viewIndex);
		historyElectricView.setFirstDraw(true);
		historyElectricView.clearViewShowData();
		if (historyElectricityList != null) {
			ArrayList<String> yearXArrayList = new ArrayList<String>();
			for (SocketElectricityData socketElectricityData : historyElectricityList) {
				historyElectricView.addViewShowData(
						socketElectricityData.getDate(),
						socketElectricityData.getElectricity());
				if (viewIndex == 2) {
					yearXArrayList.add(socketElectricityData.getDate());
				}
			}

			if (viewIndex == 2) {
				historyElectricView.setxArrayList(yearXArrayList);
				/*historyElectricView
						.setShowItem(historyElectricityList.size() - 1);*/
			}
			if(historyElectricView.getShowItem()==-1){
				historyElectricView
						.setShowItem(historyElectricityList.size() - 1);
			}
			historyElectricView.invalidate();
		}
	}

	class SocketHistoryElectricityHandler extends Handler {
		public SocketHistoryElectricityHandler() {

		}

		public SocketHistoryElectricityHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			dissmissProgressDialog();
			SocketInfo socketInfo = SocketBLL.getInstance().getCurrentSocket();
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_DAY_ELECTRICITY:
				if (socketInfo != null
						&& socketInfo.getDayElectricityListHashMap() != null) {
					handleSocketHistoryElectricity(
							0,
							socketInfo
									.getDayElectricityListHashMap()
									.get(((HistoryElectricityView) tabFragments
											.get(2)).getShowItemValue()
											+ "-"
											+ ((HistoryElectricityView) tabFragments
													.get(1)).getShowItemValue()));
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_MONTH_ELECTRICITY:
				if (socketInfo != null
						&& socketInfo.getMonthElectricityHashMap() != null) {
					handleSocketHistoryElectricity(
							1,
							socketInfo.getMonthElectricityHashMap().get(
									((HistoryElectricityView) tabFragments
											.get(2)).getShowItemValue()));
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_YEAR_ELECTRICITY:
				if (socketInfo != null
						&& socketInfo.getYearElectricityList() != null) {
					handleSocketHistoryElectricity(2,
							socketInfo.getYearElectricityList());
				}
				break;
			}
		}
	}
}
