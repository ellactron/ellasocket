package rici.roplug.open.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.rici.wifi.RiCiWifiServer;
import com.rici.wifi.bean.DeviceInfo;
import com.rici.wifi.util.WifiUtil;

import rici.roplug.open.R;
import rici.roplug.open.adapter.DeviceAdapter;
import rici.roplug.open.application.MainApplication;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.bean.SocketInfo;
import rici.roplug.open.util.autoUpdate.CheckUpdateAsyncTask;
import rici.roplug.open.util.autoUpdate.UpdateReceiver;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import rici.roplug.open.view.RefreshListView;
import rici.roplug.open.view.RefreshListView.OnRefreshListener;
import rici.roplug.open.view.ShowReaultToast;
import rici.roplug.open.view.WaitProgressView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchResultActivity extends BaseActivity implements
		DeviceAdapter.OnDeviceItemClickListener {
	public final static String SHOW_BACK_STRING = "show_back";
	private Handler searchResultHandler = null;
	private FrameLayout layout = null;
	private Button showBackButton = null;
	private ImageButton switchUserButton = null;
	private RefreshListView deviceListView = null;
	private DeviceAdapter searchListAdapter = null;
	private DeviceInfo currentDevice = null;
	private long exitTime = 0;
	private boolean showBack = false;
	protected int activityCloseEnterAnimation;
	protected int activityCloseExitAnimation;
	protected int clickItem = -1;
	protected String deleteMac = null;
	private UpdateReceiver receiver = null;
	private Context mContext = null;
	private boolean issearching = false;
	private boolean hasRepeatSearch = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_layout);
		getSocketBLL().getDataBaseInfo();
		mContext = this;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				MainApplication.getInstance().saveLogs(SearchResultActivity.this);
			}
		}, 0,1000);
		initActivity();
	}

	private Handler waitProgressHandler = new Handler();
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			if (deviceListView != null) {
				deviceListView.onRefreshComplete();
			}
		}
	};

	private OnPopupDialogButtonClick renameButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			String newName = String.valueOf(arg1);
			if(null != newName && !"". equals(newName) && newName.length() <= 20){
				sendWaitMessage(searchResultHandler, WaitProgressView.WAIT_TIME);
				showProgressDialog(WaitProgressView.DELAY_TIME,
						WaitProgressView.WAIT_TIME);
				getSocketBLL().modifyDeviceName(
						getSocketBLL().getAllSocketInfos().get(clickItem)
								.getDeviceInfo(), newName);
			}else{  
				showCheckMessage(R.string.rename_failure);
			}
		}
	};
 
	private OnPopupDialogButtonClick deleteButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			sendWaitMessage(searchResultHandler, WaitProgressView.WAIT_TIME);
			showProgressDialog(WaitProgressView.DELAY_TIME,
					WaitProgressView.WAIT_TIME);
			getSocketBLL().deleteDevice(
					getSocketBLL().getAllSocketInfos().get(clickItem)
							.getDeviceInfo());
		}
	};

	private OnPopupDialogButtonClick switchUserButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			SearchResultActivity.this.finish();
			getSocketBLL().loginOut();
			Intent loginFunction = new Intent(SearchResultActivity.this,
					LoginActivity.class);
			startActivity(loginFunction);
		}
	};

	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.net_status_bar_top:
				// showNetInfo(true);
				startActivity(new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS));
				break;
			/*case R.id.btn_menu_weixin_control:
				getSocketBLL().genWeiXinQRcode();
				break;*/
			case R.id.btn_menu_up:
				if (getSocketBLL().getAllSocketInfos().get(clickItem)
						.isDeviceOnline()&&getSocketBLL().getAllSocketInfos().get(clickItem)
						.getDeviceInfo().isShouldUpdateFirmware(currentDevice.getFiremwareType(),currentDevice.getCurrentFirmwareVersion(),getSocketBLL().getAllSocketInfos().get(clickItem)
								.getDeviceInfo())) {
					PopupDialogView updateDialogView = new PopupDialogView(
							SearchResultActivity.this);
					updateDialogView
							.setDialogType(PopupDialogView.POPUPWINDOW_TEXT); 
					updateDialogView
							.setTitleText(R.string.firmware_update_title);
					updateDialogView.setRightButtonClick(updateButtonClick);
					updateDialogView.setMessageContent(getResources()
							.getString(R.string.new_firmware_version_info));
					updateDialogView.show();
				} else {
					final PopupDialogView renameDialogView = new PopupDialogView(
							SearchResultActivity.this);
					renameDialogView
							.setDialogType(PopupDialogView.POPUPWINDOW_EDITTEXT);
					renameDialogView.setTitleText(R.string.button_rename);
					renameDialogView.setRightButtonClick(renameButtonClick);
					renameDialogView.setMessageContent(getSocketBLL()
							.getAllSocketInfos().get(clickItem).getDeviceInfo()
							.getDeviceName());
					renameDialogView.show();
					renameDialogView.getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
					renameDialogView.getEditText().setSelection(
							renameDialogView.getEditText().length());
					(new Handler()).postDelayed(new Runnable() {
						public void run() {
							InputMethodManager inManager = (InputMethodManager) renameDialogView
									.getEditText()
									.getContext()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							inManager.toggleSoftInput(0,
									InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}, 500);
				}
				break;
			case R.id.btn_menu_middle:
				final PopupDialogView renameDialogView = new PopupDialogView(
						SearchResultActivity.this);
				renameDialogView
						.setDialogType(PopupDialogView.POPUPWINDOW_EDITTEXT);
				renameDialogView.setTitleText(R.string.button_rename);
				renameDialogView.setRightButtonClick(renameButtonClick);
				renameDialogView.setMessageContent(getSocketBLL()
						.getAllSocketInfos().get(clickItem).getDeviceInfo()
						.getDeviceName());
				renameDialogView.show();
				renameDialogView.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				renameDialogView.getEditText().setSelection(
						renameDialogView.getEditText().length());
				(new Handler()).postDelayed(new Runnable() {
					public void run() {
						InputMethodManager inManager = (InputMethodManager) renameDialogView
								.getEditText().getContext()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inManager.toggleSoftInput(0,
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}, 500);
				break;
			case R.id.btn_menu_down:
				PopupDialogView deleteDialogView = new PopupDialogView(
						SearchResultActivity.this);
				deleteDialogView
						.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
				deleteDialogView.setTitleText(R.string.button_delete);
				deleteDialogView.setMessageContent(String.format(getResources()
						.getString(R.string.delete_confirm_text),
						getSocketBLL().getAllSocketInfos().get(clickItem)
								.getDeviceInfo().getDeviceName()));
				deleteDialogView.setRightButtonClick(deleteButtonClick);
				deleteDialogView.show();
				break;
			default:
				break;
			}
		}
	};

	Button emptyView = null;
	private void loadDeviceInfo() {
		deviceListView = (RefreshListView) findViewById(R.id.lv_search_list);
		emptyView = (Button) findViewById(R.id.btn_failure_refresh);
		searchListAdapter = new DeviceAdapter(SearchResultActivity.this,
				getSocketBLL().getAllSocketInfos());
		searchListAdapter.setOnDeviceItemClickListener(this);
		deviceListView.setAdapter(searchListAdapter);
//		deviceListView.setEmptyView(emptyView);
		if(getSocketBLL().getAllSocketInfos().size() == 0){
			emptyView.setVisibility(View.VISIBLE);
		}
		deviceListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						issearching = false;
						if (WifiUtil.isNetConnected(SearchResultActivity.this)) {
							if (isCanRemoteServer()) {
								getSocketBLL().getAllDevice();
							}
							if (WifiUtil.getNetType(SearchResultActivity.this) == ConnectivityManager.TYPE_WIFI) {
								hideEmptyView();
								getSocketBLL().searchAllDevice(
										BLLUtil.WIFI_SEARCH_DEFAULT_TIMEOUT);
							}
						} else {
							dissmissProgressDialog();
							/*if (deviceListView != null) {
								deviceListView.onRefreshComplete();
							}*/
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// searchListAdapter.notifyDataSetChanged();
						// searchListView.onRefreshComplete();
						if (deviceListView != null) {
							deviceListView.onRefreshComplete();
						}
					}
				}.execute(null, null, null);
			}
		});
	}
	private void hideEmptyView() {
		Handler handler = new Handler(mContext.getMainLooper());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
					emptyView.setVisibility(View.GONE);
			}
		});
	}

	private void initActivity() {
		searchResultHandler = new SearchResultHandler(getMainLooper());

		layout = (FrameLayout) findViewById(R.id.fl_search_layout);
		Intent intent = getIntent();
		showBack = intent.getBooleanExtra(SHOW_BACK_STRING, false);
		showBackButton = (Button) findViewById(R.id.btn_title_return);
		switchUserButton = (ImageButton) findViewById(R.id.ib_switch_user);
		if (!showBack) {
			switchUserButton.setVisibility(View.VISIBLE);
			showBackButton.setGravity(Gravity.CENTER);
			showBackButton.setEnabled(false);
			showBackButton.setCompoundDrawables(null, null, null, null);
			// 注册一个广播
			IntentFilter intentFilter = new IntentFilter(
					UpdateReceiver.ACTION_PROCRESS);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
			// 添加一个Category属性，CheckUpdateAsyncTask发送广播时候也要添加该属性。保持遥相呼应
			receiver = new UpdateReceiver();
			registerReceiver(receiver, intentFilter);
			// 启动后台异步执行检查更新
			CheckUpdateAsyncTask checkAsyncTask = new CheckUpdateAsyncTask(
					SearchResultActivity.this);
			checkAsyncTask.execute(10);
		} else {
			TypedArray activityStyle = getTheme().obtainStyledAttributes(
					new int[] { android.R.attr.windowAnimationStyle });

			int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);

			activityStyle.recycle();

			activityStyle = getTheme().obtainStyledAttributes(
					windowAnimationStyleResId,
					new int[] { android.R.attr.activityCloseEnterAnimation,
							android.R.attr.activityCloseExitAnimation });

			activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
			activityCloseExitAnimation = activityStyle.getResourceId(1, 0);

			activityStyle.recycle();
		}
		loadDeviceInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
		setProgressDialogContent(R.string.progress_wait);
		setCurrentHandler(searchResultHandler);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!showBack) {
			try {
				if (receiver != null) {
					unregisterReceiver(receiver);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!showBack)
			overridePendingTransition(activityCloseEnterAnimation,
					activityCloseExitAnimation);
	}

	public void searchDevice(View v) {
		switch (v.getId()) {
		case R.id.btn_failure_refresh:
			if (WifiUtil.isNetConnected(SearchResultActivity.this)) {
				showProgressDialog(R.string.progress_search_device);
				if (isCanRemoteServer()) {
					getSocketBLL().getAllDevice();
				}
				if (WifiUtil.getNetType(SearchResultActivity.this) == ConnectivityManager.TYPE_WIFI) {
					getSocketBLL().searchAllDevice(
							BLLUtil.WIFI_SEARCH_DEFAULT_TIMEOUT);
				}
			}
			break;
		case R.id.btn_title_return:
			SearchResultActivity.this.finish();
			break;
		case R.id.ib_switch_user:
			PopupDialogView deleteDialogView = new PopupDialogView(
					SearchResultActivity.this);
			deleteDialogView.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
			// deleteDialogView.setTitleText(R.string.button_switch_user);
			deleteDialogView.setHasTitle(false);
			deleteDialogView.setMessageContent(getResources().getString(
					R.string.switch_user_confirm_text));
			deleteDialogView.setRightButtonClick(switchUserButtonClick);
			deleteDialogView.show();

			break;
		case R.id.ib_search_device:
			if (WifiUtil.isNetConnected(SearchResultActivity.this)
					&& (WifiUtil.getNetType(SearchResultActivity.this) == ConnectivityManager.TYPE_WIFI)) {
				final PopupDialogView wifiLinkPopupDialogView = new PopupDialogView(
						SearchResultActivity.this);
				wifiLinkPopupDialogView
						.setDialogType(PopupDialogView.POPUPWINDOW_INPUT_WIFI_PASSWORD);
				wifiLinkPopupDialogView.setTitleText(getResources().getString(
						R.string.network_link));
				wifiLinkPopupDialogView.setArg1(WifiUtil
						.getCurrentWifiSSID(SearchResultActivity.this));
				wifiLinkPopupDialogView
						.setRightButtonClick(sendWifiPasswordButtonClick);
				wifiLinkPopupDialogView.show();
				wifiLinkPopupDialogView.getEditText().requestFocus();
				wifiLinkPopupDialogView.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				(new Handler()).postDelayed(new Runnable() {
					public void run() {
						InputMethodManager inManager = (InputMethodManager) wifiLinkPopupDialogView
								.getEditText().getContext()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inManager.toggleSoftInput(0,
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}, 500);
				
			}
			break;
		}
	}

	private void handleSendBroadcaseFinish(int deviceCount) {
		if (deviceCount == 0) {
			  if(issearching){
				  if(!hasRepeatSearch){
					  searchDevices();
					  hasRepeatSearch = true;
				  }else{
					  hasRepeatSearch = false;
					  issearching = false;
					  updateView(deviceCount);
				  }
			  }else{
				  updateView(deviceCount);
			  }
		} else {
			updateView(deviceCount);
			if (searchListAdapter != null) {
				searchListAdapter.notifyDataSetChanged();
			}
		}
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 10:
				emptyView.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	
	private void updateView(int count){ 
//		new Toast(mContext).makeText(mContext, getResources().getString(R.string.search_none_device), Toast.LENGTH_SHORT).show();
		if(count == 0)
			ShowReaultToast.showToast(SearchResultActivity.this, 
					  R.string.search_none_device, Toast.LENGTH_SHORT);
		dissmissProgressDialog();
		waitProgressHandler.removeCallbacks(waitProgressRunnable);
		if(getSocketBLL().getAllSocketInfos().size() == 0)
			emptyView.setVisibility(View.VISIBLE);
		else
			emptyView.setVisibility(View.GONE);
		
		if (deviceListView != null) {
			deviceListView.onRefreshComplete();
		}
	}
	
	private void refreshSearchAdapter() {
		if(!getSocketBLL().handleHasDeviceOnLine()){
			if(getSocketBLL().getMainActivity()!=null){
				getSocketBLL().getMainActivity().finish();
			}
			if(getSocketBLL().getAllSocketInfos().size() > 0 ){
				if(emptyView.getVisibility() == View.VISIBLE){
					emptyView.setVisibility(View.GONE);
				}
			}else{
				if(emptyView.getVisibility() != View.VISIBLE){
					emptyView.setVisibility(View.VISIBLE);
				}
			}
//			L.d("==getSocketBLL().getAllSocketInfos().size() ====   " + getSocketBLL().getAllSocketInfos().size());
			showBack = false;	
			switchUserButton.setVisibility(View.VISIBLE);
			showBackButton.setGravity(Gravity.CENTER);
			showBackButton.setEnabled(false);
			showBackButton.setCompoundDrawables(null, null, null, null);
		}
		if (searchListAdapter != null) {
			searchListAdapter.notifyDataSetChanged();
		}
	}

	public OnPopupDialogButtonClick sendWifiPasswordButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			issearching = true;
			searchResultHandler.sendEmptyMessage(CMD_SHOW_PROGRESS_DIALOG);
			MainApplication.getInstance().wifiName = arg1.toString();
			MainApplication.getInstance().wifiPwd = arg2.toString();
			searchDevices();
		}
	};
//java.lang.NullPointerException: Attempt to invoke virtual method 'void java.net.DatagramSocket.receive(java.net.DatagramPacket)' on a null object reference

	private void searchDevices(){
		MainApplication.getInstance().setIsNewVersion(!MainApplication.getInstance().isNewVersion());
		getSocketBLL().scanDevice(MainApplication.getInstance().wifiName, MainApplication.getInstance().wifiPwd);
//		new Toast(mContext).makeText(mContext, MainApplication.getInstance().isNewVersion()?"新版SDK" : "旧版sdk", Toast.LENGTH_SHORT).show();
//		L.e(MainApplication.getInstance().isNewVersion()?"新版SDK" : "旧版sdk");
	}
	public OnPopupDialogButtonClick checkButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			if (arg1 != null && arg1.toString().length() > 0) {
				currentDevice.setLinkPassword(arg1.toString());
				currentDevice.setRemember(Boolean.valueOf(arg2.toString()));
				showProgressDialog(0, WaitProgressView.WAIT_TIME);
				getSocketBLL().checkLoginPassword(currentDevice,
						arg1.toString());
			}
		}
	};

	private OnPopupDialogButtonClick updateButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			setProgressDialogContent(R.string.progress_wait);

			sendWaitMessage(searchResultHandler, WaitProgressView.WAIT_TIME);
			showProgressDialog(WaitProgressView.DELAY_TIME,
					WaitProgressView.WAIT_TIME);
			getSocketBLL().updateFirmware(clickItem);
		}
	};

	@Override
	public void onItemClick(int deviceIndex) {
		clickItem = deviceIndex;
		if (WifiUtil.isNetConnected(SearchResultActivity.this) && deviceIndex < getSocketBLL().getAllSocketInfos().size()) {
			currentDevice = getSocketBLL().getAllSocketInfos().get(deviceIndex)
					.getDeviceInfo();
			if (currentDevice.isUpdatingFirmware()) {

			} else if (currentDevice.isDeviceOnline()) {
				getSocketBLL().setCurrentSocketIndex(deviceIndex);
				if (!showBack) {
					Intent intent = new Intent(SearchResultActivity.this,
							SocketMainActivity.class);
					startActivity(intent);
				}
				SearchResultActivity.this.finish();
			} else if (WifiUtil.getNetType(SearchResultActivity.this) == ConnectivityManager.TYPE_WIFI) {
				if (currentDevice.isDeviceOffline()) {
					/*showProgressDialog(R.string.progress_search_device);
					getSocketBLL().linkDevice(currentDevice);*/
				} else {
						setProgressDialogContent(R.string.progress_add_device);
						sendWaitMessage(searchResultHandler, 60*1000);
						showProgressDialog(0, 60*1000);
						getSocketBLL().addNewDevice(currentDevice);
				}
			}
		}
	}

	@Override
	public void onItemLongClick(int deviceIndex) {
		
		clickItem = deviceIndex;
		currentDevice = getSocketBLL().getAllSocketInfos().get(deviceIndex)
				.getDeviceInfo();
		if(!currentDevice.isUpdatingFirmware()){
		PopupDialogView popupMenuDialogView = new PopupDialogView(
				SearchResultActivity.this);

		popupMenuDialogView
				.setDialogType((currentDevice.isDeviceOnline() &&currentDevice.isShouldUpdateFirmware(currentDevice.getFiremwareType(),currentDevice.getCurrentFirmwareVersion(),currentDevice)) ? PopupDialogView.POPUPWINDOW_MENU_THREE
						: PopupDialogView.POPUPWINDOW_MENU);
		if (!(currentDevice.isDeviceOnline() &&currentDevice.isShouldUpdateFirmware(currentDevice.getFiremwareType(),currentDevice.getCurrentFirmwareVersion(),currentDevice))) {
			popupMenuDialogView.setArg1(getResources().getString(
					R.string.button_rename));
		}
		popupMenuDialogView
				.setButtonCount(PopupDialogView.POPUPWINDOW_BUTTON_NONE);
		popupMenuDialogView.setMenuItemClickListener(itemsOnClick);
		popupMenuDialogView.setHasTitle(false);
		popupMenuDialogView.show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!showBack && keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000 && !showBack) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.exit_click_again),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				//getSocketBLL().exitAllDeviceSocket();
				if(getSocketBLL().getMainActivity()!=null){
					getSocketBLL().getMainActivity().finish();
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private static final int CMD_SHOW_PROGRESS_DIALOG = 0x101; 
	class SearchResultHandler extends Handler {
		public SearchResultHandler() {

		}

		public SearchResultHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case CMD_SHOW_PROGRESS_DIALOG:
					showProgressDialog(0, 90000);
					emptyView.setVisibility(View.GONE);
					break;
				case BLLUtil.REMOTE_MESSAGE_ADD_DEVICE:
					if (searchListAdapter != null) {
						searchListAdapter.notifyDataSetChanged();
					}				
					break;
				case BLLUtil.REMOTE_MESSAGE_GET_ALL_DEVICE:
					dissmissProgressDialog();
					if (WifiUtil.getNetType(SearchResultActivity.this) != ConnectivityManager.TYPE_WIFI) {
						if (deviceListView != null) {
							deviceListView.onRefreshComplete();
						}
					}
					if (searchListAdapter != null) {
						searchListAdapter.notifyDataSetChanged();
					}
//					if(getSocketBLL().getAllSocketInfos().size() > 0 ){
//						emptyView.setVisibility(View.VISIBLE);
//					}else{
//						hideEmptyView();
//					}
					break;
				case BLLUtil.LOCAL_MESSAGE_ADD_DEV_TO_SERVER:
					dissmissProgressDialog();
					if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
						refreshSearchAdapter();
					}
					break;
				case BLLUtil.REMOTE_MESSAGE_SET_SOCKET_NAME:
				case BLLUtil.LOCAL_MESSAGE_DEVICE_RENAME:
					dissmissProgressDialog();
					if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
						showCheckMessage(R.string.change_name_success);
						refreshSearchAdapter();
					} else if (msg.arg1 == BLLUtil.MSG_ERROR_DEVICE_NAME_EXIST) {
						showCheckMessage(String
								.format(getResources().getString(
										R.string.same_device_name),
										String.valueOf(msg.obj)));
					}else{
						showCheckMessage(String
								.format(getResources().getString(
										R.string.operate_failure)));
					}
					break;
				case BLLUtil.REMOTE_MESSAGE_DELETE_SOCKET:
				case BLLUtil.LOCAL_MESSAGE_DELETE:
					dissmissProgressDialog();
					if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
						showCheckMessage(R.string.delete_success);
						refreshSearchAdapter();
					}
					break;
	
				case BLLUtil.LOCAL_MESSAGE_SEND_BROADCAST_FINISH:
					handleSendBroadcaseFinish(msg.arg1);
					break;
				case BLLUtil.LOCAL_MESSAGE_DISCOVER_DEVICE:
					dissmissProgressDialog();
					if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
						if (isCanRemoteServer()) {
							getSocketBLL().addNewDevice(currentDevice);
						}
					} else {
	
					}
					if(getSocketBLL().getAllSocketInfos().size() > 0 ){
						hideEmptyView();
					}else{
						emptyView.setVisibility(View.VISIBLE);
					}
					break;
				case BLLUtil.LOCAL_MESSAGE_CHECK_LOGIN_PASSWORD:
					dissmissProgressDialog();
					currentDevice
							.setAutoLink(msg.arg1 == RiCiWifiServer.RESPONSE_RESULT_SUCCESS);
					getSocketBLL().updateDeviceAutoLink(currentDevice);
					if (msg.arg1 == RiCiWifiServer.RESPONSE_RESULT_SUCCESS) {
						if (isCanRemoteServer()) {
							getSocketBLL().addNewDevice(currentDevice);
						} else {
							getSocketBLL().setCurrentSocketIndex(clickItem);
							Intent intent = new Intent(SearchResultActivity.this,
									SocketMainActivity.class);
							startActivity(intent);
							SearchResultActivity.this.finish();
						}
					} else {
						if (currentDevice.getDeviceState() == DeviceInfo.DEVICE_TYPE_LOCATION_OFFLINE) {
							currentDevice
									.setDeviceState(DeviceInfo.DEVICE_TYPE_LOCATION_NEW);
							if (searchListAdapter != null) {
								searchListAdapter.notifyDataSetChanged();
							}
						}
//						L.d("check failure");
						ShowReaultToast.showToast(SearchResultActivity.this,
								R.string.check_password_failure, Toast.LENGTH_SHORT);
					}
					break;
				case BLLUtil.LOCAL_MESSAGE_OFFLINE:
					refreshSearchAdapter();
					break;
				case BLLUtil.LOCAL_MESSAGE_NONE_SOCKET_ONLINE:
					showBack = false;
					switchUserButton.setVisibility(View.VISIBLE);
					showBackButton.setGravity(Gravity.CENTER);
					showBackButton.setEnabled(false);
					showBackButton.setCompoundDrawables(null, null, null, null);
					break;
				case BLLUtil.REMOTE_MESSAGE_FIRMWARE_UPDATE:
					dissmissProgressDialog();
					if (msg.arg1 == RiCiWifiServer.RESPONSE_RESULT_SUCCESS) {
						refreshSearchAdapter();
					}
					else{
						showCheckMessage(R.string.net_timeout);
					}
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_FIRMWARE_UPDATING:	
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_ONOFF_LINE:
					dissmissProgressDialog();
					refreshSearchAdapter();
					break;				
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_ADD:	
					if (msg.obj != null && msg.obj instanceof SocketInfo) {
						SocketInfo socketInfo = (SocketInfo)msg.obj;
						if (socketInfo != null) {
						showCheckMessageLong(String.format(getResources().getString(R.string.add_new_socket_synch),socketInfo.getDeviceInfo().getDeviceName()));	
					}
					}
					refreshSearchAdapter();
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_DELETE:	
					if(msg.obj!=null){
						showCheckMessageLong(String.format(getResources().getString(R.string.delete_socket_synch),msg.obj.toString()));		
					}
					refreshSearchAdapter();
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_MODIFY:
					if(msg.arg2< getSocketBLL().getAllSocketInfos().size()){
						String newName = getSocketBLL().getAllSocketInfos().get(msg.arg2).getDeviceInfo().getDeviceName();
						showCheckMessageLong(String.format(getResources().getString(R.string.modify_socket_synch),msg.obj.toString(),newName));				
					}
					refreshSearchAdapter();
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_FIRMWARE_UPDATE_RESULT:
					showCheckMessageLong(String.format(getResources().getString(((msg.arg1 == RiCiWifiServer.RESPONSE_RESULT_SUCCESS) ? R.string.firmware_update_success
							: R.string.firmware_update_failure)),msg.obj.toString()));
					refreshSearchAdapter();
					break;
				case BLLUtil.REMOTE_MESSAGE_WEIXIN_QRCODE:
					if(msg.obj!=null && msg.obj.toString().length()>0){
//						L.d("msg.obj="+msg.obj.toString());
						PopupDialogView showQRcodeDialogView = new PopupDialogView(
								SearchResultActivity.this);
						showQRcodeDialogView.setDialogType(PopupDialogView.POPUPWINDOW_IMAGEVIEW);
						// deleteDialogView.setTitleText(R.string.button_switch_user);
						showQRcodeDialogView.setHasTitle(false);
						showQRcodeDialogView.setArg1(msg.obj.toString());
						showQRcodeDialogView.setMessageContent(getResources().getString(
								R.string.switch_user_confirm_text));
						showQRcodeDialogView.setRightButtonClick(switchUserButtonClick);
						showQRcodeDialogView.show();
					}
					break;
			}
		}
	}
}
