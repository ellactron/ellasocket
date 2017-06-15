package rici.roplug.open.activity;

import rici.roplug.open.R;
import rici.roplug.open.adapter.TimingFunctionAdapter;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import rici.roplug.open.view.WaitProgressView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class TimingFunctionActivity extends BaseActivity {
	private ListView lvTimingFunction = null;
	private TimingFunctionAdapter timingFunctionAdapter = null;
	private TimingFunctiontHandler timingFunctiontHandler = null;
	protected int clickItem = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timing_function_layout);
		
		timingFunctiontHandler = new TimingFunctiontHandler();
		ImageButton addButton = (ImageButton) findViewById(R.id.btn_title_add);
		addButton.setVisibility(View.VISIBLE);
		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent editIntent = new Intent(TimingFunctionActivity.this,
						EditTimingFunctionActivity.class);
				startActivity(editIntent);
			}
		});
		Button titleReturnButton = (Button) findViewById(R.id.btn_title_return);
		titleReturnButton.setText(R.string.timing_function);
		titleReturnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimingFunctionActivity.this.finish();
			}
		});
		lvTimingFunction = (ListView) findViewById(R.id.lv_timingFunction);
		timingFunctionAdapter = new TimingFunctionAdapter(
				TimingFunctionActivity.this, SocketBLL.getInstance()
						.getAllTimingFunction());
		timingFunctionAdapter
				.setOnItemClickListener(timingFunctionClickListener);
		lvTimingFunction.setAdapter(timingFunctionAdapter);

		getSocketBLL().getAllRemoteTimingFunction();
	}

	@Override
	public void onResume() {
		super.onResume();
		setCurrentHandler(timingFunctiontHandler);
		if (timingFunctionAdapter != null) {
			timingFunctionAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private OnPopupDialogButtonClick deleteButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			sendWaitMessage(timingFunctiontHandler, WaitProgressView.WAIT_TIME);
			showProgressDialog(WaitProgressView.DELAY_TIME,
					WaitProgressView.WAIT_TIME);
			getSocketBLL().deleteTimingFunction(clickItem);
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

			case R.id.btn_menu_up:
				Intent editIntent = new Intent(TimingFunctionActivity.this,
						EditTimingFunctionActivity.class);
				editIntent.putExtra(
						EditTimingFunctionActivity.EDIT_TIMINGFUNCTION_INDEX,
						clickItem);
				startActivity(editIntent);
				break;
			case R.id.btn_menu_down:
				PopupDialogView deleteDialogView = new PopupDialogView(
						TimingFunctionActivity.this);
				deleteDialogView
						.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
				deleteDialogView.setTitleText(R.string.button_delete);
				deleteDialogView.setMessageContent(String.format(getResources()
						.getString(R.string.delete_confirm_text),
						getResources().getString(R.string.timing_function)));
				deleteDialogView.setRightButtonClick(deleteButtonClick);
				deleteDialogView.show();
				break;
			default:
				break;
			}
		}
	};
	private TimingFunctionAdapter.OnItemClickListener timingFunctionClickListener = new TimingFunctionAdapter.OnItemClickListener() {
		@Override
		public void onItemLongClick(int deviceIndex) {
			clickItem = deviceIndex;
			PopupDialogView popupMenuDialogView = new PopupDialogView(
					TimingFunctionActivity.this);
			popupMenuDialogView.setDialogType(PopupDialogView.POPUPWINDOW_MENU);
			popupMenuDialogView
					.setButtonCount(PopupDialogView.POPUPWINDOW_BUTTON_NONE);
			popupMenuDialogView.setArg1(getResources().getString(
					R.string.button_edit));
			popupMenuDialogView.setMenuItemClickListener(itemsOnClick);
			popupMenuDialogView.setHasTitle(false);
			popupMenuDialogView.show();
		}

		@Override
		public void onItemClick(int deviceIndex) {
			getSocketBLL().changeTimingFunctionState(deviceIndex);
			/*TimingFunctionInfo timingFunctionInfo = getSocketBLL()
					.getAllTimingFunction().get(deviceIndex);
			timingFunctionInfo.setStatus(!timingFunctionInfo.getStatus());
			timingFunctionAdapter.notifyDataSetChanged();*/
		}
	};

	class TimingFunctiontHandler extends Handler {
		public TimingFunctiontHandler() {

		}

		public TimingFunctiontHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			dissmissProgressDialog();
			if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
				switch (msg.what) {
				case BLLUtil.REMOTE_MESSAGE_MODIFY_TIMING_FUNCTION:
				case BLLUtil.REMOTE_MESSAGE_GET_ALL_TIMING_FUNCTION:
					if (timingFunctionAdapter != null) {
						timingFunctionAdapter.notifyDataSetChanged();
					}
					break;
				case BLLUtil.REMOTE_MESSAGE_DELETE_TIMING_FUNCTION:
					showCheckMessage(R.string.delete_success);
					if (timingFunctionAdapter != null) {
						timingFunctionAdapter.notifyDataSetChanged();
					}
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_TIMING_FUNCTION_ADD:
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_TIMING_FUNCTION_DELETE:
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_TIMING_FUNCTION_MODIFY:
					if (timingFunctionAdapter != null) {
						timingFunctionAdapter.notifyDataSetChanged();
					}
					break;
				}
			} else {
				showCheckMessage(R.string.operate_failure);
			}
		}
	}
}
