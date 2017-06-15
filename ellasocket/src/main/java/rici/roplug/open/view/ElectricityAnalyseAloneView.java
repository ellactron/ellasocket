package rici.roplug.open.view;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.bll.bean.SocketInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ElectricityAnalyseAloneView extends AlertDialog{
	private int auto_update_time = 30 * 1000;
	private SocketInfo socketInfo = null;
	private ElectricityAnalyseHandler electricityAnalyseHandler = null;
	private TextView tv1, tv2, tv3, tv4, tv5;
	private Resources resources = null;
	private Context mContext;
	private Handler autoUpdateHandler = new Handler();
	private Runnable autoUpdateRunnable = new Runnable() {
		public void run() {
			updateSocketElectricity();
			autoUpdateHandler.postDelayed(this, auto_update_time);
		}
	};

	public ElectricityAnalyseAloneView(Context context) {
		super(context);
		this.mContext = context;
		resources = context.getResources();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.electricity_analyse_alone_layout);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		tv5 = (TextView) findViewById(R.id.textView5);

		electricityAnalyseHandler = new ElectricityAnalyseHandler();
		//setCanceledOnTouchOutside(false);

		int socketIndex = SocketBLL.getInstance().getCurrentSocketIndex();
		if (socketIndex < SocketBLL.getInstance().getAllSocketInfos().size()) {
			socketInfo = SocketBLL.getInstance().getAllSocketInfos()
					.get(socketIndex);
		}
		ImageButton closeButton = (ImageButton) findViewById(R.id.ib_popupwindow_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ElectricityAnalyseAloneView.this.dismiss();
				SocketBLL.getInstance().recoveActivityHandler();
			}
		});
		Button titleButton = (Button) findViewById(R.id.btn_electricity_alone);
		titleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSocketElectricity();
			}
		});
		SocketBLL.getInstance().restoreActivityHandler();
		SocketBLL.getInstance().initDanausBLL(mContext,
				electricityAnalyseHandler);

		updateSocketElectricity();
	}

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

	private void onSocketUsedElectricChanged() {
		if (socketInfo != null && resources != null) {
			tv4.setText(String.valueOf(BLLUtil.DECIMAL_FORMAT.format(socketInfo.getUsedElectric())));
			tv3.setText(String.format(resources.getString(R.string.power),
					BLLUtil.DECIMAL_FORMAT.format(socketInfo.getPower())));
			tv2.setText(String.format(resources.getString(R.string.current),
					BLLUtil.DECIMAL_FORMAT.format(socketInfo.getCurrent())));
			tv1.setText(String.format(resources.getString(R.string.voltage),
					BLLUtil.DECIMAL_FORMAT.format(socketInfo.getVoltage())));
			tv5.setText(String.format(resources
					.getString(R.string.roplug_state), resources
					.getString(socketInfo.getPowerOn() ? R.string.power_on
							: R.string.power_off)));
		}
	}

	class ElectricityAnalyseHandler extends Handler {
		public ElectricityAnalyseHandler() {

		}

		public ElectricityAnalyseHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			SocketInfo socketInfo = SocketBLL.getInstance().getCurrentSocket();
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_GET_SOCKET_REAL_TIME_ELECTRICITY:
			case BLLUtil.LOCAL_MESSAGE_GET_SOCKET_ELECTRICITY:
				onSocketUsedElectricChanged();
				break;
			}
		}
	}
}
