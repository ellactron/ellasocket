package rici.roplug.open.activity;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.bean.SocketInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SocketListenerActivity extends BaseActivity {
	private int auto_update_time = 30 * 1000;
	private SocketInfo socketInfo = null;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;
	private Handler socketListenerHandler = null;
	private Handler autoUpdateHandler = new Handler();
	private Runnable autoUpdateRunnable = new Runnable() {
		public void run() {
			updateSocketElectricity();
			autoUpdateHandler.postDelayed(this, auto_update_time);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket_listener_layout);
		socketListenerHandler = new SocketListenerHandler();
		int socketIndex = getSocketBLL().getCurrentSocketIndex();
		if (socketIndex < getSocketBLL().getAllSocketInfos().size()) {
			socketInfo = getSocketBLL().getAllSocketInfos().get(socketIndex);
		}
		
		Button titleReturnButton = (Button)findViewById(R.id.btn_title_return);
		titleReturnButton.setText(R.string.analyse_one);
		titleReturnButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SocketListenerActivity.this.finish();				
			}
		});
		
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		tv5 = (TextView) findViewById(R.id.textView5);
	}

	@Override
	public void onResume() {
		super.onResume();
		setCurrentHandler(socketListenerHandler);
		updateSocketElectricity();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopAutoGetSocketElectricity();
	}

	private void updateSocketElectricity() {
		stopAutoGetSocketElectricity();
		if (socketInfo != null) {
			getSocketBLL().getSocketElectricity(socketInfo);
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
	public void onSocketUsedElectricChanged() {
		if(socketInfo!=null){
		  tv4.setText(socketInfo.getUsedElectric()+"¶È");
		  tv3.setText(socketInfo.getPower()+"Íß");
		  tv2.setText(socketInfo.getCurrent()+"°²");
		  tv1.setText(socketInfo.getVoltage()+"·ü");
		  tv5.setText(socketInfo.getPowerOn()?R.string.power_on:R.string.power_off);
		}
	}

	class SocketListenerHandler extends Handler {
		public SocketListenerHandler() {

		}

		public SocketListenerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_REAL_TIME_ELECTRICITY:
			case BLLUtil.LOCAL_MESSAGE_GET_SOCKET_ELECTRICITY:
				onSocketUsedElectricChanged();
				break;
			}
		}
	}
}