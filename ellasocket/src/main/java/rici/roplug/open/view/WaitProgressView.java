package rici.roplug.open.view;

import com.rici.wifi.util.L;

import rici.roplug.open.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WaitProgressView {
	public static int DELAY_TIME = 200;
	public static int WAIT_TIME = 10 * 1000;
	private int delayTime = 0;
	private int waitTime = -1;
	private Dialog loadingDialog = null;
	private Toast showToast = null;
	private TextView waitText = null;
	private ImageView loadingView;
	private Animation hyperspaceJumpAnimation = null;
	private Handler waitProgressHandler = new Handler();
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			if (loadingDialog != null) {
				try {
					L.d("loadingDialog cancel");
					loadingDialog.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	};

	private Runnable delayShowRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(delayShowRunnable);
			if (loadingDialog != null) {
				// 使用ImageView显示动画
				loadingView.startAnimation(hyperspaceJumpAnimation);
				loadingDialog.show();
			}

			if (waitTime > 0) {
				waitProgressHandler.postDelayed(waitProgressRunnable, waitTime);
			}
		}
	};

	public WaitProgressView(Context context, int messageID) {
		int msgID = messageID;
		if(msgID==0){
			msgID = R.string.progress_wait;
		}
		if (loadingDialog != null) {
			waitText.setText(msgID);
		} else {
			// 加载动画
			hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					context, R.anim.loading_animation);
			initWaitDialog(context, context.getResources()
					.getString(msgID));
		}
	}

	public WaitProgressView(Context context, int messageID,int delayTime,int waitTime) {
		int msgID = messageID;
		if(msgID==0){
			msgID = R.string.progress_wait;
		}
		if (loadingDialog != null) {
			waitText.setText(msgID);
		} else {
			// 加载动画
			hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					context, R.anim.loading_animation);
			initWaitDialog(context, context.getResources()
					.getString(msgID));
		}
		this.delayTime = delayTime;
		this.waitTime = waitTime;
	}

	private Dialog initWaitDialog(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.progress_wait_layout, null);// 得到加载view
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		// main.xml中的ImageView
		loadingView = (ImageView) v.findViewById(R.id.img_loading);
		waitText = (TextView) v
				.findViewById(R.id.tv_wait_progress_message);// 提示文字
		waitText.setText(msg);// 设置加载信息		
		loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(true);// 不可以用“返回键”取消
		loadingDialog.getWindow().setContentView(v);// 设置布局
		return loadingDialog;
	}

	public void setWaiProgressTime(int delayTime, int waitTime) {
		this.delayTime = delayTime;
		this.waitTime = waitTime;
	}

	public void show() {
		waitProgressHandler.postDelayed(delayShowRunnable,
				Math.max(delayTime, 0));
	}
	public void setShowText(int msgID){
		if(waitText!=null)
			waitText.setText(msgID);
	}
	public void setShowText(String showString){
		if(waitText!=null)
		waitText.setText(showString);// 设置加载信息	
	}
	public void show(int delayTime, int waitTime){
		this.waitTime = waitTime;
		this.delayTime = delayTime;		
		waitProgressHandler.postDelayed(delayShowRunnable,
				Math.max(delayTime, 0));		
	}
	public void hide() {
		 waitProgressHandler.removeCallbacks(waitProgressRunnable);
		 waitProgressHandler.removeCallbacks(delayShowRunnable);
		if (loadingDialog != null) {
			loadingDialog.cancel();
		}
	}
	
}
