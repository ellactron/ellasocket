package rici.roplug.open.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.rici.wifi.util.SystemArgument;

import rici.roplug.open.R;
import rici.roplug.open.adapter.PopupwindowCheckBoxListAdapter;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.bll.bean.SocketInfo;
import rici.roplug.open.bll.bean.TimingFunctionInfo;
import rici.roplug.open.common.util.CommonConfig;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EditTimingFunctionActivity extends BaseActivity {
	public final static String EDIT_TIMINGFUNCTION_INDEX = "edit_index";
	private int editIndex = -1;
	private int selectType = -1;
	private int selectDay = 0;
	private ArrayList<String> socketNameList = new ArrayList<String>();
	private ArrayList<Boolean> socketSelecteds = new ArrayList<Boolean>();
	private ArrayList<String> weekNameList = new ArrayList<String>();
	private ArrayList<Boolean> weekSelecteds = new ArrayList<Boolean>();
	private Button selectSocketButton, timingTimeButton, circulationModeButton;
	private RadioGroup radioGroup;
	private Calendar calendar = null;
	//private Date timingTimeDate = null;
	private TimingFunctionInfo timingFunctionInfo = null;
	private EditTimingFunctiontHandler editTimingFunctiontHandler = null;
	private PopupwindowCheckBoxListAdapter popupwindowCheckBoxListAdapter = null; 
	private StringBuffer selectSocketName = new StringBuffer();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_timing_function_layout);
		editTimingFunctiontHandler = new EditTimingFunctiontHandler();
		calendar = Calendar.getInstance();
		//timingTimeDate = new Date();
		editIndex = getIntent().getIntExtra(EDIT_TIMINGFUNCTION_INDEX, -1);
		if (editIndex < 0) {
			timingFunctionInfo = new TimingFunctionInfo();
			timingFunctionInfo.setHour(calendar.get(Calendar.HOUR_OF_DAY));
			timingFunctionInfo.setMinute(calendar.get(Calendar.MINUTE));
		} else {
			timingFunctionInfo = SocketBLL.getInstance().getAllTimingFunction()
					.get(editIndex);
			calendar.set(Calendar.HOUR_OF_DAY,timingFunctionInfo.getHour());
			calendar.set(Calendar.MINUTE,timingFunctionInfo.getMinute());
		}

		selectSocketButton = (Button) findViewById(R.id.btn_select_sock);
		timingTimeButton = (Button) findViewById(R.id.btn_timing_time);
		circulationModeButton = (Button) findViewById(R.id.btn_circulation_mode);
		radioGroup = (RadioGroup) findViewById(R.id.rg_power_onoff);
		showTimingFunction();
		
		Button titleReturnButton = (Button) findViewById(R.id.btn_title_return);
		titleReturnButton
				.setText((editIndex < 0) ? R.string.timing_function_add
						: R.string.timing_function_modify);
		titleReturnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditTimingFunctionActivity.this.finish();
			}
		});
	}

	private void initSelecteds(){
		socketSelecteds.clear();
		weekSelecteds.clear();
		socketNameList.clear();
		weekNameList.clear();
		int itemCount = 0;
		selectSocketName = new StringBuffer();
		for (SocketInfo socketInfo : getSocketBLL().getAllSocketInfos()) {
			socketNameList.add(socketInfo.getDeviceInfo().getDeviceName());
			if (timingFunctionInfo.getSelectSocketMac() != null
					&& timingFunctionInfo.getSelectSocketMac().contains(
							socketInfo.getDeviceInfo().getMacAddress())) {
				itemCount++;
				if (itemCount > 1) {
					selectSocketName.append(SystemArgument
							.getInstance().WEEK_REGULAREX);
				}
				selectSocketName.append(socketInfo.getDeviceInfo().getDeviceName());
				socketSelecteds.add(true);
			} else {
				socketSelecteds.add(false);
			}
		}

		int j = 1;
		for (int i = 0; i < 7; i++) {
			weekNameList.add(getResources().getString(R.string.monday + i));			
			if ((timingFunctionInfo.getCirculatioMode() & j) == j) {
				weekSelecteds.add(true);
			} else {
				weekSelecteds.add(false);
			}
			j *= 2;
		}
	}
	private void showTimingFunction() {
		SimpleDateFormat ss = new SimpleDateFormat("a  hh:mm");// 12小时制
		String timeStr = ss.format(calendar.getTime());
		timingTimeButton.setText(timeStr);
		circulationModeButton.setText(timingFunctionInfo
				.getCirculationModeString(EditTimingFunctionActivity.this));
		int selectId = timingFunctionInfo.getPoweron() ? R.id.rb_power_on
				: R.id.rb_power_off;
		radioGroup.check(selectId);
		initSelecteds();
		selectSocketButton.setText(selectSocketName.toString());
	}

	private OnPopupDialogButtonClick timingFunctionButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			switch (selectType) {
			case 0:
				ArrayList<Boolean> selectSocket = (ArrayList<Boolean>) arg1;
				selectSocketName = new StringBuffer();
				StringBuffer selectSocketMac = new StringBuffer();
				int itemCount = 0;
				for (int i = 0; i < selectSocket.size(); i++) {
					if (selectSocket.get(i)) {
						itemCount++;
						if (itemCount > 1) {
							selectSocketName.append(SystemArgument
									.getInstance().WEEK_REGULAREX);
							selectSocketMac
									.append(SystemArgument.getInstance().WEEK_REGULAREX);
						}
						selectSocketName.append(socketNameList.get(i));
						selectSocketMac.append(getSocketBLL()
								.getAllSocketInfos().get(i).getDeviceInfo()
								.getMacAddress());
					}
				}
				
				timingFunctionInfo.setSelectSocketMac(selectSocketMac
						.toString());
				
				selectSocketButton.setText(selectSocketName.toString());
				selectSocketButton.setEllipsize(TruncateAt.MARQUEE);
				selectSocketButton.setMarqueeRepeatLimit(1);				
				break;
			case 1:
				if (arg1 != null && arg1 instanceof Integer) {
					calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(arg1.toString()));
					timingFunctionInfo
							.setHour(Integer.valueOf(arg1.toString()));
				}
				if (arg2 != null && arg2 instanceof Integer) {
					calendar.set(Calendar.MINUTE,Integer.valueOf(arg2.toString()));
					timingFunctionInfo.setMinute(Integer.valueOf(arg2
							.toString()));
				}
				
				SimpleDateFormat ss = new SimpleDateFormat("a  hh:mm");// 12小时制
				String timeStr = ss.format(calendar.getTime());
				timingTimeButton.setText(timeStr);
				break;
			case 2:
				selectDay = 0;
				ArrayList<Boolean> selectItem = (ArrayList<Boolean>) arg1;
				for (int i = 0; i < selectItem.size(); i++) {
					if (selectItem.get(i)) {
						selectDay += CommonConfig.WEEK_VALUES[i];
					}
				}
				timingFunctionInfo.setCirculationMode(selectDay);
				
				circulationModeButton.setText(timingFunctionInfo
						.getCirculationModeString(EditTimingFunctionActivity.this));

				circulationModeButton.setEllipsize(TruncateAt.MARQUEE);
				circulationModeButton.setMarqueeRepeatLimit(1);				
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		setCurrentHandler(editTimingFunctiontHandler);
	}
	
	public void timingFunctionButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_sock:
			selectType = 0;
			popupwindowCheckBoxListAdapter = new PopupwindowCheckBoxListAdapter(
					EditTimingFunctionActivity.this, socketNameList,
					socketSelecteds);
			PopupDialogView selectSocketDialogView = new PopupDialogView(
					EditTimingFunctionActivity.this);
			selectSocketDialogView
					.setDialogType(PopupDialogView.POPUPWINDOW_SELECTBUTTON_LIST);
			selectSocketDialogView
					.setPopupAdapter(popupwindowCheckBoxListAdapter);
			selectSocketDialogView
					.setRightButtonClick(timingFunctionButtonClick);
			selectSocketDialogView.setTitleText(getResources().getString(
					R.string.title_select_sock));
			selectSocketDialogView.show();
			break;
		case R.id.btn_timing_time:
			selectType = 1;
			PopupDialogView timingPopupDialogView = new PopupDialogView(
					EditTimingFunctionActivity.this);
			timingPopupDialogView
					.setDialogType(PopupDialogView.POPUPWINDOW_TIME_PICKER);
			timingPopupDialogView.setArg1(calendar);
			timingPopupDialogView
					.setRightButtonClick(timingFunctionButtonClick);
			timingPopupDialogView.setTitleText(getResources().getString(
					R.string.title_timing_time));
			timingPopupDialogView.show();
			break;
		case R.id.btn_circulation_mode:
			selectType = 2;
			PopupwindowCheckBoxListAdapter weekListAdapter = new PopupwindowCheckBoxListAdapter(
					EditTimingFunctionActivity.this, weekNameList,
					weekSelecteds);
			PopupDialogView weekPopupDialogView = new PopupDialogView(
					EditTimingFunctionActivity.this);
			weekPopupDialogView
					.setDialogType(PopupDialogView.POPUPWINDOW_SELECTBUTTON_LIST);
			weekPopupDialogView.setRightButtonClick(timingFunctionButtonClick);
			weekPopupDialogView.setPopupAdapter(weekListAdapter);
			weekPopupDialogView.setTitleText(getResources().getString(
					R.string.title_circulation_mode));
			weekPopupDialogView.show();
			break;
		case R.id.btn_save:
			boolean isAdd = true;
			timingFunctionInfo.setStatus(true);
			RadioButton radioButton = (RadioButton) findViewById(radioGroup
					.getCheckedRadioButtonId());
			int selectId = radioButton.getId();
			Boolean isPoweron = (selectId == R.id.rb_power_on) ? true : false;
			if (timingFunctionInfo.getSelectSocketMac() == null
					|| timingFunctionInfo.getSelectSocketMac().length() == 0) {
				showCheckMessage(R.string.timing_function_error_select_socket_empty);
				isAdd = false;
			}
			timingFunctionInfo.setPoweron(isPoweron);
			if (isAdd) {
				if (editIndex < 0) {
					SocketBLL.getInstance().addTimingFunction(
							timingFunctionInfo);
				} else {
					SocketBLL.getInstance().updateTimingFunction(editIndex,
							timingFunctionInfo);
				}
			}
			break;
		}
	}

	class EditTimingFunctiontHandler extends Handler {
		public EditTimingFunctiontHandler() {

		}

		public EditTimingFunctiontHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			dissmissProgressDialog();
			if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
				switch (msg.what) {
				case BLLUtil.REMOTE_MESSAGE_ADD_TIMING_FUNCTION:
					selectSocketButton.setText("");
					timingFunctionInfo = new TimingFunctionInfo();
					timingFunctionInfo.setHour(calendar.get(Calendar.HOUR_OF_DAY));
					timingFunctionInfo.setMinute(calendar.get(Calendar.MINUTE));
					showTimingFunction();
					showCheckMessage(R.string.timing_function_add_success);
					break;
				case BLLUtil.REMOTE_MESSAGE_MODIFY_TIMING_FUNCTION:
					showCheckMessage(R.string.timing_function_modify_success);
					break;
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_ADD:
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_MODIFY:
				case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_DELETE:
					showTimingFunction();
					if(selectType==0){
						if(popupwindowCheckBoxListAdapter!=null){
							popupwindowCheckBoxListAdapter.notifyDataSetChanged();
						}
					}
					break;
				}

			}
		}
	}
}
