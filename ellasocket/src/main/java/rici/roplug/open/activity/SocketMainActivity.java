package rici.roplug.open.activity;

import java.util.ArrayList;

import com.rici.wifi.RiCiWifiServer;
import com.rici.wifi.util.L;

import rici.roplug.open.R;
import rici.roplug.open.application.MainApplication;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.bean.SocketInfo;
import rici.roplug.open.fragment.SmartSavingFragment;
import rici.roplug.open.fragment.SocketElectricityFragment;
import rici.roplug.open.fragment.SystemSettingFragment;
import rici.roplug.open.view.CustomerViewPager;
import rici.roplug.open.view.ShowReaultToast;
import rici.roplug.open.view.WaitProgressView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabWidget;
import android.widget.Toast;

public class SocketMainActivity extends BaseActivity {
	private SocketElectricityHandler socketHandler = null;
	private long exitTime = 0;
	private TabWidget tabWidget = null;
	private CustomerViewPager mViewPager = null;
	private PagerAdapter mPagerAdapter = null;
	public ArrayList<Fragment> tabFragments = new ArrayList<Fragment>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		socketHandler = new SocketElectricityHandler();
		setContentView(R.layout.socket_main_layout);

		tabWidget = (TabWidget) findViewById(R.id.tw_switch_main);
		tabWidget.setDividerDrawable(null);
		tabWidget.setCurrentTab(0);
		tabFragments.add(new SocketElectricityFragment());
		tabFragments.add(new SmartSavingFragment());
		tabFragments.add(new SystemSettingFragment());

		mViewPager = (CustomerViewPager) findViewById(R.id.viewPager);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tabWidget.setCurrentTab(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		updateTabButton((ImageButton) findViewById(R.id.ib_socket));
	}

	@Override
	public void onResume() {
		super.onResume();
		if(RiCiWifiServer.getInstance().getCurrentUserId()<=0){
			MainApplication.getInstance().broadcastLogs.add("SocketMainActivity onResume "+RiCiWifiServer.getInstance().getCurrentUserId());
			MainApplication.getInstance().saveLogs(SocketMainActivity.this);
			ShowReaultToast.showToast(SocketMainActivity.this, getResources().getString(R.string.session_timeout), Toast.LENGTH_LONG);
			SocketMainActivity.this.finish();
			getSocketBLL().loginOut();
			Intent loginIntent = new Intent(SocketMainActivity.this,
					LoginActivity.class);
			startActivity(loginIntent);
		}
		else if (!getSocketBLL().handleHasDeviceOnLine()) {
			MainApplication.getInstance().broadcastLogs.add("none device online");
			MainApplication.getInstance().saveLogs(SocketMainActivity.this);
			SocketMainActivity.this.finish();
			Intent searchIntent = new Intent(SocketMainActivity.this,
					SearchResultActivity.class);
			startActivity(searchIntent);
		}
		ImageButton addButton = (ImageButton) findViewById(R.id.btn_add_socket);
		if (addButton != null) {
			addButton.setEnabled(true);
		}
		setCurrentHandler(socketHandler);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return tabFragments.get(position);
		}

		@Override
		public int getCount() {
			return tabFragments.size();
		}
	}

	private void updateTabButton(View v) {
		ImageButton button = null;
		if (v.getId() == R.id.ib_socket) {
			button = (ImageButton) findViewById(R.id.ib_socket);
			button.setBackgroundResource(R.drawable.button_socket_over);
			button = (ImageButton) findViewById(R.id.ib_saving);
			button.setBackgroundResource(R.drawable.button_energy_saving);
			button = (ImageButton) findViewById(R.id.ib_setting);
			button.setBackgroundResource(R.drawable.button_setting);
			tabWidget.setBackgroundResource(R.drawable.y335_bg_table_notitle);
		} else if (v.getId() == R.id.ib_saving) {
			button = (ImageButton) findViewById(R.id.ib_socket);
			button.setBackgroundResource(R.drawable.button_socket);
			button = (ImageButton) findViewById(R.id.ib_saving);
			button.setBackgroundResource(R.drawable.button_energy_saving_over);
			button = (ImageButton) findViewById(R.id.ib_setting);
			button.setBackgroundResource(R.drawable.button_setting);
			tabWidget.setBackgroundResource(R.drawable.y335_bg_table);
		} else if (v.getId() == R.id.ib_setting) {
			button = (ImageButton) findViewById(R.id.ib_socket);
			button.setBackgroundResource(R.drawable.button_socket);
			button = (ImageButton) findViewById(R.id.ib_saving);
			button.setBackgroundResource(R.drawable.button_energy_saving);
			button = (ImageButton) findViewById(R.id.ib_setting);
			button.setBackgroundResource(R.drawable.button_setting_over);
			tabWidget.setBackgroundResource(R.drawable.y335_bg_table);
		}
	}

	public void TabButtonClick(View v) {
		if (v.getId() == R.id.ib_socket) {
			tabWidget.setCurrentTab(0);
			mViewPager.setCurrentItem(0);
		} else if (v.getId() == R.id.ib_saving) {
			tabWidget.setCurrentTab(1);
			mViewPager.setCurrentItem(1);
		} else if (v.getId() == R.id.ib_setting) {
			tabWidget.setCurrentTab(2);
			mViewPager.setCurrentItem(2);
		}
		updateTabButton(v);
	}

	public void socketElectricityButtonClick(View v) {
		if (v.getId() == R.id.btn_add_socket) {			
			getSocketBLL().setMainActivity(SocketMainActivity.this);
			Intent addIntent = new Intent(SocketMainActivity.this,
					SearchResultActivity.class);
			addIntent.putExtra(SearchResultActivity.SHOW_BACK_STRING, true);
			startActivity(addIntent);
		} else {
			SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
					.get(0);
			seFragment.socketElectricityButtonClick(v);
			if(v.getId()==R.id.btn_socket_power){
				sendWaitMessage(socketHandler, WaitProgressView.WAIT_TIME);
			}
		}
	}

	public void systemSettingButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setting_aboutme:
			Intent aboutUsIntent = new Intent(SocketMainActivity.this,
					AboutUsActivity.class);
			startActivity(aboutUsIntent);
			break;
		case R.id.btn_timing_function:
			Intent timingFunction = new Intent(SocketMainActivity.this,
					TimingFunctionActivity.class);
			startActivity(timingFunction);

			break;
		case R.id.btn_electricity_analyse:
			Intent electricityAnalyseIntent = new Intent(
					SocketMainActivity.this, ElectricityAnalyseActivity.class);
			startActivity(electricityAnalyseIntent);
			break;
		case R.id.btn_setting_modify_password:
			Intent modifyPasswordIntent = new Intent(SocketMainActivity.this,
					ModifyPasswordActivity.class);
			startActivity(modifyPasswordIntent);
			break;
		case R.id.btn_setting_login_out:
			SocketMainActivity.this.finish();
			getSocketBLL().loginOut();
			Intent loginFunction = new Intent(SocketMainActivity.this,
					LoginActivity.class);
			startActivity(loginFunction);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.exit_click_again),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// getDanausBLL().exitAllDeviceSocket();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class SocketElectricityHandler extends Handler {
		public SocketElectricityHandler() {

		}

		public SocketElectricityHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_ADD:
				if (msg.obj != null && msg.obj instanceof SocketInfo) {
					SocketInfo socketInfo = (SocketInfo)msg.obj;
					if (socketInfo != null) {
						showCheckMessageLong(String.format(getResources()
								.getString(R.string.add_new_socket_synch),
								socketInfo.getDeviceInfo().getDeviceName()));

						SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
								.get(0);
						seFragment.initSocketElectricity();
						seFragment.changeItem();
					}
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_DELETE:
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					if (getSocketBLL().handleHasDeviceOnLine()) {
						if (msg.arg2 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
							showCheckMessageLong(String
									.format(getResources()
											.getString(
													R.string.delete_current_socket_synch),
											msg.obj.toString()));
							SocketMainActivity.this.finish();
							Intent searchIntent = new Intent(
									SocketMainActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						} else {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.delete_socket_synch),
									msg.obj.toString()));
							SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
									.get(0);
							seFragment.initSocketElectricity();
							seFragment.changeItem();
						}
					} else {
						if (msg.obj != null) {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.delete_socket_synch),
									msg.obj.toString()));
							SocketMainActivity.this.finish();
							Intent searchIntent = new Intent(
									SocketMainActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						}
					}
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_MODIFY:
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_REAL_TIME_ELECTRICITY:
			case BLLUtil.LOCAL_MESSAGE_GET_SOCKET_ELECTRICITY:
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_SWITCH_STATE:
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
							.get(0);
					seFragment.refreshSocket();
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_ONOFF_LINE:
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					if (getSocketBLL().handleHasDeviceOnLine()) {
						if (msg.obj != null && msg.obj instanceof SocketInfo) {
							SocketInfo socketInfo = (SocketInfo) msg.obj;
							if (socketInfo.getDeviceInfo().isDeviceOffline()&&msg.arg2 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
								showCheckMessageLong(String
										.format(getResources()
												.getString(
														R.string.onoff_line_current_socket_synch),
														socketInfo.getDeviceInfo().getDeviceName()));
								SocketMainActivity.this.finish();
								Intent searchIntent = new Intent(
										SocketMainActivity.this,
										SearchResultActivity.class);
								startActivity(searchIntent);
							} else {
								showCheckMessageLong(String
										.format(getResources()
												.getString(
														R.string.on_line_socket_synch),
														socketInfo.getDeviceInfo().getDeviceName()));

								SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
										.get(0);
								seFragment.initSocketElectricity();
								seFragment.changeItem();
							}
						}
					} else {
						if (msg.obj != null) {
							showCheckMessageLong(String.format(
									getResources().getString(
											R.string.off_line_socket_synch),
									msg.obj.toString()));
							SocketMainActivity.this.finish();
							Intent searchIntent = new Intent(
									SocketMainActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						}
					}
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_FIRMWARE_UPDATING:
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					if (getSocketBLL().handleHasDeviceOnLine()) {
						if (msg.arg2 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
							showCheckMessageLong(String
									.format(getResources()
											.getString(
													R.string.current_firmware_updating_synch),
											msg.obj.toString()));
							SocketMainActivity.this.finish();
							Intent searchIntent = new Intent(
									SocketMainActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						} else {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.firmware_updating_synch),
									msg.obj.toString()));
							SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
									.get(0);
							seFragment.initSocketElectricity();
							seFragment.changeItem();
						}
					} else {
						if (msg.obj != null) {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.firmware_updating_synch),
									msg.obj.toString()));
							SocketMainActivity.this.finish();
							Intent searchIntent = new Intent(
									SocketMainActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						}
					}
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SET_SOCKET_POWER:
			case BLLUtil.LOCAL_MESSAGE_SET_SOCKET_POWER:
				dissmissProgressDialog();
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					SocketElectricityFragment seFragment = (SocketElectricityFragment) tabFragments
							.get(0);
					seFragment.refreshSocket();
				} else {
					L.d("error_code=" + msg.arg1);
					showCheckMessage(BLLUtil.getInstance().getErrorMessage(
							msg.arg1));
				}
				break;
			}
		}
	}
}
