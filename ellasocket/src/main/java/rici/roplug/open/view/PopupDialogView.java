package rici.roplug.open.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import rici.roplug.open.R;
import rici.roplug.open.adapter.PopupwindowCheckBoxListAdapter;
import rici.roplug.open.bll.SocketBLL;
import rici.roplug.open.wheel.OnWheelChangedListener;
import rici.roplug.open.wheel.StrericWheelAdapter;
import rici.roplug.open.wheel.WheelView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class PopupDialogView extends AlertDialog {
	public static final int POPUPWINDOW_SELECTBUTTON_LIST = 1;
	public static final int POPUPWINDOW_TIME_PICKER = 2;
	public static final int POPUPWINDOW_NET_ERROR = 3;
	public static final int POPUPWINDOW_TEXT_LIST = 4;
	public static final int POPUPWINDOW_TEXT = 5;
	public static final int POPUPWINDOW_AUTO_BILLING = 6;
	public static final int POPUPWINDOW_RADIOBUTTON_LIST = 7;
	public static final int POPUPWINDOW_EDITTEXT = 8;
	public static final int POPUPWINDOW_TEXT_ONE_BUTTON = 9;
	public static final int POPUPWINDOW_LINK_PASSWORD = 10;
	public static final int POPUPWINDOW_INPUT_WIFI_PASSWORD = 11;
	public static final int POPUPWINDOW_MENU = 12;
	public static final int POPUPWINDOW_MENU_THREE = 13;
	public static final int POPUPWINDOW_IMAGEVIEW = 14;

	public static final int POPUPWINDOW_BUTTON_NONE = 0;
	public static final int POPUPWINDOW_BUTTON_ONE = 1;
	public static final int POPUPWINDOW_BUTTON_TWO = 2;
	private LinearLayout showLinearLayout = null;
	private OnPopupDialogButtonClick leftButtonClick = null;
	private OnPopupDialogButtonClick rightButtonClick = null;
	private TextView titleTextView = null;
	private Button confirmButton, cancelButton;
	private WheelView dayWheel, hourWheel, minutesWheel;
	private int dayIndex, hourIndex, minuteIndex;
	private BaseAdapter popupAdapter = null;
	private int dialogLayout = -1;
	private ArrayList<Boolean> selecteItems = null;
	private Context mContext = null;
	private int dialogType = -1;
	private String titleText = null;
	private Object arg1, arg2;
	private boolean hasTitle = true;
	private int buttonCount = POPUPWINDOW_BUTTON_TWO;
	private int layoutWidth = 0, layoutHeight = 0;
	private String messageContent = null;
	private EditText editText = null;
	private View.OnClickListener menuItemClickListener = null;
	private boolean isShowPassword = false;

	public PopupDialogView(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		{
			if (getDialogLayout() != -1) {
				setContentView(getDialogLayout());
			} else {
				setContentView(R.layout.popupwindow_layout);
			}
			this.setCanceledOnTouchOutside(false);
			titleTextView = (TextView) findViewById(R.id.tv_popupwindow_title);
			if (titleText != null) {
				titleTextView.setText(titleText);
			}
			if (!isHasTitle()) {
				LinearLayout titleLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_title);
				if (titleLayout != null)
					titleLayout.setVisibility(View.GONE);
			}
			if (getButtonCount() == POPUPWINDOW_BUTTON_NONE) {
				LinearLayout buttonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_button);
				if (buttonLinearLayout != null)
					buttonLinearLayout.setVisibility(View.GONE);
			} else if (getButtonCount() == POPUPWINDOW_BUTTON_ONE) {
				LinearLayout oneButtonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_one_button);
				if (oneButtonLinearLayout != null) {
					oneButtonLinearLayout.setVisibility(View.VISIBLE);
					confirmButton = (Button) findViewById(R.id.btn_popupwindow_one);
				}
				LinearLayout buttonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_button);
				if (buttonLinearLayout != null) {
					buttonLinearLayout.setVisibility(View.GONE);
				}
			} else if (getButtonCount() == POPUPWINDOW_BUTTON_TWO) {
				confirmButton = (Button) findViewById(R.id.btn_popupwindow_right);
			}
			switch (dialogType) {
			case POPUPWINDOW_AUTO_BILLING:
				this.setCanceledOnTouchOutside(true);
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_auto_billing);
				showLinearLayout.setVisibility(View.VISIBLE);
				final EditText price = (EditText) findViewById(R.id.et_auto_charge_degree_price);

				confirmButton.setText(R.string.button_confirm);
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						/*
						 * StringBuffer sbBuffer = new StringBuffer();
						 * DecimalFormat df = new DecimalFormat("0.000");
						 * sbBuffer
						 * .append(String.format(mContext.getResources().
						 * getString
						 * (R.string.auto_billing_title),(arg1!=null)?arg1
						 * .toString
						 * ():"",df.format(Float.valueOf(arg2.toString()) *
						 * Float.valueOf(price.getText().toString()))));
						 * titleTextView.setText(sbBuffer.toString());
						 */
						if (price.getText().length() > 0)
							SocketBLL.getInstance().setAutoBillingPrice(
									mContext, price.getText().toString());
						PopupDialogView.this.dismiss();
					}
				});
				TextView useElectricityTextView = (TextView) findViewById(R.id.tv_auto_charge_electric);
				useElectricityTextView.setText(String.format(
						mContext.getResources().getString(
								R.string.electricity_use),
						(arg2 != null && arg2 instanceof Float) ? Float
								.valueOf(arg2.toString()) : 0f));

				price.setText(SocketBLL.getInstance().getAutoBillingPrice(
						mContext));
				price.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						StringBuffer sbBuffer = new StringBuffer();
						DecimalFormat df = new DecimalFormat("0.00");
						sbBuffer.append(String.format(mContext.getResources()
								.getString(R.string.auto_billing_title),
								(arg1 != null) ? arg1.toString() : "",
								df.format(Float.valueOf(arg2.toString())
										* (price.getText().length() > 0 ? Float
												.valueOf(price.getText()
														.toString()) : 0f))));
						titleTextView.setText(sbBuffer.toString());
						// confirmButton.setText(R.string.recalculate);
					}
				});
				StringBuffer sbBuffer = new StringBuffer();
				DecimalFormat df = new DecimalFormat("0.00");
				sbBuffer.append(String.format(mContext.getResources()
						.getString(R.string.auto_billing_title),
						(arg1 != null) ? arg1.toString() : "", df.format(Float
								.valueOf(arg2.toString())
								* (price.getText().length() > 0 ? Float
										.valueOf(price.getText().toString())
										: 0f))));
				titleTextView.setText(sbBuffer.toString());

				break;
			case POPUPWINDOW_TEXT:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_text);
				showLinearLayout.setVisibility(View.VISIBLE);
				TextView messageContentTextView = (TextView) findViewById(R.id.tv_popupwindow_text);
				if (getMessageContent() != null)
					messageContentTextView.setText(getMessageContent());
				confirmButton.setText(R.string.button_confirm);
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getRightButtonClick() != null) {
							getRightButtonClick().onButtonClick(null, null);
						}
					}
				});
				break;
			case POPUPWINDOW_SELECTBUTTON_LIST:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_list);
				showLinearLayout.setVisibility(View.VISIBLE);
				ListView listView = (ListView) findViewById(R.id.lv_popupwindow);
				if (listView != null && popupAdapter != null) {
					listView.setAdapter(popupAdapter);
					popupAdapter.notifyDataSetChanged();
				}
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							if (popupAdapter != null) {
								rightButtonClick
										.onButtonClick(
												((PopupwindowCheckBoxListAdapter) popupAdapter)
														.getSelectedItem(),
												null);
							}
						}
					}
				});
				break;
			case POPUPWINDOW_TIME_PICKER:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_time_picker);
				showLinearLayout.setVisibility(View.VISIBLE);
				initTimePicker();
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							rightButtonClick.onButtonClick(hourIndex % 12 + 12
									* dayIndex, minuteIndex);
						}
					}
				});
				break;
			case POPUPWINDOW_INPUT_WIFI_PASSWORD:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_wifi_link);
				showLinearLayout.setVisibility(View.VISIBLE);
				final EditText wifiNameEditText = (EditText) getWindow()
						.findViewById(R.id.edt_wifi_name);
				final CheckBox rememberBox = (CheckBox)getWindow().findViewById(R.id.cb_wifi_remember_password);
				editText = (EditText) getWindow().findViewById(
						R.id.edt_wifi_password);
				if (arg1 != null){
					wifiNameEditText.setText(arg1.toString());
					editText.setText(SocketBLL.getInstance().getWifiPassword(arg1.toString()));
					if(editText.getText().toString().trim().length()>0){
						rememberBox.setChecked(true);
					}
					else{
						rememberBox.setChecked(false);
					}
				}
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							rightButtonClick.onButtonClick(arg1.toString(),
									editText.getText().toString().trim());
							
							if(rememberBox.isChecked()){
							SocketBLL.getInstance().setWifiPassword(arg1.toString(), editText.getText().toString().trim());
							}
							else{
								SocketBLL.getInstance().setWifiPassword(arg1.toString(), "");
							}
						}
					}
				});
				final Button showHide = (Button) getWindow().findViewById(
						R.id.btn_password_show_hide);
				if (showHide != null) {
					showHide.setOnClickListener(new android.view.View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							isShowPassword = !isShowPassword;
							editText.setInputType(isShowPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);

							editText.setSelection(editText.getText().toString()
									.length());
							showHide.setBackgroundResource(isShowPassword ? R.drawable.button_wifi_password_show
									: R.drawable.button_wifi_password_hide);
						}
					});
				}
				break;
			case POPUPWINDOW_NET_ERROR:
				showLinearLayout = (LinearLayout) findViewById(R.id.net_status_bar_top);
				showLinearLayout.setVisibility(View.VISIBLE);
				showLinearLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mContext.startActivity(new Intent(
								android.provider.Settings.ACTION_WIFI_SETTINGS));
					}
				});
				break;
			case POPUPWINDOW_LINK_PASSWORD:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_check_link_password);
				showLinearLayout.setVisibility(View.VISIBLE);
				editText = (EditText) getWindow().findViewById(
						R.id.edt_login_password);
				// passwordTextView.setText("ricisung");
				final CheckBox showCheckBox = (CheckBox) getWindow()
						.findViewById(R.id.cb_show_password);
				if (arg1 != null) {
					showCheckBox.setChecked(Boolean.valueOf(arg1.toString()));
				}
				confirmButton
						.setOnClickListener(new android.view.View.OnClickListener() {
							@Override
							public void onClick(View v) {
								PopupDialogView.this.dismiss();
								if (rightButtonClick != null) {
									rightButtonClick.onButtonClick(editText
											.getText().toString().trim(),
											showCheckBox.isChecked());
								}
							}
						});
				
				break;
			case POPUPWINDOW_MENU_THREE:	
			case POPUPWINDOW_MENU:
				this.setCanceledOnTouchOutside(true);
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_menu);
				showLinearLayout.setVisibility(View.VISIBLE);
				/*Button weixinButton = (Button)findViewById(R.id.btn_menu_weixin_control);
				weixinButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getMenuItemClickListener() != null)
							getMenuItemClickListener().onClick(v);
					}
				});*/
				Button upButton = (Button) findViewById(R.id.btn_menu_up);
				if (getArg1() != null) {
					upButton.setText(getArg1().toString());
				}
				upButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getMenuItemClickListener() != null)
							getMenuItemClickListener().onClick(v);
					}
				});
				Button downButton = (Button) findViewById(R.id.btn_menu_down);
				downButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getMenuItemClickListener() != null)
							getMenuItemClickListener().onClick(v);
					}
				});
				if(dialogType==POPUPWINDOW_MENU_THREE){
					((ImageView)findViewById(R.id.iv_menu_middle)).setVisibility(View.VISIBLE);
					Button middleButton = (Button)findViewById(R.id.btn_menu_middle);
					middleButton.setVisibility(View.VISIBLE);
					middleButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							PopupDialogView.this.dismiss();
							if (getMenuItemClickListener() != null)
								getMenuItemClickListener().onClick(v);
						}
					});
				}
				break;
			case POPUPWINDOW_EDITTEXT:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_edittext);
				showLinearLayout.setVisibility(View.VISIBLE);
				editText = (EditText) findViewById(R.id.edt_socket_name);
				editText.setText(getMessageContent());
				editText.requestFocus();

				Button editTextButton = (Button) getWindow().findViewById(
						R.id.btn_popupwindow_right);
				editTextButton
						.setOnClickListener(new android.view.View.OnClickListener() {
							@Override
							public void onClick(View v) {
								PopupDialogView.this.dismiss();
								if (rightButtonClick != null) {
									rightButtonClick.onButtonClick(editText
											.getText().toString().trim(), null);
								}
							}
						});
				break;
			case POPUPWINDOW_IMAGEVIEW:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_imageview);
				showLinearLayout.setVisibility(View.VISIBLE);
				ImageView imageView = (ImageView)getWindow().findViewById(R.id.iv_popupwindow_image);
				if(imageView!=null&&getArg1()!=null && getArg1().toString().length()>0){
					String url = getArg1().toString();
					createQRImage(url,imageView);
				}
				break;
			}
			if (getButtonCount() == POPUPWINDOW_BUTTON_TWO) {
				Button cancelButton = (Button) getWindow().findViewById(
						R.id.btn_popupwindow_left);
				cancelButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getLeftButtonClick() != null) {
							leftButtonClick.onButtonClick(null, null);
						}
					}
				});
			}
		}
	}

	public EditText getEditText() {
		return editText;
	}

	public void showInput() {
		if (editText != null) {
			editText.requestFocus();
			InputMethodManager inputManager = (InputMethodManager) editText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
		}
	}

	private void initTimePicker() {
		Calendar currentDate = null;
		if (getArg1() != null && getArg1() instanceof Calendar) {
			currentDate = (Calendar) getArg1();
		} else {
			currentDate = Calendar.getInstance();
		}
		hourIndex = currentDate.get(Calendar.HOUR_OF_DAY);
		dayIndex = hourIndex / 12;
		minuteIndex = currentDate.get(Calendar.MINUTE);
		ArrayList<String> minutesList = new ArrayList<String>();
		ArrayList<String> hourList = new ArrayList<String>();
		ArrayList<String> dayList = new ArrayList<String>();

		for (int j = 0; j < 60; j++) {
			minutesList.add(String.valueOf(j));
			if (j < 12)
				hourList.add(String.valueOf(j == 0 ? 12 : j));
		}

		String am = mContext.getResources().getString(R.string.am);
		String pm = mContext.getResources().getString(R.string.pm);
		dayList.add(am);
		dayList.add(pm);
		dayWheel = (WheelView) findViewById(R.id.daywheel);
		dayWheel.setAdapter(new StrericWheelAdapter(dayList));
		dayWheel.setCurrentItem(dayIndex);
		dayWheel.setCyclic(false);
		dayWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_textsize));
		dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

		dayWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				dayIndex = newValue;
			}
		});
		hourWheel = (WheelView) findViewById(R.id.hourwheel);
		hourWheel.setAdapter(new StrericWheelAdapter(hourList));
		hourWheel.setCurrentItem(hourIndex % 12);
		hourWheel.setCyclic(true);
		hourWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_textsize));
		hourWheel.setInterpolator(new AnticipateOvershootInterpolator());
		hourWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int lastValue = oldValue % 12;
				int nextValue = newValue % 12;
				if ((lastValue == 0 && nextValue == 11)
						|| (lastValue == 11 && nextValue == 0)) {
					dayIndex = (dayIndex == 0) ? 1 : 0;
					dayWheel.setCurrentItem(dayIndex);
				}
				hourIndex = newValue % 12 + 12 * dayIndex;
			}
		});

		minutesWheel = (WheelView) findViewById(R.id.minuteswheel);
		minutesWheel.setAdapter(new StrericWheelAdapter(minutesList));
		minutesWheel.setCurrentItem(minuteIndex);
		minutesWheel.setCyclic(true);
		minutesWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_textsize));
		minutesWheel.setInterpolator(new AnticipateOvershootInterpolator());
		minutesWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				minuteIndex = newValue % 60;
			}
		});
	}

	public interface OnPopupDialogButtonClick {
		public void onButtonClick(Object arg1, Object arg2);
	}

	public void setRightButtonClick(OnPopupDialogButtonClick rightButtonClick) {
		this.rightButtonClick = rightButtonClick;
	}

	public int getLayoutWidth() {
		return layoutWidth;
	}

	public void setLayoutWidth(int layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	public int getLayoutHeight() {
		return layoutHeight;
	}

	public void setLayoutHeight(int layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public void setTitleText(int titleTextId) {
		if (mContext != null)
			this.titleText = mContext.getResources().getString(titleTextId);
	}

	public Object getArg1() {
		return arg1;
	}

	public void setArg1(Object arg1) {
		this.arg1 = arg1;
	}

	public Object getArg2() {
		return arg2;
	}

	public void setArg2(Object arg2) {
		this.arg2 = arg2;
	}

	public OnPopupDialogButtonClick getRightButtonClick() {
		return rightButtonClick;
	}

	public int getDialogType() {
		return dialogType;
	}

	public void setDialogType(int dialogType) {
		this.dialogType = dialogType;
	}

	public BaseAdapter getPopupAdapter() {
		return popupAdapter;
	}

	public void setPopupAdapter(BaseAdapter popupAdapter) {
		this.popupAdapter = popupAdapter;
	}

	public ArrayList<Boolean> getSelecteItems() {
		return selecteItems;
	}

	public void setSelecteItems(ArrayList<Boolean> selecteItems) {
		this.selecteItems = selecteItems;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public int getButtonCount() {
		return buttonCount;
	}

	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
	}

	public OnPopupDialogButtonClick getLeftButtonClick() {
		return leftButtonClick;
	}

	public void setLeftButtonClick(OnPopupDialogButtonClick leftButtonClick) {
		this.leftButtonClick = leftButtonClick;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public int getDialogLayout() {
		return dialogLayout;
	}

	public void setDialogLayout(int dialogLayout) {
		this.dialogLayout = dialogLayout;
	}

	public View.OnClickListener getMenuItemClickListener() {
		return menuItemClickListener;
	}

	public void setMenuItemClickListener(
			View.OnClickListener menuItemClickListener) {
		this.menuItemClickListener = menuItemClickListener;
	}
	
	public void createQRImage(String url,ImageView imageView)
	{
		int QR_WIDTH = mContext.getResources().getDimensionPixelOffset(R.dimen.popupwindow_imageview_width);
		int QR_HEIGHT= mContext.getResources().getDimensionPixelOffset(R.dimen.popupwindow_imageview_width);
		Bitmap bitmap = null;
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			imageView.setImageBitmap(bitmap);
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
	}
}
