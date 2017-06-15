package rici.roplug.open.activity;

import com.rici.wifi.util.WifiUtil;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class FirstActivity extends BaseActivity {
	private static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private PopupDialogView popupDialogView = null;
	private FirstHandler firstHandler = null;
	private Handler waitProgressHandler = new Handler();
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			dissmissProgressDialog();
			if(popupDialogView!=null && popupDialogView.isShowing()){
				popupDialogView.dismiss();
				popupDialogView=null;
			}
			if (popupDialogView == null) {
				popupDialogView = new PopupDialogView(FirstActivity.this);
				popupDialogView.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
				popupDialogView.setTitleText(getResources().getString(
						R.string.server_link_timeout_title));
				popupDialogView.setLeftButtonClick(leftButtonClick);
				popupDialogView.setRightButtonClick(rightButtonClick);
				popupDialogView.setMessageContent(getResources().getString(
						R.string.server_link_timeout));
				popupDialogView.show();
			}
		}
	};
	public OnPopupDialogButtonClick leftButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			Intent searchIntent = new Intent(FirstActivity.this,
					SearchResultActivity.class);
			startActivity(searchIntent);
			FirstActivity.this.finish();
		}
	};
	public OnPopupDialogButtonClick rightButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			if (WifiUtil.isNetConnected(FirstActivity.this)) {
				showProgressDialog(0, BLLUtil.WIFI_CONNECTED_TIMEOUT);
				setCurrentHandler(firstHandler);
				getSocketBLL().linkRemoteServer();
				waitProgressHandler.postDelayed(waitProgressRunnable,
						BLLUtil.WIFI_CONNECTED_TIMEOUT);
			}
		}
	};

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showNetInfo(WifiUtil.isNetConnected(context));
			if (!WifiUtil.isNetConnected(context)) {
				getSocketBLL().exitAllDeviceSocket();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_empty_layout);
		firstHandler = new FirstHandler();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (WifiUtil.isNetConnected(FirstActivity.this)) {
			showProgressDialog(0, BLLUtil.WIFI_CONNECTED_TIMEOUT);
			setCurrentHandler(firstHandler);
			getSocketBLL().linkRemoteServer();
			waitProgressHandler.postDelayed(waitProgressRunnable,
					BLLUtil.WIFI_CONNECTED_TIMEOUT);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	class FirstHandler extends Handler {
		public FirstHandler() {

		}

		public FirstHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_LINK_SERVER:
				dissmissProgressDialog();
				waitProgressHandler.removeCallbacks(waitProgressRunnable);
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					Intent loginIntent = new Intent(FirstActivity.this,
							LoginActivity.class);
					startActivity(loginIntent);
					FirstActivity.this.finish();
				} else {
					if(popupDialogView!=null && popupDialogView.isShowing()){
						popupDialogView.dismiss();
						popupDialogView=null;
					}
					if (popupDialogView == null) {
						popupDialogView = new PopupDialogView(
								FirstActivity.this);
						popupDialogView
								.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
						popupDialogView.setTitleText(getResources().getString(
								R.string.server_link_timeout_title));
						popupDialogView.setLeftButtonClick(leftButtonClick);
						popupDialogView.setRightButtonClick(rightButtonClick);
						popupDialogView.setMessageContent(getResources()
								.getString(R.string.server_link_timeout));
						popupDialogView.show();
					}
				}
				break;
			}
		}
	}
}
