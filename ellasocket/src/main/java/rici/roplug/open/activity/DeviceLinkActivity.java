package rici.roplug.open.activity;

import java.util.ArrayList;

import rici.roplug.open.R;
import rici.roplug.open.adapter.SocketAdapter;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.bean.SocketInfo;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class DeviceLinkActivity extends BaseActivity {

	private DeviceLinkHandler deviceLinkHandler = null;
	private GridView deviceLinkGridView = null;
	private SocketAdapter socketAdapter = null;
	protected int activityCloseEnterAnimation;
	protected int activityCloseExitAnimation;
	private int hightItem = -1;
	private ArrayList<SocketInfo> onlineSocketInfos = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_link_grid_layout);
		genOnlineSocketInfo();
		deviceLinkHandler = new DeviceLinkHandler();
		deviceLinkGridView = (GridView) findViewById(R.id.gv_device_link);
		socketAdapter = new SocketAdapter(DeviceLinkActivity.this,
				onlineSocketInfos,hightItem);
		deviceLinkGridView.setAdapter(socketAdapter);
		deviceLinkGridView.setNumColumns(3);
		deviceLinkGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getSocketBLL().setCurrentSocketIndex(onlineSocketInfos.get(position).getSocketIndex());		
				DeviceLinkActivity.this.finish();
			}
		});
		Button returnButton = (Button) findViewById(R.id.btn_title_return);
		returnButton.setCompoundDrawables(null, null, null, null);
		returnButton.setBackgroundResource(R.drawable.button_close);
		returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeviceLinkActivity.this.finish();
			}
		});

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

	private void genOnlineSocketInfo(){
		int i=0;
		onlineSocketInfos = new ArrayList<SocketInfo>();
		SocketInfo currentSocketInfo = getSocketBLL().getCurrentSocket();
		for(SocketInfo s:getSocketBLL().getAllSocketInfos()){
			if(s.isDeviceOnline()){
				onlineSocketInfos.add(s);
				if(s.isEqualObj(currentSocketInfo)){
					hightItem = i;
				}
				i++;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setCurrentHandler(deviceLinkHandler);
	}

	public void onDestroy() {
		super.onDestroy();
		overridePendingTransition(activityCloseEnterAnimation,
				activityCloseExitAnimation);
	}
	
	class DeviceLinkHandler extends Handler {
		public DeviceLinkHandler() {

		}

		public DeviceLinkHandler(Looper looper) {
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

						genOnlineSocketInfo();
						if(socketAdapter!=null){
							socketAdapter.notifyDataSetChanged();
						}
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
							getSocketBLL().setCurrentSocketIndex(Math.max(0, getSocketBLL().getCurrentSocketIndex()-1));
						} else {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.delete_socket_synch),
									msg.obj.toString()));
						}

						genOnlineSocketInfo();
						if(socketAdapter!=null){
							socketAdapter.notifyDataSetChanged();
						}
					} else {
						if (msg.obj != null) {
							showCheckMessageLong(String.format(getResources()
									.getString(R.string.delete_socket_synch),
									msg.obj.toString()));
							DeviceLinkActivity.this.finish();
							Intent searchIntent = new Intent(
									DeviceLinkActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						}
					}
				}
				break;
			case BLLUtil.REMOTE_MESSAGE_SYNCHRONOUS_DEVICE_MODIFY:	
				genOnlineSocketInfo();
				if(socketAdapter!=null){
					socketAdapter.notifyDataSetChanged();
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
								getSocketBLL().setCurrentSocketIndex(Math.max(0, getSocketBLL().getCurrentSocketIndex()-1));
							} else {
								showCheckMessageLong(String
										.format(getResources()
												.getString(
														R.string.on_line_socket_synch),
														socketInfo.getDeviceInfo().getDeviceName()));								
							}
						}
						genOnlineSocketInfo();
						if(socketAdapter!=null){
							socketAdapter.notifyDataSetChanged();
						}
					} else {
						if (msg.obj != null) {
							showCheckMessageLong(String.format(
									getResources().getString(
											R.string.off_line_socket_synch),
									msg.obj.toString()));
							DeviceLinkActivity.this.finish();

							if(getSocketBLL().getMainActivity()!=null){
								getSocketBLL().getMainActivity().finish();
							}
							Intent searchIntent = new Intent(
									DeviceLinkActivity.this,
									SearchResultActivity.class);
							startActivity(searchIntent);
						}
					}
				}
				break;
			}
		}
	}
}
