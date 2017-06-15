package rici.roplug.open.view;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.bean.SocketInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SocketElectricView extends LinearLayout {
	private Context mContext;
	private SocketInfo socketInfo;
	private Button titleButton = null;
	private LinearLayout airQualityLayout = null;
	private LinearLayout airQualityNumberLayout = null;
	private TextView currentElectricity = null;
	private ImageButton sokcetPowerButton = null;
	private RotateImageView circleImageView = null;
	private Animation hyperspaceJumpAnimation = null;
	private int socketIndex = -1;
	private OnClickListener onClickListener = null;
	private OnAirQualityChangeListener onAirQualityChangeListener = null;
	private long startTime = 0;
	
	public SocketElectricView(Context context) {
		this(context, null);
	}

	public SocketElectricView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SocketElectricView(Context context, AttributeSet attrs,
			SocketInfo socketInfo, int socketIndex,
			OnClickListener onClickListener) {
		super(context, attrs);
		this.mContext = context;
		this.onClickListener = onClickListener;
		this.socketInfo = socketInfo;
		this.socketIndex = socketIndex;
		initSocketElectricityInfo();
	}

	public void initSocketElectricityInfo() {
		if (this.mContext != null) {
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			View v = inflater.inflate(R.layout.socket_electric_layout, null);
			v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					this.mContext, R.anim.refreshing_animation);

			SocketElectricView.this.addView(v);
			titleButton = (Button) v
					.findViewById(R.id.btn_socket_electricity_title);
			sokcetPowerButton = (ImageButton) v
					.findViewById(R.id.btn_socket_power);
			airQualityLayout = (LinearLayout) v
					.findViewById(R.id.ll_gas_quality_layout);
			airQualityNumberLayout = (LinearLayout) v
					.findViewById(R.id.ll_socket_electric_number);
			currentElectricity = (TextView) v
					.findViewById(R.id.tv_current_electricity);
			circleImageView = (RotateImageView) v
					.findViewById(R.id.img_air_quality_refresh);
			
			Bitmap bitmap = ((BitmapDrawable) this.getResources().getDrawable(
					R.drawable.y335_socket_electric_circle)).getBitmap();
			circleImageView.setBitmap(bitmap);
			circleImageView.setOrientation(0,false);
			
			// airQualityNumber.setTypeface(Typeface.MONOSPACE);//
			currentElectricity.setTypeface(Typeface.createFromAsset(
					mContext.getAssets(), "fonts/HelveticaNeue-Bold.otf"));
			airQualityNumberLayout.setOnClickListener(onClickListener);
			titleButton.setText(socketInfo.getDeviceInfo().getDeviceName());
			titleButton.setOnClickListener(onClickListener);
			updateSocketElectricity();
		}
	}

	public void startCircle() {
		startTime = System.currentTimeMillis();
		circleImageView.startAnimation();
		//circleImageView.startAnimation(hyperspaceJumpAnimation);
		
	}

	public void stopCircle() {
		/*hyperspaceJumpAnimation.cancel();
		circleImageView.clearAnimation();*/
		circleImageView.stopAnimation();
		/*if(startTime > 0){
		long currentTime = System.currentTimeMillis();
		int degree = (int)(((currentTime - startTime)*360/18*1000)%360);
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.y335_socket_electric_circle);
		circleImageView.setScaleType(ScaleType.CENTER);
		circleImageView.setBitmap(bitmap);
		circleImageView.setOrientation(degree, false);
		startTime = 0;
		}*/
/*		Matrix matrix = new Matrix();
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.y335_socket_electric_circle);//定义需要旋转的图片，假定图片的尺寸为50X50像素
	       matrix.preRotate(10);//matrix对旋转有两种方法，一个是preRotate(floag angle)，此方法是默认旋转给定角度
	       bitmap = Bitmap.createBitmap(bitmap ,0,0, bitmap .getWidth(), bitmap .getHeight(),matrix,true);//
	       
		circleImageView.setBackground(new BitmapDrawable(getResources(), bitmap));*/
	}

	public int getSocketIndex() {
		return this.socketIndex;
	}

	public void updateSocketElectricity() {
		if(!titleButton.getText().toString().equals(socketInfo.getDeviceInfo().getDeviceName())){
		titleButton.setText(socketInfo.getDeviceInfo().getDeviceName());
		}
		String electricity = BLLUtil.DECIMAL_FORMAT.format(socketInfo.getUsedElectric());
		SpannableString spanString = new SpannableString(electricity);  
	    AbsoluteSizeSpan span = new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.current_electricity_small_textsize));  
	    spanString.setSpan(span, electricity.length() -3 , electricity.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	    currentElectricity.setText(spanString);

		//currentElectricity.setText(String.valueOf(socketInfo.getUsedElectric()));
		sokcetPowerButton
				.setBackgroundResource(socketInfo.getPowerOn() ? R.drawable.y335_button_socket_on
						: R.drawable.y335_button_socket_off);
		if (socketInfo.getPowerOn()) {
			startCircle();
		} else {
			stopCircle();
		}
		
	}

	public interface OnAirQualityChangeListener {
		public void onAirQualityChange(int airQualityLevel);
	}

	public OnAirQualityChangeListener getOnAirQualityChange() {
		return onAirQualityChangeListener;
	}

	public void setOnAirQualityChange(
			OnAirQualityChangeListener onAirQualityChange) {
		this.onAirQualityChangeListener = onAirQualityChange;
	}

	public SocketInfo getSocketInfo() {
		return socketInfo;
	}

	public void setSocketInfo(SocketInfo socketInfo) {
		this.socketInfo = socketInfo;
	}
}
