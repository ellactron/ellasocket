package rici.roplug.open.activity;

import com.rici.wifi.util.L;
import com.rici.wifi.util.WifiUtil;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.RemoteServerConnecte;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import rici.roplug.open.view.ShowReaultToast;
import rici.roplug.open.view.WaitProgressView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
	private static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private static boolean isLeave = false;

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION)){
			showNetInfo(WifiUtil.isNetConnected(context));
			if (!WifiUtil.isNetConnected(context)) {
				getSocketBLL().exitAllDeviceSocket();
			}
			}
			else if(intent.getAction().equals(BLLUtil.SESSION_TIMEOUT_ACTION)){
				showSessionTimeout();
			}
		}
	};
	private SocketBLL socketBLL;
	private View decorView = null;
	private WaitProgressView waitProgressView = null;
	private PopupDialogView netErrorDialogView = null;
	private static SharedPreferences pref = null;
	private PopupDialogView sessionTimeout = null;

	private Handler activityHandler = null;
	private Handler waitProgressHandler = new Handler();
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			if(getActivityHandler()!=null){
				Message message = getActivityHandler().obtainMessage();
				message.what = BLLUtil.REMOTE_NETWORK_ERROR_TIMEOUT;
				message.arg1 = BLLUtil.MSG_ERROR_NETWORK_TIMEOUT;
				message.sendToTarget();				
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置背景
		if(socketBLL==null){
		decorView = this.getWindow().getDecorView(); // 获得window最顶层的View
		decorView.setBackgroundResource(R.drawable.y335_bg_list_view);
		waitProgressView = new WaitProgressView(this, 0);
		netErrorDialogView = new PopupDialogView(this);
		netErrorDialogView.setDialogType(PopupDialogView.POPUPWINDOW_NET_ERROR);
		netErrorDialogView.setDialogLayout(R.layout.net_status_bar);
		netErrorDialogView
				.setButtonCount(PopupDialogView.POPUPWINDOW_BUTTON_NONE);
		socketBLL = SocketBLL.getInstance();
		socketBLL.initDbHelper(this);
		pref = getSharedPreferences("timeoutcheck", Context.MODE_PRIVATE);
		}
		showNetInfo(WifiUtil.isNetConnected(this));
	}

	@Override
	protected void onUserLeaveHint() { // 当用户按Home键等操作使程序进入后台时即开始计时
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		if (!isLeave) {
			isLeave = true;
			saveStartTime();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		filter.addAction(BLLUtil.SESSION_TIMEOUT_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(myReceiver, filter);

		if (isLeave) {
			isLeave = false;
			timeOutCheck();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
		}
	}

	public SocketBLL getSocketBLL() {
		return socketBLL;
	}

	public boolean isCanRemoteServer() {
		return getSocketBLL().getIsCanRemoteBoolean();
	}

	public void setCurrentHandler(Handler currentHandler) {
		if (getSocketBLL() != null) {
			getSocketBLL().initDanausBLL(this, currentHandler);
		}
	}

	public void showCheckMessage(int messageId) {
		ShowReaultToast.showToast(this, messageId, Toast.LENGTH_SHORT);
	}

	public void showCheckMessage(String message) {
		ShowReaultToast.showToast(this, message, Toast.LENGTH_SHORT);
	}

	public void showCheckMessageLong(String message) {
		ShowReaultToast.showToast(this, message, Toast.LENGTH_LONG);
	}
	
	public void setProgressDialogContent(int msgID){
		if(waitProgressView!=null){
			waitProgressView.setShowText(msgID);
		}
	}
	public void setProgressDialogContent(String showString){
		if(waitProgressView!=null){
			waitProgressView.setShowText(showString);
		}
	}
	
	public void showProgressDialog(int msgId) {
		if (waitProgressView != null) {
			waitProgressView.hide();
		}
		setProgressDialogContent(msgId);
		waitProgressView.show(0,-1);
	}

	public void showProgressDialog(int delayTime, int waitTime) {
		if (waitProgressView != null) {
			waitProgressView.hide();
		}
		waitProgressView.show(delayTime, waitTime);
	}

	public void dissmissProgressDialog() {
		if (waitProgressView != null) {
			waitProgressView.hide();
		}
		stopWaitMessage();
	}

	public void showNetInfo(boolean isNetConnected) {
		if (!isNetConnected) {
			netErrorDialogView.show();
		} else {
			if (netErrorDialogView.isShowing()) {
				netErrorDialogView.dismiss();
			}
		}
	}

	private OnPopupDialogButtonClick sessionTimeoutButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			getSocketBLL().loginOut();
			Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	};

	private void showSessionTimeout(){
		if(sessionTimeout==null ||!sessionTimeout.isShowing())
		sessionTimeout = new PopupDialogView(this);
		sessionTimeout.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
		sessionTimeout.setTitleText(R.string.toast_title);
		sessionTimeout.setButtonCount(PopupDialogView.POPUPWINDOW_BUTTON_ONE);
		sessionTimeout.setMessageContent(getResources().getString(
				R.string.session_timeout));
		sessionTimeout.setRightButtonClick(sessionTimeoutButtonClick);
		sessionTimeout.show();
	}
	public void timeOutCheck() {
		long endtime = System.currentTimeMillis();
		if (endtime - getStartTime() >= BLLUtil.APP_SESSION_TIMEOUT && Integer.valueOf(RemoteServerConnecte.getInstance(BaseActivity.this).getClientInfo().getUserId())>0) {
			L.d("后台检测会话超时");
			showSessionTimeout();
		}
	}

	public void saveStartTime() {
		pref.edit().putLong("starttime", System.currentTimeMillis()).commit();
	}

	public long getStartTime() {
		return pref.getLong("starttime", 0);

	}

	public Handler getActivityHandler() {
		return activityHandler;
	}

	public void setActivityHandler(Handler activityHandler) {
		this.activityHandler = activityHandler;
	}
	public void sendWaitMessage(Handler activityHandler,long timeOut){
		this.activityHandler = activityHandler;
		waitProgressHandler.postDelayed(waitProgressRunnable, BLLUtil.WIFI_CONNECTED_TIMEOUT);		
	}
	public void stopWaitMessage(){
		waitProgressHandler.removeCallbacks(waitProgressRunnable);
	}
}
