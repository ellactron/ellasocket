package rici.roplug.open.fragment;

import java.util.ArrayList;

import rici.roplug.open.R;
import rici.roplug.open.activity.DeviceLinkActivity;
import rici.roplug.open.activity.SocketMainActivity;
import rici.roplug.open.adapter.SocketElectricPagerAdapter;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.bll.bean.SocketInfo;
import rici.roplug.open.view.ElectricityAnalyseAloneView;
import rici.roplug.open.view.HistoryElectricityDialogView;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.SocketElectricView;
import rici.roplug.open.view.WaitProgressView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class SocketElectricityFragment extends Fragment {
	private int auto_update_time = 30 * 1000;
	private ArrayList<SocketElectricView> socketElectricViews = null;
	private ViewPager socketElectricViewPager = null;
	private SocketElectricPagerAdapter socketElectricPagerAdapter = null;
	private SocketInfo socketInfo = null;;
	private SocketElectricView socketElectricView = null;
	private ImageButton socketPowerButton = null;

	private Handler autoUpdateHandler = new Handler();
	private Runnable autoUpdateRunnable = new Runnable() {
		public void run() {
			updateSocketElectricity();
			autoUpdateHandler.postDelayed(this, auto_update_time);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_socket_electricity_layout,
				container, false);
		socketElectricViewPager = (ViewPager) v
				.findViewById(R.id.vp_socket_electric);
		socketElectricViews = new ArrayList<SocketElectricView>();
		initSocketElectricity();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(SocketBLL.getInstance().isSocketSort()){
			if(socketElectricViews !=null){
				socketElectricViews.clear();
			}
			else if(socketElectricViews ==null){
				socketElectricViews = new ArrayList<SocketElectricView>();
			}
			initSocketElectricity();
			SocketBLL.getInstance().setSocketSort(false);
		}
		else{
		changeItem();			
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopAutoGetSocketElectricity();
	}

	@Override 
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
    		changeItem();
        } else {
    		stopAutoGetSocketElectricity();
        }
    }	
	
	public void changeItem() {		
		if (SocketBLL.getInstance().getCurrentSocketIndex() < SocketBLL
				.getInstance().getAllSocketInfos().size()) {
			try {
				socketInfo = SocketBLL.getInstance().getCurrentSocket();
				if (socketInfo != null && socketElectricViews!=null) {
					for (int item = 0; item < socketElectricViews.size(); item++) {
						if (socketElectricViews.get(item).getSocketInfo()
								.isEqualObj(socketInfo)) {
							socketElectricView = socketElectricViews.get(item);
							socketElectricViewPager.setCurrentItem(item);
							break;
						}
					}
					updateSocketElectricity();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void initSocketElectricity() {
		if (SocketBLL.getInstance().getAllSocketInfos() != null) {
			socketElectricViews.clear();
			int i = 0;
			for (SocketInfo socketInfo : SocketBLL.getInstance()
					.getAllSocketInfos()) {
				if (socketInfo.getDeviceInfo() != null
						&& socketInfo.getDeviceInfo().isDeviceOnline()) {
					SocketElectricView se = new SocketElectricView(
							getActivity().getBaseContext(), null, socketInfo,
							i, itemsOnClick);
					socketElectricViews.add(se);
				}
				i++;
			}

		socketElectricPagerAdapter = new SocketElectricPagerAdapter(
				socketElectricViews);

		socketElectricViewPager.setAdapter(socketElectricPagerAdapter);
		socketElectricViewPager.setOffscreenPageLimit(socketElectricViews
				.size());
		socketElectricViewPager
				.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageScrollStateChanged(int arg0) {
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageSelected(int arg0) {
						socketElectricView = socketElectricViews.get(arg0);
						socketInfo = SocketBLL.getInstance()
								.getAllSocketInfos()
								.get(socketElectricView.getSocketIndex());
						SocketBLL.getInstance().setCurrentSocketIndex(
								socketElectricView.getSocketIndex());

						updateSocketElectricity();
					}
				});
		changeItem();

		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_socket_electricity_title:
				SocketBLL.getInstance().setMainActivity(getActivity());
				Intent deviceLink = new Intent(getActivity(),
						DeviceLinkActivity.class);
				startActivity(deviceLink);
				break;
			}
		}
	};

	private void updateSocketElectricity() {
		stopAutoGetSocketElectricity();
		if (socketInfo != null) {
			SocketBLL.getInstance().getSocketElectricity(socketInfo);
		}
		startAutoGetSocketElectricity();
	}

	private void startAutoGetSocketElectricity() {
		autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
		autoUpdateHandler.postDelayed(autoUpdateRunnable, auto_update_time);
	}

	private void stopAutoGetSocketElectricity() {
		autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
	}

	public void refreshSocket() {
		if (socketElectricView != null) {
			socketElectricView.updateSocketElectricity();
		}
	}

	public void socketElectricityButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_socket_power:
			if (socketInfo != null) {
				try {
					SocketMainActivity socketMainActivity = (SocketMainActivity)getActivity();
					socketMainActivity.showProgressDialog(1000,
							WaitProgressView.WAIT_TIME);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				SocketBLL.getInstance().setSwitchState(socketInfo);
				/*socketInfo.setPowerOn(!socketInfo.getPowerOn());
				refreshSocket();*/
			}
			break;
		case R.id.img_air_quality_refresh:
		case R.id.ll_socket_electric_number:
			SocketBLL.getInstance().getSocketElectricity(socketInfo);
			break;
		case R.id.btn_history_electricity:
			HistoryElectricityDialogView historyElectricityDialogView = new HistoryElectricityDialogView(
					getActivity(), getActivity());

			Window window = historyElectricityDialogView.getWindow();
			window.setWindowAnimations(R.style.dialog_show_animation); // 设置窗口弹出动画
			//historyElectricityDialogView.setCancelable(false);
			historyElectricityDialogView.show();
			break;
		case R.id.btn_electricity_analyse:
			ElectricityAnalyseAloneView electricityAnalyseView = new ElectricityAnalyseAloneView(getActivity());

			Window electricityWindow = electricityAnalyseView.getWindow();
			electricityWindow.setWindowAnimations(R.style.dialog_show_animation); // 设置窗口弹出动画
			//electricityAnalyseView.setCancelable(false);
			electricityAnalyseView.show();
			/*Intent analyseIntent = new Intent(getActivity(),
					SocketListenerActivity.class);
			startActivity(analyseIntent);*/
			break;
		case R.id.btn_auto_billing:
			PopupDialogView autoBillingPopupDialogView = new PopupDialogView(
					getActivity());
			autoBillingPopupDialogView
					.setDialogType(PopupDialogView.POPUPWINDOW_AUTO_BILLING);
			autoBillingPopupDialogView.setArg1(socketInfo.getDeviceInfo()
					.getDeviceName());
			autoBillingPopupDialogView.setArg2(socketInfo.getUsedElectric());
			autoBillingPopupDialogView
					.setButtonCount(PopupDialogView.POPUPWINDOW_BUTTON_ONE);

			autoBillingPopupDialogView.show();
			autoBillingPopupDialogView.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

			break;
		}
	}
}
